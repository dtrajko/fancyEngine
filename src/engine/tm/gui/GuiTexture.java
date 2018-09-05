package engine.tm.gui;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class GuiTexture {

	private int texture;
	private Vector2f position;
	private Vector2f rotation;
	private Vector2f scale;

	public GuiTexture(int texture, Vector2f position, Vector2f scale) {
		super();
		this.texture = texture;
		this.position = position;
		this.rotation = new Vector2f();
		this.scale = scale;
	}

	public int getTexture() {
		return texture;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setRotation(Vector2f vector) {
		rotation = vector;
	}

	public Vector2f getRotation() {
		return rotation;
	}

	public Vector2f getScale() {
		return scale;
	}
}
