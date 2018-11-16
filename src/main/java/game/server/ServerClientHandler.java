package game.server;

import engine.network.*;
import engine.network.networkPackets.AbilityStartedData;
import engine.network.networkPackets.AllCharacterStateData;
import engine.network.networkPackets.CharacterInputData;
import engine.network.networkPackets.HitDetectedData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by eirik on 21.06.2017.
 */
public class ServerClientHandler {


    private Socket socket;

    private TcpPacketInput tcpPacketIn;
    private TcpPacketOutput tcpPacketOut;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    //private Thread inputThread;


    private LinkedList<CharacterInputData> inputData;


    //private LinkedList



    public ServerClientHandler(Socket clientSocket) {

        this.socket = clientSocket;
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            tcpPacketIn = new TcpPacketInput(socket.getInputStream());
            tcpPacketOut = new TcpPacketOutput(socket.getOutputStream());


        } catch (IOException e) {
            e.printStackTrace();
        }

        inputData = new LinkedList<>();

    }

    public TcpPacketInput getTcpPacketIn() {
        return tcpPacketIn;
    }

    public TcpPacketOutput getTcpPacketOut() {
        return tcpPacketOut;
    }



    /**
     *
     * @param stateData
     * @return false if socket is disconnected, true otherwise
     */
    public void sendCharacterData(AllCharacterStateData stateData) {
        //translate
        NetworkDataOutput dataOut = NetworkUtils.gameStateToPacket(stateData);

        //send
        tcpPacketOut.send(NetworkUtils.SERVER_CHARACTER_STATE_ID, dataOut);
    }
    public void sendAbilityStarted(AbilityStartedData abData) {
        //translate
        NetworkDataOutput dataOut = NetworkUtils.abilityStartedDataToPacket(abData);

        //send
        tcpPacketOut.send(NetworkUtils.SERVER_ABILITY_STARTED_ID, dataOut);
    }
    public void sendHitDetected(HitDetectedData hitData) {
        //translate
        NetworkDataOutput dataOut = NetworkUtils.hitDetectedToPacket(hitData);

        //send
        tcpPacketOut.send(NetworkUtils.SERVER_HIT_DETECTED_ID, dataOut);
    }
    public void sendProjectileDead(ProjectileDeadData projDeadData) {
        //translate
        NetworkDataOutput dataOut = NetworkUtils.projectileDeadToPacket(projDeadData);

        //send
        tcpPacketOut.send(NetworkUtils.SERVER_PROJECTILE_DEAD_ID, dataOut);
    }


    public CharacterInputData getInputData() {

        //get newest input
        LinkedList<NetworkDataInput> inputs = tcpPacketIn.pollAllPackets(NetworkUtils.CLIENT_CHARACTER_INPUT);
        if (!inputs.isEmpty()) {
            return NetworkUtils.packetToCharacterInput(inputs.poll());
        }

        return null;
    }

    public void terminate() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public void sendEmptyPacket(int packetId){
//        tcpPacketOut.sendEmpty(packetId);
//    }
//
//    public int available() {
//        try {
//            return inputStream.available();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public boolean intAvailable(int count) {
//        return available() >= Integer.BYTES * count;
//    }
//
//    public void sendInt(int i) {
//        try {
//            outputStream.writeInt(i);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public int readInt() {
//        try {
//            return inputStream.readInt();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
