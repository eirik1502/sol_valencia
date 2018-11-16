package engine.graphics.text;

import java.util.Arrays;
import java.util.List;

/**
 * Created by eirik on 03.07.2017.
 */
public class CharMeshData {


    private float[] vertices;
    private float[] uvs;
    private byte[] indices;


    public CharMeshData(float[] vertices, float[] uvs, byte[] indices) {
        this.vertices = vertices;
        this.uvs = uvs;
        this.indices = indices;
    }


    public float[] getVertices() {
        return vertices;
    }

    public float[] getUvs() {
        return uvs;
    }

    public byte[] getIndices() {
        return indices;
    }

    public String toString() {
        return "vertices="+Arrays.toString(vertices)+" uvs="+Arrays.toString(uvs)+" indices="+Arrays.toString(indices);
    }
}
