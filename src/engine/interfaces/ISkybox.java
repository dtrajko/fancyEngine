package engine.interfaces;

import engine.tm.models.RawModel;

public interface ISkybox {

	RawModel getCube();
	int getTexture();
	int getTextureNight();

}
