package game2D.world;

import game2D.frogger.ITileType;

public class TileType2D implements ITileType {

	public static ITileType tileTypes[] = new TileType2D[16]; // range of RGB color channels
	public static byte not = 0; // number of tile types
	protected boolean solid;
	protected boolean nextLevel;
	protected boolean previousLevel;
	protected byte id;
	protected String texture;

	public static final ITileType tile_0  = new TileType2D("textures/game2D/brick_wall");
	public static final ITileType tile_1  = new TileType2D("textures/game2D/stone").setSolid();
	public static final ITileType tile_2  = new TileType2D("textures/game2D/wall").setSolid();
	public static final ITileType tile_3  = new TileType2D("textures/game2D/lava");
	public static final ITileType tile_4  = new TileType2D("textures/game2D/door").setNextLevel();
	public static final ITileType tile_5  = new TileType2D("textures/game2D/sky");
	public static final ITileType tile_6  = new TileType2D("textures/game2D/water");
	public static final ITileType tile_7  = new TileType2D("textures/game2D/dirt").setSolid();
	public static final ITileType tile_8  = new TileType2D("textures/game2D/dirt_grass").setSolid();
	public static final ITileType tile_9  = new TileType2D("textures/game2D/door").setPreviousLevel();
	public static final ITileType tile_10 = new TileType2D("textures/game2D/mario_tile").setSolid();

	public TileType2D(String texture) {
		this.id = not;
		not++;
		this.texture = texture;
		this.solid = false;
		if (tileTypes[id] != null) {
			throw new IllegalStateException("Tile slot [" + id + "] is already being used!");
		}		
		tileTypes[id] = this;
	}

	public ITileType setSolid() {
		this.solid = true;
		return this;
	}

	public ITileType setNextLevel() {
		this.nextLevel = true;
		return this;
	}

	public ITileType setPreviousLevel() {
		this.previousLevel = true;
		return this;
	}

	public boolean isSolid() { return solid; }
	public boolean isNextLevel() { return nextLevel; }
	public boolean isPreviousLevel() { return previousLevel; }
	public byte getId() { return id; }
	public String getTexture() { return texture; }	
}
