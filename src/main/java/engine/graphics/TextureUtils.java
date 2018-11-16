package engine.graphics;

import utils.BufferUtils;
import utils.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by eirik on 15.06.2017.
 */
public class TextureUtils {

    public static Texture loadTexture(String path) {
        int[] pixels = null;
        int width;
        int height;


        BufferedImage image = FileUtils.loadImage(path);
        width = image.getWidth();
        height = image.getHeight();
        pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        //the rgb coordinates are inverted as opposed to glTextures (I think thats waht happens here)
        int[] data = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            int a = (pixels[i] & 0xff000000) >> 24;
            int r = (pixels[i] & 0xff0000) >> 16;
            int g = (pixels[i] & 0xff00) >> 8;
            int b = (pixels[i] & 0xff);

            data[i] = a << 24 | b << 16 | g << 8 | r;
        }

        return createTexture(data, width, height);
    }


    private static Texture createTexture(int[] data, int width, int height) {
        int textureID = createGlTexture(data, width, height);
        return new Texture(textureID, width, height);
    }
    private static int createGlTexture(int[] data, int width, int height) {
        int result = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, result);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtils.createIntBuffer(data));
        glBindTexture(GL_TEXTURE_2D, 0);
        return result;
    }
}
