package game2D.world;

public class MovingTile extends Tile {

	private int minX = 0;
	private int maxX = 0;
	private int minY = 0;
	private int maxY = 0;
	private int directionX = 0;
	private int directionY = 0;
	private int speed = 1;

	public MovingTile(String texture) {
		super(texture);
		minX = 100;
		maxX = 200;
		directionX = 1;
	}
	
	public void move() {
		
		if (minX == 0 && maxX == 0) directionX = 0;
		if (minY == 0 && maxY == 0) directionY = 0;

		x += directionX;
		if (x <= minX) {
			x = minX;
			directionX = 1;
		}
		if (x >= maxX) {
			x = maxX;
			directionX = -1;
		}

		y += directionY;
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
