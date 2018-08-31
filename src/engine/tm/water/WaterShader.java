package engine.tm.water;

import org.joml.Matrix4f;

import engine.tm.entities.Camera;
import engine.tm.entities.Light;
import engine.tm.settings.WorldSettings;
import engine.tm.shaders.ShaderProgram;
import engine.tm.toolbox.Maths;

public class WaterShader extends ShaderProgram {

    private final static String VERTEX_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/waterVertex.glsl";
    private final static String FRAGMENT_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/waterFragment.glsl";

    private int location_modelMatrix;
    private int location_viewMatrix;
    private int location_projectionMatrix;
    private int location_reflectionTexture;
    private int location_refractionTexture;
    private int location_dudvMap;
    private int location_moveFactor;
    private int location_cameraPosition;
    private int location_normalMap;
    private int location_lightColor;
    private int location_lightPosition;
    private int location_depthMap;

    public WaterShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = getUniformLocation("projectionMatrix");
        location_viewMatrix = getUniformLocation("viewMatrix");
        location_modelMatrix = getUniformLocation("modelMatrix");
        location_reflectionTexture = getUniformLocation("reflectionTexture");
        location_refractionTexture = getUniformLocation("refractionTexture");
        location_dudvMap = getUniformLocation("dudvMap");
        location_moveFactor = getUniformLocation("moveFactor");
        location_cameraPosition = getUniformLocation("cameraPosition");
        location_normalMap = getUniformLocation("normalMap");
        location_lightColor = getUniformLocation("lightColor");
        location_lightPosition = getUniformLocation("lightPosition");
        location_depthMap = getUniformLocation("depthMap");
    }

    public void connectTextureUnits() {
    	super.loadInt(location_reflectionTexture, 0);
    	super.loadInt(location_refractionTexture, 1);
    	super.loadInt(location_dudvMap, 2);
    	super.loadInt(location_normalMap, 3);
    	super.loadInt(location_depthMap, 4);
    }

    public void loadLight(Light sun) {
    	super.loadVector(location_lightColor, sun.getColor());
    	super.loadVector(location_lightPosition, sun.getPosition());
    }

    public void loadMoveFactor(float factor) {
    	super.loadFloat(location_moveFactor, factor);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        loadMatrix(location_projectionMatrix, projection);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        loadMatrix(location_viewMatrix, viewMatrix);
        super.loadVector(location_cameraPosition, camera.getPosition());
    }

    public void loadModelMatrix(Matrix4f modelMatrix){
        loadMatrix(location_modelMatrix, modelMatrix);
    }
}
