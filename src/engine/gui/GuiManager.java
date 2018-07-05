package engine.gui;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import config.Config;
import engine.Window;
import engine.graph.MouseInput;
import engine.graph.Texture;
import engine.gui.popups.ImportPopup;
import engine.gui.popups.QuitPopup;

public class GuiManager {
	
	private long toggleGuiLastTime;
    private boolean inventoryOn = false;
    private List<GuiElement> guiElements = new ArrayList<GuiElement>();
    GuiElement nextBlock;
    private boolean updateEnabled = true;
    
    private static QuitPopup quit_popup;
    private static ImportPopup import_popup;

	public void init(Window window) {

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

	public boolean input(MouseInput mouseInput, Window window) {

        if (inventoryOn) {
        	Vector2f mouseNDC = getNormalisedDeviceCoordinates(
	        		(float) mouseInput.getMousePosition().x,
	        		(float) mouseInput.getMousePosition().y, window);
        		for (GuiElement gb : guiElements) {
        			gb.setMouseOver(false);
        		}
	        	nextBlock = selectGuiItem(mouseNDC);
	        	if (nextBlock instanceof GuiElement && nextBlock.isInventory()) {
	        		nextBlock.setMouseOver(true);
	        	}
	        if (mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_1) || 
	        	mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_2) ||
	        	mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_3)) {
	        	toggleInventoryDialog(window);
	        }
        }

        if (import_popup.isEnabled()) {
        	import_popup.input(this, mouseInput, window);
        }
        
        if (quit_popup.isEnabled()) {
        	quit_popup.input(this, mouseInput, window);
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
    		updateEnabled = false;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    	} else {
    		inventoryOn = false;
    		updateEnabled = true;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    	}
	}

	public void toggleImportDialog(Window window) {
		slowdownGuiUpdates();
    	if (!import_popup.isEnabled()) {
    		closeAllGuis(window);
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
