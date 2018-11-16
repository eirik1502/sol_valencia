import engine.network.NetworkDataInput;
import engine.network.NetworkDataOutput;
import engine.network.TcpPacketInput;
import engine.network.TcpPacketOutput;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by eirik on 28.07.2017.
 */
public class NetworkTcpPacketIoTest {


    private TcpPacketOutput tcpOut;
    private TcpPacketInput tcpIn;

    //some streams that exchanges data
    private PipedOutputStream pipeOut;
    private PipedInputStream pipeIn;

    @Before
    public void setup() {

        //create pipe streams to simulate network
        pipeOut = new PipedOutputStream();
        try {
            pipeIn = new PipedInputStream( pipeOut );
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }


        //create network IO
        tcpOut = new TcpPacketOutput( pipeOut );


        tcpIn = new TcpPacketInput(pipeIn, 50); //50 is max packet id

    }

    @Test
    public void testSinglePacketTransfer() {

        //data to send
        int di = 1234243523;
        float df = 234.08763f;

        //write data to a netOut
        NetworkDataOutput dataOut = new NetworkDataOutput();

        dataOut.writeInt(di);
        dataOut.writeFloat(df);

        tcpOut.send(0, dataOut);


        //recieve
        tcpIn.pollPackets();

        //check if packet arrived
        Assert.assertTrue(tcpIn.hasPacket(0));

        //retrieve packet
        NetworkDataInput dataIn = tcpIn.pollPacket(0);

        int gotI = dataIn.readInt();
        float gotF = dataIn.readFloat();

        Assert.assertEquals(di, gotI);
        Assert.assertEquals(df, gotF, 0);
    }

    @Test
    public void testSingleStringPacketTransfer() {

        //data to send
        String  strSend = "haha seljsfs";

        //write data to a netOut
        NetworkDataOutput dataOut = new NetworkDataOutput();

        dataOut.writeString(strSend);

        tcpOut.send(0, dataOut);

        //input
        tcpIn.pollPackets();

        NetworkDataInput dataIn = tcpIn.pollPacket(0);

        String strGet = dataIn.readString();

        Assert.assertEquals(strSend, strGet);
    }

    @Test
    public void testNodataPacketTransfer() {

        //write data to a netOut
        NetworkDataOutput dataOut = new NetworkDataOutput();
        tcpOut.send(24, dataOut);

        //input
        tcpIn.pollPackets();

        NetworkDataInput dataIn = tcpIn.pollPacket(24);

        Assert.assertNotNull(dataIn);

    }

    @Test
    public void testMultiplePacketTransfer() {
        //data to send
        int p1Int = 123412, p2Int = 34, p3Int = 756;

        //write data to a netOut
        NetworkDataOutput p1 = new NetworkDataOutput(), p2 = new NetworkDataOutput(), p3 = new NetworkDataOutput();

        p1.writeInt(p1Int);
        p2.writeInt(p2Int);
        p3.writeInt(p3Int);

        tcpOut.send(0, p1);
        tcpOut.send(1, p2);
        tcpOut.send(2, p3);

        //input
        tcpIn.pollPackets();

        NetworkDataInput dataIn = tcpIn.pollPacket(0);
        int p1Intr = dataIn.readInt();

        dataIn = tcpIn.pollPacket(1);
        int p2Intr = dataIn.readInt();

        dataIn = tcpIn.pollPacket(2);
        int p3Intr = dataIn.readInt();


        Assert.assertEquals(p1Intr, p1Int);
        Assert.assertEquals(p2Intr, p2Int);
        Assert.assertEquals(p3Intr, p3Int);
    }

