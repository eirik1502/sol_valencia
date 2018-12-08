package game.server;

import engine.*;
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
import engine.utils.tickers.LinearTicker;
import engine.utils.tickers.Ticker;
import engine.visualEffect.VisualEffectComp;
import engine.window.Window;
import utils.loggers.Logger;
import utils.maths.Vec4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by eirik on 29.06.2017.
 */
public class Server {

    private static final float FRAME_INTERVAL = 1.0f/60.0f;

    private static String name = "server";
    public static Logger logger = new Logger(name);


    private Window window;
    private UserInput userInput;

    private WorldContainer wc;

    private Ticker updateTicker;

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

    private boolean displayWindow;
    private BufferedReader systemInReader;



    public Server() {
        this(false);
    }
    public Server(boolean displayWindow) {
        this.displayWindow = displayWindow;

        Server.logger.println("constructed");
    }

    public void init() {
        Server.logger.println("\n--- INIT SERVER ---\n");

        if (displayWindow) {
            Server.logger.println("creating window");
            window = new Window(0.3f, 0.3f, "D1n-only Server SII");

            Server.logger.println("creating user input");
            userInput = new UserInput(window, 1, 1);


            //load stuff
            Font.loadFonts(FontType.BROADWAY);
            Server.logger.println("fonts loaded");

        }
        Server.logger.println("creating world container");
        wc = new WorldContainer();

        Server.logger.println("initing world container");
        initWorldContainer();

        Server.logger.println("creating initial entities");
        createInitialEntities();



        //create connection listener
        Server.logger.println("creating connection listener");
        connectionInput = new ServerConnectionInput(NetworkUtils.PORT_NUMBER);
        serverConnectionInputThread = new Thread(connectionInput);

        systemInReader = new BufferedReader(new InputStreamReader(System.in));

        updateTicker = new LinearTicker(FRAME_INTERVAL, deltaTime -> update(deltaTime));
    }

    private void createInitialEntities() {
        if (displayWindow) {
            infoTextEntity1 = wc.createEntity("info text");
            wc.addComponent(infoTextEntity1, new PositionComp(10, 10));
            wc.addComponent(infoTextEntity1, new TextMeshComp(new TextMesh(
                    "", Font.getDefaultFont(), 64, new Vec4(1, 0.6f, 1, 1)
            )));
            infoTextEntity2 = wc.createEntity("info text");
            wc.addComponent(infoTextEntity2, new PositionComp(10, 250));
            wc.addComponent(infoTextEntity2, new TextMeshComp(new TextMesh(
                    "", Font.getDefaultFont(), 64, new Vec4(1, 0.6f, 1, 1)
            )));
        }
    }

    public void start() {
        Server.logger.println("\n--- SERVER STARTING ---\n");

        serverConnectionInputThread.start();

        updateTicker.start(); //blocking

        terminate();

    }

    private void printServerState() {
        printServerState("");
    }
    private void printServerState(String initialText) {
        StringBuilder sb1 = new StringBuilder(64);
        sb1.append("-> ").append(initialText).append("\n");
        sb1.append("\tClients connected: " + connectedClients.size() + "\n");
        sb1.append("\tClients idle: " + idleClients.size() + "\n");
        sb1.append("\tClients in 1v1 queue: " + gameQueue1v1.size() + "\n");
        sb1.append("\tClients in 2v2 queue: " + gameQueue2v2.size() + "\n");
        sb1.append("\tGames running: " + gamesRunning.size() + "\n");
        System.out.println(sb1.toString());
    }

    public void update(float deltaTime) {

        //poll window events
        if (displayWindow)
            window.pollEvents();


        //add pending connections to server state
        handleNewConnections();

        //handle clients that want to enter game queue
        handleIdleState();

        //handle game queue, and potentially start new games
        handleGameQueueState();

        //handle games running
        handleGamesRunning();

        if (displayWindow) {
            //print state
            StringBuilder sb1 = new StringBuilder(64);
            StringBuilder sb2 = new StringBuilder(64);
            sb1.append("Server\n");
            sb1.append("Clients connected: " + connectedClients.size() + "\n");
            sb1.append("Clients idle: " + idleClients.size() + "\n");
            sb2.append("Clients in 1v1 queue: " + gameQueue1v1.size() + "\n");
            sb2.append("Clients in 2v2 queue: " + gameQueue2v2.size() + "\n");
            sb2.append("Games running: " + gamesRunning.size() + "\n");

            wc.getComponent(infoTextEntity1, TextMeshComp.class).getTextMesh().setString(sb1.toString());
            wc.getComponent(infoTextEntity2, TextMeshComp.class).getTextMesh().setString(sb2.toString());
        }

        //update systems to show server status
        wc.updateSystems();

        if (displayWindow) {
            if (window.shouldClosed() || userInput.isKeyboardPressed(UserInput.KEY_ESCAPE))
                updateTicker.stop();
        }
        else {
            try {
                if (systemInReader.ready()) {
                    String inline = systemInReader.readLine();
                    //System.out.println(inline);
                    if (inline.equals("s") || inline.equals("stop")) {
                        updateTicker.stop();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleNewConnections() {
        if (connectionInput.hasConnectedClients()) {

            ServerClientHandler clientHandler = connectionInput.getConnectedClient();

            //put new client in list of clients and in the idle state
            connectedClients.add(clientHandler);
            idleClients.add(clientHandler);

//            activateClientIcon(clientHandler);
            printServerState("New client connected");
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
                printServerState("An idle client disconnected");

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

                printServerState("client moved to game queue");
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

                    printServerState("a game queue client has disconnected");
                }
                //tell client that the server is alive
                else {
                    client.getTcpPacketOut().sendHostAlive();
                }

                //check if a client wants to exit queue, then move it to idle
                if (tcpPacketIn.removeIfHasPacket(NetworkPregamePackets.QUEUE_CLIENT_EXIT)) {
                    it.remove();
                    idleClients.add(client);
                    printServerState("client removed from game queue");
                }
            }
        }

        //check if there are enough clients waiting to start a game

        //1v1 queue
        if (gameQueue1v1.size() >= 2) {

            printServerState("starting a 1v1 game");

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

            printServerState("starting a 2v2 game");

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
        Server.logger.println("\n--- SERVER TERMINATING ---\n");

        Server.logger.println("terminate running games");
        //terminate games running
        terminateAllRunningGames();

        Server.logger.println("terminating world container");
        wc.terminate();

        if (displayWindow) {
            Server.logger.println("terminate window");
            window.close();
        }

        Server.logger.println("terminating connection listener");
        connectionInput.terminate();

        Server.logger.println("joining connection listen thread");
        try {
            serverConnectionInputThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (displayWindow) {
            Server.logger.println("terminating GLFW");
            Window.terminateGLFW();

        }

        Server.logger.println("BYE BYE");
        Server.logger.close();
    }

    private void createGame(ServerGameTeams teams) {


        ServerGame game = new ServerGame(displayWindow);
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
        wc.assignComponentType(MeshCenterComp.class);
        wc.assignComponentType(ViewControlComp.class);
        wc.assignComponentType(ProjectileComp.class); //because of draw order

        if (displayWindow) {
            wc.assignComponentType(ColoredMeshComp.class);
            wc.assignComponentType(TexturedMeshComp.class);
            wc.assignComponentType(TextMeshComp.class);
            wc.assignComponentType(TextMeshComp.class);
            wc.assignComponentType(ViewRenderComp.class);
            wc.assignComponentType(VisualEffectComp.class);


            wc.addSystem(new RenderSys(window));
        }
    }

}
