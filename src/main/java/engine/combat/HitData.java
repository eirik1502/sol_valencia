package engine.combat;

import utils.maths.Vec2;

/**
 * Created by eirik on 07.07.2017.
 */
public class HitData {

    private int entityDamager;
    private int entityDamaged;

    private float damageDelt;
    private Vec2 knockbackApplied;

    public HitData(int entityDamager, int entityDamaged, float damageDelt, Vec2 knockbackApplied) {
        this.entityDamager = entityDamager;
        this.entityDamaged = entityDamaged;
        this.damageDelt = damageDelt;
        this.knockbackApplied = knockbackApplied;
    }

    public int getEntityDamager() {
        return entityDamager;
    }

    public int getEntityDamaged() {
        return entityDamaged;
    }

    public float getDamageDelt() {
        return damageDelt;
    }

    public Vec2 getKnockbackApplied() {
        return knockbackApplied;
    }
}
