package engine.tm.shaders;

import org.joml.Matrix4f;
import engine.tm.settings.WorldSettings;

public class SimpleShader extends ShaderProgram {

	private static final String VERTEX_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/simpleVertex.glsl";
	private static final String FRAGMENT_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/simpleFragment.glsl";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;

	public SimpleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		// System.out.println("StaticShader transformationMatrix: " + matrix);
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		// System.out.println("StaticShader projectionMatrix: " + projection);
		super.loadMatrix(location_projectionMatrix, projection);
	}

	public void loadViewMatrix(Matrix4f viewMatrix) {
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
}
