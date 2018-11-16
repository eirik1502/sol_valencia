package engine.physics;

import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;
import engine.combat.DamageableComp;
import javafx.geometry.Pos;
import utils.maths.Vec2;

import java.util.Set;

/**
 * Created by haraldvinje on 19-Jun-17.
 */
public class HoleResolutionSys implements Sys {
    private WorldContainer worldContainer;

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.worldContainer = wc;
    }

    @Override
    public void update() {

        worldContainer.entitiesOfComponentTypeStream(AffectedByHoleComp.class).forEach( affholeEntity -> {
            //reset affected flag
            AffectedByHoleComp affholeComp = (AffectedByHoleComp) worldContainer.getComponent(affholeEntity, AffectedByHoleComp.class);
            affholeComp.resetHoleAffectedFlag();
        });

        Set<Integer> holeEntities = worldContainer.getEntitiesWithComponentType(HoleComp.class);
        int otherEntity = -1;
        for (int entity: holeEntities){
            CollisionComp cc1 = (CollisionComp) worldContainer.getComponent(entity, CollisionComp.class);

            for (CollisionData data: cc1.getPrimaryCollisionDataList()){
                //resolve
                otherEntity = data.getEntity2();
                if (worldContainer.hasComponent(otherEntity, AffectedByHoleComp.class)){
                    onCollision(data, otherEntity);
                }
                data.setActive(false);

            }
            for (CollisionData data: cc1.getSecondaryCollisionDataList()){
                //resolve

                otherEntity = data.getEntity1();
                if (worldContainer.hasComponent(otherEntity, AffectedByHoleComp.class)) {
                    onCollision(data, otherEntity);
                }
                data.setActive(false);

            }
        }
    }

    @Override
    public void terminate() {

    }

    private void onCollision(CollisionData data, int affectedEntity) {
        AffectedByHoleComp affholeComp = (AffectedByHoleComp) worldContainer.getComponent(affectedEntity, AffectedByHoleComp.class);

        affholeComp.setHoleAffectedFlag();

//        respawnEntities(data, entity);
    }

//    private void respawnEntities(CollisionData data, int entity){
//        //TODO: Make this method much more general
//        PositionComp posComp = (PositionComp) worldContainer.getComponent(entity, PositionComp.class);
//
//        posComp.setPos(new Vec2(300,450));
//
//        ((DamageableComp)worldContainer.getComponent(entity, DamageableComp.class)).reset();
//
//    }

}
