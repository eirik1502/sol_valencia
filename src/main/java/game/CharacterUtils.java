package game;

import engine.*;
import engine.audio.AudioComp;
import engine.audio.Sound;
import engine.audio.SoundListenerComp;
import engine.character.CharacterComp;
import engine.character.CharacterInputComp;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.combat.abilities.*;
import engine.graphics.*;
import engine.graphics.view_.ViewControlComp;
import engine.network.client.InterpolationComp;
import engine.physics.*;
import engine.visualEffect.VisualEffectComp;
import engine.visualEffect.VisualEffectUtils;
import game.server.ServerGameTeams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eirik on 05.07.2017.
 */
public class CharacterUtils {

    private static float hitboxDepth = -0.1f;

    private static int characterCount;

    public static final int CHARACTER_COUNT = 4;
    public static final int SHRANK = 0, SCHMATHIAS = 1, BRAIL = 2, MAGNET = 3;
    public static final String[] CHARACTER_NAMES = {"Frank", "KingSkurkTwo", "Brail", "MagneT"};
    public static final float[] CHARACTER_RADIUS = {
            32, 32, 44, 36
    };

    //teamId - charId
    private static final LoadImageData[][] loadCharData= {
            //Shrank
            {new LoadImageData("sol_frank_red.png", CHARACTER_RADIUS[0], 160 / 2f, 512, 256, 180, 130),
                    new LoadImageData("sol_frank_blue.png", CHARACTER_RADIUS[0], 160 / 2f, 512, 256, 180, 130)
            },
            /*Schmathias*/
            {new LoadImageData("schmathias_red.png", CHARACTER_RADIUS[1], 146, 1258, 536, 386, 258),
                    new LoadImageData("schmathias_blue.png", CHARACTER_RADIUS[1], 146, 1258, 536, 386, 258)
            },
            /*Brail*/
            {new LoadImageData("brail_red.png", CHARACTER_RADIUS[2], 272, 1600, 1200, 795, 585),
                    new LoadImageData("brail_blue.png", CHARACTER_RADIUS[2], 272, 1600, 1200, 795, 585)
            },
            //Magnet
            {new LoadImageData("magnet.png", CHARACTER_RADIUS[3], 296/2, 1200, 600, 365, 257),
                    new LoadImageData("masai_blue.png", CHARACTER_RADIUS[3], 296/2, 1200, 600, 365, 257)
            }
    };

    public static void addCharacterGraphicsComps(WorldContainer wc, int teamId, int charId, int entity) {
        LoadImageData data = loadCharData[charId][teamId];

//        System.out.println("charId="+charId+" teamId="+teamId+" entity="+entity+ " Filename="+data.filename);

        TexturedMeshComp texmeshComp = new TexturedMeshComp( TexturedMeshUtils.createRectangle(data.filename, data.width, data.height) );
        MeshCenterComp meshcentComp = new MeshCenterComp(data.offsetX, data.offsetY);

        wc.addComponent(entity, texmeshComp);
        wc.addComponent(entity, meshcentComp);
    }



    public static int[][] createOfflineCharacters(WorldContainer wc, ClientGameTeams teams) {

        return createClientCharacters(wc, teams);
    }

    public static int[][] createClientCharacters(WorldContainer wc, ClientGameTeams teams) {
        int[][] charEntIds = new int[teams.getTeamCount()][];

        for (int j = 0; j < teams.getTeamCount(); j++) {
            charEntIds[j] = new int[teams.getCharacterIdsOnTeam(j).length];

            int i = 0;
            for (int charEnt : teams.getCharacterIdsOnTeam(j)) {
                boolean controlled = false;
                if (teams.getControlCharacterTeam() == j && i == teams.getControlCharacterIndex()) {
                    controlled = true;
                }

                int e = createCharacterById(charEnt, wc, controlled, j, i, GameUtils.teamStartPos[j][i].x, GameUtils.teamStartPos[j][i].y);

                charEntIds[j][i] = e;

                i++;
            }
        }

        return charEntIds;
    }

    public static int[][] createServerCharacters(WorldContainer wc, ServerGameTeams teams) {
        boolean controlled = true;

        int[][] charEntIds = new int[teams.getTeamCount()][];

        for (int j = 0; j < teams.getTeamCount(); j++) {
            charEntIds[j] = new int[teams.getCharacterIdsOnTeam(j).length];

            int i = 0;
            for (int charEnt : teams.getCharacterIdsOnTeam( j )) {

                int e = createCharacterById(charEnt, wc, controlled, j, i, GameUtils.teamStartPos[j][i].x, GameUtils.teamStartPos[j][i].y);
                charEntIds[j][i] = e;

                i++;
            }
        }

        return charEntIds;
    }


