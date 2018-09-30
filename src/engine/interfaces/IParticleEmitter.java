package engine.interfaces;

import java.util.List;

import engine.graph.particles.Particle;
import engine.items.GameItem;

public interface IParticleEmitter {

    void cleanUp();
    
    Particle getBaseParticle();
    
    List<GameItem> getParticles();
}
