package engine.tm.infiniteTerrain;

import engine.tm.lowPoly.WaterTileLowPoly;

public class InfiniteWaterChunk {

	private WaterTileLowPoly waterTile;
	private int index_x;
	private int index_z;
	
	public InfiniteWaterChunk(WaterTileLowPoly waterTile, int index_x, int index_z) {
		this.waterTile = waterTile;
		this.index_x = index_x;
		this.index_z = index_z;
	}

	public int getIndexX() {
		return this.index_x;
	}

	public int getIndexZ() {
		return this.index_z;
	}

	public WaterTileLowPoly getWaterTile() {
		return waterTile;
	}
}
