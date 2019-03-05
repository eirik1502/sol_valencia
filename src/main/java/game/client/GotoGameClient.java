package game.client;

import engine.network.*;
import engine.network.client.ClientStates;
import engine.network.client.ClientUtils;
import engine.utils.tickers.LinearTicker;
import engine.utils.tickers.Ticker;
import game.ClientGameTeams;
import utils.maths.M;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by eirik on 07.12.2018.
 */
public class GotoGameClient {
    private static final String MODE_1V1 = "1v1", MODE_2V2 = "2v2";


    private String hostname = "localhost";
    private String gametype = MODE_1V1;
    private int characterId = 3;
    private boolean loop = false;


    private TcpPacketInput netIn;
    private TcpPacketOutput netOut;

    private LinearTicker ticker;
    ClientGameTeams teams = null;
    ClientIngame game;
    Thread gameThread;

    public GotoGameClient() {

    }

    public void init() {
        ticker = new LinearTicker(1);
    }

    public void start() {
        do {
            System.out.println("Connecting to server...");
            try {
                Socket socket = new Socket(hostname, NetworkUtils.PORT_NUMBER);

                netIn = new TcpPacketInput(socket.getInputStream());
                netOut = new TcpPacketOutput(socket.getOutputStream());

            } catch (IOException e) {
                System.err.println("Could not connect to server");
                exit(-1);
            }

            System.out.println("Connected to server!");

            int queueRequest = -1;
            if (gametype.equals(MODE_1V1)) queueRequest = NetworkPregamePackets.QUEUE_CLIENT_REQUEST_QUEUE_1V1;
            if (gametype.equals(MODE_2V2)) queueRequest = NetworkPregamePackets.QUEUE_CLIENT_REQUEST_QUEUE_2V2;
            if (queueRequest == -1) {
                System.err.println("invalid game mode given");
                continue; //restart
            }

            //requesting game queue
            System.out.println("Requesting " + gametype + " queue");
            netOut.sendEmpty(queueRequest);

            ticker.setListener(deltaTime -> {
                netIn.pollPackets();
                netOut.sendHostAlive();

                if (netIn.isRemoteSocketClosed()) {
                    System.err.println("Server closed");
                    exit(-1);
                }

                if (netIn.removeIfHasPacket(NetworkPregamePackets.QUEUE_SERVER_PUT_IN_QUEUE)) {
                    //were in queue in the server
                    System.out.println("Server put us in game queue");
                }

                if (netIn.removeIfHasPacket(NetworkPregamePackets.QUEUE_SERVER_GOTO_CHARACTERSELECT)) {
                    //goto character select
                    System.out.println("Server paired us with another client,\ngo to character select");
                    ticker.stop();
                }

            });

            System.out.println("Waiting in queue to be paired...");

            ticker.start(); //blocking until characterselect

            System.out.println("Selecting character with id " + characterId);

            NetworkDataOutput data = new NetworkDataOutput();
            data.writeInt(characterId);
            netOut.send(NetworkPregamePackets.CHARSELECT_CLIENT_CHOSE_CHARACTER, data);

            ticker.setListener(deltaTime -> {
                netIn.pollPackets();
                netOut.sendHostAlive();

                if (netIn.removeIfHasPacket(NetworkPregamePackets.CHARSELECT_SERVER_GOTO_GAME)) {
                    ticker.stop();
                }

                if (netIn.isRemoteSocketClosed()) {
                    System.err.println("Server closed");
                    exit(-1);
                }
            });

            System.out.println("Waiting for other clients to chose character...");

            ticker.start(); //blocking until goto ingame

            System.out.println("Requesting initial game data");
            netOut.sendEmpty(NetworkPregamePackets.INGAME_CLIENT_READY);

            ticker.setListener(deltaTime -> {
                netIn.pollPackets();

                NetworkDataInput dataIn = netIn.pollPacket(NetworkPregamePackets.INGAME_SERVER_CLIENT_GAME_TEAMS);
                if (dataIn != null) {
                    //retrieve client teams
                    teams = NetworkPregameUtils.packetToClientGameTeams(dataIn);
                    ticker.stop();
                }

                if (netIn.isRemoteSocketClosed()) {
                    System.err.println("Server closed");
                    exit(-1);
                }
            });
            System.out.println("Waiting for initial game data...");
            ticker.start();

            System.out.println("Starting game");

            //clear net in
            netIn.clear();

            stayAlive();

            startGame(teams); //running on its own thread

            ticker.setListener(deltaTime -> {
                if (game.isShouldTerminate()) {
                    //terminate game
                    terminateGame();

                    //clear net in
                    netIn.clear();

                    ticker.stop();
                }
            });
            ticker.start();

            System.out.println("Game exited");
            stayAliveOver();

        }
        while (loop);



        onExit();
        System.out.println("Client exiting, GOODBYE");
    }


    private void startGame(ClientGameTeams teams) {

        game = new ClientIngame(0.4f);
        game.init(netIn, netOut, teams);
        gameThread = new Thread(game);

        gameThread.start();
    }

    private void terminateGame() {
        game.terminate();

        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private Ticker stayAliveTicker;
    private Thread stayAlive;
    private void stayAlive() {
        stayAliveTicker = new LinearTicker(0.5f);
        stayAlive = new Thread(() -> {
            stayAliveTicker.setListener(deltaTime ->{
                netOut.sendHostAlive();
                //System.out.println(M.random() < 0.66 ? "ah" : "stayn' alive");
            });
            stayAliveTicker.start();
        });
        stayAlive.start();
    }
    private void stayAliveOver() {
        if (stayAliveTicker != null)
            stayAliveTicker.stop();

        try {
            if (stayAlive != null)
                stayAlive.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void onExit() {

        if (netIn != null) netIn.close();
        if (netOut != null) netOut.close();
    }

    private void exit(int code) {
        onExit();
        stayAliveOver();

        System.exit(code);
    }

}
