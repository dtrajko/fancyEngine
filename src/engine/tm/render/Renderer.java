package engine.tm.render;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import engine.IScene;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.IRenderer;
import engine.tm.Camera;
import engine.tm.entities.Entity;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.scene.Scene;
import engine.tm.shaders.StaticShader;
import engine.tm.toolbox.Maths;

public class Renderer implements IRenderer {
	
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000f;

	private StaticShader shader;
	private Matrix4f projectionMatrix;

	@Override
	public void init(Window window, IScene scene) {
		shader = new StaticShader();
		projectionMatrix = createProjectionMatrix();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	@Override
	public void render(Window window, ICamera camera, IScene scene, boolean sceneChanged) {
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
		prepare();
		shader.loadViewMatrix((Camera) camera);
		renderModel(((Scene) scene).getEntity(), shader);
	}

	/**
	 * to be called in IGameLogic::update() (?)
	 */
	@Override
	public void prepare() {
		clear();
		shader.start();
	}

	@Override
	public void clear() {
		GL11.glClearColor(0, 0, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
	}

	private void renderModel(TexturedModel texturedModel) {
		RawModel model = texturedModel.getRawModel();
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE0, texturedModel.getTexture().getID());
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}

	private void renderModel(Entity entity, StaticShader shader) {
		TexturedModel texturedModel = entity.getTexturedModel();
		RawModel rawModel = texturedModel.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE0, texturedModel.getTexture().getID());
		GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}

	private Matrix4f createProjectionMatrix() {
		float aspectRatio = Window.width / Window.height;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00(x_scale);
		projectionMatrix.m11(y_scale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * FAR_PLANE * NEAR_PLANE) / frustum_length));
		projectionMatrix.m33(0);

		return projectionMatrix;
	}

	@Override
	public void cleanup() {
		shader.cleanUp();
	}

}
