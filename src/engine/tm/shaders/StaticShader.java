package engine.tm.shaders;

import org.joml.Matrix4f;

import engine.tm.entities.Camera;
import engine.tm.entities.Light;
import engine.tm.settings.WorldSettings;
import engine.tm.toolbox.Maths;

public class StaticShader extends ShaderProgram {

	private static final String VERTEX_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/vertexShader.glsl";
	private static final String FRAGMENT_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/fragmentShader.glsl";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	
	private int location_lightPosition;
	private int location_lightColor;

	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_lightPosition = super.getUniformLocation("lightPosition");
		location_lightColor = super.getUniformLocation("lightColor");
	}
	
	public void loadLight(Light light) {
		super.loadVector(location_lightPosition, light.getPosition());
		super.loadVector(location_lightColor, light.getColor());
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		// System.out.println("StaticShader transformationMatrix: " + matrix);
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		// System.out.println("StaticShader projectionMatrix: " + projection);
		super.loadMatrix(location_projectionMatrix, projection);
	}

	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		// System.out.println("StaticShader viewMatrix: " + viewMatrix);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

}
