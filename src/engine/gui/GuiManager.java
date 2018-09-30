package engine.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import config.Config;
import engine.Window;
import engine.graph.Mesh;
import engine.graph.Input;
import engine.graph.Texture;
import engine.gui.popups.ImportPopup;
import engine.gui.popups.InventoryPopup;
import engine.gui.popups.QuitPopup;
import engine.interfaces.IScene;

public class GuiManager {
	
	private long toggleGuiLastTime;
    private boolean inventoryOn = false;
    private List<GuiElement> guiElements = new ArrayList<GuiElement>();
    private GuiElement nextBlock;
    private boolean updateEnabled = true;

    private static InventoryPopup inventory_popup;
    private static ImportPopup import_popup;
    private static QuitPopup quit_popup;

	public void init(Window window, HashMap<String, Mesh> meshTypesMap) {

    	// import dialog
    	Texture txSplashBackground;
		try {
			txSplashBackground = new Texture(Config.RESOURCES_DIR +  "/textures/splash_background.png");
			GuiElement guiSplashBackground = new GuiElement(txSplashBackground, new Vector3f(0.0f, 0.0f, 1), new Vector2f(1.0f, 1.0f));
			guiSplashBackground.setSplashBackground(true);
			addGuiElement(guiSplashBackground);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		inventory_popup = new InventoryPopup();
		inventory_popup.init(this, window, meshTypesMap);

		import_popup = new ImportPopup();
		import_popup.init(this, window);
		
		quit_popup = new QuitPopup();
		quit_popup.init(this, window);
	}

    public QuitPopup getQuitPopup() {
    	return quit_popup;
    }
    
	public ImportPopup getImportPopup() {
		return import_popup;
	}

    public List<GuiElement> getGuiElements() {
    	return guiElements;
    }

	public boolean input(Input input, Window window, IScene scene) {

        if (inventory_popup.isEnabled()) {
        	inventory_popup.input(this, input, window, scene);
            nextBlock = inventory_popup.getNextBlock();
        }

        if (import_popup.isEnabled()) {
        	import_popup.input(this, input, window, scene);
        }
        
        if (quit_popup.isEnabled()) {
        	quit_popup.input(this, input, window);
        }

        return updateEnabled;
	}

    private void slowdownGuiUpdates() {
    	long currentTime = System.currentTimeMillis();
    	if (currentTime - toggleGuiLastTime < 100) {
    		return;
    	}
    	toggleGuiLastTime = currentTime;
	}

	public void toggleInventoryDialog(Window window) {
		slowdownGuiUpdates();
    	if (!inventoryOn) {
    		closeAllGuis(window);
    		inventoryOn = true;
    		inventory_popup.setEnabled(true);
    		updateEnabled = false;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    	} else {
    		inventoryOn = false;
    		inventory_popup.setEnabled(false);
    		updateEnabled = true;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    	}
	}

	public void toggleImportDialog(Window window) {
		slowdownGuiUpdates();
    	if (!import_popup.isEnabled()) {
    		closeAllGuis(window);
    		import_popup.init(this, window);
    		import_popup.setEnabled(true);
    		updateEnabled = false;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    	} else {
    		import_popup.setEnabled(false);
    		updateEnabled = true;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    	}
	}

	public void toggleQuitPopup(Window window) {
		slowdownGuiUpdates();
    	if (!quit_popup.isEnabled()) {
    		closeAllGuis(window);
    		quit_popup.setEnabled(true);
    		updateEnabled = false;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    	} else {
    		quit_popup.setEnabled(false);
    		updateEnabled = true;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    	}
	}

    public void closeAllGuis(Window window) {
    	slowdownGuiUpdates();
		inventoryOn = false;
		import_popup.setEnabled(false);
		quit_popup.setEnabled(false);
		updateEnabled = true;
		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
	}
    
    public boolean areAllGuisClosed() {
    	return !inventoryOn && !import_popup.isEnabled() && !quit_popup.isEnabled();
    }

	public boolean isInventoryOn() {
		return inventoryOn;
	}

	public GuiElement getNextBlock() {
		return nextBlock;
	}

	public boolean getUpdateEnabled() {
		return updateEnabled;
	}

	public void addGuiElement(GuiElement guiElement) {
		guiElements.add(guiElement);
	}

	public Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY, Window window) {
		float x = (2f * mouseX) / window.getWidth() - 1f;
		float y = -((2f * mouseY) / window.getHeight() - 1f);
		return new Vector2f(x, y);
	}
	
    public GuiElement selectGuiItem(Vector2f mousePos) {
    	GuiElement selectedGuiItem = null;
        for (GuiElement guiItem : guiElements) {
        	if (!guiItem.isClickable()) {
        		continue;
        	}
			if (guiItem.isImportDialog() && !import_popup.isEnabled()) {
				continue;
			}
			if (guiItem.isInventory() && !inventoryOn) {
				continue;
			}
        	guiItem.setSelected(false);
        	if (guiItem.getAABB().contains(mousePos.x, mousePos.y)) {
        		selectedGuiItem = guiItem;
        	}
        }
        return selectedGuiItem;
    }
}
