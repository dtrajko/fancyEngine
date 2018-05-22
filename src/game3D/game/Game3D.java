package game3D.game;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import game.Game;
import game3D.assets.Cube;
import game3D.entities.Entity3D;
import game3D.models.RawModel;
import game3D.models.TexturedModel;
import game3D.render.Camera3D;
import game3D.render.Loader;
import game3D.render.Renderer;
import game3D.shaders.StaticShader;
import game3D.textures.ModelTexture;

public class Game3D extends Game {

	private Loader loader3D;
	private StaticShader shader3D;
	private Renderer renderer3D;
	private RawModel model3D;
	private Entity3D entity3D;
	private TexturedModel texModel3D;
	private Camera3D camera3D;

	public Game3D() {
		init3D();
	}

	private void init3D() {
		loader3D = new Loader();
		shader3D = new StaticShader();
		renderer3D = new Renderer(shader3D);
		model3D = loader3D.loadToVAO(Cube.getVertices(), Cube.getTexCoords(), Cube.getIndices());
		texModel3D = new TexturedModel(model3D, new ModelTexture(loader3D.loadTexture("grass")));
		entity3D = new Entity3D(texModel3D, new Vector3f(-1, 0, 0), 0, 0, 0, 1);
		camera3D = new Camera3D();
	}

	private void render3D() {
		entity3D.increasePosition(0.002f, 0, 0);
		entity3D.increaseRotation(1, 1, 0);
		camera3D.move();
		renderer3D.prepare();
		shader3D.bind();
		shader3D.loadViewMatrix(camera3D);
		renderer3D.render(entity3D, shader3D);
		shader3D.unbind();
	}
	
	public static void onWindowResize() {
		// GL11.glViewport(0, 0, window.getWidth(), Window.getHeight());
	}

	public void loop() {
		render3D();
	}

	public void render() {
		GL.createCapabilities();
		render3D();
	}

	public void cleanUp() {
		shader3D.cleanUp();
		loader3D.cleanUp();
	}
}
