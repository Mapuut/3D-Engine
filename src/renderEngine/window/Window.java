package renderEngine.window;


import Debug.Exceptions.OldComputerException;
import Debug.FpsCounter;
import Input.Keyboard;
import Input.Mouse;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_DITHER;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window{

    public static final boolean DEBUG = true;
    public FpsCounter fpsCounter = new FpsCounter();

    public static int WIDTH = 1200 ;
    public static int HEIGHT = 700;

    public static boolean fullscreen = false;

    public long window_ID;
    public Keyboard keyboard;
    public Mouse mouse;

    public Window() throws OldComputerException {
        try {
            init();
        } catch (OldComputerException e) {
            throw e;
        }
    }

    public void update() {
        fpsCounter.update();
    }

    public void render() {
        fpsCounter.render();
    }

    public static void terminate(long window_ID) {
        glfwFreeCallbacks(window_ID);
        glfwDestroyWindow(window_ID);
    }

    public  void terminate() {
        glfwFreeCallbacks(window_ID);
        glfwDestroyWindow(window_ID);
    }


    protected void init() throws OldComputerException {

        setErrorCallback();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() ) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default

        //TODO
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        //

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE);

        if (fullscreen) {
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
            glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
            // Create the window
            window_ID = glfwCreateWindow(vidmode.width() + 1, vidmode.height() + 1, "3D Engine", NULL, NULL);
            if (window_ID == NULL) {
                throw new OldComputerException("Failed to create the GLFW window (openGL 3.2)");
            }


            setKeyCallback();
            setMouseCallback();
            setWindowSizeCallback();

        } else {


            // Create the window
            window_ID = glfwCreateWindow(WIDTH, HEIGHT, "3D Engine", NULL, NULL);
            if (window_ID == NULL) {
                throw new OldComputerException("Failed to create the GLFW window (openGL 3.2)");
            }

            glfwSetInputMode(window_ID, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            setKeyCallback();
            setMouseCallback();
            setWindowSizeCallback();

            // Get the thread stack and push a new frame
            try (MemoryStack stack = stackPush()) {
                IntBuffer pWidth = stack.mallocInt(1); // int*
                IntBuffer pHeight = stack.mallocInt(1); // int*

                // Get the window size passed to glfwCreateWindow
                glfwGetWindowSize(window_ID, pWidth, pHeight);

                // Get the resolution of the primary monitor
                GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

                // Center the window
                glfwSetWindowPos(
                        window_ID,
                        (vidmode.width() - pWidth.get(0)) / 2,
                        (vidmode.height() - pHeight.get(0)) / 2
                );
            } // the stack frame is popped automatically
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(window_ID);


        // Enable v-sync
        //lfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window_ID);

        GL.createCapabilities();

    }

    protected void setWindowSizeCallback() {
        glfwSetFramebufferSizeCallback(window_ID, new WindowSizeHandler());
    }

    protected void setKeyCallback() {
        keyboard = new Keyboard();
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window_ID, keyboard);
    }

    protected void setMouseCallback() {
        mouse = new Mouse();

        glfwSetCursorPosCallback(window_ID, mouse);
    }

    protected void setErrorCallback() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();
    }

}
