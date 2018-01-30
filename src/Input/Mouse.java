package Input;

import org.lwjgl.glfw.GLFWCursorPosCallback;

public class Mouse extends GLFWCursorPosCallback {


    public static int mouseX = 0;
    public static int mouseY = 0;

    public static int mouseXLast = 0;
    public static int mouseYLast = 0;

    public static int mouseXCurrent = 0;
    public static int mouseYCurrent = 0;

    @Override
    public void invoke(long window, double xpos, double ypos) {
        mouseXCurrent = (int) xpos;
        mouseYCurrent = (int) ypos;
    }


    public static void update() {
        mouseXLast = mouseX;
        mouseYLast = mouseY;

        mouseX = mouseXCurrent;
        mouseY = mouseYCurrent;
    }


    public static int mouseMovedX(){
        return mouseX - mouseXLast;
    }


    public static int mouseMovedY(){
        return mouseY - mouseYLast;
    }

    public static void reset(){
        mouseXLast = mouseX;
        mouseYLast = mouseY;
    }
}
