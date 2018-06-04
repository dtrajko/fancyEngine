package game;

import java.awt.Font;
import java.io.File;

import org.joml.Quaternionf;
import org.joml.Vector4f;

import config.Config;
import engine.IHud;
import engine.TextItem;
import engine.Window;
import engine.graph.FontTexture;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.OBJLoader;
import engine.graph.Texture;
import engine.items.GameItem;

public class Hud implements IHud {

    private static final Font FONT = new Font("Arial", Font.PLAIN, 40);
    private static final String CHARSET = "ISO-8859-1";
    private final GameItem[] gameItems;
    private final TextItem statusTextItem;
    private final TextItem bullseyeTextItem;

    public Hud(String statusText, Window window) throws Exception {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.statusTextItem = new TextItem(statusText, fontTexture);
        this.statusTextItem.getMesh().getMaterial().setAmbientColour(new Vector4f(0.8f, 0.8f, 1.0f, 10f));

        Font bullseyeFont = new Font("Arial", Font.PLAIN, 60);
        FontTexture bullseyeFontTexture = new FontTexture(bullseyeFont, CHARSET);
        bullseyeTextItem = new TextItem("+", bullseyeFontTexture);
        bullseyeTextItem.getMesh().getMaterial().setAmbientColour(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
        bullseyeTextItem.setPosition(window.getWidth() / 2 - 20, window.getHeight() / 2 - 40, 0);

        // Create list that holds the items that compose the HUD
        gameItems = new GameItem[]{ statusTextItem, bullseyeTextItem };
    }

    public void setStatusText(String statusText) {
        this.statusTextItem.setText(statusText);
    }
    
    @Override
    public GameItem[] getGameItems() {
        return gameItems;
    }
   
    public void updateSize(Window window) {
        this.statusTextItem.setPosition(10f, window.getHeight() - 50f, 0);
    }
}
