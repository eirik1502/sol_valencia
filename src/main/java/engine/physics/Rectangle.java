package engine.physics;

/**
 * Created by haraldvinje on 13-Jun-17.
 */
public class Rectangle extends Shape {

    private float height;
    private float width;

    public Rectangle(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
