package game.client;

/**
 * Created by eirik on 07.12.2018.
 */
public class MainGotoGameClient {

    public static void main(String[] args) {
        GotoGameClient client = new GotoGameClient();
        client.init();
        client.start();
    }
}