    private static int createCharacterById(int charId, WorldContainer wc, boolean controlled, int team, int idOnTeam, float x, float y) {
        int charEnt;

        switch(charId) {
            case SHRANK: charEnt = createShrank(wc, charId, controlled, team, idOnTeam, x, y);
                break;
            case SCHMATHIAS: charEnt = createSchmathias(wc, charId, controlled, team, idOnTeam, x, y);
                break;
            case BRAIL: charEnt = createBrail(wc, charId, controlled, team, idOnTeam, x, y);
                break;
            case MAGNET: charEnt = createMagnet(wc, charId, controlled, team, idOnTeam, x, y);
                break;
            default:
                throw new IllegalArgumentException("no character of id given");
        }

        return charEnt;
    }

    private static int createShrank(
            WorldContainer wc, int charId,
            boolean controlled, int team, int idOnTeam,
            float x, float y) {

        Sound sndPowershot = new Sound("audio/powershot.ogg");
        Sound sndBoom = new Sound ("audio/boom-bang.ogg");
        Sound sndRapidsShot = new Sound("audio/click4.ogg");
        Sound sndHit = new Sound("audio/laser_hit.ogg");

        float[] color1 = {1, 1, 0};
        float[] color2 = {1, 0, 1};
        int proj1Entity = ProjectileUtils.allocateSinglecolorProjectileAbility(wc, 8, color1, sndBoom);
        int proj2Entity = ProjectileUtils.allocateSinglecolorProjectileAbility(wc, 20, color2, sndHit);

        int rapidShotSoundIndex = 0;
        int powershotSoundIndex = 1;
        int boomSoundIndex = 2;

        //rapidshot
        ProjectileAbility abRapidshot = new ProjectileAbility(wc, rapidShotSoundIndex, proj1Entity, 2, 2, 30, 1200, 30 );
        abRapidshot.setDamagerValues(wc, 100, 180, 0.5f, -128, false);

        //hyperbeam3
        ProjectileAbility abHyperbeam = new ProjectileAbility(wc, powershotSoundIndex, proj2Entity, 15, 10, 120, 1500, 120);
        abHyperbeam.setDamagerValues( wc, 350,900, 1.1f, -256, false);

        //puffer
        MeleeAbility abPuffer = new MeleeAbility(wc, boomSoundIndex, 8, 2, 8, 60*3, new Circle(128f), 0f, sndBoom);
        abPuffer.setDamagerValues(wc, 20, 900f, 0.1f, 0f, false);


       List<Sound> soundList = new ArrayList<Sound>();
       soundList.add(rapidShotSoundIndex, sndRapidsShot);
       soundList.add(powershotSoundIndex, sndPowershot);
       soundList.add(boomSoundIndex, sndBoom);


        return createCharacter(wc, charId,
                controlled, team, idOnTeam,
                x, y, 1800f,
                abRapidshot, abHyperbeam, abPuffer,
                soundList);
    }

    private static int createSchmathias(
            WorldContainer wc, int charId,
            boolean controlled, int team, int idOnTeam,
            float x, float y) {

        //frogpunch
        int frogPunchSoundIndex = 0;
        int hookInitSoundIndex = 1;
        int meteorPunchSoundIndex = 2;



        MeleeAbility abFrogpunch = new MeleeAbility(wc, frogPunchSoundIndex, 3, 5, 3, 20, new Circle(64f),48.0f, null);
        abFrogpunch.setDamagerValues(wc, 150, 700, 0.8f, -48f, false);

        //hook
        int hookProjEntity = ProjectileUtils.allocateImageProjectileEntity(wc, "hook.png", 256/2, 512, 256, 24, new Sound("audio/hook_hit.ogg")); //both knockback angle and image angle depends on rotation comp. Cheat by setting rediusOnImage negative
        ProjectileAbility abHook = new ProjectileAbility(wc, hookInitSoundIndex, hookProjEntity, 5, 18, 50, 900, 30);
        abHook.setDamagerValues(wc, 200f, 1400f, 0.2f, -128, true);

        //meteorpunch
        MeleeAbility abMeteorpunch = new MeleeAbility(wc, meteorPunchSoundIndex, 15, 3, 4, 60, new Circle(32), 64, null);
        abMeteorpunch.setDamagerValues(wc, 500, 1000, 1.5f, -128f, false);

        List<Sound> soundList = new ArrayList<>();
        soundList.add(frogPunchSoundIndex, new Sound("audio/boom-kick.ogg") );
        soundList.add(hookInitSoundIndex, new Sound("audio/hook_init.ogg"));
        soundList.add(meteorPunchSoundIndex, new Sound("audio/boom-kick.ogg"));


        return createCharacter(wc, charId,
                controlled, team, idOnTeam,
                x, y, 2000f,
                abFrogpunch, abHook, abMeteorpunch, soundList);
    }

