package engine.visualEffect;

import engine.graphics.ColoredMeshUtils;
import utils.maths.M;
import utils.maths.Vec2;
import utils.maths.Vec4;

/**
 * Created by eirik on 06.07.2017.
 */
public class VisualEffectUtils {

    public static VisualEffect createOnHitEffect() {

        int particleCount = 16*2;
        float lifetime = 20;
        float lifetimeMin = 5;

        Vec4 colorMax = new Vec4(1f, 0, 0, 1);
        Vec4 colorMin = new Vec4(0.8f, 0.2f, 0.2f, 1);

        float radiusMax = 10;
        float radiusMin = 5;


        float speedMax = 8;
        float speedMin = -4;
        float angleCenter = 0;
        float angleSpread = 2*M.PI;

        return new VisualEffect(particleCount, colorMax, colorMin, radiusMax, radiusMin, lifetime, lifetimeMin, speedMin, speedMax, angleCenter, angleSpread);
    }

    public static VisualEffect createFalloutEffect() {

        int particleCount = 64;
        float lifetime = 40;
        float lifetimeMin = 20;

        Vec4 colorMax = new Vec4(0.3f, 0.3f, 1f, 1);
        Vec4 colorMin = new Vec4(0f, 0f, 1f, 1);

        float radiusMax = 32;
        float radiusMin = 16;

        float speedMax = 16;
        float speedMin = 8;
        float angleCenter = 0;
        float angleSpread = 2*M.PI;

        return new VisualEffect(particleCount, colorMax, colorMin, radiusMax, radiusMin, lifetime, lifetimeMin, speedMin, speedMax, angleCenter, angleSpread);
    }
}
