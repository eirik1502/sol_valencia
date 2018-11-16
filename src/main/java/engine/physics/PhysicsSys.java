package engine.physics;

import com.sun.javafx.collections.VetoableListDecorator;
import engine.Component;
import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;
import utils.maths.Vec2;
import javafx.geometry.Pos;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by haraldvinje on 15-Jun-17.
 */
public class PhysicsSys implements Sys {

    private static float DELTA_TIME = 1.0f / 60.0f;
    private static float MAX_VELOCITY = 30f*60f;

    private WorldContainer worldContainer;
    private Set<Integer> physicsEntities;

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.worldContainer = wc;
    }

    @Override
    public void update() {
        this.physicsEntities = worldContainer.getEntitiesWithComponentType(PhysicsComp.class);

//        applyFriction();            //adding friction acceleration vector
//        updateVelocities();         //accelerating
//        updatePositions();
//        resetAcceleration();

        for (int entity: physicsEntities){
            PhysicsComp physicsComp = (PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class);
            PositionComp posComp = (PositionComp) worldContainer.getComponent(entity, PositionComp.class);

            //apply friction
            physicsComp.addAcceleration(calculateFriction(physicsComp));

            //apply acceleration
            Vec2 deltaAcceleration = physicsComp.getAcceleration().scale(DELTA_TIME);
            physicsComp.addVelocity( deltaAcceleration.add( physicsComp.getImpulse()) );


            //check if velocity is to great. If so, apply greatest velocity, but dont change the velocity
            Vec2 correctedVelocity = new Vec2(physicsComp.getVelocity());
            if (correctedVelocity.getLengthSquared() > MAX_VELOCITY*MAX_VELOCITY) {
                //System.out.println("Hit max velocity");
                correctedVelocity.setLength(MAX_VELOCITY);
            }

            //apply velocity
            Vec2 deltaCorrectedVelocity = correctedVelocity.scale(DELTA_TIME);
            posComp.addPos(deltaCorrectedVelocity);


            //reset frame-based values
            physicsComp.resetAcceleration();
            physicsComp.resetImpulse();
        }
    }

    @Override
    public void terminate() {

    }

//    private void applyFriction() {
//        for (int entity: physicsEntities){
//            PhysicsComp physicsComp = (PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class);
//            Vec2 frictionVector = ((PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class)).getVelocity().negative();
//            float frictionConst = physicsComp.getFrictionConst();
//            frictionVector = frictionVector.scale(physicsComp.getVelocity().getLength()*frictionConst);
//
//            //physicsComp.addVelocity(frictionVector);
//            physicsComp.addAcceleration(physicsComp.getVelocity().scale(0.1f).negative());
//        }
//    }
//
//    private void updateVelocities(){
//        for (int entity: physicsEntities){
//            PhysicsComp physicsComp = (PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class);
//            Vec2 acceleration = ((PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class)).getAcceleration();
//            physicsComp.addVelocity(acceleration);
//
//
//        }
//    }
//
//
//
//    private void updatePositions(){
//        for (int entity: physicsEntities){
//            PositionComp posComp = (PositionComp) worldContainer.getComponent(entity, PositionComp.class);
//            Vec2 velocity = ((PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class)).getVelocity();
//            posComp.addPos(velocity);
//
//        }
//    }

//    private void resetAcceleration() {
//        for (int entity: physicsEntities){
//            PhysicsComp physicsComp = (PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class);
//            physicsComp.resetAcceleration();
//        }
//    }

    private Vec2 calculateFriction(PhysicsComp pc) {
        Vec2 fricAccel = new Vec2();

        int frictionModel = pc.getFrictionModel();
        float frictionConst = pc.getFrictionConst();
        Vec2 velocity = pc.getVelocity();

        if (frictionModel == PhysicsUtil.FRICTION_MODEL_COULOMB) {
            fricAccel = velocity.normalize().scale(PhysicsUtil.gravityAcceleration*frictionConst).negative();
        }
        else if (frictionModel == PhysicsUtil.FRICTION_MODEL_VICIOUS) {
            fricAccel = velocity.scale(frictionConst).negative();
        }
        return fricAccel;
    }
}
