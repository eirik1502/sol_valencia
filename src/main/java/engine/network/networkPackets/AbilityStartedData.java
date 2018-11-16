package engine.network.networkPackets;

/**
 * Created by eirik on 07.07.2017.
 */
public class AbilityStartedData {

    public static final int BYTES = Integer.BYTES *2;


    private int entityId;
    private int abilityId;


    public AbilityStartedData(int entityId, int abilityId) {
        this.entityId = entityId;
        this.abilityId = abilityId;
    }
    public AbilityStartedData() {
        this(0, 0);
    }


    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(int abilityId) {
        this.abilityId = abilityId;
    }


    public String toString() {
        return "[AbilityStartedData: entityId="+entityId+" abilityId="+abilityId+"]";
    }
}
