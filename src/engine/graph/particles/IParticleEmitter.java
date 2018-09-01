package engine.graph.particles;

import java.util.List;

import engine.items.GameItem;

public interface IParticleEmitter {

    void cleanUp();
    
    Particle getBaseParticle();
    
    List<GameItem> getParticles();
}
