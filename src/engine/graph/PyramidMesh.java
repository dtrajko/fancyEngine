package engine.graph;

public class PyramidMesh extends Mesh {

	public PyramidMesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
		super(positions, textCoords, normals, indices);
		// TODO Auto-generated constructor stub
	}

	public static float[] positions = new float[]{
		-0.5f, -0.5f, -0.5f,
		 0.5f, -0.5f, -0.5f,
		 0.5f, -0.5f,  0.5f,
		-0.5f, -0.5f,  0.5f,
		 0.0f, 0.5f,  0.0f
    };

	public static float[] colors = new float[]{
		0.5f, 0.0f, 0.0f, 
		0.0f, 0.5f, 0.0f, 
		0.0f, 0.5f, 0.5f,
		0.5f, 0.5f, 0.0f,
		1f, 1f, 1f,
    };

	public static float[] tex_coords = new float[] {
	};

	public static int[] indices = new int[] {
		0, 1, 4,
		1, 2, 4,
		2, 3, 4,
		3, 0, 4,
		0, 1, 2,
		2, 3, 0,
    };
}
