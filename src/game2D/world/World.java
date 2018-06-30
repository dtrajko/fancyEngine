package game2D.world;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import config.Config;
import engine.Window;
import engine.graph.Camera;
import game.Game2D;
import game2D.collision.AABB;
import game2D.entities.Entity;
import game2D.entities.Player;
import game2D.entities.Transform;

public class World {

	public int view_width = 26;
	public int view_height = 16;
	private byte[] tiles;
	private AABB[] bounding_boxes;
	private List<Entity> entities;
	private int width;
	private int height;
	private int scale;
	private Matrix4f worldMatrix;
	private Window window;
	private Player player;

	public World(Window window, int width, int height, int scale) {
		this.window = window;
		this.width = width;   // 16
		this.height = height; // 16
		this.scale = scale;   // 16
		tiles = new byte[width * height];
		bounding_boxes = new AABB[width * height];
		this.worldMatrix = new Matrix4f().setTranslation(new Vector3f(0));
		this.worldMatrix.scale(scale);
	}

	public World(String worldName, Camera camera, int scale, int bg_tile, Game2D game) {

		window = game.getWindow();

		String tileSheetPath = Config.RESOURCES_DIR + "/levels/" + worldName + "/tiles.png";
		String entitySheetPath = Config.RESOURCES_DIR + "/levels/" + worldName + "/entities.png";
		BufferedImage tile_sheet = null;
		BufferedImage entity_sheet = null;

		try {
			tile_sheet = ImageIO.read(new File(tileSheetPath));
		} catch (IOException e) {
			System.out.println("Failed to load file '" + tileSheetPath + "'");
			e.printStackTrace();
		}

		try {
			entity_sheet = ImageIO.read(new File(entitySheetPath));
		} catch (IOException e) {
			System.out.println("Failed to load file '" + tileSheetPath + "'");
			e.printStackTrace();
		}

		this.width = tile_sheet.getWidth();
		this.height = tile_sheet.getHeight();
		this.scale = scale;
		int[] colorTileSheet = tile_sheet.getRGB(0, 0, width, height, null, 0, width);
		int[] colorEntitySheet = entity_sheet.getRGB(0, 0, width, height, null, 0, width);
		this.tiles = new byte[width * height];
		this.bounding_boxes = new AABB[width * height];
		this.entities = new ArrayList<Entity>();
		this.worldMatrix = new Matrix4f().setTranslation(new Vector3f(0));
		this.worldMatrix.scale(scale);

		Transform transform;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				
				this.tiles[x + y * width] = (byte) bg_tile;

				int red = (colorTileSheet[x + y * width] >> 16) & 0xFF;
				int entity_index = (colorEntitySheet[x + y * width] >> 16) & 0xFF;
				int entity_alpha = (colorEntitySheet[x + y * width] >> 24) & 0xFF;

				Tile tile;
				try {
					tile = Tile.tiles[red];
				} catch (ArrayIndexOutOfBoundsException e) {
					tile = null;
				}
				if (tile != null) {
					setTile(tile, x, y);						
				}
				
				if (entity_alpha > 0) {
					transform = new Transform();
					transform.position.x = x * 2;
					transform.position.y = -y * 2;
					switch (entity_index) {
						case 1:
							player = new Player(transform, game.getInput());
							game.setPlayer(player);							
							entities.add(player);
							camera.getPosition().set(transform.position.mul(-scale, new Vector3f()));
							break;
						default:
							break;
					}
				}
			}
		}
	}

	public void calculateView(Window window) {
		view_width = window.getWidth() / (scale * 2) + 2;
		view_height = window.getHeight() / (scale * 2) + 4;
	}

	public Matrix4f getWorldMatrix() { return this.worldMatrix; }

	public void setMatrix(Matrix4f matrix) {
		this.worldMatrix = matrix;
	}

	public Player getPlayer() {
		return player;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getViewWidth() {
		return view_width;
	}

	public int getViewHeight() {
		return view_height;
	}

	public int getScale() {
		return scale;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void update(float delta, Window window, Camera camera, Game2D game) {
		for (Entity entity : entities) {
			entity.update(delta, window, camera, this, game);
		}
		for (int e1 = 0; e1 < entities.size(); e1++) {
			for (int e2 = e1 + 1; e2 < entities.size(); e2++) {
				entities.get(e1).collideWithEntity(entities.get(e2));
			}
			entities.get(e1).collideWithTiles(this);
		}
	}

	public void correctCamera(Camera camera) {

		Vector3f pos = camera.getPosition();

		int w = -width * scale * 2;
		int h = height * scale * 2;

		if (pos.x > -(window.getWidth() / 2) + scale) {
			pos.x = -(window.getWidth() / 2) + scale;
		}
		if (pos.x < w + (window.getWidth() / 2) + scale) {
			pos.x = w + (window.getWidth() / 2) + scale;
		}
		if (pos.y < (window.getHeight() / 2) - scale) {
			pos.y = (window.getHeight() / 2) - scale;
		}
		if (pos.y > h - (window.getHeight() / 2) - scale) {
			pos.y = h - (window.getHeight() / 2) - scale;
		}
	}

	public void setTile(Tile tile, int x, int y) {
		tile.setX(x);
		tile.setY(y);
		tiles[x + y * width] = tile.getId();
		bounding_boxes[x + y * width] = tile.isSolid() ?
			new AABB(new Vector2f(x * 2, -y * 2), new Vector2f(1, 1)) : null;
	}

	public void updateAABB(int x, int y, float offsetX, float offsetY) {
		AABB bb = bounding_boxes[x + y * width];
		bb.update(new Vector2f(x * 2 + offsetX, -y * 2 + offsetY), new Vector2f(1, 1));
	}

	public Tile getTile(int x, int y) {
		try {
			return Tile.tiles[tiles[x + y * width]];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public MovingTile getMovingTile(int x, int y) {
		try {
			return (MovingTile) Tile.tiles[tiles[x + y * width]];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public AABB getTileBoundingBox(int x, int y) {
		try {
			return bounding_boxes[x + y * width];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public void cleanup() {
		tiles = null;
		bounding_boxes = null;
		entities.clear();
	}
}
