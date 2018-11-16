package engine.graphics.text;

import engine.graphics.Texture;
import engine.graphics.TextureUtils;
import utils.FileUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by eirik on 03.07.2017.
 */
public class FontUtils {

    public static Font loadFont(String fontfilePath, String atlasPath) {
        InputStream is = FileUtils.loadAsStream(fontfilePath);
        Scanner s = new Scanner(is);

        int fontSize = 0;
        int atlasWidth = 0, atlasHeight = 0;
        int lineHeight = 0;
        HashMap<Character, CharData> charData = null;

        String line;
        while(s.hasNextLine()) {
            line = s.nextLine();
            String[] lineAttribs = line.split(" +");
            String lineIdentifier = lineAttribs[0]; //first word in a line

            if (lineIdentifier.equals("info")) {
                int[] values = filterAttribValueArray(lineAttribs, 11);

                // face
                fontSize = values[1]; //size
                // bold
                // italic
                // charset
                // unicode
                // stretchH
                // smooth
                // aa
                // padding
                // spacing
            }
            else if (lineIdentifier.equals("common")) {
                int[] values = filterAttribValueArray(lineAttribs, 6);

                lineHeight = values[0];
                atlasWidth = values[2];
                atlasHeight = values[3];
            }
            else if (lineIdentifier.equals("chars")) {
                //there is only one value, to we find the vakue after the '=' in the second atrribVal
                int charCount = Integer.parseInt( lineAttribs[1].split("=")[1] );
                charData = new HashMap<Character, CharData>( charCount );
            }
            else if (lineIdentifier.equals("char")) {
                //filter out values in an array
                int[] values = filterAttribValueArray(lineAttribs, 10);
                //System.out.println(Arrays.toString(values));

                char c = (char)values[0]; //character id
                int x = values[1];
                int y = values[2];
                int width = values[3];
                int height = values[4];
                int xoffset = values[5];
                int yoffset = values[6];
                int xadvance = values[7];

                CharMeshData meshData = createMeshData(x, y, width, height, xoffset, yoffset, atlasWidth, atlasHeight);
                CharData data = new CharData(meshData, xadvance);

                charData.put(c, data);
            }
        }
        if (charData == null || lineHeight == 0) throw new IllegalStateException("Could not load font");

        Texture fontAtlas = TextureUtils.loadTexture(atlasPath);


        return new Font(lineHeight, fontSize, charData, fontAtlas);
    }

    private static int[] filterAttribValueArray(String[] attribValues, int valueCount) {
        int[] values = new int[valueCount];
        for (int i = 1; i < attribValues.length; i++) {
            try {
                values[i - 1] = Integer.parseInt(attribValues[i].split("=")[1]);
            }
            //if the value is not a number, add a zero
            catch (NumberFormatException e) {
                values[i - 1] = 0;
            }
        }

        return values;
    }

    private static CharMeshData createMeshData(float x, float y, float width, float height, float xoffset, float yoffset, float atlasWidth, float atlasHeight) {
        float[] vertices = new float[] {
                xoffset,        yoffset,        0.0f,
                xoffset,        yoffset+height, 0.0f,
                xoffset+width,  yoffset+height, 0.0f,
                xoffset+width,  yoffset,        0.0f
        };

        float[] uvs = new float[] {
                x           /atlasWidth, y          /atlasHeight,
                x           /atlasWidth, (y+height) /atlasHeight,
                (x+width)   /atlasWidth, (y+height) /atlasHeight,
                (x+width)   /atlasWidth, y          /atlasHeight
        };

        byte[] indices = new byte[] {
                0, 1, 2,
                2, 3, 0
        };

        return new CharMeshData(vertices, uvs, indices);
    }
}
