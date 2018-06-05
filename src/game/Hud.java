package game;

import java.awt.Font;
import org.joml.Vector4f;
import engine.IHud;
import engine.TextItem;
import engine.Window;
import engine.graph.FontTexture;
import engine.items.GameItem;

public class Hud implements IHud {

    private static final Font FONT = new Font("Arial", Font.BOLD, 40);
    private static final String CHARSET = "ISO-8859-1";
    private GameItem[] gameItems;
    private TextItem statusTextItem;
    private TextItem bullseyeTextItem;
    private Window window;

    public Hud() {}

	public void init(Window win) {
		window = win;
        FontTexture fontTexture;
        FontTexture bullseyeFontTexture;
		try {
			fontTexture = new FontTexture(FONT, CHARSET);
	        statusTextItem = new TextItem("...", fontTexture);
	        statusTextItem.getMesh().getMaterial().setAmbientColour(new Vector4f(1.0f, 1.0f, 1.0f, 0.5f));

	        Font bullseyeFont = new Font("Monospaced", Font.PLAIN, 60); // Century, Garamond, Monospaced
	        bullseyeFontTexture = new FontTexture(bullseyeFont, CHARSET);
	        bullseyeTextItem = new TextItem("+", bullseyeFontTexture);
	        bullseyeTextItem.getMesh().getMaterial().setAmbientColour(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
			bullseyeTextItem.setPosition(window.getWidth() / 2 - 20, window.getHeight() / 2 - 40, 0);

	        // Create list that holds the items that compose the HUD
	        gameItems = new GameItem[]{ statusTextItem, bullseyeTextItem };
		} catch (Exception e) {
			e.printStackTrace();
		}
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
