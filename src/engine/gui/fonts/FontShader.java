package engine.gui.fonts;

import org.joml.Vector2f;
import org.joml.Vector3f;
import config.Config;

public class FontShader {

	private static final String VERTEX_FILE = "fontRendering/fontVertex.txt";
	private static final String FRAGMENT_FILE = "fontRendering/fontFragment.txt";
	
	private int location_colour;
	private int location_translation;

	public FontShader() {
		// super(VERTEX_FILE, FRAGMENT_FILE);
	}

	protected void getAllUniformLocations() {
		// location_colour = super.getUniformLocation("colour");
		// location_translation = super.getUniformLocation("translation");
	}

	protected void bindAttributes() {
		// super.bindAttribute(0, "position");
		// super.bindAttribute(1, "textureCoords");
	}

	protected void loadColour(Vector3f colour) {
		// super.loadVector(location_colour, colour);
	}

	protected void loadTranslation(Vector2f translation) {
		// super.load2DVector(location_translation, translation);
	}
}
