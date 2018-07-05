package engine.gui.popups;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import config.Config;
import engine.Window;
import engine.graph.MouseInput;
import engine.graph.Texture;
import engine.gui.GuiElement;
import engine.gui.GuiManager;
import engine.gui.fonts.FontFactory;
import engine.gui.fonts.FontType;
import engine.gui.fonts.GUIText;
import engine.gui.fonts.TextMaster;

public class ImportPopup {
	
	boolean enabled = false;
	TextMaster textMaster;
	
	public ImportPopup() {
		textMaster = new TextMaster();
	}

	public void init(GuiManager guiManager, Window window) {
		
		textMaster.init();

    	// import dialog
    	Texture texturePanel;
		try {
			texturePanel = new Texture(Config.RESOURCES_DIR +  "/textures/window.png");
			GuiElement guiPanel = new GuiElement(texturePanel, new Vector3f(0.0f, -0.01f, 1), new Vector2f(0.3304f, 0.790f));
			guiPanel.setImportDialog(true);
			guiManager.addGuiElement(guiPanel);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	Texture textureLongButton;
		try {

			textureLongButton = new Texture(Config.RESOURCES_DIR +  "/textures/button_long.png");
			GuiElement guiLongButton01 = new GuiElement(textureLongButton, new Vector3f(0.0f, 0.685f, 1), new Vector2f(0.31f, 0.055f)); // 0.130
			guiLongButton01.setImportDialog(true).setClickable(true);
			guiManager.addGuiElement(guiLongButton01);
			
			GuiElement guiLongButton02 = new GuiElement(textureLongButton, new Vector3f(0.0f, 0.555f, 1), new Vector2f(0.31f, 0.055f));
			guiLongButton02.setImportDialog(true).setClickable(true);
			guiManager.addGuiElement(guiLongButton02);
			
			GuiElement guiLongButton03 = new GuiElement(textureLongButton, new Vector3f(0.0f, 0.425f, 1), new Vector2f(0.31f, 0.055f));
			guiLongButton03.setImportDialog(true).setClickable(true);
			guiManager.addGuiElement(guiLongButton03);
			
			GuiElement guiLongButton04 = new GuiElement(textureLongButton, new Vector3f(0.0f, 0.295f, 1), new Vector2f(0.31f, 0.055f));
			guiLongButton04.setImportDialog(true).setClickable(true);
			guiManager.addGuiElement(guiLongButton04);

		} catch (Exception e) {
			e.printStackTrace();
		}

    	FontType font = FontFactory.getFont("candara", window);

		GUIText guiText01 = new GUIText("snapshot_2018_07_04.txt", 1.2f, font, new Vector2f(0.355f, 0.139f), 1f, false); // 0.065
		guiText01.setColor(1.0f, 1.0f, 1.0f);
		textMaster.setGuiText(0, guiText01);
		
		GUIText guiText02 = new GUIText("snapshot_2018_07_03.txt", 1.2f, font, new Vector2f(0.355f, 0.204f), 1f, false);
		guiText02.setColor(1.0f, 1.0f, 1.0f);
		textMaster.setGuiText(0, guiText02);
		
		GUIText guiText03 = new GUIText("snapshot_2018_07_02.txt", 1.2f, font, new Vector2f(0.355f, 0.269f), 1f, false);
		guiText03.setColor(1.0f, 1.0f, 1.0f);
		textMaster.setGuiText(0, guiText03);
		
		GUIText guiText04 = new GUIText("snapshot_2018_07_01.txt", 1.2f, font, new Vector2f(0.355f, 0.334f), 1f, false);
		guiText04.setColor(1.0f, 1.0f, 1.0f);
		textMaster.setGuiText(0, guiText04);
	}

	public void input(GuiManager guiManager, MouseInput mouseInput, Window window) {
    	Vector2f mouseNDC = guiManager.getNormalisedDeviceCoordinates(
        		(float) mouseInput.getMousePosition().x,
        		(float) mouseInput.getMousePosition().y, window);
    		for (GuiElement gb : guiManager.getGuiElements()) {
    			gb.setMouseOver(false);
    		}
        	GuiElement nextBlock = guiManager.selectGuiItem(mouseNDC);
        	if (nextBlock instanceof GuiElement && nextBlock.isImportDialog()) {
        		nextBlock.setMouseOver(true);
        	}
        if (mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_1) || 
        	mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_2) ||
        	mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_3)) {
        	guiManager.toggleImportDialog(window);
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

