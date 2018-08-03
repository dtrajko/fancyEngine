package game2D.world;

public class Tile {

	public static Tile tiles[] = new Tile[16]; // range of RGB color channels
	public static byte not = 0; // number of tiles
	private boolean solid;
	private boolean nextLevel;
	private boolean previousLevel;

	protected float offsetX = 0;
	protected float offsetY = 0;

	private float minOffsetX = 0;
	private float maxOffsetX = 0;
	private float minOffsetY = 0;
	private float maxOffsetY = 0;
	private int offsetDirX = 0;
	private int offsetDirY = 0;
	private float speed = 0.001f;

	public static final Tile tile_0  = new Tile("brick_wall");
	public static final Tile tile_1  = new Tile("stone").setSolid();
	public static final Tile tile_2  = new Tile("wall").setSolid();
	public static final Tile tile_3  = new Tile("lava");
	public static final Tile tile_4  = new Tile("door").setNextLevel();
	public static final Tile tile_5  = new Tile("sky");
	public static final Tile tile_6  = new Tile("water");
	public static final Tile tile_7  = new Tile("dirt").setSolid();
	public static final Tile tile_8  = new Tile("dirt_grass").setSolid();
	public static final Tile tile_9  = new Tile("door").setPreviousLevel();
	public static final Tile tile_10 = new Tile("mario_tile").setSolid();

	private byte id;
	private String texture;

	public Tile(String texture) {
		this.id = not;
		not++;
		this.texture = texture;
		this.solid = false;
		if (tiles[id] != null) {
			throw new IllegalStateException("Tile slot [" + id + "] is already being used!");
		}		
		tiles[id] = this;
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

	public void move() {

		if (this.id == 10) this.offsetX += 0.01f;

		/*
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
		*/
	}

	public Tile setSolid() {
		this.solid = true;
		return this;
	}

	public Tile setNextLevel() {
		this.nextLevel = true;
		return this;
	}

	public Tile setPreviousLevel() {
		this.previousLevel = true;
		return this;
	}

	public boolean isSolid() { return solid; }
	public boolean isNextLevel() { return nextLevel; }
	public boolean isPreviousLevel() { return previousLevel; }
	public byte getId() { return id; }
	public String getTexture() { return texture; }	
}
