package game.offline;


import engine.*;

import engine.audio.AudioComp;
import engine.audio.AudioMaster;
import engine.audio.Sound;
import engine.character.CharacterComp;
import engine.graphics.*;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.view_.View;
import engine.window.Window;
import game.CharacterUtils;
import game.ClientGameTeams;
import game.GameUtils;
import game.SysUtils;

import java.util.List;

/**
 * Created by eirik on 13.06.2017.
 */
public class Game {


    private static final float FRAME_INTERVAL = 1.0f/60.0f;

    public static final float WINDOW_WIDTH = 1600f, WINDOW_HEIGHT = 900f;


    private Window window;
    private UserInput userInput;

    private ColoredMesh vao;

    private AudioComp backgroundAudioComp;


    private long lastTime;

    private WorldContainer wc;


    public void init() {

        window = new Window(0.8f, 0.8f,"SIIII");
        userInput = new UserInput(window, GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);

        Font.loadFonts(FontType.BROADWAY);
        AudioMaster.init();

        wc = new WorldContainer( new View(GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT) );


        GameUtils.assignComponentTypes(wc);

        GameUtils.createLargeMap(wc);

        int[][] characterIds = {
                {CharacterUtils.BRAIL, CharacterUtils.SHRANK},
                {CharacterUtils.MAGNET, CharacterUtils.SCHMATHIAS}
        };

//        int[][] characterIds = {
//                { CharacterUtils.MAGNET},
//                { CharacterUtils.SCHMATHIAS}
//        };

        ClientGameTeams teams = new ClientGameTeams(characterIds, 0, 1);

        int[][] charEntities = CharacterUtils.createOfflineCharacters(wc, teams);

        GameUtils.createGameData(wc, teams, charEntities);


        SysUtils.addOfflineSystems(wc, window, userInput);


        System.out.println("HEELLLLLOOOOO");
        System.out.println(wc);
    }


    /**
     * blocking while the game runs
     */
    public void start() {

//        Sound battlefield = new Sound("audio/meleeBattlefield.ogg");
//        backgroundAudioComp = new AudioComp(battlefield, 1, 500, 600);
//        backgroundAudioComp.backgroundMusic();
//        backgroundAudioComp.playSound(0);

        Sound readyGo = new Sound("audio/readyGo.ogg");
        AudioComp audioComp = new AudioComp(readyGo, 1,600,600);
        audioComp.backgroundSound();
        audioComp.playSound(0);


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

        onTerminate();

    }

    private void onTerminate() {
        wc.terminate();

        window.close();
    }


    public void update() {
        window.pollEvents();

        wc.updateSystems();

//        //print if win condition for one character
//        int teamCount = 2;
//        int[] charsOnTeam = new int[teamCount];
//        int[] charsOverWinLine = new int[teamCount];
//        wc.entitiesOfComponentTypeStream(CharacterComp.class).forEach(entity -> {
//            PositionComp posComp = (PositionComp) wc.getComponent(entity, PositionComp.class);
//            TeamComp teamComp = (TeamComp) wc.getComponent(entity, TeamComp.class);
//
//            ++ charsOnTeam[teamComp.team];
//
//            boolean xInside = false, yInside = false;
//
//            //test y
//            if (posComp.getY() > GameUtils.LARGE_MAP_WIN_LINES_Y.x &&
//                    posComp.getY() < GameUtils.LARGE_MAP_WIN_LINES_Y.y) {
//
//                yInside = true;
//                //test x
//                //if on team 0
//                if (teamComp.team == 0) {
//                    if (posComp.getX() > GameUtils.LARGE_MAP_WIN_LINES_X[0]) {
//                        xInside = true;
//                    }
//                }
//                //if on team 1
//                else {
//                    if (posComp.getX() < GameUtils.LARGE_MAP_WIN_LINES_X[1]) {
//                        xInside = true;
//                    }
//                }
//            }
//            if (yInside && xInside) {
//                ++ charsOverWinLine[teamComp.team];
//            }
//        });
//
//        //check if a team won
//        for (int i = 0; i < teamCount; i++) {
//            if (charsOverWinLine[i] != 0) {//scharsOnTeam[i] == charsOverWinLine[i]) {
//                System.out.println("Winning!!!!");
//                break;
//            }
//        }
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
