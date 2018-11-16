package engine.network.networkPackets;

/**
 * Created by eirik on 07.07.2017.
 */
public class HitDetectedData {

    public static final int BYTES = Integer.BYTES * 2 + Float.BYTES;


    private int entityDamager;
    private int entityDamageable;
    private float damageTaken;


    public HitDetectedData(int entityDamager, int entityDamageable, float damageTaken) {
        this.entityDamager = entityDamager;
        this.entityDamageable = entityDamageable;
        this.damageTaken = damageTaken;
    }
    public HitDetectedData() {
        this(0, 0, 0);
    }


    public int getEntityDamager() {
        return entityDamager;
    }

    public void setEntityDamager(int entityDamager) {
        this.entityDamager = entityDamager;
    }

    public int getEntityDamageable() {
        return entityDamageable;
    }

    public void setEntityDamageable(int entityDamageable) {
        this.entityDamageable = entityDamageable;
    }

    public float getDamageTaken() {
        return damageTaken;
    }
    public void setDamageTaken(float damageTaken) {
        this.damageTaken = damageTaken;
    }


    public String toString() {
        return "["+getClass().getSimpleName()+": entityDamager="+entityDamager+" entityDamageable="+entityDamageable+" totalDamageTaken="+damageTaken+"]";
    }
}
