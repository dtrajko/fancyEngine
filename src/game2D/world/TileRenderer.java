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

		int posX = (int) camera.getPosition().x / (world.getScale() * 2);
		int posY = (int) camera.getPosition().y / (world.getScale() * 2);
		
		for (int i = 0; i < world.getViewWidth(); i++) {
			for (int j = 0; j < world.getViewHeight(); j++) {
				Tile tile = world.getTile(i - posX - (world.getViewWidth() / 2) + 1, j + posY - (world.getViewHeight() / 2));
				if (tile != null) {
					renderTile(tile, i - posX - (world.getViewWidth() / 2) + 1, -j - posY + (world.getViewHeight() / 2), world, camera);
				}
			}
		}
		for (Entity entity : world.getEntities()) {
			entity.render(shader, camera, world);
		}
	}

	public void renderTile(Tile tile, int x, int y, World world, Camera camera) {

		shader.bind();
		
		Matrix4f worldMatrix = world.getWorldMatrix();

		/* BEGIN render background tile */
		if (tile_textures.containsKey(Tile.tiles[world.getBackgroundTile()])) {
			tile_textures.get(Tile.tiles[world.getBackgroundTile()].getTexture()).bind(0);			
		}
		Matrix4f tile_pos_bg = new Matrix4f().translate(new Vector3f(x * 2, y * 2, 0));
		Matrix4f target_bg = new Matrix4f();
		camera.getOrthoProjection().mul(worldMatrix, target_bg);
		target_bg.mul(tile_pos_bg);
		shader.setUniform("sampler", 0);
		shader.setUniform("projection", target_bg);
		model.render();			
		/* END render background tile */

		if (tile_textures.containsKey(tile.getTexture())) {
			tile_textures.get(tile.getTexture()).bind(0);
		}

		float tileOffsetX = tile.getOffsetX();
		float tileOffsetY = tile.getOffsetY();

		Matrix4f tile_pos = new Matrix4f().translate(new Vector3f(x * 2 + tileOffsetX, y * 2 + tileOffsetY, 0));
		Matrix4f target = new Matrix4f();
		camera.getOrthoProjection().mul(worldMatrix, target);
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
