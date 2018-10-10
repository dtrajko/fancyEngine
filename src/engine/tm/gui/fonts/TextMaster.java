package engine.tm.gui.fonts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import engine.tm.gui.fonts.FontType;
import engine.tm.gui.fonts.GUIText;
import engine.tm.gui.fonts.TextMeshData;
import engine.tm.loaders.Loader;

public class TextMaster {

	private Loader loader;
	private FontRenderer renderer;
	private Map<FontType, List<GUIText>> textMap = new HashMap<FontType, List<GUIText>>();
	private FontType font;
	private TextMeshData data;
	private int vao;
	private List<GUIText> textBatch;

	public TextMaster(Loader loader) {
		this.loader = loader;
		renderer = new FontRenderer();
	}

	public void loadText(GUIText guiText) {
		font = guiText.getFont();
		data = font.loadText(guiText);		
		vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		guiText.setMeshInfo(vao, data.getVertexCount());
		textBatch = textMap.get(font);
		if (textBatch == null) {
			textBatch = new ArrayList<GUIText>();
			textMap.put(font, textBatch);
		}
		textBatch.add(guiText);
	}

	public void removeText(GUIText guiText) {
		if (guiText == null) return;
		textBatch = textMap.get(guiText.getFont());
		if (textBatch == null) return;
		textBatch.remove(guiText);
		if (textBatch.isEmpty()) {
			textMap.remove(guiText.getFont());
			textBatch.clear();
		}
	}

	public void clearTextMap() {
		textMap.clear();
	}

	public void render() {
		renderer.render(textMap);
	}

	public void cleanUp() {
		renderer.cleanUp();
	}

	public int getTextEntriesCount() {
		int count = 0;
		for (FontType fontType : textMap.keySet()) {
			count += textMap.get(fontType).size();
		}
		return count;
	}
}
