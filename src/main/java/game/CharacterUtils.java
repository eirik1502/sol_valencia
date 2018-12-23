package game;

import engine.*;
import engine.audio.AudioComp;
import engine.audio.Sound;
import engine.audio.SoundListenerComp;
import engine.character.CharacterComp;
import engine.character.CharacterInputComp;
import engine.combat.DamageableComp;
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

    public static final int CHARACTER_COUNT = 4;
    public static final int SHRANK = 0, SCHMATHIAS = 1, BRAIL = 2, MAGNET = 3;
    public static final String[] CHARACTER_NAMES = {"Frank", "KingSkurkTwo", "Brail", "MagneT"};


    private static final float[] charactersRadius = { 32, 32, 44, 36 };
    private static final float[] charactersMoveAccel = { 1800, 2000, 2000, 2000 };
    private static SpecificCharacterUtils[] charactersUtils = {new ShrankUtils(), null, null, new MagnetUtils()};


    private static int characterCount; //this is for a hack, not actual count

    //teamId - charId
    public static final LoadImageData[][] loadCharData= {
            //Shrank
            {new LoadImageData("sol_frank_red.png", charactersRadius[0], 160 / 2f, 512, 256, 180, 130),
                    new LoadImageData("sol_frank_blue.png", charactersRadius[0], 160 / 2f, 512, 256, 180, 130)
            },
            /*Schmathias*/
            {new LoadImageData("schmathias_red.png", charactersRadius[1], 146, 1258, 536, 386, 258),
                    new LoadImageData("schmathias_blue.png", charactersRadius[1], 146, 1258, 536, 386, 258)
            },
            /*Brail*/
            {new LoadImageData("brail_red.png", charactersRadius[2], 272, 1600, 1200, 795, 585),
                    new LoadImageData("brail_blue.png", charactersRadius[2], 272, 1600, 1200, 795, 585)
            },
            //Magnet
            {new LoadImageData("magnet.png", charactersRadius[3], 296/2, 1200, 600, 365, 257),
                    new LoadImageData("masai_blue.png", charactersRadius[3], 296/2, 1200, 600, 365, 257)
            }
    };


    public static int[][] createOfflineCharacters(WorldContainer wc, ClientGameTeams teams, boolean media) {

        return createClientCharacters(wc, teams, media);
    }

    public static int[][] createClientCharacters(WorldContainer wc, ClientGameTeams teams, boolean media) {
        int[][] charEntIds = new int[teams.getTeamCount()][];

        for (int j = 0; j < teams.getTeamCount(); j++) {
            charEntIds[j] = new int[teams.getCharacterIdsOnTeam(j).length];

            int i = 0;
            for (int charEnt : teams.getCharacterIdsOnTeam(j)) {
                boolean controlled = false;
                if (teams.getControlCharacterTeam() == j && i == teams.getControlCharacterIndex()) {
                    controlled = true;
                }

                int e = createCharacterById(charEnt, wc, controlled, j, i, GameUtils.teamStartPos[j][i].x, GameUtils.teamStartPos[j][i].y, media);

                charEntIds[j][i] = e;

                i++;
            }
        }

        return charEntIds;
    }

    /**
     * Chreates characters given, on the teams given, and with controll by this system if given.
     *
     * @param wc
     * @param teams
     * @param media
     * @return
     */
    public static int[][] createServerCharacters(WorldContainer wc, ServerGameTeams teams, boolean media) {
        boolean controlled = true;

        int[][] charEntIds = new int[teams.getTeamCount()][];

        for (int j = 0; j < teams.getTeamCount(); j++) {
            charEntIds[j] = new int[teams.getCharacterIdsOnTeam(j).length];

            int i = 0;
            for (int charEnt : teams.getCharacterIdsOnTeam( j )) {

                int e = createCharacterById(charEnt, wc, controlled, j, i, GameUtils.teamStartPos[j][i].x, GameUtils.teamStartPos[j][i].y, media);
                charEntIds[j][i] = e;

                i++;
            }
        }

        return charEntIds;
    }


    private static int createCharacterById(int charId, WorldContainer wc, boolean controlled, int team, int idOnTeam, float x, float y, boolean media) {
        if (charId < 0 || charId >= CHARACTER_NAMES.length)
            throw new IllegalArgumentException("char id given is not valid");


        float moveAccel = charactersMoveAccel[charId];
        float radius = charactersRadius[charId];

        //create entity
        int charEnt = wc.createEntity("character");

        //add components
        addCharacterCoreComps(wc, charEnt, moveAccel, x, y, radius);
        addCharacterTeamComps(wc, charEnt, team, idOnTeam);

        //character spesific components
        charactersUtils[charId].addAbilityComps(wc, charEnt, media);

        if (controlled) {
            addCharacterControlledComp(wc, charEnt);
        }

        if (media) {
            LoadImageData imageData = loadCharData[charId][team];
            List<Sound> sounds = charactersUtils[charId].createCharacterSounds();

            addCharacterGraphicsComps(wc, charEnt, imageData);
            addCharacterAudioComps(wc, charEnt, sounds);
            addCharacterVisualEffects(wc, charEnt);
        }

        return charEnt;
    }

    //private static void addCoreCharacterComps (
    private static void addCharacterCoreComps (
            WorldContainer wc, int characterEntity,
            float moveAccel,
            float x, float y, float radius) {

        wc.addComponent(characterEntity, new CharacterComp(moveAccel));//1500f));
        wc.addComponent(characterEntity, new PositionComp(x, y, (float) (characterCount++) / 100f)); //z value is a way to make draw ordering and depth positioning correspond. Else alpha images will appear incorrect.
        wc.addComponent(characterEntity, new RotationComp());

        //server and offline
        wc.addComponent(characterEntity, new PhysicsComp(80, 5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
        wc.addComponent(characterEntity, new CollisionComp(new Circle( radius )));
        wc.addComponent(characterEntity, new NaturalResolutionComp());

        wc.addComponent(characterEntity, new AffectedByHoleComp());

        wc.addComponent(characterEntity, new DamageableComp());
        wc.addComponent(characterEntity, new CharacterInputComp());

        //client
        wc.addComponent(characterEntity, new InterpolationComp());
    }
    private static void addCharacterTeamComps(WorldContainer wc, int entity, int team, int idOnTeam) {
        wc.addComponent(entity, new TeamComp(team, idOnTeam));

    }
    private static void addCharacterControlledComp(WorldContainer wc, int entity) {
        wc.addComponent(entity, new UserCharacterInputComp());
        wc.addComponent(entity, new ViewControlComp(-GameUtils.VIEW_WIDTH / 2f, -GameUtils.VIEW_HEIGHT / 2f));
        wc.addComponent(entity, new ControlledComp());
        wc.addComponent(entity, new SoundListenerComp());
    }

    public static void addCharacterGraphicsComps(WorldContainer wc, int entity, LoadImageData data) {
//        System.out.println("charId="+charId+" teamId="+teamId+" entity="+entity+ " Filename="+data.filename);

        TexturedMeshComp texmeshComp = new TexturedMeshComp( TexturedMeshUtils.createRectangle(data.filename, data.width, data.height) );
        MeshCenterComp meshcentComp = new MeshCenterComp(data.offsetX, data.offsetY);

        wc.addComponent(entity, texmeshComp);
        wc.addComponent(entity, meshcentComp);
    }
    private static void addCharacterAudioComps(WorldContainer wc, int entity, List<Sound> soundList) {
        wc.addComponent(entity, new AudioComp(soundList, 1, 100, 2000));
    }
    private static void addCharacterVisualEffects(WorldContainer wc, int entity) {
        wc.addComponent(entity, new VisualEffectComp(VisualEffectUtils.createFalloutEffect()));
    }




    //-----------DEPRECATED BELOW-------------

    @Deprecated
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
        int melee1Entity = MeleeAbilityUtils.allocateHitboxEntity(wc, new Circle(128f), sndBoom, true);
        MeleeAbility abPuffer = new MeleeAbility(wc, boomSoundIndex, melee1Entity, 8, 2, 8, 60*3, 0f);
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

    @Deprecated
    private static int createSchmathias(
            WorldContainer wc, int charId,
            boolean controlled, int team, int idOnTeam,
            float x, float y) {

        //frogpunch
        int frogPunchSoundIndex = 0;
        int hookInitSoundIndex = 1;
        int meteorPunchSoundIndex = 2;


        int melee1Entity = MeleeAbilityUtils.allocateHitboxEntity(wc, new Circle(64f), null, true);
        MeleeAbility abFrogpunch = new MeleeAbility(wc, frogPunchSoundIndex, melee1Entity, 3, 5, 3, 20,48.0f);
        abFrogpunch.setDamagerValues(wc, 150, 700, 0.8f, -48f, false);

        //hook
        int hookProjEntity = ProjectileUtils.allocateImageProjectileEntity(wc, "hook.png", 256/2, 512, 256, 24, new Sound("audio/hook_hit.ogg")); //both knockback angle and image angle depends on rotation comp. Cheat by setting rediusOnImage negative
        ProjectileAbility abHook = new ProjectileAbility(wc, hookInitSoundIndex, hookProjEntity, 5, 18, 50, 900, 30);
        abHook.setDamagerValues(wc, 200f, 1400f, 0.2f, -128, true);

        //meteorpunch
        int melee2Entity = MeleeAbilityUtils.allocateHitboxEntity(wc, new Circle(32f), null, true);
        MeleeAbility abMeteorpunch = new MeleeAbility(wc, meteorPunchSoundIndex, melee2Entity, 15, 3, 4, 60, 64);
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

    @Deprecated
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
        int melee1Entity = MeleeAbilityUtils.allocateHitboxEntity(wc, new Circle(70f), null, true);
        MeleeAbility ab1 = new MeleeAbility(wc, ab1CharSnd, melee1Entity, 6, 6, 6, 30,64.0f);
        ab1.setDamagerValues(wc, 150, 600, 1.2f, 400f, true);

        //chagger
        int chaggProjectile = ProjectileUtils.allocateSinglecolorProjectileAbility(wc, 64f, purple,null); //both knockback angle and image angle depends on rotation comp. Cheat by setting rediusOnImage negative
        ProjectileAbility ab2 = new ProjectileAbility(wc, ab2CharSnd, chaggProjectile, 10, 6, 120, 650, 40);
        ab2.setDamagerValues(wc, 300, 400, 0.8f, 64, false);

        //merge
        int melee2Entity = MeleeAbilityUtils.allocateHitboxEntity(wc, new Circle(160f), null, true);
        MeleeAbility ab3 = new MeleeAbility(wc, ab3CharSnd, melee2Entity, 10, 2, 8, 60, 128);
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


    private static Ability createAbMagnetSpearPoke(WorldContainer wc, int abIndex) {
        int melee1Entity = MeleeAbilityUtils.allocateHitboxEntity(wc, new Circle(20f), null, true);
        MeleeAbility ab1 = new MeleeAbility(wc, abIndex, melee1Entity, 6, 6, 6, 13,128.0f);
        ab1.setDamagerValues(wc, 150, 600, 0.7f, -100f, false);
        return ab1;
    }
    private static Ability createAbMagnetSpearThrow(WorldContainer wc, int abIndex) {
        int spearProj = ProjectileUtils.allocateImageProjectileEntity(wc, "magnet_spear.png", 48, 536, 32*2, 32, new Sound("audio/arrow_impact.ogg")); //both knockback angle and image angle depends on rotation comp. Cheat by setting rediusOnImage negative
        ProjectileAbility ab2 = new ProjectileAbility(wc, abIndex, spearProj, 30, 18, 50, 1500, 90);
        ab2.setDamagerValues(wc, 400f, 800f, 2f, -32f, false);
        return ab2;
    }
    private static Ability createAbMagnetLion(WorldContainer wc, int abIndex) {
        //lion
        int lionProj = ProjectileUtils.allocateImageProjectileEntity(wc, "masai_lion.png", 210/2, 435, 457, 64, new Sound("audio/hook_hit.ogg")); //both knockback angle and image angle depends on rotation comp. Cheat by setting rediusOnImage negative
        ProjectileAbility ab3 = new ProjectileAbility(wc, abIndex, lionProj, 12, 12, 50, 400, 40);
        ab3.setDamagerValues(wc, 600f, 800f, 0.5f, 0, false);
        return ab3;
    }

    @Deprecated
    private static int createMagnet (
            WorldContainer wc, int charId,
            boolean controlled, int team, int idOnTeam,
            float x, float y) {



        Sound snd1 = new Sound("audio/click4.ogg");
        Sound snd2 = new Sound("audio/masai_arrow_throw.ogg");
        Sound snd3 = new Sound("audio/lion-roar.ogg");

        Ability ab1 = createAbMagnetSpearPoke(wc, 0);
        Ability ab2 = createAbMagnetSpearThrow(wc, 1);
        Ability ab3 = createAbMagnetLion(wc, 2);

        List<Sound> sounds = new ArrayList<>();
        sounds.add( snd1 );
        sounds.add( snd2 );
        sounds.add( snd3 );

        return createCharacter(wc, charId,
                controlled, team, idOnTeam,
                x, y, 2000,
                ab1, ab2, ab3, sounds);
    }

    //private static void addCoreCharacterComps (
    @Deprecated
    private static int createCharacter (
            WorldContainer wc,
            int charId,
            boolean controlled, int team, int idOnTeam,
            float x, float y, float moveAccel,
            Ability ab1, Ability ab2, Ability ab3,
            List<Sound> soundList) {

        int characterEntity = wc.createEntity("character");

        //add graphics
        LoadImageData imageData = loadCharData[charId][team];
        addCharacterGraphicsComps(wc, charId, imageData);

        wc.addComponent(characterEntity, new CharacterComp(moveAccel));//1500f));
        wc.addComponent(characterEntity, new PositionComp(x, y, (float) (characterCount++) / 100f)); //z value is a way to make draw ordering and depth positioning correspond. Else alpha images will appear incorrect.
        wc.addComponent(characterEntity, new RotationComp());


        wc.addComponent(characterEntity, new AbilityComp(ab1, ab2, ab3));

        wc.addComponent(characterEntity, new TeamComp(team, idOnTeam));

        //server and offline
        wc.addComponent(characterEntity, new PhysicsComp(80, 5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
        wc.addComponent(characterEntity, new CollisionComp(new Circle( charactersRadius[charId] )));
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
}
