package game;

import java.awt.Font;
import java.io.File;

import org.joml.Vector4f;

import config.Config;
import engine.GameItem;
import engine.IHud;
import engine.TextItem;
import engine.Window;
import engine.graph.FontTexture;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.OBJLoader;

public class Hud implements IHud {

    private static final Font FONT = new Font("Consolas", Font.PLAIN, 20);
    private static final String CHARSET = "ISO-8859-1";
    private final GameItem[] gameItems;
    private final TextItem statusTextItem;
    private final GameItem compassItem;
    
    private int counter;

    public Hud(String statusText) throws Exception {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.statusTextItem = new TextItem(statusText, fontTexture);
        this.statusTextItem.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 0.5f));

        // Create compass
        Mesh mesh = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/compass.obj");
        Material material = new Material();
        material.setAmbientColour(new Vector4f(1, 0, 0, 1));
        mesh.setMaterial(material);
        compassItem = new GameItem(mesh);
        compassItem.setScale(50);
        // Rotate to transform it to screen coordinates
        compassItem.setRotation(0f, 0f, 180f);

        // Create list that holds the items that compose the HUD
        gameItems = new GameItem[]{statusTextItem, compassItem};
    }

    public void rotateCompass(float angle) {
        this.compassItem.setRotation(0, 0, 180 + angle);
    }

    public void init(Window window) throws Exception {
        counter = 0;
    }

    public void incCounter() {
        counter++;
        if (counter > 99) {
            counter = 0;
        }
    }

    @Override
    public GameItem[] getGameItems() {
        return gameItems;
    }

    public void updateSize(Window window) {
        this.statusTextItem.setScale(5.0f);
        this.statusTextItem.setPosition(20f, window.getHeight() - 140f, 0);
        this.compassItem.setPosition(window.getWidth() - 60f, 60f, 0);
    }

}
