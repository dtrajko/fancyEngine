package game2D.render;

import org.joml.Matrix4f;

import game2D.shaders.Shader;
import game2D.textures.Texture;

public class TileSheet {
	
	private Texture texture;
	private Matrix4f scale;
	private Matrix4f translation;
	private int amountOfTiles;

	public TileSheet(String textureName, int amountOfTiles) {
		this.texture = new Texture(textureName);
		this.amountOfTiles = amountOfTiles;
		this.scale = new Matrix4f().scale(1.0f / (float)amountOfTiles);
		this.translation = new Matrix4f();
	}
	
	public void bindTile(Shader shader, int x, int y) {
		this.scale.translate(x, y, 0, translation);
		shader.setUniform("sampler", 0);
		shader.setUniform("texModifier", translation);
		this.texture.bind(0);
	}

	public void bindTile(Shader shader, int tile) {
		int x = tile % amountOfTiles;
		int y = tile / amountOfTiles;
		bindTile(shader, x, y);
	}
}
