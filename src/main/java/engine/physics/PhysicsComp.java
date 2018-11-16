package engine.physics;

import engine.Component;
import utils.maths.Vec2;

/**
 * Created by haraldvinje on 13-Jun-17.
 */
public class PhysicsComp implements Component {


    public final static float DEFAULT_MASS = 50.0f;
    public final static float DEFAULT_FRICTION_CONST = 0.5f;
    public final static float DEFAULT_ELASTICITY = 0.7f;
    public static final int DEFAULT_FRICTION_MODEL = PhysicsUtil.FRICTION_MODEL_COULOMB;


    private Vec2 velocity = new Vec2();
    private Vec2 acceleration = new Vec2();
    private Vec2 impulse = new Vec2();

    private int frictionModel;
    private float invMass = 1f;
    private float frictionConst = 0.5f;
    private float elasticity = 1;



    public PhysicsComp(float mass, float frictionConst, float elasticity, int frictionModel) {
        setMass(mass);
        setFrictionConstant(frictionConst);
        setElasticity(elasticity);
        setFrictionModel(frictionModel);
    }
    public PhysicsComp(float mass, float frictionConst, float elasticity) {
        this(mass,frictionConst, elasticity, DEFAULT_FRICTION_MODEL);
    }
    public PhysicsComp(float mass, float frictionConst) {
        this(mass, frictionConst, DEFAULT_ELASTICITY, DEFAULT_FRICTION_MODEL);
    }
    public PhysicsComp(float mass) {
        this(mass, DEFAULT_FRICTION_CONST, DEFAULT_ELASTICITY, DEFAULT_FRICTION_MODEL);
    }
    public PhysicsComp() {
        this(DEFAULT_MASS, DEFAULT_FRICTION_CONST, DEFAULT_ELASTICITY, DEFAULT_FRICTION_MODEL);
    }


    public Vec2 getVelocity(){
            return this.velocity;
        }
    public void addVelocity(Vec2 velocity) {
        this.velocity = this.velocity.add(velocity);
    }
    public void resetVelocity() {
        velocity.setZero();
    }

    public Vec2 getImpulse() {
        return impulse;
    }
    public void addImpulse(Vec2 impulse) {
        this.impulse = this.impulse.add(impulse);
    }
    public void resetImpulse() {
        impulse.setZero();
    }


    public Vec2 getAcceleration() {
            return acceleration;
        }
    public void addAcceleration(Vec2 acceleration){
        this.acceleration = this.acceleration.add(acceleration);
    }
    public void resetAcceleration() {
        this.acceleration.setZero();
    }


    public void setFrictionConstant(float frictionConstant){
        this.frictionConst = frictionConstant;
    }
    public float getFrictionConst(){
            return this.frictionConst;
        }

    public float getMass() {
        if (invMass == 0) {
            return 0;
        }
        return 1/invMass;
    }
    public float getInvMass() {
        return invMass;
    }
    public void setMass(float m) {
        if (m == 0)
            invMass = 0;
        else
            invMass = 1.0f/m;
    }
    public float getElasticity() {
        return elasticity;
    }
    public void setElasticity(float elasticity) {
        this.elasticity = elasticity;
    }

    public int getFrictionModel() {
        return frictionModel;
    }
    public void setFrictionModel(int frictionModel) {
        this.frictionModel = frictionModel;
    }


    public void reset() {
        resetAcceleration();
        resetVelocity();
        resetImpulse();
    }

    void setVelocityLen(float length) {
        velocity.setLength(length);
    }
}
