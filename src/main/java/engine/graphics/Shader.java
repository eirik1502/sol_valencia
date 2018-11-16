package engine.graphics;

import org.lwjgl.opengl.GL20;

import utils.maths.Mat4;
import utils.maths.Vec3;
import utils.maths.Vec4;
import utils.ShaderUtils;

/**
 * Created by eirik on 13.06.2017.
 */
public abstract class Shader {

    private int programId;

    private boolean bound;


    public Shader(String vertex, String frag) {
        this.programId = ShaderUtils.loadShader(vertex, frag);
    }

    public void bind() {
        bound = true;
        GL20.glUseProgram(programId);
    }

    public void unbind() {
        bound = false;
        GL20.glUseProgram(0);
    }

    protected int getUniformLocation(String name) {
        int result = GL20.glGetUniformLocation(programId, name);
        if (result == -1)
            throw new IllegalStateException("Could not find uniform variable '" + name + "'!");
        return result;
    }


    protected void setUniform1i(int location, int value) {
        if (!bound) throw new IllegalStateException("Trying to use a shader while it is disabled");
        GL20.glUniform1i(location, value);
    }

    protected void setUniform1f(int location, float value) {
        if (!bound) throw new IllegalStateException("Trying to use a shader while it is disabled");
        GL20.glUniform1f(location, value);
    }

    protected void setUniform2f(int location, float x, float y) {
        if (!bound) throw new IllegalStateException("Trying to use a shader while it is disabled");
        GL20.glUniform2f(location, x, y);
    }

    protected void setUniform3f(int location, Vec3 vector) {
        if (!bound) throw new IllegalStateException("Trying to use a shader while it is disabled");
        GL20.glUniform3f(location, vector.x, vector.y, vector.z);
    }
    protected void setUniform4f(int location, Vec4 vector) {
        if (!bound) throw new IllegalStateException("Trying to use a shader while it is disabled");
        GL20.glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
    }

    protected void setUniformMat4f(int location, Mat4 matrix) {
        if (!bound) throw new IllegalStateException("Trying to use a shader while it is disabled");
        GL20.glUniformMatrix4fv(location, false, matrix.toFloatBuffer());
    }
}
