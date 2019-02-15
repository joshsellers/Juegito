package juegito.core;

import com.amp.pre.Debug;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import juegito.entities.Entity;
import juegito.entities.Mob;
import juegito.level.Level;
import juegito.particles.ActiveParticle;
import juegito.particles.Particle;

/**
 *
 * @author joshsellers
 */
public class HouseKeeping implements Runnable {

    public Level l;
    private Main m;
    private Debug d;
    
    private Timer t;
    
    private Thread thread;
    
    public HouseKeeping(Main m, Debug d) {
        this.m = m;
        this.d = d;
    }
    
    public void start() {
        thread = new Thread(this, "HouseKeeping");
        thread.start();
    }

    @Override
    public void run() {
        t = new Timer();
        t.schedule(new Cleanup(), new Date(System.currentTimeMillis() + 1), 60000 / 2);
    }
    
    private class Cleanup extends TimerTask {

        @Override
        public void run() {
            if (d != null) {
                d = m.getDebug();
                d.printMessage(Debug.DebugType.INFO, "HouseKeeping", "Running cleanup", 5);
            }
            if (l != null) {
                clean();
            } else if (m.l != null) {
                l = m.l;
                clean();
            } else if (d != null) {
                d.printMessage(Debug.DebugType.WARNING, "HouseKeeping", "Level = null", 5);
            }
            if (d != null) {
                d.printMessage(Debug.DebugType.INFO, "HouseKeeping", "Finished running cleanup", 5);
            }
        }
        
        void clean() {
            if (!m.isPaused()) {
                for (Iterator<Entity> iter = l.getEntities().listIterator(); iter.hasNext();) {
                    if (!iter.next().getActive()) {
                        iter.remove();
                    }
                }

                for (Iterator<Mob> iter = l.getMobs().listIterator(); iter.hasNext();) {
                    if (!iter.next().getActive()) {
                        iter.remove();
                    }
                }

                for (Iterator<ActiveParticle> iter = Particle.getActiveParticles().listIterator(); iter.hasNext();) {
                    if (!iter.next().active) {
                        iter.remove();
                    }
                }
            }
            
            Main.gc(d);
        }
        
    }
}