package juegito.particles;

import java.awt.Color;
import juegito.gfx.Screen;

/**
 *
 * @author joshsellers
 */
public class ActiveParticle {
    
    protected int x;
    protected int y;
    
    protected int dir;
    
    private int timer;
    
    protected int color;
    
    public boolean active;
    
    protected Particle type;
    
    public ActiveParticle(int x, int y, int dir, int color, Particle type) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.color = color;
        this.type = type;
        
        active = true;
    }
    
    public void tick() {
        if (timer < type.lifetime) timer++;
        else terminate();
        if (dir == 0) y--;
        if (dir == 1) y++;
        if (dir == 2) x--;
        if (dir == 3) x++;
    }
    
    public void render(Screen s) {
        if (type.id > 9) s.render(x, y, type.tileID, 0, color, 1);
        else s.renderNumber(type.id, x, y, new Color(color));
    }
    
    protected void terminate() {
        active = false;
    }
}
