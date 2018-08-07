package game2D.frogger;

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
import engine.IGameLogic;
import engine.Window;
import engine.graph.Camera;
import game2D.collision.AABB;
import game2D.entities.Entity;
import game2D.entities.Player;
import game2D.entities.Transform;
import game2D.world.IScene;
import game2D.world.Tile;

public class FroggerScene implements IScene {

	public int view_width = 22;
	public int view_height = 16;
	private byte[] tiles;
	private AABB[] bounding_boxes;
	private List<Entity> entities;
	private int width;
	private int height;
	private int scale;
	private Matrix4f worldMatrix;
	private Window window;
	private FroggerPlayer player;
	private int bgTileID = 0;
	private Tile[] tile_grid;
	private ITileType bgTileType;
	private Tile bgTile;
	private final ITileType[] tileTypes;

	public FroggerScene(String worldName, Camera camera, int scale, int bg_tile, IGameLogic game) {

		tileTypes = TileTypeFrogger.tileTypes;
		window = game.getWindow();
		bgTileID = bg_tile;
		this.bgTileType = tileTypes[bgTileID];
		this.bgTile = new Tile(bgTileType);

		String tileSheetPath = Config.RESOURCES_DIR + "/frogger/levels/" + worldName + "/tiles.png";
		String entitySheetPath = Config.RESOURCES_DIR + "/frogger/levels/" + worldName + "/entities.png";
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
		this.tile_grid = new Tile[width * height];
		this.bounding_boxes = new AABB[width * height];
		this.entities = new ArrayList<Entity>();
		this.worldMatrix = new Matrix4f().setTranslation(new Vector3f(0));
		this.worldMatrix.scale(scale);

		Transform transform;
		ITileType tileType;
		Tile tileObject;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				
				this.tiles[x + y * width] = (byte) bg_tile;

				int red = (colorTileSheet[x + y * width] >> 16) & 0xFF;
				
				int entity_index = (colorEntitySheet[x + y * width] >> 16) & 0xFF;
				int entity_alpha = (colorEntitySheet[x + y * width] >> 24) & 0xFF;
				
				try {
					tileType = TileTypeFrogger.tileTypes[red];					
					tileObject = new Tile(tileType);
				} catch (ArrayIndexOutOfBoundsException e) {
					tileType = this.bgTileType;
					tileObject = this.bgTile;
				}

				if (tileObject.getType().getId() == 10) { // 10 stands for mario_tile tile, class Tile line 32
					tileObject.setOffsetRangeX(-3.0f, 3.0f);
					tileObject.setOffsetDirectionX(-1);
				}
				setTile(tileObject, x, y);

				if (entity_alpha > 0) {
					transform = new Transform();
					transform.position.x = x * 2;
					transform.position.y = -y * 2;
					switch (entity_index) {
						case 1:
							player = new FroggerPlayer(transform, game.getInput());
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
	
	public ITileType[] getTileTypes() {
		return this.tileTypes;
	}

	public Tile[] getTileGrid() {
		return this.tile_grid;
	}

	public void calculateView(Window window) {
		view_width = window.getWidth() / (scale * 2) + 4;
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
	
	public int getBackgroundTile() {
		return bgTileID;
	}

	public void update(float delta, Window window, Camera camera, IGameLogic game) {

		for (Entity entity : entities) {
			entity.update(delta, window, camera, this, game);
		}
		for (int e1 = 0; e1 < entities.size(); e1++) {
			for (int e2 = e1 + 1; e2 < entities.size(); e2++) {
				entities.get(e1).collideWithEntity(entities.get(e2));
			}
			entities.get(e1).collideWithTiles(this);
		}

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Tile tile = getTile(x, y);
				tile.move(this);
				
				// update grid location based on current offsets
				if (tile.getOffsetX() != 0 || tile.getOffsetY() != 0) {
					int newX = x + (int) tile.getOffsetX();
					int newY = y + (int) tile.getOffsetY();
					if (newX != x || newY != y) {
						// System.out.println("Tile old XY: " + x +  "|"+ y + " new XY: " + newX + "|" + newY);
						// setTile(this.bgTile, x, y);
						// setTile(tile, newX, newY);
					}
				}
				updateAABB(x, y, tile.getOffsetX(), tile.getOffsetY());					
			}
		}
	}

	public void updateAABB(int x, int y, float offsetX, float offsetY) {
		AABB bb = bounding_boxes[x + y * width];
		if (bb instanceof AABB) {
			bb.update(new Vector2f(x * 2 + offsetX, -y * 2 + offsetY), new Vector2f(1, 1));			
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
		tiles[x + y * width] = tile.getType().getId();
		tile.setX(x);
		tile.setY(y);
		tile_grid[x + y * width] = tile;
		bounding_boxes[x + y * width] = tile.getType().isSolid() ?
			new AABB(new Vector2f(x * 2, -y * 2), new Vector2f(1, 1)) : null;
	}

	public Tile getTile(int x, int y) {
		try {
			return this.tile_grid[x + y * width];
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
