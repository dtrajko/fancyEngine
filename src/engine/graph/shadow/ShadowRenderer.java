package engine.graph.shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import config.Config;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL30.*;
import engine.Scene;
import engine.SceneLight;
import engine.Utils;
import engine.Window;
import engine.graph.InstancedMesh;
import engine.graph.Mesh;
import engine.graph.Renderer;
import engine.graph.ShaderProgram;
import engine.graph.Transformation;
import engine.graph.anim.AnimGameItem;
import engine.graph.anim.AnimatedFrame;
import engine.graph.lights.DirectionalLight;
import engine.interfaces.ICamera;
import engine.interfaces.IScene;
import engine.items.GameItem;

public class ShadowRenderer {

    public static final int NUM_CASCADES = 3;
    public static final float[] CASCADE_SPLITS = new float[]{ Window.Z_FAR / 20.0f, Window.Z_FAR / 10.0f, Window.Z_FAR };
    private ShaderProgram depthShaderProgram;
    private List<ShadowCascade> shadowCascades;
    private ShadowBuffer shadowBuffer;
    private final List<GameItem> filteredItems;

    public ShadowRenderer() {
        filteredItems = new ArrayList<>();
    }

    public void init(Window window) throws Exception {
        shadowBuffer = new ShadowBuffer();
        shadowCascades = new ArrayList<>();

        setupDepthShader();

        float zNear = Window.Z_NEAR;
        for (int i = 0; i < NUM_CASCADES; i++) {
            ShadowCascade shadowCascade = new ShadowCascade(zNear, CASCADE_SPLITS[i]);
            shadowCascades.add(shadowCascade);
            zNear = CASCADE_SPLITS[i];
        }
    }

    public List<ShadowCascade> getShadowCascades() {
        return shadowCascades;
    }

    public void bindTextures(int start) {
        this.shadowBuffer.bindTextures(start);
    }

    private void setupDepthShader() throws Exception {
        depthShaderProgram = new ShaderProgram();
        depthShaderProgram.createVertexShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/depth_vertex.vs"));
        depthShaderProgram.createFragmentShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/depth_fragment.fs"));
        depthShaderProgram.link();

        depthShaderProgram.createUniform("isInstanced");
        depthShaderProgram.createUniform("modelNonInstancedMatrix");
        depthShaderProgram.createUniform("lightViewMatrix");
        depthShaderProgram.createUniform("jointsMatrix");
        depthShaderProgram.createUniform("orthoProjectionMatrix");
    }

    private void update(Window window, Matrix4f viewMatrix, IScene scene) {
        SceneLight sceneLight = ((Scene) scene).getSceneLight();
        DirectionalLight directionalLight = sceneLight != null ? sceneLight.getDirectionalLight() : null;
        for (int i = 0; i < NUM_CASCADES; i++) {
            ShadowCascade shadowCascade = shadowCascades.get(i);
            shadowCascade.update(window, viewMatrix, directionalLight);
        }
    }

    public void render(Window window, IScene scene, ICamera camera, Transformation transformation, Renderer renderer) {
        update(window, camera.getViewMatrix(), scene);

        // Setup view port to match the texture size
        glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer.getDepthMapFBO());
        glViewport(0, 0, ShadowBuffer.SHADOW_MAP_WIDTH, ShadowBuffer.SHADOW_MAP_HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShaderProgram.bind();

        // Render scene for each cascade map
        for (int i = 0; i < NUM_CASCADES; i++) {
            ShadowCascade shadowCascade = shadowCascades.get(i);

            depthShaderProgram.setUniform("orthoProjectionMatrix", shadowCascade.getOrthoProjMatrix());
            depthShaderProgram.setUniform("lightViewMatrix", shadowCascade.getLightViewMatrix());

            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowBuffer.getDepthMapTexture().getIds()[i], 0);
            glClear(GL_DEPTH_BUFFER_BIT);

            renderNonInstancedMeshes(scene, transformation);

            renderInstancedMeshes(scene, transformation);
        }

        // Unbind
        depthShaderProgram.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void renderNonInstancedMeshes(IScene scene, Transformation transformation) {
        depthShaderProgram.setUniform("isInstanced", 0);

        // Render each mesh with the associated game Items
        Map<Mesh, List<GameItem>> mapMeshes = ((Scene) scene).getGameMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
            mesh.renderList(mapMeshes.get(mesh), (GameItem gameItem) -> {
                Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
                depthShaderProgram.setUniform("modelNonInstancedMatrix", modelMatrix);
                if (gameItem instanceof AnimGameItem) {
                    AnimGameItem animGameItem = (AnimGameItem) gameItem;
                    AnimatedFrame frame = animGameItem.getCurrentFrame();
                    depthShaderProgram.setUniform("jointsMatrix", frame.getJointMatrices());
                }
            }
            );
        }
    }

    private void renderInstancedMeshes(IScene scene, Transformation transformation) {
        depthShaderProgram.setUniform("isInstanced", 1);

        // Render each mesh with the associated game Items
        Map<InstancedMesh, List<GameItem>> mapMeshes = ((Scene) scene).getGameInstancedMeshes();
        for (InstancedMesh mesh : mapMeshes.keySet()) {
            filteredItems.clear();
            for (GameItem gameItem : mapMeshes.get(mesh)) {
                if (gameItem.isInsideFrustum()) {
                    filteredItems.add(gameItem);
                }
            }
            bindTextures(GL_TEXTURE2);

            mesh.renderListInstanced(filteredItems, transformation, null);
        }
    }

    public void cleanup() {
        if (shadowBuffer != null) {
            shadowBuffer.cleanup();
        }
        if (depthShaderProgram != null) {
            depthShaderProgram.cleanup();
        }
    }

}