    private static int createBrail(
            WorldContainer wc, int charId,
            boolean controlled, int team, int idOnTeam,
            float x, float y) {

        Sound snd1 = new Sound("audio/click4.ogg");
        Sound snd2 = new Sound("audio/laser02.ogg");
        Sound snd3 = new Sound("audio/boom-bang.ogg");

        int ab1CharSnd = 0;
        int ab2CharSnd = 1;
        int ab3CharSnd = 2;

        float[] purple = {1.0f, 0f, 1.0f};

        //lightForce
        MeleeAbility ab1 = new MeleeAbility(wc, ab1CharSnd, 6, 6, 6, 30, new Circle(70f),64.0f, null);
        ab1.setDamagerValues(wc, 150, 600, 1.2f, 400f, true);

        //chagger
        int chaggProjectile = ProjectileUtils.allocateSinglecolorProjectileAbility(wc, 64f, purple,null); //both knockback angle and image angle depends on rotation comp. Cheat by setting rediusOnImage negative
        ProjectileAbility ab2 = new ProjectileAbility(wc, ab2CharSnd, chaggProjectile, 10, 6, 120, 650, 40);
        ab2.setDamagerValues(wc, 300, 400, 0.8f, 64, false);

        //merge
        MeleeAbility ab3 = new MeleeAbility(wc, ab3CharSnd, 10, 2, 8, 60, new Circle(160), 128, null);
        ab3.setDamagerValues(wc, 20, 800, 0.4f, 0, true);

        List<Sound> sounds = new ArrayList<>();
        sounds.add( snd1 );
        sounds.add( snd2 );
        sounds.add( snd3 );

        return createCharacter(wc, charId,
                controlled, team, idOnTeam,
                x, y, 2000,
                ab1, ab2, ab3, sounds);
    }

    private static int createMagnet (
            WorldContainer wc, int charId,
            boolean controlled, int team, int idOnTeam,
            float x, float y) {

        Sound snd1 = new Sound("audio/click4.ogg");
        Sound snd2 = new Sound("audio/masai_arrow_throw.ogg");
        Sound snd3 = new Sound("audio/lion-roar.ogg");

        int ab1CharSnd = 0;
        int ab2CharSnd = 1;
        int ab3CharSnd = 2;

        float[] purple = {1.0f, 0f, 1.0f};

        //spear poke
        MeleeAbility ab1 = new MeleeAbility(wc, ab1CharSnd, 6, 6, 6, 13, new Circle(20f),128.0f, null);
        ab1.setDamagerValues(wc, 150, 600, 0.7f, -100f, false);

        //spear
        int spearProj = ProjectileUtils.allocateImageProjectileEntity(wc, "magnet_spear.png", 48, 536, 32*2, 32, new Sound("audio/arrow_impact.ogg")); //both knockback angle and image angle depends on rotation comp. Cheat by setting rediusOnImage negative
        ProjectileAbility ab2 = new ProjectileAbility(wc, ab2CharSnd, spearProj, 30, 18, 50, 1500, 90);
        ab2.setDamagerValues(wc, 400f, 800f, 2f, -32f, false);

        //lion
        int lionProj = ProjectileUtils.allocateImageProjectileEntity(wc, "masai_lion.png", 210/2, 435, 457, 64, new Sound("audio/hook_hit.ogg")); //both knockback angle and image angle depends on rotation comp. Cheat by setting rediusOnImage negative
        ProjectileAbility ab3 = new ProjectileAbility(wc, ab3CharSnd, lionProj, 12, 12, 50, 400, 40);
        ab3.setDamagerValues(wc, 600f, 800f, 0.5f, 0, false);

        List<Sound> sounds = new ArrayList<>();
        sounds.add( snd1 );
        sounds.add( snd2 );
        sounds.add( snd3 );

        return createCharacter(wc, charId,
                controlled, team, idOnTeam,
                x, y, 2000,
                ab1, ab2, ab3, sounds);
    }

//    private static int createShitface(
//            WorldContainer wc, int charId,
//            boolean controlled, int team, int idOnTeam, float x, float y) {
//
//        //frogpunch
//        MeleeAbility abFrogpunch = new MeleeAbility(wc, -1, 3, 5, 3, 20, new Circle(64f),48.0f, null);
//        abFrogpunch.setDamagerValues(wc, 15, 70, 0.8f, -48f, false);
//
//        //hook
//        int hookProjEntity = ProjectileUtils.allocateImageProjectileEntity(wc, "hook.png", 256/2, 512, 256, 24, null); //both knockback angle and image angle depends on rotation comp. Cheat by setting rediusOnImage negative
//        ProjectileAbility abHook = new ProjectileAbility(wc, -1, hookProjEntity, 5, 18, 50, 900, 30);
//        abHook.setDamagerValues(wc, 20f, 140f, 0.2f, -128, true);
//
//        //meteorpunch
//        MeleeAbility abMeteorpunch = new MeleeAbility(wc, -1, 15, 3, 4, 60, new Circle(32), 64, null);
//        abMeteorpunch.setDamagerValues(wc, 50, 100, 1.5f, -128f, false);
//
//
//        List<Sound> sounds = new ArrayList<Sound>();
//        sounds.add( new Sound("audio/si.ogg") );
//
//        return createCharacter(wc, controlled, team, idOnTeam,
//                x, y, 2000f,
//                abFrogpunch, abHook, abMeteorpunch, sounds );
//    }


