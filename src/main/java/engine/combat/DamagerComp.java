package engine.combat;

import engine.Component;

/**
 * Created by eirik on 20.06.2017.
 */
public class DamagerComp implements Component {


    private float damage;
    private float baseKnockback;
    private float knockbackRatio;

    private float knockbackPoint; //along rotation direction
    private boolean towardPoint; //from or towards point


    private boolean deltDamageFlag = false; //is reset by damageResolutionSys every frame



    public DamagerComp(float damage, float baseKnockback, float knockbackRatio, float knockbackPoint, boolean towardPoint) {
        setDamage(damage);
        setBaseKnockback(baseKnockback);
        setKnockbackRatio(knockbackRatio);
        setKnockbackPoint(knockbackPoint);
        setTowardPoint(towardPoint);
    }
    public DamagerComp() {
        this(0, 0,0, 0, false);
    }


    public float getDamage() {
        return damage;
    }
    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getBaseKnockback() {
        return baseKnockback;
    }
    public void setBaseKnockback(float baseKnockback) {
        this.baseKnockback = baseKnockback;
    }

    public float getKnockbackRatio() {
        return knockbackRatio;
    }
    public void setKnockbackRatio(float knockbackRatio) {
        this.knockbackRatio = knockbackRatio;
    }

    public float getKnockbackPoint() {
        return knockbackPoint;
    }
    public void setKnockbackPoint(float knockbackPoint) {
        this.knockbackPoint = knockbackPoint;
    }

    public boolean isTowardPoint() {
        return towardPoint;
    }
    public void setTowardPoint(boolean towardPoint) {
        this.towardPoint = towardPoint;
    }

    public boolean hasDeltDamage() {
        return deltDamageFlag;
    }
    public void deltDamage() {
        deltDamageFlag = true;
    }
    void resetDeltDamage() {
        deltDamageFlag = false;
    }
}
