package engine.network.client;

import engine.*;
import engine.audio.AudioComp;
import engine.character.CharacterComp;
import engine.combat.DamageableComp;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.HitboxComp;
import engine.combat.abilities.ProjectileComp;
import engine.network.*;
import engine.network.networkPackets.*;
import engine.visualEffect.VisualEffectComp;
import utils.maths.Vec2;

import java.util.LinkedList;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientNetworkInSys implements Sys{



    private WorldContainer wc;

    private TcpPacketInput tcpPacketIn;


    private LinkedList<AllCharacterStateData> statesPending = new LinkedList<>();


    public ClientNetworkInSys(TcpPacketInput tcpPacketIn) {

        this.tcpPacketIn = tcpPacketIn;

    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;

    }

    @Override
    public void update() {
        //poll net in
        boolean polledPackets = tcpPacketIn.pollPackets();
//        if (polledPackets) System.out.println(tcpPacketIn);


        updateGameStateByServer();
    }

    @Override
    public void terminate() {
//        try {
//            inputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private void updateGameStateByServer() {

        //retrieve new packets, they are null if no packet available

        LinkedList<NetworkDataInput> characterStateData = tcpPacketIn.pollAllPackets(NetworkUtils.SERVER_CHARACTER_STATE_ID);
        NetworkDataInput abilityStartedData = tcpPacketIn.pollPacket(NetworkUtils.SERVER_ABILITY_STARTED_ID);
        NetworkDataInput hitDetectedData = tcpPacketIn.pollPacket(NetworkUtils.SERVER_HIT_DETECTED_ID);
        NetworkDataInput projectileDeadData = tcpPacketIn.pollPacket(NetworkUtils.SERVER_PROJECTILE_DEAD_ID);
        NetworkDataInput entityDeadData = tcpPacketIn.pollPacket(NetworkUtils.SERVER_CHARACTER_DEAD_ID);
        NetworkDataInput gameOverData = tcpPacketIn.pollPacket(NetworkUtils.SERVER_GAME_OVER_ID);



        //for each state data packet, might be 0
        for (NetworkDataInput data : characterStateData) {
            //translate packet
            AllCharacterStateData stateData = NetworkUtils.packetToGameState(data);

            //add packet to pendingPackets
            statesPending.add(stateData);
        }

        if (abilityStartedData != null) {
            //translate data
            AbilityStartedData data = NetworkUtils.packetToAbilityStarted(abilityStartedData);

            //Handle
            applyAbilityStarted(data);
        }

        if (hitDetectedData != null) {
            //translate
            HitDetectedData data = NetworkUtils.packetToHitDetected(hitDetectedData);

            //handle
            applyHitDetected(data);
        }

        if (projectileDeadData != null) {
            //translate
            ProjectileDeadData data = NetworkUtils.packetToProjectileDead(projectileDeadData);

            //handle
            applyProjectileDead(data);
        }

        if (entityDeadData != null) {
            //translate
            EntityDeadData data = NetworkUtils.packetToEntityDead( entityDeadData );

            //handle
            applyEntityDead(data);

        }

        if (gameOverData != null) {
            //translate
            GameOverData data = NetworkUtils.packetToGameOver( gameOverData );

            //handle
            applyGameOver(data);
        }


        //apply character states
        if (!statesPending.isEmpty()) {
//            //apply last state obtained
//            while(statesPending.size() > 1) {
//                statesPending.poll();
//            }
//            applyCharacterStates(statesPending.poll());

            //remove too old states
            int statesPendingCount = statesPending.size();
            for (int i = 1 + NetworkUtils.CLIENT_INPUT_BUFFERING; i < statesPendingCount; i++) {
                statesPending.poll();
            }

            //apply state
            applyCharacterStates(statesPending.poll());
        }
    }



    private void applyCharacterStates(AllCharacterStateData gameState) {
        //apply state

        //System.out.println("Recieved frame: " + gameState.getFrameNumber());

        //update each character according to state received
        int entityNumb = 0;
        for (int entity : wc.getEntitiesWithComponentType(CharacterComp.class)) {
//            if (entityNumb >= NetworkUtils.CHARACTER_NUMB) throw new IllegalStateException("applying game state to more characters than supported by network system");

            //Give new character state to interpolation component
            InterpolationComp interpComp = (InterpolationComp)wc.getComponent(entity, InterpolationComp.class);

            interpComp.addFrame(gameState.getFrameNumber(), new CharacterStateData( gameState.getX(entityNumb), gameState.getY(entityNumb), gameState.getRotation(entityNumb) ));

            entityNumb++;
        }

    }

    private void applyAbilityStarted(AbilityStartedData data) {
        int abilityEntity = data.getEntityId();
        int abilityId = data.getAbilityId();

        //System.out.println("apllying ability started; "+data);

        AbilityComp abComp = (AbilityComp) wc.getComponent(abilityEntity, AbilityComp.class);
        abComp.forceExecution(abilityId);

    }
    private void applyHitDetected(HitDetectedData data) {

        //System.out.println("Hit detected: "+data);

        int entityDamager = data.getEntityDamager();
        int entityDamaged = data.getEntityDamageable();
        float damageTaken = data.getDamageTaken();

        PositionComp dmgablPosComp = (PositionComp) wc.getComponent(entityDamaged, PositionComp.class);
        DamageableComp dmgableComp = (DamageableComp) wc.getComponent(entityDamaged, DamageableComp.class);

        VisualEffectComp dmgerVisefComp = (VisualEffectComp) wc.getComponent(entityDamager, VisualEffectComp.class);

        dmgableComp.applyDamage(damageTaken);

        dmgerVisefComp.requestEffect(0, dmgablPosComp.getPos());

        if (wc.hasComponent(entityDamager, AudioComp.class)) {
            AudioComp dmgerAudioComp = (AudioComp) wc.getComponent(entityDamager, AudioComp.class);

            if (dmgerAudioComp.hasSound(0)) {
                dmgerAudioComp.requestSound(0);
            }
        }
    }

    private void applyProjectileDead(ProjectileDeadData data) {

        //System.out.println("projectile should die: "+data);

        int projOwnerEntity = data.getEntityOwnerId();
        int projAbilityId = data.getProjectileAbilityId();

        for (int projEntity : wc.getEntitiesWithComponentType(ProjectileComp.class)) {
            ProjectileComp projComp = (ProjectileComp)wc.getComponent(projEntity, ProjectileComp.class);
            HitboxComp hitbComp = (HitboxComp)wc.getComponent(projEntity, HitboxComp.class);

            //if projectile does not belong to current character, skip it
            if (hitbComp.getOwner() != projOwnerEntity) continue;

            if (projComp.getAbilityId() == projAbilityId) {
                projComp.setShouldDeactivateFlag();
                break;
            }
        }

    }

    public void applyEntityDead(EntityDeadData data) {
        //create effect
        Vec2 effPos = ( (PositionComp) wc.getComponent(data.entityId, PositionComp.class) ).getPos();
        ( (VisualEffectComp)wc.getComponent(data.entityId, VisualEffectComp.class) ).requestEffect(0, effPos);
        //System.out.println("Entity died: " + data.entityId);

        //reset damage
        ( (DamageableComp)wc.getComponent(data.entityId, DamageableComp.class) ).reset();
    }

    public void applyGameOver(GameOverData data) {
        //write to gameDataComp

        //if the entity lost is on the same team as the controlled entity, print that we lost

        int controlledEntityTeam = -1;
        for (int controlledEntity : wc.getEntitiesWithComponentType(ControlledComp.class)) {
            controlledEntityTeam = ((TeamComp)wc.getComponent(controlledEntity, TeamComp.class)).team;
        }
        boolean won = controlledEntityTeam == data.teamWon;

        wc.entitiesOfComponentTypeStream(GameDataComp.class).forEach(entity -> {
            GameDataComp dataComp = (GameDataComp) wc.getComponent(entity, GameDataComp.class);
            dataComp.endGameRequest = true;
            dataComp.gameWon = won;
        });
    }

//    public AllCharacterStateData readCharacterData() {
//        AllCharacterStateData gameState = null;
//
//        int messageBytes = AllCharacterStateData.BYTES;
//
//        if (inBytesAvailable() >= messageBytes) {
//            gameState = NetworkUtils.streamToGameState(inputStream);
//        }
//
//        return gameState;
//    }
//
//    public AbilityStartedData readAbilityStarted() {
//        AbilityStartedData data = null;
//
//        int messageBytes = AbilityStartedData.BYTES;
//
//        if (inBytesAvailable() >= messageBytes) {
//            data = NetworkUtils.streamToAbilityStarted(inputStream);
//        }
//
//        return data;
//    }
//
//    public HitDetectedData readHitDetected() {
//        HitDetectedData data = null;
//
//        int messageBytes = AbilityStartedData.BYTES;
//
//        if (inBytesAvailable() >= messageBytes) {
//            data = NetworkUtils.streamToHitDetected(inputStream);
//        }
//
//        return data;
//    }
//
//
//    public ProjectileDeadData readProjectileDead() {
//        ProjectileDeadData data = null;
//
//        int messageBytes = ProjectileDeadData.BYTES;
//
//        if (inBytesAvailable() >= messageBytes) {
//            data = NetworkUtils.streamToProjectileDead(inputStream);
//        }
//
//        return data;
//    }
}
