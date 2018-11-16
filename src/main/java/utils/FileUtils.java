package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by eirik on 13.06.2017.
 */
public class FileUtils {

    private FileUtils() {
    }

    public static InputStream loadAsStream(String filepath) {
        return FileUtils.class.getClassLoader().getResourceAsStream(filepath);
    }

    public static String loadAsString(String file) {
        StringBuilder result = new StringBuilder();
        try {
            InputStream rs = FileUtils.class.getClassLoader().getResourceAsStream(file);

            BufferedReader reader = new BufferedReader(new InputStreamReader(rs));
            String buffer = "";
            while ((buffer = reader.readLine()) != null) {
                result.append(buffer + '\n');
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(FileUtils.class.getClassLoader().getResourceAsStream(path) ); // new FileInputStream(path))
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Could not load image, see more above");
        }
    }

}