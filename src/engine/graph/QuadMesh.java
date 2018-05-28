package engine.graph;

public class QuadMesh extends Mesh {

	public QuadMesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
		super(positions, textCoords, normals, indices);
		// TODO Auto-generated constructor stub
	}

	public static float[] positions = new float[] {
        -0.5f,  0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
         0.5f, -0.5f, 0.0f,
         0.5f,  0.5f, 0.0f,
    };

	public static float[] colors = new float[] {
        0.5f, 0.0f, 0.0f,
        0.0f, 0.5f, 0.0f,
        0.0f, 0.0f, 0.5f,
        0.5f, 0.5f, 0.0f,
    };

	public static float[] textCoords = new float[] {};

	public static int[] indices = new int[] {
        0, 1, 3, 3, 1, 2,
    };
}
