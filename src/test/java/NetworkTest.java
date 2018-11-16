import org.junit.After;
import org.junit.Before;

import java.io.IOException;

/**
 * Created by eirik on 29.07.2017.
 */
public class NetworkTest {


    //Test transfer over local net

    //    private int port = 7777;
//
//    private ServerSocket serverAcceptSocket;
//    private Socket serverSocket, clientSocket;
//
//    private DataOutputStream serverOut, clientOut;
//    private DataInputStream serverIn, clientIn;


    @Before
    public void setUp() {

//        try {
//            serverAcceptSocket = new ServerSocket(port);
//
//            Thread serverAcceptThread = new Thread( () -> {
//                try {
//                    serverSocket = serverAcceptSocket.accept();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } );
//            serverAcceptThread.start();
//
//
//            Thread.sleep(100);
//
//            clientSocket = new Socket("localhost", port);
//
//            Thread.sleep(100);
//
//            serverAcceptThread.join();
//
//
//            serverOut = new DataOutputStream( serverSocket.getOutputStream() );
//            serverIn = new DataInputStream( serverSocket.getInputStream() );
//
//            clientOut = new DataOutputStream( clientSocket.getOutputStream() );
//            clientIn = new DataInputStream( clientSocket.getInputStream() );
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }



    }

    //    @Test
//    public void testConnection() {
//        int serverSend = 4536;
//        int clientSend = 965432;
//
//        int serverReceive = 0;
//        int clientReceive = 0;
//
//        try {
//            serverOut.writeInt(serverSend);
//            clientOut.writeInt(clientSend);
//
//            Thread.sleep(100);
//
//            clientReceive = clientIn.readInt();
//            serverReceive = serverIn.readInt();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//        Assert.assertEquals(serverSend, clientReceive);
//        Assert.assertEquals(clientSend, serverReceive);
//    }


//    @After
//    public void tearDown() {
//        try {
//            clientSocket.close();
//
//            serverAcceptSocket.close();
//            serverSocket.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
}
