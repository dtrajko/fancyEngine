package engine.tm.entities;

import org.joml.Vector2f;
import org.joml.Vector3f;
import engine.tm.utils.Color;

/**
 * Represents a directional light in the scene. This has a direction, a colour,
 * and also indicates how much diffuse lighting should be used and how much
 * ambient lighting should be used.
 * 
 * @author Karl
 *
 */
public class LightDirectional {

	private Vector3f direction;
	private Vector2f lightBias;// how much ambient light and how much diffuse light
	private Color color;

	public LightDirectional(Vector3f direction, Color color, Vector2f lightBias) {
		this.direction = direction;
		this.direction.normalize();
		this.color = color;
		this.lightBias = lightBias;
	}

	public void setDirection(Vector3f dir) {
		direction.set(dir);
		direction.normalize();
	}

	public Vector3f getDirection() {
		return direction;
	}

	public Color getColor() {
		return color;
	}

	/**
	 * @return A vector with 2 float values. The x value is how much ambient
	 *         lighting should be used, and the y value is how much diffuse
	 *         lighting should be used.
	 */
	public Vector2f getLightBias() {
		return lightBias;
	}
}
