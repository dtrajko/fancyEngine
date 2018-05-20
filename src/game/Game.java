package game;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import config.Config;
import engine.GameItem;
import engine.IGameLogic;
import engine.Window;
import engine.graph.CubeMesh;
import engine.graph.Mesh;
import engine.graph.PyramidMesh;
import engine.graph.Texture;

public class Game implements IGameLogic {

    private int displxInc = 0;
    private int displyInc = 0;
    private int displzInc = 0;
    private int scaleInc = 0;

    private final Renderer renderer;
    private GameItem[] gameItems;

	public Game() {
		renderer = new Renderer();
	}

	@Override
	public void init(Window window) throws Exception {
		renderer.init(window);

        Texture texture = new Texture(Config.RESOURCES_DIR + "/textures/grassblock.png");
        Mesh mesh = new Mesh(CubeMesh.positions, CubeMesh.textCoords, CubeMesh.indices, texture);
        GameItem gameItem = new GameItem(mesh);
        gameItem.setPosition(0, 0, -2);
        gameItems = new GameItem[]{gameItem};
	}

	@Override
	public void input(Window window) {
        displyInc = 0;
        displxInc = 0;
        displzInc = 0;
        scaleInc = 0;
        if (window.isKeyPressed(GLFW.GLFW_KEY_UP)) {
            displyInc = 4;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
            displyInc = -4;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
            displxInc = -4;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
            displxInc = 4;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            displzInc = -2;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_Q)) {
            displzInc = 2;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_Z)) {
            scaleInc = -1;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_X)) {
            scaleInc = 1;
        }
	}

	@Override
	public void update(float interval) {
        for (GameItem gameItem : gameItems) {
            // Update position
            Vector3f itemPos = gameItem.getPosition();
            float posx = itemPos.x + displxInc * 0.01f;
            float posy = itemPos.y + displyInc * 0.01f;
            float posz = itemPos.z + displzInc * 0.01f;
            gameItem.setPosition(posx, posy, posz);
            
            // Update scale
            float scale = gameItem.getScale();
            scale += scaleInc * 0.05f;
            if ( scale < 0 ) {
                scale = 0;
            }
            gameItem.setScale(scale);
            
            // Update rotation angle
            float rotation = gameItem.getRotation().z + 1.5f;
            if ( rotation > 360 ) {
                rotation = 0;
            }
            // gameItem.setRotation(0, 0, rotation);            
        }
	}

    @Override
    public void render(Window window) {
        renderer.render(window, gameItems);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }
}
