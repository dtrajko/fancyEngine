package engine.tm.infiniteTerrain;

import engine.interfaces.ITerrain;

public class InfiniteTerrainChunk {

	private ITerrain terrain;
	private int index_x;
	private int index_z;
	
	public InfiniteTerrainChunk(ITerrain terrain, int index_x, int index_z) {
		this.terrain = terrain;
		this.index_x = index_x;
		this.index_z = index_z;
	}

	public int getIndexX() {
		return this.index_x;
	}

	public int getIndexZ() {
		return this.index_z;
	}

	public ITerrain getTerrain() {
		return terrain;
	}
}
