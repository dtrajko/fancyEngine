package engine.tm.water;

import engine.tm.loaders.Loader;
import engine.tm.models.RawModel;

public class Water {

	private static final String DUDV_MAP = "/water/waterDUDV";
	private static final String NORMAL_MAP = "/water/normal";

    private RawModel quad;
    private int dudvTexture;
    private int normalMap;

    public Water(Loader loader) {
    	this.dudvTexture = loader.loadTexture(DUDV_MAP);
        this.normalMap = loader.loadTexture(NORMAL_MAP);
        setUpVAO(loader);
    }

    private void setUpVAO(Loader loader) {
        // Just x and z vertex positions here, y is set to 0 in v.shader
        float[] vertices = { -1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1 };
        quad = loader.loadToVAO(vertices, 2);
    }

    public RawModel getQuad() {
    	return quad;
    }

    public int getDuDvTexture() {
    	return dudvTexture;
    }

    public int getNormalMap() {
    	return normalMap;
    }
}
