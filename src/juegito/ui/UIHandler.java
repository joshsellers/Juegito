package juegito.ui;

import com.amp.pre.Debug;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.event.MouseInputListener;
import juegito.core.DebugListener;
import juegito.core.InventoryListener;
import juegito.core.Main;
import juegito.entities.Mob;
import juegito.level.items.Item;
import juegito.level.items.StoredItem;

/**
 *
 * @author joshsellers
 */
public class UIHandler implements MouseInputListener, MouseWheelListener, ActionListener, InventoryListener, DebugListener {
    
    public final static int SCROLL_SPEED = 4;
    
    protected Canvas c;
    private Mob source;
        
    private List<UIComponent> components = new ArrayList<>();
    
    private BufferedImage cursor;
    
    private Rectangle mouseBounds = new Rectangle(0, 0, 1, 1);
    
    private List<StoredItem> viewBuffer = new ArrayList<>();
    private List<Rectangle> invb = new ArrayList<>();
    
    public boolean showXPLU;
    
    public boolean dispInv;
    public boolean dispCrafting;
    private int invY = 16;
    private int yOffset;
    private int xOffset;
    
    private int disp = -2;
    
    private int globalMouseX;
    private int globalMouseY;
    private int globalMouseXOnScreen;
    private int globalMouseYOnScreen;
    private int mouseDraggedX;
    private int mouseDraggedY;
    
    private UIButton tomainb = new UIButton("Back", "Back,UIH", 10, 10, false, this, this);
    private UIButton allb = new UIButton("All items", "All items,UIH", 0, 0, false, this, this);
    private UIButton weaponsb = new UIButton("Weapons", "Weapons,UIH", 0, 0, false, this, this);
    private UIButton ammob = new UIButton("Ammunition", "Ammunition,UIH", 0, 0, false, this, this);
    private UIButton foodb = new UIButton("Food", "Food,UIH", 0, 0, false, this, this);
    private UIButton armorb = new UIButton("Armor", "Armor,UIH", 0, 0, false, this, this);
    
    private List<UIButton> craftingButtons = new ArrayList<>();
    
