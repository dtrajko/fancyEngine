package engine.tm.infiniteTerrain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import engine.tm.settings.WorldSettings;

public class InfiniteTerrainManager {

	private double visibleDistance = WorldSettings.GRID_SIZE;

	private static List<InfiniteTerrainChunk> terrainChunks = new ArrayList<InfiniteTerrainChunk>();
	private static List<InfiniteWaterChunk> waterChunks = new ArrayList<InfiniteWaterChunk>();

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
		Iterator<InfiniteTerrainChunk> iterator = terrainChunks.iterator();
		while(iterator.hasNext()){
			InfiniteTerrainChunk chunk = iterator.next();
			double distance = Math.sqrt(Math.pow(centerX - chunk.getIndexX(), 2) + Math.pow(centerZ - chunk.getIndexZ(), 2));
			if (distance > visibleDistance) {
				remoteTerrainChunks.add(chunk);
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
}
