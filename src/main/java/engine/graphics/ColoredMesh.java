package engine.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import utils.BufferUtils;

/**
 * A vertex array
 * Created by eirik on 13.06.2017.
 */
public class ColoredMesh {

    int vaoId;
    int verticesId;
    int colorsId;
    int indicesId;
    int normalsId;

    int indicesCount;

    public ColoredMesh(float[] vertices, float[] colors, byte[] indices) {
        indicesCount = indices.length;
        float[] normals = {0,0,0};
        vaoId = createVertexArray(vertices, normals, colors, indices);

        throw new IllegalStateException("Do not use this constructor, normals not defined");
    }
    public ColoredMesh(float[] vertices, float[] normals, float[] colors, byte[] indices) {
        indicesCount = indices.length;
        vaoId = createVertexArray(vertices, normals, colors, indices);
    }

    public int getIndicesCount() {
        return indicesCount;
    }

    public void bind() {
        GL30.glBindVertexArray(vaoId);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesId);
    }
    public void unbind() {
        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private int createVertexArray(float[] vertices, float[] normals, float[] colors, byte[] indices) {
        int id = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(id);

        verticesId = VertexArrayUtils.createVertexBuffer(ColorShader.VERTEX_LOCATION, 3, vertices);
        normalsId = VertexArrayUtils.createVertexBuffer(ColorShader.NORMALS_LOCATION, 3, normals);
        colorsId = VertexArrayUtils.createVertexBuffer(ColorShader.COLORS_LOCATION, 3, colors);
        indicesId = VertexArrayUtils.createIndicesBuffer(indices);


        GL30.glBindVertexArray(0);

        return id;
    }


}
