package game2D.collision;

import org.joml.Vector2f;

public class Collision {
	
	public Vector2f distance;
	public boolean isIntersecting;
	
	public static final int BOUNCE_DIR_UP = 1;
	public static final int BOUNCE_DIR_DOWN = -1;
	public static final int BOUNCE_DIR_NONE = 0;

	public Collision(Vector2f distance, boolean isIntersecting) {
		this.distance = distance;
		this.isIntersecting = isIntersecting;
	}
}
