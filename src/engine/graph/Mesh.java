package engine.graph;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import config.Config;
import engine.GameItem;

public class Mesh {

	public static final int MAX_WEIGHTS = 4;
    private final int vaoId;
    private final List<Integer> vboIdList;
    private final int vertexCount;
    private final Texture texture;
    private Material material;

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices, int[] jointIndices, float[] weights) throws Exception {
    	FloatBuffer posBuffer = null;
    	FloatBuffer textCoordsBuffer = null;
    	FloatBuffer vecNormalsBuffer = null;
    	IntBuffer indicesBuffer = null;

    	vertexCount = indices.length;
    	vboIdList = new ArrayList<Integer>();
    	vaoId = GL30.glGenVertexArrays();
    	this.texture = new Texture(Config.RESOURCES_DIR + "/textures/fancy.png");
	}

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) throws Exception {
    	this(positions, textCoords, normals, indices, new Texture(Config.RESOURCES_DIR + "/textures/fancy.png"));
    }

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices, Texture texture) {

    	FloatBuffer posBuffer = null;
    	FloatBuffer textCoordsBuffer = null;
    	FloatBuffer vecNormalsBuffer = null;
    	IntBuffer indicesBuffer = null;

    	vertexCount = indices.length;
    	vboIdList = new ArrayList<Integer>();
    	vaoId = GL30.glGenVertexArrays();
    	this.texture = texture;


        try {

            GL30.glBindVertexArray(vaoId);

            // Position VBO
            int vboId = GL15.glGenBuffers();
            vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, posBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

            // Texture coordinates VBO
            vboId = GL15.glGenBuffers();
            vboIdList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textCoordsBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);

            // Vertex normals VBO
            vboId = GL15.glGenBuffers();
            vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vecNormalsBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);

            // Index VBO
            vboId = GL15.glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            GL30.glBindVertexArray(0);

        } catch (Exception e) {
        	System.err.println("[Mesh] Failed to instantiate the object.");
        	e.printStackTrace();
        } finally {

            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (textCoordsBuffer != null) {
                MemoryUtil.memFree(textCoordsBuffer);
            }
            if (vecNormalsBuffer != null) {
                MemoryUtil.memFree(vecNormalsBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }

	public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setMaterial(Material material) {
    	this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    private void initRender() {
        Texture texture = material.getTexture();
        if (texture != null) {
            // Activate first texture bank
        	GL13.glActiveTexture(GL13.GL_TEXTURE0);
            // Bind the texture
        	GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
        }

        // Draw the mesh
        GL30.glBindVertexArray(getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
    }

    private void endRender() {
        // Restore state
    	GL20.glDisableVertexAttribArray(0);
    	GL20.glDisableVertexAttribArray(1);
    	GL20.glDisableVertexAttribArray(2);
    	GL30.glBindVertexArray(0);

    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void renderList(List<GameItem> gameItems, Consumer<GameItem> consumer) {
        initRender();
        for (GameItem gameItem : gameItems) {
            // Set up data required by gameItem
            consumer.accept(gameItem);
            // Render this game item
            GL11.glDrawElements(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        }
        endRender();
    }

    public void render() {
        initRender();
        GL11.glDrawElements(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        endRender();
    }

    public void cleanUp() {
    	GL20.glDisableVertexAttribArray(0);

        // Delete the VBOs
    	GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
        	GL15.glDeleteBuffers(vboId);
        }

        // Delete the texture
        Texture texture = material != null ? material.getTexture() : null;
        if (texture != null) {
            texture.cleanup();
        }

        // Delete the VAO
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoId);
    }

    public void deleteBuffers() {
    	GL20.glDisableVertexAttribArray(0);

        // Delete the VBOs
    	GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
        	GL15.glDeleteBuffers(vboId);
        }

        // Delete the VAO
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoId);
    }
}
