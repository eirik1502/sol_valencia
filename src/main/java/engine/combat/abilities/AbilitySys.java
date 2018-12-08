package engine.combat.abilities;

import engine.PositionComp;
import engine.RotationComp;
import engine.Sys;
import engine.WorldContainer;
import engine.audio.AudioComp;
import engine.combat.DamageableComp;
import engine.physics.PhysicsComp;
import javafx.geometry.Pos;
import utils.maths.Vec2;

import java.awt.geom.RoundRectangle2D;
import java.util.Set;

/**
 * Created by haraldvinje on 21-Jun-17.
 */
public class AbilitySys implements Sys {

    WorldContainer wc;

    @Override

    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        wc.entitiesOfComponentTypeStream(AbilityComp.class).forEach(entity -> {

            PositionComp posComp = wc.getComponent(entity, PositionComp.class);
            RotationComp rotComp = wc.getComponent(entity, RotationComp.class);
            AbilityComp abComp = wc.getComponent(entity, AbilityComp.class);
            AudioComp audioComp =
                    wc.hasComponent(entity, AudioComp.class) ?
                    wc.getComponent(entity, AudioComp.class) :
                    null;

            if (wc.hasComponent(entity, DamageableComp.class)) {
                DamageableComp dmgableComp = wc.getComponent(entity, DamageableComp.class);

                if (dmgableComp.isInterrupted()) {
                    abComp.abortExecution();
                }
            }

            if (abComp.isAbortExecution()) {
                abComp.resetAbortExecution();
                abortAbilityExecution(abComp, entity);
            }

            abComp.streamAbilities().forEach(a -> updateMeleeAbility(entity, a, abComp, posComp, rotComp, audioComp ) );
        });
    }

    @Override
    public void terminate() {

    }

    private void abortAbilityExecution(AbilityComp abComp, int requestingEntity) {
        if (abComp.getOccupiedBy() != null) {
            Ability ab = abComp.getOccupiedBy();

            //end effect
            ab.endEffect(wc, requestingEntity);

            //go to recharge if ability is aborted
            ab.setRecharging(true);

            abComp.setOccupiedBy(null);
        }
    }

    private void updateMeleeAbility(int entity, Ability ability, AbilityComp abComp, PositionComp posComp, RotationComp rotComp, AudioComp audioComp){

        int startupTime = ability.getStartupTime();
        int effectTime = ability.getEffectTime();
        int endingLagTime = ability.getEndlagTime();
        int rechargeTime = ability.getRechargeTime();


        //move to next frame. Even thoug it is not executing
        ability.counter++;

        //if this ability is recharging, continue recharging and do nothing else
        if (! ability.isRecharging()) {

            //if no ability is executing, check if this one should be executed
            if (abComp.getOccupiedBy() == null) {
                //is ability is requested, execute it
                if (ability.isRequestingExecution()) {

                    startExecution(abComp, ability);
//                    AudioComp ac = (AudioComp)wc.getComponent(entity, AudioComp.class);

                }
            }

            //if this ability should execute, do it
            if (abComp.getOccupiedBy() == ability) {

                if (ability.counter < startupTime) {
                    //do nothing, but keeps the flow straight
                } else if (ability.counter == startupTime) {
                    startEffect(ability, entity, audioComp);
                } else if (ability.counter < startupTime + effectTime) {
                    duringEffect(ability, entity);
                } else if (ability.counter == startupTime + effectTime) {
                    endEffect(ability, entity);
                } else if (ability.counter == startupTime + effectTime + endingLagTime) {
                    endExecution(abComp, ability);
                }

            }
        }

        //cannot use else, because rechargeTime may be 0
        if (ability.counter == startupTime + effectTime + endingLagTime + rechargeTime) {
            endRecharge(ability);
        }

        ability.setRequestExecution(false); //checking if a request is made each frame.
    }


    private void startExecution(AbilityComp abComp, Ability ability) {
        abComp.setOccupiedBy(ability);
        ability.counter = 0;
    }

    private void startEffect(Ability ability, int entity, AudioComp audioComp){
        ability.startEffect(wc, entity);

        //play start effect sound
        if (audioComp != null) {
            if (ability.getStartEffectSoundIndex() != -1) {
                audioComp.requestSound(ability.getStartEffectSoundIndex());
            }
        }
    }


    private void duringEffect(Ability ability, int entity ){
        ability.duringEffect(wc, entity);
    }

    private void endEffect(Ability ability, int entity){
        ability.endEffect(wc, entity);
    }

    private void endExecution(AbilityComp abComp, Ability ability) {
        ability.setRecharging(true);
        abComp.setOccupiedBy(null); //release abComp
    }

    private void endRecharge(Ability ability){
        ability.setRecharging(false);

    }



}
