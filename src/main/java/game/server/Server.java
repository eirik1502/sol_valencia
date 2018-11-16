package game.server;

import engine.PositionComp;
import engine.RotationComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.combat.abilities.ProjectileComp;
import engine.graphics.*;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.text.TextMesh;
import engine.graphics.text.TextMeshComp;
import engine.graphics.view_.ViewControlComp;
import engine.network.NetworkPregamePackets;
import engine.network.NetworkUtils;
import engine.network.TcpPacketInput;
import engine.network.client.ClientUtils;
import engine.visualEffect.VisualEffectComp;
import engine.window.Window;
import utils.maths.Vec4;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by eirik on 29.06.2017.
 */
public class Server {

    private static final float FRAME_INTERVAL = 1.0f/60.0f;


    private Window window;
    private UserInput userInput;

    private WorldContainer wc;

    private long lastTime;

    //clinet connection listener
    private ServerConnectionInput connectionInput;
    private Thread serverConnectionInputThread;


    //texts to print info
    private int infoTextEntity1, infoTextEntity2;

    //all connected clients
    private List<ServerClientHandler> connectedClients = new ArrayList<>();

    //idle clients
    private List<ServerClientHandler> idleClients = new ArrayList<>();

    //game queues
    private LinkedList<ServerClientHandler> gameQueue1v1 = new LinkedList<>();
    private LinkedList<ServerClientHandler> gameQueue2v2 = new LinkedList<>();

    //games running
    private ConcurrentMap<ServerGame, Thread> gamesRunning = new ConcurrentHashMap<>();



    public void init() {
        window = new Window(0.3f, 0.3f, "D1n-only Server SII");
        userInput = new UserInput(window, 1, 1);

        //load stuff
        Font.loadFonts(FontType.BROADWAY);

        wc = new WorldContainer();
        initWorldContainer();
        createInitialEntities();


        //create connection listener
        connectionInput = new ServerConnectionInput(NetworkUtils.PORT_NUMBER);
        serverConnectionInputThread = new Thread(connectionInput);

    }

    private void createInitialEntities() {
        infoTextEntity1 = wc.createEntity("info text");
        wc.addComponent(infoTextEntity1, new PositionComp(10, 10));
        wc.addComponent(infoTextEntity1, new TextMeshComp(new TextMesh(
                "", Font.getDefaultFont(), 64, new Vec4(1,0.6f,1,1)
        )));
        infoTextEntity2 = wc.createEntity("info text");
        wc.addComponent(infoTextEntity2, new PositionComp(10, 250));
        wc.addComponent(infoTextEntity2, new TextMeshComp(new TextMesh(
                "", Font.getDefaultFont(), 64, new Vec4(1,0.6f,1,1)
        )));
    }

    public void start() {

        serverConnectionInputThread.start();

        lastTime = System.nanoTime();

        float timeSinceUpdate = 0;

        while (true) {
            timeSinceUpdate += timePassed();
            //System.out.println("Time since update: "+timeSinceUpdate);

            if (timeSinceUpdate >= FRAME_INTERVAL) {
                timeSinceUpdate -= FRAME_INTERVAL;

                update();
            }


            if (window.shouldClosed() || userInput.isKeyboardPressed(UserInput.KEY_ESCAPE))
                break;
        }


        terminate();

    }


    public void update() {

        //poll window events
        window.pollEvents();


        //add pending connections to server state
        handleNewConnections();

        //handle clients that want to enter game queue
        handleIdleState();

        //handle game queue, and potentially start new games
        handleGameQueueState();

        //handle games running
        handleGamesRunning();

        //print state
        StringBuilder sb1 = new StringBuilder(64);
        StringBuilder sb2 = new StringBuilder(64);
        sb1.append("Server\n");
        sb1.append("Clients connected: " + connectedClients.size() + "\n");
        sb1.append("Clients idle: " + idleClients.size() + "\n");
        sb2.append("Clients in 1v1 queue: " + gameQueue1v1.size() + "\n");
        sb2.append("Clients in 2v2 queue: " + gameQueue2v2.size() + "\n");
        sb2.append("Games running: " + gamesRunning.size() + "\n");

        ((TextMeshComp) wc.getComponent(infoTextEntity1, TextMeshComp.class)).getTextMesh().setString( sb1.toString() );
        ((TextMeshComp) wc.getComponent(infoTextEntity2, TextMeshComp.class)).getTextMesh().setString( sb2.toString() );


        //update systems to show server status
        wc.updateSystems();
    }

