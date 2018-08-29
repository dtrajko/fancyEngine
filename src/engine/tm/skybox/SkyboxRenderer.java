package engine.tm.skybox;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import engine.IScene;
import engine.tm.entities.Camera;
import engine.tm.loaders.Loader;
import engine.tm.models.RawModel;
import engine.tm.scene.Scene;

public class SkyboxRenderer {

	private static final float SIZE = 800f;
	
	private static final float[] VERTICES = {
		-SIZE,  SIZE, -SIZE,
		-SIZE, -SIZE, -SIZE,
		 SIZE, -SIZE, -SIZE,
		 SIZE, -SIZE, -SIZE,
		 SIZE,  SIZE, -SIZE,
		-SIZE,  SIZE, -SIZE,
		
		-SIZE, -SIZE,  SIZE,
		-SIZE, -SIZE, -SIZE,
		-SIZE,  SIZE, -SIZE,
		-SIZE,  SIZE, -SIZE,
		-SIZE,  SIZE,  SIZE,
		-SIZE, -SIZE,  SIZE,
		
		 SIZE, -SIZE, -SIZE,
		 SIZE, -SIZE,  SIZE,
		 SIZE,  SIZE,  SIZE,
		 SIZE,  SIZE,  SIZE,
		 SIZE,  SIZE, -SIZE,
		 SIZE, -SIZE, -SIZE,
		
		-SIZE, -SIZE,  SIZE,
		-SIZE,  SIZE,  SIZE,
		 SIZE,  SIZE,  SIZE,
		 SIZE,  SIZE,  SIZE,
		 SIZE, -SIZE,  SIZE,
		-SIZE, -SIZE,  SIZE,
		
		-SIZE,  SIZE, -SIZE,
		 SIZE,  SIZE, -SIZE,
		 SIZE,  SIZE,  SIZE,
		 SIZE,  SIZE,  SIZE,
		-SIZE,  SIZE,  SIZE,
		-SIZE,  SIZE, -SIZE,
		
		-SIZE, -SIZE, -SIZE,
		-SIZE, -SIZE,  SIZE,
		 SIZE, -SIZE, -SIZE,
		 SIZE, -SIZE, -SIZE,
		-SIZE, -SIZE,  SIZE,
		 SIZE, -SIZE,  SIZE
	};

	/**
	 * GL_TEXTURE_CUBE_MAP_POSITIVE_X = Right Face
	 * GL_TEXTURE_CUBE_MAP_NEGATIVE_X = Left Face
	 * GL_TEXTURE_CUBE_MAP_POSITIVE_Y = Top Face
	 * GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = Bottom Face
	 * GL_TEXTURE_CUBE_MAP_POSITIVE_Z = Back Face
	 * GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = Front Face
	 */
	private static String[] TEXTURE_FILES = {
		"skybox_1/right", 
		"skybox_1/left", 
		"skybox_1/top", 
		"skybox_1/bottom", 
		"skybox_1/back", 
		"skybox_1/front"
	};

	private RawModel cube;
	private int texture;
	private SkyboxShader shader;

	public SkyboxRenderer(Matrix4f projectionMatrix) {
		Loader loader = new Loader();
		cube = loader.loadToVAO(VERTICES, 3);
		texture = loader.loadCubeMap(TEXTURE_FILES);
		shader = new SkyboxShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void render(IScene scene) {
		Camera camera = (Camera) ((Scene) scene).getCamera();
		shader.start();
		shader.loadViewMatrix(camera);
		GL30.glBindVertexArray(cube.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
}
