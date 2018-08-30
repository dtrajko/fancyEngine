package engine.tm.skybox;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import engine.tm.entities.Camera;
import engine.tm.settings.WorldSettings;
import engine.tm.shaders.ShaderProgram;
import engine.tm.toolbox.Maths;

public class SkyboxShader extends ShaderProgram {

	private static final String VERTEX_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/skyboxVertexShader.glsl";
	private static final String FRAGMENT_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/skyboxFragmentShader.glsl";
	
	private int location_projectionMatrix;
	private int location_viewMatrix;

	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera) {
		Matrix4f matrix = Maths.createViewMatrix(camera);
		matrix.m30(0);
		matrix.m31(0);
		matrix.m31(0);
		super.loadMatrix(location_viewMatrix, matrix);
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
}
