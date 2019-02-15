package juegito.entities;

import juegito.gfx.Screen;
import juegito.level.Level;
import juegito.level.items.Item;

/**
 *
 * @author joshsellers
 */
public class FireBall extends Entity {
    
    private Mob source;
    
    private int dist;
    private int dir;
    private int speed = 10;
    private int damage = Item.STAFF_FLAMES.getDamage();

    private int gx;
    private int gy;
    
    private int frameCount;

    public FireBall(int x, int y, int dir, int sourcelevel, Mob source, Level l) {
        super(x, y, 11, 6, (char)0x71, "FIREBALL", l);
        
        this.source = source;
        
        this.dir = dir;
        
        if (dir < 2) {
            bounds.width = height;
            bounds.height = width;
        }
        
        gx = this.x;
        gy = this.y;
        
        dist = 100 + 13 * (sourcelevel);
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
        int xTile = 16 + ((frameCount >> 2) % 3);
        int xOffset = x;
        int yOffset = y;
        if (dir < 2) {
            s.render(xOffset, yOffset, (xTile + 3) + 29 * (Screen.TILE_SHEET_SIZE / Screen.TILE_SIZE), dir * 2, 0, 1);
        } else {
            s.render(xOffset, yOffset, xTile + 29 * (Screen.TILE_SHEET_SIZE / Screen.TILE_SIZE), 3 - dir, 0, 1);
        }
        
        frameCount++;
    }

    @Override
    public char[] getSaveInfo() {
        return "FIREBALL".toCharArray();
    }
    
}
