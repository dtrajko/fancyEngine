package game2D.world;

public class Tile {

	public static Tile tiles[] = new Tile[16]; // range of RGB color channels
	public static byte not = 0; // number of tiles
	private boolean solid;
	private boolean nextLevel;
	private boolean previousLevel;

	public static final Tile tile_0 = new Tile("brick_wall");
	public static final Tile tile_1 = new Tile("stone").setSolid();
	public static final Tile tile_2 = new Tile("wall").setSolid();
	public static final Tile tile_3 = new Tile("lava");
	public static final Tile tile_4 = new Tile("door").setNextLevel();
	public static final Tile tile_5 = new Tile("sky");
	public static final Tile tile_6 = new Tile("water");
	public static final Tile tile_7 = new Tile("dirt").setSolid();
	public static final Tile tile_8 = new Tile("dirt_grass").setSolid();
	public static final Tile tile_9 = new Tile("door").setPreviousLevel();

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
