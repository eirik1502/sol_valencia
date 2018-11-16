package engine.graphics;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

/**
 * Created by eirik on 15.06.2017.
 */
public class TexturedMesh {

    int vaoId;
    int verticesId;
    int uvsId;
    int indicesId;
    int normalsId;

    int indicesCount;

    private Texture tex;


    public TexturedMesh(float[] vertices, float[] normals, float[] uvs, byte[] indices, Texture tex) {
        indicesCount = indices.length;
        vaoId = createVertexArray(vertices, normals, uvs, indices);
        this.tex = tex;
    }

    public int getIndicesCount() {
        return indicesCount;
    }

    public void bind() {
        //bind vao
        GL30.glBindVertexArray(vaoId);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesId);

        //bind texture
        tex.bind();
    }
    public void unbind() {
        //unbind vao
        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        //unbind texture
        tex.unbind();
    }

    private int createVertexArray(float[] vertices, float[] normals, float[] uvs, byte[] indices) {
        int id = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(id);

        verticesId = VertexArrayUtils.createVertexBuffer(TextureShader.VERTEX_LOCATION, 3, vertices);
        normalsId = VertexArrayUtils.createVertexBuffer(TextureShader.NORMALS_LOCATION, 3, normals);
        uvsId = VertexArrayUtils.createVertexBuffer(TextureShader.UVS_LOCATION, 2, uvs);
        indicesId = VertexArrayUtils.createIndicesBuffer(indices);


        GL30.glBindVertexArray(0);

        return id;
    }
}
