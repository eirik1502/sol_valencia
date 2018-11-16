package engine.graphics;

import utils.maths.M;

/**
 *
 * Created by eirik on 15.06.2017.
 */
public class ColoredMeshUtils {

    public static ColoredMesh createRectangle(float width, float height) {

        float[] vertices = new float[] {
                0.0f, 0.0f, 0.0f,
                0.0f, height, 0.0f,
                width, height, 0.0f,
                width, 0.0f, 0.0f
        };

        //gets normalized in fragment shader
//		float[] normals = new float[] {
//			-0.7f, -0.7f, -0.7f,
//			-0.7f, 0.7f, -0.7f,
//			0.7f, 0.7f, -0.7f,
//			0.07f, -0.7f, -0.7f
//		};

        float[] normals = new float[] {
                0f, 0f, -1f,
                0f, 0f, -1f,
                0f, 0f, -1f,
                0f, 0f, -1f
        };

        byte[] indices = new byte[] {
                0, 1, 2,
                2, 3, 0
        };

        float sr = 1;//(float)Math.random(); //to variate the spread around 1
        float[] colors = new float[] { //texture coordinates
                sr*(float)Math.random(), sr*(float)Math.random(), sr*(float)Math.random(),
                sr*(float)Math.random(), sr*(float)Math.random(), sr*(float)Math.random(),
                sr*(float)Math.random(), sr*(float)Math.random(), sr*(float)Math.random(),
                sr*(float)Math.random(), sr*(float)Math.random(), sr*(float)Math.random()
        };

        return new ColoredMesh( vertices, normals, colors, indices );
    }

    private static ColoredMesh createCircleColored(float radius, int sides, float[] colors) {
        int verticesCount = (1+sides);
        int verticesLength = verticesCount*3;
        int indicesLength = sides*3;

        if (colors.length != verticesLength) throw new IllegalArgumentException("number of colors not equal number of vertices");

        float[] vertices = new float[verticesLength];
        float[] normals = new float[verticesLength];
        byte[] indices = new byte[indicesLength];

        float anglesPerSide = 2* M.PI / sides;

        putThreeFloat(vertices, 0, 0f, 0f, 0f);//center vertex
        putThreeFloat(normals, 0, 0f, 0f, -1f);//normal corresponding to center vertex


        for (int i = 0; i < sides; i++) {
            //vertices
            float angle = anglesPerSide * i;
            int verticesStartIndex = 3*(i+1);
            putThreeFloat(vertices, verticesStartIndex, radius * M.cos(angle), radius * M.sin(angle), 0.0f);

            //normals
            putThreeFloat(normals, verticesStartIndex, 0f, 0f, -1f);

            //indices
            int indicesStartIndex = 3*i;
            int indexStart = i;
            putThreeByte(indices, indicesStartIndex, 0, indexStart +1, indexStart +2);
        }
        indices[indicesLength-1] = (byte) 1;//the very last index should be the first vertex

        return new ColoredMesh( vertices, normals, colors, indices );
    }
    public static ColoredMesh createCircleSinglecolor(float radius, int sides, float[] color) {
        if (color.length != 3) throw new IllegalStateException("illegal color data given");

        int vertexCount = sides+1;
        int colorsLength = (1+sides)*3;
        float[] colors = new float[colorsLength];

        for (int i = 0; i < vertexCount; i++) {
            int colorStartIndex = i * 3;
            putThreeFloat(colors, colorStartIndex, color);
        }
        return createCircleColored(radius, sides, colors);
    }
    public static ColoredMesh createCircleTwocolor(float radius, int sides) {
        int colorsLength = (1+sides)*3;
        float[] colors = new float[colorsLength];

        float colorRange = 1;//(float)Math.random(); //to variate the spread around 1
        float[] color1 = {colorRange*(float)Math.random(), colorRange*(float)Math.random(), colorRange*(float)Math.random()};
        float[] color2 = {colorRange*(float)Math.random(), colorRange*(float)Math.random(), colorRange*(float)Math.random()};

        putThreeFloat(colors, 0, color1);

        for (int i = 0; i < sides; i++) {
            int colorStartIndex = (1+i) * 3;
            putThreeFloat(colors, colorStartIndex, color2);
        }

        return createCircleColored(radius, sides, colors);
    }
    public static ColoredMesh createCircleMulticolor(float radius, int sides) {
        int colorsLength = (1+sides)*3;
        int vertexCount = sides+1;
        float[] colors = new float[colorsLength];

        float colorRange = 1;//(float)Math.random(); //to variate the spread around 1
        float[] centerColor = {colorRange*(float)Math.random(), colorRange*(float)Math.random(), colorRange*(float)Math.random()};
        //float[] black = {0f,0f,0f};
        //float[] white = {1f, 1f, 1f};
        //float[] centerColor = M.random() < 0.5? black : white;

        putThreeFloat(colors, 0, centerColor);

        for (int i = 0; i < vertexCount-1; i++) {
            //colors
            float[] boundaryColor = {colorRange * (float) Math.random(), colorRange * (float) Math.random(), colorRange * (float) Math.random()};
            int colorStartIndex = (1+i) * 3;
            putThreeFloat(colors, colorStartIndex, boundaryColor);
        }

        return createCircleColored(radius, sides, colors);
    }

    public static ColoredMesh createRectangleSingleColor(float width, float height, float [] color) {
        float[] vertices = new float[]{
                0.0f, 0.0f, 0.0f,
                0.0f, height, 0.0f,
                width, height, 0.0f,
                width, 0.0f, 0.0f
        };

        //gets normalized in fragment shader
//		float[] normals = new float[] {
//			-0.7f, -0.7f, -0.7f,
//			-0.7f, 0.7f, -0.7f,
//			0.7f, 0.7f, -0.7f,
//			0.07f, -0.7f, -0.7f
//		};

        float[] normals = new float[]{
                0f, 0f, -1f,
                0f, 0f, -1f,
                0f, 0f, -1f,
                0f, 0f, -1f
        };

        byte[] indices = new byte[]{
                0, 1, 2,
                2, 3, 0
        };

        return new ColoredMesh(vertices, normals, color, indices);
    }


    public static void putThreeFloat(float[] array, int startIndex, float v1, float v2, float v3) {
        array[startIndex] = v1;
        array[startIndex +1] = v2;
        array[startIndex +2] = v3;
    }
    public static void putThreeFloat(float[] array, int startIndex, float[] values) {
        if (values.length != 3) throw new IllegalArgumentException("This method only accepts three values");
        putThreeFloat(array, startIndex, values[0], values[1], values[2]);
    }
    public static void putThreeByte(byte[] array, int startIndex, byte v1, byte v2, byte v3) {
        array[startIndex] = v1;
        array[startIndex +1] = v2;
        array[startIndex +2] = v3;
    }
    public static void putThreeByte(byte[] array, int startIndex, int v1, int v2, int v3) {
        putThreeByte(array, startIndex, (byte)v1, (byte)v2, (byte)v3);
    }

}
