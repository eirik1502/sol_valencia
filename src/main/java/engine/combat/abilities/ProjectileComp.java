package engine.combat.abilities;

import engine.Component;

/**
 * Created by eirik on 28.06.2017.
 */
public class ProjectileComp implements Component {


    private int lifeTime;

    private int abilityId; //the id of the ability that activated this projectile

    private boolean shouldDeactivateFlag = false;

    public ProjectileComp(int lifeTime) {
        this.lifeTime = lifeTime;
    }

    public ProjectileComp() {
        this(0);
    }

    public int getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(int lifeTime) {
        this.lifeTime = lifeTime;
    }

    public void decrementLifeTime() {
        lifeTime --;
    }


    public int getAbilityId() {
        return abilityId;
    }
    public void setAbilityId(int abilityId) {
        this.abilityId = abilityId;
    }


    public void setShouldDeactivateFlag() {
        shouldDeactivateFlag = true;
    }
    public boolean isShouldDeactivateFlag() {
        return shouldDeactivateFlag;
    }
    public void resetShouldDeactivateFlag() {
        shouldDeactivateFlag = false;
    }
}
