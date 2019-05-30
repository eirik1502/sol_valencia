package engine.network.networkPackets;

import engine.network.NetworkUtils;
import utils.Parse;

/**
 * Created by eirik on 21.06.2017.
 */
public class CharacterInputData implements NetworkPacket{

    public static final int BYTES = Float.BYTES*2 + 1 * 7; //a boolean is sent as one byte with the dataStreams


    public int clientId = -1;

    private boolean moveLeft, moveRight, moveUp, moveDown;

    private float aimX;
    private float aimY;
    private boolean action1;
    private boolean action2;
    private boolean action3;


    public CharacterInputData() {

    }

    public void setMovement(boolean moveLeft, boolean moveRight, boolean moveUp, boolean moveDown) {
        this.moveLeft = moveLeft;
        this.moveRight = moveRight;
        this.moveUp = moveUp;
        this.moveDown = moveDown;


    }

    public void setActions(boolean action1, boolean action2, boolean action3) {
        this.action1 = action1;
        this.action2 = action2;
        this.action3 = action3;
    }

    public void setAim(float aimX, float aimY) {
        this.aimX = aimX;
        this.aimY = aimY;
    }


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

    @Override
    public String toString() {
        return "[CharacterInputData: moveLeft="+moveLeft+" moveRight="+moveRight+" moveUp="+moveUp+" moveDown="+moveDown+" action1="+action1+" action2="+action2+" aimX="+aimX+" aimY="+aimY+"]";
    }

    @Override
    public int getPacketId() {
        return NetworkUtils.CLIENT_CHARACTER_INPUT;
    }

    public String serialize() {
        return ""
                +clientId + ' '
                +Parse.boolToBinChar(moveLeft)+' '
                +Parse.boolToBinChar(moveRight)+' '
                +Parse.boolToBinChar(moveUp)+' '
                +Parse.boolToBinChar(moveDown)+' '
                +Parse.boolToBinChar(action1)+' '
                +Parse.boolToBinChar(action2)+' '
                +Parse.boolToBinChar(action3)+' '
                +aimX+' '
                +aimY;
    }
}
