package engine.graphics;

import engine.Component;

/**
 * Defines the center of a mesh. Apllies to ie rotation
 * Created by eirik on 15.06.2017.
 */
public class MeshCenterComp implements Component {


    private float cx, cy;


    public MeshCenterComp(float cx, float cy) {
        this.cx = cx;
        this.cy = cy;
    }

    public float getCx() {
        return cx;
    }

    public void setCx(float cx) {
        this.cx = cx;
    }

    public float getCy() {
        return cy;
    }

    public void setCy(float cy) {
        this.cy = cy;
    }
}
