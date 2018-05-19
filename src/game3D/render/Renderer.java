package game3D.render;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import engine.Window;
import game3D.entities.Entity3D;
import game3D.models.RawModel;
import game3D.models.TexturedModel;
import game3D.shaders.StaticShader;
import game3D.utils.Maths;

public class Renderer {

	private static final float FOV = 70; // field of view angle
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;

	private Matrix4f projectionMatrix;
	private Window window;
	private StaticShader shader;

	public Renderer(StaticShader shader) {
		this.shader = shader;
		createProjectionMatrix();
		shader.bind();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.unbind();
	}

	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		// GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		// GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
		// GL11.glPolygonOffset(-1.f,-1.f);
        // GL11.glDisable(GL11.GL_POLYGON_OFFSET_LINE);
		GL11.glClearColor(0.5f, 0.5f, 0.9f, 1.0f);
	}

	public void render(Entity3D entity, StaticShader shader) {
		TexturedModel model = entity.getModel();
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
			entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
		GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);		
		GL30.glBindVertexArray(0);
	}

	private void createProjectionMatrix() {
		float aspectRatio = (float) window.getWidth() / (float) window.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		projectionMatrix = new Matrix4f();
		projectionMatrix._m00(x_scale);
		projectionMatrix._m11(y_scale);
		projectionMatrix._m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix._m23(-1);
		projectionMatrix._m32(-((2 * FAR_PLANE * NEAR_PLANE) / frustum_length));
		projectionMatrix._m33(0);
	}
}
