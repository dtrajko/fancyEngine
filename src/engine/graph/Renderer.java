package engine.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import config.Config;
import engine.IHud;
import engine.Scene;
import engine.SceneLight;
import engine.Utils;
import engine.Window;
import engine.graph.anim.AnimGameItem;
import engine.graph.anim.AnimatedFrame;
import engine.graph.lights.DirectionalLight;
import engine.graph.lights.PointLight;
import engine.graph.lights.SpotLight;
import engine.graph.particles.IParticleEmitter;
import engine.graph.shadow.ShadowCascade;
import engine.graph.shadow.ShadowRenderer;
import engine.gui.GuiManager;
import engine.gui.GuiRenderer;
import engine.gui.fonts.FontRenderer;
import engine.items.GameItem;
import engine.items.SkyBox;
import game.Hud;

public class Renderer {

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;

    private final Transformation transformation;
    private final ShadowRenderer shadowRenderer;
	private ShaderProgram shaderProgram;
    private ShaderProgram depthShaderProgram;
    private ShaderProgram sceneShaderProgram;
    private ShaderProgram hudShaderProgram;
    private ShaderProgram skyBoxShaderProgram;
    private ShaderProgram particlesShaderProgram;
    private ShaderProgram fontShaderProgram;
    
    private GuiRenderer guiRenderer;
    private FontRenderer textRenderer;

	private final float specularPower;
	private ShadowMap shadowMap;
	private final FrustumCullingFilter frustumFilter;
	private final List<GameItem> filteredItems;

