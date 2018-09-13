package engine.tm.water;

import engine.tm.loaders.Loader;
import engine.tm.models.RawModel;
import engine.tm.settings.WorldSettings;

public class Water {

	public static int HEIGHT = -6;

	private static final String DUDV_MAP = WorldSettings.TEXTURES_DIR + "/water/waterDUDV.png";
	private static final String NORMAL_MAP = WorldSettings.TEXTURES_DIR + "/water/normal.png";

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
