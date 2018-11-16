package game.client;

import engine.GameDataComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.audio.AudioComp;
import engine.audio.AudioMaster;
import engine.audio.Sound;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.view_.View;
import engine.network.NetworkPregamePackets;
import engine.network.TcpPacketInput;
import engine.network.TcpPacketOutput;
import engine.network.client.ClientStates;
import engine.visualEffect.VisualEffectSys;
import engine.window.Window;
import game.CharacterUtils;
import game.ClientGameTeams;
import game.GameUtils;
import game.SysUtils;

/**
 * Created by eirik on 22.06.2017.
 */
public class ClientIngame implements Runnable{

    private static final float FRAME_INTERVAL = 1.0f/60.0f;

    public static final float WINDOW_WIDTH = 1600f, WINDOW_HEIGHT = 900f;

    private long lastTime;
    private boolean running = true;



    private TcpPacketInput tcpPacketIn;
    private TcpPacketOutput tcpPacketOut;

    private Window window;
    private UserInput userInput;

    private WorldContainer wc;

    private boolean shouldTerminate = false;

    private boolean gameOver = false;


    private ClientGameTeams teams;



    public ClientIngame() {
    }

    public void init(TcpPacketInput tcpPacketIn, TcpPacketOutput tcpPacketOut, ClientGameTeams teams) {
        this.tcpPacketIn = tcpPacketIn;
        this.tcpPacketOut = tcpPacketOut;

        this.teams = teams;

        wc = new WorldContainer( new View(GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT) );

        System.out.println("HEELLLLLOOOOO");
    }


    public synchronized void terminate() {
        running = false;
    }
    //should create a separate monitor for this variable
    public synchronized boolean isShouldTerminate() {
        return shouldTerminate;
    }
    public synchronized void setShouldTerminate() {
//        VisualEffectSys.removeAllEffects();
        shouldTerminate = true;
    }

    /**
     * blocking while the game runs
     */
    @Override
    public void run() {

        window = new Window("Client    Siiiii");
        userInput = new UserInput(window, GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);

        //make sure window has focus
        window.focus();

        //load stuff
        Font.loadFonts(FontType.BROADWAY);
        AudioMaster.init();



        GameUtils.assignComponentTypes(wc);

        //create map
        if (teams.getTotalCharacterCount() <= 2) {
            GameUtils.createMap(wc);
        }
        else if (teams.getTotalCharacterCount() <= 4) {
            GameUtils.createLargeMap(wc);
        }
        else {
            throw new IllegalStateException("Dont know what map to use for " + teams.getTotalCharacterCount() + " clients");
        }

        int[][] charEntIds = CharacterUtils.createClientCharacters(wc, teams);
        GameUtils.createGameData(wc, teams, charEntIds);


//        List<Integer> charEntIds = CharacterUtils.createClientCharacters(wc, teams);
//
//        GameUtils.createGameData(wc, teams, charEntIds);

        //do this afte rbecaus of onscreen sys wich creates entities..
        SysUtils.addClientSystems(wc, window, userInput, tcpPacketIn, tcpPacketOut);


        //print initial state
        System.out.println("Initial state:");
        System.out.println(wc.entitiesToString());




        //game loop
        lastTime = System.nanoTime();

        float timeSinceUpdate = 0;

        while (running) {
            timeSinceUpdate += timePassed();
            //System.out.println("Time since update: "+timeSinceUpdate);

            if (timeSinceUpdate >= FRAME_INTERVAL) {
                timeSinceUpdate -= FRAME_INTERVAL;

                update();
            }


            if (window.shouldClosed() || userInput.isKeyboardPressed(UserInput.KEY_ESCAPE)) {
                if (!gameOver) {
                    //tell server to disconnect us
                    //we have to disconnect when exiting in the midle of a game
                    tcpPacketOut.sendHostDisconnected();
                }

                setShouldTerminate();
            }
        }

        onTerminate();

    }


    public void update() {

        window.pollEvents();

        wc.updateSystems();

        //check if game is over
        wc.entitiesOfComponentTypeStream(GameDataComp.class).forEach(entity -> {
            GameDataComp dataComp = (GameDataComp) wc.getComponent(entity, GameDataComp.class);

            if (dataComp.endGameRequest) {
                gameOver = true;
            }
        });

        //check if server has terminated our game
        handleExitGame();
    }

    private void handleExitGame() {
        //if received an exit game packet, exit
        if (tcpPacketIn.removeIfHasPacket(NetworkPregamePackets.GAME_SERVER_EXIT) ||
                tcpPacketIn.isRemoteSocketClosed()) {

            if (tcpPacketIn.isRemoteSocketClosed()) System.err.println("Remote socket is closed");

            //dont go out of game if we are in end game state
            if (gameOver) return;

            setShouldTerminate();
        }
    }

    private void onTerminate() {


        //terminate systems
        wc.terminate();

        window.close();
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
