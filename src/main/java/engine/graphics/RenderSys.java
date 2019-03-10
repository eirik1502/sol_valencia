package engine.graphics;

import com.sun.prism.TextureMap;
import engine.PositionComp;
import engine.RotationComp;
import engine.Sys;
import engine.WorldContainer;
import engine.combat.abilities.ProjectileComp;
import engine.graphics.text.*;
import engine.graphics_module.GraphicsModule;
import engine.visualEffect.VisualEffect;
import engine.visualEffect.VisualEffectComp;
import engine.visualEffect.VisualEffectSys;
import engine.window.Window;
import org.lwjgl.opengl.GL11;
import utils.maths.Mat4;
import utils.maths.Vec2;
import utils.maths.Vec3;
import engine.graphics.view_.View;
import utils.maths.Vec4;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by eirik on 14.06.2017.
 */
public class RenderSys implements Sys {



//    private static List<TextMeshComp> texts = new ArrayList<>();
//    public static void addText(TextMeshComp text) {
//        texts.add(text);
//    }

    private Window window;
    private ColorShader colorShader;
    private TextureShader textureShader;
    private TextShader textShader;


    private WorldContainer wc;


    public RenderSys() {
        this(null);
    }

    public RenderSys(Window window) {
        this.window = window;
        colorShader = new ColorShader();
        textureShader = new TextureShader();
        textShader = new TextShader();

    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
        this.window = wc.getGame().getModule(GraphicsModule.class).getWindow();
    }


    public void update() {
        //clear screen
        glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);


        //get view propreties
        View view = wc.getView();

        Mat4 viewTransform = view.getViewTransform();
        Mat4 projectionTransform = view.getProjectionTransform();



        colorShader.bind();
        colorShader.setLightPoint(new Vec3(500, 500, -5000f));
        textureShader.bind();
        textureShader.setLightPoint(new Vec3(500, 500, -5000f));


        //draw colored meshes in the world
        wc.entitiesOfComponentTypeStream(ColoredMeshComp.class).forEach(entity -> {
            ColoredMeshComp coloredMeshComp = (ColoredMeshComp)wc.getComponent(entity, ColoredMeshComp.class);
            renderColoredMesh(entity, coloredMeshComp.getMesh(), viewTransform, projectionTransform);

        });

        //draw textured meshes in the world
        //not projectiles
        wc.entitiesOfComponentTypeStream(TexturedMeshComp.class).forEach( entity -> {
            if ( !wc.hasComponent(entity, ProjectileComp.class)) {
                TexturedMeshComp texturedMeshComp = (TexturedMeshComp) wc.getComponent(entity, TexturedMeshComp.class);
                renderTexturedMesh(entity, texturedMeshComp.getMesh(), viewTransform, projectionTransform);
            }
        });
        //projectiles
        wc.entitiesOfComponentTypeStream(TexturedMeshComp.class).forEach( entity -> {
            if ( wc.hasComponent(entity, ProjectileComp.class)) {
                TexturedMeshComp texturedMeshComp = (TexturedMeshComp) wc.getComponent(entity, TexturedMeshComp.class);
                renderTexturedMesh(entity, texturedMeshComp.getMesh(), viewTransform, projectionTransform);
            }
        });

        //draw text meshes in the world
        wc.entitiesOfComponentTypeStream(TextMeshComp.class).forEach( entity -> {
            TextMeshComp textMeshComp = (TextMeshComp)wc.getComponent(entity, TextMeshComp.class);

            TextMesh textMesh = textMeshComp.getTextMesh();

            Mat4 modelTransform = retrieveModelTransform(entity);

            float textScale = textMesh.getSize() / textMesh.getFont().getFontSize();
            Mat4 modelScale = Mat4.scale(new Vec3(textScale, textScale, 1f));
            Mat4 textModelTransform = modelTransform.multiply( modelScale );

            renderTextMesh(textMesh, textMesh.getColor(), textModelTransform, viewTransform, projectionTransform);
        });


        //draw effects in the world
//        VisualEffectSys.forEachActiveParticle(p -> {
        wc.entitiesOfComponentTypeStream(VisualEffectComp.class).forEach(entity -> {
            VisualEffectComp viseffComp = (VisualEffectComp) wc.getComponent(entity, VisualEffectComp.class);

            //render if there is a running effect
            if (viseffComp.runningEffectId != -1) {
                VisualEffect runningEffect = viseffComp.effects.get(viseffComp.runningEffectId);

                runningEffect.activeParticleStream().forEach(p -> {
                    Mat4 translateTransform = Mat4.translate(new Vec3(p.getPos(), 1));
                    renderColoredMesh(p.getMesh(), translateTransform, viewTransform, projectionTransform);
                });
            }
        });


        //render stuff on view
        Mat4 toScreenTranslate = Mat4.translate(new Vec3(0, 0, 9.99f) );
        for (int entity : wc.getEntitiesWithComponentType(ViewRenderComp.class) ) {
            ViewRenderComp viewrendComp = (ViewRenderComp)wc.getComponent(entity, ViewRenderComp.class);

            Mat4 modelTransform = toScreenTranslate.multiply( retrieveModelTransform(entity) );

            viewrendComp.textureMeshesStream().forEach(textureMesh -> {
                renderTexturedMesh(textureMesh, modelTransform, Mat4.identity(), projectionTransform);
            });

            viewrendComp.colorMeshesStream().forEach(colorMesh -> {
                renderColoredMesh(colorMesh, modelTransform, Mat4.identity(), projectionTransform);
            });

            viewrendComp.textMeshesStream().forEach(textMesh -> {
                //get text size
                float textScale = textMesh.getSize() / textMesh.getFont().getFontSize();

                Mat4 modelScale = Mat4.scale(new Vec3(textScale, textScale, 1f));
                Mat4 textModelTransform = modelTransform.multiply( modelScale );

                renderTextMesh(textMesh, textMesh.getColor(), textModelTransform, Mat4.identity(), projectionTransform);
            });
        }


