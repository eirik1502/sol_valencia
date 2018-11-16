package engine.graphics;

import engine.Component;

/**
 * Created by eirik on 13.06.2017.
 */
public class TexturedMeshComp implements Component{

    private TexturedMesh mesh;

    public TexturedMeshComp(TexturedMesh mesh) {
        setMesh(mesh);
    }

    public TexturedMesh getMesh() {
        return mesh;
    }

    public void setMesh(TexturedMesh mesh) {
        this.mesh = mesh;
    }

}
