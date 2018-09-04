package engine.tm.gui.fonts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import engine.gui.fonts.FontRenderer;
import engine.tm.loaders.Loader;

public class TextMaster {

	private static Loader loader;
	private static FontRenderer renderer;
	private Map<FontType, List<GUIText>> guiTextsMap = new HashMap<FontType, List<GUIText>>();
	private List<GUIText> guiTexts;

	public TextMaster() {
		renderer = new FontRenderer();
	}

	public void init() {
		loader = new Loader();
		guiTexts = new ArrayList<GUIText>();
		try {
			renderer.setupShader();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void render() {
		renderer.render(guiTextsMap);
	}

	public List<GUIText> getGuiTexts() {
		return guiTexts;
	}

	public Map<FontType, List<GUIText>> getGuiTextsMap() {
		return guiTextsMap;
	}

	public GUIText getGuiText(int index) {
		return guiTexts.get(index);
	}

	public void setGuiText(int index, GUIText newGuiText) {
		guiTexts.add(index, newGuiText);
		loadText(newGuiText);
	}

	public void loadText(GUIText text) {
		FontType font = text.getFont();
		TextMeshData textMeshData = font.loadText(text);
		int vao = loader.loadToVAO(textMeshData.getVertexPositions(), textMeshData.getTextureCoords());
		text.setMeshInfo(vao, textMeshData.getVertexCount());
		List<GUIText> textBatch = guiTextsMap.get(font);
		if (textBatch == null) {
			textBatch = new ArrayList<GUIText>();
			guiTextsMap.put(font, textBatch);
		}
		textBatch.add(text);
	}

	public void removeText(GUIText text) {
		List<GUIText> textBatch = guiTextsMap.get(text.getFont());
		if (textBatch != null) {
			textBatch.remove(text);
			if (textBatch.isEmpty()) {
				guiTextsMap.remove(text.getFont());
			}			
		}
	}

	public static void cleanUp() {
		renderer.cleanUp();
	}
}
