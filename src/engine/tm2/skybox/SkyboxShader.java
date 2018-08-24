package engine.tm2.skybox;

import engine.tm2.settings.WorldSettings;
import engine.tm2.shaders.ShaderProgram;
import engine.tm2.shaders.UniformMatrix;

public class SkyboxShader extends ShaderProgram {

	private static final String VERTEX_SHADER = WorldSettings.RESOURCES_SUBDIR + "/shaders/skyboxVertex.glsl";
	private static final String FRAGMENT_SHADER = WorldSettings.RESOURCES_SUBDIR + "/shaders/skyboxFragment.glsl";

	protected UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");

	public SkyboxShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position");
		super.storeAllUniformLocations(projectionViewMatrix);
	}
}
