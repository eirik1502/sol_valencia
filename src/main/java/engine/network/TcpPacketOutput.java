package engine.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by eirik on 28.07.2017.
 */
public class TcpPacketOutput {


    private OutputStream out;

    private boolean remoteSocketClosed = false;


    public TcpPacketOutput(OutputStream out) {
        this.out = out;
    }


    public void close() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean send(int packetId, NetworkDataOutput packet) {
        try {
            //bytes from packet
            byte[] packetBytes = packet.getBytes();

            //get packet size and id as bytes
            byte[] lengthIdBytes = ByteBuffer.allocate(Integer.BYTES * 2)
                    .putInt(packetBytes.length)
                    .putInt(packetId)
                    .array();

            //write packet length and id
            out.write(lengthIdBytes);

            //write packet data
            out.write( packetBytes );

        } catch (IOException e) {
            //remote socket is probably closed
            remoteSocketClosed = true;
            return false;
        }

        return true;
    }

    public void sendEmpty(int packetId) {
        send(packetId, new NetworkDataOutput());
    }

    public void sendHostAlive() {
        sendEmpty(TcpPacketInput.ALIVE_PACKET);
    }
    public void sendHostDisconnected() {
        sendEmpty(TcpPacketInput.DISCONNECT_PACKET);
    }
}
