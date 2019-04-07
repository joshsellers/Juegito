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
import juegito.level.tiles.Tile;

/**
 *
 * @author joshsellers
 */
public class Dungeon extends Level {

    public Dungeon(int[] data, char[] events, char[] warps, char[] spawnPoints, int width, int height, Debug d) {
        super((char)0, data, new int[data.length], events, warps, spawnPoints, width, height, d);
        int[] air = new int[data.length];
        for (int i = 0; i < air.length; i++) air[i] = Tile.AIR.getID();
        this.overlayTiles = air;
    }
    
    
}
