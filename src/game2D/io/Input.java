package game2D.io;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class Input {
	
	private long window;
	private boolean keys[];
	private boolean buttons[];

	public Input(long window) {
		this.window = window;
		this.keys = new boolean[GLFW.GLFW_KEY_LAST];
		for (int k = 32; k < GLFW.GLFW_KEY_LAST; k++) {
			this.keys[k] = false;
		}
		this.buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
		for (int mb = 0; mb < GLFW.GLFW_MOUSE_BUTTON_LAST; mb++) {
			this.buttons[mb] = false;
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

	public void update() {
		for (int k = 32; k < GLFW.GLFW_KEY_LAST; k++) {
			this.keys[k] = isKeyDown(k);
		}
		for (int mb = 0; mb < GLFW.GLFW_MOUSE_BUTTON_LAST; mb++) {
			this.buttons[mb] = isMouseButtonDown(mb);
		}
	}

	public void handle(long window) {
		// Keyboard events
		if (isKeyDown(GLFW.GLFW_KEY_A)) {
			// pass
		}
		if (isKeyDown(GLFW.GLFW_KEY_D)) {
			// pass
		}
		if (isKeyDown(GLFW.GLFW_KEY_W)) {
			// pass
		}
		if (isKeyDown(GLFW.GLFW_KEY_S)) {
			// pass
		}
		if (isKeyDown(GLFW.GLFW_KEY_F)) {
			// pass
		}
		if (isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
			GLFW.glfwSetWindowShouldClose(window, true);
		}
	}
}
