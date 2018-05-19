package game3D.render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import game3D.models.RawModel;

public class Loader {

	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();

	public RawModel loadToVAO(float[] positions, float[] textureCoords, int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}

	public int loadTexture(String fileName) {

		int t_id = -1;
		String filePath = "./res/textures/" + fileName + ".png";
		try {
			 BufferedImage bi = ImageIO.read(new File(filePath));
			 int width  = bi.getWidth();
			 int height = bi.getHeight();
			 int[] pixels_raw = new int[width * height];
			 pixels_raw = bi.getRGB(0, 0, width, height, null, 0, width);
			 ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
			 for (int i = 0; i < width; i++) {
				 for (int j = 0; j < height; j++) {
					 int pixel = pixels_raw[i * width + j];
					 pixels.put((byte)((pixel >> 16) & 0xFF)); // RED
					 pixels.put((byte)((pixel >>  8) & 0xFF)); // GREEN
					 pixels.put((byte)((pixel >>  0) & 0xFF)); // BLUE
					 pixels.put((byte)((pixel >> 24) & 0xFF)); // ALPHA
				 }
			 }
			 pixels.flip();
			 t_id = GL11.glGenTextures();
			 GL11.glBindTexture(GL11.GL_TEXTURE_2D, t_id);
			 GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			 GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			 GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
		} catch (IOException e) {
			System.err.println("Failed to load texture '" + filePath + "'");
			e.printStackTrace();
		}
		return t_id;
	}

	public void cleanUp() {
		for (int vaoID:vaos) {
			GL30.glDeleteVertexArrays(vaoID);
		}
		for (int vboID:vbos) {
			GL15.glDeleteBuffers(vboID);
		}
		for (int texture:textures) {
			GL11.glDeleteTextures(texture);
		}
	}

	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}

	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private void unbindVAO() {
		GL30.glBindVertexArray(0);
	}
	
	private void bindIndicesBuffer(int[] indices) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
