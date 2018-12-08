package engine.network.server;

import engine.PositionComp;
import engine.RotationComp;
import engine.Sys;
import engine.WorldContainer;
import engine.character.CharacterComp;
import engine.character.CharacterInputComp;
import engine.combat.DamageableComp;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.HitboxComp;
import engine.combat.abilities.ProjectileComp;
import engine.network.*;
import engine.network.networkPackets.*;
import engine.physics.AffectedByHoleComp;
import game.server.ServerClientHandler;
import game.server.ServerGameDataComp;
import game.server.ServerIngame;

import java.util.*;

/**
 * Created by eirik on 21.06.2017.
 */
public class ServerNetworkSys implements Sys {

    private WorldContainer wc;

    private int frameNumber = 0; //Integer.MIN_VALUE;

    private List<ServerClientHandler> clientHandlers;



    public ServerNetworkSys(List<ServerClientHandler> clientHandlers) {

        this.clientHandlers = clientHandlers;

    }


    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        //poll client network input
        clientHandlers.forEach(client -> {
            client.getTcpPacketIn().pollPackets();
        });

        updateCharactersByInput();

        sendGameDataToClients();

        frameNumber++;
        if (frameNumber == Integer.MAX_VALUE) throw new IllegalStateException("max frame number value reached. Make it wrap");
    }

    @Override
    public void terminate() {

    }



    private void updateCharactersByInput() {
        //update each character. WATCH THE ORDERING OF CHARACTERS CORRESPONDING TO CLIENTS
        //This should be done at the start of iteration.. if feels necessary...
        int i = 0;
        for (int entity : wc.getEntitiesWithComponentType(CharacterComp.class)) {
            if (clientHandlers.size() == 0 || (clientHandlers.size() == 1 && i==1) ) break; //<-----------------------------------------------UGLY

            //get input from each client, hoply corresponding to character ordering, and update the entity
            CharacterInputComp inpComp = wc.getComponent(entity, CharacterInputComp.class);

            //data from clients
            CharacterInputData inData = clientHandlers.get(i).getInputData();

            if (inData != null) {
                //System.out.println("Got input data: "+inData);
                writeInDataToComp(inData, inpComp);
            }

            i++;
        }
    }

    private void sendGameDataToClients() {

        sendCharacterData( retrieveCharacterData() );

        retrieveAbilitiesStarted().forEach(abStarted -> sendAbilityStarted(abStarted) );

        retrieveHitsDetected().forEach(hitsDetected -> sendHitDetected(hitsDetected) );

        retrieveDeadProjectiles().forEach(deadProj -> sendProjectileDead(deadProj) );

        retrieveDeadEntities().forEach(deadEntity -> sendEntityDead(deadEntity));

        retrieveGameOver().forEach(gameOver -> sendGameOver(gameOver));
    }


    private AllCharacterStateData retrieveCharacterData() {
        AllCharacterStateData sd = new AllCharacterStateData();

        sd.setFrameNumber(frameNumber);

        Set<Integer> chars = wc.getEntitiesWithComponentType(CharacterComp.class);
//        if (chars.size() != 2) throw new IllegalStateException("THere is not 2 characters on the field :(");

        int charNumb = 0;
        for (int c : chars) {
            PositionComp posComp = (PositionComp)wc.getComponent(c, PositionComp.class);
            RotationComp rotComp = (RotationComp)wc.getComponent(c, RotationComp.class);
//            CharacterComp charComp = (CharacterComp)wc.getComponent(c, CharacterComp.class);

            sd.setX(charNumb, posComp.getX());
            sd.setY(charNumb, posComp.getY());
            sd.setRotation(charNumb, rotComp.getAngle());


            charNumb++;
        }

        return sd;
    }

    private List<AbilityStartedData> retrieveAbilitiesStarted() {
        List<AbilityStartedData> data = new ArrayList<>();

        wc.entitiesOfComponentTypeStream(AbilityComp.class).forEach(abEntity -> {
            AbilityComp abComp = (AbilityComp)wc.getComponent(abEntity, AbilityComp.class);

            //find newly executed abilities
            if (abComp.hasNewExecuting()) {
                int startedAbility = abComp.popNewExecuting();
                data.add( new AbilityStartedData(abEntity, startedAbility) );
            }
        });

        return data;
    }

    private List<HitDetectedData> retrieveHitsDetected() {
        List<HitDetectedData> data = new ArrayList<>();

        wc.entitiesOfComponentTypeStream(DamageableComp.class).forEach(dmgablEntity -> {
            DamageableComp dmgablComp = (DamageableComp)wc.getComponent(dmgablEntity, DamageableComp.class);

            dmgablComp.hitDataStream().forEach( hitData -> {
                data.add( new HitDetectedData(hitData.getEntityDamager(), hitData.getEntityDamaged(), hitData.getDamageDelt() ) );

            });
        });

        return data;
    }

    private List<ProjectileDeadData> retrieveDeadProjectiles() {
        List<ProjectileDeadData> projectileData = new ArrayList<>();

        for (int entity : wc.getEntitiesWithComponentType(ProjectileComp.class)) {
            ProjectileComp projComp = (ProjectileComp)wc.getComponent(entity, ProjectileComp.class);
            HitboxComp hitbComp = (HitboxComp)wc.getComponent(entity, HitboxComp.class);


            if (projComp.isShouldDeactivateFlag()) {
                ProjectileDeadData data = new ProjectileDeadData(hitbComp.getOwner(), projComp.getAbilityId());
                projectileData.add(data);
            }
        }

        return projectileData;
    }


    private List<EntityDeadData> retrieveDeadEntities() {
        List<EntityDeadData> deadEntities = new ArrayList<>();

        wc.entitiesOfComponentTypeStream(CharacterComp.class).forEach( entity -> {
//            AffectedByHoleComp affholeComp = (AffectedByHoleComp) wc.getComponent(entity, AffectedByHoleComp.class);
//
//            if (affholeComp.isHoleAffectedFlag()) {
//                deadEntities.add( new EntityDeadData( entity ) );
//            }
            CharacterComp charComp = (CharacterComp) wc.getComponent(entity, CharacterComp.class);
            if (charComp.respawnTimer == charComp.respawnTime) {
                deadEntities.add( new EntityDeadData( entity ) );
            }
        });

        return deadEntities;
    }

    private List<GameOverData> retrieveGameOver() {
        List<GameOverData> gameOver = new ArrayList<>();

        wc.entitiesOfComponentTypeStream(ServerGameDataComp.class).forEach(entity -> {
            ServerGameDataComp dataComp = (ServerGameDataComp) wc.getComponent(entity, ServerGameDataComp.class);

            if (dataComp.teamWon != -1) {
                gameOver.add( new GameOverData(dataComp.teamWon) );
            }
        });

        return gameOver;
    }

    public void sendCharacterData(AllCharacterStateData gameState) {
        clientHandlers.forEach(client -> client.sendCharacterData(gameState));
    }

    private void sendAbilityStarted(AbilityStartedData abData) {
        clientHandlers.forEach(client -> client.sendAbilityStarted(abData));
    }
    private void sendHitDetected(HitDetectedData hitData) {
        clientHandlers.forEach(client -> client.sendHitDetected(hitData));
    }
    private void sendProjectileDead(ProjectileDeadData projDeadData) {
        clientHandlers.forEach(client -> client.sendProjectileDead(projDeadData));
    }
    private void sendEntityDead(EntityDeadData data) {
        //create packet
        NetworkDataOutput out = NetworkUtils.entityDeadToPacket(data);

        //send packet to all clients
        clientHandlers.forEach(client -> {
            client.getTcpPacketOut().send(NetworkUtils.SERVER_CHARACTER_DEAD_ID, out);
        });
    }
    private void sendGameOver(GameOverData data) {
        //create network packet
        NetworkDataOutput out = NetworkUtils.gameOverToPacket(data);

        //send packet to all clients
        clientHandlers.forEach(client -> {
            client.getTcpPacketOut().send(NetworkUtils.SERVER_GAME_OVER_ID, out);
        });
    }




    private void writeInDataToComp(CharacterInputData inData, CharacterInputComp inpComp) {
        inpComp.setMoveLeft( inData.isMoveLeft() );
        inpComp.setMoveRight( inData.isMoveRight() );
        inpComp.setMoveUp( inData.isMoveUp() );
        inpComp.setMoveDown( inData.isMoveDown() );

        inpComp.setAction1( inData.isAction1() );
        inpComp.setAction2( inData.isAction2() );
        inpComp.setAction3( inData.isAction3() );

        inpComp.setAimX( inData.getAimX() );
        inpComp.setAimY( inData.getAimY() );
    }


}
