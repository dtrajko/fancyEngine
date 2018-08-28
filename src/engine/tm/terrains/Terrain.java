package engine.tm.terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.tm.loaders.Loader;
import engine.tm.models.RawModel;
import engine.tm.settings.WorldSettings;
import engine.tm.textures.TerrainTexture;
import engine.tm.textures.TerrainTexturePack;
import engine.tm.toolbox.Maths;

public class Terrain {

	private static final float SIZE = 800;
	private static final int MAX_HEIGHT = 40;
	private static final int MAX_PIXEL_COLOR = 256 * 256 * 256;

	private static int vertexCount = 63;
	
	private float x;
	private float z;
	
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;
	private String heightMap;

	private float[][] heights;

	public Terrain(float gridX, float gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, String heightMap) {
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.heightMap = heightMap;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.model = generateTerrain(loader, this.heightMap);
	}

	private RawModel generateTerrain(Loader loader, String heightMap){
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(WorldSettings.TEXTURES_DIR + "/" + heightMap + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		vertexCount = image.getHeight();

        int count = vertexCount * vertexCount;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6*(vertexCount-1) * (vertexCount-1)];
        int vertexPointer = 0;
        for(int i = 0; i < vertexCount; i++){
            for(int j = 0; j < vertexCount; j++){

				float vertice_height = getHeight(j, i, image);
				if (i % 2 == 0 && j % 2 == 0
					&& i > 0 && i < vertexCount - 1
					&& j > 0 && j < vertexCount - 1) {
					vertice_height -= 1.0f;
				}

				vertices[vertexPointer * 3] = (float) j / ((float) vertexCount - 1) * SIZE;
                vertices[vertexPointer * 3 + 1] = vertice_height;
                vertices[vertexPointer * 3 + 2] = (float) i / ((float) vertexCount - 1) * SIZE;

                Vector3f normal = calculateNormal(j, i, image);

                normals[vertexPointer * 3]     = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;

                textureCoords[vertexPointer * 2] = (float) j / ((float) vertexCount - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) vertexCount - 1);
                vertexPointer++;
            }
        }
		generateTerrainIndices(indices);
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
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

	private Vector3f calculateNormal(int x, int z, BufferedImage image) {
		float heightL = getHeight(x - 1, z, image);
		float heightR = getHeight(x + 1, z, image);
		float heightD = getHeight(x, z - 1, image);
		float heightU = getHeight(x, z + 1, image);
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalize();
		return normal;
	}
	
	private void generateTerrainIndices(int[] indices) {
		int pointer = 0;
		for (int gz = 0; gz < vertexCount - 1; gz++){
			for (int gx = 0; gx < vertexCount - 1; gx++){
				int topLeft = (gz * vertexCount) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * vertexCount) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}		
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

	/*
	private RawModel generateTerrainTriangles(Loader loader){
		int count = vertexCount * vertexCount;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (vertexCount - 1) * (vertexCount - 1)];
		int vertexPointer = 0;
		for (int i = 0; i < vertexCount; i++){
			for(int j = 0; j < vertexCount; j++){

				float vertice_height = -0.3f;
				float normal_x = 0.0f;
				float normal_y = 1.0f;
				float normal_z = 0.0f;
				if ((i % 2 == 0 && j % 2 == 0)) {
					vertice_height = 0.3f;
					normal_x = -0.2f;
					normal_y = 1.0f;
					normal_z = 0.2f;
				}

				vertices[vertexPointer * 3] = (float) j / ((float) vertexCount - 1) * SIZE;
				vertices[vertexPointer * 3 + 1] = vertice_height;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) vertexCount - 1) * SIZE;
				normals[vertexPointer * 3]     = normal_x;
				normals[vertexPointer * 3 + 1] = normal_y;
				normals[vertexPointer * 3 + 2] = normal_z;
				textureCoords[vertexPointer * 2] = (float) j / ((float) vertexCount - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) vertexCount - 1);
				vertexPointer++;
			}
		}
		generateTerrainIndices(indices);
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	*/

	/*
	private RawModel generateFlatTerrain(Loader loader){
        int count = vertexCount * vertexCount;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (vertexCount-1) * (vertexCount-1)];
        int vertexPointer = 0;
        for(int i = 0; i < vertexCount; i++){
            for(int j = 0; j < vertexCount; j++){
                vertices[vertexPointer * 3] = (float) j / ((float) vertexCount - 1) * SIZE;
                vertices[vertexPointer * 3 + 1] = 0;
                vertices[vertexPointer * 3 + 2] = (float) i / ((float) vertexCount - 1) * SIZE;
                normals[vertexPointer * 3] = 0;
                normals[vertexPointer * 3 + 1] = 1;
                normals[vertexPointer * 3 + 2] = 0;
                textureCoords[vertexPointer * 2] = (float) j / ((float) vertexCount - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) vertexCount - 1);
                vertexPointer++;
            }
        }
        generateTerrainIndices(indices);
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }
    */
}
