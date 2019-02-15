package juegito.entities;

import com.amp.mathem.Statc;
import juegito.gfx.Screen;
import juegito.level.Level;
import juegito.level.items.Item;

/**
 *
 * @author joshsellers
 */
public class FlyingArrow extends Entity {
    
    private Mob source;
    
    private int dist;
    private int dir;
    private int speed = 10;
    private int damage = 5;

    private int gx;
    private int gy;
    
    public FlyingArrow(int x, int y, int dir, int type, int sourcelevel, Mob source, Level l) {
        super(x, y, 12, 3, (char)0x70, "ARROW_FLYING_" + type, l);
        
        this.source = source;
        
        this.dir = dir;
        
        if (dir < 2) {
            bounds.width = height;
            bounds.height = width;
        }
        
        gx = this.x;
        gy = this.y;
        
        dist = 100 + 10 * (sourcelevel);
        if (dir == 0) {
            gy = this.y - (dist + 16);
        } else if (dir == 1) {
            gy = this.y + dist;
        } else if (dir == 2) {
            gx = this.x - dist;
        } else {
            gx = this.x + dist;
        }
    }

    @Override
    public void tick() {
        //speed = ((int)(Math.sqrt(Math.pow(x - gx, 2) + Math.pow(y - gy, 2)))) / 5 + 1;
        speed = 4;
        
        int moving = 0;
        if (x < gx) {
            x += speed * 2;
            moving++;
        }
        if (x > gx) {
            x -= speed * 2;
            moving++;
        }
        if (y < gy) {
            y += speed * 2;
            moving++;
        }
        if (y > gy) {
            y -= speed * 2;
            moving++;
        }

        if (moving == 2) {
            gx = x;
            gy = y;
        }
        
        if (y == gy && x == gx) {
            l.addEntity(new DroppedItem(x, y, Statc.random(5, 0), 1, true, l, null, Item.ARROW_BASIC));
            active = false;
        }
        
        l.getMobs().stream().filter((m) -> (m != source && m.active && bounds.intersects(m.bounds))).map((m) -> {
            m.subtractHP(damage, source);
            return m;
        }).forEach((_item) -> {
            active = false;
        });
    }

    @Override
    public void render(Screen s) {   
        int xOffset = x;
        int yOffset = y;
        if (dir == 1) yOffset -= 4;
        if (dir == 2) xOffset -= 4;
        if (dir < 2) {
            s.render(xOffset, yOffset, 29 * (Screen.TILE_SHEET_SIZE/Screen.TILE_SIZE), dir * 2, 0, 1);
        } else {
            s.render(xOffset, yOffset, 1 + 29 * (Screen.TILE_SHEET_SIZE/Screen.TILE_SIZE), 3 - dir, 0, 1);
        }
    }
    
    @Override
    public char[] getSaveInfo() {
        return "FLYING_ARROW".toCharArray();
    }
}