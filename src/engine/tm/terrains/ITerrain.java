package engine.tm.terrains;

import engine.tm.models.RawModel;
import engine.tm.textures.TerrainTexture;
import engine.tm.textures.TerrainTexturePack;

public interface ITerrain {

	float getX();
	float getZ();
	float getHeightOfTerrain(float coordX, float coordZ);
	RawModel getModel();
	TerrainTexturePack getTexturePack();
	TerrainTexture getBlendMap();

}
