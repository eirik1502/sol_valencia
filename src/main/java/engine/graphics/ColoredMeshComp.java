package engine.graphics;

import engine.Component;

/**
 * Created by eirik on 13.06.2017.
 */
public class ColoredMeshComp implements Component{

    private ColoredMesh mesh;

    public ColoredMeshComp(ColoredMesh mesh) {
        setMesh(mesh);
    }

    public ColoredMesh getMesh() {
        return mesh;
    }

    public void setMesh(ColoredMesh mesh) {
        this.mesh = mesh;
    }

}
