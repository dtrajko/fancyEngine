package engine.thinmatrix.terrains;

import org.joml.Vector4f;

import engine.graph.ICamera;
import engine.models.RawModel;
import engine.thinmatrix.opengl.Vao;
import engine.thinmatrix.rendering.TerrainRenderer;
import engine.thinmatrix.utils.Light;

public class Terrain implements ITerrain {

	private final Vao vao;
	private final int vertexCount;
	private final TerrainRenderer renderer;
	private final float[][] heights;
	private final float WATER_HEIGHT = -0.2f;

	public Terrain(Vao vao, int vertexCount, TerrainRenderer renderer, float[][] heights){
		this.vao = vao;
		this.vertexCount = vertexCount;
		this.renderer = renderer;
		this.heights = heights;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
	public Vao getVao() {
		return vao;
	}

	public void render(ICamera camera, Light light, Vector4f clipPlane){
		renderer.render(this, camera, light, clipPlane);
	}
	
	public void delete(){
		vao.delete();
	}

	public float getHeightOfTerrain(float worldX, float worldZ) {
		int intX = (int) Math.floor(worldX);
		int intZ = (int) Math.floor(worldZ);
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

	@Override
	public RawModel getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TerrainTexturePack getTexturePack() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TerrainTexture getBlendMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getZ() {
		// TODO Auto-generated method stub
		return 0;
	}
}
