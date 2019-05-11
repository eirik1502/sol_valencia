package engine.network.networkPackets;

/**
 * Created by eirik on 07.07.2017.
 */
public class EntityDeadData {

    public static final int BYTES = Integer.BYTES;


    public int entityId;


    public EntityDeadData(int entityId) {
        this.entityId = entityId;
    }
    public EntityDeadData() {
    }

//    public EntityDeadData() {
//        this(0);
//    }
//
//
//    public int getEntityId() {
//        return entityId;
//    }
//
//    public void setEntityId(int entityId) {
//        this.entityId = entityId;
//    }

    public String toString() {
        return "["+getClass().getSimpleName()+": entityId="+entityId+"]";
    }

    public String serialize() {
        return ""+entityId;
    }
}
