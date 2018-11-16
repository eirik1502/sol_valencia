package game;

import engine.PositionComp;
import engine.RotationComp;
import engine.WorldContainer;
import engine.audio.AudioComp;
import engine.audio.Sound;
import engine.combat.DamagerComp;
import engine.combat.abilities.HitboxComp;
import engine.combat.abilities.ProjectileComp;
import engine.graphics.*;
import engine.physics.Circle;
import engine.physics.CollisionComp;
import engine.physics.PhysicsComp;
import engine.visualEffect.VisualEffectComp;
import engine.visualEffect.VisualEffectUtils;

/**
 * Created by eirik on 05.07.2017.
 */
public class ProjectileUtils {

    private static float projectileDepth = 1.5f;


    public static int allocateSinglecolorProjectileAbility(WorldContainer wc, float radius, float[] color, Sound onHitSound) {
        int p = allocateNonRenderableProjectileEntity(wc, radius, onHitSound);
        wc.addInactiveComponent(p, new ColoredMeshComp( ColoredMeshUtils.createCircleSinglecolor(radius, 12, color) ));
        return p;
    }

    public static int allocateTwocolorProjectileAbility(WorldContainer wc, float radius, Sound onHitSound) {
        int p = allocateNonRenderableProjectileEntity(wc, radius, onHitSound);
        wc.addInactiveComponent(p, new ColoredMeshComp( ColoredMeshUtils.createCircleTwocolor(radius, 12) ));
        return p;
    }
    public static int allocateImageProjectileEntity(WorldContainer wc, String imagePath, float radiusOnImage, float imageWidth, float imageHeight, float radius, Sound onHitSound) {
        float scale = radius/radiusOnImage;
        float width = imageWidth*scale;
        float height = imageHeight*scale;

        int p = allocateNonRenderableProjectileEntity(wc, radius, onHitSound);
        wc.addInactiveComponent(p, new TexturedMeshComp(TexturedMeshUtils.createRectangle(imagePath, width, height)) );
        wc.addInactiveComponent(p, new MeshCenterComp(width/2, height/2));

        return p;
    }

    public static int allocateNonRenderableProjectileEntity(WorldContainer wc, float radius, Sound onHitSound) {
        int b = wc.createEntity();

        wc.addComponent(b, new PositionComp(0,0, projectileDepth));
        wc.addInactiveComponent(b, new RotationComp());

        wc.addInactiveComponent(b, new PhysicsComp(20, 0.05f, 0.3f));
        wc.addInactiveComponent(b, new HitboxComp());
        wc.addInactiveComponent(b, new ProjectileComp());

        wc.addInactiveComponent(b, new DamagerComp()); //because of ability system

        wc.addInactiveComponent(b, new CollisionComp(new Circle(radius)));

        wc.addComponent(b, new VisualEffectComp(VisualEffectUtils.createOnHitEffect()));

        if (onHitSound != null) {
            wc.addComponent(b, new AudioComp(onHitSound));
        }

        return b;
    }
}