	public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
        shadowRenderer = new ShadowRenderer();
        frustumFilter = new FrustumCullingFilter();
        filteredItems = new ArrayList<>();
        
    }

    public void init(Window window, Scene scene) throws Exception {
        if (scene.isRenderShadows()) {
        	shadowRenderer.init(window);
        }
        setupSkyBoxShader();
        setupSceneShader();
        setupParticlesShader();
        setupHudShader();
        setupFontShader();
        guiRenderer = new GuiRenderer();
        guiRenderer.setupGuiShader();
        textRenderer = new FontRenderer();
        textRenderer.setupShader();
    }

    private void setupParticlesShader() throws Exception {
        particlesShaderProgram = new ShaderProgram();
        particlesShaderProgram.createVertexShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/particles_vertex.vs"));
        particlesShaderProgram.createFragmentShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/particles_fragment.fs"));
        particlesShaderProgram.link();

        particlesShaderProgram.createUniform("projectionMatrix");
        particlesShaderProgram.createUniform("modelViewMatrix");
        particlesShaderProgram.createUniform("texture_sampler");
        particlesShaderProgram.createUniform("transparency");

        particlesShaderProgram.createUniform("texXOffset");
        particlesShaderProgram.createUniform("texYOffset");
        particlesShaderProgram.createUniform("numCols");
        particlesShaderProgram.createUniform("numRows");
    }

    private void setupDepthShader() throws Exception {
        depthShaderProgram = new ShaderProgram();
        depthShaderProgram.createVertexShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/depth_vertex.vs"));
        depthShaderProgram.createFragmentShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/depth_fragment.fs"));
        depthShaderProgram.link();

        depthShaderProgram.createUniform("isInstanced");
        depthShaderProgram.createUniform("jointsMatrix");
        depthShaderProgram.createUniform("modelLightViewNonInstancedMatrix");
        depthShaderProgram.createUniform("orthoProjectionMatrix");
    }

    private void setupSkyBoxShader() throws Exception {
        skyBoxShaderProgram = new ShaderProgram();
        skyBoxShaderProgram.createVertexShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/sb_vertex.vs"));
        skyBoxShaderProgram.createFragmentShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/sb_fragment.fs"));
        skyBoxShaderProgram.link();

        // Create uniforms for projection matrix
        skyBoxShaderProgram.createUniform("projectionMatrix");
        skyBoxShaderProgram.createUniform("modelViewMatrix");
        skyBoxShaderProgram.createUniform("texture_sampler");
        skyBoxShaderProgram.createUniform("ambientLight");
        skyBoxShaderProgram.createUniform("colour");
        skyBoxShaderProgram.createUniform("hasTexture");
    }

    private void setupSceneShader() throws Exception {
        // Create shader
        sceneShaderProgram = new ShaderProgram();
        sceneShaderProgram.createVertexShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/scene_vertex.vs"));
        sceneShaderProgram.createFragmentShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/scene_fragment.fs"));
        sceneShaderProgram.link();

        // Create uniforms for view and projection matrices
        sceneShaderProgram.createUniform("viewMatrix");
        sceneShaderProgram.createUniform("projectionMatrix");
        sceneShaderProgram.createUniform("texture_sampler");
        sceneShaderProgram.createUniform("normalMap");

        // Create uniform for material
        sceneShaderProgram.createMaterialUniform("material");

        // Create lighting related uniforms
        sceneShaderProgram.createUniform("specularPower");
        sceneShaderProgram.createUniform("ambientLight");
        sceneShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        sceneShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        sceneShaderProgram.createDirectionalLightUniform("directionalLight");
        sceneShaderProgram.createFogUniform("fog");

        // Create uniforms for shadow mapping
        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
            sceneShaderProgram.createUniform("shadowMap_" + i);
        }
        sceneShaderProgram.createUniform("orthoProjectionMatrix", ShadowRenderer.NUM_CASCADES);
        sceneShaderProgram.createUniform("modelNonInstancedMatrix");
        sceneShaderProgram.createUniform("lightViewMatrix", ShadowRenderer.NUM_CASCADES);
        sceneShaderProgram.createUniform("cascadeFarPlanes", ShadowRenderer.NUM_CASCADES);
        sceneShaderProgram.createUniform("renderShadow");

        // Create uniform for joint matrices
        sceneShaderProgram.createUniform("jointsMatrix");

        sceneShaderProgram.createUniform("isInstanced");
        sceneShaderProgram.createUniform("numCols");
        sceneShaderProgram.createUniform("numRows");

        sceneShaderProgram.createUniform("selectedNonInstanced");
        
        sceneShaderProgram.createUniform("transparency");
    }

    private void setupHudShader() throws Exception {
        hudShaderProgram = new ShaderProgram();
        hudShaderProgram.createVertexShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/hud_vertex.vs"));
        hudShaderProgram.createFragmentShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/hud_fragment.fs"));
        hudShaderProgram.link();

        // Create uniforms for orthographic-model projection matrix and base colour
        hudShaderProgram.createUniform("projModelMatrix");
        hudShaderProgram.createUniform("colour");
        hudShaderProgram.createUniform("hasTexture");
    }

	public void clear() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
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
        // sceneShaderProgram.createTextureUniform("material");
    }

    private void setupFontShader() throws Exception {
    	fontShaderProgram = new ShaderProgram();
    	fontShaderProgram.createVertexShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/font_vertex.vs"));
    	fontShaderProgram.createFragmentShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/font_fragment.fs"));
    	fontShaderProgram.link();

    	fontShaderProgram.createUniform("color");
    	fontShaderProgram.createUniform("translation");
    }

	public void render(Window window, Camera camera, List<GameItem> gameItems, Hud hud) {
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

    public void render(Window window, Camera camera, Scene scene, boolean sceneChanged) {
        clear();

        if (window.getOptions().frustumCulling) {
            frustumFilter.updateFrustum(window.getProjectionMatrix(), camera.getViewMatrix());
            frustumFilter.filter(scene.getGameMeshes());
            frustumFilter.filter(scene.getGameInstancedMeshes());
        }

        // Render depth map before view ports has been set up
        if (scene.isRenderShadows() && sceneChanged) {
            shadowRenderer.render(window, scene, camera, transformation, this);
        }

        GL11.glViewport(0, 0, window.getWidth(), window.getHeight());

        // Update projection matrix once per render cycle
        window.updateProjectionMatrix();

        // Update projection and view matrices once per render cycle
        transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        transformation.updateViewMatrix(camera);

        renderSkyBox(window, camera, scene);
        renderScene(window, camera, scene);
        renderParticles(window, camera, scene);
    }

    private void renderParticles(Window window, Camera camera, Scene scene) {
        particlesShaderProgram.bind();

        particlesShaderProgram.setUniform("texture_sampler", 0);
        Matrix4f projectionMatrix = transformation.getProjectionMatrix();
        particlesShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = transformation.getViewMatrix();
        IParticleEmitter[] emitters = scene.getParticleEmitters();
        int numEmitters = emitters != null ? emitters.length : 0;

        // GL11.glDepthMask(false);
        // GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        for (int i = 0; i < numEmitters; i++) {
            IParticleEmitter emitter = emitters[i];
            Mesh mesh = emitter.getBaseParticle().getMesh();

            Texture texture = mesh.getMaterial().getTexture();
            float transparency = mesh.getMaterial().getTransparency();
            particlesShaderProgram.setUniform("numCols", texture.getNumCols());
            particlesShaderProgram.setUniform("numRows", texture.getNumRows());

            particlesShaderProgram.setUniform("transparency", transparency);

            mesh.renderList((emitter.getParticles()), (GameItem gameItem) -> {
                int col = gameItem.getTextPos() % texture.getNumCols();
                int row = gameItem.getTextPos() / texture.getNumCols();
                float textXOffset = (float) col / texture.getNumCols();
                float textYOffset = (float) row / texture.getNumRows();
                particlesShaderProgram.setUniform("texXOffset", textXOffset);
                particlesShaderProgram.setUniform("texYOffset", textYOffset);

                Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);

                viewMatrix.transpose3x3(modelMatrix);
                // viewMatrix.scale(gameItem.getScale());

                Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
                modelViewMatrix.scale(gameItem.getScale());
                particlesShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            });
        }

        // GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        // GL11.glDepthMask(true);

        particlesShaderProgram.unbind();
    }

	private void renderDepthMap(Window window, Camera camera, Scene scene) {
        if (scene.isRenderShadows()) {
            // Setup view port to match the texture size
        	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        	GL11.glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
        	GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

            depthShaderProgram.bind();

            DirectionalLight light = scene.getSceneLight().getDirectionalLight();
            Vector3f lightDirection = light.getDirection();

            float lightAngleX = (float) Math.toDegrees(Math.acos(lightDirection.z));
            float lightAngleY = (float) Math.toDegrees(Math.asin(lightDirection.x));
            float lightAngleZ = 0;
            Matrix4f lightViewMatrix = transformation.updateLightViewMatrix(new Vector3f(lightDirection).mul(light.getShadowPosMult()), new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
            DirectionalLight.OrthoCoords orthCoords = light.getOrthoCoords();
            Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthCoords.left, orthCoords.right, orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);

            depthShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);

            renderNonInstancedMeshes(scene);
            renderInstancedMeshes(scene, lightViewMatrix);

            // Unbind
            depthShaderProgram.unbind();
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        }
    }

    private void renderSkyBox(Window window, Camera camera, Scene scene) {
    	
        SkyBox skyBox = scene.getSkyBox();
        if (skyBox != null) {
            skyBoxShaderProgram.bind();

            skyBoxShaderProgram.setUniform("texture_sampler", 0);

            Matrix4f projectionMatrix = transformation.getProjectionMatrix();
            skyBoxShaderProgram.setUniform("projectionMatrix", projectionMatrix);
            Matrix4f viewMatrix = transformation.getViewMatrix();
            float m30 = viewMatrix.m30();
            viewMatrix.m30(0);
            float m31 = viewMatrix.m31();
            viewMatrix.m31(0);
            float m32 = viewMatrix.m32();
            viewMatrix.m32(0);

            Mesh mesh = skyBox.getMesh();
            Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(skyBox, viewMatrix);
            skyBoxShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            skyBoxShaderProgram.setUniform("ambientLight", scene.getSceneLight().getSkyBoxLight());
            skyBoxShaderProgram.setUniform("colour", mesh.getMaterial().getAmbientColour());
            skyBoxShaderProgram.setUniform("hasTexture", mesh.getMaterial().isTextured() ? 1 : 0);

            mesh.render();

            viewMatrix.m30(m30);
            viewMatrix.m31(m31);
            viewMatrix.m32(m32);
            skyBoxShaderProgram.unbind();
        }
    }

    private void renderLights(Matrix4f viewMatrix, SceneLight sceneLight) {

        sceneShaderProgram.setUniform("ambientLight", sceneLight.getAmbientLight());
        sceneShaderProgram.setUniform("specularPower", specularPower);

        // Process Point Lights
        PointLight[] pointLightList = sceneLight.getPointLightList();
        int numLights = pointLightList != null ? pointLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the point light object and transform its position to view coordinates
            PointLight currPointLight = new PointLight(pointLightList[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            sceneShaderProgram.setUniform("pointLights", currPointLight, i);
        }

        // Process Spot Ligths
        SpotLight[] spotLightList = sceneLight.getSpotLightList();
        numLights = spotLightList != null ? spotLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the spot light object and transform its position and cone direction to view coordinates
            SpotLight currSpotLight = new SpotLight(spotLightList[i]);
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));

            Vector3f lightPos = currSpotLight.getPointLight().getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;

            sceneShaderProgram.setUniform("spotLights", currSpotLight, i);
        }

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        // System.out.println("Renderer directionalLight: " + currDirLight);
        sceneShaderProgram.setUniform("directionalLight", currDirLight);
    }

    private void renderHud(Window window, IHud hud) {
        if (hud != null) {
            hudShaderProgram.bind();
            Matrix4f ortho = transformation.getOrtho2DProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
            for (GameItem gameItem : hud.getGameItems()) {
                Mesh mesh = gameItem.getMesh();
                // Set ortohtaphic and model matrix for this HUD item
                Matrix4f projModelMatrix = transformation.buildOrthoProjModelMatrix(gameItem, ortho);
                hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
                hudShaderProgram.setUniform("colour", gameItem.getMesh().getMaterial().getAmbientColour());
                hudShaderProgram.setUniform("hasTexture", gameItem.getMesh().getMaterial().isTextured() ? 1 : 0);                

                // Render the mesh for this HUD item
                mesh.render();
            }
            hudShaderProgram.unbind();
        }
    }
    
    public void renderGui(GuiManager guiManager, Window window) {
    	this.guiRenderer.render(guiManager, window);
    }

	public void renderGuiText(GuiManager guiManager) {
		if (guiManager.getImportPopup().isEnabled()) {
			guiManager.getImportPopup().render();
		}
		if(guiManager.getQuitPopup().isEnabled()) {
			guiManager.getQuitPopup().render();
		}
	}

    public void renderScene(Window window, Camera camera, Scene scene) {
        sceneShaderProgram.bind();

        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = window.getProjectionMatrix();
        sceneShaderProgram.setUniform("viewMatrix", viewMatrix);
        sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        if (scene.isRenderShadows()) {
	        List<ShadowCascade> shadowCascades = shadowRenderer.getShadowCascades();
	        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
	            ShadowCascade shadowCascade = shadowCascades.get(i);
	            sceneShaderProgram.setUniform("orthoProjectionMatrix", shadowCascade.getOrthoProjMatrix(), i);
	            sceneShaderProgram.setUniform("cascadeFarPlanes", ShadowRenderer.CASCADE_SPLITS[i], i);
	            sceneShaderProgram.setUniform("lightViewMatrix", shadowCascade.getLightViewMatrix(), i);
	        }
        }

        SceneLight sceneLight = scene.getSceneLight();
        renderLights(viewMatrix, sceneLight);

        sceneShaderProgram.setUniform("fog", scene.getFog());
        sceneShaderProgram.setUniform("texture_sampler", 0);
        sceneShaderProgram.setUniform("normalMap", 1);

        if (scene.isRenderShadows()) {
	        int start = 2;
	        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
	            sceneShaderProgram.setUniform("shadowMap_" + i, start + i);
	        }
	        sceneShaderProgram.setUniform("renderShadow", scene.isRenderShadows() ? 1 : 0);
        }

        renderNonInstancedMeshes(scene);
        renderInstancedMeshes(scene, viewMatrix);

        sceneShaderProgram.unbind();
    }

    private void renderNonInstancedMeshes(Scene scene) {
        sceneShaderProgram.setUniform("isInstanced", 0);

        // Render each mesh with the associated game Items
        Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();

        for (Mesh mesh : mapMeshes.keySet()) {
        	sceneShaderProgram.setUniform("material", mesh.getMaterial());
        	sceneShaderProgram.setUniform("transparency", mesh.getMaterial().getTransparency());
        	
            Texture text = mesh.getMaterial().getTexture();
            if (text != null) {
                sceneShaderProgram.setUniform("numCols", text.getNumCols());
                sceneShaderProgram.setUniform("numRows", text.getNumRows());
            }
            
            if (scene.isRenderShadows()) {
            	shadowRenderer.bindTextures(GL13.GL_TEXTURE2);            	
            }

            mesh.renderList(mapMeshes.get(mesh), (GameItem gameItem) -> {
            	sceneShaderProgram.setUniform("selectedNonInstanced", gameItem.isSelected() ? 1.0f : 0.0f);
                Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
                sceneShaderProgram.setUniform("modelNonInstancedMatrix", modelMatrix);
                if (gameItem instanceof AnimGameItem) {
                    AnimGameItem animGameItem = (AnimGameItem) gameItem;
                    AnimatedFrame frame = animGameItem.getCurrentFrame();
                    sceneShaderProgram.setUniform("jointsMatrix", frame.getJointMatrices());
                }                
            });
        }
    }

    private void renderInstancedMeshes(Scene scene, Matrix4f viewMatrix) {
    	sceneShaderProgram.setUniform("isInstanced", 1);

        // Render each mesh with the associated game Items
        Map<InstancedMesh, List<GameItem>> mapMeshes = scene.getGameInstancedMeshes();

        Set<InstancedMesh> setMeshes = mapMeshes.keySet();
        List<InstancedMesh> meshesSortedList = new ArrayList<InstancedMesh>(setMeshes);
        Collections.sort(meshesSortedList, new Comparator<Mesh>() {
			@Override
			public int compare(Mesh mesh1, Mesh mesh2) {
				if (mesh1.getMaterial().getTransparency() > mesh2.getMaterial().getTransparency()) return -1;
				else if (mesh1.getMaterial().getTransparency() < mesh2.getMaterial().getTransparency()) return 1;
				else return 0;
			}
        	
        });

        for (InstancedMesh mesh : meshesSortedList) {
        	
        	float transparency = mesh.getMaterial().getTransparency();
        	// System.out.println("Renderer render mesh transparency: " + transparency);

            Texture text = mesh.getMaterial().getTexture();
            if (text != null) {
                sceneShaderProgram.setUniform("numCols", text.getNumCols());
                sceneShaderProgram.setUniform("numRows", text.getNumRows());
            }
            
            sceneShaderProgram.setUniform("material", mesh.getMaterial());
            sceneShaderProgram.setUniform("transparency", transparency);

            filteredItems.clear();
            for (GameItem gameItem : mapMeshes.get(mesh)) {
                if (gameItem.isInsideFrustum()) {
                    filteredItems.add(gameItem);
                }
            }
            if (scene.isRenderShadows()) {
            	shadowRenderer.bindTextures(GL13.GL_TEXTURE2);
            }

            mesh.renderListInstanced(filteredItems, transformation, viewMatrix);
        }        
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
