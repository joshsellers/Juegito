package juegito.level;

import com.amp.pre.Debug;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import juegito.entities.Entity;
import juegito.entities.Mob;
import juegito.entities.NPC;
import juegito.gfx.Screen;
import juegito.level.tiles.Tile;
import java.util.Collections;
import juegito.level.items.Item;
import juegito.ui.Seshat;

/**
 *
 * @author joshsellers
 */
public class Level {
    
    protected Debug d;
    public LightHandler light;
    
    private List<Entity> entities = new ArrayList<>();
    private List<Mob> mobs = new ArrayList<>();
    
    public int[] tiles;
    public int[] overlayTiles;
    public char[] events;
    public char[] warps;
    public char[] spawnPoints;
    public int width;
    public int height;
    
    public final char lID;
    
    private boolean warp = false;
    private char newID = 0x00;
    
    public boolean showBounds = false;
    
    protected boolean dungeon = false;
    
    public boolean isDungeon() {
        return dungeon;
    }
    
    public Level(char lID, int[] data, int[] overlayData, char[] events, char[] warps, char[] spawnPoints, int width, int height, Debug d) {
        tiles = data;
        overlayTiles = overlayData;
        this.d = d;
        this.events = events;
        this.warps = warps;
        this.spawnPoints = spawnPoints;
        this.width = width;
        this.height = height;
        this.lID = lID;
    }
    
    public void tick() {
        for (Tile t : Tile.tiles) {
            if (t == null) break;
            t.tick();
        }
        
        try {
            getEntities().stream().filter((e) -> (e.getActive())).forEach((e) -> {
                e.superTick();
            });
            
            getMobs().stream().map((e) -> {
                if (e.getActive()) {
                    e.superTick();
                }
                return e;
            }).map((e) -> {
                if (!e.beenNotified) {
                    e.levelInitializationNotification();
                }
                return e;
            }).filter((e) -> (e.useEquipped/* && e.getEquippedWeapon().getItem() != Item.NULL*/)).map((e) -> {
                if (getTile(e.x >> Screen.SHIFT, e.y >> Screen.SHIFT, false) != Tile.WATER) {
                    e.getEquippedWeapon().getItem().use(e, this);
                } else {
                    e.useEquipped = false;
                    e.renderEquipped = false;
                }
                return e;
            }).forEach((e) -> {
                e.useEquipped = false;
            });
        } catch (java.util.ConcurrentModificationException ex) {
            if (d != null) {
                //!ADD CODES FOR WHERE THE ERROR ORIGINATES
                d.printMessage(Debug.DebugType.ERROR, "LEVEL", ex.getMessage(), 5);
            }
        }
    }
    
    public void render(Screen s) {
        try {
            Collections.sort(mobs);
        } catch (java.lang.IllegalArgumentException | java.util.ConcurrentModificationException ex) {
            if (d != null) {
                d.printMessage(Debug.DebugType.ERROR, "LEVEL", ex.getMessage(), 5);
            }
        }
        
        try {
            getEntities().stream().filter((e) -> (e.getActive())).map((e) -> {
                e.superRender(s);
                return e;
            }).filter((e) -> (showBounds)).forEach((e) -> {
                e.getBounds().render(s, 0xFF000000);
            });
            
            getMobs().stream().filter((e) -> (e.getActive())).map((e) -> {
                if (e.renderEquipped && e.movingDir == 0) {
                    e.getEquippedWeapon().getItem().render(s, e, e.movingDir);
                }
                e.superRender(s);
                renderWearables(e, s);
                if (e.renderEquipped && e.movingDir > 0) {
                    e.getEquippedWeapon().getItem().render(s, e, e.movingDir);
                }
                return e;
            }).filter((e) -> (showBounds)).forEach((e) -> {
                e.getBounds().render(s, 0xFF000000);
            });
        } catch (java.util.ConcurrentModificationException | java.lang.NullPointerException ex) {
            if (d != null) {
                d.printMessage(Debug.DebugType.ERROR, "LEVEL", ex.getLocalizedMessage(), 5);
            }
        }
    }
    
    private void renderWearables(Mob m, Screen s) {
        Item[] wearing = m.getEquippedWearables();
        if (wearing == null) return;
        
        int xTile = 0;
        int yTile = 0;
        
        //gonna have to be a little different for shirts bc theyre on both tiles
        for (int i = 0; i < wearing.length; i++) {
        if (wearing[i] != null) {
            int xOffset = 0;
            int yOffset = m.movingDir;
            
            if (m.getMoving()) xOffset += (m.getNumSteps() >> 3) & 7;
            else if (m.movingDir > 1) {
                xOffset = 6 + m.movingDir;
                yOffset = 1;
            }
            
            if (m.renderEquipped && m.movingDir > 1) {
                xOffset = 6 + m.movingDir;
                yOffset = 2;
            }
            
            s.render(m.x, m.y, (wearing[i].getXTile() + xOffset) + (wearing[i].getYTile() + yOffset) * (Screen.TILE_SHEET_SIZE / Screen.TILE_SIZE));
        }
        }
    }
    
