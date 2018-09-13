package engine.tm.normalMapping;

import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import engine.IScene;
import engine.graph.ICamera;
import engine.tm.entities.Entity;
import engine.tm.entities.Light;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.render.MasterRenderer;
import engine.tm.scene.Scene;
import engine.tm.textures.ModelTexture;
import engine.tm.toolbox.Maths;

public class NormalMappingRenderer {

	private NormalMappingShader shader;

	public NormalMappingRenderer(Matrix4f projectionMatrix) {
		this.shader = new NormalMappingShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(IScene scene, Vector4f clipPlane) {
		List<Light> lights = ((Scene) scene).getLights();
		ICamera camera = ((Scene) scene).getCamera();
		Map<TexturedModel, List<Entity>> entities = ((Scene) scene).getEntityList();
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
		shader.loadClipPlane(clipPlane);
		// need to be public variables in MasterRenderer
		shader.loadSkyColor(MasterRenderer.RED, MasterRenderer.GREEN, MasterRenderer.BLUE);
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		shader.loadLights(lights, viewMatrix);
		shader.loadViewMatrix(viewMatrix);
	}

	public void render(IScene scene) {
		render(scene, new Vector4f());
	}

	private void bindTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		ModelTexture texture = model.getTexture();
		shader.loadTextureAtlasNumRows(texture.getNumberOfRows());
		if (texture.isTransparent()) {
			MasterRenderer.disableCulling();
		}
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getNormalMap());
	}

	private void unbindTexturedModel() {
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
				entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadTextureAtlasOffset(entity.getTextureOffsetX(), entity.getTextureOffsetY());
	}

	public void cleanUp(){
		shader.cleanUp();
	}
}
