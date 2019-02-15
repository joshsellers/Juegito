package juegito.entities;

import com.amp.pre.Debug;
import juegito.gfx.Screen;
import juegito.level.Level;
import juegito.level.items.Item;

/**
 *
 * @author joshsellers
 */
public class DroppedItem extends Entity {
    
    private Debug d;
    
    private Item item;
    
    public int abundance;
    
    private int originOffset;
    private int hoverPoint;
    private boolean dir;
    private int anim;
    
    private int gy;
    
    public DroppedItem(int x, int y, int originOffset, int usage, boolean doesDecay, Level l, Debug d, Item item) {
        super(x, y, 16, 16, (char)0x71, "DI" + item.getID(), l);
        this.d = d;
        
        this.item = item;
        this.abundance = usage;
                
        hoverPoint = y - 5;
        
        gy = y;
        
        this.originOffset = originOffset;
        this.y = gy - originOffset;
        if (this.y < hoverPoint) {
            dir = false;
        } else if (this.y > gy) {
            dir = true;
        }
    }

    @Override
    public void tick() {
        for (Mob m : l.getMobs()) {
            if (m.active) {
                if (m.getBounds().intersects(bounds)) {
                    m.addItem(item, abundance);
                    active = false;
                    break;
                }
            }
        }
        
        anim += 1;
        if ((anim & 1) == 0) {
            if (!dir) {
                if (y > hoverPoint) {
                    y--;
                } else {
                    dir = true;
                }
            } else {
                if (y < gy) {
                    y++;
                } else {
                    dir = false;
                }
            }
        }
    }

    @Override
    public void render(Screen s) {
        s.render(x, y, item.getTileID(), 0x00, 0, 1);
    }
    
    @Override
    public char[] getSaveInfo() {
        return new char[] {(char)(abundance << 4), (char)(item.getID() << 4)};
    }
}