    public boolean buttonControls;
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public UIHandler(Canvas c) {
        this.c = c;
        c.addMouseListener(this);
        c.addMouseMotionListener(this);
        c.addMouseWheelListener(this);

        try {
            cursor = ImageIO.read(Main.class.getResource("res/cursor.png"));
            c.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0, 0), ""));
        } catch (IOException ex) {
            Logger.getLogger(UIHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int centerX = (Main.width / Main.SCALE) / 2;
        int centerY = (Main.height / Main.SCALE) / 2 - 8;
        
        int craftableCount = 0;
        for (Item item : Item.items) {if (item.isCraftable()) craftableCount++;}
        int uiWidth = craftableCount * 16 + (4 *craftableCount);
        int uiX = centerX - (uiWidth / 2);
        int j = 0;
        for (Item item : Item.items) {
            if (item.isCraftable()) {
                UIButton b = new UIButton(item.getName(), String.valueOf(item.getID()) + ",UIH,craft", uiX + (20 * j), centerY, false, this, this);
                addComponent(b);
                b.setImage(item.getIcon());
                b.useImage = true;
                craftingButtons.add(b);
                j++;
            }
        }

        allb.setPosition((Main.width / Main.SCALE / 2) - allb.getWidth() / 2, (Main.height / Main.SCALE / 2) - allb.getHeight() / 2);
        addComponent(allb);
        BufferedImage weaponsicn = new BufferedImage(Item.SWORD_BROAD.getIcon().getWidth(), Item.SWORD_BROAD.getIcon().getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < weaponsicn.getHeight(); y++) {
            for (int x = 0; x < weaponsicn.getWidth(); x++) {
                if (Item.SWORD_BROAD.getIcon().getRGB(x, y) == 0x00000000) {
                    weaponsicn.setRGB(x, y, Color.BLUE.darker().darker().darker().getRGB());
                } else {
                    weaponsicn.setRGB(x, y, Color.BLUE.getRGB());
                }
            }
        }
        weaponsb.setImage(weaponsicn);
        weaponsb.useImage = true;
        weaponsb.setPosition((Main.width / Main.SCALE / 2) - weaponsb.getWidth() / 2, (Main.height / Main.SCALE / 2) - (allb.getHeight() + allb.getHeight() / 2 + 10));
        addComponent(weaponsb);
        addComponent(tomainb);
        BufferedImage ammoicn = new BufferedImage(Item.ARROW_BASIC.getIcon().getWidth(), Item.ARROW_BASIC.getIcon().getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < ammoicn.getHeight(); y++) {
            for (int x = 0; x < ammoicn.getWidth(); x++) {
                if (Item.ARROW_BASIC.getIcon().getRGB(x, y) == 0x00000000) {
                    ammoicn.setRGB(x, y, Color.BLUE.darker().darker().darker().getRGB());
                } else {
                    ammoicn.setRGB(x, y, Color.BLUE.getRGB());
                }
            }
        }
        ammob.setImage(ammoicn);
        ammob.useImage = true;
        addComponent(ammob);
        BufferedImage foodicn = new BufferedImage(Item.APPLE.getIcon().getWidth(), Item.APPLE.getIcon().getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < foodicn.getHeight(); y++) {
            for (int x = 0; x < foodicn.getWidth(); x++) {
                if (Item.APPLE.getIcon().getRGB(x, y) == 0x00000000) {
                    foodicn.setRGB(x, y, Color.BLUE.darker().darker().darker().getRGB());
                } else {
                    foodicn.setRGB(x, y, Color.BLUE.getRGB());
                }
            }
        }
        foodb.setImage(foodicn);
        foodb.useImage = true;
        foodb.setPosition((Main.width / Main.SCALE / 2) - foodb.getWidth() / 2, allb.y + allb.getHeight() + 9);
        addComponent(foodb);
        BufferedImage armoricn = new BufferedImage(Item.HELMET_IRON.getIcon().getWidth(), Item.HELMET_IRON.getIcon().getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < armoricn.getHeight(); y++) {
            for (int x = 0; x < armoricn.getWidth(); x++) {
                if (Item.HELMET_IRON.getIcon().getRGB(x, y) == 0x00000000) {
                    armoricn.setRGB(x, y, Color.BLUE.darker().darker().darker().getRGB());
                } else {
                    armoricn.setRGB(x, y, Color.BLUE.getRGB());
                }
            }
        }
        armorb.setImage(armoricn);
        armorb.useImage = true;
        addComponent(armorb);
    }
    
    public void tick() {
        try {
            getComponents().stream().filter((uc) -> (uc.active)).forEach((uc) -> {
                uc.superTick();
            });
        } catch (ConcurrentModificationException ex) {
            if (source != null) {
                source.l.getDebug().printMessage(Debug.DebugType.ERROR, "UIHandler", ex.getLocalizedMessage(), 0);
            }
        }
        
        int neutralArea = 45;
        if (source instanceof juegito.level.Player && buttonControls) {
            juegito.level.Player p = (juegito.level.Player) source;
            if (this.getMouseY() < Main.height / Main.SCALE / 2 - neutralArea) {
                p.getKeyIn().w.toggle(true);
                p.getKeyIn().s.toggle(false);
            } else if (this.getMouseY() > Main.height / Main.SCALE / 2 + neutralArea) {
                p.getKeyIn().s.toggle(true);
                p.getKeyIn().w.toggle(false);
            } else {
                p.getKeyIn().s.toggle(false);
                p.getKeyIn().w.toggle(false);
            }
            
            if (this.getMouseX() < Main.width / Main.SCALE / 2 - neutralArea) {
                p.getKeyIn().a.toggle(true);
                p.getKeyIn().d.toggle(false);
            } else if (this.getMouseX() > Main.width / Main.SCALE / 2 + neutralArea) {
                p.getKeyIn().d.toggle(true);
                p.getKeyIn().a.toggle(false);
            } else {
                p.getKeyIn().d.toggle(false);
                p.getKeyIn().a.toggle(false);
            }
        }
    }

    public void render(Graphics g) {
        renderSrcStats(g);
        
        if (dispInv && source != null) {
            invY = ((Main.height / Main.SCALE / 2) - ((16 + 2) * viewBuffer.size()) / 2) - yOffset;
            
            if (disp > -2) {
                int baseX = Main.width / Main.SCALE / 2 - 8;
                int padding = 2;
                for (int i = 0; i < viewBuffer.size(); i++) {
                    StoredItem si = viewBuffer.get(i);
                    g.setColor(Color.BLUE.darker().darker().darker());
                    g.fillRect(baseX - padding, invY + (16 + padding) * i - padding, 16 + padding * 2, 16 + padding * 2);
                    g.setColor(Color.BLUE);
                    g.fillRect(baseX, invY + (16 + padding) * i, 16, 16);
                    g.drawImage(si.getItem().getIcon(), baseX, invY + (16 + padding) * i, null);
                    if (si.equippedAs != 0) {
                        g.setColor(Color.CYAN);
                        g.fillRect(baseX - 8 - padding, invY + (16 + padding) * i + 6, 4, 4);
                    }
                    g.setColor(Color.WHITE);
                    g.setFont(g.getFont().deriveFont(12f));
                    String nameTxt = si.getItem().getName();
                    if (si.getAbundance() > 1) {
                        nameTxt += " (" + String.valueOf(si.getAbundance()) + ")";
                    }
                    g.drawString(nameTxt, baseX + 16 + padding + 2, invY + (16 + padding) * i + 12);
                }
            } else {
                yOffset = 0;
                updateBounds();
                
                allb.setPosition((Main.width / Main.SCALE / 2) - allb.getWidth() / 2, (Main.height / Main.SCALE / 2) - allb.getHeight() / 2);
                ammob.setPosition(allb.x - ammob.getWidth() - 10, (Main.height / Main.SCALE / 2) - ammob.getHeight() / 2);
                armorb.setPosition(allb.x + allb.width + 10, (Main.height / Main.SCALE / 2) - armorb.getHeight() / 2);
                allb.setActive(true);
                weaponsb.setActive(true);
                ammob.setActive(true);
                foodb.setActive(true);
                armorb.setActive(true);
            }
        } else {
            yOffset = 0;
            updateBounds();
            
            tomainb.setActive(false);
            allb.setActive(false);
            weaponsb.setActive(false);
            ammob.setActive(false);
            foodb.setActive(false);
            armorb.setActive(false);
            setDisp(-2);
        }
        //invx = ((Main.width / Main.SCALE / 2) - (20 * (Player.INVENTORY_SIZE / (Player.INVENTORY_SIZE / 10))) / 2);
        
        craftingButtons.forEach((ub) -> {ub.setActive(dispCrafting);});
        
        try {
            getComponents().stream().filter((uc) -> (uc.active)).forEach((uc) -> {
                uc.render(g);
            });
        } catch (ConcurrentModificationException ex) {
            if (source != null) {
                source.l.getDebug().printMessage(Debug.DebugType.ERROR, "UIHandler", ex.getLocalizedMessage(), 0);
            }
        }
        
        if (dispCrafting) {
            int centerX = (Main.width / Main.SCALE) / 2;
            int centerY = (Main.height / Main.SCALE) / 2;
            
            g.setColor(Color.blue.brighter());
            g.drawString("Crafting", centerX - 170, centerY - 64);
            
            craftingButtons.stream().filter((ub) -> (mouseBounds.intersects(ub.bounds))).map((ub) -> Item.getItem(Integer.parseInt(ub.actionCommand.split(",")[0])).getName()).forEachOrdered((itemName) -> {
                String req = "Requires ";
                for (int[] mat : Item.getItem(itemName).getRecipe()) {
                    req += mat[1] + " " + Item.getItem(mat[0]).getName() + " ";
                }
                FontMetrics fm = g.getFontMetrics();
                int reqLen = fm.stringWidth(req);
                int nameLen = fm.stringWidth(itemName);
                int len = reqLen;
                if (nameLen > reqLen) len = nameLen;
                g.setColor(new Color(0xffffca));
                g.fillRect(mouseBounds.x - len / 2, mouseBounds.y +5, len + 6, 30);
                g.setColor(new Color(0x000023));
                g.drawString(itemName, mouseBounds.x + 3 - len / 2, mouseBounds.y + 13 + 5);
                g.drawString(req, mouseBounds.x + 3 - len / 2, mouseBounds.y + 23 + 5);
            });
        }
        
        for (int i = 0; i < notifications.length; i++) {
            if (source != null && notifications[i] != null && !source.leveledUp()) {
                Notification n = notifications[i];
                g.setColor(Color.white);
                g.setFont(g.getFont().deriveFont(12));
                if (n.active) g.drawString(n.message, 5, 30 + (12 * i));
            }
        }
    }
    
    public void renderText(Graphics g) {
        getComponents().stream().filter((uc) -> (uc.active && uc.textSync())).forEach((uc) -> {
            uc.renderText(g);
        });
    }
    
    private void renderSrcStats(Graphics g) {
        if (source != null) {
            FontMetrics m = g.getFontMetrics();
            String s = String.valueOf("LEVEL: " + source.getLevel() + " XP: " + source.getTotalXP());
            int w = m.stringWidth(s);
            int dispX = ((((Main.width / Main.SCALE / 2) - w / 2) / 2) - (w / 2) / 2) - 3;
            if (source.leveledUp()) {
                g.setColor(Color.CYAN);
            } else {
                g.setColor(Color.GREEN);
            }
            g.drawString(s, dispX, 10);
            if (showXPLU) {
                g.drawString(String.valueOf("XP to level up: " + (source.getXPToLevelUp() - source.getXP())), 5, 34);
            }
            
            int padWidth = (Main.width / Main.SCALE - 8) / 2;
            int fillWidth = (int)(((float)(source.getXP()) / ((float)(source.getLevel() * Mob.LEVEL_XP_SCALE) / 100f)) * ((float)(padWidth - 3f) / 100f));
            g.setColor(Color.GREEN.darker().darker());
            g.fillRect(1, 12, padWidth, 10);
            g.setColor(Color.GREEN);
            g.fillRect(2, 13, fillWidth, 8);
            
            
            String sa = String.valueOf("HP: " + source.getHP() + "/" + source.getMaxHP());
            int wa = m.stringWidth(sa);
            int dispXa = ((Main.width / Main.SCALE / 2) + 2);
            if (source.leveledUp()) {
                g.setColor(Color.CYAN);
            } else {
                g.setColor(Color.RED);
            }
            g.drawString(sa, (Main.width / Main.SCALE) - ((padWidth / 2) + (wa / 2)), 10);
            
            int fillWidtha = (int)(((float)(source.getHP()) / ((float)(source.getMaxHP()) / 100f)) * ((float)(padWidth - 2f) / 100f));
            g.setColor(Color.RED.darker().darker());
            g.fillRect(dispXa, 12, padWidth, 10);
            g.setColor(Color.RED);
            g.fillRect(dispXa + 1, 13, fillWidtha, 8);
            
            if (source.getMana() < source.getBaseMana() || source.leveledUp()) {
                String sb = String.valueOf("ARCANA: " + source.getMana() + "/" + source.getBaseMana());
                int wb = m.stringWidth(sb);
                int dispXb = ((((Main.width / Main.SCALE / 2) - wb / 2) / 2) - (wb / 2) / 2) - 3 + (Main.width / Main.SCALE / 2) - padWidth / 2;
                Color manaCol = new Color(0x99FFEF);
                if (source.leveledUp()) {
                    g.setColor(Color.CYAN);
                } else {
                    g.setColor(manaCol);
                }
                g.drawString(sb, dispXb, 34);

                int fillWidthb = (int) (((float) (source.getMana()) / ((float) (source.getBaseMana()) / 100f)) * ((float) (padWidth - 1f) / 100f));
                g.setColor(manaCol.darker().darker());
                g.fillRect((Main.width / Main.SCALE / 2) - padWidth / 2, 36, padWidth, 10);
                g.setColor(manaCol);
                g.fillRect((Main.width / Main.SCALE / 2) - padWidth / 2 + 1, 37, fillWidthb, 8);
            }
            
            if (source.leveledUp()) {
                g.setFont(g.getFont().deriveFont(45f));
                m = g.getFontMetrics();
                w = m.stringWidth("LEVEL UP");
                g.drawString("LEVEL UP", (Main.width / Main.SCALE / 2) - w / 2, 80);
            }
        }
    }
    
    public void setDisp(int disp) {
        if (source != null) {
            this.disp = disp;
            invb.clear();
            viewBuffer.clear();
            source.getInventory().stream().filter((si) -> (si.getItem().getType() == disp || disp == -1)).forEach((si) -> {
                viewBuffer.add(si);
                boundsUp();
            });
        }
    }
    
    public void setSource(Mob m) {
        if (source != null) source.removeInventoryListener(this);
        source = m;
        source.addInventoryListener(this);
    }
    
    public Mob getSource() {
        return source;
    }
    
    public synchronized List<UIComponent> getComponents() {
        return components;
    }
    
    public synchronized void addComponent(UIComponent c) {
        this.getComponents().add(c);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (dispInv && source != null && disp != -2) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                for (int i = 0; i < invb.size(); i++) {
                    if (mouseBounds.intersects(invb.get(i))) {
                        int a = 0;
                        for (int j = 0; j < source.getInventory().size(); j++) {
                            if (source.getInventory().get(j).equals(viewBuffer.get(i))) {
                                a = j;
                                break;
                            }
                        }
                        source.equip(a);
                        break;
                    }
                }
            } else {
                for (int i = 0; i < invb.size(); i++) {
                    if (mouseBounds.intersects(invb.get(i))) {
                        int a = 0;
                        for (int j = 0; j < source.getInventory().size(); j++) {
                            if (source.getInventory().get(j).equals(viewBuffer.get(i))) {
                                a = j;
                                break;
                            }
                        }
                        if (e.isShiftDown()) {
                            source.dropItem(a, source.getInventory().get(a).getAbundance());
                        } else {
                            source.dropItem(a, 1);
                        }
                        break;
                    }
                }
            }
        }

        getComponents().stream().filter((uc) -> (mouseBounds.intersects(uc.bounds))).filter((uc) -> (uc.active)).forEach((uc) -> {
            uc.mousePressed(e);
        });
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        try {
            getComponents().stream().filter((uc) -> (mouseBounds.intersects(uc.bounds))).filter((uc) -> (uc.active)).forEach((uc) -> {
                uc.mouseReleased(e);
            });
        } catch (java.util.ConcurrentModificationException ex) {
            if (source != null) {
                source.l.getDebug().printMessage(Debug.DebugType.ERROR, "UIHANDLER_MOUSERELEASED", "ConcurrentModificationException", 5);
            }
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
        float scalex = ((float) c.getWidth()) / ((float) (Main.width/Main.SCALE));
        float scaley = ((float) c.getHeight()) / ((float) (Main.height/Main.SCALE));

        mouseDraggedX = (int) (e.getX() / scalex);
        mouseDraggedY = (int) (e.getY() / scaley);
        
        for (UIComponent uc : getComponents()) {
            if (uc.active) {
                uc.mouseDragged(e);
            }
        }
    }
    
    private Robot r = null;
    @Override
    public void mouseMoved(MouseEvent e) {
        try {
            c.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0, 0), ""));
        } catch (java.lang.NullPointerException ex) {
            //This is lazy but it doesn't seem to affect performance I just wanted the expection to stop showing up in the output
        }
        
        float scalex = ((float) c.getWidth()) / ((float) (Main.width/Main.SCALE));
        float scaley = ((float) c.getHeight()) / ((float) (Main.height/Main.SCALE));

        mouseBounds.x = (int) (e.getX() / scalex);
        mouseBounds.y = (int) (e.getY() / scaley);
        
        globalMouseX = mouseBounds.x;
        globalMouseY = mouseBounds.y;
        
        globalMouseXOnScreen = e.getX();
        globalMouseYOnScreen = e.getY();
        
        //if (source != null) source.l.getDebug().printMessage(Debug.DebugType.INFO, "UI", String.valueOf(mouseBounds.x + " " + mouseBounds.y), 1);
        
