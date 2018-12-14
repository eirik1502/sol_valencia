package game.server;

import com.google.gson.Gson;
import engine.PositionComp;
import engine.TeamComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.audio.AudioMaster;
import engine.character.CharacterComp;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.view_.View;
import engine.utils.tickers.LinearTicker;
import engine.utils.tickers.Ticker;
import engine.window.Window;
import game.CharacterUtils;
import game.GameUtils;
import game.SysUtils;
import game.loaders.solGameInstance.SolGameInstanceConfig;
import utils.loggers.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Created by eirik on 13.12.2018.
 */
public class ServerGameInstance {

    private static final float FRAME_INTERVAL = 1.0f/60.0f;


    public Logger logger = new Logger("server_ingame");

    private Window window;
    private UserInput userInput;

    private WorldContainer wc;


    private int[] stockLossCount;


    //quikfix. This gets read from serverNetworkSys
    public int gameDataEntity;

    private boolean displayWindow;

    private Ticker updateTicker;
    private String instanceConfigJson;
    private Thread thread;




    public ServerGameInstance(String instanceConfigJson) {
        this.instanceConfigJson = instanceConfigJson;
    }

    /**
     * This will load the game and start it in its own thread
     */
    public void start() {
        this.thread = new Thread(this::onStart);
        this.thread.start();
    }

    private void onStart() {
        //listen for client connections
        init(); //load stuff
        startGame();
    }

    private SolGameInstanceConfig loadGameInstanceConfigs() {
        Gson gsonLoader = new Gson();
        SolGameInstanceConfig instanceConfig = gsonLoader.fromJson(instanceConfigJson, SolGameInstanceConfig.class);
        return instanceConfig;
    }

    public void init() {
        logger.printh1("init");
        //make utils log to our logger
        GameUtils.logger = logger;

        if (displayWindow) {
            logger.println("create window");
            this.window = new Window(0.3f, "Server ingame");
            logger.println("create user input");
            this.userInput = new UserInput(window, 1, 1);

            //load other stuff
            logger.println("load fonts");
            Font.loadFonts(FontType.BROADWAY);
            logger.println("init audio master");
            AudioMaster.init();
        }

        SolGameInstanceConfig instanceConfig = loadGameInstanceConfigs();

        logger.println("create world container");
        wc = new WorldContainer(new View(GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT) );

        GameUtils.assignComponentTypes(wc);

        String map = instanceConfig.map;
        logger.println("creating map: " + map );

        if (map.equals("small")) {
            GameUtils.createMap(wc, displayWindow);
        } else if (map.equals("large")) {
            GameUtils.createLargeMap(wc, displayWindow);
        } else {
            logger.printError("map given in config is illegal: " + map);
        }
        //List<ServerClientHandler> = instanceConfig.clients.stream().map(c -> c.address);
        SysUtils.addServerSystems(wc, window, null);

        logger.println("create server characters");
        CharacterUtils.createServerCharacters(wc, null, displayWindow);

        //add a gameData entity
        gameDataEntity = wc.createEntity("game data");
        wc.addComponent(gameDataEntity, new ServerGameDataComp());




        updateTicker = new LinearTicker(FRAME_INTERVAL, this::update);
    }


    public void startGame() {
        logger.printh1("start");


        logger.println(wc.entitiesToString());
        System.out.println(wc.entitiesToString());

        updateTicker.stop(); //blocking

        onTerminate();
    }


    public void update(float deltaTime) {

        if (displayWindow && window != null) {
            if (window.shouldClosed() || userInput.isKeyboardPressed(UserInput.KEY_ESCAPE)) {
                //serverGame.setShouldTerminate();
            }

            window.pollEvents();
        }

        wc.updateSystems();

        handleWinCondition();

        //check if clients have disconnected
        //if so, ask to terminate game
//        Arrays.stream(teams.getAllClients()).forEach( client -> {
//            if (client.getTcpPacketIn().isRemoteSocketClosed()) {
//                logger.println("a client disconnected");
//                logger.println("requesting server termination");
//                serverGame.setShouldTerminate();
//            }
//        });

    }

    private void handleWinCondition() {
//        ServerGameDataComp dataComp = (ServerGameDataComp) wc.getComponent(gameDataEntity, ServerGameDataComp.class);
//        //if win condition was obtained last frame, exit
//        if (dataComp.teamWon != -1) {
//            //gameOver will shut down serverIngame
//            gameOver(dataComp.teamWon);
//        }
//
//        //check win condition
//        if (smallMap) {
//
//            wc.entitiesOfComponentTypeStream(CharacterComp.class).forEach(entity -> {
//                CharacterComp charComp = (CharacterComp) wc.getComponent(entity, CharacterComp.class);
//
//                if (charComp.getRespawnCount() >= 3) {
//                    TeamComp teamComp = (TeamComp) wc.getComponent(entity, TeamComp.class);
//
//                    //set team won to the team not containing the loser. assuming two teams
//                    dataComp.teamWon = 1 - teamComp.team;
//                }
//            });
//
//        }
//        else {
//            //check characters push win condition
//            int teamCount = 2;
//            int[] charsOnTeam = new int[teamCount];
//            int[] charsOverWinLine = new int[teamCount];
//            wc.entitiesOfComponentTypeStream(CharacterComp.class).forEach(entity -> {
//                PositionComp posComp = (PositionComp) wc.getComponent(entity, PositionComp.class);
//                TeamComp teamComp = (TeamComp) wc.getComponent(entity, TeamComp.class);
//
//                ++ charsOnTeam[teamComp.team];
//
//                boolean xInside = false, yInside = false;
//
//                //test y
//                if (posComp.getY() > GameUtils.LARGE_MAP_WIN_LINES_Y.x &&
//                        posComp.getY() < GameUtils.LARGE_MAP_WIN_LINES_Y.y) {
//                    yInside = true;
//
//                    //test x
//                    //if on team 0
//                    if (teamComp.team == 0) {
//                        if (posComp.getX() > GameUtils.LARGE_MAP_WIN_LINES_X[0]) {
//                            xInside = true;
//                        }
//                    }
//                    //if on team 1
//                    else {
//                        if (posComp.getX() < GameUtils.LARGE_MAP_WIN_LINES_X[1]) {
//                            xInside = true;
//                        }
//                    }
//                }
//                if (yInside && xInside) {
//                    ++ charsOverWinLine[teamComp.team];
//                }
//            });
//
//            //check if a team won
//            for (int i = 0; i < teamCount; i++) {
//                if (charsOnTeam[i] == charsOverWinLine[i]) {
//                    dataComp.teamWon = i;
//                    break;
//                }
//            }
//        }
    }

    private void gameOver(int winner) {
        System.out.println("Player "+ winner + " won!");
        //serverGame.setShouldTerminate();
    }

    private void onTerminate() {
        wc.terminate();

        if (window != null)
            window.close();
    }


//    public ServerGameTeams getTeams() {
//        return teams;
//    }
//
//    public void terminate() {
//        running = false;
//    }


}

