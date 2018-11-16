package game;

import engine.*;
import engine.audio.AudioComp;
import engine.audio.AudioSys;
import engine.audio.Sound;
import engine.audio.SoundListenerComp;
import engine.character.*;
import engine.combat.DamageResolutionSys;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.combat.abilities.*;
import engine.graphics.*;


import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.text.TextMesh;
import engine.graphics.text.TextMeshComp;
import engine.graphics.view_.ViewControlComp;
import engine.graphics.view_.ViewControlSys;
import engine.network.client.InterpolationComp;
import engine.network.client.InterpolationSys;

import engine.network.client.*;

import game.server.ServerClientHandler;
import engine.network.server.ServerNetworkSys;
import engine.physics.*;
import engine.visualEffect.VisualEffectComp;
import engine.visualEffect.VisualEffectSys;
import engine.window.Window;
import game.server.ServerGameDataComp;
import utils.maths.M;
import utils.maths.Vec2;
import utils.maths.Vec4;

import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/**
 * Created by eirik on 22.06.2017.
 */
public class GameUtils {


    public static float SMALL_MAP_WIDTH = 1600f,
            SMALL_MAP_HEIGHT = 900f;

    public static float LARGE_MAP_WIDTH = 2034;//3200f;
    public static float LARGE_MAP_HEIGHT = 1087; //1800f;

    public static float LARGE_MAP_WIN_LINES_X[];
    public static Vec2 LARGE_MAP_WIN_LINES_Y;

    public static float VIEW_WIDTH = SMALL_MAP_WIDTH, VIEW_HEIGHT = SMALL_MAP_HEIGHT;

    public static Vec2[][] teamStartPos;





    public static void assignComponentTypes(WorldContainer wc) {

        wc.assignComponentType(TextMeshComp.class);

        wc.assignComponentType(PositionComp.class);
        wc.assignComponentType(ColoredMeshComp.class);
        wc.assignComponentType(TexturedMeshComp.class);
        wc.assignComponentType(CollisionComp.class);
        wc.assignComponentType(NaturalResolutionComp.class);
        wc.assignComponentType(PhysicsComp.class);
        wc.assignComponentType(CharacterComp.class);
        wc.assignComponentType(CharacterInputComp.class);
        wc.assignComponentType(RotationComp.class);
        wc.assignComponentType(MeshCenterComp.class);
        wc.assignComponentType(HoleComp.class);
        wc.assignComponentType(DamageableComp.class);
        wc.assignComponentType(DamagerComp.class);
        wc.assignComponentType(AffectedByHoleComp.class);
        wc.assignComponentType(AbilityComp.class);
        wc.assignComponentType(HitboxComp.class);
        wc.assignComponentType(UserCharacterInputComp.class);
        wc.assignComponentType(ProjectileComp.class);
        wc.assignComponentType(InterpolationComp.class);
        wc.assignComponentType(ViewControlComp.class);
        wc.assignComponentType(ControlledComp.class);
        wc.assignComponentType(VisualEffectComp.class);
        wc.assignComponentType(ViewRenderComp.class);
        wc.assignComponentType(AudioComp.class);
        wc.assignComponentType(SoundListenerComp.class);
        wc.assignComponentType(GameDataComp.class);
        wc.assignComponentType(TeamComp.class);
        wc.assignComponentType(ServerGameDataComp.class);

    }


