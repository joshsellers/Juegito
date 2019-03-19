package juegito.entities;

import com.amp.audio.WAVSound;
import com.amp.mathem.Statc;
import com.amp.pre.Debug;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import juegito.core.InventoryListener;
import juegito.core.collision.BoundingBox;
import juegito.gfx.Screen;
import juegito.level.Level;
import juegito.level.Player;
import juegito.level.items.Item;
import juegito.level.items.StoredItem;
import juegito.level.tiles.Tile;
import juegito.particles.Particle;

/**
 *
 * @author joshsellers
 */
public abstract class Mob extends Entity implements Comparable<Mob> {    
    
    protected List<InventoryListener> invListeners = new ArrayList<>();
    
    protected int maxHP;
    protected int HP;
    
    protected int manaRestorationTime;
    public int manaRestorationIncrement = 1;
    protected int mana;
    public int baseMana;
    
    protected int actLevel;
    protected int level;
    protected int xp;
    protected int totalxp;
    protected int xtlu;
    
    protected int gx, gy;
    
    protected int speed = 1;
    
    public int movingDir;
    protected int numSteps;
    
    protected boolean leveledUp;
    
    protected boolean moving;
    protected boolean canMove = true;
    public boolean useEquipped = false;
    public boolean renderEquipped;
    private int renderTimer;
    
    private int manaRestoreTimer;
    
    protected int invWeightLimit;
    public final static int LEVEL_XP_SCALE = 250;
    
    protected List<StoredItem> inventory = new ArrayList<>();
    protected int weightLimit = 60;
    
    protected WAVSound step;
    
    private Item mrItem;
    
    public int treeDamage;
    
    public boolean manaFortune = false;
    
