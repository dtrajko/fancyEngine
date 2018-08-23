package engine.tm2.terrains;

import org.joml.Vector4f;

import engine.graph.ICamera;
import engine.tm.models.RawModel;
import engine.tm2.opengl.Vao;
import engine.tm2.utils.Light;

public interface ITerrain {

	void render(ICamera camera, Light light, Vector4f clipPlane);
	Vao getVao();
	RawModel getModel();
	int getVertexCount();
	float getHeightOfTerrain(float worldX, float worldZ);
	float getX();
	float getZ();
	TerrainTexturePack getTexturePack();
	TerrainTexture getBlendMap();
}