    public static int createGameData(WorldContainer wc, ClientGameTeams teams, int[][] charEntityIds) {
        //charEntityIds are assumed to be in same order as team character ids

        float[] teamStartX = {10, GameUtils.VIEW_WIDTH-200};
        float startY = GameUtils.VIEW_HEIGHT - 300;
        float spaceY = 50;

        Vec4 dmgTextColor = new Vec4(1, 0, 0, 1);
        float dmgTextSize = 64;

        GameDataComp dataComp = new GameDataComp();


        //Create damage text entities, and store id in dataComp
        for (int i = 0; i < teams.getTeamCount();  i++) {
            float currY = startY;
            int j = 0;
            for (int charId : teams.getCharacterIdsOnTeam(i)) {

                //create text entity
                int t = wc.createEntity("damage text");
                wc.addComponent(t, new PositionComp(teamStartX[i], currY));
                wc.addComponent(t, new ViewRenderComp(new TextMesh("0", Font.getFont(FontType.BROADWAY), dmgTextSize, dmgTextColor)));

                //map character entity id to text entity id
                dataComp.charDamageTextEntities.put(charEntityIds[i][j], t);

                ++j;
                currY += spaceY;
            }
        }

        //create game end text entity and store in data comp
//        int endGameTextEntity = wc.createEntity("game end text");
//        wc.addComponent(endGameTextEntity, new PositionComp(300, 300 ));
//        wc.addComponent(endGameTextEntity, new ViewRenderComp(new TextMesh("", Font.getFont(FontType.BROADWAY), 128, dmgTextColor)));
//        dataComp.gameEndTextEntity = endGameTextEntity;

        //add game end images. index 0 = win, index 1 = lose
        int endGameVictoryEntity = wc.createEntity("game end image");
        wc.addComponent(endGameVictoryEntity, new PositionComp(GameUtils.VIEW_WIDTH/2,GameUtils.VIEW_HEIGHT/2));
        wc.addInactiveComponent(endGameVictoryEntity, new MeshCenterComp(542, 373));
        wc.addInactiveComponent(endGameVictoryEntity, new ViewRenderComp(TexturedMeshUtils.createRectangle("sol_victory.png", 542*2, 373*2)));

        int endGameDefeatEntity = wc.createEntity("game end image");
        wc.addComponent(endGameDefeatEntity, new PositionComp(GameUtils.VIEW_WIDTH/2,GameUtils.VIEW_HEIGHT/2));
        wc.addInactiveComponent(endGameDefeatEntity, new MeshCenterComp(542, 373));
        wc.addInactiveComponent(endGameDefeatEntity, new ViewRenderComp(TexturedMeshUtils.createRectangle("sol_defeat.png", 542*2, 373*2)));



        Sound victoryTheme = new Sound("audio/si.ogg");
        AudioComp victoryThemeAudioComp = new AudioComp(victoryTheme);
        victoryThemeAudioComp.backgroundAudio = true;
        victoryThemeAudioComp.backgroundSound();
        wc.addInactiveComponent(endGameVictoryEntity, victoryThemeAudioComp);
        //must have positionComponent for AudoSys to work.

        Sound defeatTheme = new Sound("audio/soundOfSilence.ogg");
        AudioComp defeatThemeAudioComp = new AudioComp(defeatTheme);
        defeatThemeAudioComp.backgroundAudio = true;
        defeatThemeAudioComp.backgroundSound();
        wc.addInactiveComponent(endGameDefeatEntity, defeatThemeAudioComp);
        //must have positionComponent for AudioSys to work.


        //Adding Audiocomp to gamedataEntity;
        Sound battlefield = new Sound("audio/meleeBattlefield.ogg");
        AudioComp backgroundAudioComp = new AudioComp(battlefield);
        backgroundAudioComp.backgroundAudio = true;
        backgroundAudioComp.backgroundMusic();
        backgroundAudioComp.requestSound = 0;
        int backgroundMusicEntity = wc.createEntity();
        wc.addComponent(backgroundMusicEntity, backgroundAudioComp);
        //must have positionComponent for AudioSys to work.
        wc.addComponent(backgroundMusicEntity, new PositionComp(0,0));


        Sound readyGo = new Sound("audio/readyGo.ogg");
        AudioComp audioComp = new AudioComp(readyGo);
        audioComp.backgroundAudio = true;
        audioComp.backgroundSound();
        audioComp.requestSound = 0;
        int readyGoSoundEntity = wc.createEntity();
        wc.addComponent(readyGoSoundEntity, audioComp);
        wc.addComponent(readyGoSoundEntity, new PositionComp(0,0));


        dataComp.backgroundMusicEntity = backgroundMusicEntity;

        dataComp.gameEndDefeatEntity = endGameDefeatEntity;
        dataComp.gameEndVictoryEntity = endGameVictoryEntity;


        //Create actual game data entity
        int gameDataEntity = wc.createEntity("game data");
        wc.addComponent(gameDataEntity, dataComp);

        return gameDataEntity;
    }

