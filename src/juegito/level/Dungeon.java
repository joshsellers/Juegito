package juegito.level;

import com.amp.mathem.Statc;
import com.amp.pre.Debug;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import juegito.entities.Mob;
import juegito.entities.Stalker;
import juegito.gfx.Screen;
import juegito.level.tiles.Tile;

/**
 *
 * @author joshsellers
 */
public class Dungeon extends Level {
    
    private List<DungeonGenerator.Room> rooms;

    public Dungeon(int[] data, char[] events, char[] warps, char[] spawnPoints, int width, int height, Debug d) {
        super((char)0, data, new int[data.length], events, warps, spawnPoints, width, height, d);
        int[] air = new int[data.length];
        for (int i = 0; i < air.length; i++) air[i] = Tile.AIR.getID();
        this.overlayTiles = air;
        
        this.dungeon = true;
    }
    
    public void setRooms(List<DungeonGenerator.Room> rooms) {
        this.rooms = rooms;
    }
    
    public List<DungeonGenerator.Room> getRooms() {
        return this.rooms;
    }

    public void spawnLoot(DungeonGenerator.Room room) {
        if (room.getLevel() == 1) {
            int x = Statc.intRandom(room.x + 1, room.x + room.width - 1) << Screen.SHIFT;
            int y = Statc.intRandom(room.y + 1, room.y + room.height - 1) << Screen.SHIFT;
            Stalker s = new Stalker(x, y, 1, true, this);
            Player p = null;
            for (Mob m : getMobs()) {
                if (m.getActive() && m instanceof Player) {
                    p = (Player) m;
                    break;
                }
            }
            s.setTartget(p);
            s.setActive(true);
            this.addMob(s);
            System.out.println("Spawned stalker");
        }
    }
    
    
}
