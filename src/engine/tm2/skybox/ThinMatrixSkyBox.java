package engine.tm2.skybox;

import engine.tm2.opengl.Vao;
import engine.tm2.textures.Texture;

public class ThinMatrixSkyBox implements ISkyBox {
	
	public static final String SKYBOX_FOLDER = "skybox2";
	public static final String[] SKYBOX_TEX_FILES = { "sRight.png", "sLeft.png", "sUp.png", "sDown.png", "sBack.png", "sFront.png" };
	public static final float SKYBOX_SIZE = 500;

	private Vao cube;
	private Texture texture;
	
	public ThinMatrixSkyBox(Texture cubeMapTexture){
		cube = CubeGenerator.generateCube(SKYBOX_SIZE);
		this.texture = cubeMapTexture;
	}
	
	public Vao getCubeVao(){
		return cube;
	}
	
	public Texture getTexture(){
		return texture;
	}
	
	public void delete(){
		cube.delete();
		texture.delete();
	}

	public static String[] getSkyboxTexFiles(String string) {
		String[] files = new String[SKYBOX_TEX_FILES.length];
		for (int i = 0; i < files.length; i++) {
			files[i] = string + "/" + SKYBOX_TEX_FILES[i];
		}
		return files;
	}
}
