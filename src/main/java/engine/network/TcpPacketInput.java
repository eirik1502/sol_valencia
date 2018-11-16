package engine.network;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *Reads packets sendt over tcp by TcpPacketOutput,
 * and stores them in a list.
 *
 * the data length int added by TcpPacketOutput is removed on arrival
 *
 * Created by eirik on 28.07.2017.
 */
public class TcpPacketInput {

    static final int
            LEAST_PACKET_ID = -2,

            ALIVE_PACKET = -1,
            DISCONNECT_PACKET = -2;



    private InputStream in;

    private boolean remoteSocketClosed = false;


    //the bytes to be read for the next packet. -1 indicates no packet to be read
    private int nextPacketSize = -1;
    private int nextPacketId = -1;

    //packets waiting by id
    private HashMap<Integer, LinkedList<NetworkDataInput> > packetsWaiting = new HashMap<>();

    private final int noPacketTimeout;
    private int noPacketPolls = 0;


    public TcpPacketInput(InputStream in, int noPacketTimeout, int idRange) {
        this.in = in;
        this.noPacketTimeout = noPacketTimeout;

        //allocate lists for packets
        for (int i = LEAST_PACKET_ID; i < idRange+1; i++) {
            packetsWaiting.put(i, new LinkedList<>() );
        }
    }
    public TcpPacketInput(InputStream in, int noPacketTimeout) {
        this(in, noPacketTimeout, 50);
    }
    public TcpPacketInput(InputStream in) {
        this(in, 240);
    }

    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRemoteSocketClosed() {
        return remoteSocketClosed;
    }

    public int availablePacketTypes() {
        return (int)packetsWaiting.values().stream().filter(packList -> !packList.isEmpty()).count();
    }
    public int availablePackets() {
        return packetsWaiting.values().stream().mapToInt(packList -> packList.size()).sum();
    }

    public int packetCount(int packetId) {
        return packetsWaiting.get(packetId).size();
    }
    public boolean hasPacket(int packetId) {
        return !packetsWaiting.get(packetId).isEmpty();
    }
    public boolean removeIfHasPacket(int packetId) {
        return removePacket(packetId);
    }
    public boolean removePacket(int packetId) {
        return pollPacket(packetId) != null;
    }
    public void removeAllPackets(int packetId) {
        packetsWaiting.get(packetId).clear();
    }

    public NetworkDataInput peekPacket(int packetId) {
        return packetsWaiting.get(packetId).peek();
    }
    public NetworkDataInput pollPacket(int packetId) {
        return packetsWaiting.get(packetId).poll();
    }
    public LinkedList<NetworkDataInput> pollAllPackets(int packetId) {
        //returns the list representing packets, and inits a new list internally
        LinkedList<NetworkDataInput> packets = packetsWaiting.get(packetId);
        packetsWaiting.put(packetId, new LinkedList<>());
        return packets;
    }

    public void clear() {
        //allocate lists for packets
        int size = packetsWaiting.size();
        for (int i = 0; i < size; i++) {
            packetsWaiting.put(i, new LinkedList<>() );
        }
    }

    public boolean pollPackets() {
        boolean polledPackets = false;

        try {
            while(true) {
                if (nextPacketSize == -1 && in.available() >= Integer.BYTES *2) {

                    //read next packet size and id
                    byte[] sizeIdBytes = new byte[Integer.BYTES *2];
                    in.read(sizeIdBytes);

                    //convert bytes to int
                    ByteBuffer sizeIdBuff = ByteBuffer.wrap(sizeIdBytes);
                    nextPacketSize = sizeIdBuff.getInt();
                    nextPacketId = sizeIdBuff.getInt();

//                    System.out.println("Got packet size: " + nextPacketSize);
                }

                //if we are waiting for a packet, and all its data has arrived, retrieve it
                //else no more to poll
                if (nextPacketSize != -1 && in.available() >= nextPacketSize) {

                    storeDataInPacket(nextPacketSize);
                    polledPackets = true;

                    //tell that the last packet is finished reading
                    nextPacketSize = -1;
                    nextPacketId = -1;
                }
                else {
                    break;
                }
            }
        }
        catch(IOException e) {
            //socket is probably closed
            remoteSocketClosed = true;
            System.err.println("Trying to read bytes, but remote socket closed");
        }

        //check if no packet was received, if so, set remote socket to closed
        //else reset noPacket timer
        if (!polledPackets) {
            if (++noPacketPolls >= noPacketTimeout) {
                remoteSocketClosed= true;
            }
        }
        else {
            noPacketPolls = 0;
        }

        //remove AlivePackets
        while(hasPacket(TcpPacketInput.ALIVE_PACKET)) {
            removePacket(TcpPacketInput.ALIVE_PACKET);
        }

        //check if remote disconnected
        if (removeIfHasPacket(TcpPacketInput.DISCONNECT_PACKET)) {
            remoteSocketClosed = true;
        }

        return polledPackets;
    }

    /**
     * reads byteCount bytes from the input stream, and stores it in a NetworkIDataInput in the waiting packet list
     * @param byteCount
     */
    private void storeDataInPacket(int byteCount) {

        try {
            byte[] bytes = new byte[byteCount];
            in.read(bytes);

            NetworkDataInput newPacket = new NetworkDataInput( bytes );

            packetsWaiting.get(nextPacketId).add( newPacket );

        }
        catch (IOException e) {
            //socket is probably closed
            remoteSocketClosed = true;
            System.err.println("Trying to read bytes, but remote socket closed");
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("Packets pending in TcpPacketInput:\n");
        for (Map.Entry<Integer, LinkedList<NetworkDataInput> > entry: packetsWaiting.entrySet()) {
            int packetId = entry.getKey();
            LinkedList<NetworkDataInput> packetsPending = entry.getValue();

            if (!packetsPending.isEmpty()) {
                sb.append(packetId);
                sb.append(" count: ");
                sb.append(packetsPending.size());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

}
