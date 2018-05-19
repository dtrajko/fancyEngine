package game2D.gui;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import game2D.assets.Assets;
import game2D.collision.AABB;
import game2D.render.Camera2D;
import game2D.render.TileSheet;
import game2D.shaders.Shader;

public class Button {

	public static final int STATE_IDLE =    0;
	public static final int STATE_SELECT =  1;
	public static final int STATE_CLICKED = 2;

	private AABB boundingBox;
	private int selectedState;
	private static Matrix4f transform = new Matrix4f();
	private Vector2f position;
	private Vector2f scale;

	public Button(Vector2f position, Vector2f scale) {
		this.boundingBox = new AABB(position, scale);
		this.position = this.boundingBox.getCenter();
		this.scale = this.boundingBox.getHalfExtent();
	}

	public void render(Camera2D camera, TileSheet sheet, Shader shader) {

		shader.bind();

		transform.identity().translate(this.position.x, this.position.y, 0).scale(this.scale.x, this.scale.y, 1); // Middle/Fill
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 1, 1);
		Assets.getModel().render();

		this.renderSides(camera, sheet, shader);
		this.renderCorners(camera, sheet, shader);
	}
	
	private void renderSides(Camera2D camera, TileSheet sheet, Shader shader) {

		transform.identity().translate(position.x, position.y + scale.y - 16, 0).scale(scale.x, 16, 1); // Top
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 1, 0);
		Assets.getModel().render();

		transform.identity().translate(position.x, position.y - scale.y + 16, 0).scale(scale.x, 16, 1); // Bottom
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 1, 2);
		Assets.getModel().render();
		
		transform.identity().translate(position.x - scale.x + 16, position.y, 0).scale(16, scale.y, 1); // Left
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 0, 1);
		Assets.getModel().render();

		transform.identity().translate(position.x + scale.x - 16, position.y, 0).scale(16, scale.y, 1); // Right
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 2, 1);
		Assets.getModel().render();
	}

	private void renderCorners(Camera2D camera, TileSheet sheet, Shader shader) {

		transform.identity().translate(position.x - scale.x + 16, position.y + scale.y - 16, 0).scale(16, 16, 1); // Top Left
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 0, 0);
		Assets.getModel().render();
		
		transform.identity().translate(position.x + scale.x - 16, position.y + scale.y - 16, 0).scale(16, 16, 1); // Top Right
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 2, 0);
		Assets.getModel().render();

		transform.identity().translate(position.x - scale.x + 16, position.y - scale.y + 16, 0).scale(16, 16, 1); // Bottom Left
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 0, 2);
		Assets.getModel().render();

		transform.identity().translate(position.x + scale.x - 16, position.y - scale.y + 16, 0).scale(16, 16, 1); // Bottom Right
		shader.setUniform("projection", camera.getProjection().mul(transform));
		sheet.bindTile(shader, 2, 2);
		Assets.getModel().render();
	}
}
