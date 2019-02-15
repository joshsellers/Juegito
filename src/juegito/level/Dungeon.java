package juegito.level;

import com.amp.mathem.Statc;
import com.amp.pre.Debug;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import juegito.level.tiles.Tile;

/**
 *
 * @author joshsellers
 */
public class Dungeon extends Level {

    public Dungeon(int[] data, char[] events, char[] warps, char[] spawnPoints, int width, int height, Debug d) {
        super((char)0, data, null, events, warps, spawnPoints, width, height, d);
    }
    
    public static Dungeon generateDungeon() {
        int w = Statc.random(3000, 500);
        int h = Statc.random(3000, 500);
        System.out.println("dungeon width: " + w + " dungeon height: " + h);
        
        int[] data = new int[w*h];
        char[] events = new char[w*h];
        char[] warps = new char[w*h];
        char[] spawnPoints = new char[w*h];

        int l = 1000;
        System.out.println("cave count: " + l);
        int[][] coordinates = new int[l][l];
        for (int i = 0; i < l; i++) {
            int x = Statc.random(w, 0);
            int y = Statc.random(h, 0);
            coordinates[i][0] = x;
            coordinates[0][i] = y;
            int width = Statc.random(20, 2);
            int height = Statc.random(20, 2);
            System.out.println("cave number " + i + " x: " + x + " y: " + y +" width: " + width + " height: " + height);
            for (int ya = y; ya < y + height && ya < h; ya++) {
                for (int xa = x; xa < x + width && xa < w; xa++) {
                    data[xa + ya * w] = Tile.PATH_0_4.getID();
                }
            }
        }
        
        for (int i = 0; i < coordinates.length-1; i++) {
            if (Math.abs(coordinates[0][i] - coordinates[0][i+1]) > Math.abs(coordinates[i][0] - coordinates[i+1][0])) {
                for (int y = coordinates[0][i]; y < coordinates[0][i+1] && y < h; y++) {
                    if (coordinates[i][0] + y * w < data.length) {
                        data[coordinates[i][0] + y * w] = Tile.PATH_0_4.getID();
                    }
                }
            } else {
                for (int x = coordinates[i][0]; x < coordinates[i+1][0] && x < w; x++) {
                    if (x + coordinates[0][i] * w < data.length) {
                        data[x + coordinates[0][i] * w] = Tile.PATH_0_4.getID();
                    }
                }
            }
        }
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (y > 2 && data[x + (y-1) * w] == Tile.VOID.getID() && data[x + y * w] == Tile.PATH_0_4.getID()) {
                    if (data[x + (y-2) * w] == Tile.WALL_0_0.getID() || data[x + (y-2) * w] == Tile.WALL_0_1.getID()) data[x + (y-2) * w] = Tile.VOID.getID();
                    if (data[x + (y-3) * w] != Tile.WALL_0_0.getID() || data[x + (y-3) * w] != Tile.WALL_0_1.getID()) data[x + (y-3) * w] = Tile.VOID.getID();
                    
                    data[x + (y-2) * w] = Tile.WALL_0_0.getID();
                    data[x + (y-1) * w] = Tile.WALL_0_1.getID();
                }
            }
        }
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (data[x + y * w] == Tile.WALL_0_1.getID() && data[x + (y+1) * w] == Tile.VOID.getID()) {
                    data[x + (y+1) * w] = Tile.PATH_0_4.getID();
                }
            }
        }    

        for (int i = 0; i < w*h; i++) {
            if (!Tile.getTile(data[i]).getSolid()) {
                spawnPoints[i] = (char) 5;
                break;
            }
        }
        
        saveDungeonData(data, w, h);
        
        return new Dungeon(data, events, warps, spawnPoints, w, h, null);
    }
    
    @SuppressWarnings("ImplicitArrayToString")
    public static void saveDungeonData(int[] data, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                img.setRGB(x, y, Tile.getTile(data[x + y * w]).getColor());
            }
        }
        
        try {
            ImageIO.write(img, "png", new File(System.getProperty("user.home") + "/Library/Application Support/Juegito/generated_dungeons/" + data.toString() + ".png"));
        } catch (IOException ex) {
            Logger.getLogger(Dungeon.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
}