        window.swapBuffers();
    }

    @Override
    public void terminate() {
        glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

    }

    private void renderColoredMesh(int entity, ColoredMesh mesh, Mat4 viewTransform, Mat4 projectionTransform) {
        Mat4 modelTransform = retrieveModelTransform(entity);

        renderColoredMesh(mesh, modelTransform, viewTransform, projectionTransform);
    }
    private void renderColoredMesh(ColoredMesh mesh, Vec3 imageCenterTranslation, Vec3 translation, Vec3 scale, float rotation, Mat4 viewTransform, Mat4 projectionTransform) {
        Mat4 modelTransform = composeModelTransform(imageCenterTranslation, translation, scale, rotation);

        renderColoredMesh(mesh, modelTransform, viewTransform, projectionTransform);
    }

    private void renderTexturedMesh(int entity, TexturedMesh mesh, Mat4 viewTransform, Mat4 projectionTransform) {
        Mat4 modelTransform = retrieveModelTransform(entity);

        renderTexturedMesh(mesh, modelTransform, viewTransform, projectionTransform);
    }

    private void renderTexturedMesh(TexturedMesh mesh, Vec3 imageCenterTranslation, Vec3 translation, Vec3 scale, float rotation, Mat4 viewTransform, Mat4 projectionTransform) {
        Mat4 modelTransform = composeModelTransform(imageCenterTranslation, translation, scale, rotation);

        renderTexturedMesh(mesh, modelTransform, viewTransform, projectionTransform);
    }

    //text rendering by entity
    private void renderTextMesh(int entity, TextMesh mesh, Mat4 viewTransform, Mat4 projectionTransform) {
        Mat4 modelTransform = retrieveModelTransform(entity);

        renderTextMesh(mesh, mesh.getColor(), modelTransform, viewTransform, projectionTransform);
    }


    /**
     * Render a vertex array.
     * @param mesh
     */
    private void renderColoredMesh(ColoredMesh mesh, Mat4 modelTransform, Mat4 viewTransform, Mat4 projectionTransform) {
        colorShader.bind();
        mesh.bind();

        colorShader.setModelTransform(modelTransform);
        colorShader.setViewTransform(viewTransform);
        colorShader.setProjectionTransform(projectionTransform);


        glDrawElements(GL_TRIANGLES, mesh.getIndicesCount(), GL_UNSIGNED_BYTE, 0);

        mesh.unbind();
        colorShader.unbind();
    }

    private void renderTexturedMesh(TexturedMesh mesh, Mat4 modelTransform, Mat4 viewTransform, Mat4 projectionTransform) {
        textureShader.bind();
        mesh.bind();

        textureShader.setModelTransform(modelTransform);
        textureShader.setViewTransform(viewTransform);
        textureShader.setProjectionTransform(projectionTransform);


        glDrawElements(GL_TRIANGLES, mesh.getIndicesCount(), GL_UNSIGNED_BYTE, 0);

        mesh.unbind();
        textureShader.unbind();
    }
    private void renderTextMesh(TextMesh mesh, Vec4 color, Mat4 modelTransform, Mat4 viewTransform, Mat4 projectionTransform) {
        textShader.bind();
        mesh.bind();

        textShader.setModelTransform(modelTransform);
        textShader.setViewTransform(viewTransform);
        textShader.setProjectionTransform(projectionTransform);

        textShader.setTextColor(color);


        glDrawElements(GL_TRIANGLES, mesh.getIndicesCount(), GL_UNSIGNED_BYTE, 0);

        mesh.unbind();
        textShader.unbind();
    }


    private Mat4 retrieveModelTransform( int entity ) {
        PositionComp positionComp = (PositionComp)wc.getComponent(entity, PositionComp.class);

        Vec3 translate = positionComp.getPos3();
        Vec3 centerTranslate = getMeshCentering(entity);
        Vec3 scale = new Vec3(1, 1, 1);
        float rotate = getRotation(entity);

        return composeModelTransform(centerTranslate, translate, scale, rotate);
    }
    private Mat4 composeModelTransform( Vec3 imageCenterTranslation, Vec3 translation, Vec3 scale, float rotation ) {
        Mat4 modelScale = Mat4.scale( scale );
        Mat4 modelRotate = Mat4.rotate( rotation );
        Mat4 modelTranslate = Mat4.translate( translation );
        Mat4 modelCenterTranslate = Mat4.translate( imageCenterTranslation );

        Mat4 modelTransform = modelTranslate.multiply( modelScale.multiply( modelRotate.multiply( modelCenterTranslate ) ) );

        return modelTransform;
    }

    /**
     * Get rotation if rotation comp is present, else return 0
     * @param meshEntity
     * @return
     */
    private float getRotation(int meshEntity) {
        if (wc.hasComponent(meshEntity, RotationComp.class)) {
            RotationComp rotComp = (RotationComp) wc.getComponent(meshEntity, RotationComp.class);

            return rotComp.getAngle();
        }
        return 0;
    }

    /**
     * Returns mesh centering vector if meshCenterComp is present, [0,0] else
     */
    private Vec3 getMeshCentering(int meshEntity) {
        if (wc.hasComponent(meshEntity, MeshCenterComp.class)) {
            MeshCenterComp centerComp = (MeshCenterComp) wc.getComponent(meshEntity, MeshCenterComp.class);
            return new Vec3(-centerComp.getCx(), -centerComp.getCy(), 0);
        }
        return new Vec3();
    }

}
