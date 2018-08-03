package game2D.world;

public class TileType {

	public static TileType tileTypes[] = new TileType[16]; // range of RGB color channels
	public static byte not = 0; // number of tile types
	private boolean solid;
	private boolean nextLevel;
	private boolean previousLevel;

	public static final TileType tile_0  = new TileType("brick_wall");
	public static final TileType tile_1  = new TileType("stone").setSolid();
	public static final TileType tile_2  = new TileType("wall").setSolid();
	public static final TileType tile_3  = new TileType("lava");
	public static final TileType tile_4  = new TileType("door").setNextLevel();
	public static final TileType tile_5  = new TileType("sky");
	public static final TileType tile_6  = new TileType("water");
	public static final TileType tile_7  = new TileType("dirt").setSolid();
	public static final TileType tile_8  = new TileType("dirt_grass").setSolid();
	public static final TileType tile_9  = new TileType("door").setPreviousLevel();
	public static final TileType tile_10 = new TileType("mario_tile").setSolid();

	private byte id;
	private String texture;

	public TileType(String texture) {
		this.id = not;
		not++;
		this.texture = texture;
		this.solid = false;
		if (tileTypes[id] != null) {
			throw new IllegalStateException("Tile slot [" + id + "] is already being used!");
		}		
		tileTypes[id] = this;
	}

	public TileType setSolid() {
		this.solid = true;
		return this;
	}

	public TileType setNextLevel() {
		this.nextLevel = true;
		return this;
	}

	public TileType setPreviousLevel() {
		this.previousLevel = true;
		return this;
	}

	public boolean isSolid() { return solid; }
	public boolean isNextLevel() { return nextLevel; }
	public boolean isPreviousLevel() { return previousLevel; }
	public byte getId() { return id; }
	public String getTexture() { return texture; }	
}
