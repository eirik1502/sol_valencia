package engine.window;


import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.*;

import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;


/**
 * Created by eirik on 13.06.2017.
 */
public class Window {

    private boolean GLFWinitialized = false;
    private boolean GLinitialized = false;


    private long windowId;

    private float width, height;


    public Window(float relMonitorWidthSize, float relMonitorHeightSize, String title) {

        initGLFW();
        createWindow(relMonitorWidthSize, relMonitorHeightSize, title);
        initGL();

        this.width = width;
        this.height = height;
    }
    public Window(float relMonitorSize, String title) {
        initGLFW();
        createWindow(relMonitorSize, title);
        initGL();
    }
    public Window(String title) {

        initGLFW();
        createFullscreenWindow(title);
        initGL();
    }

    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }

    public void hide() {
        glfwHideWindow(windowId);
    }
    public void show() {
        glfwShowWindow(windowId);
    }
    public void focus() {
        glfwFocusWindow(windowId);
    }
    public void minimize() {
        glfwIconifyWindow(windowId);
    }

    public void initGLFW() {
        if (!glfwInit()) {
            throw new IllegalStateException("Could not initialize GLFW!");
        }
        GLFWErrorCallback.createPrint(System.err).set();

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will not be resizable

        GLFWinitialized = true;
    }
    public void createFullscreenWindow(String title) {
        long primaryMonitor = glfwGetPrimaryMonitor();
        GLFWVidMode vidmode = glfwGetVideoMode(primaryMonitor);

        int iwidth = vidmode.width();
        int iheight = vidmode.height();

        if (!GLFWinitialized) throw new IllegalStateException("Cannot create window because GLFW is not initialized");

        windowId =  glfwCreateWindow(iwidth, iheight, title, primaryMonitor, NULL);

        if (windowId == NULL) {
            throw new IllegalStateException("Could not create GLFW window!");
        }
        System.out.println("Created GLFW window");

        this.width = iwidth;
        this.height = iheight;

        glfwShowWindow(windowId);

    }

    public void createWindow(float relMonitorSize, String title) {
        createWindow(relMonitorSize, relMonitorSize, title);
    }
    public void createWindow(float relMonitorWidthSize, float relMonitorHeightSize, String title) {

        long primaryMonitor = glfwGetPrimaryMonitor();
        GLFWVidMode vidmode = glfwGetVideoMode(primaryMonitor);

        int iwidth = (int)((float)vidmode.width() * relMonitorWidthSize);
        int iheight = (int)((float)vidmode.height() * relMonitorHeightSize);

        if (!GLFWinitialized) throw new IllegalStateException("Cannot create window because GLFW is not initialized");

        windowId =  glfwCreateWindow(iwidth, iheight, title, NULL, NULL);

        if (windowId == NULL) {
            throw new IllegalStateException("Could not create GLFW window!");
        }

        System.out.println("Created GLFW window");

        int width, height;

        //center window
        try (MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(windowId, pWidth, pHeight);

            width = pWidth.get(0);
            height = pHeight.get(0);

            // Center our window
            glfwSetWindowPos(
                    windowId,
                    (vidmode.width() - width) / 2,
                    (vidmode.height() - height) / 2
            );
        }
        this.width = width;
        this.height = height;

        glfwShowWindow(windowId);
    }

    public void initGL() {
        if (windowId == -1) throw new IllegalStateException("cannot init OpenGL before a window is created");

        glfwMakeContextCurrent(windowId);

        glfwSwapInterval(1);// Enable v-sync

        GL.createCapabilities(); //get opengl context

        //print version
        System.out.println("OpenGL: " + glGetString(GL_VERSION));


        glEnable(GL_DEPTH_TEST);
        glActiveTexture(GL_TEXTURE0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        glfwSwapBuffers(windowId);

        GLinitialized = true;
    }

    public void pollEvents() {
        glfwPollEvents();
    }

    public void swapBuffers() {
        glfwSwapBuffers(windowId);
    }

    public void close() {
        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);
    }


    public boolean shouldClosed() {
        return glfwWindowShouldClose(windowId);
    }

    public void setMouseButtonCallback(GLFWMouseButtonCallbackI call) {
        glfwSetMouseButtonCallback( windowId, call);
    }

    public void setCursorPosCallback(GLFWCursorPosCallbackI call) {
        glfwSetCursorPosCallback( windowId, call);
    }

    public void setKeyCallback(GLFWKeyCallbackI call) {
        glfwSetKeyCallback( windowId, call);
    }

    public static void terminateGLFW() {
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
