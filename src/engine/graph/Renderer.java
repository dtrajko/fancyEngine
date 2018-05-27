package engine.graph;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import config.Config;
import engine.GameItem;
import engine.IHud;
import engine.Scene;
import engine.Utils;
import engine.Window;
import engine.graph.lights.DirectionalLight;
import engine.graph.lights.PointLight;
import game.Hud;

public class Renderer {

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

    private final Transformation transformation;
	private ShaderProgram shaderProgram;

	private ShaderProgram sceneShaderProgram;
	private ShaderProgram hudShaderProgram;

	private final float specularPower;

	public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

	public void init(Window window) throws Exception {

		// Create shader
        shaderProgram = new ShaderProgram();        
        shaderProgram.createVertexShader(Utils.readFile(Config.RESOURCES_DIR + "/shaders/light_vertex.vs"));
        shaderProgram.createFragmentShader(Utils.readFile(Config.RESOURCES_DIR + "/shaders/light_fragment.fs"));
        shaderProgram.link();

        // Create uniforms for world and projection matrices
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix"); // ex worldMatrix
        shaderProgram.createUniform("texture_sampler");

        shaderProgram.createUniform("selectedNonInstanced");

        // Create uniform for material
        shaderProgram.createMaterialUniform("material");

        // Create lighting related uniforms
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightUniform("pointLight");
        shaderProgram.createDirectionalLightUniform("directionalLight");

        window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        setupBasicSceneShader();
        setupHudShader();
	}

	public void clear() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

    private void setupBasicSceneShader() throws Exception {
        // Create shader
        sceneShaderProgram = new ShaderProgram();
        sceneShaderProgram.createVertexShader(Utils.readFile(Config.RESOURCES_DIR + "/shaders/scene_vertex.vs"));
        sceneShaderProgram.createFragmentShader(Utils.readFile(Config.RESOURCES_DIR + "/shaders/scene_fragment.fs"));
        sceneShaderProgram.link();

        // Create uniforms for modelView and projection matrices and texture
        sceneShaderProgram.createUniform("projectionMatrix");
        sceneShaderProgram.createUniform("modelViewMatrix");
        sceneShaderProgram.createUniform("texture_sampler");
        // Create uniform for material
        sceneShaderProgram.createTextureUniform("material");
    }

	private void setupHudShader() throws Exception {
		hudShaderProgram = new ShaderProgram();
		hudShaderProgram.createVertexShader(Utils.readFile(Config.RESOURCES_DIR + "/shaders/hud_vertex.vs"));
		hudShaderProgram.createFragmentShader(Utils.readFile(Config.RESOURCES_DIR + "/shaders/hud_fragment.fs"));
		
		hudShaderProgram.link();

		// Create uniforms for Ortographic-model projection matrix and base colour
		hudShaderProgram.createUniform("projModelMatrix");
		hudShaderProgram.createUniform("color");
		hudShaderProgram.createUniform("hasTexture");
	}

	public void render(Window window, Camera camera, List<GameItem> gameItems, Hud hud) {
        clear();
        if ( window.isResized() ) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
        shaderProgram.bind();

        // Update projection matrix once per render cycle
        window.updateProjectionMatrix(); // do we need this?

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        shaderProgram.setUniform("texture_sampler", 0);

        // Render each gameItem
        for(GameItem gameItem : gameItems) {
        	Mesh mesh = gameItem.getMesh();

        	// Set model view matrix for this item
        	Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
        	shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
        	shaderProgram.setUniform("selectedNonInstanced", gameItem.isSelected() ? 1.0f : 0.0f);

            // Render the mesh for this game item
            // shaderProgram.setUniform("material", mesh.getMaterial());
            mesh.render();
        }

        renderHud(window, hud);
        shaderProgram.unbind();
	}

    public void render(Window window, Camera camera, List<GameItem> gameItems, Vector3f ambientLight,
        PointLight pointLight, DirectionalLight directionalLight) {
        
        clear();

        if ( window.isResized() ) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
        shaderProgram.bind();
        
        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        // Update Light Uniforms
        shaderProgram.setUniform("ambientLight", ambientLight);
        shaderProgram.setUniform("specularPower", specularPower);

        // Get a copy of the point light object and transform its position to view coordinates
        if (pointLight != null) {
            PointLight currPointLight = new PointLight(pointLight);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            shaderProgram.setUniform("pointLight", currPointLight);        	
        }

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        shaderProgram.setUniform("directionalLight", currDirLight);

        shaderProgram.setUniform("texture_sampler", 0);

        // Render each gameItem
        for(GameItem gameItem : gameItems) {
            Mesh mesh = gameItem.getMesh();

            // Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            shaderProgram.setUniform("selectedNonInstanced", gameItem.isSelected() ? 1.0f : 0.0f);

            // Render the mesh for this game item
            shaderProgram.setUniform("material", mesh.getMaterial());
            mesh.render();

        }
        shaderProgram.unbind();
    }

    public void render(Window window, Camera camera, Scene scene, IHud hud) {
        clear();
        if (window.isResized()) {
        	GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
        // Update projection and view matrices once per render cycle
        transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        transformation.updateViewMatrix(camera);
        renderScene(window, camera, scene);
        // renderSkyBox(window, camera, scene);
        renderHud(window, hud);
    }

    public void renderScene(Window window, Camera camera, Scene scene) {
        sceneShaderProgram.bind();

        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = transformation.getViewMatrix();

        // SceneLight sceneLight = scene.getSceneLight();
        // renderLights(viewMatrix, sceneLight);

        sceneShaderProgram.setUniform("texture_sampler", 0);
        // Render each mesh with the associated game Items
        Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
            // sceneShaderProgram.setUniform("material", mesh.getMaterial());
            mesh.renderList(mapMeshes.get(mesh), (GameItem gameItem) -> {
                Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(gameItem, viewMatrix);
                sceneShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            }
            );
        }

        sceneShaderProgram.unbind();
    }

    private void renderHud(Window window, IHud hud) {
        hudShaderProgram.bind();

        Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
        for (GameItem gameItem : hud.getGameItems()) {
            Mesh mesh = gameItem.getMesh();
         // Set ortohtaphic and model matrix for this HUD item
            Matrix4f projModelMatrix = transformation.buildOrtoProjModelMatrix(gameItem, ortho);
            hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
            hudShaderProgram.setUniform("color", gameItem.getMesh().getMaterial().getAmbientColor());
            hudShaderProgram.setUniform("hasTexture", gameItem.getMesh().getMaterial().isTextured() ? 1 : 0);

            // Render the mesh for this HUD item
            mesh.render();
        }

        hudShaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
        if (hudShaderProgram != null) {
            hudShaderProgram.cleanup();
        }
    }
}
