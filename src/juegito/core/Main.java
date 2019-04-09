package juegito.core;

import com.amp.AmpIO.ImageGetter;
import com.amp.AmpIO.hard.KeyIN;
import com.amp.mathem.Statc;
import com.amp.pre.Debug;
import com.amp.pre.ABFrame;
import com.amp.text.Text;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.util.logging.Logger;
import javax.swing.event.MouseInputListener;
import juegito.core.collision.BoundingBox;
import juegito.core.net.GameClient;
import juegito.core.net.HostServer;
import juegito.entities.DroppedItem;
import juegito.entities.Mob;
import juegito.entities.NPC;
import juegito.entities.Stalker;
import juegito.gfx.Screen;
import juegito.gfx.SpriteSheet;
import juegito.level.Dungeon;
import juegito.level.DungeonGenerator;
import juegito.level.DungeonGeneratorLoader;
import juegito.level.Level;
import juegito.level.Messages;
import juegito.level.Player;
import juegito.level.items.Item;
import juegito.level.tiles.Tile;
import juegito.particles.Particle;
import juegito.quest.QuestHandler;
import juegito.ui.Seshat;
import juegito.ui.UIButton;
import juegito.ui.UIComponent;
import juegito.ui.UIHandler;
import juegito.ui.UITextInput;
import juegito.ui.talent.UITalentNode;
import juegito.ui.talent.UITalentTree;

/**
 *
 * @author joshsellers
 */
public class Main extends ABFrame implements KeyListener, MouseInputListener, ActionListener, DungeonGeneratorLoader {

    public static final String VERSION = "0.6.5.1";

    public final static int width = 1360/2;
    public final static int height = 760/2;
    public final static int SCALE = 2;

    private BufferedImage image = new BufferedImage(width / SCALE, height / SCALE, BufferedImage.TYPE_INT_RGB);
    private final int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

    private Screen screen;
    private UIHandler ui;
    private UITalentTree ut;
    private UITextInput ti;
    private UIButton newgb;
    private UIButton loadgb;
    private UIButton exitgb;
    private UIButton wb;
    private UIButton ab;
    private UIButton sb;
    private UIButton db;
    
    private boolean gameStarted = false;

    private Player p;
    protected Level l;

    private boolean debugwindow = false;
    private boolean buttonControls = false;
    private boolean chill = false;
    private int lastW = 0;
    private int lastH = 0;

    private boolean dispDebug = false;
    private boolean paused = false;
    
    private QuestHandler qh;
    
