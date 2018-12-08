package game.offline;

import engine.Component;

/**
 *
 * Created by eirik on 16.11.2018.
 */
public class MovementControlComp implements Component {
    public float speed = 10f;


    public static MovementControlComp create(float speed) {
        MovementControlComp moveCtrlComp = new MovementControlComp();
        moveCtrlComp.speed = speed;
        return moveCtrlComp;
    }
}