package engine.graphics;

/**
 * Created by eirik on 15.06.2017.
 */
public class TexturedMeshUtils {


    public static TexturedMesh createRectangle(String texPath, float width, float height) {

        float[] vertices = new float[] {
                0.0f, 0.0f, 0.0f,
                0.0f, height, 0.0f,
                width, height, 0.0f,
                width, 0.0f, 0.0f
        };


        float[] normals = new float[] {
                0f, 0f, -1f,
                0f, 0f, -1f,
                0f, 0f, -1f,
                0f, 0f, -1f
        };

        float[] uvs = new float[] {
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };

        byte[] indices = new byte[] {
                0, 1, 2,
                2, 3, 0
        };

        Texture tex = TextureUtils.loadTexture(texPath);

        return new TexturedMesh( vertices, normals, uvs, indices, tex );
    }
}
