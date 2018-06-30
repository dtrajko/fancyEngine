package game2D.world;

public class MovingTile extends Tile {

	private float minX = 0;
	private float maxX = 0;
	private float minY = 0;
	private float maxY = 0;
	private int directionX = 0;
	private int directionY = 0;
	private float speed = 1.0f;

	public MovingTile(String texture) {
		super(texture);
		minX = 100;
		maxX = 200;
		directionX = 1;
	}

	public void setRangeX(float min, float max) {
		this.minX = min;
		this.maxX = max;
	}

	public void setRangeY(float min, float max) {
		this.minY = min;
		this.maxY = max;
	}

	public void move() {
		
		if (minX == 0 && maxX == 0) directionX = 0;
		if (minY == 0 && maxY == 0) directionY = 0;

		x += directionX * speed;
		if (x <= minX) {
			x = minX;
			directionX = 1;
		}
		if (x >= maxX) {
			x = maxX;
			directionX = -1;
		}

		y += directionY * speed;
		if (y <= minY) {
			y = minY;
			directionY = 1;
		}
		if (y >= maxY) {
			y = maxY;
			directionY = -1;
		}
	}
}
