package juegito.core;

/**
 * An <code>InventoryListener</code> responds to a change in the inventory
 * of a <code>Mob</code>
 * 
 * @author joshsellers
 */
public interface InventoryListener {
    
    /**
     * Called when the length of a mob's inventory has been changed due to
     * a stack being added or completely emptied
     */
    public void inventorySizeChanged();
    
    /**
     * Called when the <code>abundance</code> of a <code>StoredItem</code>
     * has been incremented
     * @param item <code>StoredItem</code> object that has been modified
     * @param abundance amount added to the stack
     */
    public void itemAdded(juegito.level.items.StoredItem item, int abundance);
    
    /**
     * Called when the <code>abundance</code> of a <code>StoredItem</code>
     * has been decremented
     * @param item <code>StoredItem</code> object that has been modified
     * @param abundance amount removed from the stack
     */
    public void itemRemoved(juegito.level.items.StoredItem item, int abundance);
}
