package game2D.render;

import java.util.HashMap;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import engine.graph.Camera;
import engine.interfaces.IScene2D;
import engine.interfaces.ITileType;
import game2D.assets.Assets;
import game2D.assets.Sprite;
import game2D.entities.Entity;
import game2D.shaders.Shader;
import game2D.textures.Texture;
import game2D.world.Tile;

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
	}

	public void loadTextures(IScene2D scene) {
		for (int i = 0; i < scene.getTileTypes().length; i++) {
			if (scene.getTileTypes()[i] != null) {
				if (!tile_textures.containsKey(scene.getTileTypes()[i].getTexture())) {
					String texturePath = scene.getTileTypes()[i].getTexture();
					tile_textures.put(texturePath, new Texture(texturePath));
				}
			}
		}
	}

	public void render(IScene2D scene, Camera camera) {

		if (tile_textures.isEmpty()) {
			loadTextures(scene);
		}

		int posX = (int) camera.getPosition().x / (scene.getScale() * 2);
		int posY = (int) camera.getPosition().y / (scene.getScale() * 2);

		// render background tiles
		for (int i = 0; i < scene.getViewWidth(); i++) {
			for (int j = 0; j < scene.getViewHeight(); j++) {
				
				int getX = i - posX - (scene.getViewWidth() / 2);
				int getY = j + posY - (scene.getViewHeight() / 2);
				int renderX = getX;
				int renderY = -getY;

				ITileType tileType = scene.getTileTypes()[scene.getBackgroundTile()];
				Tile tile = new Tile(tileType);

				renderBackgroundTile(tile, renderX, renderY, scene, camera);
			}
		}

		// render front tiles
		for (int i = 0; i < scene.getViewWidth(); i++) {
			for (int j = 0; j < scene.getViewHeight(); j++) {
				
				int getX = i - posX - (scene.getViewWidth() / 2);
				int getY = j + posY - (scene.getViewHeight() / 2);
				int renderX = getX;
				int renderY = -getY;

				Tile tile = scene.getTile(getX, getY);

				if (tile != null && tile.getType().getId() != scene.getBackgroundTile()) {					
					renderTile(tile, renderX, renderY, scene, camera);
				}
			}
		}
		for (Entity entity : scene.getEntities()) {
			entity.render(shader, camera, scene);
		}
	}

	public void renderBackgroundTile(Tile tile, int x, int y, IScene2D scene, Camera camera) {

		shader.bind();

		Matrix4f worldMatrix = scene.getWorldMatrix();
		
		tile_textures.get(tile.getType().getTexture()).bind(0);

		Matrix4f tile_pos_bg = new Matrix4f().translate(new Vector3f(x * 2, y * 2 + 2, 0));
		Matrix4f target_bg = new Matrix4f();
		camera.getOrthoProjection().mul(worldMatrix, target_bg);
		target_bg.mul(tile_pos_bg);
		shader.setUniform("sampler", 0);
		shader.setUniform("projection", target_bg);
		model.render();
	}

	public void renderTile(Tile tile, int x, int y, IScene2D scene, Camera camera) {

		shader.bind();

		Matrix4f worldMatrix = scene.getWorldMatrix();

		float tileOffsetX = tile.getOffsetX();
		float tileOffsetY = tile.getOffsetY();
		
		// System.out.println("Tile ID: " + tile.getId() + " tileOffsetX: " + tileOffsetX + " tileOffsetY: " + tileOffsetY);

		if (tile_textures.containsKey(tile.getType().getTexture())) {
			tile_textures.get(tile.getType().getTexture()).bind(0);
		}

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
