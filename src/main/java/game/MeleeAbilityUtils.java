package game;

import engine.PositionComp;
import engine.RotationComp;
import engine.WorldContainer;
import engine.audio.AudioComp;
import engine.audio.Sound;
import engine.combat.DamagerComp;
import engine.combat.abilities.HitboxComp;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.physics.Circle;
import engine.physics.CollisionComp;
import engine.visualEffect.VisualEffectComp;
import engine.visualEffect.VisualEffectUtils;

/**
 * Created by eirik on 08.12.2018.
 */
public class MeleeAbilityUtils {
    private static float hitboxDepth = -0.1f;


    public static int allocateHitboxEntity(WorldContainer wc, Circle shape, Sound onHitSound, boolean media){
        int e = wc.createEntity("melee hitbox");

        wc.addComponent(e, new PositionComp(0, 0, hitboxDepth));
        wc.addInactiveComponent(e, new RotationComp());

        //wc.addInactiveComponent(e, new PhysicsComp());
        wc.addInactiveComponent(e, new HitboxComp());
        wc.addInactiveComponent(e, new DamagerComp());
        wc.addInactiveComponent(e, new CollisionComp(shape));

        if (media) {
            float[] redColor = {1.0f, 0f,0f};
            wc.addInactiveComponent(e, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(shape.getRadius(), 16, redColor)) );
            wc.addComponent(e, new VisualEffectComp(VisualEffectUtils.createOnHitEffect()));

            if (onHitSound != null) {
                wc.addComponent(e, new AudioComp(onHitSound));
            }
        }

        return e;
    }
}
