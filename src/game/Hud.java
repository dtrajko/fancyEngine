package game;

import java.awt.Font;
import org.joml.Vector4f;
import engine.IHud;
import engine.TextItem;
import engine.Window;
import engine.graph.FontTexture;
import engine.items.GameItem;
// import org.lwjgl.nanovg.NVGColor;
//import static org.lwjgl.nanovg.NanoVG.*;
//import static org.lwjgl.nanovg.NanoVGGL3.*;

public class Hud implements IHud {

    private static final Font FONT = new Font("Arial", Font.BOLD, 40);
    private static final String CHARSET = "ISO-8859-1";
    private GameItem[] gameItems;
    private TextItem statusTextItem;
    private TextItem bullseyeTextItem;
    private Window window;
    
    private long vg;

    public Hud() {}

	public void init(Window win) {

		window = win;

		// vg = window.getOptions().antialiasing ? nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES) : nvgCreate(NVG_STENCIL_STROKES);

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

    public void render(Window window) {
    	/*
        nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);

        // Upper ribbon
        nvgBeginPath(vg);
        nvgRect(vg, 0, window.getHeight() - 100, window.getWidth(), 50);
        nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 200, colour));
        nvgFill(vg);

        // Lower ribbon
        nvgBeginPath(vg);
        nvgRect(vg, 0, window.getHeight() - 50, window.getWidth(), 10);
        nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour));
        nvgFill(vg);

        glfwGetCursorPos(window.getWindowHandle(), posx, posy);
        int xcenter = 50;
        int ycenter = window.getHeight() - 75;
        int radius = 20;
        int x = (int) posx.get(0);
        int y = (int) posy.get(0);
        boolean hover = Math.pow(x - xcenter, 2) + Math.pow(y - ycenter, 2) < Math.pow(radius, 2);

        // Circle
        nvgBeginPath(vg);
        nvgCircle(vg, xcenter, ycenter, radius);
        nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour));
        nvgFill(vg);

        // Clicks Text
        nvgFontSize(vg, 25.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        if (hover) {
            nvgFillColor(vg, rgba(0x00, 0x00, 0x00, 255, colour));
        } else {
            nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 255, colour));

        }
        nvgText(vg, 50, window.getHeight() - 87, String.format("%02d", counter));

        // Render hour text
        nvgFontSize(vg, 40.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgFillColor(vg, rgba(0xe6, 0xea, 0xed, 255, colour));
        nvgText(vg, window.getWidth() - 150, window.getHeight() - 95, dateFormat.format(new Date()));

        nvgEndFrame(vg);

        // Restore state
        window.restoreState();
        */
    }
}
