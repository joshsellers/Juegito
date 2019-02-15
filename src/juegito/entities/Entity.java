package juegito.entities;

import com.amp.pre.Debug;
import juegito.core.collision.BoundingBox;
import juegito.gfx.Screen;
import juegito.level.Level;
import juegito.level.tiles.Tile;

/**
 *
 * @author joshsellers
 */
public abstract class Entity {
    
    public Level l;
        
    protected String ID;
    protected int width, height;
    
    public int x, y;
    
    protected boolean active = true;
    
    protected BoundingBox bounds;
    
    protected char saveID;
    
    protected int currentTileID = -1;
    
    protected int tickCount;
    
    public Entity(int x, int y, int width, int height, char saveID, String ID, Level l) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        
        this.saveID = saveID;
        
        this.l = l;
        
        this.ID = String.valueOf(ID + (x * y + width - height));        
        
        bounds = new BoundingBox(x, y, width, height);
    }
    
    public void superTick() {
        bounds.x = x;
        bounds.y = y;
        tick();
        tickCount++;
    }
    
    public void superRender(Screen s) {
        render(s);
        waterReflect(s);
    }
    
    public abstract void tick();
    public abstract void render(Screen s);
    
    protected void waterReflect(Screen s) {
        if (l.getTile(x >> Screen.SHIFT, (y >> Screen.SHIFT) + 1, false) == Tile.WATER && currentTileID != -1) {
            s.render(x, y + Screen.TILE_SIZE, currentTileID, 0x02, 0x108DB1, 1);
        } else if (l.getTile(x >> Screen.SHIFT, (y >> Screen.SHIFT) + 1, false) == Tile.WATER && currentTileID == -1) {
            l.getDebug().printMessage(Debug.DebugType.ERROR, ID, "Current tile ID is undefined; cannot render reflection.", 1);
        }
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean getActive() {
        return active;
    }
    
    public BoundingBox getBounds() {
        return bounds;
    }
    
    public String getID() {
        return ID;
    }
    
    public char getSaveID() {
        return saveID;
    }
    
    public abstract char[] getSaveInfo();
}
