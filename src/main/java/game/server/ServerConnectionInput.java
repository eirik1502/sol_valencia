package game.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

/**
 * Created by eirik on 21.06.2017.
 */
public class ServerConnectionInput implements Runnable {

    private ServerSocket serverSocket;


    private LinkedList<ServerClientHandler> clientsConnected;

    private boolean running = true;


    public ServerConnectionInput(int port) {

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e1) {
            e1.printStackTrace();
        }


        clientsConnected = new LinkedList<>();
    }


    @Override
    public void run() {

        //Socket socket = null;

        while(running) {
            System.out.println("Waiting for client");
            try {

                clientConnected(serverSocket.accept());


            } catch (SocketException e) {
                System.out.println("Socket closed while waiting for connections");
                //e.printStackTrace();
            } catch (IOException e) {
                throw new IllegalStateException("IO exception");
            }
        }

    }

//    public void terminate() {
//        running = false;
//    }

    public void clientConnected(Socket socket) {
        System.out.println("[server] Client connected to server");

        //create clientHandeler
        ServerClientHandler clientHandler = new ServerClientHandler(socket);
        addConnectedClient(clientHandler);
    }

    private void addConnectedClient(ServerClientHandler clientHandler) {
        synchronized (clientsConnected) {
            clientsConnected.add(clientHandler);
        }
    }

    public boolean hasConnectedClients() {
        synchronized (clientsConnected) {
            return !clientsConnected.isEmpty();
        }
    }
    public ServerClientHandler getConnectedClient() {
        synchronized (clientsConnected) {
            return clientsConnected.poll();
        }
    }


    /**
     * force end, most likely blocked while waiting for connections
     */
    public void terminate() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
