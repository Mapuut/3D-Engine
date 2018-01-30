package entities;


import Input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private Vector3f position = new Vector3f(0, 10, 0);
    public float pitch;
    public float yaw= 90;
    public float roll;

    public Camera() {

    }


    private static final float SPEED = 0.1F;
    public void move() {
        if (Keyboard.isKeyDown(GLFW_KEY_W)) {
            position.x += Math.sin(Math.toRadians(yaw)) * SPEED;
            position.z -= Math.cos(Math.toRadians(yaw)) * SPEED;
            position.y -= Math.sin(Math.toRadians(pitch)) * SPEED;
        }
        if (Keyboard.isKeyDown(GLFW_KEY_S)) {
            position.x -= Math.sin(Math.toRadians(yaw)) * SPEED;
            position.z += Math.cos(Math.toRadians(yaw)) * SPEED;
            position.y += Math.sin(Math.toRadians(pitch)) * SPEED;
        }

        if (Keyboard.isKeyDown(GLFW_KEY_Q)) {
            position.y  += SPEED;
        }
        if (Keyboard.isKeyDown(GLFW_KEY_E)) {
            position.y -= SPEED;
        }

        if (Keyboard.isKeyDown(GLFW_KEY_D)) {
            position.x += Math.sin(Math.toRadians(yaw + 90)) * SPEED;
            position.z -= Math.cos(Math.toRadians(yaw + 90)) * SPEED;
        }
        if (Keyboard.isKeyDown(GLFW_KEY_A)) {
            position.x += Math.sin(Math.toRadians(yaw - 90)) * SPEED;
            position.z -= Math.cos(Math.toRadians(yaw - 90)) * SPEED;
        }

        if(position.y < 1f ) {
            position.y = 1f;
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}
