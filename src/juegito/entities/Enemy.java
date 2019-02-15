package juegito.entities;

import juegito.level.Level;

/**
 *
 * @author joshsellers
 */
public abstract class Enemy extends Mob {
    
    protected Mob target;
    
    protected int damage;

    public Enemy(int x, int y, int width, int height, int maxHP, int level, int damage, int manaBase, char saveID, String ID, Level l, Mob target) {
        super(x, y, width, height, maxHP, manaBase, level, saveID, ID, l);
        
        this.target = target;
        this.damage = damage;
    }
    
    public Mob getTarget() {
        return target;
    }
    
    public void setTartget(Mob m) {
        this.target = m;
    }
    
    public int getDamage() {
        return damage;
    }
}
