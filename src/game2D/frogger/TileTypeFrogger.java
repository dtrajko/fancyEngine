package game2D.frogger;

import engine.interfaces.ITileType;

public class TileTypeFrogger implements ITileType {

	public static ITileType tileTypes[] = new TileTypeFrogger[16]; // range of RGB color channels
	public static byte not = 0; // number of tile types
	protected boolean solid;
	protected boolean nextLevel;
	protected boolean previousLevel;
	protected byte id;
	protected String texture;

	public static final ITileType tile_0  = new TileTypeFrogger("frogger/textures/black").setSolid();
	public static final ITileType tile_1  = new TileTypeFrogger("frogger/textures/purple");
	public static final ITileType tile_2  = new TileTypeFrogger("frogger/textures/green").setSolid();
	public static final ITileType tile_3  = new TileTypeFrogger("frogger/textures/asphalt");
	public static final ITileType tile_4  = new TileTypeFrogger("frogger/textures/dark_blue");

	public TileTypeFrogger(String texture) {
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
