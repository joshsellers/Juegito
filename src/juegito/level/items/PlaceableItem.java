package juegito.level.items;

import com.amp.pre.Debug;
import juegito.entities.Entity;
import juegito.entities.Mob;
import juegito.gfx.Screen;
import juegito.level.Level;
import juegito.level.tiles.Tile;

/**
 *
 * @author joshsellers
 */
public class PlaceableItem extends Item {
    
    private int xa[];
    private int ya[];
    
    private int distance;
    
    public PlaceableItem(int ID, boolean isCraftable, int x, int y, int xa[], int ya[], int weight, int distance, String name, int[][] recipe) {
        super(ID, x, y, isCraftable, 256, 0, distance, weight, Item.WEAPON, Item.TYPE_PLACEABLE, 0, name, recipe);
        if (xa.length != 2 || ya.length != 2) {
            throw new RuntimeException("Incorrect coordinate format. Requires {x0, x1}, {y0, y1}");
        }
        this.xa = xa;
        this.ya = ya;
        this.distance = distance;
    }
    
    @Override
    public void render(Screen s, Mob source, int dir) {
        
    }
    
    @Override
    public void use(Mob source, Level l) {
        //Check that tile in front of source is grass or other acceptable tile
        //then replace necessary tiles
        //wait or nah
        //i was gonna make them entities not tiles but it still needs to check
        //if terrain is acceptable
        
        //so i gotta decide if a placeditem should be its own class extended from entity
        //or if i should just define an object that extends entity when the item is placed
        PlaceableItem ref = this;
        
        int lx = 0; 
        int ly = 0;
        switch (source.movingDir) {
            case 0:
                lx = source.x;
                ly = ((source.y >> Screen.SHIFT) - this.distance - 1) << Screen.SHIFT;
                break;
            case 1:
                lx = source.x;
                ly = ((source.y >> Screen.SHIFT) + this.distance) << Screen.SHIFT;
                break;
            case 2:
                lx = ((source.x >> Screen.SHIFT) - this.distance) << Screen.SHIFT;
                ly = source.y;
                break;
            case 3:
                lx = ((source.x >> Screen.SHIFT) + this.distance) << Screen.SHIFT;
                ly = source.y;
                break;
        }
        
        Entity e = new Entity(lx, ly, ref.xa[1] - ref.xa[0], ref.ya[1] - ref.ya[0], (char) 0xBD, ref.name + ref.ID, l) {
            @Override
            public void tick() {

            }

            @Override
            public void render(Screen s) {
                s.render(x, y, ref.tileX + ref.tileY * (Screen.TILE_SHEET_SIZE / Screen.TILE_SIZE));
            }

            @Override
            public char[] getSaveInfo() {
                return ("PLACEDITEM" + this.ID + ref.name).toCharArray();
            }
        };
        
        l.addEntity(e);
        
        int x0 = 0;
        int y0 = 0;
        //source.l.tiles[((source.x >> Screen.SHIFT) + x0) + ((source.y >> Screen.SHIFT) + y0) * source.l.width] = ID;
        
        source.getEquippedWeapon().take(1);
    }
    
    public int[] getXa() {
        return xa;
    }
    
    public int[] getYa() {
        return ya;
    }
    
}
