package tryingStuff;


import game.web_socket.WebSocketServer;

/**
 * Created by eirik on 03.12.2018.
 */
public class WebSocketPlay {

    private static int TCP_PORT = 4444;

    private WebSocketServer server;

    public WebSocketPlay() {
        server = new WebSocketServer(TCP_PORT);
        server.start();

        long time = System.currentTimeMillis();
        while(true) {
            long deltaTime = System.currentTimeMillis() - time;
            if (deltaTime >= 1000) {
                time = System.currentTimeMillis();
                System.out.println("Sending HEI");
                server.send("HEI");
            }
        }
    }

    public static void main(String[] args) {
        new WebSocketPlay();
    }
}