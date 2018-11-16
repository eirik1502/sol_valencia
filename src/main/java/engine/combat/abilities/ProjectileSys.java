package engine.combat.abilities;

import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;
import engine.audio.AudioComp;
import engine.combat.DamagerComp;
import engine.visualEffect.VisualEffect;
import engine.visualEffect.VisualEffectComp;

import java.util.LinkedList;

/**
 * Created by eirik on 28.06.2017.
 */
public class ProjectileSys implements Sys {

    private WorldContainer wc;

    private LinkedList<Integer> deactivateEntities = new LinkedList<>();

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        wc.entitiesOfComponentTypeStream(ProjectileComp.class).forEach(entity -> {
            updateProjectile(entity);
        });

        deactivateEntities();
    }

    private void deactivateEntities() {
        while(!deactivateEntities.isEmpty()) {
            deactivateProj(deactivateEntities.poll());
        }
    }

    private void updateProjectile(int projEntity){
        ProjectileComp projComp = (ProjectileComp) wc.getComponent(projEntity, ProjectileComp.class);
        DamagerComp dmgerComp = (DamagerComp) wc.getComponent(projEntity, DamagerComp.class);

        //deactivate projectile it is told to
        if (projComp.isShouldDeactivateFlag()) {
            projComp.resetShouldDeactivateFlag();

            deactivateEntities.add(projEntity);
            return;
        }

        //deactivate projectile if it delt damage
        if (dmgerComp.hasDeltDamage()) {
            projComp.setShouldDeactivateFlag();
        }

        //decrement lifetime and deactivate if below zero
        projComp.decrementLifeTime();
        //System.out.println("Projectile lifetime: "+projComp.getLifeTime());
        if (projComp.getLifeTime() <= 1) {
            projComp.setShouldDeactivateFlag();
        }
    }

    private void deactivateProj(int projEntity) {
        wc.deactivateEntity(projEntity);
        wc.activateComponent(projEntity, VisualEffectComp.class);
        wc.activateComponent(projEntity, AudioComp.class);
        wc.activateComponent(projEntity, PositionComp.class);

    }

    @Override
    public void terminate() {

    }
}
