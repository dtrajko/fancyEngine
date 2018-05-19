package game2D.assets;

public class Sprite {

	private static final float[] vertices = new float[] {
			-1f,  1f, 0, // TOP LEFT     0
			-1f, -1f, 0, // BOTTOM LEFT  1
			 1f, -1f, 0, // BOTTOM RIGHT 2
			 1f,  1f, 0, // TOP RIGHT    3
	};

	private static final float[] tex_coords = new float[] {
			0, 0,
			0, 1,
			1, 1,
			1, 0,
	};

	private static final int[] indices = new int[] {
			0, 1, 3, // top left triangle
			3, 1, 2  // bottom right triangle
	};

	public static float[] getVertices() {
		return vertices;
	}

	public static float[] getTexCoords() {
		return tex_coords;
	}

	public static int[] getIndices() {
		return indices;
	}
}
