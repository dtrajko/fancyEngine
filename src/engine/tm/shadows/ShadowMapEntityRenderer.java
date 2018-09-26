package engine.tm.shadows;

import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import engine.tm.animation.animatedModel.AnimatedModel;
import engine.tm.entities.Entity;
import engine.tm.entities.IPlayer;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.render.MasterRenderer;
import engine.tm.toolbox.Maths;

public class ShadowMapEntityRenderer {

	private Matrix4f projectionViewMatrix;
	private ShadowShader shader;

	/**
	 * @param shader
	 *            - the simple shader program being used for the shadow render
	 *            pass.
	 * @param projectionViewMatrix
	 *            - the orthographic projection matrix multiplied by the light's
	 *            "view" matrix.
	 */
	protected ShadowMapEntityRenderer(ShadowShader shader, Matrix4f projectionViewMatrix) {
		this.shader = shader;
		this.projectionViewMatrix = projectionViewMatrix;
	}

	/**
	 * Renders entities to the shadow map. Each model is first bound and then all
	 * of the entities using that model are rendered to the shadow map.
	 * 
	 * @param entities
	 *            - the entities to be rendered to the shadow map.
	 */
	protected void render(Map<TexturedModel, List<Entity>> entities) {
		for (TexturedModel model : entities.keySet()) {
			RawModel rawModel = model.getRawModel();
			bindModel(rawModel);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
			if (model.getTexture().isTransparent()) {
				MasterRenderer.disableCulling();
			}
			for (Entity entity : entities.get(model)) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
			}
			if (model.getTexture().isTransparent()) {
				MasterRenderer.enableCulling();
			}
		}
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}

	protected void renderAnimatedPlayer(IPlayer player) {
		prepareInstance((Entity) player);
		((AnimatedModel) player).getTexture().bindToUnit(0);
		((AnimatedModel) player).getModel().bind(0, 1, 2, 3, 4);
		// shader.jointTransforms.loadMatrixArray(((AnimatedModel) player).getJointTransforms());
		GL11.glDrawElements(GL11.GL_TRIANGLES, ((AnimatedModel) player).getModel().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		((AnimatedModel) player).getModel().unbind(0, 1, 2, 3, 4);
	}

	/**
	 * Binds a raw model before rendering. Only the attribute 0 is enabled here
	 * because that is where the positions are stored in the VAO, and only the
	 * positions are required in the vertex shader.
	 * 
	 * @param rawModel
	 *            - the model to be bound.
	 */
	private void bindModel(RawModel rawModel) {
		GL30.glBindVertexArray(rawModel.getVaoID());		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
	}

	/**
	 * Prepares an entity to be rendered. The model matrix is created in the
	 * usual way and then multiplied with the projection and view matrix (often
	 * in the past we've done this in the vertex shader) to create the
	 * mvp-matrix. This is then loaded to the vertex shader as a uniform.
	 * 
	 * @param entity
	 *            - the entity to be prepared for rendering.
	 */
	private void prepareInstance(Entity entity) {
		float scale = entity.getScale();
		if (entity instanceof AnimatedModel) {
			// scale *= 10;
		}
		Matrix4f modelMatrix = Maths.createTransformationMatrix(entity.getPosition(),
				entity.getRotX(), entity.getRotY(), entity.getRotZ(), scale);
		Matrix4f mvpMatrix = new Matrix4f();
		projectionViewMatrix.mul(modelMatrix, mvpMatrix);
		shader.loadMvpMatrix(mvpMatrix);
	}

	public Matrix4f getTransformationMatrix(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
			entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		return transformationMatrix;
	}

}
