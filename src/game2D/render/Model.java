package game2D.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

public class Model {
	
	private int draw_count;
	private int v_id;
	private int t_id;
	private int i_id;
	private float[] vertices;
	private float[] tex_coords;
	private int[] indices;

	public Model(float[] vertices, float[] tex_coords, int[] indices) {
		this.draw_count = indices.length;
		this.vertices = vertices;
		this.tex_coords = tex_coords;
		this.indices = indices;
	}

	public void renderInit() {
		v_id = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, v_id);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, createFloatBuffer(vertices), GL15.GL_STATIC_DRAW);
		
		t_id = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, t_id);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, createFloatBuffer(tex_coords), GL15.GL_STATIC_DRAW);
		
		i_id = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, i_id);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, createIntBuffer(indices), GL15.GL_STATIC_DRAW);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public void render() {
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, v_id);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, t_id);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, i_id);

		// enable transparency
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glDrawElements(GL11.GL_TRIANGLES, draw_count, GL11.GL_UNSIGNED_INT, 0);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
	}

	public int getDrawCount() {
		return draw_count;
	}

	private FloatBuffer createFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private IntBuffer createIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	protected void finalize() throws Throwable {
		GL15.glDeleteBuffers(v_id);
		GL15.glDeleteBuffers(t_id);
		GL15.glDeleteBuffers(i_id);
		super.finalize();
	}
}
