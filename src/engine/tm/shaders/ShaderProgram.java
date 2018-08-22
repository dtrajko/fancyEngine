package engine.tm.shaders;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glShaderSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import engine.utils.Log;

import static org.lwjgl.opengl.GL20.*;

public abstract class ShaderProgram {

	private int programID;
	private int vertexShaderID = -1;
	private int fragmentShaderID = -1;

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	public ShaderProgram(String vertexFile, String fragmentFile) {
		try {
			programID = glCreateProgram();
			vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
			fragmentShaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
			glAttachShader(programID, vertexShaderID);
			glAttachShader(programID, fragmentShaderID);
			bindAttributes();
			glLinkProgram(programID);
			glValidateProgram(programID);
			getAllUniformLocations();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ShaderProgram(String vertexFile, String fragmentFile, String... inVariables) {
		try {
			programID = glCreateProgram();
			int vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
			int fragmentShaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
			glAttachShader(programID, vertexShaderID);
			glAttachShader(programID, fragmentShaderID);
			bindAttributes(inVariables);
			glLinkProgram(programID);
			glDetachShader(programID, vertexShaderID);
			glDetachShader(programID, fragmentShaderID);
			glDeleteShader(vertexShaderID);
			glDeleteShader(fragmentShaderID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ShaderProgram(String vertexFile, String geometryFile, String fragmentFile, boolean useGeom, String... inVariables) {
		try {
			programID = glCreateProgram();
			int vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
			int geometryShaderID = loadShader(geometryFile, GL32.GL_GEOMETRY_SHADER);
			int fragmentShaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
			glAttachShader(programID, vertexShaderID);
			glAttachShader(programID, geometryShaderID);
			glAttachShader(programID, fragmentShaderID);
			bindAttributes(inVariables);
			glLinkProgram(programID);
			glDetachShader(programID, vertexShaderID);
			glDetachShader(programID, geometryShaderID);
			glDetachShader(programID, fragmentShaderID);
			glDeleteShader(vertexShaderID);
			glDeleteShader(geometryShaderID);
			glDeleteShader(fragmentShaderID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int loadShader(String fileName, int type) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			File file = new File(fileName);
			FileReader fr = new FileReader(file);
			BufferedReader reader = new BufferedReader(fr);
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("//\n");
			}
			reader.close();
		} catch (Exception e) {
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderID = glCreateShader(type);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);
		if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader "+ fileName);
			System.exit(-1);
		}
		return shaderID;
	}

    protected int loadShaderAlt(String shaderCode, int shaderType) throws Exception {    	
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }
        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }
        return shaderId;
    }

	protected void storeAllUniformLocations(Uniform... uniforms){
		for(Uniform uniform : uniforms){
			uniform.storeUniformLocation(programID);
		}
		glValidateProgram(programID);
	}

	public void start() {
		glUseProgram(programID);
	}

	public void stop() {
		glUseProgram(0);
	}

	public void cleanUp() {
		stop();
		if (vertexShaderID != -1) {
			glDetachShader(programID, vertexShaderID);
			glDeleteShader(vertexShaderID);
		}
		if (fragmentShaderID != -1) {
			glDetachShader(programID, fragmentShaderID);
			glDeleteShader(fragmentShaderID);
		}
		glDeleteProgram(programID);
	}

	private void bindAttributes(String[] inVariables) {
		for(int i = 0; i < inVariables.length; i++) {
			glBindAttribLocation(programID, i, inVariables[i]);
		}
	}

	protected void loadFloat(int location, float value) {
		glUniform1f(location, value);
	}

	protected void loadInt(int location, int value) {
		glUniform1i(location, value);
	}

	protected void loadVector(int location, Vector3f vector) {
		glUniform3f(location, vector.x, vector.y, vector.z);
	}

	protected void load2DVector(int location, Vector2f vector) {
		glUniform2f(location, vector.x, vector.y);
	}

	protected void load4DVector(int location, Vector4f vector) {
		glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
	}

	protected void loadBoolean(int location, boolean value) {
		float toLoad = 0;
		if (value) {
			toLoad = 1;
		}
		glUniform1f(location, toLoad);
	}
	
	protected void loadMatrix(int location, Matrix4f matrix) {
		matrix.get(matrixBuffer);
		matrixBuffer.flip();
		glUniformMatrix4fv(location, false, matrixBuffer);
	}

	protected void getAllUniformLocations() {
	}

	protected int getUniformLocation(String uniformName) {
		return glGetUniformLocation(programID, uniformName);
	}

	protected void bindAttributes() {
	}

	protected void bindAttribute(int attribute, String variableName) {
		glBindAttribLocation(programID, attribute, variableName);
	}

	protected void bindFragOutput(int attachment, String variableName) {
		GL30.glBindFragDataLocation(programID, attachment, variableName);
	}
}
