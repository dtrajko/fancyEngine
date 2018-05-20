package engine.graph;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

public class Mesh {

	private final int vaoId;
    private final int posVboId;
    private final int colorVboId;
    private final int idxVboId;
    private final int vertexCount;

    public Mesh(float[] positions, float[] colors, int[] indices) {
        FloatBuffer posBuffer = null;
        FloatBuffer colourBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            vertexCount = indices.length;

            vaoId = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(vaoId);

            // Position VBO
            posVboId = GL15.glGenBuffers();
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, posVboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, posBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

            // Colour VBO
            colorVboId = GL15.glGenBuffers();
            colourBuffer = MemoryUtil.memAllocFloat(colors.length);
            colourBuffer.put(colors).flip();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorVboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colourBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);

            // Index VBO
            idxVboId = GL15.glGenBuffers();
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, idxVboId);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            GL30.glBindVertexArray(0);
        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (colourBuffer != null) {
                MemoryUtil.memFree(colourBuffer);
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

    public void render() {
        // Draw the mesh
    	GL30.glBindVertexArray(getVaoId());
    	GL20.glEnableVertexAttribArray(0);
    	GL20.glEnableVertexAttribArray(1);

        GL11.glDrawElements(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        // Restore state
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    public void cleanUp() {
        GL20.glDisableVertexAttribArray(0);

        // Delete the VBOs
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(posVboId);
        GL15.glDeleteBuffers(colorVboId);
        GL15.glDeleteBuffers(idxVboId);

        // Delete the VAO
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoId);
    }
}
