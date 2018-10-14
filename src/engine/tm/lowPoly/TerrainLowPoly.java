package engine.tm.lowPoly;

import engine.interfaces.ITerrain;
import engine.tm.models.RawModel;
import engine.tm.openglObjects.Vao;
import engine.tm.scene.SceneLowPoly;
import engine.tm.settings.WorldSettingsLowPoly;
import engine.tm.textures.TerrainTexture;
import engine.tm.textures.TerrainTexturePack;

public class TerrainLowPoly implements ITerrain {

	private final Vao vao;
	private final int vertexCount;
	private final float[][] heights;
	private float x;
	private float z;
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
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		int intX = (int) (terrainX / scale);
		int intZ = (int) (terrainZ / scale);
		intX += WorldSettingsLowPoly.WORLD_SIZE / 2;
		intZ += WorldSettingsLowPoly.WORLD_SIZE / 2;
		int worldY = 0;
		if (intX < 0 || intX >= heights.length - 1 ||
			intZ < 0 || intZ >= heights.length - 1) {
			return worldY;
		}
		worldY = (int) this.heights[intZ][intX];
		worldY *= scale;
		worldY += 2.0f; // a small adjustment
		
		if (worldY < WorldSettingsLowPoly.WATER_HEIGHT + SceneLowPoly.waterLevelOffset) {
			worldY = (int) (WorldSettingsLowPoly.WATER_HEIGHT + SceneLowPoly.waterLevelOffset);
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

	@Override
	public float getWaterHeight() {
		return WorldSettingsLowPoly.WATER_HEIGHT + SceneLowPoly.getWaterLevelOffset();
	}
}
