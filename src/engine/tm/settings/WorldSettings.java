package engine.tm.settings;

import org.joml.Vector3f;

import config.Config;

public class WorldSettings {
	
	public static final int MAX_LIGHTS = 5;
	public static final int MAX_WEIGHTS = 3; // used in AnimatedModelLoader
	public static final String RESOURCES_SUBDIR = Config.RESOURCES_DIR + "/ThinMatrix";
	public static final String TEXTURES_DIR = RESOURCES_SUBDIR + "/textures";
	public static final String MODELS_DIR = RESOURCES_SUBDIR + "/models";
	public static final String FONTS_DIR = RESOURCES_SUBDIR + "/fonts";
	public static final String LENS_FLARE_DIR = RESOURCES_SUBDIR + "/textures/lensFlare";
	public static final Vector3f LIGHT_DIR = new Vector3f(0.55f, -0.15f, 0.54f);
	public static final float WATER_HEIGHT = -0.2f;

}
