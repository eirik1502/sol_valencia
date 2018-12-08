package engine.combat.abilities;

import engine.PositionComp;
import engine.RotationComp;
import engine.WorldContainer;
import engine.audio.AudioComp;
import engine.audio.Sound;
import engine.combat.DamagerComp;
import engine.physics.*;
import engine.visualEffect.VisualEffectComp;
import game.CharacterUtils;
import game.GameUtils;
import game.MeleeAbilityUtils;
import utils.maths.Vec2;

/**
 * Created by eirik on 19.06.2017.
 */
public class MeleeAbility extends Ability{


    private int hitboxEntity;

    private float relativeDistance;
    private float relativeAngle;



    public MeleeAbility(WorldContainer wc, int startEffectSoundIndex, int hitboxEntity, int startupTime, int activeHitboxTime, int endlagTime, int rechargeTime, float distance){
        super(wc, startEffectSoundIndex, startupTime, activeHitboxTime, endlagTime, rechargeTime);

        this.relativeDistance = distance;
        this.relativeAngle = 0;

        this.hitboxEntity = hitboxEntity;
    }
//    public MeleeAbility(WorldContainer wc){
//        this(wc, 5, 0.5f, new Circle(5), 0.0f, 0.0f, 10, 10, 10, 10);
//    }
    public void setDamagerValues(WorldContainer wc, float damage, float baseKnockback, float knockbackRatio, float knockbackPoint, boolean towardKnockbackPoint) {
        DamagerComp dmgrComp = (DamagerComp)wc.getInactiveComponent(hitboxEntity, DamagerComp.class);
        dmgrComp.setDamage(damage);
        dmgrComp.setBaseKnockback(baseKnockback);
        dmgrComp.setKnockbackRatio(knockbackRatio);
        dmgrComp.setKnockbackPoint(knockbackPoint);
        dmgrComp.setTowardPoint(towardKnockbackPoint);
    }


    float getRelativeDistance() {
        return relativeDistance;
    }
    float getRelativeAngle() {return relativeAngle;}

    int getHitboxEntity() {
        return hitboxEntity;
    }

    @Override
    public void startEffect(WorldContainer wc, int requestingEntity) {
        wc.activateEntity(hitboxEntity);

        PositionComp reqPosComp = wc.getComponent(requestingEntity, PositionComp.class);
        RotationComp reqRotComp = wc.getComponent(requestingEntity, RotationComp.class);
        //AudioComp reqAudioComp = (AudioComp)wc.getComponent(requestingEntity, AudioComp.class);

        RotationComp hbRotComp = wc.getComponent(hitboxEntity, RotationComp.class);
        HitboxComp hbHitbComp = wc.getComponent(hitboxEntity, HitboxComp.class);



        //set hitbox comp state
        hbHitbComp.reset();
        hbHitbComp.setOwner(requestingEntity);

        //set hitbox knockback direction
        float hitboxAngle = reqRotComp.getAngle() + relativeAngle;
        hbRotComp.setAngle(hitboxAngle);

        //set relative positiom
        positionHitbox(wc, requestingEntity);

    }

    @Override
    public void duringEffect(WorldContainer wc, int requestingEntity) {
        positionHitbox(wc, requestingEntity);
    }

    @Override
    public void endEffect(WorldContainer wc, int requestingEntity) {
        //deactivate hitbox
        deactivateHitbox(wc);
    }

    private void positionHitbox(WorldContainer wc, int requestingEntity) {
        PositionComp reqPosComp = (PositionComp)wc.getComponent(requestingEntity, PositionComp.class);
        RotationComp reqRotComp = (RotationComp)wc.getComponent(requestingEntity, RotationComp.class);

        PositionComp hbPosComp = (PositionComp)wc.getComponent(hitboxEntity, PositionComp.class);

        Vec2 relPos = Vec2.newLenDir(relativeDistance, reqRotComp.getAngle() + relativeAngle );
        hbPosComp.setPos( reqPosComp.getPos().add(relPos) );
    }


    private void deactivateHitbox(WorldContainer wc) {
        wc.deactivateEntity(hitboxEntity);
        wc.activateComponent(hitboxEntity, VisualEffectComp.class);
        wc.activateComponent(hitboxEntity, PositionComp.class);

        if (wc.hasComponent(hitboxEntity, AudioComp.class)) {
            wc.activateComponent(hitboxEntity, AudioComp.class);
        }
    }

}
