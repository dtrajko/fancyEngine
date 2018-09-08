package engine.tm.terrains;

import java.awt.image.BufferedImage;
import java.util.Random;
import org.joml.Vector2f;
import org.joml.Vector3f;
import engine.tm.loaders.Loader;
import engine.tm.models.RawModel;
import engine.tm.textures.TerrainTexture;
import engine.tm.textures.TerrainTexturePack;
import engine.tm.toolbox.Maths;

public class TerrainProcedural implements ITerrain {

	private static final float SIZE = 800;
	private static final float MAX_HEIGHT = 100;
	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;
	private static final int VERTEX_COUNT = 256;
	private static final int SEED = new Random().nextInt(1000000000);
	
	private float x;
	private float z;
	
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;
	private String heightMap;

	private float[][] heights;

	private HeightsGenerator generator;

	public TerrainProcedural(float gridX, float gridZ, Loader loader, TerrainTexturePack texturePack,
			TerrainTexture blendMap, String heightMap) {
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.heightMap = heightMap;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		generator = new HeightsGenerator();
		// generator = new HeightsGenerator(gridX, gridZ, VERTEX_COUNT, SEED);
		this.model = generateTerrain(loader);
	}

	private RawModel generateTerrain(Loader loader){
		
		int vertex_count = VERTEX_COUNT;
		heights = new float[vertex_count][vertex_count];
		int count = vertex_count * vertex_count;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (vertex_count - 1) * (vertex_count - 1)];
		int vertexPointer = 0;
		for (int i = 0; i < vertex_count; i++){
			for(int j = 0; j < vertex_count; j++){
				vertices[vertexPointer * 3] = (float) j / ((float) vertex_count - 1) * SIZE;
				float height = this.getHeight(j, i, generator);
				heights[j][i] = height;
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) vertex_count - 1) * SIZE;
				Vector3f normal = this.calculateNormal(j, i, generator);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;
				textureCoords[vertexPointer * 2] = (float) j / ((float) vertex_count - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) vertex_count - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < vertex_count - 1; gz++){
			for (int gx = 0; gx < vertex_count - 1; gx++){
				int topLeft = (gz * vertex_count) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * vertex_count) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}

	private Vector3f calculateNormal(int x, int z, BufferedImage image) {
		float heightL = getHeight(x - 1, z, image);
		float heightR = getHeight(x + 1, z, image);
		float heightD = getHeight(x, z - 1, image);
		float heightU = getHeight(x, z + 1, image);
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalize();
		return normal;
	}

	private Vector3f calculateNormal(int x, int z, HeightsGenerator generator) {
		float heightL = getHeight(x - 1, z, generator);
		float heightR = getHeight(x + 1, z, generator);
		float heightD = getHeight(x, z - 1, generator);
		float heightU = getHeight(x, z + 1, generator);
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalize();
		return normal;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public RawModel getModel() {
		return model;
	}

	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}

	public TerrainTexture getBlendMap() {
		return blendMap;
	}

	public float getHeightOfTerrain(float worldX, float worldZ) {
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		float gridSquareSize = SIZE / ((float) heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		if (gridX < 0 || gridX >= heights.length - 1 || 
			gridZ < 0 || gridZ >= heights.length - 1) {
			return 0;
		}
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		float answer;
		if (xCoord <= 1 - zCoord) {
			answer = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
				heights[gridX + 1][gridZ], 0), new Vector3f(0,
				heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} else {
			answer = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
				heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
				heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}
		return answer;
	}

	private float getHeight(int x, int z, BufferedImage image) {
		if (x < 0 || x >= image.getWidth() || z < 0 || z >= image.getHeight()) {
			return 0;
		}
		float height = image.getRGB(x, z);
		height += this.MAX_PIXEL_COLOR / 2f;
		height /= this.MAX_PIXEL_COLOR / 2f;
		height *= this.MAX_HEIGHT;
		return height;
	}

	private float getHeight(int x, int z, HeightsGenerator generator) {
		return generator.generateHeight(x, z);
	}
}
