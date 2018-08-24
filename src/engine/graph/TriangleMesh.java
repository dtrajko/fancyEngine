package engine.graph;

/**
 * TriangleMesh generates a quad (rectangle) made of two triangles using GL_TRIANGLES
 * 
 * @author Dejan Trajkovic
 *
 */
public class TriangleMesh extends Mesh {

	public TriangleMesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
		super(positions, textCoords, normals, indices);
		// TODO Auto-generated constructor stub
	}

	// OpenGL expects vertices to be defined counter clockwise by default
	public static float[] positions = new float[] {
		-0.5f,  0.5f, 0.0f, // V0
		-0.5f, -0.5f, 0.0f, // V1
		 0.5f, -0.5f, 0.0f, // V2
		 0.5f,  0.5f, 0.0f, // V3
    };

	public static float[] colors = new float[] {
		0.0f,  1.0f, 0.5f, // V0
		0.0f,  0.0f, 0.5f, // V1
		1.0f,  0.0f, 0.5f, // V2
		1.0f,  1.0f, 0.5f, // V3
    };

	public static float[] textCoords = new float[] {
		0, 0, // V0
		1, 0, // V1
		1, 1, // V2
		0, 1, // V3
	};

	public static int[] indices = new int[] {
        0, 1, 3, // Top left triangle     (V0, V1, V2)
        3, 1, 2, // Bottom right triangle (V3, V1, V2)
    };
}
