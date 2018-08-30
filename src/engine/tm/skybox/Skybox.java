package engine.tm.skybox;

import engine.tm.loaders.Loader;
import engine.tm.models.RawModel;

public class Skybox {

	public static final float SIZE = 1200f;

	/**
	 * GL_TEXTURE_CUBE_MAP_POSITIVE_X = Right Face
	 * GL_TEXTURE_CUBE_MAP_NEGATIVE_X = Left Face
	 * GL_TEXTURE_CUBE_MAP_POSITIVE_Y = Top Face
	 * GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = Bottom Face
	 * GL_TEXTURE_CUBE_MAP_POSITIVE_Z = Back Face
	 * GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = Front Face
	 */
	private static String[] TEXTURE_FILES = {
		"skybox_3/right", 
		"skybox_3/left", 
		"skybox_3/top", 
		"skybox_3/bottom", 
		"skybox_3/back", 
		"skybox_3/front"
	};

	public static final float[] VERTICES = {
		-SIZE,  SIZE, -SIZE,
		-SIZE, -SIZE, -SIZE,
		 SIZE, -SIZE, -SIZE,
		 SIZE, -SIZE, -SIZE,
		 SIZE,  SIZE, -SIZE,
		-SIZE,  SIZE, -SIZE,
		
		-SIZE, -SIZE,  SIZE,
		-SIZE, -SIZE, -SIZE,
		-SIZE,  SIZE, -SIZE,
		-SIZE,  SIZE, -SIZE,
		-SIZE,  SIZE,  SIZE,
		-SIZE, -SIZE,  SIZE,
		
		 SIZE, -SIZE, -SIZE,
		 SIZE, -SIZE,  SIZE,
		 SIZE,  SIZE,  SIZE,
		 SIZE,  SIZE,  SIZE,
		 SIZE,  SIZE, -SIZE,
		 SIZE, -SIZE, -SIZE,
		
		-SIZE, -SIZE,  SIZE,
		-SIZE,  SIZE,  SIZE,
		 SIZE,  SIZE,  SIZE,
		 SIZE,  SIZE,  SIZE,
		 SIZE, -SIZE,  SIZE,
		-SIZE, -SIZE,  SIZE,
		
		-SIZE,  SIZE, -SIZE,
		 SIZE,  SIZE, -SIZE,
		 SIZE,  SIZE,  SIZE,
		 SIZE,  SIZE,  SIZE,
		-SIZE,  SIZE,  SIZE,
		-SIZE,  SIZE, -SIZE,
		
		-SIZE, -SIZE, -SIZE,
		-SIZE, -SIZE,  SIZE,
		 SIZE, -SIZE, -SIZE,
		 SIZE, -SIZE, -SIZE,
		-SIZE, -SIZE,  SIZE,
		 SIZE, -SIZE,  SIZE
	};

	private RawModel cube;
	private int texture;

	public Skybox(Loader loader) {
		cube = loader.loadToVAO(VERTICES, 3);
		texture = loader.loadCubeMap(TEXTURE_FILES);
	}

	public RawModel getCube() {
		return cube;
	}

	public int getTexture() {
		return texture;
	}
}