    public static void createMap(WorldContainer wc) {

        Vec2[][] startPositions = {
                { new Vec2(100, SMALL_MAP_HEIGHT/2), new Vec2(100, SMALL_MAP_HEIGHT/2+100) },
                { new Vec2(SMALL_MAP_WIDTH-100, SMALL_MAP_HEIGHT/2), new Vec2(SMALL_MAP_WIDTH-100, SMALL_MAP_HEIGHT/2+100) }
        };
        GameUtils.teamStartPos = startPositions;

        //create background
        createBackground(wc);

        //create walls
        float wallThickness = 64f;
        createWall(wc, wallThickness/2, SMALL_MAP_HEIGHT/2, wallThickness, SMALL_MAP_HEIGHT);
        createWall(wc, SMALL_MAP_WIDTH-wallThickness/2, SMALL_MAP_HEIGHT/2, wallThickness, SMALL_MAP_HEIGHT);

//        createWall(wc, MAP_WIDTH/2, wallThickness/2, MAP_WIDTH-wallThickness*2, wallThickness);
//        createWall(wc, MAP_WIDTH/2, MAP_HEIGHT-wallThickness/2, MAP_WIDTH-wallThickness*2, wallThickness);

        //create holes
        createRectangleHoleInvisible(wc, SMALL_MAP_WIDTH/2, wallThickness/2, SMALL_MAP_WIDTH-wallThickness*2, wallThickness);
        createRectangleHoleInvisible(wc, SMALL_MAP_WIDTH/2, SMALL_MAP_HEIGHT-wallThickness/2, SMALL_MAP_WIDTH-wallThickness*2, wallThickness);
        createCircleHole(wc, SMALL_MAP_WIDTH/2, SMALL_MAP_HEIGHT/2, 48f);

    }



