package engine.graphics.view_;

import utils.maths.Mat4;
import utils.maths.Vec2;
import utils.maths.Vec3;

/**
 * Created by eirik on 06.07.2017.
 */
public class View {

    private float width, height;
    private float x, y;

    private final float znear = -10, zfar = 10;



    public View(float width, float height, float x, float y) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }
    public View(float width, float height) {
        this(width, height, 0, 0);
    }

    public Mat4 getViewTransform() {
        return Mat4.translate( new Vec3( new Vec2(x, y).negative(), 0f) );
    }
    public Mat4 getProjectionTransform( ) {
        return Mat4.orthographic(0, width, height, 0, znear, zfar);
    }

    public float getWidth() {
        return width;
    }
    public void setWidth(float width) {
        this.width = width;
    }
    public float getHeight() {
        return height;
    }
    public void setHeight(float height) {
        this.height = height;
    }

    public Vec2 getSize() {
        return new Vec2(width, height);
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
}
