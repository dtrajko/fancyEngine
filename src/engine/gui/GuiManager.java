package engine.gui;

import java.util.List;
import org.joml.Vector2f;
import engine.Window;

public class GuiManager {

	public static Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY, Window window) {
		float x = (2f * mouseX) / window.getWidth() - 1f;
		float y = -((2f * mouseY) / window.getHeight() - 1f);
		return new Vector2f(x, y);
	}
	
    public static GuiButton selectGuiItem(Vector2f mousePos, List<GuiButton> guiItems) {
    	GuiButton selectedGuiItem = null;
        for (GuiButton guiItem : guiItems) {
        	guiItem.setSelected(false);
        	
        	if (guiItem.getAABB().contains(mousePos.x, mousePos.y)) {
        		selectedGuiItem = guiItem;
        	}
        }
        return selectedGuiItem;
    }
}
