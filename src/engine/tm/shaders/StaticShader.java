package engine.tm.shaders;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.graph.ICamera;
import engine.tm.entities.Camera;
import engine.tm.entities.Light;
import engine.tm.settings.WorldSettings;
import engine.tm.toolbox.Maths;

public class StaticShader extends ShaderProgram {
	
	private static final int MAX_LIGHTS = WorldSettings.MAX_LIGHTS;

	private static final String VERTEX_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/vertexShader.glsl";
	private static final String FRAGMENT_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/fragmentShader.glsl";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColor[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColor;
	private int location_textureAtlasNumRows;
	private int location_textureAtlasOffset;

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
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_skyColor = super.getUniformLocation("skyColor");
		location_textureAtlasNumRows = super.getUniformLocation("textureAtlasNumRows");
		location_textureAtlasOffset = super.getUniformLocation("textureAtlasOffset");
		
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColor = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		for (int i = 0; i < MAX_LIGHTS; i++) {
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
	}

	public void loadTextureAtlasNumRows(int numberOfRows) {
		super.loadFloat(location_textureAtlasNumRows, numberOfRows);
	}

	public void loadTextureAtlasOffset(float x, float y) {
		super.load2DVector(location_textureAtlasOffset, new Vector2f(x, y));
	}

	public void loadSkyColor(float r, float g, float b) {
		super.loadVector(location_skyColor, new Vector3f(r, g, b));
	}

	public void loadFakeLightingVariable(boolean useFakeLighting) {
		super.loadBoolean(location_useFakeLighting, useFakeLighting);
	}

	public void loadShineVariables(float shineDamper, float reflectivity) {
		super.loadFloat(location_shineDamper, shineDamper);
		super.loadFloat(location_reflectivity, reflectivity);
	}

	public void loadLights(List<Light> lights) {
		for (int i = 0; i < MAX_LIGHTS; i++) {
			if (i < lights.size()) {
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(location_lightColor[i],    lights.get(i).getColor());
				super.loadVector(location_attenuation[i],    lights.get(i).getAttenuation());
			} else {
				super.loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector(location_lightColor[i],    new Vector3f(0, 0, 0));
				super.loadVector(location_attenuation[i],   new Vector3f(1, 0, 0));
			}
		}
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		// System.out.println("StaticShader transformationMatrix: " + matrix);
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		// System.out.println("StaticShader projectionMatrix: " + projection);
		super.loadMatrix(location_projectionMatrix, projection);
	}

	public void loadViewMatrix(ICamera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		// System.out.println("StaticShader viewMatrix: " + viewMatrix);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

}
