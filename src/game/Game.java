package game;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import engine.GameItem;
import engine.IGameLogic;
import engine.Window;
import engine.graph.Mesh;

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
		float[] positions = new float[]{
            -0.5f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
             0.5f, -0.5f, 0.0f,
             0.5f,  0.5f, 0.0f,
        };
        float[] colors = new float[]{
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
        };
        int[] indices = new int[]{
            0, 1, 3, 3, 1, 2,
        };
        Mesh mesh = new Mesh(positions, colors, indices);
        GameItem gameItem1 = new GameItem(mesh);
        gameItem1.setPosition(-0.8f, 0, -2);
        GameItem gameItem2 = new GameItem(mesh);
        gameItem2.setPosition(0.8f, 0, -2);
        gameItems = new GameItem[] { gameItem1, gameItem2 };
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
            gameItem.setRotation(0, 0, rotation);            
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