    public Mob(int x, int y, int width, int height, int maxHP, int manaBase, int level, char saveID, String ID, Level l) {
        super(x, y, width, height, saveID, ID, l);
        
        xtlu = level * LEVEL_XP_SCALE;
        
        this.level = level;
        
        this.maxHP = maxHP;
        HP = maxHP;
        
        this.baseMana = manaBase;
        mana = baseMana;
        
        gx = x;
        gy = y;
        
        try {
            step = new WAVSound(juegito.core.Main.class.getResource("res/audio/step.wav"));
        } catch (MalformedURLException ex) {
            Logger.getLogger(Mob.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Mob.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void superTick() {
        if (renderEquipped && renderTimer < 17) renderTimer++; 
        else {
            renderEquipped = false;
            renderTimer = 0;
        }
        
        if (mana < baseMana) {
            manaRestoreTimer++;
            if (manaRestoreTimer == manaRestorationTime) {
                mana += manaRestorationIncrement;
                manaRestoreTimer = 0;
            }
        } else if (mana == baseMana) {
            manaRestorationTime = baseMana / 6 + 2;
        }
        
        bounds.x = x;
        bounds.y = y;
        
        handleInventory();
        tick();
        tickCount++;
        
        renderTimer++;
    }
    
    protected abstract void die(Mob source);
    
    protected void useEquipped() {
        useEquipped = true;
        renderEquipped = true;
    }
    
    public void handleInventory() {
        for (Iterator<StoredItem> iter = getInventory().listIterator(); iter.hasNext();) {
            if (iter.next().isEmpty()) {
                iter.remove();
                getInventoryListeners().stream().forEach((il) -> {il.inventorySizeChanged();});
            }
        }
    }
    
    protected void move(int xa, int ya) {
        if (xa != 0 && ya != 0) {
            move(xa, 0);
            move(0, ya);
            numSteps--;
            return;
        } else if (xa == 0 && ya == 0) {
            moving = false;
        }
        numSteps++;
        if (ya < 0) {
            movingDir = 0;
        }
        if (ya > 0) {
            movingDir = 1;
        }
        if (xa < 0) {
            movingDir = 2;
        }
        if (xa > 0) {
            movingDir = 3;
        }
        if (!colliding(gx, gy)) {
            x += xa * speed * 1;
            y += ya * speed * 1;
        } else {
            if ((x >> Screen.SHIFT) << Screen.SHIFT == x && (y >> Screen.SHIFT) << Screen.SHIFT == y) {
                gx = x;
                gy = y;
            }
        }
    }
    
    @Override
    protected void waterReflect(Screen s) {
        boolean nearTile = l.getTile(x >> Screen.SHIFT, (y >> Screen.SHIFT) + 1, false) == Tile.WATER;
        boolean farTile = l.getTile(x >> Screen.SHIFT, (y >> Screen.SHIFT) + 2, false) == Tile.WATER && moving && movingDir == 1;
        if ((nearTile || farTile) && currentTileID != -1) {
            int tileY = (y >> Screen.SHIFT) + 2;
            if (nearTile) tileY = (y >> Screen.SHIFT) + 1;
            int yOffset = 0;
            if (!this.getMoving()) yOffset = ((tickCount >> 5) & 1); 
            s.render(x, y + Screen.TILE_SIZE + (tileY - (y >> Screen.SHIFT)) - yOffset, currentTileID, 0x02, 0x108DB1 ^ 0x222222, 1);
        } else if ((nearTile || farTile) && currentTileID == -1) {
            l.getDebug().printMessage(Debug.DebugType.ERROR, ID, "Current tile ID is undefined; cannot render reflection.", 1);
        }
    }
    
    protected boolean colliding(int newX, int newY) {
        BoundingBox tempBounds = new BoundingBox(newX, newY, width, height);
        return l.getMobs().stream().anyMatch((oe) -> (oe.active && oe.getBounds().intersects(tempBounds) && oe != this));
    }
    
    public StoredItem getEquippedWeapon() {
        for (StoredItem si : getInventory()) {
            if (si.equippedAs == Item.WEAPON) return si;
        }
        return new StoredItem(Item.NULL, 0, 0);
    }
    
    public synchronized void addItem(Item i, int abundance) {
        mrItem = i;
        if (this instanceof Player) {
            String str = "Given " + i.getName();
            if (abundance > 1) str += " (" + abundance + ")";
            l.getDebug().printPlainMessage(str, 5);
        }
        
        for (StoredItem si : getInventory()) {
            if (si.getItem() == i) {
                si.give(abundance);
                getInventoryListeners().stream().forEach((il) -> {il.itemAdded(si, abundance);});
                return;
            }
        }
        StoredItem si = new StoredItem(i, 0, abundance);
        this.getInventory().add(si);
        getInventoryListeners().stream().forEach((il) -> {il.itemAdded(si, abundance); il.inventorySizeChanged();});
    }
    
    public synchronized void dropItem(int index, int abundance) {
        StoredItem si = getInventory().get(index);
        si.take(abundance);
        handleInventory();
        
        int xa = x;
        int ya = y;
        if (movingDir == 0) ya -= 32;
        else if (movingDir == 1) ya += 32;
        else if (movingDir == 2) xa -= 32;
        else if (movingDir == 3) xa += 32;
        DroppedItem di = new DroppedItem(xa, ya, Statc.random(5, 0), abundance, false, l, l.getDebug(), si.getItem());
        l.addEntity(di);
        
        if (this instanceof Player) {
            String str = si.getItem().getName() + " ";
            if (abundance > 1) str += "(" + abundance + ") ";
            str += "removed";
            l.getDebug().printPlainMessage(str, 5);
        }
        
        getInventoryListeners().stream().forEach((il) -> {il.itemRemoved(si, abundance);});
    }
    
    public synchronized void equip(int index) {
        StoredItem si = getInventory().get(index);
        if (si.equippedAs == 0) {
            si.equippedAs = si.getItem().getEquippableAs();
            if (si.equippedAs == 0 && si.getItem().getType() == Item.TYPE_FOOD) {
                si.getItem().use(this, l);
                si.take(1);
                handleInventory();
                return;
            }
            for (StoredItem sib : getInventory()) {
                if (sib != si) {
                    if (si.equippedAs == sib.equippedAs) {
                        sib.equippedAs = 0;
                        return;
                    }
                }
            }
        } else si.equippedAs = 0;
    }
    
    public synchronized void clearInventory() {
        getInventory().stream().forEach((si) -> {
            if (this instanceof Player) {
                String str = si.getItem().getName() + " ";
                if (si.getAbundance() > 1) str += "(" + si.getAbundance() + ") ";
                str += "removed";
                l.getDebug().printPlainMessage(str, 5);
            }
            si.take(si.getAbundance());
        });
        handleInventory();
    }
    
    public synchronized List<StoredItem> getInventory() {
        return this.inventory;
    }
    
    public void expandWeightLimit(int expansion) {
        weightLimit += expansion;
    }
    
    public int getWeightLimit() {
        return weightLimit;
    }
    
    @Override
    public int compareTo(Mob another) {
        if (another.y > this.y) {
            return -1;
        } else {
            return 1;
        }
    }
    
    public int getMaxHP() {
        return maxHP;
    }
    
    public int getHP() {
        return HP;
    }
    
    public void subtractHP(int removal, Mob source) {
        HP -= removal;
        Particle.emitValue(x, y, removal, 0xFFFFFF);
        if (HP <= 0) {
            die(source);
            active = false;
        } else if (source != null) {
            attackResponse(source);
        }
    }
    
    protected abstract void attackResponse(Mob source);
    
    public void addHP(int addition) {
        HP += addition;
        if (HP <= 0) {
            die(null);
            active = false;
        }
    }
    
    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }
    
    public abstract void interact();
    
    public boolean beenNotified = false;
    public abstract void levelInitializationNotification();
    
    public void questStartNotification(byte ID) {
        
    }
    
    public void setBaseMana(int baseMana) {
        this.baseMana = baseMana;
        manaRestorationTime = baseMana / 10 / 7 + 2;
    }
    
    public int getBaseMana() {
        return baseMana;
    }
    
    public void setMana(int mana) {
        this.mana = mana;
    }
    
    public void addMana(int addition) {
        mana += addition;
    }
    
    public int getMana() {
        return mana;
    }
    
    public void setActLevel(int actLevel) {
        this.actLevel = actLevel;
    }
    
    public int getActLevel() {
        return actLevel;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void resetXTLU() {
        xtlu = level * LEVEL_XP_SCALE;
    }
    
    public int getXPToLevelUp() {
        return xtlu;
    }
    
    public void setTotalXP(int totalxp) {
        this.totalxp = totalxp;
    }
    
    public int getTotalXP() {
        return totalxp;
    }
    
    public int getXP() {
        return xp;
    }
    
    public void addXP(int xp) {
        this.xp += xp;
        this.totalxp += xp;
        
        int lastLevel = level;
        
        while (this.xp >= xtlu) {
            level++;
            this.xp = this.xp - xtlu;
            xtlu = level * LEVEL_XP_SCALE;
            maxHP += (2 * ((level - 1) / 5) + 2);
            baseMana += (((level - 1) / 6) + 1);
        }
        
        if (lastLevel < level) {
            HP = maxHP;
            mana = baseMana;
        }
    }
    
    public void loadXP(int xp) {
        this.xp += xp;
        this.totalxp += xp;
        
        while (actLevel != 0 && level < actLevel) {
            if (this.xp >= xtlu) {
                level++;
                this.xp = this.xp - xtlu;
                xtlu = level * LEVEL_XP_SCALE;
            }
        }
        actLevel = 0;
    }
    
    public boolean getMoving() {
        return moving;
    }
    
    public int getGX() {
        return gx;
    }
    
    public int getGY() {
        return gy;
    }
    
    public void setGX(int gx) {
        this.gx = gx;
    }
    
    public void setGY(int gy) {
        this.gy = gy;
    }
    
    public boolean leveledUp() {
        return leveledUp;
    }
    
    public int getNumSteps() {
        return numSteps;
    }
    
    public Item[] getEquippedWearables() {
        int slots = 10;
        int completion = 0;
        Item[] buff = new Item[slots];
        completion = this.getInventory().stream().filter((i) -> (i.equippedAs > 2 && i.equippedAs < slots)).map((i) -> {
            buff[i.equippedAs-3] = i.getItem();
            return i;
        }).map((_item) -> 1).reduce(completion, Integer::sum);
        
        if (completion > 0) return buff;
        return null;
    }
    
    public synchronized void addInventoryListener(InventoryListener i) {
        this.getInventoryListeners().add(i);
    }
    
    public synchronized void removeInventoryListener(InventoryListener i) {
        this.getInventoryListeners().remove(i);
    }
    
    public synchronized List<InventoryListener> getInventoryListeners() {
        return this.invListeners;
    }
    
    public Item getMostRecentItem() {
        return mrItem;
    }
    
    public int getTickCount() {
        return tickCount;
    }
}
