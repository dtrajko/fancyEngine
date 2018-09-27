package engine.tm.lowPoly;

import org.joml.Vector4f;
import engine.graph.ICamera;
import engine.tm.entities.LightDirectional;
import engine.tm.openglObjects.Vao;
import engine.tm.settings.WorldSettings;

public class TerrainLowPoly {

	private final Vao vao;
	private final int vertexCount;
	private final TerrainRendererLowPoly renderer;
	private final float[][] heights;
	private final float WATER_HEIGHT = WorldSettings.WATER_HEIGHT;

	public TerrainLowPoly(Vao vao, int vertexCount, TerrainRendererLowPoly renderer, float[][] heights){
		this.vao = vao;
		this.vertexCount = vertexCount;
		this.renderer = renderer;
		this.heights = heights;
	}
	
	public int getVertexCount(){
		return vertexCount;
	}
	
	public Vao getVao(){
		return vao;
	}
	
	public void render(ICamera camera, LightDirectional light, Vector4f clipPlane){
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
}