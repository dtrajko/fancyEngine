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

	private static Loader loader;
	private static FontRenderer renderer;
	private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();

	public static void init() {
		renderer = new FontRenderer();
		loader = new Loader();
	}

	public static void loadText(GUIText text) {
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);		
		int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		text.setMeshInfo(vao, data.getVertexCount());
		List<GUIText> textBatch = texts.get(font);
		if (textBatch == null) {
			textBatch = new ArrayList<GUIText>();
			texts.put(font, textBatch);
		}
		textBatch.add(text);		
	}

	public static void removeText(GUIText text) {
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);
		if (textBatch.isEmpty()) {
			texts.remove(text.getFont());
		}
	}

	public static void emptyTextMap() {
		texts.clear();
	}

	public static void render() {
		renderer.render(texts);
	}

	public static void cleanUp() {
		renderer.cleanUp();
	}
}
