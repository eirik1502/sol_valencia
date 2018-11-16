package engine.combat.abilities;

import engine.WorldContainer;
import engine.physics.Circle;
import engine.physics.Rectangle;
import engine.physics.Shape;
import game.GameUtils;

/**
 * Created by eirik on 19.06.2017.
 */
public abstract class Ability {

    private int abilityId;

    private int startupTime;
    private int effectTime;
    private int endlagTime;
    private int rechargeTime;

    private int startEffectSoundIndex = -1;



    private boolean recharging = false;

    private boolean requestExecution;

    public int counter;



    public Ability(WorldContainer wc, int startEffectSoundIndex, int startupTime, int effectTime, int endlagTime, int rechargeTime){
        this.startEffectSoundIndex = startEffectSoundIndex;
        this.startupTime = startupTime;
        this.effectTime = effectTime;
        this.endlagTime = endlagTime;
        this.rechargeTime = rechargeTime;
    }
//    public MeleeAbility(WorldContainer wc){
//        this(wc, 5, 0.5f, new Circle(5), 0.0f, 0.0f, 10, 10, 10, 10);
//    }

    abstract void startEffect(WorldContainer wc, int requestingEntity);
    abstract void duringEffect(WorldContainer wc, int requestingEntity);
    abstract void endEffect(WorldContainer wc, int requestingEntity);

    //to be called by abilityCom
    void setAbilityId(int id) {
        this.abilityId = id;
    }
    public int getAbilityId() {
        return abilityId;
    }

    public void requestExecution() {
        requestExecution = true;

    }

    boolean isRequestingExecution() {
        return requestExecution;
    }
    void setRequestExecution(boolean b){
        this.requestExecution = b;
    }

    int getStartEffectSoundIndex() {
        return startEffectSoundIndex;
    }

    int getStartupTime() {
        return startupTime;
    }
    int getEffectTime() {
        return effectTime;
    }
    int getEndlagTime() {
        return endlagTime;
    }
    int getRechargeTime() {
        return rechargeTime;
    }

    boolean isRecharging() {
        return recharging;
    }
    void setRecharging(boolean recharge) {
        this.recharging = recharge;
    }


}
