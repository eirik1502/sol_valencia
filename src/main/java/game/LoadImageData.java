package game;

/**
 * Created by eirik on 02.08.2017.
 */
public class LoadImageData {

    public final String filename;
    public final float width;
    public final float height;
    public final float offsetX;
    public final float offsetY;

    public LoadImageData(String filename,
                         float scale,
                         float imageWidth, float imageHeight,
                         float offsetXOnImage, float offsetYOnImage) {

        this.filename = filename;
        width = imageWidth * scale;
        height = imageHeight * scale;
        offsetX = offsetXOnImage * scale;
        offsetY = offsetYOnImage * scale;
    }

    public LoadImageData(String filename,
                         float radiusInGame, float radiusOnImage,
                         float imageWidth, float imageHeight,
                         float offsetXOnImage, float offsetYOnImage) {
        this(filename, radiusInGame/radiusOnImage, imageWidth, imageHeight, offsetXOnImage, offsetYOnImage);
    }
}
