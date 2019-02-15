package juegito.entities;

import com.amp.mathem.Statc;
import com.amp.pre.Debug;
import juegito.gfx.Screen;
import juegito.level.Level;
import juegito.level.Player;
import juegito.level.items.Item;
import juegito.level.items.StoredItem;
import juegito.level.tiles.Tile;
import juegito.ui.Seshat;

/**
 *
 * @author joshsellers
 */
public class NPC extends Mob {
    
    public static final String CODEVERSION = "V1.8.7";
    
    /*//!If leaving level with npc already in it, have list or something
    with changed/moved npcs in it. When entering a level, check if
    the level has been loaded before, and if so, check if any npcs have
    been moved or changed at all. If an npc has changed, do not load npc
    data from LID_npcs.txt, instead load from the list. Upon game shutdown,
    save the list in the game's save file, because in the future, files such
    as LID_npcs.txt will be inside the jarfile and therefore unwriteable*/
    
    private int tickCount;
    
    private Player p;
    
    private String scriptid;
    
    private boolean waiting;
    private int waitTime;
    private int waitTimer;
        
    private int movementType;
    
    private int[] interactionResponse;
    private int responseIntervals;
    private int currentInterval;
    private int overallInterval;
    
    private int[] intervalLocations;
    
    private boolean responding;
    
    private boolean invokedPlayerLock;
    
    private int[] traits;
    //luck, skill, intellegence, response, statCount, statWeight0, ... //josh wtf weight? oh wait no like weight like the other kind
    private int[][] stats;

    public NPC(int x, int y, int level, boolean randomizeInv, int id, int[] interactionResponse, int responseIntervals, int currentInterval, int movementType, Level l, Player p, String scriptid) {
        super(x, y, 16, 16, 50, 30, level, (char) 0x88, String.valueOf((x >> Screen.SHIFT) + "" + (y >> Screen.SHIFT) + "" + id), l);
        this.interactionResponse = interactionResponse;
        this.responseIntervals = responseIntervals;
        
        this.p = p;
        
        this.scriptid = scriptid;
        
        this.movementType = movementType;
                
        if (randomizeInv) {
            //randomize inventory
        }
        //System.out.println(responseIntervals + " " + ID);
        //Find functions in NPC's code, place their positions in the array
        intervalLocations = new int[responseIntervals];
        int j = 0;
        for (int i = 0; i < interactionResponse.length && j < intervalLocations.length; i++) {
            if (interactionResponse[i] == 0xFF || interactionResponse[i] == 0x0F) {
                j++;
                if (j < intervalLocations.length) {
                    intervalLocations[j] = i;
                }
            }
        }
    }

