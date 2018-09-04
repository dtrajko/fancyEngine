package engine.tm.water;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import engine.GameEngine;
import engine.IScene;
import engine.tm.entities.Camera;
import engine.tm.entities.Light;
import engine.tm.scene.Scene;
import engine.tm.toolbox.Maths;

public class WaterRenderer {
 
	private WaterShader shader;
	
	private WaterFrameBuffers fbos;
	private static final float WAVE_SPEED = 0.01f;
	private float moveFactor = 0;

    public WaterRenderer(Matrix4f projectionMatrix) {
        this.shader = new WaterShader();
        this.fbos = new WaterFrameBuffers();
        shader.start();
        shader.connectTextureUnits();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(IScene scene) {
        prepareRender(scene);
        for (WaterTile tile : ((Scene) scene).getWaterTiles()) {
            Matrix4f modelMatrix = Maths.createTransformationMatrix(
                    new Vector3f(tile.getX(), tile.getY(), tile.getZ()), 0, 0, 0,
                    WaterTile.TILE_SIZE);
            shader.loadModelMatrix(modelMatrix);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, ((Scene) scene).getWater().getQuad().getVertexCount());
        }
        unbind();
    }

    private void prepareRender(IScene scene) {

    	Camera camera = (Camera) ((Scene) scene).getCamera();
    	Light sun = ((Scene) scene).getLights().get(0);
    	Water water = ((Scene) scene).getWater();

        shader.start();
        shader.loadViewMatrix(camera);
        moveFactor += WAVE_SPEED * (1f / GameEngine.TARGET_UPS);
        moveFactor %= 1;
        shader.loadMoveFactor(moveFactor);
        shader.loadLight(sun);
        GL30.glBindVertexArray(((Scene) scene).getWater().getQuad().getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getReflectionTexture());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionTexture());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, water.getDuDvTexture());
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, water.getNormalMap());
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionDepthTexture());

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void unbind(){
    	GL11.glDisable(GL11.GL_BLEND);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

	public WaterFrameBuffers getFBOs() {
		return fbos;
	}

    public void cleanUp() {
    	fbos.cleanUp();
    	shader.cleanUp();
    }
}
