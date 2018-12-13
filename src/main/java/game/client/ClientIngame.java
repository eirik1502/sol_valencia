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
import utils.loggers.Logger;

/**
 * Created by eirik on 22.06.2017.
 */
public class ClientIngame implements Runnable{

    private static final float FRAME_INTERVAL = 1.0f/60.0f;
    private float relWindowSize = 0.5f;//-1;


    public static final float WINDOW_WIDTH = 1600f, WINDOW_HEIGHT = 900f;

    private long lastTime;
    private boolean running = true;


    public Logger logger = new Logger("client", true);


    private TcpPacketInput tcpPacketIn;
    private TcpPacketOutput tcpPacketOut;

    private Window window;
    private UserInput userInput;

    private WorldContainer wc;

    private boolean shouldTerminate = false;

    private boolean gameOver = false;


    private ClientGameTeams teams;

    //must be true as of now
    private boolean displayWindow = true;

    public ClientIngame() {
    }
    public ClientIngame(float relWindowSize) {
        this.relWindowSize = relWindowSize;
    }

    public void init(TcpPacketInput tcpPacketIn, TcpPacketOutput tcpPacketOut, ClientGameTeams teams) {
        logger.printh1("init");

        logger.println("getting tcp in and out");
        this.tcpPacketIn = tcpPacketIn;
        this.tcpPacketOut = tcpPacketOut;

        this.teams = teams;

        logger.println("creating world container");
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
        logger.println("setting should terminate");
        shouldTerminate = true;
    }

    /**
     * blocking while the game runs
     */
    @Override
    public void run() {
        logger.printh1("run");
        String title = "Client    Siiiii";

        logger.println("creating window");
        window = relWindowSize == -1? new Window(title) : new Window(relWindowSize, title);

        logger.println("creating user input");
        userInput = new UserInput(window, GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);

        //make sure window has focus
        window.focus();

        //load stuff
        logger.println("loading fonts");
        Font.loadFonts(FontType.BROADWAY);
        logger.println("initing audio master");
        AudioMaster.init();

        logger.println("assigning component types");
        GameUtils.assignComponentTypes(wc);

        logger.println("creating map entities");
        //create map
        if (teams.getTotalCharacterCount() <= 2) {
            GameUtils.createMap(wc, displayWindow);
        }
        else if (teams.getTotalCharacterCount() <= 4) {
            GameUtils.createLargeMap(wc, displayWindow);
        }
        else {
            throw new IllegalStateException("Dont know what map to use for " + teams.getTotalCharacterCount() + " clients");
        }

        logger.println("creating client characters");
        int[][] charEntIds = CharacterUtils.createClientCharacters(wc, teams, displayWindow);
        logger.println("creating game data entity");
        GameUtils.createGameData(wc, teams, charEntIds);


//        List<Integer> charEntIds = CharacterUtils.createClientCharacters(wc, teams);
//
//        GameUtils.createGameData(wc, teams, charEntIds);

        logger.println("adding client systems");
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
                logger.println("close game user input detected");

                if (!gameOver) {
                    //tell server to disconnect us
                    //we have to disconnect when exiting in the midle of a game
                    logger.println("sending host disconnected");
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
            GameDataComp dataComp = wc.getComponent(entity, GameDataComp.class);

            if (dataComp.endGameRequest) {
                logger.println("end game is requested by GameDataComp");
                logger.println("ClientIngame requesting game over");
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

            if (tcpPacketIn.isRemoteSocketClosed()) {
                 System.err.println("Remote socket is closed");
                 logger.println("remote socket was closed");
            } else {
                logger.println("recieved game exit from server");
            }

            //dont go out of game if we are in end game state
            if (gameOver) return;

            setShouldTerminate();
        }
    }

    private void onTerminate() {
        logger.printh1("on terminate");

        //terminate systems
        logger.println("terminating world container");
        wc.terminate();

        logger.println("closing window");
        window.close();

        logger.close();
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
