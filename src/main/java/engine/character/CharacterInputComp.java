package engine.character;

import engine.Component;

/**
 * Created by eirik on 15.06.2017.
 */
public class CharacterInputComp implements Component {


    private boolean moveLeft, moveRight, moveUp, moveDown;

    private float aimX;
    private float aimY;
    private boolean action1;
    private boolean action2;
    private boolean action3;


    public boolean isMoveLeft() {
        return moveLeft;
    }

    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
    }

    public boolean isMoveRight() {
        return moveRight;
    }

    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
    }

    public boolean isMoveUp() {
        return moveUp;
    }

    public void setMoveUp(boolean moveUp) {
        this.moveUp = moveUp;
    }

    public boolean isMoveDown() {
        return moveDown;
    }

    public void setMoveDown(boolean moveDown) {
        this.moveDown = moveDown;
    }

    public float getAimX() {
        return aimX;
    }

    public void setAimX(float aimX) {
        this.aimX = aimX;
    }

    public float getAimY() {
        return aimY;
    }

    public void setAimY(float aimY) {
        this.aimY = aimY;
    }

    public boolean isAction1() {
        return action1;
    }

    public void setAction1(boolean action1) {
        this.action1 = action1;
    }

    public boolean isAction2() {
        return action2;
    }

    public void setAction2(boolean action2) {
        this.action2 = action2;
    }

    public boolean isAction3() {
        return action3;
    }

    public void setAction3(boolean action3) {
        this.action3 = action3;
    }
}
