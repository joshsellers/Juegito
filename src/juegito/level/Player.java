package juegito.level;

import com.amp.AmpIO.hard.KeyIN;
import juegito.entities.Mob;
import juegito.gfx.Screen;
import juegito.level.tiles.Tile;
import juegito.quest.QuestHandler;

/**
 *
 * @author joshsellers
 */
public class Player extends Mob {
    
    private static final long keyStrokeDurationBoundary = 108;
    
    private KeyIN k;
    
    private QuestHandler qh;
        
    public boolean noCollide = false;
    
    private int lastLevel;
    private int displayTimer = -1;
        
    public Player(int x, int y, int level, Level l, KeyIN k, QuestHandler qh) {
        super(x, y, 16, 16, 100, 60, level, (char)0x73, "PLAYER", l);
        this.x = x;
        this.y = y;
        gx = x;
        gy = y;
        
        this.k = k;
        this.qh = qh;
        
        lastLevel = level;
    }
    
    @Override
    public void tick() {                
        leveledUp = level > lastLevel || (displayTimer < 35 * 5 && displayTimer >= 0);
        if (leveledUp) {
            displayTimer++;
            if (displayTimer >= 35 * 5) displayTimer = -1;
        }
        
        if (canMove) movement();
        
        if (!l.getWarping()) {
            if (x == (x >> Screen.SHIFT) << Screen.SHIFT && y == (y >> Screen.SHIFT) << Screen.SHIFT) {
                if (l.events[(x >> Screen.SHIFT) + (y >> Screen.SHIFT) * l.width] == Event.ANIMATION_STEP_TRIGGER) {
                    Tile.getTile(l.tiles[(x >> Screen.SHIFT) + (y >> Screen.SHIFT) * l.width]).trigger();
                }

                if (l.warps[(x >> Screen.SHIFT) + (y >> Screen.SHIFT) * l.width] != 0x00) {
                    char warp = l.warps[(x >> Screen.SHIFT) + (y >> Screen.SHIFT) * l.width];
                    if (warp == 0x01) {
                        warp = 0x00;
                    }
                    l.warp(warp);
                }
            }
        }
        
        lastLevel = level;
    }
    
    private void movement() {        
        if (!moving || (gx == x && gy == y)) {
            if (k.w.isPressed()) {
                gy -= Screen.TILE_SIZE;
                if (gy < 0) gy = y;
            } else if (k.s.isPressed()) {
                gy += Screen.TILE_SIZE;
                if (gy >= l.height << Screen.SHIFT) gy = y;
            } else if (k.a.isPressed()) {
                gx -= Screen.TILE_SIZE;
                if (gx < 0) gx = x;
            } else if (k.d.isPressed()) {
                gx += Screen.TILE_SIZE;
                if (gx >= l.width << Screen.SHIFT) gx = x;
            } 
            
            if (k.shift.isPressed()) speed = 4;
            else speed = 1;
            
            if ((gx >> Screen.SHIFT) << Screen.SHIFT != gx || (gy >> Screen.SHIFT) << Screen.SHIFT != gy) {
                gx = (gx >> Screen.SHIFT) << Screen.SHIFT;
                gy = (gy >> Screen.SHIFT) << Screen.SHIFT;
            }
        }
        
        if (!noCollide && ((Tile.getTile(l.getTile(gx >> Screen.SHIFT,  gy >> Screen.SHIFT, false).getID()).getSolid() || (Tile.getTile(l.getTile(gx >> Screen.SHIFT,  gy >> Screen.SHIFT, true).getID()).getSolid()) && Tile.getTile(l.getTile(gx >> Screen.SHIFT,  gy >> Screen.SHIFT, true).getID()) != Tile.VOID))) {
            gx = x;
            gy = y;
        }

        int xa = 0;
        int ya = 0;
        
        if (x < gx && ((!k.d.hasReleased() && k.d.getElapsedTimeSincePress() > keyStrokeDurationBoundary) || ((k.d.hasReleased() && k.d.getPressDuration()> keyStrokeDurationBoundary)))) {
            xa++;
            moving = true;
        } else if (y < gy && ((!k.s.hasReleased() && k.s.getElapsedTimeSincePress() > keyStrokeDurationBoundary) || ((k.s.hasReleased() && k.s.getPressDuration() > keyStrokeDurationBoundary)))) {
            ya++;
            moving = true;
        } else if (x > gx && ((!k.a.hasReleased() && k.a.getElapsedTimeSincePress() > keyStrokeDurationBoundary) || ((k.a.hasReleased() && k.a.getPressDuration() > keyStrokeDurationBoundary)))) {
            xa--;
            moving = true;
        } else if (y > gy && ((!k.w.hasReleased() && k.w.getElapsedTimeSincePress() > keyStrokeDurationBoundary) || ((k.w.hasReleased() && k.w.getPressDuration() > keyStrokeDurationBoundary)))) {
            ya--;
            moving = true;
        }
        
        if (!moving) {
            gx = x;
            gy = y;
        }
        
        move(xa, ya);
        if (x == gx && y == gy && ((x >> Screen.SHIFT) << Screen.SHIFT != x || (y >> Screen.SHIFT) << Screen.SHIFT != y)) {
            x = (x >> Screen.SHIFT) << Screen.SHIFT;
            y = (y >> Screen.SHIFT) << Screen.SHIFT;
        }
        
        if (!moving) {
            if (k.w.isPressed()/* || (key == 0 && ticksSincePress < 2)*/) {
                moving = true;
                movingDir = 0;
            } else if (k.s.isPressed()/* || (key == 1 && ticksSincePress < 2)*/) {
                moving = true;
                movingDir = 1;
            } else if (k.a.isPressed()/* || (key == 2 && ticksSincePress < 2)*/) {
                moving = true;
                movingDir = 2;
            } else if (k.d.isPressed()/* || (key == 3 && ticksSincePress < 2)*/) {
                moving = true;
                movingDir = 3;
            }
        }
    }
    
    @Override
    public void render(Screen s) {
        int yOffset = -16;
        int yOffsetB = 0;
        int xTile = 24;
        int yTile = 27;
        int animSpeed = 3;
        //int xLimit = 16;
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
            if (!moving) yOffsetB += (tickCount >> 5) & 1;
            waterYTileOffset += (tickCount >> 5) & 1;
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
        
        currentTileID = xTile + yTile * (Screen.TILE_SHEET_SIZE / Screen.TILE_SIZE);
    }

    @Override
    public char[] getSaveInfo() {
        return new char[] {0x00};
    }

    @Override
    protected void die(Mob source) {
        if (source != null) {
            source.addXP(source.getTotalXP() / 2);
        }
    }

    @Override
    protected void attackResponse(Mob source) {

    }

    @Override
    public void interact() {

    }
    
    public QuestHandler getQuestHandler() {
        return qh;
    }
    
    public boolean canMove() {
        return canMove;
    }
    
    public void enableMovement() {
        canMove = true;
    }
    
    public void disableMovement() {
        canMove = false;
    }

    @Override
    public void levelInitializationNotification() {
        beenNotified = true;
    }
}