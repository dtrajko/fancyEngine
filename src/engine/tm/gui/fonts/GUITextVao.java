package engine.tm.gui.fonts;

public class GUITextVao {

	private GUIText guiText;
	private TextMeshData meshData;
	private int vaoID;

	public GUITextVao(GUIText guiText, TextMeshData meshData, int vaoID) {
		this.guiText = guiText;
		this.meshData = meshData;
		this.vaoID = vaoID;
	}

	public TextMeshData getTextMeshData() {
		return meshData;
	}

	public int getVaoID() {
		return vaoID;
	}

	public GUIText getGUIText() {
		return guiText;
	}
}
