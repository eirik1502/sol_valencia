package engine.network.client;

import engine.Component;
import utils.maths.Vec4;

import java.util.function.Consumer;

/**
 * Created by eirik on 27.07.2017.
 */
public class ButtonComp implements Component {

    public static final int NO_STATE = 0, HOVER_STATE = 1, PRESS_STATE = 2, RELEASE_STATE = 3;

    public int state = NO_STATE;

//
//    public Vec4 noActionColor = new Vec4(0.8f, 0.8f, 0.8f, 1f);
//    public Vec4 hoverColor = new Vec4(0.8f, 0.8f, 0.8f, 0.8f);
//    public Vec4 pressColor = new Vec4(0.8f, 0.8f, 0.8f, 0.5f);

    public float noActionAlpha = 1;
    public float hoverAlpha = 0.8f;
    public float pressAlpha = 0.5f;


    public OnButtonAction onPress = null;
    public OnButtonAction onRelease = null;
    public OnButtonAction onEnter = null;
    public OnButtonAction onExit = null;

    /**
     * assign same class for all actions
     */
    public ButtonComp(OnButtonAction onAllActions) {
        onPress = onAllActions;
        onRelease = onAllActions;
        onEnter = onAllActions;
        onExit = onAllActions;
    }

    public ButtonComp(OnButtonAction pressAction, OnButtonAction releaseAction, OnButtonAction enterAction, OnButtonAction exitAction) {
        onPress = pressAction;
        onRelease = releaseAction;
        onEnter = enterAction;
        onExit = exitAction;
    }


}
