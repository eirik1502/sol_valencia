package engine.combat.abilities;

import engine.Sys;
import engine.TeamComp;
import engine.WorldContainer;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.physics.CollisionComp;
import engine.physics.CollisionCompIterator;
import engine.physics.CollisionData;

/**
 * Created by eirik on 27.06.2017.
 *
 * remove damager-damageable pairs that have already collided with a given hitbox, or the hitbox's owner
 */
public class HitboxResolutionSys implements Sys{


    private WorldContainer wc;

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        for (int entity : wc.getEntitiesWithComponentType(HitboxComp.class)) {

            HitboxComp hitbComp = (HitboxComp) wc.getComponent(entity, HitboxComp.class);
            CollisionComp collComp = (CollisionComp)wc.getComponent(entity, CollisionComp.class);

            updateHitboxEntity(entity, collComp, hitbComp);
        }
    }

    @Override
    public void terminate() {

    }

    public void updateHitboxEntity(int entity, CollisionComp collComp, HitboxComp hitbComp) {
        CollisionCompIterator collIt = collComp.collisionCompIterator();
        while(collIt.hasNext()) {
            CollisionData data = collIt.next();

            //if the collision is inactive, skip it
            if (!data.isActive()) continue;

            int otherEntity = collIt.getOtherEntity();

            //if other entity is the owner of the hitbox, remove collision
            if (hitbComp.getOwner() == otherEntity) {
                data.setActive(false);
            }

            //remove if hit someone on team
            else if (wc.hasComponent(otherEntity, TeamComp.class)){

                TeamComp hitboxTeamComp = (TeamComp) wc.getComponent(hitbComp.getOwner(), TeamComp.class);
                TeamComp otherTeamComp = (TeamComp) wc.getComponent(otherEntity, TeamComp.class);
                if (hitboxTeamComp.team == otherTeamComp.team) {
                    data.setActive(false);
                }
            }

            //if the other entity has already collided with the hitbox, remove collision, else add the interaction
            hitbComp.streamEntityInteractions().forEach(registeredEntity -> {
                if (registeredEntity == otherEntity) {
                    data.setActive(false);
                }
            });

            //add other to the set of interactions
            hitbComp.addInteraction(otherEntity);
        }
    }
}
