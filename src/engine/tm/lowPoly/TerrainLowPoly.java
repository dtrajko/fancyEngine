package engine.tm.lowPoly;

import engine.interfaces.ITerrain;
import engine.tm.models.RawModel;
import engine.tm.openglObjects.Vao;
import engine.tm.scene.SceneLowPoly;
import engine.tm.settings.WorldSettings;
import engine.tm.textures.TerrainTexture;
import engine.tm.textures.TerrainTexturePack;

public class TerrainLowPoly implements ITerrain {

	private final Vao vao;
	private final int vertexCount;
	private final float[][] heights;
	private int x;
	private int z;
	private float scale;

	public TerrainLowPoly(Vao vao, int vertexCount, float[][] heights, float scale) {
		this.vao = vao;
		this.vertexCount = vertexCount;
		this.heights = heights;
		this.x = 0;
		this.z = 0;
		this.scale = scale;
	}

	public Vao getVao() {
		return vao;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public float[][] getHeights() {
		return heights;
	}

	public void delete() {
		vao.delete();
	}

	public float getHeightOfTerrain(float worldX, float worldZ) {
		float terrainX = (float) (worldX - Math.round(worldX / (WorldSettings.WORLD_SIZE * scale)) * (WorldSettings.WORLD_SIZE * scale));
		float terrainZ = (float) (worldZ - Math.round(worldZ / (WorldSettings.WORLD_SIZE * scale)) * (WorldSettings.WORLD_SIZE * scale));
		int intX = (int) Math.floor((terrainX - x) / scale + WorldSettings.WORLD_SIZE / 2);
		int intZ = (int) Math.floor((terrainZ - z) / scale + WorldSettings.WORLD_SIZE / 2);
		float worldY = 0;
		if (intX < 0 || intX >= heights.length - 1 ||
			intZ < 0 || intZ >= heights.length - 1) {
			return worldY;
		}
		worldY = this.heights[intZ][intX];
		worldY *= scale;
		worldY += 0.2f; // a small adjustment
		if (worldY < WorldSettings.WATER_HEIGHT + SceneLowPoly.waterLevelOffset) {
			worldY = WorldSettings.WATER_HEIGHT + SceneLowPoly.waterLevelOffset;
		}
		return worldY;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setZ(int z) {
		this.z = z;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getZ() {
		return z;
	}

	@Override
	public RawModel getModel() {
		return null;
	}

	@Override
	public TerrainTexturePack getTexturePack() {
		return null;
	}

	@Override
	public TerrainTexture getBlendMap() {
		return null;
	}
}
