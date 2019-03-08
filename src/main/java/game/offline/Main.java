package game.offline;

public class Main {

    public static void main(String[] args) {
//        GameInstanceLoader gameLoader = new GameInstanceLoader();
//        Game game = gameLoader.load("");

        DevGame g = new DevGame();
        g.init();
        g.start();
//        Game_old g = new Game_old();
//        g.init();
//        g.start();

//        ServerGame serverGame = new ServerGame();
//        serverGame.init();
//        serverGame.start();

//        ClientIngame clientGame = new ClientIngame();
//        clientGame.init();
//        clientGame.start();

//        try {
//            ServerNetworkSys serverSys = new ServerNetworkSys();
//
//
//            Thread.sleep(100);
//
//
//            ClientNetworkSys clientSys = new ClientNetworkSys("127.0.0.1");
//
//            Thread.sleep(100);
//
//            serverSys.update(); //notice connected client
//
//            Thread.sleep(100);
//
//
//            //send input state
//            CharacterInputData id = new CharacterInputData();
//            id.setMovement(true, false, false, true);
//            id.setActions(false, true);
//            id.setAim(100.56f, 643f);
//
//            System.out.println("[main] Seting up client to send data: "+id);
//            clientSys.sendInputData(id);
//            System.out.println("[main] Client has sent data");
//
//            Thread.sleep(100);
//
//            //server should receive data
//            System.out.println("[main] letting server receive data");
//            serverSys.update();
//
//            Thread.sleep(100);
//
//            //server send gameState
//            System.out.println("[main] let server send game state");
//            AllCharacterStateData gs = new AllCharacterStateData();
//            gs.setX1(34);
//            gs.setY1(456);
//            gs.setRotation1(0.43f);
//
//            gs.setX2(43225.43f);
//            gs.setY2(435.221f);
//            gs.setRotation2(2121f);
//
//            serverSys.sendGameState(gs);
//
//            System.out.println("Sendt: " + gs);
//
//            Thread.sleep(100);
//
//            //client should receive gameState
//            System.out.println("Client received");
//            System.out.println( clientSys.getStateData() );
//
//            clientSys.end();
//            serverSys.close();
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public String toString () {
        return "main";
    }
}
