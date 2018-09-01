package engine.tm.skybox;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.GameEngine;
import engine.tm.entities.Camera;
import engine.tm.settings.WorldSettings;
import engine.tm.shaders.ShaderProgram;
import engine.tm.toolbox.Maths;

public class SkyboxShader extends ShaderProgram {

	private static final String VERTEX_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/skyboxVertexShader.glsl";
	private static final String FRAGMENT_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/skyboxFragmentShader.glsl";

	private static final float ROTATE_SPEED = 1f;

	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_fogColor;
	private int location_cubeMap;
	private int location_cubeMapNight;
	private int location_blendFactor;
	private int location_clipPlane;

	private float rotation = 0;

	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_fogColor = super.getUniformLocation("fogColor");
		location_cubeMap = super.getUniformLocation("cubeMap");
		location_cubeMapNight = super.getUniformLocation("cubeMapNight");
		location_blendFactor = super.getUniformLocation("blendFactor");
		location_clipPlane = super.getUniformLocation("clipPlane");
	}

	public void loadClipPlane(Vector4f clipPlane) {
		super.load4DVector(location_clipPlane, clipPlane);
		
	}

	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera) {
		Matrix4f matrix = Maths.createViewMatrix(camera);
		matrix.m30(0);
		matrix.m31(0);
		matrix.m32(0);
		// consider moving it to the transformation matrix
		rotation += ROTATE_SPEED * (1f / GameEngine.TARGET_FPS);
		matrix.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0));		
		super.loadMatrix(location_viewMatrix, matrix);
	}

	/**
	 * Loading multiple cubes, for day/night transition
	 */
	public void connectTextureUnits() {
		super.loadInt(location_cubeMap, 0);
		super.loadInt(location_cubeMapNight, 1);
	}

	/**
	 * Loading multiple cubes, for day/night transition
	 */
	public void loadBlendFactor(float blendFactor) {
		super.loadFloat(location_blendFactor, blendFactor);
	}

	public void loadFogColor(float r, float g, float b) {
		super.loadVector(location_fogColor, new Vector3f(r, g, b));
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
}
