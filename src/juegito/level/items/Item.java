package juegito.level.items;

import com.amp.audio.WAVSound;
import com.amp.mathem.Statc;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import juegito.core.Main;
import juegito.core.collision.BoundingBox;
import juegito.entities.DroppedItem;
import juegito.entities.FireBall;
import juegito.entities.FlyingArrow;
import juegito.entities.Mob;
import juegito.gfx.Screen;
import juegito.gfx.SpriteSheet;
import juegito.level.Level;
import juegito.level.Player;
import juegito.level.tiles.Tile;
import juegito.particles.Particle;

/**
 *
 * @author joshsellers
 */
public class Item {
    
    public static final int TOP_ID = 28;
    
    public static final Item[] items = new Item[TOP_ID]; //ID   tileX tileY craftable  damage range weight   type
    public static final Item NULL             = new Item( 0,     0,    0,    false,  0,     0,   0,   0,   0,  0,  0,   "_",                new int[][] {{0, 0}});
    public static final Item AXE              = new Item( 1,     4,    30,   true,   128,   20,  4,   10,  1,  1,  0,   "Axe",              new int[][] {{21, 2}, {20, 3}});
    public static final Item SWORD_BROAD      = new Item( 2,     5,    30,   true,   128,   25,  4,   9,   1,  1,  0,   "Broad sword",      new int[][] {{0, 0}});
    public static final Item BOW_             = new Item( 3,     4,    31,   true,   128,   10,  0,   5,   1,  1,  0,   "Bow",              new int[][] {{0, 0}});
    public static final Item ARROW_BASIC      = new Item( 4,     5,    31,   true,   40,    1,   0,   0,   2,  4,  0,   "Arrow",            new int[][] {{21, 1}, {20, 1}});
    public static final Item WARHAMMER        = new Item( 5,     6,    30,   true,   128,   28,  2,   12,  1,  1,  0,   "War hammer",       new int[][] {{0, 0}});
    public static final Item SWORD_SHORT      = new Item( 6,     7,    30,   true,   128,   22,  2,   8,   1,  1,  0,   "Short sword",      new int[][] {{0, 0}});
    public static final Item STAFF_           = new Item( 7,     6,    31,   true,   128,   10,  32,  4,   1,  1,  5,   "Staff",            new int[][] {{20, 3}});
    public static final Item DAGGER_          = new Item( 8,     7,    31,   true,   128,   15,  2,   2,   1,  1,  0,   "Dagger",           new int[][] {{21, 2}, {20, 1}});
    public static final Item APPLE            = new Item( 9,     8,    30,   false,  64,    0,   0,   1,   0,  3,  0,   "Apple",            new int[][] {{0, 0}});
    public static final Item GOLD             = new Item( 10,    8,    31,   false,  64,    0,   0,   0,   0,  0,  0,   "Gold",             new int[][] {{0, 0}});
    public static final Item HELMET_IRON      = new Item( 11,    0,    31,   true,   0,     0,   0,   13,  3,  2,  0,   "Iron Helmet",      new int[][] {{0, 0}});
    public static final Item BREASTPLATE_IRON = new Item( 12,    1,    31,   true,   0,     0,   0,   16,  4,  2,  0,   "Iron Breastplate", new int[][] {{0, 0}});
    public static final Item GAUNTLET_IRON    = new Item( 13,    2,    31,   true,   0,     0,   0,   5,   5,  2,  0,   "Iron Gauntlets",   new int[][] {{0, 0}});
    public static final Item BOOTS_IRON       = new Item( 14,    3,    31,   true,   0,     0,   0,   6,   6,  2,  0,   "Iron Boots",       new int[][] {{0, 0}});
    public static final Item STAFF_FLAMES     = new Item( 15,    3,    30,   true,   128,   15,  10,  5,   1,  1,  8,   "Staff of Flames",  new int[][] {{26, 2}, {20, 5}});
    public static final Item MANABISCUIT      = new Item( 16,    9,    30,   true,   64,    0,   0,   1,   0,  3,  -2,  "Arcana Buscuit",   new int[][] {{0, 0}});
    public static final Item MANACAKE         = new Item( 17,    9,    31,   true,   64,    0,   0,   1,   0,  3,  -10, "Arcana Cake",      new int[][] {{0, 0}});
    public static final Item KUULA            = new Item( 18,    8,    32,   false,  64,    0,   0,   1,   0,  0,  0,   "Kuula" ,           new int[][] {{0, 0}});
    public static final Item JEANS            = new Item( 19,    0,    32,   true,   0,     0,   0,   3,   9,  5,  0,   "Jeans",            new int[][] {{0, 0}});
    public static final Item WOOD             = new Item( 20,    10,   30,   false,  0,     3,   0,   5,   1,  6,  0,   "Wood",             new int[][] {{0, 0}});
    public static final Item ROCK             = new Item( 21,    2,    31,   false,  0,     2,   1,   2,   1,  6,  0,   "Rock",             new int[][] {{0, 0}});
    public static final Item PICKAXE          = new Item( 22,    3,    31,   true,   0,     18,  3,   8,   1,  1,  0,   "Pickaxe",          new int[][] {{21, 3}, {20, 3}});
    public static final Item BULLET_BASIC     = new Item( 23,    10,   31,  true,   128,   1,   1,   1,   2,  4,  0,    "Bullet",       new int[][] {{0, 0}});
    
