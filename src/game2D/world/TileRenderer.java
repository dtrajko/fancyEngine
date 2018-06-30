package game2D.world;

import java.util.HashMap;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import engine.graph.Camera;
import game2D.assets.Assets;
import game2D.assets.Sprite;
import game2D.entities.Entity;
import game2D.render.Model;
import game2D.shaders.Shader;
import game2D.textures.Texture;

public class TileRenderer {

	private HashMap<String, Texture> tile_textures;
	private Model model;
	private static Shader shader;

	public TileRenderer() {
		model = new Model(Sprite.getVertices(), Sprite.getTexCoords(), Sprite.getIndices());
	}

	public void init() {
		shader = new Shader("shader");
		model.renderInit();
		Assets.getModel().renderInit();
		tile_textures = new HashMap<String, Texture>();
		for (int i = 0; i < Tile.tiles.length; i++) {
			if (Tile.tiles[i] != null) {
				if (!tile_textures.containsKey(Tile.tiles[i].getTexture())) {
					String texturePath = Tile.tiles[i].getTexture();
					tile_textures.put(texturePath, new Texture(texturePath));
				}
			}
		}
	}

	public void render(World world, Camera camera) {

		int posX = (int)camera.getPosition().x / (world.getScale() * 2);
		int posY = (int)camera.getPosition().y / (world.getScale() * 2);
		
		for (int i = 0; i < world.getViewWidth(); i++) {
			for (int j = 0; j < world.getViewHeight(); j++) {
				Tile tile = world.getTile(i - posX - (world.getViewWidth() / 2) + 1, j + posY - (world.getViewHeight() / 2));
				if (tile != null) {
					renderTile(tile, i - posX - (world.getViewWidth() / 2) + 1, -j - posY + (world.getViewHeight() / 2), world.getWorldMatrix(), camera);
				}
			}
		}
		for (Entity entity : world.getEntities()) {
			entity.render(shader, camera, world);
		}
	}

	public void renderTile(Tile tile, int x, int y, Matrix4f world, Camera camera) {

		shader.bind();

		if (tile_textures.containsKey(tile.getTexture())) {
			tile_textures.get(tile.getTexture()).bind(0);
		}
		
		int tile_x = 0; // tile.getX();
		int tile_y = 0; // tile.getY();
		
		Matrix4f tile_pos = new Matrix4f().translate(new Vector3f(x * 2 + tile_x, y * 2 + tile_y, 0));
		Matrix4f target = new Matrix4f();
		camera.getOrthoProjection().mul(world, target);
		target.mul(tile_pos);
		shader.setUniform("sampler", 0);
		shader.setUniform("projection", target);
		model.render();
	}

	public Shader getShader() {
		return shader;
	}

	public void clear() {
		// shader.finalize();
	}
}