    public static void createLargeMap(WorldContainer wc){
        final float scale = 2f; //1.3f;

        //////// WIN LINES
        float[] winLines = {(LARGE_MAP_WIDTH -356)*scale,  356 *scale};
        LARGE_MAP_WIN_LINES_X = winLines;

        //for both teams
        LARGE_MAP_WIN_LINES_Y = new Vec2(422*scale, (422+253) *scale);

        createLargeBackgroundScale(wc, scale);


        /////////START POSITIONS

        //set start pos rel to map center y
        //defined for base on left, and mirrotred for right
        float spaceSepY = 46;
        float spaceX = 200;

        //left x position scaled
        float sx = spaceX;
        float sy1 = (LARGE_MAP_HEIGHT/2 - spaceSepY);
        float sy2 = (LARGE_MAP_HEIGHT/2 + spaceSepY);

        float sxMirror = (LARGE_MAP_WIDTH - sx);


        Vec2[][] startPositions = {
                //team1
                { new Vec2(sx, sy1), new Vec2(sx, sy2) },
                //team2
                { new Vec2(sxMirror, sy1), new Vec2(sxMirror, sy2) }
        };
      
        //scale start positions
        Arrays.stream(startPositions).forEach( teamPositions -> {
                Arrays.stream(teamPositions).forEach( pos -> {
                    pos.setAs( pos.scale(scale) );
                });
        });
        //set positions for use
        GameUtils.teamStartPos = startPositions;


        ////////// WALLS BASE

        //both sides
        float cx, cy, w, h, r;
        float cxm, cym;
        //back wall
        cx = 28;    cy = LARGE_MAP_HEIGHT/2;    w = 56;     h = 252;
        cxm = LARGE_MAP_WIDTH - cx;     cym = cy;
//        createWallInvisible(wc, cx*scale, cy*scale, w*scale, h*scale);
//        createWallInvisible(wc, cxm*scale, cym*scale, w*scale, h*scale);
        createRectangleHoleInvisible(wc, cx*scale, cy*scale, w*scale, h*scale);
        createRectangleHoleInvisible(wc, cxm*scale, cym*scale, w*scale, h*scale);

        //top
        cx = 125; cy = cy-h/2-w/2 -6; w = 250; h = 64;
        cxm = LARGE_MAP_WIDTH - cx;
        cym = cy;
        createWallInvisible(wc, cx*scale, cy*scale, w*scale, h*scale);
        createWallInvisible(wc, cxm*scale, cym*scale, w*scale, h*scale);

        //bot
        cy = LARGE_MAP_HEIGHT - cy;
        cym = cy;
        createWallInvisible(wc, cx*scale, cy*scale, w*scale, h*scale);
        createWallInvisible(wc, cxm*scale, cym*scale, w*scale, h*scale);


        //////////CENTER CIRCLE WALLS
        cx = 632; //628;
        cxm = LARGE_MAP_WIDTH-cx;
        cy = (LARGE_MAP_HEIGHT/2); //549 * scale;
        r = 125;

        //left side
        createCircleWallInvisible(wc, cx*scale, cy*scale, r*scale);

        //rightSide
        createCircleWallInvisible(wc, cxm*scale, cy*scale, r*scale);



        /////////CORNER CIRCLE HOLES

        cx = 123;
        cy = 25;
        r = 317;
        cxm = LARGE_MAP_WIDTH-cx;
        cym = LARGE_MAP_HEIGHT-cy;

        //top left
        createCircleHoleInvisible(wc, cx*scale, cy*scale, r*scale);
        //bot left
        createCircleHoleInvisible(wc, cx*scale, cym*scale, r*scale);
        //bot right
        createCircleHoleInvisible(wc, cxm*scale, cym*scale, r*scale);
        //top right
        createCircleHoleInvisible(wc, cxm*scale, cy*scale, r*scale);


        ////////RECTANGLE EDGE WALLs HOLES

        cx = 1015;
        cy = 117; //121;
        w = 386;
        h = 64;
        float hh = h-32; //so holes areent on the edge
        cym = LARGE_MAP_HEIGHT-cy;

        //center: top wall, bot hole
        createWallInvisible(wc, cx*scale, cy*scale, w*scale, h*scale);
        createRectangleHoleInvisible(wc, cx*scale, cym*scale, w*scale, hh*scale);

        cx = cx - w;
        cxm = LARGE_MAP_WIDTH-cx;

        //quickfix
        cy -= 5;
        cym = LARGE_MAP_HEIGHT - cy;

        //sides: top hole bot wall
        //left
        createRectangleHoleInvisible(wc, cx*scale, cy*scale, w*scale, hh*scale);
        createWallInvisible(wc, cx*scale, cym*scale, w*scale, h*scale);
        //right
        createRectangleHoleInvisible(wc, cxm*scale, cy*scale, w*scale, hh*scale);
        createWallInvisible(wc, cxm*scale, cym*scale, w*scale, h*scale);


        //////WALL AND HOLES CENTER
        cx = 766;
        cy = 709;
        w = 284;
        h = 40;
        cxm = LARGE_MAP_WIDTH - cx;
        cym = LARGE_MAP_HEIGHT - cy;

        //bot holes
        createRectangleHoleInvisible(wc, cx*scale, cy*scale, w*scale, h*scale);
        createRectangleHoleInvisible(wc, cxm*scale, cy*scale, w*scale, h*scale);

        //top walls
        cx = LARGE_MAP_WIDTH/2;
        cy = 390;
        w = 191;
        h = 64;
        createWallInvisible(wc, cx*scale, cy*scale, w*scale, h*scale);


    }


//    private static int createSandbag(WorldContainer wc) {
//        if (PROGRAM != OFFLINE) throw new UnsupportedOperationException("Sandbag not implemented for nonoffline use");
//
//        float radius = 32f;
//        int sandbag = wc.createEntity();
//        wc.addComponent(sandbag, new PositionComp(500, 300) );
//        wc.addComponent(sandbag, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sandbag.png", radius*2, radius*2)));
//        wc.addComponent(sandbag, new MeshCenterComp(radius, radius));
//
//        wc.addComponent(sandbag, new PhysicsComp(80, 2.5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
//        wc.addComponent(sandbag, new CollisionComp(new Circle(radius)));
//        wc.addComponent(sandbag, new NaturalResolutionComp());
//
//
//        wc.addComponent(sandbag, new DamageableComp());
//        wc.addComponent(sandbag, new AffectedByHoleComp());
//
//        return sandbag;
//    }

