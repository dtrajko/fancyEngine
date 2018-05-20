package engine.graph;

public class QuadMesh extends Mesh {

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

	public QuadMesh(float[] positions, float[] colors, int[] indices, Texture texture) {
		super(positions, colors, indices, texture);
	}
}
