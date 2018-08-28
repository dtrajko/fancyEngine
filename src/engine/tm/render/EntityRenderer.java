package engine.tm.render;

import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import engine.IScene;
import engine.Window;
import engine.graph.ICamera;
import engine.tm.entities.Entity;
import engine.tm.entities.Light;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.scene.Scene;
import engine.tm.shaders.StaticShader;
import engine.tm.textures.ModelTexture;
import engine.tm.toolbox.Maths;

public class EntityRenderer {

	private StaticShader shader;

	public EntityRenderer(Matrix4f projectionMatrix) {
		this.shader = new StaticShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(IScene scene) {
		Light light = ((Scene) scene).getLight();
		ICamera camera = ((Scene) scene).getCamera();
		Map<TexturedModel, List<Entity>> entities = ((Scene) scene).getEntityList();
		shader.start();
		shader.loadLight(light);
		shader.loadViewMatrix(camera);
		for(TexturedModel model: entities.keySet()) {
			bindTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity:batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
		shader.stop();		
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
		shader.loadSkyColor(MasterRenderer.RED, MasterRenderer.GREEN, MasterRenderer.BLUE);
		shader.loadFakeLightingVariable(texture.useFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}

	public void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadTextureAtlasOffset(entity.getTextureOffsetX(), entity.getTextureOffsetY());
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