    private void handleNewConnections() {
        if (connectionInput.hasConnectedClients()) {

            System.out.println("New client connected");
            ServerClientHandler clientHandler = connectionInput.getConnectedClient();

            //put new client in list of clients and in the idle state
            connectedClients.add(clientHandler);
            idleClients.add(clientHandler);

//            activateClientIcon(clientHandler);
        }
    }

    private void handleIdleState() {
        Iterator<ServerClientHandler> it = idleClients.iterator();

        while(it.hasNext()) {
            ServerClientHandler client = it.next();

            TcpPacketInput tcpPacketIn = client.getTcpPacketIn();
            boolean packetPolled = tcpPacketIn.pollPackets();
//            if (packetPolled) System.out.println(c.getTcpPacketIn());

            //if client is disconnected, remove it
            //else tell it that server is alive
            if (tcpPacketIn.isRemoteSocketClosed()) {
                System.err.println("A client has disconnected");

                //tell the client that we are disconecting it
                client.getTcpPacketOut().sendHostDisconnected();

                it.remove();
                connectedClients.remove(client);

                //terminate client
                client.terminate();
            }
            else {
                client.getTcpPacketOut().sendHostAlive();
            }
        }


        //check if anyone wants to enter game queue
        it = idleClients.iterator();

        while(it.hasNext()) {
            ServerClientHandler client = it.next();

            TcpPacketInput tcpPacketIn = client.getTcpPacketIn();

            //handle game queue requests, 1v1 and 2v2
            LinkedList<ServerClientHandler> queue = null;

            if (tcpPacketIn.removeIfHasPacket(NetworkPregamePackets.QUEUE_CLIENT_REQUEST_QUEUE_1V1)) {

                queue = gameQueue1v1;
            }
            else if(tcpPacketIn.removeIfHasPacket(NetworkPregamePackets.QUEUE_CLIENT_REQUEST_QUEUE_2V2)) {

                queue = gameQueue2v2;
            }

            if (queue != null) {
                //remove from idle state and put into queue state
                it.remove();
                queue.add(client);

                //let client know that it is put in queue
                client.getTcpPacketOut().sendEmpty(NetworkPregamePackets.QUEUE_SERVER_PUT_IN_QUEUE);
            }
        }
    }

