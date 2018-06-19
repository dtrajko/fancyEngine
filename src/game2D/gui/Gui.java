package game2D.gui;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import engine.Window;
import engine.graph.Camera;
import game2D.assets.Assets;
import game2D.entities.Transform;
import game2D.render.Camera2D;
import game2D.render.TileSheet;
import game2D.shaders.Shader;

public class Gui {

	private Shader shader;
	private Camera camera;
	private TileSheet sheet;
	private Button tmpButton;

	public Gui(TileSheet sheet, Window window) {
		this.shader = new Shader("gui");
		this.camera = new Camera();
		this.camera.setOrthoProjection();
		this.sheet = sheet;
	}

	public Gui(Window window) {
		this.shader = new Shader("gui");
		this.camera = new Camera();
		this.camera.setOrthoProjection();
		this.sheet = new TileSheet("gui", 9);
		this.tmpButton = new Button(new Vector2f(0, 0), new Vector2f(96, 32));
	}

	public void resizeCamera(Window window) {
		camera.setOrthoProjection(window);
	}

	public void render() {
		this.tmpButton.render(camera, sheet, shader);
	}

	public void render(Transform transform, int tileIndex) {
		Matrix4f projectionMatrix = camera.getOrthoProjection();
		projectionMatrix.translate(transform.position);
		projectionMatrix.scale(transform.scale);
		shader.bind();
		shader.setUniform("projection", projectionMatrix);
		sheet.bindTile(shader, tileIndex);
		Assets.getModel().render();
	}
}
