package engine.network;

import utils.maths.Vec2;

/**
 * Created by eirik on 27.06.2017.
 */
public class CharacterStateData {

    private float x,y, rotation;


    public CharacterStateData(float x, float y, float rotation) {
        setX(x);
        setY(y);
        setRotation(rotation);
    }

    public CharacterStateData() {
        this(0,0,0);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Vec2 getPos() {
        return new Vec2(x, y);
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public String toString() {
        return "[CharacterStateData: x="+x+" y="+y+" rotation="+rotation+"]";
    }
}
