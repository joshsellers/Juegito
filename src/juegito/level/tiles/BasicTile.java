package juegito.level.tiles;

import juegito.gfx.Screen;
import juegito.level.Level;

/**
 *
 * @author joshsellers
 */
public class BasicTile extends Tile {
    
    protected int tileID;
    protected int flip = 0x00;

    public BasicTile(int ID, int x, int y, int color, boolean solid, int flip, boolean top) {
        super(ID, color, solid, top);
        this.tileID = x + y * (Screen.TILE_SHEET_SIZE/Screen.TILE_SIZE);
        this.flip = flip;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y, int hue) {
        screen.render(x, y, tileID, flip, hue, 1);
    }

    @Override
    public void tick() {}
    
}
