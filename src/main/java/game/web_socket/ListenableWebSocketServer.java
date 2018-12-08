package game.web_socket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;

/**
 * Created by eirik on 03.12.2018.
 */
public class ListenableWebSocketServer extends org.java_websocket.server.WebSocketServer {
    interface Open {
        void onOpen(WebSocket conn, ClientHandshake handshake);
    }
    interface Close {
        void onClose(WebSocket conn, int code, String reason, boolean remote);
    }
    interface Message {
        void onMessage(WebSocket conn, String message);
    }
    interface Error {
        void onError(WebSocket conn, Exception ex);
    }

    Open onOpen;
    Close onClose;
    Message onMessage;
    Error onError;

    ListenableWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        onOpen.onOpen(conn, handshake);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        onClose.onClose(conn, code, reason, remote);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        onMessage.onMessage(conn, message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        onError.onError(conn, ex);
    }

    @Override
    public void onStart() {
        System.out.println("WebSocketServer server is running");
    }

}
