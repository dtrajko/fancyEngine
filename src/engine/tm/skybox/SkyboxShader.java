package engine.tm.skybox;

import config.Config;
import engine.tm.shaders.ShaderProgram;
import engine.tm.shaders.UniformMatrix;

public class SkyboxShader extends ShaderProgram {
	
	private static final String VERTEX_SHADER = Config.RESOURCES_DIR + "/ThinMatrix/shaders/skyboxVertex.glsl";
	private static final String FRAGMENT_SHADER = Config.RESOURCES_DIR + "/ThinMatrix/shaders/skyboxFragment.glsl";

	protected UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");

	public SkyboxShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position");
		super.storeAllUniformLocations(projectionViewMatrix);
	}
}
