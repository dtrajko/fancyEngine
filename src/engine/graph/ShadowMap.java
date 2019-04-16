package engine.graph;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class ShadowMap {

    public static final int SHADOW_MAP_WIDTH = 1024;
    public static final int SHADOW_MAP_HEIGHT = 1024;
    private final int depthMapFBO;
    private final TextureAtlas depthMap;

    public ShadowMap() throws Exception {

        // Create a FBO to render the depth map
        depthMapFBO = GL30.glGenFramebuffers();

        // Create the depth map texture
        depthMap = new TextureAtlas(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL11.GL_DEPTH_COMPONENT);

        // Attach the the depth map texture to the FBO
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthMapFBO);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthMap.getId(), 0);
        // Set only depth
        GL11.glDrawBuffer(GL11.GL_NONE);
        GL11.glReadBuffer(GL11.GL_NONE);

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Could not create FrameBuffer");
        }

        // Unbind
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public TextureAtlas getDepthMapTexture() {
        return depthMap;
    }

    public int getDepthMapFBO() {
        return depthMapFBO;
    }

    public void cleanup() {
    	GL30.glDeleteFramebuffers(depthMapFBO);
        depthMap.cleanup();
    }
}
