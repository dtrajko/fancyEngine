package engine.gui;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import engine.Scene;
import engine.Window;
import engine.graph.MouseInput;

public class GuiManager {
	
	private long toggleGuiLastTime;
    private boolean inventoryOn = false;
    private boolean importDialogOn = false;
    private boolean quitPopupOn = false;
    private List<GuiElement> guiElements = new ArrayList<GuiElement>();
    GuiElement nextBlock;
    private boolean updateEnabled = true;

    public List<GuiElement> getGuiElements() {
    	return guiElements;
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
			if (guiItem.isImportDialog() && !importDialogOn) {
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

        if (importDialogOn) {
        	Vector2f mouseNDC = getNormalisedDeviceCoordinates(
	        		(float) mouseInput.getMousePosition().x,
	        		(float) mouseInput.getMousePosition().y, window);
        		for (GuiElement gb : guiElements) {
        			gb.setMouseOver(false);
        		}
	        	nextBlock = selectGuiItem(mouseNDC);
	        	if (nextBlock instanceof GuiElement && nextBlock.isImportDialog()) {
	        		nextBlock.setMouseOver(true);
	        	}
	        if (mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_1) || 
	        	mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_2) ||
	        	mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_3)) {
	        	this.toggleImportDialog(window);
	        }
        }
        
        if (quitPopupOn) {
        	Vector2f mouseNDC = getNormalisedDeviceCoordinates(
	        		(float) mouseInput.getMousePosition().x,
	        		(float) mouseInput.getMousePosition().y, window);
        		for (GuiElement gb : guiElements) {
        			gb.setMouseOver(false);
        		}
	        	GuiElement nextBlock = selectGuiItem(mouseNDC);
	        	if (nextBlock instanceof GuiElement && nextBlock.isQuitPopup()) {
	        		nextBlock.setMouseOver(true);
	        	}
	        if (mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_1) || 
	        	mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_2) ||
	        	mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_3)) {
	        	toggleQuitPopup(window);
	        	if (nextBlock instanceof GuiElement && nextBlock.isCancelButton()) {
	        		// do nothing, close the popup
	        	}
	        	if (nextBlock instanceof GuiElement && nextBlock.isConfirmButton()) {
	        		window.close();	        		
	        	}	        	
	        }
        }

        // Scene.getQuitPopup().input(this, mouseInput, window, updateEnabled);

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
    	if (!importDialogOn) {
    		closeAllGuis(window);
    		importDialogOn = true;
    		updateEnabled = false;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    	} else {
    		importDialogOn = false;
    		updateEnabled = true;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    	}
	}
	
    public void closeAllGuis(Window window) {
    	slowdownGuiUpdates();
		inventoryOn = false;
		importDialogOn = false;
		quitPopupOn = false;
		updateEnabled = true;
		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
	}
    
    public boolean areAllGuisClosed() {
    	return !inventoryOn && !importDialogOn && !quitPopupOn;
    }

	public boolean isInventoryOn() {
		return inventoryOn;
	}

	public boolean isImportDialogOn() {
		return importDialogOn;
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

	public void toggleQuitPopup(Window window) {
    	long currentTime = System.currentTimeMillis();
    	if (currentTime - toggleGuiLastTime < 100) {
    		return;
    	}
    	toggleGuiLastTime = currentTime;

    	if (!quitPopupOn) {
    		closeAllGuis(window);
    		quitPopupOn = true;
    		updateEnabled = false;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    	} else {
    		quitPopupOn = false;
    		updateEnabled = true;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    	}
	}

	public boolean isQuitDialogOn() {
		return quitPopupOn;
	}
}
