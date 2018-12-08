package game.server;

import engine.UserInput;
import engine.network.*;
import game.ClientGameTeams;

import java.util.Arrays;
import java.util.List;

/**
 * Created by haraldvinje on 06-Jul-17.
 */
public class ServerGame implements Runnable {



    private ServerCharacterSelection characterSelection;
    private ServerIngame serverIngame;

//    private List< List<ServerClientHandler> > teamClients;
//
//    private HashMap<ServerClientHandler, Integer> clientsCharacter;

    private ServerGameTeams teams;


    private static final float FRAME_INTERVAL = 1.0f/60.0f;


    private boolean running = true;
    private boolean shouldTerminate = false;

    private UserInput userInput;

    private long lastTime;


    private boolean selectingCharacter = true;
    private int waitingClientsIngame = -1;

    private boolean displayWindow;

    public ServerGame() {
        this(false);
    }
    public ServerGame(boolean displayWindow) {
        this.displayWindow = displayWindow;
    }

    public void init( ServerGameTeams teams ) {

        this.teams = teams;

    }


    @Override
    public void run() {


        float timeSinceUpdate = 0;

        while (running) {
            timeSinceUpdate += timePassed();
            //System.out.println("Time since update: "+timeSinceUpdate);

            if (timeSinceUpdate >= FRAME_INTERVAL) {
                timeSinceUpdate -= FRAME_INTERVAL;
                update();
            }

        }

        onTerminate();

    }


    public void update() {

        //poll packets for all clients
        teams.forEachClient(c -> c.getTcpPacketIn().pollPackets());

        handleCharacterSelect();

        handleWaitForClientsIngame();
    }

    public List<ServerClientHandler> getClients() {
        synchronized (this) {
            return Arrays.asList( teams.getAllClients() );
        }
    }

    private void handleCharacterSelect() {
        if (!selectingCharacter) return;

        //retrieve selections
        teams.forEachClient(client -> {
            TcpPacketInput tcpPacketIn = client.getTcpPacketIn();

            //if one client has disconnected, terminate game
            //else, tell clients we are alive
            if (tcpPacketIn.isRemoteSocketClosed()) {

                //tell server we want to terminate
                //clients should be removed when they enter idle state on server if they eare inactive
                setShouldTerminate();
            }
            else {
                client.getTcpPacketOut().sendHostAlive();
            }

            NetworkDataInput data = client.getTcpPacketIn().pollPacket(NetworkPregamePackets.CHARSELECT_CLIENT_CHOSE_CHARACTER);

            if (data != null) {
                int characterId = data.readInt();

                teams.setClientCharacterId(client, characterId);
            }
        });

        if (!teams.hasCharacterSelectionRemaining()) {
            //done selecting character, initialize game
            selectingCharacter = false;
            System.out.println("Done selecting character");

            initGame();
        }
    }

    private void handleWaitForClientsIngame() {
        if (selectingCharacter) return;
        if (waitingClientsIngame <= 0) return;

        //check if clients are ingame. If so, send init client team data
        teams.forEachClient(client -> {
            if ( client.getTcpPacketIn().removeIfHasPacket(NetworkPregamePackets.INGAME_CLIENT_READY) ) {

                //ingame received, send client teams to client
                ClientGameTeams clientTeams = teams.getClientGameTeams(client);
                NetworkDataOutput dataOut = NetworkPregameUtils.clientGameTeamsToPacket(clientTeams);
                client.getTcpPacketOut().send(NetworkPregamePackets.INGAME_SERVER_CLIENT_GAME_TEAMS, dataOut);

                //update how many clients we are waiting for
                --waitingClientsIngame;
            }
        });

        //check if all clients are ingame
        if (waitingClientsIngame == 0) {

            //clear net in
            Arrays.asList( teams.getAllClients() ).forEach(client -> client.getTcpPacketIn().clear());

            //blocking until game ends
            serverIngame.start();
        }

    }

    private void initGame() {
        //init game
        System.out.println("Initializing inGame, waiting for clients");
        serverIngame = new ServerIngame(displayWindow);
        serverIngame.init(this, teams);

        //tell clients to go to ingame
        teams.forEachClient(client -> {

            client.getTcpPacketOut().sendEmpty(NetworkPregamePackets.CHARSELECT_SERVER_GOTO_GAME);

        });

        //start waiting for clients to report ingame
        waitingClientsIngame = teams.getTotalClientCount();
    }


    private void onTerminate() {
        //tell clients that we are terminating
        getClients().forEach(client -> client.getTcpPacketOut().sendEmpty(NetworkPregamePackets.GAME_SERVER_EXIT));
    }

    void setShouldTerminate() {
        synchronized (this) {
            shouldTerminate = true;
        }
    }
    public boolean isShouldTerminate(){
        synchronized (this) {
            return shouldTerminate;
        }
    }

    public void terminate() {
        if (serverIngame != null)
            serverIngame.terminate();

        running = false;
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