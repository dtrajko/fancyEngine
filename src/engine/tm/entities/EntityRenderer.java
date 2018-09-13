package engine.tm.entities;

import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import engine.IScene;
import engine.Window;
import engine.graph.ICamera;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.normalMapping.NormalMappingShader;
import engine.tm.render.MasterRenderer;
import engine.tm.scene.Scene;
import engine.tm.textures.ModelTexture;
import engine.tm.toolbox.Maths;

public class EntityRenderer {

	private EntityShader shader;
	private NormalMappingShader shader_nm;

	public EntityRenderer(Matrix4f projectionMatrix) {
		this.shader = new EntityShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();

		this.shader_nm = new NormalMappingShader();
		shader_nm.start();
		shader_nm.loadProjectionMatrix(projectionMatrix);
		shader_nm.connectTextureUnits();
		shader_nm.stop();
	}

	public void render(IScene scene, Vector4f clipPlane) {
		List<Light> lights = ((Scene) scene).getLights();
		ICamera camera = ((Scene) scene).getCamera();
		Map<TexturedModel, List<Entity>> entities = ((Scene) scene).getEntityList();
		shader.start();
		// shader_nm.start();
		prepare(clipPlane, lights, camera);
		for (TexturedModel model : entities.keySet()) {
			bindTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for (Entity entity : batch) {
				if (entity.isUsingNormalMap()) {
					// shader.stop();
					// shader_nm.start();
				} else {
					// shader_nm.stop();
					// shader.start();
				}
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
		shader.stop();
		shader_nm.stop();
	}

	private void prepare(Vector4f clipPlane, List<Light> lights, ICamera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(MasterRenderer.RED, MasterRenderer.GREEN, MasterRenderer.BLUE);
		shader.loadLights(lights);
		shader.loadViewMatrix(viewMatrix);

		shader_nm.loadClipPlane(clipPlane);
		shader_nm.loadSkyColor(MasterRenderer.RED, MasterRenderer.GREEN, MasterRenderer.BLUE);
		// shader_nm.loadLights(lights, viewMatrix);
		shader_nm.loadViewMatrix(viewMatrix);
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
		GL20.glEnableVertexAttribArray(3);

		ModelTexture texture = model.getTexture();
		if (texture.isTransparent()) {
			MasterRenderer.disableCulling();
		}

		shader.loadTextureAtlasNumRows(texture.getNumberOfRows());
		shader.loadSkyColor(MasterRenderer.RED, MasterRenderer.GREEN, MasterRenderer.BLUE);
		shader.loadFakeLightingVariable(texture.useFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

		shader_nm.loadTextureAtlasNumRows(texture.getNumberOfRows());
		shader_nm.loadSkyColor(MasterRenderer.RED, MasterRenderer.GREEN, MasterRenderer.BLUE);
		shader_nm.loadFakeLightingVariable(texture.useFakeLighting());
		shader_nm.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getNormalMap());
	}

	public void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());

		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadTextureAtlasOffset(entity.getTextureOffsetX(), entity.getTextureOffsetY());

		shader_nm.loadTransformationMatrix(transformationMatrix);
		shader_nm.loadTextureAtlasOffset(entity.getTextureOffsetX(), entity.getTextureOffsetY());
	}

	public void unbindTexturedModel() {
		Window.WindowOptions opts = new Window.WindowOptions();
		if (opts.cullFace) {
			MasterRenderer.enableCulling();			
		}
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL30.glBindVertexArray(0);
		unbindTexture();
	}

	private void unbindTexture() {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public void cleanUp() {
		shader.cleanUp();
		shader_nm.cleanUp();
	}
}
