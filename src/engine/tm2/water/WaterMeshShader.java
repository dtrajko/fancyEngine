package engine.tm2.water;

import config.Config;
import engine.tm2.settings.WorldSettings;
import engine.tm2.shaders.ShaderProgram;
import engine.tm2.shaders.UniformFloat;
import engine.tm2.shaders.UniformMatrix;
import engine.tm2.shaders.UniformSampler;
import engine.tm2.shaders.UniformVec2;
import engine.tm2.shaders.UniformVec3;

/**
 * Represents the water's shader program. Handles all the uniform variables.
 * 
 * @author Karl
 *
 */
public class WaterMeshShader extends ShaderProgram {

	protected static final int REFLECT_TEX_UNIT = 0;
	protected static final int REFRACT_TEX_UNIT = 1;
	protected static final int DEPTH_TEX_UNIT = 2;

	private static final String VERTEX_SHADER = WorldSettings.RESOURCES_SUBDIR + "/shaders/waterVertex.glsl";
	private static final String FRAGMENT_SHADER = WorldSettings.RESOURCES_SUBDIR + "/shaders/waterFragment.glsl";


	protected UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");
	protected UniformFloat height = new UniformFloat("height");
	protected UniformVec3 cameraPos = new UniformVec3("cameraPos");
	protected UniformVec2 nearFarPlanes = new UniformVec2("nearFarPlanes");
	protected UniformFloat waveTime = new UniformFloat("waveTime");
	protected UniformVec3 lightDirection = new UniformVec3("lightDirection");
	protected UniformVec3 lightColour = new UniformVec3("lightColour");
	protected UniformVec2 lightBias = new UniformVec2("lightBias");

	protected UniformSampler reflectionTexture = new UniformSampler("reflectionTexture");
	protected UniformSampler refractionTexture = new UniformSampler("refractionTexture");
	protected UniformSampler depthTexture = new UniformSampler("depthTexture");

	public WaterMeshShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
		super.storeAllUniformLocations(projectionViewMatrix, height, reflectionTexture, refractionTexture, depthTexture,
				cameraPos, nearFarPlanes, waveTime, lightDirection, lightColour, lightBias);
		linkTextureUnits();
	}

	/**
	 * Links the texture samplers in the fragment shader to the texture units
	 * that they're going to be sampling from.
	 */
	private void linkTextureUnits() {
		super.start();
		reflectionTexture.loadTexUnit(REFLECT_TEX_UNIT);
		refractionTexture.loadTexUnit(REFRACT_TEX_UNIT);
		depthTexture.loadTexUnit(DEPTH_TEX_UNIT);
		super.stop();
	}

}
