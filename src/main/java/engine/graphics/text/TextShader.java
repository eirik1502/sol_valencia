package engine.graphics.text;

import engine.graphics.Shader;
import utils.maths.Mat4;
import utils.maths.Vec3;
import utils.maths.Vec4;

/**
 * Created by eirik on 05.07.2017.
 */
public class TextShader extends Shader{

    public static final int VERTEX_LOCATION = 0;
    public static final int NORMALS_LOCATION = 1;
    public static final int UVS_LOCATION = 2;


    private final int modelTransformLocation;
    private final int viewTransformLocation;
    private final int projectionTransformLocation;

    private final int textColorLocation;


    private static String vertexPath = "shaders/text_shader.vert";
    private static String fragPath = "shaders/text_shader.frag";


    public TextShader() {
        super(vertexPath, fragPath);

        modelTransformLocation = super.getUniformLocation("modelTransform");
        viewTransformLocation = super.getUniformLocation("viewTransform");
        projectionTransformLocation = super.getUniformLocation("projectionTransform");

        textColorLocation = super.getUniformLocation("textColor");
    }


    public void setModelTransform(Mat4 transform) {
        super.setUniformMat4f(modelTransformLocation, transform);
    }

    public void setViewTransform(Mat4 transform) {
        super.setUniformMat4f(viewTransformLocation, transform);
    }

    public void setProjectionTransform(Mat4 transform) {
        super.setUniformMat4f(projectionTransformLocation, transform);
    }

    public void setTextColor(Vec4 color) {
        super.setUniform4f(textColorLocation, color);
    }
}
