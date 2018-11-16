package engine.graphics;


import utils.maths.Mat4;
import utils.maths.Vec3;

/**
 * Created by eirik on 08.05.2017.
 */
public class ColorShader extends Shader{

    public static final int VERTEX_LOCATION = 0;
    public static final int NORMALS_LOCATION = 1;
    public static final int COLORS_LOCATION = 2;


    private static final String UNIFORM_MODEL_TRANSFORM = "modelTransform";
    private static final String UNIFORM_VIEW_TRANSFORM = "viewTransform";
    private static final String UNIFORM_PROJECTION_TRANSFORM = "projectionTransform";
    private static final String UNIFORM_LIGHT = "lightPosition";

    private final int modelTransformLocation;
    private final int viewTransformLocation;
    private final int projectionTransformLocation;

    private final int pointLightLocation;


    private static String vertexPath = "shaders/light_shader.vert";
    private static String fragPath = "shaders/light_shader.frag";


    public ColorShader() {
        super(vertexPath, fragPath);

        modelTransformLocation = super.getUniformLocation(UNIFORM_MODEL_TRANSFORM);
        viewTransformLocation = super.getUniformLocation(UNIFORM_VIEW_TRANSFORM);
        projectionTransformLocation = super.getUniformLocation(UNIFORM_PROJECTION_TRANSFORM);

        pointLightLocation = super.getUniformLocation(UNIFORM_LIGHT);
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

    public void setLightPoint(Vec3 point) {
        super.setUniform3f(pointLightLocation, point);
    }
}
