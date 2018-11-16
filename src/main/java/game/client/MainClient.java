package game.client;

/**
 * Created by eirik on 22.06.2017.
 */
public class MainClient {

    public static void main(String[] args) {

//        if (args.length != 1) {
//            System.err.println("Give the server host name as input when executing");
//            return;
//        }

//        Socket socket = null;
//        try {
//            socket = new Socket(args[0], NetworkUtils.PORT_NUMBER);
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new IllegalStateException("Could not connect to server");
//        }
//
//        List<Integer> friendlyCharacters = new ArrayList<>();
//        List<Integer> enemyCharacters = new ArrayList<>();
//        int team = 0;
//
//        try {
//            DataInputStream in = new DataInputStream(socket.getInputStream());
//
//            team = in.readInt();
//            friendlyCharacters.add( in.readInt() );
//            enemyCharacters.add( in.readInt() );
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        ClientIngame cg = new ClientIngame(socket);
//        cg.init(null, null, friendlyCharacters, enemyCharacters, team, 0);
//        cg.run();

        Client client = new Client("");
        client.init();
        client.start();
    }
}