    private void handleGameQueueState() {
        List< LinkedList<ServerClientHandler> > gameQueues = new ArrayList<>();
        gameQueues.add(gameQueue1v1);   gameQueues.add(gameQueue2v2);

        //poll net input and handle queue exit for all game queues
        for (LinkedList<ServerClientHandler> gameQueue : gameQueues) {

            Iterator<ServerClientHandler> it = gameQueue.iterator();
            while (it.hasNext()) {
                ServerClientHandler client = it.next();

                TcpPacketInput tcpPacketIn = client.getTcpPacketIn();

                //pollPackets
                boolean packetPolled = tcpPacketIn.pollPackets();
                //if (packetPolled) System.out.println(tcpPacketIn);

                //handle disconnection
                if (tcpPacketIn.isRemoteSocketClosed()) {
                    //client disconnected

                    //tell client we are no longer handling it
                    client.getTcpPacketOut().sendHostDisconnected();

                    //remove it from internal state
                    it.remove();
                    connectedClients.remove(client);

                    //terminate client object
                    client.terminate();
                }
                //tell client that the server is alive
                else {
                    client.getTcpPacketOut().sendHostAlive();
                }

                //check if a client wants to exit queue, then move it to idle
                if (tcpPacketIn.removeIfHasPacket(NetworkPregamePackets.QUEUE_CLIENT_EXIT)) {
                    it.remove();
                    idleClients.add(client);
                }
            }
        }

        //check if there are enough clients waiting to start a game

        //1v1 queue
        if (gameQueue1v1.size() >= 2) {

            System.out.println("At least two clients in game queue, starting game");

            //retrieve clients
            ServerClientHandler client1 = gameQueue1v1.pop();
            ServerClientHandler client2 = gameQueue1v1.pop();

            //let clients know that they are about to start game
            client1.getTcpPacketOut().sendEmpty(NetworkPregamePackets.QUEUE_SERVER_GOTO_CHARACTERSELECT);
            client2.getTcpPacketOut().sendEmpty(NetworkPregamePackets.QUEUE_SERVER_GOTO_CHARACTERSELECT);

            //datastructure to hold clients
            ServerGameTeams teams = new ServerGameTeams(client1, client2);

            createGame( teams );
        }

        //2v2 queue
        if (gameQueue2v2.size() >= 4) {

            System.out.println("At least two clients in game queue, starting game");

            //retrieve clients
            ServerClientHandler client1 = gameQueue2v2.pop();
            ServerClientHandler client2 = gameQueue2v2.pop();
            ServerClientHandler client3 = gameQueue2v2.pop();
            ServerClientHandler client4 = gameQueue2v2.pop();

            //let clients know that they are about to start game
            client1.getTcpPacketOut().sendEmpty(NetworkPregamePackets.QUEUE_SERVER_GOTO_CHARACTERSELECT);
            client2.getTcpPacketOut().sendEmpty(NetworkPregamePackets.QUEUE_SERVER_GOTO_CHARACTERSELECT);
            client3.getTcpPacketOut().sendEmpty(NetworkPregamePackets.QUEUE_SERVER_GOTO_CHARACTERSELECT);
            client4.getTcpPacketOut().sendEmpty(NetworkPregamePackets.QUEUE_SERVER_GOTO_CHARACTERSELECT);

            //datastructure to hold clients
            ServerGameTeams teams = new ServerGameTeams(client1, client2, client3, client4);

            createGame( teams );
        }
    }

    public void handleGamesRunning() {
//        for (int i = 0; i < gamesRunning.size(); i++) {
        for (ServerGame game : gamesRunning.keySet()) {

            //check if games should close
            if (game.isShouldTerminate()) {
                terminateRunningGame(game);
            }
        }
    }

    public void terminate() {
        //terminate games running
        terminateAllRunningGames();


        wc.terminate();
        window.close();

        connectionInput.terminate();

        try {
            serverConnectionInputThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Window.terminateGLFW();
    }

    private void createGame(ServerGameTeams teams) {


        ServerGame game = new ServerGame();
        game.init(teams);

        Thread gameThread = new Thread(game);
        gamesRunning.put(game, gameThread);

        gameThread.start();
    }

    private void terminateAllRunningGames() {
        while (!gamesRunning.isEmpty()) {

            //retrieve a random element
            ServerGame game = gamesRunning.keySet().iterator().next();

            terminateRunningGame(game);
        }
    }

    private void terminateRunningGame(ServerGame game) {

        //put clients in idle state and clears netIn
        game.getClients().forEach( client -> {
            client.getTcpPacketIn().clear();
            idleClients.add(client);
        });

        //stop game
        Thread gameThread = gamesRunning.remove(game); //retrieve thread and remove entry

        game.terminate();
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void initWorldContainer() {
        wc.assignComponentType(PositionComp.class);
        wc.assignComponentType(RotationComp.class);
        wc.assignComponentType(ColoredMeshComp.class);
        wc.assignComponentType(TexturedMeshComp.class);
        wc.assignComponentType(TextMeshComp.class);
        wc.assignComponentType(MeshCenterComp.class);
        wc.assignComponentType(ViewControlComp.class);
        wc.assignComponentType(TextMeshComp.class);
        wc.assignComponentType(ViewRenderComp.class);
        wc.assignComponentType(VisualEffectComp.class);
        wc.assignComponentType(ProjectileComp.class); //because of draw order



        wc.addSystem(new RenderSys(window));
    }



    /**
     * time passed since last call to this method
     * @return
     */
    private float timePassed() {
        long newTime = System.nanoTime();
        int deltaTime = (int)(newTime - lastTime);
        float deltaTimeF = (float) deltaTime;

        lastTime = newTime;

        return deltaTimeF/1000000000;
    }
}