    public static final Item CAMPFIRE         = new PlaceableItem(24, true, 0, 0, new int[] {0, 0}, new int[] {0, 0}, 12, 2, "Campfire", new int[][] {{Item.WOOD.getID(), 8}});
    public static final Item WOOD_FLOOR       = new PlaceableItem(25, true, 20, 0, new int[] {20, 0}, new int[] {1, 1}, 8, 2, "Wooden Floor", new int[][] {{Item.WOOD.getID(), 50}});
    public static final Item FIRE_BULB        = new Item( 26,    10,   2,   false,   0,     0,   1,   1,0,  6,  0,         "Flame Bulb", new int[][] {{}});
    public static final Item WOOD_WALL       = new PlaceableItem(27, true, 17, 0, new int[] {17, 0}, new int[] {3, 3}, 12, 4, "Wooden Wall", new int[][] {{Item.WOOD.getID(), 100}});
    
    public static final int NOT_EQUIPPED = 0;
    public static final int WEAPON = 1;
    public static final int AMMUNITION = 2;
    public static final int ARMOR_HEAD = 3;
    public static final int ARMOR_CHEST = 4;
    public static final int ARMOR_HANDS = 5;
    public static final int ARMOR_LOWER = 6;
    public static final int CLOTHES_HEAD = 7;
    public static final int CLOTHES_TORSO = 8;
    public static final int CLOTHES_LEGS = 9;
    public static final int CLOTHES_FEET = 10;
    public static final int CLOTHES_HANDS = 11;
    public static final int CLOTHES_HAIR = 12;
    public static final int PLACEABLE = 13;
    
    public static final int TYPE_NA = 0;
    public static final int TYPE_WEAPON = 1;
    public static final int TYPE_ARMOR = 2;
    public static final int TYPE_FOOD = 3;
    public static final int TYPE_AMMO = 4;
    public static final int TYPE_CLOTHES = 5;
    public static final int TYPE_MATERIAL = 6;
    public static final int TYPE_PLACEABLE = 7;
    
    protected int ID;
    protected int tileID;
    protected int tileX;
    protected int tileY;
    
    protected BufferedImage icon;
    
    protected boolean isCraftable;
    protected int maxusage;
    protected int damage;
    protected int weight;
    protected int range;
    protected int maxRange;
    protected int equippableAs;
    protected int type;
    protected int manaConsumption;
    protected int[][] recipe;
    
    protected String name;
    
