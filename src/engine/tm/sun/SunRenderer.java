package engine.tm.sun;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import engine.graph.ICamera;
import engine.tm.ThinMatrixCamera;
import engine.tm.opengl.Vao;
import engine.tm.utils.OpenGlUtils;
import engine.utils.Log;

public class SunRenderer {

	private final SunShader shader;

	private static final float[] POSITIONS = { -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f };

	private final Vao quad;

	public SunRenderer() {
		this.shader = new SunShader();
		this.quad = Vao.create();
		quad.bind();
		quad.storeData(4, POSITIONS);
		quad.unbind();
	}

	public void render(Sun sun, ThinMatrixCamera camera) {		
		prepare(sun, camera);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		endRendering();
	}

	public void cleanUp() {
		shader.cleanUp();
	}

	private void prepare(Sun sun, ThinMatrixCamera camera) {
		OpenGlUtils.antialias(false);
		GL11.glDepthMask(false);
		OpenGlUtils.enableAlphaBlending();
		shader.start();		
		Matrix4f mvpMat = calculateMvpMatrix(sun, camera);
		shader.mvpMatrix.loadMatrix(mvpMat);
		quad.bind(0);
		sun.getTexture().bindToUnit(0);
	}

	private Matrix4f calculateMvpMatrix(Sun sun, ThinMatrixCamera camera) {
		/*
		Matrix4f modelMatrix = new Matrix4f();
		Vector3f sunPos = sun.getWorldPosition(camera.getPosition());		
		modelMatrix = modelMatrix.translate(sunPos);
		Matrix4f modelViewMat = applyViewMatrix(modelMatrix, camera.getViewMatrix());		
		modelViewMat.scale(new Vector3f(sun.getScale(), sun.getScale(), sun.getScale()));
		Matrix4f projMat = camera.getProjectionMatrix();
		Matrix4f mvpMat = projMat.mul(modelViewMat, projMat);
		return mvpMat;
		*/
		return new Matrix4f().identity();
	}

	/**
	 * Check the particle tutorial for explanations of this. Basically we remove
	 * the rotation effect of the view matrix, so that the sun quad is always
	 * facing the camera.
	 * 
	 * @param modelMatrix
	 * @param viewMatrix
	 * @return The model-view matrix.
	 */
	private Matrix4f applyViewMatrix(Matrix4f modelMatrix, Matrix4f viewMatrix) {
		modelMatrix.m00(viewMatrix.m00());
		modelMatrix.m01(viewMatrix.m10());
		modelMatrix.m02(viewMatrix.m20());
		modelMatrix.m10(viewMatrix.m01());
		modelMatrix.m11(viewMatrix.m11());
		modelMatrix.m12(viewMatrix.m21());
		modelMatrix.m20(viewMatrix.m02());
		modelMatrix.m21(viewMatrix.m12());
		modelMatrix.m22(viewMatrix.m22());
		return viewMatrix.mul(modelMatrix);
	}

	private void endRendering() {
		GL11.glDepthMask(true);
		quad.unbind(0);
		shader.stop();
		OpenGlUtils.disableBlending();
	}

}
