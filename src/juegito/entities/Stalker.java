package juegito.entities;

import com.amp.mathem.Statc;
import juegito.gfx.Screen;
import juegito.level.Level;
import juegito.level.items.Item;
import juegito.level.tiles.Tile;
import juegito.particles.Particle;

/**
 *
 * @author joshsellers
 */
public class Stalker extends Enemy {
    
    private int tickCounter;
    private int tickCount;
    
    private int gox, goy;

    public Stalker(int x, int y, int level, boolean randomizeInv, Level l) {
        super(x, y, 16, 16, 75, level, 5, 45, (char)0x72, "STALKER", l, null);
        
        if (randomizeInv) {
            addItem(Item.DAGGER_, 1);
            equip(0);
            
            int r = Statc.random(100, 0);
            for (int i = 0; i < r; i++) {
                addItem(Item.getItem(Statc.random(Item.TOP_ID-1, 0)), 1);
            }
        }
    }

    @Override
    public void tick() {                
        if (tickCounter == 90) {
            useEquipped();
            tickCounter = 0;
        }
        
        if (target != null) {
            gox = (target.x << Screen.SHIFT) >> Screen.SHIFT;
            goy = (target.y << Screen.SHIFT) >> Screen.SHIFT;
            
            if (target.movingDir == 0) {
                goy += target.bounds.height;
            }
            if (target.movingDir == 1) {
                goy -= target.bounds.height;
            }
            if (target.movingDir == 2) {
                gox += target.bounds.width;
            }
            if (target.movingDir == 3) {
                gox -= target.bounds.width;
            }
        } else {
            gox = x;
            goy = y;
        }
        
        if (!moving || (gx == x && gy == y)) {
            if (y > goy) {
                gy -= Screen.TILE_SIZE;
                if (gy < 0) gy = y;
            } else if (y < goy) {
                gy += Screen.TILE_SIZE;
                if (gy >= l.height<<Screen.SHIFT) gy = y;
            } else if (x > gox) {
                gx -= Screen.TILE_SIZE;
                if (gx < 0) gx = x;
            } else if (x < gox) {
                gx += Screen.TILE_SIZE;
                if (gx >= l.width<<Screen.SHIFT) gx = x;
            }
        }
                        
        if (Tile.getTile(l.getTile(gx >> Screen.SHIFT,  gy >> Screen.SHIFT, false).getID()).getSolid() || (Tile.getTile(l.getTile(gx >> Screen.SHIFT,  gy >> Screen.SHIFT, true).getID()).getSolid() && Tile.getTile(l.getTile(gx >> Screen.SHIFT,  gy >> Screen.SHIFT, true).getID()) != Tile.VOID)) {
            gx = x;
            gy = y;
        }
        
        int xa = 0;
        int ya = 0;
        
        if (x < gx) {
            xa++;
            moving = true;
        } else if (y < gy) {
            ya++;
            moving = true;
        } else if (x > gx) {
            xa--;
            moving = true;
        } else if (y > gy) {
            ya--;
            moving = true;
        }
        
        move(xa, ya);
        
        tickCounter++;
        tickCount++;
    }   

    @Override
    public void render(Screen s) {
        int yOffset = -16;
        int yOffsetB = 0;
        int xTile = 24;
        int yTile = 27;
        int animSpeed = 3;
        int xLimit = 16;
        int waterYTileOffset = 0;
        
        boolean inWater = false; 
        if (movingDir == 0) {
            inWater = l.getTile(x >> Screen.SHIFT, (y + 15) >> Screen.SHIFT, false) == Tile.WATER;
        } else if (movingDir == 1) {
            inWater = l.getTile(x >> Screen.SHIFT, (y + 15) >> Screen.SHIFT, false) == Tile.WATER;
        } else if (movingDir == 2) {
            inWater = l.getTile(x >> Screen.SHIFT, y >> Screen.SHIFT, false) == Tile.WATER;
            if (inWater) inWater = l.getTile((x + 15) >> Screen.SHIFT, y >> Screen.SHIFT, false) == Tile.WATER;
        } else if (movingDir == 3) {
            inWater = l.getTile(x >> Screen.SHIFT, y >> Screen.SHIFT, false) == Tile.WATER;
            if (inWater) inWater = l.getTile((x + 15) >> Screen.SHIFT, y >> Screen.SHIFT, false) == Tile.WATER;
        }
         
        if (inWater) {
            yOffset = 0;
            if (!moving) yOffsetB += (tickCount >> 3) & 1;
            waterYTileOffset += (tickCount >> 3) & 1;
        }
        
        if (speed == 4) animSpeed = 1;
        
        if (movingDir == 3) {
            xTile = 8;
            //xLimit = 8;
        }
        if (movingDir == 2) {
            xTile = 16;
           // xLimit = 12;
        }
        if (movingDir == 1) {
            xTile = 0;
            //xLimit = 4;
        }
        
        if (moving) {
            int animOffset = (numSteps >> animSpeed) & 7;
            xTile += animOffset;
        } else {
            xTile = 8 + movingDir;
            yTile = 25;
        }
        
        if (renderEquipped) {
            yTile = 25;
            if (movingDir == 0) xTile = 12;
            else if (movingDir == 1) xTile = 13;
            else if (movingDir == 2) {
                xTile = 15;
                s.render(x - 16, y + yOffset + yOffsetB, 14 + yTile * (Screen.TILE_SHEET_SIZE / Screen.TILE_SIZE), 0, 0, 1);
                s.render(x - 16, y, 14 + (yTile + 1) * (Screen.TILE_SHEET_SIZE / Screen.TILE_SIZE), 0, 0, 1);
            }
            else if (movingDir == 3) {
                xTile = 16;
                s.render(x + 16, y + yOffset + yOffsetB, 17 + yTile * (Screen.TILE_SHEET_SIZE / Screen.TILE_SIZE), 0, 0, 1);
                s.render(x + 16, y, 17 + (yTile + 1) * (Screen.TILE_SHEET_SIZE / Screen.TILE_SIZE), 0, 0, 1);
            }
        }
        
        if (inWater) s.render(x, y + 6 + yOffsetB, 15 + (29 + waterYTileOffset) * (Screen.TILE_SHEET_SIZE / Screen.TILE_SIZE), 0, 0, 1);
        else s.render(x, y, xTile + (yTile + 1) * (Screen.TILE_SHEET_SIZE / Screen.TILE_SIZE), 0, 0, 1);
        s.render(x, y + yOffset + yOffsetB, xTile + yTile * (Screen.TILE_SHEET_SIZE / Screen.TILE_SIZE), 0, 0, 1);
    }

    @Override
    public char[] getSaveInfo() {
        char nullP = 0x00;
        if (target == null) nullP = 0x01;
        return new char[] {nullP};
    }

    @Override
    protected void die(Mob source) {
        if (source != null) {
            source.addXP(getTotalXP() / 3);
            Particle.emitValue(source.x, source.y, getTotalXP() / 3, 0x00EFA5);
        }
    }

    @Override
    protected void attackResponse(Mob source) {
        target = source;
    }

    @Override
    public void interact() {

    }

    @Override
    public void levelInitializationNotification() {
        beenNotified = true;
    }
}
