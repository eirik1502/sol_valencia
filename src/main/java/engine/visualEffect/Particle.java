package engine.visualEffect;

import engine.graphics.ColoredMesh;
import utils.maths.Vec2;

/**
 * Created by eirik on 06.07.2017.
 */
public class Particle {

    private boolean active= false;

    private float lifetime;

    private Vec2 pos;
    private Vec2 velocity;

    //private ParticleShape shape;
    private ColoredMesh mesh;


    public Particle(ColoredMesh mesh) {
        this.mesh = mesh;

    }

    public void setPos(Vec2 pos) {
        this.pos = pos;
    }
    public void setVelocity(Vec2 vel) {
        this.velocity = vel;
    }
    public void setLifetime(float lifetime) {
        this.lifetime = lifetime;
    }


    public boolean isActive() {
        return active;
    }
    public void activate() {
        active = true;
    }
    public void deactivate() {
        active = false;
    }

    public Vec2 getVelocity() {
        return velocity;
    }
    public Vec2 getPos() {
        return pos;
    }
    public void addPos(Vec2 newpos) {
        pos = pos.add(newpos);
    }

    public void decrementLifetime() {
        lifetime--;
    }
    public float getLifetime() {
        return lifetime;
    }

    public ColoredMesh getMesh() {
        return mesh;
    }
}
