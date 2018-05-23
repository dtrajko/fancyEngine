package engine.graph;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import engine.Window;

public class MouseInput {

	private long window;
    private final Vector2d previousPos;
    private final Vector2d currentPos;
    private final Vector2f displVec;
    private boolean inWindow = false;
    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;
    
	private boolean keys[];
	private boolean buttons[];

    public MouseInput() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();
    }

    public void init(Window window) {

    	this.window = window.getHandle();

        GLFW.glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;
        });
        GLFW.glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, entered) -> {
            inWindow = entered;
        });
        GLFW.glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mode) -> {
            leftButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
            rightButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;
        });
        
		this.keys = new boolean[GLFW.GLFW_KEY_LAST];
		for (int k = 32; k < GLFW.GLFW_KEY_LAST; k++) {
			this.keys[k] = false;
		}
		this.buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
		for (int mb = 0; mb < GLFW.GLFW_MOUSE_BUTTON_LAST; mb++) {
			this.buttons[mb] = false;
		}
		
		GLFW.glfwSetInputMode(this.window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public Vector2d getCurrentPos() {
        return currentPos;        
    }

    public void input(Window window) {
        displVec.x = 0;
        displVec.y = 0;
        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
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
