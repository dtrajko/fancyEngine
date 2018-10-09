package engine.tm.lowPoly;

import engine.tm.settings.WorldSettings;
import engine.tm.shaders.ShaderProgram;
import engine.tm.shaders.UniformFloat;
import engine.tm.shaders.UniformMatrix;
import engine.tm.shaders.UniformSampler;
import engine.tm.shaders.UniformVec2;
import engine.tm.shaders.UniformVec3;

/**
 * Represents the water's shader program. Handles all the uniform variables.
 * 
 * @author Karl
 *
 */
public class WaterShader extends ShaderProgram {

	protected static final int REFLECT_TEX_UNIT = 0;
	protected static final int REFRACT_TEX_UNIT = 1;
	protected static final int DEPTH_TEX_UNIT = 2;
	
    private final static String VERTEX_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/lowPolyWaterVertex.glsl";
    private final static String FRAGMENT_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/lowPolyWaterFragment.glsl";

    protected UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
    protected UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	protected UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	protected UniformFloat height = new UniformFloat("height");
	protected UniformVec3 cameraPos = new UniformVec3("cameraPos");
	protected UniformVec2 nearFarPlanes = new UniformVec2("nearFarPlanes");
	protected UniformFloat waveTime = new UniformFloat("waveTime");
	protected UniformFloat waveLength = new UniformFloat("waveLength");
	protected UniformFloat waveAmplitude = new UniformFloat("waveAmplitude");
	protected UniformVec3 lightDirection = new UniformVec3("lightDirection");
	protected UniformVec3 lightColor = new UniformVec3("lightColor");
	protected UniformVec2 lightBias = new UniformVec2("lightBias");

	protected UniformSampler reflectionTexture = new UniformSampler("reflectionTexture");
	protected UniformSampler refractionTexture = new UniformSampler("refractionTexture");
	protected UniformSampler depthTexture = new UniformSampler("depthTexture");

	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		super.storeAllUniformLocations(projectionMatrix, viewMatrix, transformationMatrix,
			height, reflectionTexture, refractionTexture, depthTexture, cameraPos, nearFarPlanes,
			waveTime, waveAmplitude, waveLength,
			lightDirection, lightColor, lightBias);
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
