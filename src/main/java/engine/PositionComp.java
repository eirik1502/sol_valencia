package engine;

import utils.maths.Vec2;
import utils.maths.Vec3;

/**
 * Created by eirik on 13.06.2017.
 */
public class PositionComp implements Component {

    private float x, y, z;


    public PositionComp(float x, float y, float z) {
        setX(x);
        setY(y);
        setZ(z);
    }
    public PositionComp(float x, float y) {
        this(x, y, 0);
    }

    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }
    public void addX(float x) {
        setX(getX()+x);
    }

    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }
    public void addY(float y) {
        setY(getY()+y);
    }

    public float getZ() {
        return z;
    }
    public void setZ(float z) {
        this.z = z;
    }

    public void setPos(Vec2 v) {
        setX(v.x);
        setY(v.y);
    }
    public void addPos(Vec2 vector){
        addX(vector.x);
        addY(vector.y);
    }
    public Vec2 getPos() {
        return new Vec2(x, y);
    }

    public Vec3 getPos3() {
        return new Vec3(x, y, z);
    }

//    @Override
//    public int getMask() {
//        return WorldContainer.COMPMASK_POSITION;
//    }

    @Override
    public String toString() {
        return "x="+x+" y="+y;
    }
}
