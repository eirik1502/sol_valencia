package tryingStuff.Kryonet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

/**
 * Created by eirik on 13.12.2018.
 */
public class KryonetFun {

    public static void main(String[] args) {
        //SETUP SERVER -----------------------
        Server server = new Server();
        server.start();
        try {
            server.bind(7779);
        } catch (IOException e) {
            System.err.println("Server could not listen to port 7779,");
            System.err.println(e.getMessage());
        }
        Kryo kryo = server.getKryo();
        kryo.register(NetPackage.class);
        //-----------------------------------

        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof NetPackage) {
                    NetPackage request = (NetPackage)object;
                    System.out.println("[server] got from client: " + request.text);

                    NetPackage response = new NetPackage();
                    response.text = "Thanks";
                    System.out.println("[server] responding too client: " + response.text);
                    connection.sendTCP(response);
                }
            }
        });

        //CLIENT SETUP ---------------
        Client client = new Client();
        client.start();
        try {
            client.connect(5000, "localhost", 7779);
        } catch (IOException e) {
            System.err.println("Client could not connect to server, but maybe next time :)");
        }
        Kryo clientKryo = client.getKryo();
        clientKryo.register(NetPackage.class);
        //----------------------------

        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                System.out.println("[client] server connected: " + connection.isConnected());

                if (object instanceof NetPackage) {
                    NetPackage response = (NetPackage)object;
                    System.out.println("[client] got response: " + response.text);
                }
            }
        });

        NetPackage request = new NetPackage();
        request.text = "this is a request";
        System.out.println("[Client] sending to server: " + request.text);
        client.sendTCP(request);

        try {
            Thread.sleep(1000);
            System.out.println("is client still connected? " + client.isConnected());

            server.close();

            Thread.sleep(100);

            System.out.println("is client still connected? " + client.isConnected());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}