    private static int createCircleHole(WorldContainer wc, float x, float y, float radius) {
        int hole = wc.createEntity("circle hole");
        float[] color = {0.0f, 0.0f, 0.0f};

        wc.addComponent(hole, new PositionComp(x, y));

        wc.addComponent(hole, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(radius, 16, color)));
        //wc.addComponent(hole, new MeshCenterComp(radius, radius));


        wc.addComponent(hole, new CollisionComp(new Circle(radius)));
        //wc.addComponent(hole, new PhysicsComp(500f, 10.0f));

        wc.addComponent(hole, new HoleComp());

        return hole;
    }

    private static int createCircleHoleInvisible(WorldContainer wc, float x, float y, float radius) {
        int hole = wc.createEntity("circle hole");
        float[] color = {0.0f, 0.0f, 0.0f};

        wc.addComponent(hole, new PositionComp(x, y));

//        wc.addComponent(hole, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(radius, 16, color)));
        //wc.addComponent(hole, new MeshCenterComp(radius, radius));


        wc.addComponent(hole, new CollisionComp(new Circle(radius)));
        //wc.addComponent(hole, new PhysicsComp(500f, 10.0f));

        wc.addComponent(hole, new HoleComp());

        return hole;
    }

    private static int createRectangleHoleInvisible(WorldContainer wc, float x, float y, float width, float height) {
        int hole = wc.createEntity("rectangle hole");
        float[] color = {0.0f, 0.0f, 0.0f};

        wc.addComponent(hole, new PositionComp(x, y));

//        wc.addComponent(hole, new ColoredMeshComp(ColoredMeshUtils.createRectangle(radius, 16, color)));
//        wc.addComponent(hole, new MeshCenterComp(width/2, height/2));

        wc.addComponent(hole, new CollisionComp(new Rectangle(width, height)));
        //wc.addComponent(hole, new PhysicsComp(500f, 10.0f));

        wc.addComponent(hole, new HoleComp());

        return hole;
    }

    private static int createWallInvisible(WorldContainer wc, float x, float y, float width, float height) {
        int w = wc.createEntity("wall");
        wc.addComponent(w, new PositionComp(x, y));

//        wc.addComponent(w, new ColoredMeshComp(ColoredMeshUtils.createRectangle(width, height)));
//        wc.addComponent(w, new MeshCenterComp(width/2, height/2)); //physical rectangle is defined with position being the center, while the graphical square is defined in the upper left corner


        wc.addComponent(w, new PhysicsComp(0, 1, 1));
        wc.addComponent(w, new CollisionComp(new Rectangle(width, height)));
        wc.addComponent(w, new NaturalResolutionComp());

        return w;
    }

    private static int createBackground(WorldContainer wc) {
        int bg = wc.createEntity("background");
        wc.addComponent(bg, new PositionComp(0, 0, -0.5f));
        wc.addComponent(bg, new TexturedMeshComp(TexturedMeshUtils.createRectangle("background_difuse.png", 1600, 900)));

        return bg;
    }


    private static int createLargeBackgroundScale(WorldContainer wc, float scale) {
        int bg = wc.createEntity("map");
//        wc.addComponent(bg, new PositionComp(scale*18, scale*-55, -0.5f));
        wc.addComponent(bg, new PositionComp(0, 0, -0.5f));
        wc.addComponent(bg, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sol_large_map.png", scale*LARGE_MAP_WIDTH, scale*LARGE_MAP_HEIGHT)));

        return bg;
    }



//    private static int createBackgroundScale(WorldContainer wc, float scale) {
//        int bg = wc.createEntity();
//        wc.addComponent(bg, new PositionComp(18*scale, -55*scale, -0.5f));
//        wc.addComponent(bg, new TexturedMeshComp(TexturedMeshUtils.createRectangle("sol_large_map.png", scale*LARGE_MAP_WIDTH
//                , scale*LARGE_MAP_HEIGHT)));
//
//        return bg;
//    }



    private static int createWall(WorldContainer wc, float x, float y, float width, float height) {
        int w = wc.createEntity("wall");
        wc.addComponent(w, new PositionComp(x, y));

        wc.addComponent(w, new ColoredMeshComp(ColoredMeshUtils.createRectangle(width, height)));
        wc.addComponent(w, new MeshCenterComp(width/2, height/2)); //physical rectangle is defined with position being the center, while the graphical square is defined in the upper left corner


        wc.addComponent(w, new PhysicsComp(0, 1, 1));
        wc.addComponent(w, new CollisionComp(new Rectangle(width, height)));
        wc.addComponent(w, new NaturalResolutionComp());

        return w;
    }

    //    private static void addClientCharacter(WorldContainer wc, int characterEntity, boolean controlled) {
//        wc.addComponent(characterEntity, new InterpolationComp());
//
//        if (controlled) {
//            wc.addComponent(characterEntity, new UserCharacterInputComp());
//            wc.addComponent(characterEntity, new ViewControlComp(VIEW_WIDTH, VIEW_HEIGHT,  -VIEW_WIDTH/2f, -VIEW_HEIGHT/2f) );
//        }
//    }
//    private static void addServerCharacter(WorldContainer wc, int characterEntity, float radius) {
//        wc.addComponent(characterEntity, new ViewControlComp(VIEW_WIDTH, VIEW_HEIGHT,  -VIEW_WIDTH/2f, -VIEW_HEIGHT/2f) );
//
//        wc.addComponent(characterEntity, new CharacterInputComp());
//
//        wc.addComponent(characterEntity, new PhysicsComp(80, 5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
//        wc.addComponent(characterEntity, new CollisionComp(new Circle(radius)));
//        wc.addComponent(characterEntity, new NaturalResolutionComp());
//
//        wc.addComponent(characterEntity, new AffectedByHoleComp());
//
//        wc.addComponent(characterEntity, new DamageableComp());
//    }
//
//    private static void addOfflineCharacter(WorldContainer wc, int characterEntity, float radius) {
//        wc.addComponent(characterEntity, new CharacterInputComp());
//
//        wc.addComponent(characterEntity, new PhysicsComp(80, 5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
//        wc.addComponent(characterEntity, new CollisionComp(new Circle(radius)));
//        wc.addComponent(characterEntity, new NaturalResolutionComp());
//
//        wc.addComponent(characterEntity, new AffectedByHoleComp());
//
//        wc.addComponent(characterEntity, new DamageableComp());
//
//        wc.addComponent(characterEntity, new UserCharacterInputComp());
//
//    }

    private static int createCircleWall(WorldContainer wc, float x, float y, float radius) {
        int w = wc.createEntity();
        wc.addComponent(w, new PositionComp(x, y));

//        wc.addComponent(w, new ColoredMeshComp(ColoredMeshUtils.createCircleMulticolor(radius, 32)));

        wc.addComponent(w, new PhysicsComp(0, 1, 1));
        wc.addComponent(w, new CollisionComp(new Circle(radius)));
        wc.addComponent(w, new NaturalResolutionComp());



        return w;
    }

    private static int createCircleWallInvisible(WorldContainer wc, float x, float y, float radius) {
        int w = wc.createEntity();
        wc.addComponent(w, new PositionComp(x, y));

//        wc.addComponent(w, new ColoredMeshComp(ColoredMeshUtils.createCircleMulticolor(radius, 32)));

        wc.addComponent(w, new PhysicsComp(0, 1, 1));
        wc.addComponent(w, new CollisionComp(new Circle(radius)));
        wc.addComponent(w, new NaturalResolutionComp());



        return w;
    }

    private static int createRectangleHole(WorldContainer wc, float x, float y, float width, float height) {
        int hole = wc.createEntity();
        float[] color = {0.0f, 0.0f, 0.0f};

        wc.addComponent(hole, new PositionComp(x, y));

        wc.addComponent(hole, new ColoredMeshComp(ColoredMeshUtils.createRectangleSingleColor(width, height, color)));
        wc.addComponent(hole, new MeshCenterComp(width/2, height/2)); //physical rectangle is defined with position being the center, while the graphical square is defined in the upper left corner

        wc.addComponent(hole, new CollisionComp(new Rectangle(width, height)));
            //wc.addComponent(hole, new PhysicsComp(500f, 10.0f));

        wc.addComponent(hole, new HoleComp());


        return hole;
    }

}