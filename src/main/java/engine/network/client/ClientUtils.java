package engine.network.client;

import engine.*;
import engine.combat.abilities.ProjectileComp;
import engine.graphics.*;
import engine.graphics.text.TextMesh;
import engine.graphics.text.TextMeshComp;
import engine.graphics.view_.View;
import engine.visualEffect.VisualEffectComp;
import engine.window.Window;
import game.client.Client;
import utils.maths.Vec4;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientUtils {

    //title attributes
    public static final float
            titleLeft = 400, titleTop = 300,
            titleTextSize = 72;
    public static final Vec4 titleTextColor = new Vec4(0.8f, 1f, 0.8f, 1f);


    //menu button attributes
    public static final float
            buttonsLeft = 400, buttonsTop = 450,
            buttonWidth = 500, buttonHeight = 60,
            buttonVertSpace = 10,
            buttonTextSize = 54;
    public static final Vec4 buttonTextColor = new Vec4(0.9f, 0.9f, 0.9f, 1f);

    /**
     * WorldContainer initialized with client default component types and systems
     * @return
     */
    public static WorldContainer createDefaultWorldContainer(Window window, UserInput userInput) {
        WorldContainer wc = new WorldContainer(
                new View(Client.CLIENT_WIDTH, Client.CLIENT_HEIGHT)
                );

        //assign components
        wc.assignComponentType(PositionComp.class);
        wc.assignComponentType(RotationComp.class);
        wc.assignComponentType(ColoredMeshComp.class);
        wc.assignComponentType(TexturedMeshComp.class);
        wc.assignComponentType(MeshCenterComp.class);
        wc.assignComponentType(TextMeshComp.class);
        wc.assignComponentType(ViewRenderComp.class);
        wc.assignComponentType(RectangleComp.class);
        wc.assignComponentType(ButtonComp.class);
        wc.assignComponentType(VisualEffectComp.class);
        wc.assignComponentType(ProjectileComp.class); //because of draw order



        //add systems
        wc.addSystem(new ButtonSys(userInput));

        wc.addSystem(new RenderSys(window));


        return wc;
    }


    public static void setEntityString(WorldContainer wc, int textMeshEntity, String s) {
        ( (ViewRenderComp)wc.getComponent(textMeshEntity, ViewRenderComp.class) ).getTextMesh(0).setString(s);
    }

    public static TextMesh getEntityTextMesh(WorldContainer wc, int textMeshEntity) {
        return ( (ViewRenderComp)wc.getComponent(textMeshEntity, ViewRenderComp.class) ).getTextMesh(0);
    }

    public static int createButton(WorldContainer wc,
                                   float x, float y, float width, float height,
                                   TextMesh textMesh,
                                   OnButtonAction pressAction, OnButtonAction releaseAction, OnButtonAction enterAction, OnButtonAction exitAction
                                   )
    {
        int e = wc.createEntity();
        wc.addComponent(e, new PositionComp(x, y));
        wc.addComponent(e, new RectangleComp(width, height));
        wc.addComponent(e, new ButtonComp(pressAction, releaseAction, enterAction, exitAction));
        wc.addComponent(e, new ViewRenderComp(textMesh));

        return e;
    }
}
