package juegito.level;

import com.amp.mathem.Statc;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javafx.scene.shape.Line;
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
            p.l.addMob(p);
        }
        if (dgl != null) {
            dgl.loadDungeon((Dungeon) p.l);
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
        List<Room> rooms =  new ArrayList<>();
        int count = Statc.intRandom(50, 100) * ((w * h) / 10000) / 8;
        for (int i = 0; i < count; i++) {
            int x = Statc.intRandom(0, w);
            int y = Statc.intRandom(0, h);
            int width = Statc.intRandom(3, 50);
            int height = Statc.intRandom(3, 50);
            Room r = new Room(x, y, width, height);
            float pcnt = ((float) i / (float) count) * 100f;
            System.out.println("CREATING ROOMS... " + pcnt + "%");
            progress = (int) pcnt;
            boolean remove = false;
            int indx = 0;
            for (int j = 0; j < rooms.size(); j++) {
                float pcnt0 = ((float) j / (float) rooms.size()) * 100f;
                System.out.println("CHECKING FOR COLLISIONS... " + pcnt0 + "% <-- " + pcnt + "% total");
                Room r0 = rooms.get(j);
                if (r0.intersects(r) || (r.x + r.width) >= w || (r.y + r.height) >= h) {
//                    if (Statc.intRandom(0, 1) == 0) {
//                        indx = j;
//                    } else {
                        remove = true;
                        break;
                    //}
                }
            }
            
            if (!remove) {
                //while (r.x + r.width >= w) r.width--;
                ///while (r.y + r.height >= h) r.height--;
                rooms.add(r);
               // rooms.remove(indx);
            }
        }
        
        Graphics g = buffer.getGraphics();
        g.setColor(Color.WHITE);
        int cnt = 0;
        for (int index = 0; index < 2; index++) {
            for (Room r : rooms) {
                Room lastClosest = null;
                float pcnt = ((float) cnt / (float) (rooms.size() * 2)) * 100f;
                progress = (int) pcnt;
                System.out.println("CONNECTING ROOMS... " + pcnt + "%");
                for (Room other : rooms) {

                    if (lastClosest == null) {
                        lastClosest = other;
                    }
                    if (other != r && !(r.hashChain.contains(String.valueOf(other.hashCode())))) {
                        int rx = r.x + (r.width / 2);
                        int ry = r.y + (r.height / 2);
                        int ox = other.x + (other.width / 2);
                        int oy = other.y + (other.height / 2);
                        int lx = lastClosest.x + (lastClosest.width / 2);
                        int ly = lastClosest.y + (lastClosest.height / 2);

                        double dist = Math.sqrt((Math.pow(rx - ox, 2)) + (Math.pow(ry - oy, 2)));
                        double oldDist = Math.sqrt((Math.pow(rx - lx, 2)) + (Math.pow(ry - ly, 2)));
                        
                       
                        if (dist < oldDist) {
                            lastClosest = other;
                        }
                    }
                }

                r.connections++;
                lastClosest.connections++;
                r.hashChain += String.valueOf(lastClosest.hashCode());
                lastClosest.hashChain += String.valueOf(r.hashCode());

                int rx = r.x + (r.width / 2);
                int ry = r.y + (r.height / 2);
                int lx = lastClosest.x + (lastClosest.width / 2);
                int ly = lastClosest.y + (lastClosest.height / 2);

                if (Math.abs(rx - lx) > Math.abs(ry - ly)) {
                    if (lx + lastClosest.width / 2 < rx) {
                        drawLine(rx - (r.width / 2), ry, lx + (lastClosest.width / 2), ly, buffer, Color.WHITE, rooms, r, lastClosest);
                    } else if (lx > rx + (r.width / 2)) {
                        drawLine(rx + (r.width / 2), ry, lx - (lastClosest.width / 2), ly, buffer, Color.WHITE, rooms, r, lastClosest);
                    }
                }

                if (Math.abs(ry - ly) > Math.abs(rx - lx)) {
                    if (lastClosest.y + lastClosest.height < r.y) {
                        drawLine(r.x + (r.width / 2), r.y, lastClosest.x + (lastClosest.width / 2), lastClosest.y + lastClosest.height, buffer, Color.WHITE, rooms, r, lastClosest);
                    } else if (r.y + r.height < lastClosest.y) {
                        drawLine(r.x + (r.width / 2), r.y + r.height, lastClosest.x + (lastClosest.width / 2), lastClosest.y, buffer, Color.WHITE, rooms, r, lastClosest);
                    }
                }

                cnt++;
            }

        }
        
        
        int i = 0;
        for (Room r : rooms) {
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
    
    public class Room extends Rectangle {
        
        public String hashChain = "_";
        public int connections = 0;
        
        public Room(int x, int y, int w, int h) {
            super(x, y, w, h);
        }
    }
    
    static void drawLine(int x0, int y0, int x1, int y1, BufferedImage img, Color c, List <Room> rooms, Room r0, Room r1) {
        boolean steep = false; 
        if (Math.abs(x0-x1) < Math.abs(y0-y1)) { 
            int temp0 = x0;
            x0 = y0;
            y0 = temp0;
            int temp1 = x1;
            x1 = y1;
            y1 = temp1;
            steep = true;
        } 
        if (x0 > x1) { 
            int tempX = x0;
            x0 = x1;
            x1 = tempX;
            int tempY = y0;
            y0 = y1;
            y1 = tempY;
        } 
        int dx = x1 - x0; 
        int dy = y1 - y0; 
        int derror2 = (int) Math.abs(dy)*2; 
        int error2 = 0; 
        int y = y0; 
        for (int x = x0; x <= x1; x++) { 
            boolean intersects = false;
            for (Room r : rooms) {
                int xa = x;
                int ya = y;
                if (steep) {
                    int buf = x;
                    xa = y;
                    ya = buf;
                }
                if (r.contains(new Point(xa, ya))) {
                    intersects = true;
                }
            }
            try {
                if (steep && !intersects) {
                    img.setRGB(y, x, c.getRGB());
                } else if (!intersects) {
                    img.setRGB(x, y, c.getRGB());
                }
            } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                System.out.println(x + " " + y);
                if (steep) {
                    img.setRGB(y-1, x-1, c.getRGB());
                } else {
                    img.setRGB(x-1, y-1, c.getRGB());
                }
            }
            error2 += derror2; 
            if (error2 > dx) { 
                y += (y1>y0?1:-1); 
                error2 -= dx*2; 
            } 
        } 
    }
}
