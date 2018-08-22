package engine.tm.terrains;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import engine.tm.generators.ColorGenerator;
import engine.tm.generators.PerlinNoise;
import engine.tm.opengl.Vao;
import engine.tm.opengl.VaoLoader;
import engine.tm.utils.Color;

/**
 * This is a method that I came up with this week to try and achieve correct
 * low-poly lighting while still using the "flat" type qualifier in the shaders.
 * It's a bit more complicated than the other techniques, and I'll explain it
 * more in the tutorial, but basically the idea is to duplicate just enough
 * vertices so that there is one vertex for every triangle in the terrain. Then,
 * each triangle can have its own provoking vertex, and therefore the provoking
 * vertex for each triangle can provide that triangle's correct normal vector.
 * This allows for correct lighting without duplicating all of the vertices, and
 * without using an expensive geometry shader stage.
 * 
 * @author Karl
 *
 */
public class HybridTerrainGenerator extends TerrainGenerator {

	private static final int VERTEX_SIZE_BYTES = 12 + 4 + 4;// position + normal + colour

	private final TerrainRenderer renderer;

	public HybridTerrainGenerator(PerlinNoise perlinNoise, ColorGenerator colourGen) {
		super(perlinNoise, colourGen);
		this.renderer = new TerrainRenderer(true);
	}

	@Override
	public void cleanUp() {
		renderer.cleanUp();
	}

	@Override
	protected Terrain createTerrain(float[][] heights, Color[][] colors) {
		int vertexCount = calculateVertexCount(heights.length);
		byte[] terrainData = createMeshData(heights, colors, vertexCount);
		int[] indices = IndexGenerator.generateIndexBuffer(heights.length);
		Vao vao = VaoLoader.createVao(terrainData, indices);
		return new Terrain(vao, indices.length, renderer, heights);
	}

	private int calculateVertexCount(int vertexLength) {
		int bottom2Rows = 2 * vertexLength;
		int remainingRowCount = vertexLength - 2;
		int topCount = remainingRowCount * (vertexLength - 1) * 2;
		return topCount + bottom2Rows;
	}

	private byte[] createMeshData(float[][] heights, Color[][] colors, int vertexCount) {
		int byteSize = VERTEX_SIZE_BYTES * vertexCount;
		ByteBuffer buffer = ByteBuffer.allocate(byteSize).order(ByteOrder.nativeOrder());
		GridSquare[] lastRow = new GridSquare[heights.length - 1];
		for (int row = 0; row < heights.length - 1; row++) {
			for (int col = 0; col < heights[row].length - 1; col++) {
				GridSquare square = new GridSquare(row, col, heights, colors);
				square.storeSquareData(buffer);
				if (row == heights.length - 2) {
					lastRow[col] = square;
				}
			}
		}
		for (int i = 0; i < lastRow.length; i++) {
			lastRow[i].storeBottomRowData(buffer);
		}
		return buffer.array();
	}

}
