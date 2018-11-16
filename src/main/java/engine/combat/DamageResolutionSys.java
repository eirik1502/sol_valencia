package engine.combat;

import engine.PositionComp;
import engine.RotationComp;
import engine.Sys;
import engine.WorldContainer;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.ProjectileComp;
import engine.physics.CollisionComp;
import engine.physics.CollisionCompIterator;
import engine.physics.CollisionData;
import engine.physics.PhysicsComp;
import engine.visualEffect.VisualEffectComp;
import utils.maths.M;
import utils.maths.TrigUtils;
import utils.maths.Vec2;

/**
 * Created by eirik on 19.06.2017.
 */
public class DamageResolutionSys implements Sys {


    private WorldContainer wc;



    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        //reset deltDamage flags in damager
        for (int entity : wc.getEntitiesWithComponentType(DamagerComp.class)) {
            DamagerComp dmgerComp = (DamagerComp) wc.getComponent(entity, DamagerComp.class);

            dmgerComp.resetDeltDamage();
        }

        for (int entity : wc.getEntitiesWithComponentType(DamageableComp.class)) {
            //reset flags
            DamageableComp dmgableComp = (DamageableComp) wc.getComponent(entity, DamageableComp.class);

            //reset one-frame data
            dmgableComp.resetFrame();

            //update damageable with respect to damagers
            CollisionComp collComp = (CollisionComp) wc.getComponent(entity, CollisionComp.class);
            updateDamageableEntity(entity, collComp);
        }

    }

    @Override
    public void terminate() {

    }

    private void updateDamageableEntity(int entity, CollisionComp collComp) {

        //decrement stun timer if stunned
        updateStunTimer(entity);

        //check for each colliding object if it is a damager
        CollisionCompIterator collIt = collComp.collisionCompIterator();

        while(collIt.hasNext()) {
            CollisionData data = collIt.next();

            if (!data.isActive()) continue;


            int damagerEntity = collIt.getOtherEntity();

            if (wc.hasComponent(damagerEntity, DamagerComp.class)) {
                //System.out.println("Taking damage, bullet: "+collIt.getOtherEntity() +" victim: " + collIt.getSelfEntity());
                applyDamage(wc, entity, collIt.getOtherEntity());
            }

        }
    }

    private void updateStunTimer(int dmgableEntity) {
        DamageableComp dmgablComp = (DamageableComp)wc.getComponent(dmgableEntity, DamageableComp.class);

        if (dmgablComp.isStunned()) {
            dmgablComp.decrementStunTimer();
        }
    }

    public static void applyDamage(WorldContainer wc, int damaged, int damager) {
        DamagerComp dmgerComp = (DamagerComp)wc.getComponent(damager, DamagerComp.class);
        PositionComp dmgerPosComp = (PositionComp)wc.getComponent(damager, PositionComp.class);
        RotationComp dmgerRotComp = (RotationComp)wc.getComponent(damager, RotationComp.class);
        VisualEffectComp dmgerViseffComp = (VisualEffectComp)wc.getComponent(damager, VisualEffectComp.class);


        DamageableComp dmgablComp = (DamageableComp)wc.getComponent(damaged, DamageableComp.class);
        PositionComp dmgablPosComp = (PositionComp)wc.getComponent(damaged, PositionComp.class);
        PhysicsComp dmgablPhysComp = (PhysicsComp)wc.getComponent(damaged, PhysicsComp.class);


        //reset physics for damageable. Should happen before naturalResolution
        dmgablPhysComp.reset();

        //apply damage
        float damage = dmgerComp.getDamage();
        dmgablComp.applyDamage(damage);

        //apply knockback
        float knockbackLen = M.pow(dmgablComp.getDamage() * dmgerComp.getKnockbackRatio(), 1 ) + dmgerComp.getBaseKnockback();

        Vec2 knockbackPoint = dmgerPosComp.getPos().add( Vec2.newLenDir(dmgerComp.getKnockbackPoint(), dmgerRotComp.getAngle()) );
        Vec2 damagedPos = dmgablPosComp.getPos();
        Vec2 knockbackDir = dmgerComp.isTowardPoint()?
                TrigUtils.pointDirectionVec(damagedPos, knockbackPoint) :
                TrigUtils.pointDirectionVec(knockbackPoint, damagedPos);
        Vec2 knockback = knockbackDir.scale(knockbackLen);

        dmgablPhysComp.addImpulse( knockback );


        //apply hitstun
        int stunDuration = (int)(knockbackLen/60f);
        dmgablComp.setStunTimer(stunDuration);

        //apply visual effect
//        dmgerViseffComp.startEffect(0, dmgablPosComp.getPos());


        //set deltDamage and interrupt flags
        dmgerComp.deltDamage();
        dmgablComp.interrupt();


        //add a data object about the interraction to the damaged entity. To be read by feks network
        HitData data = new HitData(damager, damaged, damage, knockback);
        dmgablComp.addHitData(data);
    }
}
