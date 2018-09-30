package engine.tm.animation.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import engine.interfaces.ICamera;
import engine.tm.animation.animatedModel.AnimatedModel;
import engine.tm.entities.Entity;
import engine.tm.toolbox.Maths;
import engine.tm.utils.OpenGlUtils;

/**
 * 
 * This class deals with rendering an animated entity. Nothing particularly new
 * here. The only exciting part is that the joint transforms get loaded up to
 * the shader in a uniform array.
 * 
 * @author Karl
 *
 */
public class AnimatedModelRenderer {

	private AnimatedModelShader shader;

	/**
	 * Initializes the shader program used for rendering animated models.
	 * @param projectionMatrix 
	 */
	public AnimatedModelRenderer(Matrix4f projectionMatrix) {
		this.shader = new AnimatedModelShader();		
		shader.start();
		shader.projectionMatrix.loadMatrix(projectionMatrix);
		shader.stop();
	}

	/**
	 * Renders an animated entity. The main thing to note here is that all the
	 * joint transforms are loaded up to the shader to a uniform array. Also 5
	 * attributes of the VAO are enabled before rendering, to include joint
	 * indices and weights.
	 * 
	 * @param entity
	 *            - the animated entity to be rendered.
	 * @param camera
	 *            - the camera used to render the entity.
	 * @param lightDir
	 *            - the direction of the light in the scene.
	 */
	public void render(AnimatedModel entity, ICamera camera, Vector3f lightDir, Vector4f clipPlane) {
		prepare(entity, camera, lightDir, clipPlane);
		entity.getTexture().bindToUnit(0);
		entity.getModel().bind(0, 1, 2, 3, 4);
		shader.jointTransforms.loadMatrixArray(entity.getJointTransforms());
		GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		entity.getModel().unbind(0, 1, 2, 3, 4);
		finish();
	}

	/**
	 * Starts the shader program and loads up the projection view matrix, as
	 * well as the light direction. Enables and disables a few settings which
	 * should be pretty self-explanatory.
	 * 
	 * @param camera
	 *            - the camera being used.
	 * @param lightDir
	 *            - the direction of the light in the scene.
	 */
	private void prepare(AnimatedModel entity, ICamera camera, Vector3f lightDir, Vector4f clipPlane) {
		shader.start();
		shader.viewMatrix.loadMatrix(Maths.createViewMatrix(camera));
		shader.transformationMatrix.loadMatrix(getTransformationMatrix(entity));
		shader.lightDirection.loadVec3(lightDir);
		shader.clipPlane.loadVec4(clipPlane);
		OpenGlUtils.antialias(true);
		OpenGlUtils.disableBlending();
		OpenGlUtils.enableDepthTesting(true);
	}

	public Matrix4f getTransformationMatrix(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
			entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		return transformationMatrix;
	}

	/**
	 * Stops the shader program after rendering the entity.
	 */
	private void finish() {
		shader.stop();
	}

	/**
	 * Deletes the shader program when the game closes.
	 */
	public void cleanUp() {
		shader.cleanUp();
	}
}
