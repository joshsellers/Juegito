package juegito.level.items;

import com.amp.pre.Debug;
import juegito.entities.Entity;
import juegito.entities.Mob;
import juegito.gfx.Screen;
import juegito.level.Level;
import juegito.level.Player;
import juegito.level.tiles.Tile;
import juegito.ui.Seshat;

/**
 *
 * @author joshsellers
 */
public class PlaceableItem extends Item {
    
    private int coordinates[];
    private int size[];
    
    private int distance;
    
    public PlaceableItem(int ID, boolean isCraftable, int x, int y, int xa[], int ya[], int weight, int distance, String name, int[][] recipe) {
        super(ID, x, y, isCraftable, 256, 0, distance, weight, Item.WEAPON, Item.TYPE_PLACEABLE, 0, name, recipe);

        this.coordinates = xa;
        this.size = ya;
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
        
        Mob e = new Mob(lx, ly, ref.size[0], ref.size[1], 350, 0, 1, (char) 0xBD, ref.name.toLowerCase() + "placeable", l) {
            @Override
            public void tick() {
                bounds.width = ref.size[0] * 16;
                bounds.height = ref.size[1] * 16;
                bounds.x = x;
                bounds.y = y;
                
                if (ref.ID == Item.WOOD_WALL.ID) {
                    bounds.y += 16;
                    bounds.height -= 16;
                }
                
                if (ref.getID() == Item.WOOD_FLOOR.getID()) {
                    this.sortPriority = 0;
                }
            }

            @Override
            public void render(Screen s) {
                for (int i = ref.coordinates[1]; i < ref.coordinates[1] + ref.size[1]; i++) {
                    for (int j = ref.coordinates[0]; j < ref.coordinates[0] + ref.size[0]; j++) {
                        s.render(x + (j-coordinates[0]) * Screen.TILE_SIZE, y + (i-coordinates[1]) * Screen.TILE_SIZE, j + i * (Screen.TILE_SHEET_SIZE/Screen.TILE_SIZE));
                    }
                }
            }

            @Override
            public char[] getSaveInfo() {
                return ("PLACEDITEM" + this.ID + ref.name).toCharArray();
            }

            @Override
            protected void die(Mob source) {
                for (int[] recipe1 : ref.getRecipe()) {
                    source.addItem(Item.getItem(recipe1[0]), recipe1[1]);
                }
            }

            @Override
            protected void attackResponse(Mob source) {

            }

            @Override
            public void interact() {
                Seshat.display(ref.getName() + " Level " + this.getLevel() + " " + this.getHP() + " HP", 12);
            }

            @Override
            public void levelInitializationNotification() {

            }
            
            @Override
            public int compareTo(Mob another) {
                int ya = another.y;
                if (another instanceof Player) ya -= Screen.TILE_SIZE;
                if (ref.ID == Item.WOOD_FLOOR.ID || ya > this.y) {
                    return -1;
                } else {
                    return 1;
                }
            }
        };
        
        l.addMob(e);
        
        int x0 = 0;
        int y0 = 0;
        //source.l.tiles[((source.x >> Screen.SHIFT) + x0) + ((source.y >> Screen.SHIFT) + y0) * source.l.width] = ID;
        
        source.getEquippedWeapon().take(1);
    }
    
    public int[] getSheetCoordinates() {
        return coordinates;
    }
    
    public int[] getSize() {
        return size;
    }
    
}
