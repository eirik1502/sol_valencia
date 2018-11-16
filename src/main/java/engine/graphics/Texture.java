package engine.graphics;

import static org.lwjgl.opengl.GL11.*;


/**
 * Created by eirik on 15.06.2017.
 */
public class Texture {

    private int width, height;
    private int textureId;


    Texture(int textureId, int width, int height) {
        this.textureId = textureId;
        this.width = width;
        this.height = height;
    }
//    public Texture(String path) {
//        textureId = load(path);
//    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }


}
