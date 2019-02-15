package juegito.particles;

import com.amp.mathem.Statc;
import java.util.ArrayList;
import java.util.List;
import juegito.gfx.Screen;

/**
 * An object that cannot be collided with, and only exists for a finite period
 * of time. While it is active, it is represented as an image taken from the
 * <code>SpriteSheet</code>
 * <p>When static, this class is used to handle and render each current 
 * {@link ActiveParticle}
 * @author joshsellers
 */
public class Particle {
    
    /**
     * Used to store the different variations of particles
     */
    public static Particle[] particles = new Particle[256];
    
    public static final Particle ZERO = new Particle(0, 2, 29, 35, MovementType.FLOAT);
    public static final Particle ONE = new Particle(1, 3, 29, 35, MovementType.FLOAT);
    public static final Particle TWO = new Particle(2, 4, 29, 35, MovementType.FLOAT);
    public static final Particle THREE = new Particle(3, 5, 29, 35, MovementType.FLOAT);
    public static final Particle FOUR = new Particle(4, 6, 29, 35, MovementType.FLOAT);
    public static final Particle FIVE = new Particle(5, 7, 29, 35, MovementType.FLOAT);
    public static final Particle SIX = new Particle(6, 8, 29, 35, MovementType.FLOAT);
    public static final Particle SEVEN = new Particle(7, 9, 29, 35, MovementType.FLOAT);
    public static final Particle EIGHT = new Particle(8, 10, 29, 35, MovementType.FLOAT);
    public static final Particle NINE = new Particle(9, 11, 29, 35, MovementType.FLOAT);

    /**
     * The lifetime of this <code>Particle</code>
     */
    protected long lifetime;
        
    /**
     * The unique number associated with this <code>Particle</code>
     */
    protected int id;
    
    /**
     * The location of the tile to be rendered from the <code>SpriteSheet</code>
     */
    protected int tileID;
    
    /**
     * The identification of the way the <code>Particle</code> will be animated
     */
    protected MovementType mType;
    
    /**
     * All particles that are currently active
     */
    private static List<ActiveParticle> activeParticles = new ArrayList<>();
    
    /**
     * Creates a new generic particle type
     * @param id        unique number associated with this 
     *                  <code>Particle</code>
     * @param xTile     x coordinate of this particle's tile on the 
     *                  <code>SpriteSheet</code>
     * @param yTile     y coordinate of this particle's tile on the 
     *                  <code>SpriteSheet</code>
     * @param lifetime  lifetime of this <code>Particle</code>
     * @param mType     identification of the way the <code>Particle</code> 
     *                  will be animated
     */
    public Particle(int id, int xTile, int yTile, int lifetime, MovementType mType) {
        this.id = id;
        this.lifetime = lifetime;
        
        this.mType = mType;
        
        //Insert this particle type into the list of particle types
        particles[id] = this;
        
        //Calculate the indexed location of this particle on the SpriteSheet
        tileID = xTile + yTile * (Screen.TILE_SHEET_SIZE / Screen.TILE_SIZE);
    }
    
    /**
     * Invokes the <code>tick()</code> method in every 
     * <code>ActiveParticle</code>
     */
    public static void tick() {
        try {
            getActiveParticles().stream().filter((p) -> (p != null)).filter((p) -> (p.active)).forEach((p) -> {
                p.tick();
            });
        } catch (java.util.ConcurrentModificationException ex) {}
    }
    
    /**
     * Renders every <code>ActiveParticle</code> onto the <code>Screen</code>
     * @param s the screen on which the particles will be rendered
     */
    public static void render(Screen s) {
        try {
            getActiveParticles().stream().filter((p) -> (p != null)).filter((p) -> (p.active)).forEach((p) -> {
                p.render(s);
            });
        } catch (java.util.ConcurrentModificationException ex) {}
    }
    
    /**
     * Creates a new <code>ActiveParticle</code> representing this type of 
     * particle
     * @param x     the particle's x coordinate on the <code>Level</code>
     * @param y     the particle's y coordinate on the <code>Level</code>
     * @param color color that the new <code>ActiveParticle</code> will be 
     *              displayed as
     */
    public synchronized void emit(int x, int y, int color) {
        int dir = 0;
        if (mType == MovementType.SINK) dir = 1;
        if (mType == MovementType.SPREAD) dir = Statc.random(3, 0);
        
        getActiveParticles().add(new ActiveParticle(x, y, dir, color, this));
    }
    
    public static synchronized void emitValue(int x, int y, int value, int color) {
        String p = "0123456789";
        String r = String.valueOf(value);
        for (int i = 0; i < r.length(); i++) {
            for (int j = 0; j < p.length(); j++) {
                if (r.charAt(i) == p.charAt(j)) {
                    Particle.particles[j].emit(x + 10 * i, y - 16, color);
                    break;
                }
            }
        }
    }
    
    /**
     * Returns the current list of active particles
     * @return all active particles
     */
    public static synchronized List<ActiveParticle> getActiveParticles() {
        return activeParticles;
    }
    
    protected enum MovementType {
        FLOAT, SINK, SPREAD
    }
}
