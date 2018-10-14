package engine.interfaces;

import engine.tm.models.RawModel;
import engine.tm.textures.TerrainTexture;
import engine.tm.textures.TerrainTexturePack;

public interface ITerrain {

	float getX();
	float getZ();
	RawModel getModel();
	TerrainTexturePack getTexturePack();
	TerrainTexture getBlendMap();
	float getHeightOfTerrain(float coordX, float coordZ);
	float getWaterHeight();
}
