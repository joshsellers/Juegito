package juegito.level.items;

/**
 * A representation of an {@link Item} stored in the inventory of a 
 * <code>Mob</code>
 * <p>Instances of this class should be used to simplify the storage of Items
 * and their abundances, positions in the inventory, statuses as equipment, and
 * the ID of the <code>Mob</code> that they belong to
 * @author joshsellers
 */
public class StoredItem {
    
    /** 
     * The <code>Item</code> that this <code>StoredItem</code> represents
     */ 
    protected Item i;
    
    /**
     * The type of <code>Item</code> that this <code>StoredItem</code> is
     * equipped as
     */
    public int equippedAs;
    
    /**
     * The amount of items in this stack of items
     */
    private int abundance;
    
    /**
     * Whether or not there are any items in this stack
     */
    private boolean empty;

    /**
     * Constructs a new <code>StoredItem</code>
     * @param i          the <code>Item</code> that this <code>StoredItem</code> 
     *                   represents 
     * @param equippedAs type of <code>Item</code> that this
     *                   <code>StoredItem</code> is equipped as
     * @param abundance  The amount of items in this stack of items
     * @see Item
     * @see Item.NOT_EQUIPPED
     * @see Item.WEAPON
     * @see Item.ARMOR
     * @see Item.CURRENCY
     */
    public StoredItem(Item i, int equippedAs, int abundance) {
        this.i = i;
        this.equippedAs = equippedAs;
        this.abundance = abundance;
        if (abundance == 0) empty = true;
        
        /** 
         * Check that that an apple isn't trying to be a
         * weapon or something
         */ 
        if (equippedAs != i.getEquippableAs() && equippedAs != 0) {
            throw new RuntimeException("Equip option does not match bounds");
        }
    }
    
    /**
     * Increases the amount of items in this stack of items
     * @param abundance the amount to increment by
     */
    public void give(int abundance) {
        if (abundance > 0) this.abundance += abundance;
    }
    
    /**
     * Decreases the amount of items in this stack of items
     * @param abundance the amount to decrement by
     */
    public void take(int abundance) {
        if (abundance > 0) this.abundance -= abundance;
        if (this.abundance <= 0) empty = true;
    }
    
    /**
     * Returns the amount of items in this stack of items
     * @return amount of items in this stack of items
     */
    public int getAbundance() {
        return abundance;
    }
    
    /**
     * Returns true if there are no items in this stack
     * @return true if there are no items in this stack
     */
    public boolean isEmpty() {
        return empty;
    }
    
    /**
     * Returns the <code>Item</code> object that this <code>StoredItem</code>
     * represents
     * @return the <code>Item</code> that this <code>StoredItem</code>
     * represents
     */
    public Item getItem() {
        return i;
    }
}
