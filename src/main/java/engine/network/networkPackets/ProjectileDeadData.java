package engine.network.networkPackets;

import engine.network.NetworkUtils;

/**
 * Created by eirik on 07.07.2017.
 */
public class ProjectileDeadData implements NetworkPacket{

    public static final int BYTES = Integer.BYTES *2;


    private int entityOwnerId;
    private int projectileAbilityId;

    public ProjectileDeadData(int entityOwnerId, int projectileAbilityId) {
        this.entityOwnerId = entityOwnerId;
        this.projectileAbilityId = projectileAbilityId;
    }
    public ProjectileDeadData() {
        this(0, 0);
    }


    public int getEntityOwnerId() {
        return entityOwnerId;
    }
    public void setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
    }

    public int getProjectileAbilityId() {
        return projectileAbilityId;
    }
    public void setProjectileAbilityId(int projectileAbilityId) {
        this.projectileAbilityId = projectileAbilityId;
    }


    public String toString() {
        return "["+getClass().getSimpleName()+": entityOwnerId="+entityOwnerId+" projectileAbilityId="+projectileAbilityId+"]";
    }

    @Override
    public int getPacketId() {
        return NetworkUtils.SERVER_PROJECTILE_DEAD_ID;
    }

    @Override
    public String serialize() {
        return ""+ entityOwnerId + ' ' +projectileAbilityId;
    }
}
