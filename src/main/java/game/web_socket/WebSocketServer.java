package game.web_socket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import tryingStuff.WebSocketPlay;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * Used to oopen a socket from a web client (in browser)
 *
 * Created by eirik on 03.12.2018.
 */
public class WebSocketServer {
    public interface ClientConnected {
        void onNewClient(WebSocket conn, ClientHandshake handshake);
    }

    private Set<WebSocket> clientConnections;
    private ListenableWebSocketServer impServer;

    private ClientConnected clientConnectedListener = null;

    public WebSocketServer(int port) {
        clientConnections = new HashSet<>();
        impServer = new ListenableWebSocketServer(port);

        impServer.onOpen = (conn, handshake) -> {
            clientConnections.add(conn);
            System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());

            if (clientConnectedListener != null)
                clientConnectedListener.onNewClient(conn,handshake);
        };

        impServer.onClose = (conn, code, reason, remote) -> {
            clientConnections.remove(conn);
            System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        };

        impServer.onMessage = (conn, message) -> {
            System.out.println("Message from client: " + message);
            for (WebSocket sock : clientConnections) {
                sock.send(message);
            }
        };

        impServer.onError = (conn, exc) -> {
            //ex.printStackTrace();
            if (conn != null) {
                clientConnections.remove(clientConnections);
                // do some thing if required
            }
            System.out.println("ERROR from control client at " +
                    (conn != null? conn.getRemoteSocketAddress().getAddress().getHostAddress() :
                    "client disconnected"));
        };
    }

    public void start() {
        impServer.start();
    }

    public void setOnClientConnected(ClientConnected clientConnectedListener) {
        this.clientConnectedListener = clientConnectedListener;
    }

    public void send(String message) {
        for (WebSocket sock : clientConnections) {
            sock.send(message);
        }
    }

    public void stop() {
        try {
            impServer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