    private DungeonGenerator dg;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("getversion")) {
            System.out.println(VERSION);
            System.exit(0);
        } else {
            new Main(60, "Juegito", true, true, 1.0f, width, height, 1).start();
        }
    }

    public Main(int MAX_TICK_SPEED, String NAME, boolean resizeable, boolean decorated, float opacity, int WIDTH, int HEIGHT, int bufferStrategy) {
        super(MAX_TICK_SPEED, NAME, resizeable, decorated, opacity, WIDTH, HEIGHT, bufferStrategy, true);
        f.addKeyListener(this);
        c.addMouseListener(this);
        c.addMouseMotionListener(this);

        new HouseKeeping(this, debug).start();

        //Hermes.start(debug);
    }

    @Override
    public void init() {     
        thread.setPriority(Thread.MAX_PRIORITY);
        
        //toggleFullScreen();
        ui = new UIHandler(c);
        DebugInterceptor repl = new DebugInterceptor(this);
        repl.addDebugListener(ui);
        debug = repl;
        
        newgb = new UIButton("NEW GAME", "NEW GAME,m_ss", 0, 0, false, this, ui);
        newgb.render(c.getGraphics());
        loadgb = new UIButton("LOAD GAME", "LOAD GAME,m_ss", 0, 0, false, this, ui);
        loadgb.render(c.getGraphics());
        exitgb = new UIButton("EXIT GAME", "Exit game,m_ss", 0, 0, false, this, ui);
        exitgb.render(c.getGraphics());
        
        newgb.setPosition((WIDTH / SCALE / 2) - newgb.getWidth() - 5, (HEIGHT / SCALE / 2) - newgb.getHeight() / 2);
        newgb.setActive(true);
        loadgb.setPosition((WIDTH / SCALE / 2), (HEIGHT / SCALE / 2) - loadgb.getHeight() / 2);
        loadgb.setActive(true);
        exitgb.setPosition((WIDTH / SCALE / 2) - exitgb.getWidth() / 2, (HEIGHT / SCALE / 2) + exitgb.getHeight() / 2+ 5);
        exitgb.setActive(true);
        
        ui.addComponent(newgb);
        ui.addComponent(loadgb);
        ui.addComponent(exitgb);
    }
    
    private void startGame(boolean newgame) {
        image = toCompatibleImage(image);
        
        //if (!getFullScreen()) toggleFullScreen();
        loadMessages();
        try {
            if (!newgame && Text.getFile("/Library/Application Support/Juegito/Save/" + 0 + ".dat") != null) {
                scanSave(Text.getFile("/Library/Application Support/Juegito/Save/" + 0 + ".dat").toURI().toURL().openStream(), true);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        if (l == null && p == null) {
            int lID = getLevelID();
            l = new Level((char)lID, loadLevelData(lID), loadOverlayData(lID), loadEventData(lID), loadWarps(lID), loadSpawnPoints(lID), loadLevelSize(lID)[0], loadLevelSize(lID)[1], debug);
            int coords[] = seekSpawnPoint((char) lID,(char) 1);
            qh = new QuestHandler(debug, l);
            p = new Player(coords[0], coords[1], 1, l, new KeyIN(f), qh);

            loadNPCS(lID);
        }
        if (l != null) l.addMob(p);
        if (ui != null) {
            ui.setSource(p);
            ui.buttonControls = buttonControls;
        }
        
        if (ui != null) {
            ut = new UITalentTree(ui, c);
            UITalentNode tn = new UITalentNode("TestSkill", 100, 100, 0, 0, this, ui, ut);
            tn.setDescription("This is a test skill.&nIt is useless.&nIt doesn't do anything.&nDon't use it.&nYou'd be wasting your time.");
            ut.addChild(tn);
            ut.setActive(false);
            ui.addComponent(ut);
        }
        
        Seshat.ui = ui;
        addButtons();

        ti = new UITextInput(c, f);
        ti.setActive(false);
        if (ui != null) ui.addComponent(ti);
        
        screen = new Screen(width / SCALE, height / SCALE, new SpriteSheet(debug));
        
        gameStarted = true;
    }

     private void loadNPCS(int lID) {
        String path = System.getProperty("user.home") + "/Library/Application Support/Juegito/levels/" + String.valueOf(lID) + "/" + String.valueOf(lID) + "_npcs.txt";

        if (new File(path).exists()) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(new File(path));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            if (scanner != null) {
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    line = line.replaceAll("&n", "");
                    int x = Integer.parseInt(line.split(",")[1]) << Screen.SHIFT;
                    int y = Integer.parseInt(line.split(",")[2]) << Screen.SHIFT;
                    int id = Integer.parseInt(line.split(",")[3]);
                    int movementType = Integer.parseInt(line.split(",")[4]);

                    String filePath = System.getProperty("user.home") + "/Library/Application Support/Juegito/NPC_DEFAULTS/" + line.split(",")[0] + "/" + line.split(",")[0] + ".npc";
                    char[] buffer = null;
                    try {
                        buffer = getData(new File(filePath).toURI().toURL().openStream());
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                        return;
                    } catch (IOException ex) {
                        Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                        return;
                    }

                    int[] commands = new int[buffer.length];
                    int responseIntervals = 0;
                    boolean clearedVersion = false;
                    String version = "";
                    int j = 0;
                    for (int i = 0; i < commands.length; i++) {
                        if (buffer[i] == ';' && !clearedVersion) {
                            clearedVersion = true;
                            i++;
                        }
                        if (clearedVersion) {
                            commands[j] = (int) (buffer[i]);
                            //System.out.println(Integer.toHexString(commands[j]) + " " + buffer[i]);
                            if (commands[j] == 0xFF || commands[j] == 0x0F) {
                                responseIntervals++;
                            }
                            j++;
                        } else {
                            version += (char) (buffer[i]);
                        }
                    }
                    if (!version.equals(NPC.CODEVERSION)) debug.printMessage(Debug.DebugType.WARNING, "MAIN_INIT", "NPC " + id + " is outdated", 2);

                    NPC npc = new NPC(x, y, 1, false, id, commands, responseIntervals, 0, movementType, l, p, line.split(",")[0]);
                    l.addMob(npc);
                }
            }
        }
    }

    private void loadMessages() {
        try {
            String path = System.getProperty("user.home") + "/Library/Application Support/Juegito/";
            Messages.messages = new String(getData(new File(path + "messages.txt").toURI().toURL().openStream())).split("\\$");
            String[] ids = new String(getData(new File(path + "messageids.txt").toURI().toURL().openStream())).split("\\$");
            Messages.IDs = new int[ids.length];
            for (int i = 0; i < ids.length; i++) {
                Messages.IDs[i] = Integer.parseInt(ids[i]);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    void addButtons() {
        UIButton saveb = new UIButton("Save game", "Save game,m", 5, 25, false, this, ui);
        ui.addComponent(saveb);
        UIButton debugb = new UIButton("Show debug information", "Show debug information,m", 5, 45, true, this, ui);
        if (debug != null) {
            debugb.setCondition(debug.displayDebug);
        }
        ui.addComponent(debugb);
        UIButton exitb = new UIButton("Exit game", "Exit game,m", 5, 5, false, this, ui);
        ui.addComponent(exitb);
        UIButton resreloadb = new UIButton("Reload textures", "Reload textures,m", 5, 65, false, this, ui);
        ui.addComponent(resreloadb);
        UIButton debugsizeb = new UIButton("Debug window", "Debug window,m", 5, 85, true, this, ui);
        debugsizeb.setCondition(debugwindow);
        ui.addComponent(debugsizeb);
        UIButton fullscreenb = new UIButton("Fullscreen mode", "Fullscreen mode,m", 5, 105, true, this, ui);
        fullscreenb.setCondition(getFullScreen());
        ui.addComponent(fullscreenb);
        UIButton deletesaveb = new UIButton("Delete save file", "Delete save file,m", 5, 125, false, this, ui);
        ui.addComponent(deletesaveb);
        UIButton buttoncontrolb = new UIButton("Mouse controls", "Toggle button controls,m", 5, 145, true, this, ui);
        buttoncontrolb.setCondition(buttonControls);
        ui.addComponent(buttoncontrolb);
        updateButtons();
        
        wb = new UIButton("W", "W,bc", 100, HEIGHT / SCALE - 100, true, this, ui);
        wb.setActive(false);
        ui.addComponent(wb);
        ab = new UIButton("A", "A,bc", 50, HEIGHT / SCALE - 50, true, this, ui);
        ab.setActive(false);
        ui.addComponent(ab);
        sb = new UIButton("S", "S,bc", 100, HEIGHT / SCALE - 75, true, this, ui);
        sb.setActive(false);
        ui.addComponent(sb);
        db = new UIButton("D", "D,bc", 125, HEIGHT / SCALE - 50, true, this, ui);
        db.setActive(false);
        ui.addComponent(db);
    }

    void updateButtons() {
        ui.getComponents().stream().filter((uc) -> (uc instanceof UIButton && uc.getID().split(":")[1].split(",")[1].equals("m"))).forEach((uc) -> {
            uc.setActive(paused);
        });
    }

    int getLevelID() {
        int lID = 4;
        try {
            loadEventData(lID);
        } catch (java.lang.NullPointerException | java.lang.NumberFormatException ex) {
            return getLevelID();
        }
        return lID;
    }

    private boolean[] switches = new boolean[2048];
    private int i = 0;
    public void questTest() {
        i = 0;
        if (p != null) {
            if (p.x >> Screen.SHIFT == 114 && p.y >> Screen.SHIFT == 102 && l.lID == 4) {
                p.getQuestHandler().loadQuest((byte)9);
            }
        }
    }
    
    private boolean showMessage(String message, int lID, boolean parameters) {
        if (l != null && l.lID == lID && parameters) {
            if (!switches[l.lID + i]) {
                Seshat.display(message, 12);
                switches[l.lID + i] = true;
                return true;
            }
        }
        
        return false;
    }
    
    
    @Override
    protected void tick() {
        f.requestFocusInWindow();
        
        if (ti != null && !paused && !Seshat.displaying() && !ui.dispInv && !ti.getActive()) {
            if (l != null) {
                l.tick();
            }
            Particle.tick();
            
            questTest();

            if (l != null && l.getWarping()) {
                loadMessages();
                char oldid = l.lID;
                char newid = l.getNewID();
                if (newid != 0) {
                    l = new Level(l.getNewID(), loadLevelData(l.getNewID()), loadOverlayData(l.getNewID()), loadEventData(l.getNewID()), loadWarps(l.getNewID()), loadSpawnPoints(l.getNewID()), loadLevelSize(l.getNewID())[0], loadLevelSize(l.getNewID())[1], debug);
                    qh.l = l;
                    l.showBounds = dispDebug;
                    l.addMob(p);
                    p.l = l;
                    try {
                        int coords[] = seekSpawnPoint(newid, oldid);
                        p.x = coords[0];
                        p.y = coords[1];
                    } catch (java.lang.NullPointerException ex) {
                        debug.printMessage(Debug.DebugType.ERROR, "MAIN", "Level " + newid + " does not exist", 5);
                    }
                    p.setGX(p.x);
                    p.setGY(p.y);
                    System.gc();
                } else if (oldid == 5 || oldid == 7) {
                    //l = Dungeon.generateDungeon();
                    l.showBounds = dispDebug;
                    l.addMob(p);
                    p.l = l;
                    for (int y = 0; y < l.height; y++) {
                        for (int x = 0; x < l.width; x++) {
                            if (l.spawnPoints[x + y * l.width] == 5) {
                                p.x = x << Screen.SHIFT;
                                p.y = y << Screen.SHIFT;
                                break;
                            }
                        }
                    }

                    p.setGX(p.x);
                    p.setGY(p.y);

                    System.gc();
                }
                loadNPCS((int) newid);
            }

        }

        ui.tick();
        Seshat.tick();

        if (Seshat.selected() && Seshat.mID == Messages.IDs[5]) {
            String[] o = {"Yes", "No"};
            Seshat.dealt();
            Seshat.display(o[Seshat.getSelection()], 2);
        } else if (Seshat.selected() && Seshat.mID == 0xFA9BCAD7) {
            if (Seshat.getSelection() == 0) {
                Seshat.dealt();
                stop();
            } else {
                Seshat.dealt();
            }
        }

        if (!chill && (f.getWidth() != 643 || f.getHeight() != 399)) {
            for (UIComponent uc : ui.getComponents()) {
                if (uc instanceof UIButton && uc.getID().split(":")[1].split(",")[1].equals("m") && uc.getID().split(":")[1].split(",")[0].equals("Debug window")) {
                    uc.sendMessage("setCondition:false");
                    debugwindow = false;
                    break;
                }
            }
            chill = true;
        }
    }

    @Override
    protected void draw(Graphics g) {
        if (screen != null) {
            if (l != null && p != null) {
                l.renderTiles(screen, p.x - ((screen.width / 2) - 8), p.y - ((screen.height / 2) - 8));
                l.showBounds = dispDebug;
                l.render(screen);
                l.renderOverlayTiles(screen, p.x - ((screen.width / 2) - 8), p.y - ((screen.height / 2) - 8));
            }
            Particle.render(screen);

            if (screen.tripping) {
                screen.trip(0);
            }

            for (int y = 0; y < screen.height; y++) {
                for (int x = 0; x < screen.width; x++) {
                    int colorCode = screen.pixels[x + y * screen.width];
                    if (colorCode < 255) {
                        pixels[x + y * image.getWidth()] = colorCode;
                    }
                }
            }
        }

        Graphics gs = image.getGraphics();
        Seshat.render(gs);
        if (!gameStarted) {
            gs.setColor(Color.BLACK);
            gs.fillRect(0, 0, WIDTH, HEIGHT);
        }
        ui.render(gs);

        if (dispDebug) {
            int x = 33;
            if (!gameStarted) x = 10;
            gs.setFont(gs.getFont().deriveFont(12f));
            gs.setColor(Color.WHITE);
            gs.drawString("VERSION " + VERSION, 1, x);
            x += 10;
            gs.drawString(String.valueOf("FPS: " + fps), 1, x);
            x += 10;
            if (p != null) {
                gs.drawString(String.valueOf("DIR: " + p.movingDir), 1, x);
                x += 10;
                gs.drawString(String.valueOf("X: " + p.x), 1, x);
                x += 10;
                gs.drawString(String.valueOf("Y: " + p.y), 1, x);
                x += 10;
                gs.drawString(String.valueOf("X TILE: " + (p.x >> Screen.SHIFT)), 1, x);
                x += 10;
                gs.drawString(String.valueOf("Y TILE: " + (p.y >> Screen.SHIFT)), 1, x);
                x += 10;
            }
            gs.drawString(String.valueOf("ALLOC: " + Runtime.getRuntime().maxMemory() / 1000000 + " MB"), 1, x);
            x += 10;
            gs.drawString(String.valueOf("FREE MEM: " + (Runtime.getRuntime().freeMemory() / 1000000) + " MB"), 1, x);
        }
        
        if (dg != null && dg.isGenerating()) {
            if (indx % 30 == 0) {
                switch (ps) {
                    case "Generating dungeon...":
                        ps = "Generating dungeon....";
                        break;
                    case "Generating dungeon.":
                        ps = "Generating dungeon..";
                        break;
                    case "Generating dungeon..":
                        ps = "Generating dungeon...";
                        break;
                    case "Generating dungeon....":
                        ps = "Generating dungeon.";
                        break;
                }
            }
            indx++;
            gs.setColor(Color.blue.darker().darker().darker().darker());
            gs.fillRect(image.getWidth() / 2 - 130 / 2, image.getHeight() / 2 - 28, 130, 35);
            FontMetrics fm = gs.getFontMetrics();
            int w = fm.stringWidth(ps);
            gs.setColor(Color.cyan);
            gs.drawString(ps, image.getWidth() / 2 - w / 2, image.getHeight() / 2 - 15);
            gs.setColor(Color.blue.darker().darker());
            gs.fillRect(image.getWidth() / 2 - 52, image.getHeight() / 2 - 12, 102, 10);
            gs.setColor(new Color(0x009AFF));
            gs.fillRect(image.getWidth() / 2- 50, image.getHeight() / 2 - 10, dg.getProgress()-1, 6);
        }

        g.drawImage(image, 0, 0, f.getWidth(), f.getHeight(), f);
        ui.renderText(g);
    }
    
    private int indx = 0;
    private String ps = "Generating dungeon....";
    
    public int[] loadLevelData(int LID) {
        try {
            BufferedImage img = ImageGetter.read(new File(System.getProperty("user.home") + "/Library/Application Support/Juegito/levels/" + String.valueOf(LID) + "/" + String.valueOf(LID) + "_tle.png").toURI().toURL())[0];
            int[] temp = new int[img.getWidth() * img.getHeight()];
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    for (int i = 0; i < Tile.getTiles().length; i++) {
                        if (Tile.getTile(i) != null && Tile.getTile(i).getColor() == img.getRGB(x, y)) {
                            temp[x + y * img.getWidth()] = i;
                            break;
                        }
                    }
                }
            }
            return temp;
        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public int[] loadOverlayData(int LID) {
        try {
            BufferedImage img = ImageGetter.read(new File(System.getProperty("user.home") + "/Library/Application Support/Juegito/levels/" + String.valueOf(LID) + "/" + String.valueOf(LID) + "_ovr.png").toURI().toURL())[0];
            int[] temp = new int[img.getWidth() * img.getHeight()];
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    for (int i = 0; i < Tile.getTiles().length; i++) {
                        if (Tile.getTile(i) != null && Tile.getTile(i).getColor() == img.getRGB(x, y)) {
                            temp[x + y * img.getWidth()] = i;
                            break;
                        }
                    }
                }
            }
            return temp;
        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return null;
    }

    public int[] loadLevelSize(int LID) {
        try {
            BufferedImage img = ImageGetter.read(new File(System.getProperty("user.home") + "/Library/Application Support/Juegito/levels/" + String.valueOf(LID) + "/" + String.valueOf(LID) + "_tle.png").toURI().toURL())[0];
            int[] temp = {img.getWidth(), img.getHeight()};
            return temp;
        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return null;
    }

    public char[] loadEventData(int lID) {
        try {
            return getData(new File(System.getProperty("user.home") + "/Library/Application Support/Juegito/levels/" + String.valueOf(lID) + "/" + String.valueOf(lID) + "_evt.dat").toURI().toURL().openStream());
        } catch (MalformedURLException | java.io.FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return null;
    }

    public char[] loadWarps(int lID) {
        try {
            return getData(new File(System.getProperty("user.home") + "/Library/Application Support/Juegito/levels/" + String.valueOf(lID) + "/" + String.valueOf(lID) + "_wrp.dat").toURI().toURL().openStream());
        } catch (MalformedURLException | java.io.FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return null;
    }

    public char[] loadSpawnPoints(int lID) {
        try {
            return getData(new File(System.getProperty("user.home") + "/Library/Application Support/Juegito/levels/" + String.valueOf(lID) + "/" + String.valueOf(lID) + "_spn.dat").toURI().toURL().openStream());
        } catch (MalformedURLException | java.io.FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return null;
    }

    public int[] seekSpawnPoint(char lID, char oldid) {
        try {

            char[] sp = getData(new File(System.getProperty("user.home") + "/Library/Application Support/Juegito/levels/" + String.valueOf((int)lID) + "/" + String.valueOf((int)lID) + "_spn.dat").toURI().toURL().openStream());

            int w = loadLevelSize(lID)[0];
            int h = loadLevelSize(lID)[1];

            int[] coordinates = new int[2];

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (sp[x + y * w] == oldid) {
                        coordinates[0] = x << Screen.SHIFT;
                        coordinates[1] = y << Screen.SHIFT;
                        return coordinates;
                    }
                }
            }
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (sp[x + y * w] == 1) {
                        coordinates[0] = x << Screen.SHIFT;
                        coordinates[1] = y << Screen.SHIFT;
                        return coordinates;
                    }
                }
            }
            return coordinates;
        } catch (MalformedURLException | java.io.FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return null;
    }

    public static char[] getData(InputStream i) {
        Scanner scanner = null;
        scanner = new Scanner(i, "UTF-8");

        String s = "ERROR";
        try {
            s = scanner.nextLine();
            while (scanner.hasNextLine()) {
                s += scanner.nextLine();
            }
            scanner.close();
        } catch (java.util.NoSuchElementException ex) {

        }
        return s.toCharArray();
    }

    private void preProcessCommand(String in) {
        int errorCode = processCommand(in);
        if (errorCode != 0) {
            String message = "";
            if (errorCode == 1) message = "Unrecognized command: " + in;
            if (errorCode == 2) message = "Invalid or missing parameter";

            debug.printMessage(Debug.DebugType.ERROR, "MAIN", "ERROR CODE: " + errorCode + "; " + message, 5);
        }
    }

    private int processCommand(String in) {
        if (in.contains("give")) {
            if (in.split(":").length > 1) {
                try {
                    if (in.split(":").length > 2) {
                        l.addEntity(new DroppedItem(p.x, p.y, Statc.random(5, 0), Integer.parseInt(in.split(":")[2]), false, l, debug, Item.getItem(Integer.parseInt(in.split(":")[1]))));
                        return 0;
                    } else {
                        l.addEntity(new DroppedItem(p.x, p.y, Statc.random(5, 0), 1, false, l, debug, Item.getItem(Integer.parseInt(in.split(":")[1]))));
                    }
                } catch (java.lang.NullPointerException | java.lang.ArrayIndexOutOfBoundsException ex) {
                    return 2;
                } catch (java.lang.NumberFormatException ex) {
                    if (in.split(":").length > 2) {
                        l.addEntity(new DroppedItem(p.x, p.y, Statc.random(5, 0), Integer.parseInt(in.split(":")[2]), false, l, debug, Item.getItem(in.split(":")[1])));
                    } else {
                        l.addEntity(new DroppedItem(p.x, p.y, Statc.random(5, 0), 1, false, l, debug, Item.getItem(in.split(":")[1])));
                    }
                }
                return 0;
            } else {
                return 2;
            }
        } else if (in.contains("summon:stalker:")) {
            int yMin = (p.y >> Screen.SHIFT) - 10;
            int yMax = (p.y >> Screen.SHIFT) + 10;
            int xMin = (p.x >> Screen.SHIFT) - 10;
            int xMax = (p.x >> Screen.SHIFT) + 10;
            for (int i = 0; i < Integer.parseInt(in.split(":")[2]); i++) {
                int x = Statc.random(xMax, xMin);
                int y = Statc.random(yMax, yMin);
                Stalker st = new Stalker(x << Screen.SHIFT, y << Screen.SHIFT, 0, true, l);
                st.addXP(p.getTotalXP() + p.getTotalXP() / 10 + 5);
                l.addMob(st);
            }
            return 0;
        } else if (in.contains("remove")) {
            if (in.split(":").length > 1) {
                if (in.split(":")[1].equalsIgnoreCase("dropped")) {
                    l.getEntities().stream().filter((e) -> (e instanceof DroppedItem)).forEach((e) -> {
                        e.setActive(false);
                    });
                } else if (in.split(":")[1].equalsIgnoreCase("mob")) {
                    l.getMobs().stream().forEach((m) -> {
                        if (in.split(":").length > 2 && in.split(":")[2].equalsIgnoreCase("player")) {
                            if (!(m instanceof Player)) m.setActive(false);
                        } else {
                            m.setActive(false);
                        }
                    });
                } else {
                    return 2;
                }
                return 0;
            } else {
                return 2;
            }
        } else if (in.contains("clearinv")) {
            p.clearInventory();
            return 0;
        } else if (in.contains("addxp")) {
            if (in.split(":").length > 1) {
                p.addXP(Integer.parseInt(in.split(":")[1]));
                return 0;
            } else {
                return 2;
            }
        } else if (in.contains("clearxpl")) {
            p.addXP(-p.getXP());
            p.setTotalXP(0);
            p.setLevel(1);
            p.resetXTLU();
            return 0;
        } else if (in.contains("addhp")) {
            if (in.split(":").length > 1) {
                if (Integer.parseInt(in.split(":")[1]) > 0) {
                    p.addHP(Integer.parseInt(in.split(":")[1]));
                } else {
                    p.subtractHP(-Integer.parseInt(in.split(":")[1]), null);
                }
                return 0;
            } else {
                return 2;
            }
        } else if (in.equalsIgnoreCase("rp")) {
            p.addHP(100);
            p.setActive(true);
            return 0;
        } else if (in.contains("warp")) {
            if (in.split(":").length > 1) {
                try {
                    l.warp((char) Byte.parseByte(in.split(":")[1]));
                    if (!p.canMove()) p.enableMovement();
                } catch (java.lang.NullPointerException ex) {
                    return 2;
                }
                return 0;
            } else {
                return 2;
            }
        } else if (in.contains("trip")) {
            if (in.split(":").length > 1) {
                long duration = 0;
                try {
                    duration = Long.parseLong(in.split(":")[1]) * 3600;
                } catch (java.lang.NumberFormatException ex) {
                    return 2;
                }
                screen.trip(duration);
                return 0;
            } else {
                return 2;
            }
        } else if (in.contains("createnpc")) {
            if (in.split(":").length > 1) {
                String[] commandlistUP = in.split(":")[1].split("#");
                int[] commandlistP = new int[commandlistUP.length];
                int intervalCount = 0;

                for (int i = 0; i < commandlistP.length; i++) {
                    commandlistP[i] = Integer.valueOf(commandlistUP[i], 16);
                    if (commandlistP[i] == 0xFF) intervalCount++;
                }

                NPC n = new NPC(p.x, ((p.y >> Screen.SHIFT) - 3) << Screen.SHIFT, p.getLevel(), false, (int) System.nanoTime(), commandlistP, intervalCount, 0, 0, l, p, "null");
                l.addMob(n);
                return 0;
            } else {
                return 2;
            }
        } else if (in.contains("hermes:deliver")) {
            if (in.split(":").length > 1) {
                String msg = in.split(":")[2];
                Hermes.deliver(msg);
                return 0;
            } else {
                return 2;
            }
        } else if (in.contains("override")) {
            try {
                //debug_clear();
                fatal = false;
                l = null;
                p = null;
                scanSave(Text.getFile("/Library/Application Support/Juegito/Save/" + 0 + ".dat").toURI().toURL().openStream(), false);
                if (l != null) l.addMob(p);
            } catch (MalformedURLException ex) {
                Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            debug.printMessage(Debug.DebugType.INFO, NAME + "_MAIN", "Checksum invalidity ignored, save file has (probably) been read", 5);
            return 0;
        } else if (in.contains("take")) {
            if (in.split(":").length > 2) {
                p.dropItem(Integer.parseInt(in.split(":")[1]), Integer.parseInt(in.split(":")[2]));
                return 0;
            } else {
                return 2;
            }
        } else if (in.contains("open skill tree")) {
            ut.setActive(true);
            return 0;
        } else if (in.contains("close")) {
            if (ut.getActive()) {
                ut.setActive(false);
                return 0;
            } else {
                return 2;
            }
        } else if (in.contains("start server")) {
            int port = 0;
            if (in.contains(":")) {
                port = Integer.parseInt(in.split(":")[1]);
            }
            
            try {
                new HostServer(port, debug).start();
                debug.printPlainMessage("Server started on port " + port, 5);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        } else if (in.contains("connect to host")) {
            GameClient gc = new GameClient(in.split(":")[1], Integer.parseInt(in.split(":")[2]), debug);
            debug.printPlainMessage("Connected to " + in.split(":")[1] + " with responce " + gc.sendPacket("connect"), 5);
        } else if (in.contains("generate dungeon")) {
            dg = new DungeonGenerator();
            dg.setPlayer(p);
            if (!in.contains(":")) {
                dg.init();
                dg.setDungeonGeneratorLoader(this);
                debug.printMessage(Debug.DebugType.INFO, "MAIN", "Dungeon generated successfully", 5);
            } else {
                int[] size = {Integer.parseInt(in.split(":")[1]), Integer.parseInt(in.split(":")[2])};
                dg.init(size[0], size[1]);
                dg.setDungeonGeneratorLoader(this);
            }
            return 0;
        }

        return 1;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }
    
    private BufferedImage hideCursor = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        c.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(hideCursor, new Point(0, 0), ""));

        //if (key == KeyEvent.VK_C) //debug_clear();

        if (key == KeyEvent.VK_T && !ti.getActive()) ti.setActive(true);
        if (key == KeyEvent.VK_ENTER && ti.getActive()) {
            ti.setActive(false);
            preProcessCommand(ti.getInput());
            ti.clearInput();
        }
        if (key == KeyEvent.VK_F3) {
            dispDebug = !dispDebug;
            if (l != null) {
                l.showBounds = dispDebug;
            }
        }
        
        if (ti != null && !ti.getActive()) {
            if (key == KeyEvent.VK_I) {
                ui.dispInv = !ui.dispInv;
                if (ui.dispCrafting) ui.dispCrafting = false;
            }
            
            if (key == KeyEvent.VK_Q) {
                ui.dispCrafting = !ui.dispCrafting;
                if (ui.dispInv) ui.dispInv = false;
            }

            if (key == KeyEvent.VK_ESCAPE) {
                if (!ui.dispCrafting && !ui.dispInv) {
                    paused = !paused;
                } else if (ui.dispCrafting) {
                    ui.dispCrafting = false;
                } else if (ui.dispInv) {
                    ui.dispInv = false;
                }
                updateButtons();
            }

            if (key == KeyEvent.VK_F4) {
                ui.showXPLU = !ui.showXPLU;
            }

            

            if (key == KeyEvent.VK_SPACE) {
                p.noCollide = !p.noCollide;
            }

            if (key == KeyEvent.VK_E && p != null && l != null && Seshat.displaying()) {
                Seshat.nextPage();
            }

            if (key == KeyEvent.VK_E && !ui.dispInv && p != null && l != null) {
                int x = 0;
                int y = 0;
                int px = p.x >> Screen.SHIFT;
                int py = p.y >> Screen.SHIFT;
                if (p.movingDir == 0) {
                    x = px;
                    y = py - 1;
                } else if (p.movingDir == 1) {
                    x = px;
                    y = py + 1;
                } else if (p.movingDir == 2) {
                    x = px - 1;
                    y = py;
                } else if (p.movingDir == 3) {
                    x = px + 1;
                    y = py;
                }

                p.useEquipped = !Seshat.displaying() && !l.interact(x, y) && !paused && p.getHP() > 0;
                p.renderEquipped = p.useEquipped;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && buttonControls) {
            this.keyPressed(new KeyEvent(c, 1, 20, 1, KeyEvent.VK_E, 'e'));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            float scalex = ((float) c.getWidth()) / ((float) (Main.width / Main.SCALE));
            float scaley = ((float) c.getHeight()) / ((float) (Main.height / Main.SCALE));
            int mx = (int) (e.getX() / scalex);
            int my = (int) (e.getY() / scaley);
            mx += screen.getXOffset();
            my += screen.getYOffset();

            BoundingBox b = new BoundingBox(mx, my, 6, 6);
            for (Mob m : l.getMobs()) {
                if (m.getActive() && m.getBounds().intersects(b)) {
                    ui.setSource(m);
                    break;
                }
            }
        }
        
        if (e.getButton() == MouseEvent.BUTTON1 && buttonControls) {
            this.keyReleased(new KeyEvent(c, 1, 20, 1, KeyEvent.VK_E, 'e'));
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }
    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void scanSave(InputStream is, boolean considerChecksum) {
        char[] ins = getData(is);

        boolean checks = considerChecksum && !Save.checksum(ins, debug);

        int check = 0;
        if (checks || ins[0] != Save.TYPE_HEAD) {
            check = ins.length;
            fatal = true;
            useDebug();
            debug.displayDebug = true;
            debug.printMessage(Debug.DebugType.ERROR, NAME + "_main", "Save file missing header or checksum failed", 5);
            debug.printPlainMessage("ERROR: The save file is corrupt. The game will stop in ten seconds.", 5);
        }

        char lID = 0xFF;
        for (int i = check; i < ins.length; i++) {
            try {
                if (ins[i] == Save.TYPE_LEVEL) {
                    lID = (char) (ins[i + 1] >> 4);
                    l = new Level((char) lID, loadLevelData(lID), loadOverlayData(lID), loadEventData(lID), loadWarps(lID), loadSpawnPoints(lID), loadLevelSize(lID)[0], loadLevelSize(lID)[1], debug);
                    qh = new QuestHandler(debug, l);
                }

                if (ins[i] == Save.TYPE_OPT) {
                    if (ins[i + 1] == 0x01) {
                        if (debug != null) {
                            debug.displayDebug = ins[i + 2] == 0x01;
                        }

                        if (ins[i + 3] == 0x01 && !getFullScreen()) {
                            toggleFullScreen();
                        } else if (ins[i + 3] == 0x00 && getFullScreen()) {
                            toggleFullScreen();
                        }

                        if (!getFullScreen()) {
                            f.setSize((int) ins[i + 4], (int) ins[i + 5]);
                            f.setLocationRelativeTo(null);
                        }

                        if (ins[i + 6] == 0x01) {
                            debugwindow = true;
                            f.setSize(643, 399);
                        }
                        
                        buttonControls = ins[i + 7] == 0x01;

                        lastW = (int) ins[i + 4];
                        lastH = (int) ins[i + 5];
                    }
                }

                if (ins[i] == Save.TYPE_PLR) {
                    if (p == null && ins[i + 1] == 0xA0) {
                        p = new Player(((int) ins[i + 2] >> 4) << Screen.SHIFT, ((int) ins[i + 3] >> 4) << Screen.SHIFT, (int) (ins[i + 5] >> 4), l, new KeyIN(f), qh);
                        p.movingDir = (int) ins[i + 4] - 16;
                        p.setLevel(0);
                        p.resetXTLU();
                        p.setActLevel((int) (ins[i + 5] >> 4));

                        String xpString = "";
                        for (int j = i + 7; j < ins.length; j++) {
                            if (ins[j] == ';') {
                                i = j + 1;
                                break;
                            }
                            xpString += ins[j];
                        }
                        p.loadXP(Integer.parseInt(xpString));
                        
                        String hpString = "";
                        for (int j = i; j < ins.length; j++) {
                            if (ins[j] == ';') {
                                i = j + 1;
                                break;
                            }
                            hpString += ins[j];
                        }
                        p.addHP(Integer.parseInt(hpString) - p.getMaxHP());
                        String maxHpString = "";
                        for (int j = i; j < ins.length; j++) {
                            if (ins[j] == ';') {
                                i = j + 1;
                                break;
                            }
                            maxHpString += ins[j];
                        }
                        p.setMaxHP(Integer.parseInt(maxHpString));
                        String manaString = "";
                        for (int j = i; j < ins.length; j++) {
                            if (ins[j] == ';') {
                                i = j + 1;
                                break;
                            }
                            manaString += ins[j];
                        }
                        p.setMana(Integer.parseInt(manaString));
                        String baseManaString = "";
                        for (int j = i; j < ins.length; j++) {
                            if (ins[j] == ';') break;
                            baseManaString += ins[j];
                        }
                        p.setBaseMana(Integer.parseInt(baseManaString));
                        if (ui == null) {
                            ui = new UIHandler(c);
                            ui.setSource(p);
                        }
                    }
                    if (p != null) {
                        if (ins[i + 1] == 0xA1) {
                            for (int j = i + 2; j < ins.length; j += 4) {
                                if (ins[j] == 0xED) {
                                    i += p.getInventory().size() * 4;
                                    break;
                                }
                                int abd = ins[j + 2];
                                if (ins[j + 1] == 0x21) abd >>= 4; 
                                p.addItem(Item.getItem(ins[j] >> 4), abd);
                                int eqp = ins[j + 3] >> 4;
                                if (eqp != 0) p.equip((j - i - 2) / 4);
                            }
                        }
                        if (ins[i + 1] == 0xA2) {

                        }
                        if (ins[i + 1] == 0xA3) {
                            
                        }
                        if (ins[i + 1] == 0xA4) {
                            
                        }
                        if (ins[i + 1] == 0xA5) {
                            
                        }
                    }
                }

                if (ins[i] == Save.TYPE_ENT) {
                    if (ins[i + 1] == 0x71) {
                        l.addEntity(new DroppedItem(ins[i + 2] >> 4, ins[i + 3] >> 4, Statc.random(5, 0), ins[i + 4] >> 4, false, l, debug, Item.getItem(ins[i + 5] >> 4)));
                    }
                }

                if (ins[i] == Save.TYPE_MOB) {
                    if (ins[i + 1] == 0x72) {
                        Player pl = null;
                        if (ins[i + 7] == 0x00) {
                            pl = p;
                        }
                        Stalker s = new Stalker(ins[i + 2] >> 4, ins[i + 3] >> 4, 0, false, l);
                        s.setTartget(pl);
                        s.movingDir = ins[i + 4] >> 4;
                        s.setActLevel((int) (ins[i + 5] >> 4));
                        
                        if (ins[i + 9] == 0xEE) {
                            i += 10;
                            for (int j = i; j < ins.length; j += 4) {
                                if (ins[j] == 0xED) {
                                    i = j;
                                    break;
                                }
                                int abd = ins[j + 2];
                                if (ins[j + 1] == 0x21) abd >>= 4; 
                                s.addItem(Item.getItem(ins[j] >> 4), abd);
                                int eqp = ins[j + 3] >> 4;
                                if (eqp != 0) s.equip((j - i) / 4);
                            }
                        }
                        
                        String xpString = "";
                        for (int j = i + 1; j < ins.length; j++) {
                            if (ins[j] == ';') {
                                i = j + 1;
                                break;
                            }
                            xpString += ins[j];
                        }
                        s.loadXP(Integer.parseInt(xpString));
                        String hpString = "";
                        for (int j = i; j < ins.length; j++) {
                            if (ins[j] == ';') {
                                i = j + 1;
                                break;
                            }
                            hpString += ins[j];
                        }
                        s.addHP(Integer.parseInt(hpString) - s.getMaxHP());
                        String maxHpString = "";
                        for (int j = i; j < ins.length; j++) {
                            if (ins[j] == ';') {
                                i = j + 1;
                                break;
                            }
                            maxHpString += ins[j];
                        }
                        s.setMaxHP(Integer.parseInt(maxHpString));
                        String manaString = "";
                        for (int j = i; j < ins.length; j++) {
                            if (ins[j] == ';') {
                                i = j + 1;
                                break;
                            }
                            manaString += ins[j];
                        }
                        s.setMana(Integer.parseInt(manaString));
                        String baseManaString = "";
                        for (int j = i; j < ins.length; j++) {
                            if (ins[j] == ';') break;
                            baseManaString += ins[j];
                        }
                        s.setBaseMana(Integer.parseInt(baseManaString));

                        l.addMob(s);
                    } else if (ins[i + 1] == 0x88) {
                        int cb = 0;
                        String scriptid = "";
                        for (int j = i + 12; i < ins.length; j++) {
                            if (ins[j] == ';') {
                                cb = j + 1;
                                break;
                            }
                            scriptid += ins[j];
                        }

                        String filePath = System.getProperty("user.home") + "/Library/Application Support/Juegito/NPC_DEFAULTS/" + scriptid + "/" + scriptid + ".npc";
                        char[] buffer = null;
                        try {
                            buffer = getData(new File(filePath).toURI().toURL().openStream());
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                            return;
                        } catch (IOException ex) {
                            Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                            return;
                        }
                        
                        int[] commands = new int[buffer.length];
                        int responseIntervals = 0;
                        boolean clearedVersion = false;
                        String version = "";
                        int b = 0;
                        for (int a = 0; a < commands.length; a++) {
                            if (buffer[a] == ';') {
                                clearedVersion = true;
                                a++;
                            }
                            if (clearedVersion) {
                                commands[b] = (int) (buffer[a]);
                                if (commands[b] == 0xFF || commands[b] == 0x0F) {
                                    responseIntervals++;
                                }
                                b++;
                            } else {
                                version += (char) (buffer[a]);
                            }
                        }
                        if (!version.equals(NPC.CODEVERSION)) debug.printMessage(Debug.DebugType.WARNING, "MAIN_INIT", "NPC " + 0 + " is outdated", 5);

                        NPC npc = new NPC(ins[i + 2] >> 4, ins[i + 3] >> 4, 0, false, 0, commands, responseIntervals, (int) ins[i + 8] >> 4, (int) ins[i + 10] >> 4, l, p, scriptid);
                        npc.setOverallInterval((int) ins[i + 9] >> 4);
                        npc.movingDir = ins[i + 4] >> 4;
                        npc.setActLevel((int) (ins[i + 5] >> 4));
                        if (ins[i + 11] == 0x01) {
                            npc.interact();
                        }
                        
                        if (ins[cb] == 0xEE) {
                            i = cb + 1;
                            for (int j = i; j < ins.length; j += 4) {
                                if (ins[j] == 0xED) {
                                    i = j;
                                    break;
                                }
                                int abd = ins[j + 2];
                                if (ins[j + 1] == 0x21) abd >>= 4; 
                                npc.addItem(Item.getItem(ins[j] >> 4), abd);
                                int eqp = ins[j + 3] >> 4;
                                if (eqp != 0) npc.equip((j - i) / 4);
                            }
                        }
                        
                        String xpString = "";
                        for (int j = i + 1; j < ins.length; j++) {
                            if (ins[j] == ';') {
                                i = j + 1;
                                break;
                            }
                            xpString += ins[j];
                        }
                        npc.loadXP(Integer.parseInt(xpString));
                        String hpString = "";
                        for (int j = i; j < ins.length; j++) {
                            if (ins[j] == ';') {
                                i = j + 1;
                                break;
                            }
                            hpString += ins[j];
                        }
                        npc.addHP(Integer.parseInt(hpString) - npc.getMaxHP());
                        String maxHpString = "";
                        for (int j = i; j < ins.length; j++) {
                            if (ins[j] == ';') {
                                i = j + 1;
                                break;
                            }
                            maxHpString += ins[j];
                        }
                        npc.setMaxHP(Integer.parseInt(maxHpString));
                        String manaString = "";
                        for (int j = i; j < ins.length; j++) {
                            if (ins[j] == ';') {
                                i = j + 1;
                                break;
                            }
                            manaString += ins[j];
                        }
                        npc.setMana(Integer.parseInt(manaString));
                        String baseManaString = "";
                        for (int j = i; j < ins.length; j++) {
                            if (ins[j] == ';') break;
                            baseManaString += ins[j];
                        }
                        npc.setBaseMana(Integer.parseInt(baseManaString));
                        
                        l.addMob(npc);
                    }
                }
            } catch (java.lang.ArrayIndexOutOfBoundsException | java.lang.NumberFormatException ex) {
                System.err.println("Error while reading save: " + ex.getMessage() + "\nat byte " + (i) + ":");
                Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }

            //Soteria.notifyResponce();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand().split(",")[0]) {
            case "Save game":
                Save s = new Save("0", debug);
                char[] data = new char[6];
                if (debug.displayDebug) data[0] = (char) 0x01;
                if (getFullScreen()) data[1] = (char) 0x01;
                data[2] = (char) f.getWidth();
                data[3] = (char) f.getHeight();
                if (debugwindow) data[4] = (char) 0x01;
                if (buttonControls) data[5] = (char) 0x01;
                s.writeToBuffer(Save.TYPE_OPT, (char) 0x01, data);
                s.writeLevel(p, l);
                s.save();
                break;
            case "Show debug information":
                debug.displayDebug = !debug.displayDebug;
                break;
            case "Exit game":
                if (l != null && l.lID == 0) {
                    String[] o = {"Yes", "No"};
                    Seshat.display("Are you sure you want to exit? When you&nre-enter the game, you will have woken&nup and left the dungeon.", 2, o);
                    Seshat.mID = 0xFA9BCAD7;
                } else {
                    stop();
                }
                break;
            case "Reload textures":
                screen.sheet = new SpriteSheet(debug);
                for (Item item : Item.items) {
                    if (item != null) {
                        item.loadIcon();
                    }
                }
                break;
            case "Debug window":
                if (debugwindow) {
                    debugwindow = false;
                    if (lastW == -1) {
                        toggleFullScreen();
                        for (UIComponent uc : ui.getComponents()) {
                            if (uc instanceof UIButton && uc.getID().split(":")[1].split(",")[1].equals("m") && uc.getID().split(":")[1].split(",")[0].equals("Fullscreen mode")) {
                                uc.sendMessage("setCondition:true");
                                break;
                            }
                        }
                    } else f.setSize(lastW, lastH);
                } else {
                    debugwindow = true;
                    if (this.getFullScreen()) {
                        toggleFullScreen();
                        lastW = -1;
                        for (UIComponent uc : ui.getComponents()) {
                            if (uc instanceof UIButton && uc.getID().split(":")[1].split(",")[1].equals("m") && uc.getID().split(":")[1].split(",")[0].equals("Fullscreen mode")) {
                                uc.sendMessage("setCondition:false");
                                break;
                            }
                        }
                    } else {
                        lastW = f.getWidth();
                        lastH = f.getHeight();
                    }
                    f.setSize(643, 399);
                }
                chill = false;
                break;
            case "Fullscreen mode":
                this.toggleFullScreen();
                break;
            case "Delete save file":
                if (Save.exists()) {
                    Save.delete();
                    debug.printPlainMessage("The save file was deleted", 5);
                } else {
                    debug.printPlainMessage("There is no save file", 5);
                }
                break;
            case "NEW GAME":
                newgb.setActive(false);
                loadgb.setActive(false);
                exitgb.setActive(false);
                startGame(true);
                break;
            case "LOAD GAME":
                newgb.setActive(false);
                loadgb.setActive(false);
                exitgb.setActive(false);
                startGame(false);
                break;
            case "Toggle button controls":
                buttonControls = !buttonControls;
                ui.buttonControls = buttonControls;
                break;
            case "W":
                p.getKeyIn().w.toggle(!p.getKeyIn().w.isPressed());
                break;
        }
    }

    public Debug getDebug() {
        return debug;
    }

    public static void gc(Debug d) {
        if (d != null) {
            d.printMessage(Debug.DebugType.INFO, "_" + "_STATIC", "Free memory before garbage collector: " + (double) (Runtime.getRuntime().freeMemory() / 1048576) + " MB", 5);
        }
        System.gc();
        if (d != null) {
            d.printMessage(Debug.DebugType.INFO, "_" + "_STATIC", "Free memory after garbage collector: " + (double) (Runtime.getRuntime().freeMemory() / 1048576)  + " MB", 5);
        }
    }
    
    public static BufferedImage toCompatibleImage(BufferedImage image) {
        // obtain the current system graphical settings
        GraphicsConfiguration gfx_config = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();

        /*
         * if image is already compatible and optimized for current system 
         * settings, simply return it
         */
        if (image.getColorModel().equals(gfx_config.getColorModel())) {
            return image;
        }

        // image is not optimized, so create a new image that is
        BufferedImage new_image = gfx_config.createCompatibleImage(
                image.getWidth(), image.getHeight(), image.getTransparency());

        // get the graphics context of the new image to draw the old image on
        Graphics2D g2d = (Graphics2D) new_image.getGraphics();

        // actually draw the image and dispose of context no longer needed
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // return the new optimized image
        return new_image;
    }
    
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void loadDungeon(Dungeon d) {
        this.l = d;
        this.qh.l = d;
    }
}