//        if (r == null) {
//            try {
//                r = new Robot();
//            } catch (AWTException ex) {
//                Logger.getLogger(UIHandler.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } else if (mouseBounds.y < 5) {
//            r.mouseMove(e.getX(), 20);
//        }
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (source != null && dispInv) {
            yOffset += e.getWheelRotation() * SCROLL_SPEED;
            updateBounds();
        }
        if (source != null && dispCrafting) {
            craftingButtons.forEach((ub) -> {
                ub.setPosition(ub.x - e.getWheelRotation() * SCROLL_SPEED, ub.y);
            });    
        }
    }
    
    private StoredItem searchSourceInv(int id, int amount) {
        for (StoredItem i : source.getInventory()) {
            if (i.getItem().getID() == (byte) id && i.getAbundance() >= amount) {
                return i;
            }
        }
        return new StoredItem(Item.NULL, Item.NOT_EQUIPPED, 0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().contains("craft")) {
            Item item = Item.getItem(Integer.parseInt(e.getActionCommand().split(",")[0]));
            StoredItem[] ingredients = new StoredItem[item.getRecipe().length];
            int[] amtsUsed = new int[ingredients.length];
            int matches = 0;
            for (int[] recipe : item.getRecipe()) {
                StoredItem foundItem = searchSourceInv(recipe[0], recipe[1]);
                if (foundItem.getItem().getID() != Item.NULL.getID()) {
                    ingredients[matches] = foundItem;
                    amtsUsed[matches] = recipe[1];
                    matches++;
                }
            }
            if (matches == item.getRecipe().length) {
                for (int i = 0; i < ingredients.length; i++) {
                    ingredients[i].take(amtsUsed[i]);
                }
                source.addItem(item, 1);
            }
        }
        
        if (e.getActionCommand().split(",")[1].equals("UIH")) {
            switch (e.getActionCommand().split(",")[0]) {
                case "All items":
                    setDisp(-1);
                    tomainb.setActive(true);
                    allb.setActive(false);
                    weaponsb.setActive(false);
                    ammob.setActive(false);
                    foodb.setActive(false);
                    armorb.setActive(false);
                    break;
                case "Weapons":
                    setDisp(1);
                    tomainb.setActive(true);
                    allb.setActive(false);
                    weaponsb.setActive(false);
                    ammob.setActive(false);
                    foodb.setActive(false);
                    armorb.setActive(false);
                    break;
                case "Ammunition":
                    setDisp(4);
                    tomainb.setActive(true);
                    allb.setActive(false);
                    weaponsb.setActive(false);
                    ammob.setActive(false);
                    foodb.setActive(false);
                    armorb.setActive(false);
                    break;
                case "Food":
                    setDisp(3);
                    tomainb.setActive(true);
                    allb.setActive(false);
                    weaponsb.setActive(false);
                    ammob.setActive(false);
                    foodb.setActive(false);
                    armorb.setActive(false);
                    break;
                case "Armor":
                    setDisp(2);
                    tomainb.setActive(true);
                    allb.setActive(false);
                    weaponsb.setActive(false);
                    ammob.setActive(false);
                    foodb.setActive(false);
                    armorb.setActive(false);
                    break;
                case "Back":
                    setDisp(-2);
                    tomainb.setActive(false);
                    allb.setActive(true);
                    weaponsb.setActive(true);
                    ammob.setActive(true);
                    foodb.setActive(true);
                    armorb.setActive(true);
                    break;
            }
        } else if (Seshat.rva(e.getActionCommand().split(",")[0], Integer.parseInt(e.getActionCommand().split(",")[1]))) {
            Seshat.set(Integer.parseInt(e.getActionCommand().split(",")[1]));
        }
    }

    @Override
    public void inventorySizeChanged() {
        setDisp(disp);
    }
    
    private void updateBounds() {
        int invYb = ((Main.height / Main.SCALE / 2) - ((16 + 2) * viewBuffer.size()) / 2) - yOffset;
        
        int baseX = Main.width / Main.SCALE / 2 - 8;
        int padding = 2;
        for (int i = 0; i < invb.size(); i++) {
            invb.get(i).x = baseX - padding;
            invb.get(i).y = invYb + (16 + padding) * i - padding;
        }
    }
    
    private synchronized void boundsUp() {
        int padding = 2;
        this.invb.add(new Rectangle(0, 0, 16 + padding * 2, 16 + padding * 2));
        updateBounds();
    }
    
    private synchronized void boundsDown() {
        this.invb.remove(invb.size() - 1);
        updateBounds();
    }

    @Override
    public void itemAdded(StoredItem item, int abundance) {

    }

    @Override
    public void itemRemoved(StoredItem item, int abundance) {

    }
    
    public int getMouseX() {
        return globalMouseX;
    }
    
    public int getMouseY() {
        return globalMouseY;
    }

    public int getRealMouseX() {
        return globalMouseXOnScreen;
    }
    
    public int getRealMouseY() {
        return globalMouseYOnScreen;
    }
    
    public int getMouseDraggedX() {
        return mouseDraggedX;
    }
    
    public int getMouseDraggedY() {
        return mouseDraggedY;
    }

    private Notification[] notifications = new Notification[5];
    private Timer timer = new Timer();
    @Override
    public void messageIntercepted(String message) {
        for (int i = notifications.length - 1; i > 0; i--) {
            notifications[i] = notifications[i-1];
        }
        notifications[0] = new Notification(message, timer);
    }
    
    private class Notification extends TimerTask {
        
        public boolean active = true;
        public String message;
        
        public Notification(String message, Timer t) {
            this.message = message;
            t.schedule(this, 3000);
        }

        @Override
        public void run() {
            active = false;
        }
        
    }
}
