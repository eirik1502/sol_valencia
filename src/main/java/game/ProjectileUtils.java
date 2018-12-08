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

    /**
     * Adds all components needed for the projectile to work.
     * Without media; graphics, sunds, visua effects and so.
     *
     * @param wc
     * @param entity
     * @param radius
     */
    public static void addProjectileCoreComps(WorldContainer wc, int entity, float radius) {
        wc.addComponent(entity, new PositionComp(0,0, projectileDepth));
        wc.addInactiveComponent(entity, new RotationComp());

        wc.addInactiveComponent(entity, new PhysicsComp(20, 0.05f, 0.3f));
        wc.addInactiveComponent(entity, new HitboxComp());
        wc.addInactiveComponent(entity, new ProjectileComp());

        wc.addInactiveComponent(entity, new DamagerComp()); //because of ability system

        wc.addInactiveComponent(entity, new CollisionComp(new Circle(radius)));
    }

    /**
     * Adds all projectile media, with the graphical beeing a single colored circle.
     * If color is set to null, a two colored mesh is created with random colors.
     *
     * @param wc
     * @param entity
     * @param radius
     * @param color
     * @param sndHit
     */
    public static void addProjectilesMediaColorComps(
            WorldContainer wc, int entity,
            float radius,
            float[] color,
            Sound sndHit) {

        ProjectileUtils.addProjectileGraphicsColorComps(wc, entity, radius, color);
        ProjectileUtils.addProjectileVisualEffectComps(wc, entity);
        ProjectileUtils.addProjectileAudioComps(wc, entity, sndHit);
    }

    /**
     * Adds all projectile media, with the graphics beeing a textured circle
     *
     * @param wc
     * @param entity
     * @param radius
     * @param imagePath
     * @param radiusOnImage
     * @param imageWidth
     * @param imageHeight
     * @param sndHit
     */
    public static void addProjectilesMediaTextureComps(
            WorldContainer wc, int entity,
            float radius,
            String imagePath, float radiusOnImage, float imageWidth, float imageHeight,
            Sound sndHit) {

        ProjectileUtils.addProjectileGraphicsImageComps(wc, entity, imagePath, radiusOnImage, imageWidth, imageHeight, radius);
        ProjectileUtils.addProjectileVisualEffectComps(wc, entity);
        ProjectileUtils.addProjectileAudioComps(wc, entity, sndHit);
    }

    public static void addProjectileGraphicsImageComps(WorldContainer wc, int entity, String imagePath, float radiusOnImage, float imageWidth, float imageHeight, float radius) {
        float scale = radius/radiusOnImage;
        float width = imageWidth*scale;
        float height = imageHeight*scale;

        wc.addInactiveComponent(entity, new TexturedMeshComp(TexturedMeshUtils.createRectangle(imagePath, width, height)) );
        wc.addInactiveComponent(entity, new MeshCenterComp(width/2, height/2));
    }

    /**
     * If a color is spesified, use that color, if it is null, chose two random colors
     *
     * @param wc
     * @param entity
     * @param radius
     * @param color
     */
    public static void addProjectileGraphicsColorComps(WorldContainer wc, int entity, float radius, float[] color) {
        if (color != null) {
            wc.addInactiveComponent(entity, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(radius, 12, color)));
        } else {
            wc.addInactiveComponent(entity, new ColoredMeshComp( ColoredMeshUtils.createCircleTwocolor(radius, 12) ));
        }
    }
    public static void addProjectileVisualEffectComps(WorldContainer wc, int entity) {
        wc.addComponent(entity, new VisualEffectComp(VisualEffectUtils.createOnHitEffect()));
    }
    public static void addProjectileAudioComps(WorldContainer wc, int entity, Sound onHitSound) {
        wc.addComponent(entity, new AudioComp(onHitSound));
    }



    @Deprecated
    public static int allocateSinglecolorProjectileAbility(WorldContainer wc, float radius, float[] color, Sound onHitSound) {
        int p = allocateNonRenderableProjectileEntity(wc, radius, onHitSound);
        wc.addInactiveComponent(p, new ColoredMeshComp( ColoredMeshUtils.createCircleSinglecolor(radius, 12, color) ));
        return p;
    }
    @Deprecated
    public static int allocateTwocolorProjectileAbility(WorldContainer wc, float radius, Sound onHitSound) {
        int p = allocateNonRenderableProjectileEntity(wc, radius, onHitSound);
        wc.addInactiveComponent(p, new ColoredMeshComp( ColoredMeshUtils.createCircleTwocolor(radius, 12) ));
        return p;
    }
    @Deprecated
    public static int allocateImageProjectileEntity(WorldContainer wc, String imagePath, float radiusOnImage, float imageWidth, float imageHeight, float radius, Sound onHitSound) {
        float scale = radius/radiusOnImage;
        float width = imageWidth*scale;
        float height = imageHeight*scale;

        int p = allocateNonRenderableProjectileEntity(wc, radius, onHitSound);
        wc.addInactiveComponent(p, new TexturedMeshComp(TexturedMeshUtils.createRectangle(imagePath, width, height)) );
        wc.addInactiveComponent(p, new MeshCenterComp(width/2, height/2));

        return p;
    }
    @Deprecated
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