    public static int allocateHitboxEntity(WorldContainer wc, Circle shape, Sound onHitSound){
        int e = wc.createEntity("melee hitbox");

        wc.addComponent(e, new PositionComp(0, 0, hitboxDepth));
        wc.addInactiveComponent(e, new RotationComp());

        //wc.addInactiveComponent(e, new PhysicsComp());
        wc.addInactiveComponent(e, new HitboxComp());

        wc.addInactiveComponent(e, new DamagerComp());

        float[] redColor = {1.0f, 0f,0f};
        wc.addInactiveComponent(e, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(shape.getRadius(), 16, redColor)) );

        wc.addInactiveComponent(e, new CollisionComp(shape));

        wc.addComponent(e, new VisualEffectComp(VisualEffectUtils.createOnHitEffect()));

        if (onHitSound != null) {
            wc.addComponent(e, new AudioComp(onHitSound));
        }

        return e;
    }



    private static int createCharacter(
            WorldContainer wc,
            int charId,
            boolean controlled, int team, int idOnTeam,
            float x, float y, float moveAccel,
            Ability ab1, Ability ab2, Ability ab3,
            List<Sound> soundList) {

        int characterEntity = wc.createEntity("character");

        //add graphics
        addCharacterGraphicsComps(wc, team, charId, characterEntity);

        wc.addComponent(characterEntity, new CharacterComp(moveAccel));//1500f));
        wc.addComponent(characterEntity, new PositionComp(x, y, (float) (characterCount++) / 100f)); //z value is a way to make draw ordering and depth positioning correspond. Else alpha images will appear incorrect.
        wc.addComponent(characterEntity, new RotationComp());


        wc.addComponent(characterEntity, new AbilityComp(ab1, ab2, ab3));

        wc.addComponent(characterEntity, new TeamComp(team, idOnTeam));

        //server and offline
        wc.addComponent(characterEntity, new PhysicsComp(80, 5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
        wc.addComponent(characterEntity, new CollisionComp(new Circle( CHARACTER_RADIUS[charId] )));
        wc.addComponent(characterEntity, new NaturalResolutionComp());

        wc.addComponent(characterEntity, new AffectedByHoleComp());

        wc.addComponent(characterEntity, new DamageableComp());
        wc.addComponent(characterEntity, new CharacterInputComp());

        //client
        wc.addComponent(characterEntity, new InterpolationComp());

        wc.addComponent(characterEntity, new AudioComp(soundList, 1, 100, 2000));

        wc.addComponent(characterEntity, new VisualEffectComp(VisualEffectUtils.createFalloutEffect()));

        if (controlled) {
            wc.addComponent(characterEntity, new UserCharacterInputComp());
            wc.addComponent(characterEntity, new ViewControlComp(-GameUtils.VIEW_WIDTH / 2f, -GameUtils.VIEW_HEIGHT / 2f));
            wc.addComponent(characterEntity, new ControlledComp());
            wc.addComponent(characterEntity, new SoundListenerComp());

        }


        return characterEntity;

    }
}
