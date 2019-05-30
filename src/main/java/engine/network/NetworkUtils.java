package engine.network;

import engine.network.networkPackets.*;

/**
 * Created by eirik on 21.06.2017.
 */
public class NetworkUtils {

    public static final int PORT_NUMBER = 7779;
    public static int CHARACTER_COUNT = 4;
    //public static final String HOST_NAME

    //network smoothening attributes
    public static int SERVER_INPUT_BUFFERING = 1;
    public static int CLIENT_INPUT_BUFFERING = 2;

    public static float CLIENT_INTERPOLATION_FRAME_COUNT = 1.0f;

    //to simulate packet loss
    public static float PACKET_LOSS_TO_SERVER = 0.0f;
    public static float PACKET_LOSS_TO_CLIENT = 0.0f;

    //packet id's
    public static final int SERVER_CHARACTER_STATE_ID = 0,
            SERVER_ABILITY_STARTED_ID = 1,
            SERVER_HIT_DETECTED_ID = 2,
            SERVER_CHARACTER_DEAD_ID = 3,
            SERVER_PROJECTILE_DEAD_ID = 4,
            SERVER_GAME_OVER_ID = 5,

            CLIENT_CHARACTER_INPUT = 6;


    public static NetworkDataOutput gameStateToPacket(AllCharacterStateData stateData) {
//        //simulate packet loss
//        if (PACKET_LOSS_TO_CLIENT > 0) {
//            if (M.random() <= PACKET_LOSS_TO_CLIENT) {
//                return true;
//            }
//        }

        NetworkDataOutput out = new NetworkDataOutput();

        out.writeInt(stateData.getFrameNumber()); //write frame number

        for (int i = 0; i < NetworkUtils.CHARACTER_COUNT; i++) {
            out.writeFloat(stateData.getX(i));
            out.writeFloat(stateData.getY(i));
            out.writeFloat(stateData.getRotation(i));
        }

        return out;
    }

    public static AllCharacterStateData packetToGameState(NetworkDataInput in) {
        AllCharacterStateData state = new AllCharacterStateData();

        state.setFrameNumber(in.readInt()); //read frame number

        for (int i = 0; i < NetworkUtils.CHARACTER_COUNT; i++) {
            state.setX(i, in.readFloat());
            state.setY(i, in.readFloat());
            state.setRotation(i, in.readFloat());
        }

        return state;
    }

    public static NetworkDataOutput characterInputToPacket(CharacterInputData charInput) {
//        //simulate packet loss
//        if (PACKET_LOSS_TO_SERVER > 0) {
//            if (M.random() <= PACKET_LOSS_TO_SERVER) {
//                return true;
//            }
//        }

        NetworkDataOutput out = new NetworkDataOutput();

        out.writeBoolean(charInput.isMoveLeft());
        out.writeBoolean(charInput.isMoveRight());
        out.writeBoolean(charInput.isMoveUp());
        out.writeBoolean(charInput.isMoveDown());

        out.writeBoolean(charInput.isAction1());
        out.writeBoolean(charInput.isAction2());
        out.writeBoolean(charInput.isAction3());

        out.writeFloat(charInput.getAimX());
        out.writeFloat(charInput.getAimY());

        return out;
    }

    public static CharacterInputData packetToCharacterInput(NetworkDataInput in) {
        CharacterInputData id = new CharacterInputData();

        id.setMovement(in.readBoolean(), in.readBoolean(), in.readBoolean(), in.readBoolean());
        id.setActions(in.readBoolean(), in.readBoolean(), in.readBoolean());
        id.setAim(in.readFloat(), in.readFloat());

        return id;
    }


    public static NetworkDataOutput abilityStartedDataToPacket(AbilityStartedData data) {
        NetworkDataOutput out = new NetworkDataOutput();

        out.writeInt(data.getEntityId());
        out.writeInt(data.getAbilityId());

        return out;
    }
    public static AbilityStartedData packetToAbilityStarted(NetworkDataInput in) {
        AbilityStartedData data = new AbilityStartedData();

        data.setEntityId( in.readInt() );
        data.setAbilityId( in.readInt() );

        return data;
    }


    public static NetworkDataOutput hitDetectedToPacket(HitDetectedData data) {
        NetworkDataOutput out = new NetworkDataOutput();

        out.writeInt(data.getEntityDamager());
        out.writeInt(data.getEntityDamageable());
        out.writeFloat(data.getDamageTaken());

        return out;
    }

    public static HitDetectedData packetToHitDetected(NetworkDataInput out) {
        HitDetectedData data = new HitDetectedData();

        data.setEntityDamager( out.readInt() );
        data.setEntityDamageable( out.readInt() );
        data.setDamageTaken( out.readFloat() );

        return data;
    }


    public static NetworkDataOutput projectileDeadToPacket(ProjectileDeadData data) {
        NetworkDataOutput out = new NetworkDataOutput();

        out.writeInt(data.getEntityOwnerId());
        out.writeInt(data.getProjectileAbilityId());

        return out;
    }

    public static ProjectileDeadData packetToProjectileDead(NetworkDataInput in) {
        ProjectileDeadData data = new ProjectileDeadData();

        data.setEntityOwnerId( in.readInt() );
        data.setProjectileAbilityId( in.readInt() );

        return data;
    }

    public static NetworkDataOutput entityDeadToPacket(EntityDeadData data) {
        NetworkDataOutput out = new NetworkDataOutput();

        out.writeInt(data.entityId);

        return out;
    }

    public static EntityDeadData packetToEntityDead(NetworkDataInput in) {
        EntityDeadData data = new EntityDeadData();

        data.entityId = in.readInt();

        return data;
    }

    public static NetworkDataOutput gameOverToPacket(GameOverData data) {
        NetworkDataOutput out = new NetworkDataOutput();

        out.writeInt(data.teamWon);

        return out;
    }

    public static GameOverData packetToGameOver(NetworkDataInput in) {
        GameOverData data = new GameOverData();

        data.teamWon = in.readInt();

        return data;
    }

//
//    public static boolean sendClientStateId(int id, DataOutputStream outputStream) {
//
//        try{
//            System.out.println("Sending id: " + id + " to " + outputStream.toString());
//            outputStream.write(id);
//        }
//
//
//        catch (SocketException e) {
//            return false;
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return true;
//
//    }
//
//    public static int streamToCharacterSelected(DataInputStream inputStream) {
//        int characterId = -1;
//        try{
//            characterId = inputStream.readInt();
//        }
//        catch (IOException e){
//            System.out.println("Could not read");
//        }
//        return characterId;
//    }

}