    private WAVSound[] treeSounds = new WAVSound[5];
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Item(int ID, int x, int y, boolean isCraftable, int maxusage, int damage, int range, int weight, int equippableAs, int type, int manaConsumption, String name, int[][] recipe) {
        this.ID = (byte) ID;
        tileX = x;
        tileY = y;
        this.tileID = x + y * (Screen.TILE_SHEET_SIZE / Screen.TILE_SIZE);
        if (items[ID] != null) {
            throw new RuntimeException("Duplicate item ID: " + ID);
        }
        
        this.isCraftable = isCraftable;
        this.maxusage = maxusage;
        this.damage = damage;
        this.weight = weight;
        this.range = range;
        this.name = name;
        this.equippableAs = equippableAs;
        this.type = type;
        this.manaConsumption = manaConsumption;
        this.recipe = recipe;
        
        maxRange = range * 10;
                
        items[ID] = this;
        
        try {
            treeSounds[0] = new WAVSound(Main.class.getResource("res/audio/choppin/chopping_0.wav"));
            treeSounds[1] = new WAVSound(Main.class.getResource("res/audio/choppin/chopping_1.wav"));
            treeSounds[2] = new WAVSound(Main.class.getResource("res/audio/choppin/chopping_2.wav"));
            treeSounds[3] = new WAVSound(Main.class.getResource("res/audio/choppin/treefalling_0.wav"));
            treeSounds[4] = new WAVSound(Main.class.getResource("res/audio/choppin/treefalling_1.wav"));
        } catch (Exception ex) {
            Logger.getLogger(Item.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        loadIcon();
    }
    
    public BufferedImage getIcon() {
        icon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        SpriteSheet sheet = new SpriteSheet(null);
        for (int ya = tileY << Screen.SHIFT; (ya - (tileY << Screen.SHIFT)) < icon.getHeight() && ya < sheet.height; ya++) {
            for (int xa = tileX << Screen.SHIFT; (xa - (tileX << Screen.SHIFT)) < icon.getWidth() && xa < sheet.width; xa++) {
                if (sheet.pixels[xa + ya * sheet.width] != 0xFF000001) {
                    icon.setRGB((xa - (tileX << Screen.SHIFT)), (ya - (tileY << Screen.SHIFT)), sheet.pixels[xa + ya * sheet.width]);
                } else {
                    icon.setRGB((xa - (tileX << Screen.SHIFT)), (ya - (tileY << Screen.SHIFT)), 0);
                }
            }
        }
        return icon;
    }
    
    public void render(Screen s, Mob source, int dir) {
        if (this != Item.NULL) {
            int x = source.x;
            int y = source.y;
            int yTile = this.tileY - 5;
            int flip = 0x00;
            if (dir < 2) {
                x += 0;
                y -= 20;
                yTile -= 2;
                if (dir == 1) {
                    x -= 0;
                    y += 24;
                    flip = 0x02;
                }
            } else {
                x += 14;
                y -= 8;
                if (dir == 2) {
                    x -= 27;
                    flip = 0x01;
                }
            }
            
            if (this.equals(BOW_) && source.movingDir == 1) y -= 8;
            else if (this.equals(BOW_) && source.movingDir == 0) y += 8;
            s.render(x, y, tileX + yTile * (Screen.TILE_SHEET_SIZE / Screen.TILE_SIZE), flip, 0, 1);
        } 
    }
    
    private boolean timerStarted = false;
    public void use(Mob source, Level l) {
        if (source.getMana() - manaConsumption >= 0 && source.getMana() - manaConsumption <= source.getBaseMana()) {
            source.addMana(-manaConsumption);

            if (ID == Item.NULL.getID()) {
                int xOffset = 0;
                int yOffset = 0;
                switch (source.movingDir) {
                    case 0:
                        yOffset = -1;
                        break;
                    case 1: 
                        yOffset = 1;
                        break;
                    case 2:
                        xOffset = -1;
                        break;
                    case 3:
                        xOffset = 1;
                        break;
                }
                
                int curTile = l.getTile((source.x >> Screen.SHIFT) + xOffset, (source.y >> Screen.SHIFT) + yOffset, false).getID();
                if (curTile == Tile.ROCK_0.getID() || curTile == Tile.ROCK_1.getID() || curTile == Tile.ROCK_2.getID()
                || curTile == Tile.ROCK_3.getID() || curTile == Tile.ROCK_4.getID()) {
                    source.addItem(Item.ROCK, 1);
                    l.tiles[((source.x >> Screen.SHIFT) + xOffset) + ((source.y >> Screen.SHIFT) + yOffset) * l.width] = Tile.GRASS_0.getID();
                } else if (curTile == Tile.MANAROCK_0.getID()) {
                    l.tiles[((source.x >> Screen.SHIFT) + xOffset) + ((source.y >> Screen.SHIFT) + yOffset) * l.width] = Tile.GRASS_0.getID();
                    source.setBaseMana(source.getBaseMana() + 1);
                    source.addXP(Statc.intRandom(0, 3));
                    if (source instanceof Player) {
                        ((Player) source).l.getDebug().printPlainMessage("You have arcana fortune", 5);
                        ((Player) source).manaFortune = true;
                        Timer t = new Timer();
                        TimerTask tt = new TimerTask() {
                            @Override
                            public void run() {
                                ((Player) source).manaFortune = false;
                                ((Player) source).l.getDebug().printPlainMessage("Your arcana fortune has expired", 5);
                            }
                        };
                        t.schedule(tt, Statc.intRandom(100000, 600000));
                    }
                } else if (curTile == Tile.TOSTYPLANT_1.getID() && l.getTile((source.x >> Screen.SHIFT) + xOffset, (((source.y >> Screen.SHIFT) + yOffset - 1)), true).getID() == Tile.TOSTYPLANT_0.getID()) {
                    l.overlayTiles[(source.x >> Screen.SHIFT) + xOffset + (((source.y >> Screen.SHIFT) + yOffset) - 1) * l.width] = Tile.AIR.getID();
                    //l.tiles[((source.x >> Screen.SHIFT) + xOffset) + ((source.y >> Screen.SHIFT) + yOffset) * l.width] = Tile.GRASS_3.getID();
                    
                    source.addItem(Item.FIRE_BULB, Statc.intRandom(1, 2));
                    source.addXP(Statc.intRandom(5, 30));
                    source.setMaxHP(source.getMaxHP() + Statc.intRandom(1, 5));
                }
            } else if (ID == Item.BOW_.getID()) {
                boolean notEmpty = false;
                for (StoredItem si : source.getInventory()) {
                    if (si.equippedAs == Item.AMMUNITION) {
                        si.take(1);
                        notEmpty = true;
                        break;
                    }
                }
                if (notEmpty) {
                    l.addEntity(new FlyingArrow((source.x + source.getWidth() / 2) - 7 / 2, (source.y + source.getHeight() / 2) - 6, source.movingDir, 0, source.getLevel(), source, l));
                }
            } else if (ID == Item.STAFF_FLAMES.getID()) {
                int dir = source.movingDir;
                int x = source.x;
                int y = source.y;
                int xOffset = 16;
                int yOffset = 16;
                if (dir < 2) {
                    x += 4;
                    y -= 16;
                    xOffset = 6;
                    yOffset = -14;
                    if (dir == 1) {
                        x -= 7;
                        y += 24;
                        yOffset = 12;
                    }
                } else {
                    x += 8;
                    y -= 5;
                    yOffset = 6;
                    if (dir == 2) {
                        xOffset = -14;
                        x -= 16;
                    }
                }

                l.addEntity(new FireBall(x + xOffset, y + yOffset, source.movingDir, source.getLevel(), source, l));
            } else {
                if (this.equals(Item.AXE) || this.equals(Item.ROCK)) {
                    treeChoppin(source, l);
                }
                
                BoundingBox b = new BoundingBox(source.getBounds().x, source.getBounds().y, source.getBounds().width, source.getBounds().height);
                if (source.movingDir == 0) {
                    b.y -= range;
                }
                if (source.movingDir == 1) {
                    b.y = b.y + b.height + range;
                }
                if (source.movingDir == 2) {
                    b.x -= range;
                }
                if (source.movingDir == 3) {
                    b.x = b.x + b.width + range;
                }

                l.getMobs().stream().filter((m) -> (m != source && m.getActive())).filter((m) -> (b.intersects(m.getBounds()))).map((m) -> {
                    m.subtractHP(damage * source.getLevel() / 2, source);
                    return m;
                }).forEach((_item) -> {
                    //source.eusage--;
                });
            }
        } else if (source instanceof juegito.level.Player) {
            if (manaConsumption > 0) {
                l.getDebug().printPlainMessage("Insufficient arcana", 5);
            }
        }
    }
    
    private int lastSmack;
    private int treeHP;
    private void treeChoppin(Mob source, Level l) {
        int xOffset = 0;
        int yOffset = 0;
        switch (source.movingDir) {
            case 0:
                yOffset = -1;
                break;
            case 1:
                yOffset = 1;
                break;
            case 2:
                xOffset = -1;
                break;
            case 3:
                xOffset = 1;
                break;
        }

        boolean isSmackingTree = false;
        byte referenceTile = 0;
        for (int i = Tile.TREE_0_0.getID(); i < Tile.TREE_0_5.getID() + 1; i++) {
            isSmackingTree = (l.getTile((source.x >> Screen.SHIFT) + xOffset, (source.y >> Screen.SHIFT) + yOffset, false) == Tile.getTile((byte) i)
                    || l.getTile((source.x >>Screen.SHIFT) + xOffset, (source.y >> Screen.SHIFT) + yOffset, true) == Tile.getTile((byte) i));
            if (isSmackingTree) {
                referenceTile = (byte) i;
                break;
            }
        }

        if (isSmackingTree) {
            if (source.treeDamage == 0) {
                treeHP = Statc.intRandom(8, 18);
                if (source.manaFortune) treeHP /= 2;
                if (this.equals(Item.ROCK)) treeHP += 20;
            } else if (source.getTickCount() - lastSmack > 60) {
                source.treeDamage = 0;
            }
            treeSounds[Statc.intRandom(0, 2)].play(false);
            source.treeDamage++;
            lastSmack = source.getTickCount();
            int x = (source.x >> Screen.SHIFT) + xOffset;
            int y = (source.y >> Screen.SHIFT) + yOffset;
            
            Particle.emitValue(x << Screen.SHIFT, y << Screen.SHIFT, treeHP - source.treeDamage, new Color(0x6f4f10).brighter().getRGB());
            
            if (source.treeDamage == treeHP / 2) {
                treeSounds[3].play(false);
            }
        }

        if (source.treeDamage >= treeHP && isSmackingTree) {
            source.treeDamage = 0;         
            treeSounds[4].play(false);
            
            int x = (source.x >> Screen.SHIFT) + xOffset;
            int y = (source.y >> Screen.SHIFT) + yOffset;
            int mod = 1;
            if (source.manaFortune) mod = Statc.intRandom(2, 7);
            int deltaXp = Statc.intRandom(1, 5);
            l.addEntity(new DroppedItem(source.x, source.y, 0, Statc.intRandom(1, 3)* mod, false, l, l.getDebug(), Item.WOOD));
            if (Statc.intRandom(0, 2) == 0) {
                deltaXp += 2 * mod;
                l.addEntity(new DroppedItem(source.x, source.y, 0, Statc.intRandom(1, 2)* mod, false, l, l.getDebug(), Item.APPLE));
            }
            
            if (Statc.intRandom(0, 50 / (mod+1)) == 0) {
                source.manaRestorationIncrement += Statc.intRandom(1, 2) * mod;
                if (source.getMana() + source.manaRestorationIncrement > source.baseMana) {
                    source.baseMana += (source.getMana() + source.manaRestorationIncrement);
                }
                deltaXp += 2;
                if (Statc.intRandom(0, 75 / (mod+1)) == 0) {
                    source.baseMana += Statc.intRandom(1, 20) + mod;
                    deltaXp*=2*source.getLevel() * mod;
                    if (source instanceof Player) {
                        source.l.getDebug().printPlainMessage("Your base arcana has increased", 5);
                    }
                }
                if (source instanceof Player) {
                    source.l.getDebug().printPlainMessage("Your arcana recharge speed has increased", 5);
                }
                
            }
            source.addXP(deltaXp);

            if (referenceTile == Tile.TREE_0_1.getID()) {
                x--;
            }
            if (referenceTile == Tile.TREE_0_2.getID()) {
                y--;
            }
            if (referenceTile == Tile.TREE_0_3.getID()) {
                x--;
                y--;
            }
            if (referenceTile == Tile.TREE_0_4.getID()) {
                y -= 2;
            }
            if (referenceTile == Tile.TREE_0_5.getID()) {
                x--;
                y -= 2;
            }

            for (int ya = y; ya < y + 3; ya++) {
                for (int xa = x; xa < x + 2; xa++) {
                    if (ya < y + 2) {
                        l.overlayTiles[xa + ya * l.width] = Tile.AIR.getID();
                        if (ya > y) l.overlayTiles[xa + ya * l.width] = (byte) (Tile.TREE_1_2.getID() + xa - x);
                    } else {
                        l.tiles[xa + ya * l.width] = (byte) (Tile.TREE_1_4.getID() + xa - x);
                    }
                }
            }
        }
    }
        
    public int getID() {
        return ID;
    }
    
    public String getName() {
        return name;
    }
    
    public static Item getItem(String name) {
        for (Item item : items) {
            if (item != null) {
                if (name.equalsIgnoreCase(item.getName())) {
                    return item;
                }
            }
        }
        
        return Item.NULL;
    }
    
    public static Item getItem(int ID) {
        return items[ID];
    }
    
    public int getDamage() {
        return damage;
    }
    
    public int getRange() {
        return range;
    }
    
    public int getMaxRange() {
        return maxRange;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public int getTileID() {
        return tileID;
    }
    
    public int getEquippableAs() {
        return equippableAs;
    }
    
    public int getType() {
        return type;
    }
    
    public int getXTile() {
        return tileX;
    }
    
    public int getYTile() {
        return tileY;
    }
    
    public int getManaConsumption() {
        return manaConsumption;
    }
    
    public boolean isCraftable() {
        return isCraftable;
    }
    
    public int[][] getRecipe() {
        return recipe;
    }
}
