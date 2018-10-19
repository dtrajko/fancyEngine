package engine.helloWorld;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import engine.Window;
import engine.interfaces.IMasterRenderer;
import engine.interfaces.IScene;
import engine.tm.entities.Entity;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.toolbox.Maths;

public class MasterRendererHelloWorld implements IMasterRenderer {

	private float RED = 0.2f;
	private float GREEN = 0.3f;
	private float BLUE = 0.8f;

	private SimpleShader shader;
	private static Matrix4f projectionMatrix;

	@Override
	public void init(IScene scene) {
		shader = new SimpleShader();
		projectionMatrix = createProjectionMatrix();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	@Override
	public Matrix4f getProjectionMatrix() {
		if (projectionMatrix == null) {
			createProjectionMatrix();
		}
		return projectionMatrix;
	}

	@Override
	public void render(Window window, IScene scene) {
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
		prepare();
		shader.loadViewMatrix(scene.getCamera().getViewMatrix());
		Entity entity = ((SceneHelloWorld) scene).getEntity();
		renderModel(entity, shader);			
	}

	@Override
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1.0f);
		shader.start();
	}

	private void renderModel(Entity entity, SimpleShader shader) {
		TexturedModel texturedModel = entity.getTexturedModel();
		RawModel rawModel = texturedModel.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
			entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE0, texturedModel.getTexture().getID());
		GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}

	public static Matrix4f createProjectionMatrix() {
		projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Window.width / (float) Window.height;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00(x_scale);
		projectionMatrix.m11(y_scale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * FAR_PLANE * NEAR_PLANE) / frustum_length));
		projectionMatrix.m33(0);

		return projectionMatrix;
	}

	@Override
	public void cleanUp() {
		shader.cleanUp();
	}
}
