package engine.gui.popups;

import java.io.File;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import config.Config;
import engine.Scene;
import engine.Window;
import engine.graph.MouseInput;
import engine.graph.Texture;
import engine.gui.GuiElement;
import engine.gui.GuiManager;
import engine.gui.fonts.FontFactory;
import engine.gui.fonts.FontType;
import engine.gui.fonts.GUIText;
import engine.gui.fonts.TextMaster;
import engine.utils.FileSystem;
import game.Game;

public class ImportPopup {
	
	boolean enabled = false;
	TextMaster textMaster;
	
	public ImportPopup() {
	}

	public void init(GuiManager guiManager, Window window) {

		textMaster = new TextMaster();
		textMaster.init();

    	// import dialog
    	Texture texturePanel;
    	Texture textureLongButton;

		try {
			texturePanel = new Texture(Config.RESOURCES_DIR +  "/textures/window.png");
			textureLongButton = new Texture(Config.RESOURCES_DIR +  "/textures/button_long.png");
			FontType font = FontFactory.getFont("candara", window);
			List<String> files = listSaveFiles();

			GuiElement guiPanel = new GuiElement(texturePanel, new Vector3f(0.0f, -0.01f, 1), new Vector2f(0.3304f, 0.790f));
			guiPanel.setImportDialog(true);
			guiManager.addGuiElement(guiPanel);
			
			float buttonY = 0.685f;
			float buttonStepY = -0.1265f;
			float buttonTextY = 0.140f;
			float buttonTextStepY = 0.0632f;

			for (String file : files) {

				GuiElement guiLongButton = new GuiElement(textureLongButton, new Vector3f(0.0f, buttonY, 1), new Vector2f(0.31f, 0.053f)); // 0.130
				guiLongButton.setImportDialog(true).setClickable(true).setTitle(file);
				guiManager.addGuiElement(guiLongButton);

				GUIText guiText = new GUIText(file, 1.2f, font, new Vector2f(0.355f, buttonTextY), 1f, false); // 0.065
				guiText.setColor(1.0f, 1.0f, 1.0f);
				textMaster.setGuiText(0, guiText);

				buttonY += buttonStepY;
				buttonTextY += buttonTextStepY;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	public void input(GuiManager guiManager, MouseInput mouseInput, Window window, Scene scene) {
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

        	if (nextBlock instanceof GuiElement && nextBlock.isClickable() && nextBlock.getTitle() != null) {
        		// System.out.println("ImportPopup importing the selected save file: [" + nextBlock.getTitle() + "]");
        		scene.load(Game.getMeshTypesMap(), nextBlock.getTitle());
        	}

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
	
	public List<String> listSaveFiles() {
		int listMaxSize = 12;
		String savesDirPath = Config.RESOURCES_DIR + "/saves/";
		final File savesDir = new File(savesDirPath);
		List<String> files = FileSystem.listFilesForFolder(savesDir);
		files = files.subList(0, files.size() < listMaxSize ? files.size() : listMaxSize);
		return files;
	}
}
