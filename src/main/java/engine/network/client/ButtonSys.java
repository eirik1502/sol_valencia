package engine.network.client;

import engine.*;
import engine.graphics.ViewRenderComp;
import engine.graphics.text.TextMesh;
import utils.maths.Vec4;

import java.awt.*;

/**
 * Created by eirik on 27.07.2017.
 */
public class ButtonSys implements Sys {

    private WorldContainer wc;

    private UserInput userInput;


    public ButtonSys(UserInput userInput) {
        this.userInput = userInput;
    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        wc.entitiesOfComponentTypeStream(ButtonComp.class).forEach(entity -> {
            ButtonComp butComp = (ButtonComp)wc.getComponent(entity, ButtonComp.class);
            PositionComp posComp = (PositionComp)wc.getComponent(entity, PositionComp.class);
            RectangleComp rectComp = (RectangleComp)wc.getComponent(entity, RectangleComp.class);
            ViewRenderComp viewRendComp = (ViewRenderComp)wc.getComponent(entity, ViewRenderComp.class);

            updateButton(entity, butComp, posComp, rectComp, viewRendComp);
        });
    }

    @Override
    public void terminate() {

    }

    private void updateButton(int butEntity, ButtonComp butComp, PositionComp posComp, RectangleComp rectComp, ViewRenderComp viewRendComp) {
        float mx = userInput.getMouseX();
        float my = userInput.getMouseY();
        boolean mbPressed = userInput.isMousePressed(UserInput.MOUSE_BUTTON_1);

        //if mouse over button, it is either hovered or pressed
        boolean mouseOver = mouseOverRect(mx, my, posComp, rectComp);

        //enter action
        if (mouseOver && butComp.state == ButtonComp.NO_STATE) {
            applyOnAction(butComp.onEnter, butEntity, 0);
        }

        if (mouseOver && mbPressed) { //pressed criteria
            if (butComp.state != ButtonComp.PRESS_STATE) {

                //set button text color
                setEntityTextAlpha(butEntity, butComp.pressAlpha);

                butComp.state = ButtonComp.PRESS_STATE;

                applyOnAction(butComp.onPress, butEntity, 0);
            }

        }

        //if button was pressed last frame, call on release
        else if (mouseOver && butComp.state == ButtonComp.PRESS_STATE) {

            //will only happen once

            //set button text color
            setEntityTextAlpha(butEntity, butComp.hoverAlpha);

            butComp.state = ButtonComp.RELEASE_STATE;
            applyOnAction(butComp.onRelease, butEntity, 0);
        }

        else if (mouseOver && !mbPressed) { //hover criteria
            if (butComp.state != ButtonComp.HOVER_STATE) {

                //set button text color
                setEntityTextAlpha(butEntity, butComp.hoverAlpha);

                butComp.state = ButtonComp.HOVER_STATE;
            }
        }

        //if mouse not over button, set no action, and reset alpha
        else {
            if (butComp.state != ButtonComp.NO_STATE) {
                setEntityTextAlpha(butEntity, butComp.noActionAlpha);
                butComp.state = ButtonComp.NO_STATE;

                applyOnAction(butComp.onExit, butEntity, 0);
            }
        }

    }

    private boolean mouseOverRect(float mx, float my, PositionComp posComp, RectangleComp rectComp) {
        if (mx < posComp.getX() || mx > posComp.getX() + rectComp.width) return false;
        if (my < posComp.getY() || my > posComp.getY() + rectComp.height) return false;

        return true;
    }

    private void applyOnAction(OnButtonAction buttonAction, int entity, int action) {
        if (buttonAction != null) {
            buttonAction.onButtonAction(entity, action);
        }
    }

    private void setEntityTextColor(int viewRenderEntity, Vec4 color) {
        ( (ViewRenderComp)wc.getComponent(viewRenderEntity, ViewRenderComp.class)).getTextMesh(0).setColor(color);
    }
    private void setEntityTextAlpha(int viewRenderEntity, float alpha) {
        ( (ViewRenderComp)wc.getComponent(viewRenderEntity, ViewRenderComp.class)).getTextMesh(0).getColor().w = alpha;

    }
}
