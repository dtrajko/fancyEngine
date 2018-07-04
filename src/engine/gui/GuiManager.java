package engine.gui;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import engine.Window;
import engine.graph.MouseInput;

public class GuiManager {
	
	private long toggleGuiLastTime;
    private boolean inventoryOn = false;
    private boolean importDialogOn = false;
    private List<GuiElement> guiElements = new ArrayList<GuiElement>();
    GuiElement nextBlock;
    private boolean updateEnabled = true;

    public List<GuiElement> getGuiIElements() {
    	return guiElements;
    }

	public Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY, Window window) {
		float x = (2f * mouseX) / window.getWidth() - 1f;
		float y = -((2f * mouseY) / window.getHeight() - 1f);
		return new Vector2f(x, y);
	}
	
    public GuiElement selectGuiItem(Vector2f mousePos, List<GuiElement> guiItems, boolean renderInventory, boolean renderImportDialog) {
    	GuiElement selectedGuiItem = null;
        for (GuiElement guiItem : guiItems) {
        	if (!guiItem.isClickable()) {
        		continue;
        	}
			if (guiItem.isImportDialog() && !renderImportDialog) {
				continue;
			}
			if (guiItem.isInventory() && !renderInventory) {
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
	        	nextBlock = selectGuiItem(mouseNDC, guiElements, inventoryOn, importDialogOn);
	        	if (nextBlock instanceof GuiElement && nextBlock.isInventory()) {
	        		nextBlock.setMouseOver(true);
	        	}
	        if (mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_1) || 
	        	mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_2) ||
	        	mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_3)) {
	        	toggleGui(window);
	        }
        }

        if (importDialogOn) {
        	Vector2f mouseNDC = getNormalisedDeviceCoordinates(
	        		(float) mouseInput.getMousePosition().x,
	        		(float) mouseInput.getMousePosition().y, window);
        		for (GuiElement gb : guiElements) {
        			gb.setMouseOver(false);
        		}
	        	nextBlock = selectGuiItem(mouseNDC, guiElements, inventoryOn, importDialogOn);
	        	if (nextBlock instanceof GuiElement && nextBlock.isImportDialog()) {
	        		nextBlock.setMouseOver(true);
	        	}
	        if (mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_1) || 
	        	mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_2) ||
	        	mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_3)) {
	        	this.toggleImportDialog(window);
	        }
        }
        return updateEnabled;
	}

	public void toggleGui(Window window) {
    	long currentTime = System.currentTimeMillis();
    	if (currentTime - toggleGuiLastTime < 100) {
    		return;
    	}
    	toggleGuiLastTime = currentTime;

    	if (!inventoryOn) {
    		closeAllGuis();
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
    	long currentTime = System.currentTimeMillis();
    	if (currentTime - toggleGuiLastTime < 100) {
    		return;
    	}
    	toggleGuiLastTime = currentTime;
    	
    	if (!importDialogOn) {
    		closeAllGuis();
    		importDialogOn = true;
    		updateEnabled = false;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    	} else {
    		importDialogOn = false;
    		updateEnabled = true;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    	}
	}
	
	private void closeAllGuis() {
		inventoryOn = false;
		importDialogOn = false;
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
}
