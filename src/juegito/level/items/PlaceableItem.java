package juegito.level.items;

/**
 *
 * @author joshsellers
 */
public abstract class PlaceableItem extends Item {
    
    public PlaceableItem(int ID, boolean isCraftable, int x, int y, int xa[], int ya[], int weight, int distance, String name, int[][] recipe) {
        super(ID, x, y, isCraftable, 256, 0, distance, weight, Item.PLACEABLE, Item.TYPE_PLACEABLE, 0, name, recipe);
    }
    
}
