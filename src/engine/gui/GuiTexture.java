package engine.gui;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class GuiTexture {

	private int texture;
	private Vector3f position;
	private Vector3f rotation;
	private Vector2f scale;

	public GuiTexture(int texture, Vector3f position, Vector2f scale) {
		super();
		this.texture = texture;
		this.position = position;
		this.rotation = new Vector3f();
		this.scale = scale;
	}

	public int getTexture() {
		return texture;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setRotation(Vector3f vector) {
		rotation = vector;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public Vector2f getScale() {
		return scale;
	}
}
