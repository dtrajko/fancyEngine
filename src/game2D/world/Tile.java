package game2D.world;

import game2D.frogger.ITileType;
import game2D.frogger.TileTypeFrogger;

public class Tile {

	public ITileType tileType;

	private int x; // place in grid
	private int y; // place in grid

	protected float offsetX = 0;
	protected float offsetY = 0;

	private float minOffsetX = 0;
	private float maxOffsetX = 0;
	private float minOffsetY = 0;
	private float maxOffsetY = 0;
	private int offsetDirX = 0;
	private int offsetDirY = 0;
	private float speed = 0.05f;
	
	public float deltaX = 0;
	public float deltaY = 0;

	public Tile(ITileType bgTileType) {
		this.tileType = bgTileType;
		this.x = 0;
		this.y = 0;
	}
	
	public ITileType getType() {
		return tileType;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setOffsetX(int x) {
		this.offsetX = x;
	}

	public void setOffsetY(int y) {
		this.offsetY = y;
	}

	public float getOffsetX() {
		return this.offsetX;
	}

	public float getOffsetY() {
		return this.offsetY;
	}

	public void setOffsetDirectionX(int value) {
		this.offsetDirX = value;
	}

	public void setOffsetDirectionY(int value) {
		this.offsetDirY = value;
	}

	public int getOffsetDirectionX() {
		return this.offsetDirX;
	}

	public int getOffsetDirectionY() {
		return this.offsetDirY;
	}

	public void setOffsetRangeX(float min, float max) {
		this.minOffsetX = min;
		this.maxOffsetX = max;
	}

	public void setOffsetRangeY(float min, float max) {
		this.minOffsetY = min;
		this.maxOffsetY = max;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void move(IScene scene) {

		if (minOffsetX == 0 && maxOffsetX == 0) offsetDirX = 0;
		if (minOffsetY == 0 && maxOffsetY == 0) offsetDirY = 0;

		if (offsetDirX != 0) {

			offsetX += offsetDirX * speed;
			if (offsetX <= minOffsetX) {
				offsetX = minOffsetX;
				offsetDirX = -offsetDirX;
			}
			if (offsetX >= maxOffsetX) {
				offsetX = maxOffsetX;
				offsetDirX = -offsetDirX;
			}
			
		}
		if (offsetDirY != 0) {
			offsetY += offsetDirY * speed;
			if (offsetY <= minOffsetY) {
				offsetY = minOffsetY;
				offsetDirY = -offsetDirY;
			}
			if (offsetY >= maxOffsetY) {
				offsetY = maxOffsetY;
				offsetDirY = -offsetDirY;
			}
		}

		deltaX = offsetDirX * speed;
		deltaY = offsetDirY * speed;
	}
}
