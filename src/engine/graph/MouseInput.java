package engine.graph;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import engine.Window;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private final Vector2d previousPos;
    private final Vector2d currentPos;
    private final Vector2f displVec;
    private boolean inWindow = false;
    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;
    private boolean keys[];
    private boolean buttons[];
    private long window;
    private float mouseWheelDirection;
    // private float mouseWheelDirectionPrev;

    public MouseInput(Window window) {
        previousPos = new Vector2d(0, 0);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();
    }

    public void init(Window window) {

    	this.window = window.getWindowHandle();

    	if (window.getOptions().mode3D) {
    		GLFW.glfwSetInputMode(this.window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);    		
    		GLFW.glfwSetCursorPos(this.window, window.getWidth() / 2, window.getHeight() / 2);
    	}

        glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;
        });
        glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, entered) -> {
            inWindow = entered;
        });
        glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mode) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
        glfwSetScrollCallback(window.getWindowHandle(), (windowHandle, dx, dy) -> {
                mouseWheelDirection = (float) dy;
        });

		this.keys = new boolean[GLFW.GLFW_KEY_LAST];
		for (int k = 32; k < GLFW.GLFW_KEY_LAST; k++) {
			this.keys[k] = false;
		}
		this.buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
		for (int mb = 0; mb < GLFW.GLFW_MOUSE_BUTTON_LAST; mb++) {
			this.buttons[mb] = false;
		}
    }

    public Vector2f getDisplVec() {
        return displVec;
    }
    
    public Vector2d getMousePosition() {
    	return this.currentPos;
    }
    
    public float getMouseWheelDelta() {
    	float mouseWheelDelta = mouseWheelDirection;
    	mouseWheelDirection = 0;
    	return mouseWheelDelta;
    }

    public void input(Window window) {
        displVec.x = 0;
        displVec.y = 0;
        if (previousPos.x != 0 && previousPos.y != 0) {
            double deltax = currentPos.x - previousPos.x;
            double deltay = currentPos.y - previousPos.y;
            boolean rotateX = deltax != 0;
            boolean rotateY = deltay != 0;
            if (rotateX) {
                displVec.y = (float) deltax;
            }
            if (rotateY) {
                displVec.x = (float) deltay;
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }

	public void update(Window window) {
		for (int k = 32; k < GLFW.GLFW_KEY_LAST; k++) {
			this.keys[k] = isKeyDown(k);
		}
		for (int mb = 0; mb < GLFW.GLFW_MOUSE_BUTTON_LAST; mb++) {
			this.buttons[mb] = isMouseButtonDown(mb);
		}
	}

	public boolean isKeyDown(int key) {
		return GLFW.glfwGetKey(window, key) == GL11.GL_TRUE;
	}

	public boolean isKeyPressed(int key) {
		return isKeyDown(key) && !keys[key];
	}

	public boolean isKeyReleased(int key) {
		return !isKeyDown(key) && keys[key];
	}

	public boolean isMouseButtonDown(int button) {
		return GLFW.glfwGetMouseButton(window, button) == GL11.GL_TRUE;
	}

	public boolean isMouseButtonPressed(int button) {
		return isMouseButtonDown(button) && !buttons[button];
	}

	public boolean isMouseButtonReleased(int button) {
		return !isMouseButtonDown(button) && buttons[button];
	}
}
