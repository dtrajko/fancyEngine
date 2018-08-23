package engine.tm2.utils;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Represents a directional light in the scene. This has a direction, a colour,
 * and also indicates how much diffuse lighting should be used and how much
 * ambient lighting should be used.
 * 
 * @author Karl
 *
 */
public class Light {

	private Vector3f direction;
	private Color color;
	private Vector2f lightBias;// how much ambient light and how much diffuse light

	private Vector3f position;
	private Vector3f colorVector;
	private Vector3f attenuation = new Vector3f(1, 0, 0);

	public Light(Vector3f direction, Color color, Vector2f lightBias) {
		this.direction = direction;
		this.direction.normalize();
		this.color = color;
		this.colorVector = color.getVector();
		this.lightBias = lightBias;
		this.position = new Vector3f(-5000, 5000, -5000); // warning: hard- coded
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

	public Vector3f getColorVector() {
		return colorVector;
	}

	/**
	 * @return A vector with 2 float values. The x value is how much ambient
	 *         lighting should be used, and the y value is how much diffuse
	 *         lighting should be used.
	 */
	public Vector2f getLightBias() {
		return lightBias;
	}

	public Light(Vector3f position, Vector3f colorVector) {
		this.position = position;
		this.colorVector = colorVector;
	}

	public Light(Vector3f position, Vector3f colorVector, Vector3f attenuation) {
		this.position = position;
		this.colorVector = colorVector;
		this.attenuation = attenuation;
	}

	public Vector3f getAttenuation() {
		return attenuation;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setColor(Vector3f colorVector) {
		this.colorVector = colorVector;
	}
}
