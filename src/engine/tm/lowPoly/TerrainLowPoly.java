package engine.tm.lowPoly;

import engine.interfaces.ITerrain;
import engine.tm.models.RawModel;
import engine.tm.openglObjects.Vao;
import engine.tm.settings.WorldSettings;
import engine.tm.textures.TerrainTexture;
import engine.tm.textures.TerrainTexturePack;

public class TerrainLowPoly implements ITerrain {

	private final Vao vao;
	private final int vertexCount;
	private final float[][] heights;
	private final float WATER_HEIGHT = WorldSettings.WATER_HEIGHT;
	private int x;
	private int z;

	public TerrainLowPoly(Vao vao, int vertexCount, float[][] heights) {
		this.vao = vao;
		this.vertexCount = vertexCount;
		this.heights = heights;
		this.x = 0;
		this.z = 0;
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
		int intX = (int) Math.floor(worldX - x + WorldSettings.WORLD_SIZE / 2);
		int intZ = (int) Math.floor(worldZ - z + WorldSettings.WORLD_SIZE / 2);
		float worldY = 0;
		if (intX < 0 || intX >= heights.length - 1 ||
			intZ < 0 || intZ >= heights.length - 1) {
			return worldY;
		}
		worldY = this.heights[intZ][intX];
		worldY += 0.2f; // a small adjustment
		if (worldY < WATER_HEIGHT) {
			worldY = WATER_HEIGHT;
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
