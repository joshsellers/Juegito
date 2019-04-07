package juegito.level;

import com.amp.mathem.Statc;
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
 * @author josh
 */
public class DungeonGenerator implements Runnable {
    
    private int w, h;
    private Thread t;
    
    private int progress = 0;
    
    private boolean generating = false;
    private Player p;
    private DungeonGeneratorLoader dgl;
    
    public synchronized void init(int w, int h) {
        this.w = w;
        this.h = h;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        generating = true;
        Dungeon d = generateDungeon(w, h);
        System.out.println("Generated Dungeon");
        if (p != null) {
            p.l = d;
            p.x = (w / 2) << juegito.gfx.Screen.SHIFT;
            p.y = (h / 2) << juegito.gfx.Screen.SHIFT;
            p.setGX(p.x);
            p.setGY(p.y);
            d.addMob(p);
        }
        if (dgl != null) {
            dgl.loadDungeon(d);
        }
    }
    
    public synchronized void init() {
        int w = Statc.intRandom(100, 500);
        int h = Statc.intRandom(100, 500);
        init(w, h);
    }
    
    private synchronized Dungeon generateDungeon(int w, int h) {
        System.out.println("dungeon width: " + w + " dungeon height: " + h);
        
        int[] data = new int[w*h];
        char[] events = new char[w*h];
        char[] warps = new char[w*h];
        char[] spawnPoints = new char[w*h];

        BufferedImage buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        List<Rectangle> rooms =  new ArrayList<>();
        int count = Statc.intRandom(50, 100) * ((w * h) / 10000) / 8;
        for (int i = 0; i < count; i++) {
            int x = Statc.intRandom(0, w);
            int y = Statc.intRandom(0, h);
            int width = Statc.intRandom(3, 50);
            int height = Statc.intRandom(3, 50);
            Rectangle r = new Rectangle(x, y, width, height);
            float pcnt = ((float) i / (float) count) * 100f;
            System.out.println("CREATING ROOMS... " + pcnt + "%");
            progress = (int) pcnt;
            boolean remove = false;
            int indx = 0;
            for (int j = 0; j < rooms.size(); j++) {
                float pcnt0 = ((float) j / (float) rooms.size()) * 100f;
                System.out.println("CHECKING FOR COLLISIONS... " + pcnt0 + "% <-- " + pcnt + "% total");
                Rectangle r0 = rooms.get(j);
                if (r0.intersects(r)) {
//                    if (Statc.intRandom(0, 1) == 0) {
//                        indx = j;
//                    } else {
                        remove = true;
                        break;
                    //}
                }
            }
            
            if (!remove) {
                while (r.x + r.width >= w) r.width--;
                while (r.y + r.height >= h) r.height--;
                rooms.add(r);
               // rooms.remove(indx);
            }
        }
        Graphics g = buffer.getGraphics();
        int i = 0;
        for (Rectangle r : rooms) {
            float pcnt = ((float) i / (float) rooms.size()) * 100f;
            System.out.println("DRWAWING ROOMS... " + pcnt + "%");
            g.setColor(Color.WHITE);
            g.drawRect(r.x, r.y, r.width, r.height);
            i++;
            progress = (int) pcnt;
        }
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (buffer.getRGB(x, y) == Color.white.getRGB()) {
                    float pcnt = ((float) (x + y * w) / (float) (w * h)) * 100f;
                    System.out.println("CONVERTING IMAGE... " + pcnt + "%");
                    progress = (int) pcnt;
                    data[x + y * w] = Tile.GRASS_4.getID();
                }
            }
        }
        
        saveDungeonData(data, w, h);
        generating = false;
        
        return new Dungeon(data, events, warps, spawnPoints, w, h, null);
    }
    
    @SuppressWarnings("ImplicitArrayToString")
    private synchronized void saveDungeonData(int[] data, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float pcnt = ((float) (x + y * w) / (float) (w * h)) * 100f;
                System.out.println("CONVERTING DATA BACK TO IMAGE... " + pcnt + "%");
                progress = (int) pcnt;
                img.setRGB(x, y, Tile.getTile(data[x + y * w]).getColor());
            }
        }
        
        try {
            ImageIO.write(img, "png", new File(System.getProperty("user.home") + "/Library/Application Support/Juegito/generated_dungeons/" + data.toString() + ".png"));
        } catch (IOException ex) {
            Logger.getLogger(Dungeon.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    public int getProgress() {
        return progress;
    }
    
    public boolean isGenerating() {
        return generating;
    }
    
    public void setPlayer(Player p) {
        this.p = p;
    }
    
    public void setDungeonGeneratorLoader(DungeonGeneratorLoader dgl) {
        this.dgl = dgl;
    }
}
