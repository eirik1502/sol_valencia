package game.server;

import engine.PositionComp;
import engine.TeamComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.audio.AudioMaster;
import engine.character.*;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.view_.View;
import engine.network.networkPackets.GameOverData;
import engine.physics.*;
import engine.window.Window;
import game.CharacterUtils;
import game.GameUtils;
import game.SysUtils;

import java.util.*;


/**
 * Created by eirik on 13.06.2017.
 */
public class ServerIngame {


    private static final float FRAME_INTERVAL = 1.0f/60.0f;


    public static final float WINDOW_WIDTH = GameUtils.SMALL_MAP_WIDTH /4,
                                WINDOW_HEIGHT = GameUtils.SMALL_MAP_WIDTH /4;


//    private ServerCharacterSelection charactersSelected;

    private ServerGame serverGame;

    private Window window;
    private UserInput userInput;

    private WorldContainer wc;

    private boolean running = true;
    private long lastTime;

    private boolean smallMap;

    private ServerGameTeams teams;
//    private List< List<ServerClientHandler> > teamClients;
//    private HashMap<ServerClientHandler, Integer> clientCharacters;

    private int[] stockLossCount;


    //quikfix. This gets read from serverNetworkSys
    public int gameDataEntity;


    public ServerIngame() {

    }


    public void init( ServerGame serverGame, ServerGameTeams teams) {
        this.serverGame = serverGame;
        this.teams = teams;

        //are we on small map?
        smallMap = teams.getTotalClientCount() <= 2;


        //add an stockLoss entry for every client
        stockLossCount = new int[teams.getTotalClientCount()];
    }


    public void start() {

        this.window = new Window(0.3f, "Server ingame");
        this.userInput = new UserInput(window, 1, 1);

        wc = new WorldContainer(new View(GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT) );

        //load other stuff
        Font.loadFonts(FontType.BROADWAY);
        AudioMaster.init();


        GameUtils.assignComponentTypes(wc);
        SysUtils.addServerSystems(wc, window, Arrays.asList( teams.getAllClients() ) );




        //create entities
        //create map
        if (smallMap) {
            GameUtils.createMap(wc);
        }
        else {
            GameUtils.createLargeMap(wc);
        }


        CharacterUtils.createServerCharacters(wc, teams);


        //add a gameData entity
        gameDataEntity = wc.createEntity("game data");
        wc.addComponent(gameDataEntity, new ServerGameDataComp());

        System.out.println("Server game initiated with clients: "+ Arrays.toString(teams.getAllClients()) );



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
                serverGame.setShouldTerminate();
            }
        }

        onTerminate();
    }


    public void update() {

        window.pollEvents();

        wc.updateSystems();

        handleWinCondition();

        //check if clients have disconnected
        //if so, ask to terminate game
        Arrays.stream(teams.getAllClients()).forEach( client -> {
            if (client.getTcpPacketIn().isRemoteSocketClosed()) {
                serverGame.setShouldTerminate();
            }
        });
    }

    private void handleWinCondition() {
        ServerGameDataComp dataComp = (ServerGameDataComp) wc.getComponent(gameDataEntity, ServerGameDataComp.class);
        //if win condition was obtained last frame, exit
        if (dataComp.teamWon != -1) {
            //gameOver will shut down serverIngame
            gameOver(dataComp.teamWon);
        }

        //check win condition
        if (smallMap) {

            wc.entitiesOfComponentTypeStream(CharacterComp.class).forEach(entity -> {
                CharacterComp charComp = (CharacterComp) wc.getComponent(entity, CharacterComp.class);

                if (charComp.getRespawnCount() >= 3) {
                    TeamComp teamComp = (TeamComp) wc.getComponent(entity, TeamComp.class);

                    //set team won to the team not containing the loser. assuming two teams
                    dataComp.teamWon = 1 - teamComp.team;
                }
            });

        }
        else {
            //check characters push win condition
            int teamCount = 2;
            int[] charsOnTeam = new int[teamCount];
            int[] charsOverWinLine = new int[teamCount];
            wc.entitiesOfComponentTypeStream(CharacterComp.class).forEach(entity -> {
                PositionComp posComp = (PositionComp) wc.getComponent(entity, PositionComp.class);
                TeamComp teamComp = (TeamComp) wc.getComponent(entity, TeamComp.class);

                ++ charsOnTeam[teamComp.team];

                boolean xInside = false, yInside = false;

                //test y
                if (posComp.getY() > GameUtils.LARGE_MAP_WIN_LINES_Y.x &&
                        posComp.getY() < GameUtils.LARGE_MAP_WIN_LINES_Y.y) {
                    yInside = true;

                    //test x
                    //if on team 0
                    if (teamComp.team == 0) {
                        if (posComp.getX() > GameUtils.LARGE_MAP_WIN_LINES_X[0]) {
                            xInside = true;
                        }
                    }
                    //if on team 1
                    else {
                        if (posComp.getX() < GameUtils.LARGE_MAP_WIN_LINES_X[1]) {
                            xInside = true;
                        }
                    }
                }
                if (yInside && xInside) {
                    ++ charsOverWinLine[teamComp.team];
                }
            });

            //check if a team won
            for (int i = 0; i < teamCount; i++) {
                if (charsOnTeam[i] == charsOverWinLine[i]) {
                    dataComp.teamWon = i;
                    break;
                }
            }
        }
    }

    private void gameOver(int winner) {
        System.out.println("Player "+ winner + " won!");
        serverGame.setShouldTerminate();
    }

    private void onTerminate() {
        wc.terminate();

        window.close();
    }


    public ServerGameTeams getTeams() {
        return teams;
    }

    public void terminate() {
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

