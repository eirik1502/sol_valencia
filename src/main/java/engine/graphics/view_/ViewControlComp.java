package engine.graphics.view_;

import engine.Component;
import utils.maths.Vec2;

/**
 * Created by eirik on 05.07.2017.
 */
public class ViewControlComp implements Component{


    private float viewOffsetX, viewOffsetY;


    public ViewControlComp(float viewOffsetX, float viewOffsetY) {
        setViewOffsetX(viewOffsetX);
        setViewOffsetY(viewOffsetY);
    }
    public ViewControlComp() {
        this(0, 0);
    }


    public float getViewOffsetX() {
        return viewOffsetX;
    }

    public void setViewOffsetX(float viewOffsetX) {
        this.viewOffsetX = viewOffsetX;
    }

    public float getViewOffsetY() {
        return viewOffsetY;
    }

    public void setViewOffsetY(float viewOffsetY) {
        this.viewOffsetY = viewOffsetY;
    }

    public Vec2 getViewOffset() {
        return new Vec2( viewOffsetX, viewOffsetY );
    }
}