    @Test
    public void testSendingMultipleOfSameType() {
        //data to send
        int p1Int = 123412, p2Int = 34, p3Int = 756, p4Int = 2333;

        //write data to a netOut
        NetworkDataOutput p1 = new NetworkDataOutput(),
                p2 = new NetworkDataOutput(),
                p3 = new NetworkDataOutput(),
                p4 = new NetworkDataOutput();

        p1.writeInt(p1Int);
        p2.writeInt(p2Int);
        p3.writeInt(p3Int);
        p4.writeInt(p4Int);

        tcpOut.send(5, p1);
        tcpOut.send(5, p2);
        tcpOut.send(5, p3);
        tcpOut.send(5, p3);
        tcpOut.send(5, p3);

        tcpOut.send(24, p4);

        //input
        tcpIn.pollPackets();

        Assert.assertTrue(tcpIn.hasPacket(5));
        Assert.assertTrue(tcpIn.hasPacket(24));
        Assert.assertFalse(tcpIn.hasPacket(0));

        Assert.assertEquals(tcpIn.availablePackets(), 6);
        Assert.assertEquals(tcpIn.availablePacketTypes(), 2);
        Assert.assertEquals(tcpIn.packetCount(5), 5);
        Assert.assertEquals(tcpIn.packetCount(24), 1);
        Assert.assertEquals(tcpIn.packetCount(0), 0);

        //poll first packet
        NetworkDataInput dataIn = tcpIn.pollPacket(5);
        int p1Intr = dataIn.readInt();

        //check if packet received is right
        Assert.assertEquals(p1Intr, p1Int);

        Assert.assertTrue(tcpIn.hasPacket(5));
        Assert.assertTrue(tcpIn.hasPacket(24));
        Assert.assertFalse(tcpIn.hasPacket(0));

        Assert.assertEquals(tcpIn.availablePackets(), 5);
        Assert.assertEquals(tcpIn.availablePacketTypes(), 2);
        Assert.assertEquals(tcpIn.packetCount(5), 4);
        Assert.assertEquals(tcpIn.packetCount(24), 1);
        Assert.assertEquals(tcpIn.packetCount(0), 0);

        //poll a packet with id 5
        tcpIn.pollPacket(5);

        Assert.assertTrue(tcpIn.hasPacket(5));
        Assert.assertTrue(tcpIn.hasPacket(24));
        Assert.assertFalse(tcpIn.hasPacket(0));

        Assert.assertEquals(tcpIn.availablePackets(), 4);
        Assert.assertEquals(tcpIn.availablePacketTypes(), 2);
        Assert.assertEquals(tcpIn.packetCount(5), 3);
        Assert.assertEquals(tcpIn.packetCount(24), 1);
        Assert.assertEquals(tcpIn.packetCount(0), 0);


        //test when 24 packet is removed
        tcpIn.pollPacket(24);

        Assert.assertTrue(tcpIn.hasPacket(5));
        Assert.assertFalse(tcpIn.hasPacket(24)); //<-- set to false
        Assert.assertFalse(tcpIn.hasPacket(0));

        Assert.assertEquals(tcpIn.availablePackets(), 3);
        Assert.assertEquals(tcpIn.availablePacketTypes(), 1);
        Assert.assertEquals(tcpIn.packetCount(5), 3);
        Assert.assertEquals(tcpIn.packetCount(24), 0);
        Assert.assertEquals(tcpIn.packetCount(0), 0);

        //test when one is removed
        tcpIn.removePacket(5);

        Assert.assertTrue(tcpIn.hasPacket(5));

        Assert.assertEquals(2, tcpIn.availablePackets());
        Assert.assertEquals(tcpIn.availablePacketTypes(), 1);
        Assert.assertEquals(tcpIn.packetCount(5), 2);

        //test when the 2 rest packets are retrieved
        LinkedList<NetworkDataInput> inputs = tcpIn.pollAllPackets(5);

        Assert.assertFalse(tcpIn.hasPacket(5));

        Assert.assertEquals(tcpIn.availablePackets(), 0);
        Assert.assertEquals(tcpIn.availablePacketTypes(), 0);
        Assert.assertEquals(tcpIn.packetCount(5), 0);

        //check length of inputs
        Assert.assertEquals( inputs.size(), 2 );

        //check if packets are right
        Assert.assertEquals( inputs.get(0).readInt(), p3Int);
        Assert.assertEquals( inputs.get(1).readInt(), p3Int);

    }

    @Test
    public void testReadWrongPacketId() {

        //write data to a netOut
        tcpOut.sendEmpty(37);

        //input
        tcpIn.pollPackets();

        //check that wrong id is not registered
        Assert.assertFalse(tcpIn.hasPacket(50));
        NetworkDataInput dataIn = tcpIn.pollPacket(50);
        Assert.assertNull(dataIn);

        //check that right id is registered
        Assert.assertTrue(tcpIn.hasPacket(37));
        dataIn = tcpIn.pollPacket(37);
        Assert.assertNotNull(dataIn);

    }

    @After
    public void tearDown() {
        try {
            pipeOut.close();
            pipeIn.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
