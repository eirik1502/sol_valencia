package engine.visualEffect;

import engine.graphics.ColoredMesh;
import engine.graphics.ColoredMeshUtils;
import utils.maths.M;
import utils.maths.Vec2;
import utils.maths.Vec4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by eirik on 06.07.2017.
 */
public class VisualEffect {

    private boolean active = false;

    private int attrParticleCount;

    private float attrLifetime;
    private float attrEffectAngle;


    //attributes per particle
    private Vec4 attrColorMax, attrColorMin;
    private float attrRadiusMin, attrRadiusMax;
    private float attrLifetimeMin;
    private float attrSpeedMin, attrSpeedMax;
    private float attrAngleCenter, attrAngleSpread;



    private List<Particle> particles = new ArrayList<>();


    private float lifetime;


    public VisualEffect(int particleCount, Vec4 colorMax, Vec4 colorMin, float radiusMax, float radiusMin, float lifetime, float lifetimeMin, float speedMin, float speedMax, float angleCenter, float angleSpread) {
        this.attrColorMax = colorMax;
        this.attrColorMin = colorMin;

        this.attrRadiusMax = radiusMax;
        this.attrRadiusMin = radiusMin;

        this.attrLifetime = lifetime;
        this.attrLifetimeMin = lifetimeMin;
        this.attrParticleCount = particleCount;

        this.attrSpeedMin = speedMin;
        this.attrSpeedMax = speedMax;

        this.attrAngleCenter = angleCenter;
        this.attrAngleSpread = angleSpread;

        createParticles(particleCount);

        resetThis();
    }

    private void createParticles(int count) {
        for (int i = 0; i < count; i++) {
            float r = M.random();
            float[] color = {
                    attrColorMin.x + r*(attrColorMax.x - attrColorMin.x),
                    attrColorMin.y + r*(attrColorMax.y - attrColorMin.y),
                    attrColorMin.z + r*(attrColorMax.z - attrColorMin.z)};
            float radius = attrRadiusMin + M.random() * (attrRadiusMax - attrRadiusMin);
            ColoredMesh mesh = ColoredMeshUtils.createCircleSinglecolor(radius, 8 + (int)(radius/10), color);

            Particle p = new Particle(mesh);
            particles.add(p);
        }
    }

    private void startParticles(Vec2 pos) {
        for (Particle p : particles) {
            p.activate();

            p.setLifetime(attrLifetime - M.random() * (attrLifetime-attrLifetimeMin));

            p.setPos(pos);
            Vec2 vel = Vec2.newLenDir((attrSpeedMax-attrSpeedMin)* M.random()+attrSpeedMin,
                    attrAngleCenter + attrAngleSpread*(M.random() - 0.5f) );

            p.setVelocity(vel);
        }
    }

    public void startEffect(Vec2 pos) {
        resetThis();
        startParticles(pos);
        active = true;
        //System.out.println("Starting effect, particle count="+particles.size());
    }



    public Stream<Particle> allParticleStream() {
        return particles.stream();
    }
    public Stream<Particle> activeParticleStream() {
        return particles.stream().filter(p -> p.isActive());
    }

    public boolean isActive() {
        return active;
    }
    public void decrementLifetime() {
        lifetime--;
    }
    public float getLifetime() {
        return lifetime;
    }
    public void endEffect() {
        activeParticleStream().forEach(p -> p.deactivate());
        active = false;
    }


//    private void reset() {
////
//        resetThis();
//    }

    private void resetThis() {
        lifetime = attrLifetime;
    }
}
