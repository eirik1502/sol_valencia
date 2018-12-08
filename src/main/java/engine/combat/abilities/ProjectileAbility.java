package engine.combat.abilities;

import engine.PositionComp;
import engine.RotationComp;
import engine.WorldContainer;
import engine.audio.AudioComp;
import engine.combat.DamagerComp;
import engine.physics.Circle;
import engine.physics.PhysicsComp;
import engine.physics.Shape;
import engine.visualEffect.VisualEffectComp;
import game.GameUtils;
import utils.maths.Vec2;

/**
 * Created by eirik on 19.06.2017.
 */
public class ProjectileAbility extends Ability {


    private final float startDistance = 64f;

    private float projStartSpeed;
    private int projLifeTime;
    private float knockbackAngle;

    private int projEntity;



    public ProjectileAbility(WorldContainer wc, int startEffectSoundIndex, int projectileEntity, int startupTime, int endlagTime, int rechargeTime,
                             float projStartSpeed, int projLifeTime) {
        super(wc, startEffectSoundIndex, startupTime, 0, endlagTime, rechargeTime);

        this.projEntity = projectileEntity;

        this.projStartSpeed = projStartSpeed;
        this.projLifeTime = projLifeTime;
        this.knockbackAngle = 0; //knockback point on movement line is now used
    }

    public void setDamagerValues(WorldContainer wc, float damage, float baseKnockback, float knockbackRatio, float knockbackPoint, boolean towardKnockbackPoint) {
        DamagerComp dmgrComp = (DamagerComp)wc.getInactiveComponent(projEntity, DamagerComp.class);
        dmgrComp.setDamage(damage);
        dmgrComp.setBaseKnockback(baseKnockback);
        dmgrComp.setKnockbackRatio(knockbackRatio);
        dmgrComp.setKnockbackPoint(knockbackPoint);
        dmgrComp.setTowardPoint(towardKnockbackPoint);
    }

    @Override
    void startEffect(WorldContainer wc, int requestingEntity) {
        wc.activateEntity(projEntity);

        PositionComp reqPosComp = wc.getComponent(requestingEntity, PositionComp.class);
        RotationComp reqRotComp = wc.getComponent(requestingEntity, RotationComp.class);

        PositionComp projPosComp = wc.getComponent(projEntity, PositionComp.class);
        RotationComp projRotComp = wc.getComponent(projEntity, RotationComp.class);
        PhysicsComp projPhysComp = wc.getComponent(projEntity, PhysicsComp.class);
        HitboxComp projHitbComp = wc.getComponent(projEntity, HitboxComp.class);
        ProjectileComp projProjComp = wc.getComponent(projEntity, ProjectileComp.class);


        //set abilityId in projectile entity
        projProjComp.setAbilityId(getAbilityId());

        float velDir = reqRotComp.getAngle();

        //set hitbox comp state
        projHitbComp.reset();
        projHitbComp.setOwner(requestingEntity);

        //set initial position
        Vec2 relPos = Vec2.newLenDir(startDistance, velDir );
        projPosComp.setPos( reqPosComp.getPos().add(relPos) );

        //set initial velocity
        Vec2 startVel = Vec2.newLenDir(projStartSpeed, velDir);
        projPhysComp.reset();
        projPhysComp.addImpulse(startVel);

        //set proj lifetime
        projProjComp.setLifeTime(projLifeTime);

        //set direction to movement
        projRotComp.setAngle(velDir);
    }

    @Override
    void duringEffect(WorldContainer wc, int requestingEntity) {

    }

    @Override
    void endEffect(WorldContainer wc, int requestingEntity) {

    }

}