    @Override
    public void tick() {
        if (responding && p != null && l != null && !waiting) {
            if (overallInterval < interactionResponse.length && x == gx && y == gy) {
                processCommand();
            }
        } else if (waiting) {
            responding = false;
            if (waitTimer < waitTime) {
                waitTimer++;
            } else if (waitTimer == waitTime) {
                responding = true;
                waiting = false;
                waitTimer = 0;
                waitTime = 0;
            }
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
    
    public void processCommand() {
        //l.getDebug().printMessage(Debug.DebugType.INFO, ID, currentInterval + "." + overallInterval + ": " + Integer.toHexString(interactionResponse[overallInterval]), 5);
        System.out.println(currentInterval + "." + overallInterval + ": " + Integer.toHexString(interactionResponse[overallInterval]));
        switch (interactionResponse[overallInterval]) {
            case 0x00:
                overallInterval++;
                break;
            case 0xFF:
                if (overallInterval != 0) {
                    currentInterval++;
                }   overallInterval++;
                break;
            case 0xFE:
                responding = false;
                //if (currentInterval != responseIntervals - 1) { //Disableing this is yikes but it also messes everything else up so.
                //update it's not yikes and i have yet to find out why it's not being yikes
                //future josh doesnt understand why past josh didnt uncomment it if its working right
                overallInterval++;
                //}
                if (!p.canMove && invokedPlayerLock) p.canMove = true;
                break;
            case 0xFD:
                l.getDebug().printMessage(Debug.DebugType.INFO, "npc" + ID, String.valueOf(overallInterval), 5);
                overallInterval = interactionResponse[overallInterval + 1];
                responding = false;
                l.getDebug().printMessage(Debug.DebugType.INFO, "npc" + ID, String.valueOf(overallInterval), 5);
                if (!p.canMove && invokedPlayerLock) p.canMove = true;
                break;
            case 0x20:
                gy -= interactionResponse[overallInterval + 1] << Screen.SHIFT;
                overallInterval += 2;
                break;
            case 0x21:
                gy += interactionResponse[overallInterval + 1] << Screen.SHIFT;
                overallInterval += 2;
                break;
            case 0x22:
                gx -= interactionResponse[overallInterval + 1] << Screen.SHIFT;
                overallInterval += 2;
                break;
            case 0x23:
                gx += interactionResponse[overallInterval + 1] << Screen.SHIFT;
                overallInterval += 2;
                break;
            case 0xD0:
                String message = "";
                for (overallInterval++; overallInterval < interactionResponse.length; overallInterval++) {
                    if (interactionResponse[overallInterval] == 0xDE) {
                        overallInterval++;
                        break;
                    }
                    message += (char) interactionResponse[overallInterval];
                }   Seshat.display(message, 2);
                break;
            case 0x24:
                movingDir = p.movingDir ^ 1;
                overallInterval++;
                break;
            case 0x25:
                movingDir = interactionResponse[overallInterval + 1];
                overallInterval += 2;
                break;
            case 0x27:
                {
                    int xOffset = 0;
                    int yOffset = 0;
                    if (p.movingDir == 0) yOffset = -2;
                    if (p.movingDir == 1) yOffset = 2;
                    if (p.movingDir == 2) xOffset = -1;
                    if (p.movingDir == 3) xOffset = 1;
                    gx = ((p.x >> Screen.SHIFT) + xOffset) << Screen.SHIFT;
                    gy = ((p.y >> Screen.SHIFT) + yOffset) << Screen.SHIFT;
                    overallInterval++;
                    break;
                }
            case 0x28:
                p.movingDir = movingDir ^ 1;
                overallInterval++;
                break;
            case 0x29:
                {
                    int steps = interactionResponse[overallInterval + 1];
                    int xOffset = 0;
                    int yOffset = 0;
                    if (p.movingDir == 0) yOffset = -2 - steps;
                    if (p.movingDir == 1) yOffset = 2 + steps;
                    if (p.movingDir == 2) xOffset = -1 - steps;
                    if (p.movingDir == 3) xOffset = 1 + steps;
                    gx = ((p.x >> Screen.SHIFT) + xOffset) << Screen.SHIFT;
                    gy = ((p.y >> Screen.SHIFT) + yOffset) << Screen.SHIFT;
                    overallInterval += 2;
                    break;
                }
            case 0x2A:
                waiting = true;
                waitTime = interactionResponse[overallInterval + 1];
                overallInterval += 2;
                break;
            case 0x05:
                //deactivate??? I don't remember what I wanted this to do.
                overallInterval++;
                break;
            case 0xD5:
                useEquipped();
                overallInterval++;
                break;
            case 0xD6:
                {
                    int id = interactionResponse[overallInterval + 1];
                    int usage = interactionResponse[overallInterval + 2];
                    int adjx = x >> Screen.SHIFT;
                    int adjy = y >> Screen.SHIFT;
                    if (movingDir == 0) adjx--;
                    if (movingDir == 1) adjy++;
                    if (movingDir == 2) adjx--;
                    if (movingDir == 3) adjx++;
                    Mob m = l.mobPresent(adjx, adjy);
                    if (m != null) {
                        m.addItem(Item.getItem(id), usage);
                    } else {
                        l.addEntity(new DroppedItem(adjx >> Screen.SHIFT, adjy >> Screen.SHIFT, 3, usage, true, l, l.getDebug(), Item.getItem(id)));
                    }       overallInterval += 3;
                    break;
                }
            case 0x06:
                if (!p.canMove && invokedPlayerLock) p.canMove = true;
                active = false;
                break;
            case 0xD7:
                int id = interactionResponse[overallInterval + 1];
                int usage = interactionResponse[overallInterval + 2];
                addItem(Item.getItem(id), usage);
                overallInterval += 3;
                break;
            case 0xD8:
                for (int j = 0; j < this.getInventory().size(); j++) {
                    Item i = getInventory().get(j).getItem();
                    if (i == this.getMostRecentItem()) {
                        equip(j);
                        break;
                    }
                }
                overallInterval++;
                break;
            case 0x30:
                p.canMove = false;
                invokedPlayerLock = true;
                overallInterval++;
                break;
            case 0xDA:
                p.getQuestHandler().addQuest(p.getQuestHandler().loadQuest((byte) interactionResponse[overallInterval + 1]));
                overallInterval += 2;
                break;
            case 0xD9:
                p.getQuestHandler().advanceQuest((byte) interactionResponse[overallInterval + 1], interactionResponse[overallInterval + 2]);
                overallInterval += 3;
                break;
            default:
                break;
        }
    }
    
    @Override
    protected void die(Mob source) {

    }
    
    @Override
    public void interact() {
        responding = true;
    }
    
    @Override
    public void levelInitializationNotification() {
        if (overallInterval >= interactionResponse.length) overallInterval = interactionResponse.length - 1; //this is horrible plz fix
        if (interactionResponse[overallInterval] == 0x0F) {
            responding = true;
            overallInterval++;
        }
        
        beenNotified = true;
    }
    
    @Override
    public void questStartNotification(byte ID) {
        System.out.println(Integer.toHexString(interactionResponse[overallInterval]));
        if (overallInterval >= interactionResponse.length) overallInterval = interactionResponse.length - 1; //this is horrible plz fix
        if (interactionResponse[overallInterval] == 0x1F && interactionResponse[overallInterval + 1] == ID) {
            responding = true;
            overallInterval += 2;
        }
    }

    @Override
    protected void attackResponse(Mob source) {

    }
    
    public void setOverallInterval(int overallInterval) {
        this.overallInterval = overallInterval;
    }

    @Override
    public char[] getSaveInfo() {
        int oicheck = overallInterval;
        if (waiting) {
            oicheck -= 2;
        }
        
        char[] out = new char[5 + scriptid.length()];
        
        out[0] = (char) (currentInterval << 4);
        out[1] = (char) (oicheck << 4);
        out[2] = (char) (movementType << 4);
        out[3] = (char) 0x00;
        if (responding) out[3] = (char) 0x01;
        
        for (int i = 4; i < out.length - 1; i++) {
            out[i] = scriptid.charAt(i - 4);
        }
        out[out.length - 1] = ';';
        
        return out;
    }
    
}
