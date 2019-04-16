package engine.gui.popups;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import config.Config;
import engine.Window;
import engine.graph.Input;
import engine.graph.TextureAtlas;
import engine.gui.GuiElement;
import engine.gui.GuiManager;
import engine.gui.fonts.FontFactory;
import engine.gui.fonts.TextMaster;
import engine.tm.gui.fonts.FontType;
import engine.tm.gui.fonts.GUIText;

public class QuitPopup {
	
	boolean enabled = false;
	TextMaster textMaster;
	
	public QuitPopup() {
		textMaster = new TextMaster();
	}

	public void init(GuiManager guiManager, Window window) {

		textMaster.init();

    	TextureAtlas txQuitPopup;
		try {
			txQuitPopup = new TextureAtlas(Config.RESOURCES_DIR +  "/textures/small_popup.png");
			GuiElement guiQuitPopup = new GuiElement(txQuitPopup, new Vector3f(0.0f, 0.0f, 1), new Vector2f(0.24f, 0.22f));
			guiQuitPopup.setQuitPopup(true);
			guiManager.addGuiElement(guiQuitPopup);
		} catch (Exception e) {
			e.printStackTrace();
		}

    	TextureAtlas txButton;
		try {
			txButton = new TextureAtlas(Config.RESOURCES_DIR +  "/textures/button.png");

			GuiElement guiButtonCancel = new GuiElement(txButton, new Vector3f(-0.113f, -0.132f, 1), new Vector2f(0.108f, 0.0508f));
			guiButtonCancel.setQuitPopup(true).setClickable(true).setCancelButton(true);
			guiManager.addGuiElement(guiButtonCancel);
			
			GuiElement guiButtonConfirm = new GuiElement(txButton, new Vector3f(0.113f, -0.132f, 1), new Vector2f(0.108f, 0.0508f));
			guiButtonConfirm.setQuitPopup(true).setClickable(true).setConfirmButton(true);
			guiManager.addGuiElement(guiButtonConfirm);

		} catch (Exception e) {
			e.printStackTrace();
		}

		FontType font = FontFactory.getFont("candara", window);

		GUIText guiQuitGame = new GUIText("Quit Game?", 1.8f, font, new Vector2f(0.445f, 0.44f), 1f, false);
		guiQuitGame.setColor(1.0f, 1.0f, 1.0f);
		textMaster.setGuiText(0, guiQuitGame);

		GUIText guiCancel = new GUIText("Cancel", 1.2f, font, new Vector2f(0.424f, 0.551f), 1f, false);
		guiCancel.setColor(1.0f, 1.0f, 1.0f);
		textMaster.setGuiText(0, guiCancel);
		
		GUIText guiConfirm = new GUIText("Confirm", 1.2f, font, new Vector2f(0.53f, 0.551f), 1f, false);
		guiConfirm.setColor(1.0f, 1.0f, 1.0f);
		textMaster.setGuiText(0, guiConfirm);
	}

	public void input(GuiManager guiManager, Input input, Window window) {
    	Vector2f mouseNDC = guiManager.getNormalisedDeviceCoordinates(
        		(float) input.getMousePosition().x,
        		(float) input.getMousePosition().y, window);
    		for (GuiElement gb : guiManager.getGuiElements()) {
    			gb.setMouseOver(false);
    		}
        	GuiElement nextBlock = guiManager.selectGuiItem(mouseNDC);
        	if (nextBlock instanceof GuiElement && nextBlock.isQuitPopup()) {
        		nextBlock.setMouseOver(true);
        	}
        if (input.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_1) || 
        	input.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_2) ||
        	input.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_3)) {
        	guiManager.toggleQuitPopup(window);
        	if (nextBlock instanceof GuiElement && nextBlock.isCancelButton()) {
        		// do nothing, close the popup
        	}
        	if (nextBlock instanceof GuiElement && nextBlock.isConfirmButton()) {
        		window.close();
        	}
        }
	}

    public TextMaster getTextMaster() {
    	return textMaster;
    }

	public void render() {
		textMaster.render();
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;
	}
}
