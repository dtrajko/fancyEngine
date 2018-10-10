package engine.tm.infiniteTerrain;

import java.util.ArrayList;
import java.util.List;

import engine.interfaces.ITerrain;
import engine.tm.entities.Entity;

public class InfiniteTerrainChunk {

	private ITerrain terrain;
	private int index_x;
	private int index_z;
	private List<Entity> terrainEntities;

	public InfiniteTerrainChunk(ITerrain terrain, int index_x, int index_z, List<Entity> terrainEntities) {
		this.terrain = terrain;
		this.index_x = index_x;
		this.index_z = index_z;
		this.terrainEntities = terrainEntities;
	}

	public InfiniteTerrainChunk(ITerrain terrain, int index_x, int index_z) {
		this(terrain, index_x, index_z, new ArrayList<Entity>());
	}

	public void addEntity(Entity entity) {
		terrainEntities.add(entity);
	}
 
	public List<Entity> getEntities() {
		return terrainEntities;
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
	
	public void cleanUp() {
		terrainEntities.clear();
	}
}
