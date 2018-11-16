package game.server;

/**
 * Created by eirik on 22.06.2017.
 */
public class MainServer {

    public static void main(String[] args) {

        Server s = new Server();
        s.init();
        s.start();
    }
}