    public boolean npcPresent(int x, int y) throws java.util.ConcurrentModificationException {
        return getMobs().stream().filter((m) -> (m instanceof NPC)).anyMatch((m) -> (m.x >> Screen.SHIFT == x && m.y >> Screen.SHIFT == y));
    }
    
    public Mob mobPresent(int x, int y) {
        for (Mob m : getMobs()) {
            if (m.getActive() && (m.x >> Screen.SHIFT == x && m.y >> Screen.SHIFT == y)) {
                return m;
            }
        }
        
        return null;
    }
    
    public boolean interact(int x, int y) {
        for (Mob m : getMobs()) {
            if (m instanceof NPC || m.getID().contains("placeable")) {
                if (m.x >> Screen.SHIFT == x && m.y >> Screen.SHIFT == y) {
                    m.interact();
                    return true;
                }
            }
        }
        
        if (events[x + y * width] == Event.INPUT_TRIGGERED_SIGNPOST_MESSAGE) {
            Seshat.display(Messages.getMessage(Messages.getMessageID(Event.INPUT_TRIGGERED_SIGNPOST_MESSAGE, x, y, width)), 2);
            Seshat.mID = Messages.getMessageID(Event.INPUT_TRIGGERED_SIGNPOST_MESSAGE, x, y, width);
            return true;
        }
        
        if (events[x + y * width] == Event.INPUT_TRIGGERED_YESNO_MESSAGE) {
            Seshat.display(Messages.getMessage(Messages.getMessageID(Event.INPUT_TRIGGERED_YESNO_MESSAGE, x, y, width)), 2, new String[]{"Yes", "No"});
            Seshat.mID = Messages.getMessageID(Event.INPUT_TRIGGERED_YESNO_MESSAGE, x, y, width);
            return true;
        }
        
        return false;
    }
    
    public void renderTiles(Screen screen, int xOffset, int yOffset) {
        if (xOffset < 0)
            xOffset = 0;
        if (xOffset > ((width << Screen.SHIFT) - screen.width))
            xOffset = ((width << Screen.SHIFT) - screen.width);
        if (yOffset < 0)
            yOffset = 0;
        if (yOffset > ((height << Screen.SHIFT) - screen.height))
            yOffset = ((height << Screen.SHIFT) - screen.height);

        screen.setOffset(xOffset, yOffset);

        Player p = null;
        if (this instanceof Dungeon) {
            for (Mob m : getMobs()) {
                if (m instanceof Player) {
                    p = (Player) m;
                    break;
                }
            }
        }
        DungeonGenerator.Room room = null;
        if (p != null) room = p.getCurrentRoom();
        for (int y = (yOffset >> Screen.SHIFT); y < (yOffset + screen.height >> Screen.SHIFT) + 1; y++) {
            for (int x = (xOffset >> Screen.SHIFT); x < (xOffset + screen.width >> Screen.SHIFT) + 1; x++) {
                if ((room != null && room.contains(new Point(x, y))) || !this.isDungeon()) getTile(x, y, false).render(screen, this, x << Screen.SHIFT, y << Screen.SHIFT, 0x000000);
                else Tile.VOID.render(screen, this, x << Screen.SHIFT, y << Screen.SHIFT, 0x000000);
            }
        }
    }
    
    public void renderOverlayTiles(Screen screen, int xOffset, int yOffset) {
        if (xOffset < 0)
            xOffset = 0;
        if (xOffset > ((width << Screen.SHIFT) - screen.width))
            xOffset = ((width << Screen.SHIFT) - screen.width);
        if (yOffset < 0)
            yOffset = 0;
        if (yOffset > ((height << Screen.SHIFT) - screen.height))
            yOffset = ((height << Screen.SHIFT) - screen.height);

        screen.setOffset(xOffset, yOffset);

        for (int y = (yOffset >> Screen.SHIFT); y < (yOffset + screen.height >> Screen.SHIFT) + 1; y++) {
            for (int x = (xOffset >> Screen.SHIFT); x < (xOffset + screen.width >> Screen.SHIFT) + 1; x++) {
                Tile t = getTile(x, y, true);
                if (t != Tile.AIR) {
                    t.render(screen, this, x << Screen.SHIFT, y << Screen.SHIFT, 0x000000);
                }
            }
        }
    }
    
    public synchronized List<Entity> getEntities() {
        return this.entities;
    }
    
    public synchronized void addEntity(Entity e) {
        this.getEntities().add(e);
    }
    
    public synchronized List<Mob> getMobs() {
        return this.mobs;
    }
    
    public synchronized void addMob(Mob m) {
        this.getMobs().add(m);
    }
    
    public Tile getTile(int x, int y, boolean z) {
        if (0 > x || x >= width || 0 > y || y >= height)
            return Tile.VOID;
        if (!z)
            return Tile.tiles[tiles[x + y * width]];
        else return Tile.tiles[overlayTiles[x + y * width]];
    }
    
    public void warp(char lID) {
        warp = true;
        newID = lID;
    }
    
    public boolean getWarping() {
        return warp;
    }
    
    public char getNewID() {
        return newID;
    }
    
    public Debug getDebug() {
        return d;
    }
}
