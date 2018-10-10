package engine.tm.infiniteTerrain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import engine.tm.settings.WorldSettingsLowPoly;

public class InfiniteTerrainManager {

	private double visibleDistance = WorldSettingsLowPoly.GRID_SIZE + 1;

	private List<InfiniteTerrainChunk> terrainChunks = new ArrayList<InfiniteTerrainChunk>();
	private List<InfiniteWaterChunk> waterChunks = new ArrayList<InfiniteWaterChunk>();

	public void addTerrainChunk(InfiniteTerrainChunk chunk) {
		if (!terrainChunkExists(chunk)) {
			terrainChunks.add(chunk);			
		}
	}

	public void addWaterChunk(InfiniteWaterChunk waterChunk) {
		if (!waterChunkExists(waterChunk)) {
			waterChunks.add(waterChunk);			
		}
	}

	public boolean terrainChunkExists(InfiniteTerrainChunk newChunk) {
		for (InfiniteTerrainChunk chunk : terrainChunks) {
			if (chunk.getIndexX() == newChunk.getIndexX() &&
				chunk.getIndexZ() == newChunk.getIndexZ()) {
				return true;
			}
		}
		return false;
	}

	public boolean terrainChunkExists(int indX, int indZ) {
		for (InfiniteTerrainChunk chunk : terrainChunks) {
			if (chunk.getIndexX() == indX &&
				chunk.getIndexZ() == indZ) {
				return true;
			}
		}
		return false;
	}

	private boolean waterChunkExists(InfiniteWaterChunk newChunk) {
		for (InfiniteWaterChunk chunk : waterChunks) {
			if (chunk.getIndexX() == newChunk.getIndexX() &&
				chunk.getIndexZ() == newChunk.getIndexZ()) {
				return true;
			}
		}
		return false;
	}

	public boolean waterChunkExists(int indX, int indZ) {
		for (InfiniteWaterChunk chunk : waterChunks) {
			if (chunk.getIndexX() == indX &&
				chunk.getIndexZ() == indZ) {
				return true;
			}
		}
		return false;
	}

	public void removeTerrainChunk(InfiniteTerrainChunk chunk) {
		terrainChunks.remove(chunk);
	}

	public void removeWaterChunk(InfiniteWaterChunk chunk) {
		waterChunks.remove(chunk);
	}

	public List<InfiniteTerrainChunk> getRemoteTerrainChunks(int centerX, int centerZ) {
		List<InfiniteTerrainChunk> remoteTerrainChunks = new ArrayList<InfiniteTerrainChunk>();		
		for (InfiniteTerrainChunk terrainChunk : terrainChunks) {
			double distance = Math.sqrt(Math.pow(centerX - terrainChunk.getIndexX(), 2) + Math.pow(centerZ - terrainChunk.getIndexZ(), 2));
			if (distance > visibleDistance) {
				remoteTerrainChunks.add(terrainChunk);
			}
		}
		return remoteTerrainChunks;
	}

	public List<InfiniteWaterChunk> getRemoteWaterChunks(int centerX, int centerZ) {
		List<InfiniteWaterChunk> remoteWaterChunks = new ArrayList<InfiniteWaterChunk>();
		Iterator<InfiniteWaterChunk> iterator = waterChunks.iterator();
		while(iterator.hasNext()){
			InfiniteWaterChunk waterChunk = iterator.next();
			double distance = Math.sqrt(Math.pow(centerX - waterChunk.getIndexX(), 2) + Math.pow(centerZ - waterChunk.getIndexZ(), 2));
			if (distance > visibleDistance) {
				remoteWaterChunks.add(waterChunk);
			}
		}
		return remoteWaterChunks;
	}

	public List<InfiniteTerrainChunk> getVisibleTerrainChunks() {
		return terrainChunks;
	}

	public List<InfiniteWaterChunk> getVisibleWaterChunks() {
		return waterChunks;
	}

	public InfiniteTerrainChunk getTerrainChunk(int indX, int indZ) {
		for (InfiniteTerrainChunk terrainChunk : terrainChunks) {
			if (indX == terrainChunk.getIndexX() && indZ == terrainChunk.getIndexZ()) {
				return terrainChunk;
			}
		}
		return null;
	}
}
