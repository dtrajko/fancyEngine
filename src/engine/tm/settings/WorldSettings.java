package engine.tm.settings;

import config.Config;

public class WorldSettings {
	
	public static final int MAX_LIGHTS = 5;
	public static final int MAX_WEIGHTS = 3; // used in AnimatedModelLoader
	public static final String RESOURCES_SUBDIR = Config.RESOURCES_DIR + "/ThinMatrix";
	public static final String TEXTURES_DIR = RESOURCES_SUBDIR + "/textures";
	public static final String MODELS_DIR = RESOURCES_SUBDIR + "/models";
	public static final String FONTS_DIR = RESOURCES_SUBDIR + "/fonts";

}
