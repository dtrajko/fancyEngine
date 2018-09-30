package engine.tm.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import engine.Window;
import engine.interfaces.ICamera;
import engine.interfaces.IMasterRenderer;
import engine.interfaces.IScene;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.render.MasterRenderer;
import engine.tm.textures.ModelTexture;
import engine.tm.toolbox.Maths;

public class EntityRenderer {

	private EntityShader shader;

	// contains only regular entities
	private static Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();

	public EntityRenderer(Matrix4f projectionMatrix) {
		this.shader = new EntityShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void init(IScene scene) {
		entities.clear();
		Map<TexturedModel, List<Entity>> entitiesTmp = scene.getEntityList();
		for (TexturedModel model : entitiesTmp.keySet()) {
			List<Entity> batchTmp = entitiesTmp.get(model);
			for (Entity entity : batchTmp) {
				if (entity.isUsingNormalMap()) continue; // handled by NormalMappingRenderer
				List<Entity> batch = entities.get(model);
				if (batch != null) {
					batch.add(entity);
				} else {
					List<Entity> newBatch = new ArrayList<Entity>();
					newBatch.add(entity);
					entities.put(model, newBatch);
				}
			}
		}
	}

	public void render(IScene scene, Vector4f clipPlane) {
		List<Light> lights = scene.getLights();
		ICamera camera = scene.getCamera();
		shader.start();
		prepare(clipPlane, lights, camera);
		for (TexturedModel model : entities.keySet()) {
			bindTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for (Entity entity : batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
		shader.stop();
	}

	private void prepare(Vector4f clipPlane, List<Light> lights, ICamera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(IMasterRenderer.RED, IMasterRenderer.GREEN, IMasterRenderer.BLUE);
		shader.loadLights(lights);
		shader.loadViewMatrix(viewMatrix);
	}

	public void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadTextureAtlasOffset(entity.getTextureOffsetX(), entity.getTextureOffsetY());
	}

	public void render(IScene scene) {
		render(scene, new Vector4f());
	}

	/**
	 * Originally prepareTexturedModel()
	 * @param model
	 */
	private void bindTexturedModel(TexturedModel model) {

		RawModel rawModel = model.getRawModel();

		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		ModelTexture texture = model.getTexture();
		if (texture.isTransparent()) {
			MasterRenderer.disableCulling();
		}

		shader.loadTextureAtlasNumRows(texture.getNumberOfRows());
		shader.loadSkyColor(IMasterRenderer.RED, IMasterRenderer.GREEN, IMasterRenderer.BLUE);
		shader.loadFakeLightingVariable(texture.useFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}

	public void unbindTexturedModel() {
		Window.WindowOptions opts = new Window.WindowOptions();
		if (opts.cullFace) {
			MasterRenderer.enableCulling();			
		}
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		unbindTexture();
	}

	private void unbindTexture() {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public void cleanUp() {
		shader.cleanUp();
	}
}
