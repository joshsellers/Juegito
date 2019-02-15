package juegito.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import juegito.core.Main;

/**
 *
 * @author joshsellers
 */
public class Seshat {
    
    private static Graphics gfx;
    
    private static String text = "";
    private static int page = 0;
    
    private static boolean display = false;
    
    private static int lineSpacing;
    
    
    private static int x = 0;
    private static int y = Main.height/Main.SCALE-76;
    private static int width = Main.width/Main.SCALE - 1;
    private static int height = 75;
    
    public static UIHandler ui;
    
    private static UIButton[] options;
    private static String[] ot;
    private static boolean selected;
    private static int selection;
    
    private static BufferedImage dialog;
    
    public static int mID;

    public static void tick() {
        if (ui != null && display) {
            ui.tick();
        }
    }
    
    public static void render(Graphics g) {
        gfx = g;
        
        if (display && ui != null) {      
            ui.render(g);
            
            try {
                dialog = ImageIO.read(Main.class.getResource("res/dialog.png"));
                /*g.setColor(Color.WHITE);
                g.fillRoundRect(x, y, width, height, 15, 15);
                g.setColor(Color.GRAY);
                g.drawRoundRect(x, y, width, height, 15, 15);*/
                
                g.drawImage(dialog, x + (Main.width/Main.SCALE) / 2 - dialog.getWidth() / 2, y, null);
            } catch (IOException ex) {
                Logger.getLogger(Seshat.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            g.setFont(g.getFont().deriveFont(12f));
            
            int yOffset = y+25;
            g.setColor(Color.BLACK);
            
            String[] t = text.split("&n");
            int a = 0;
            for (int i = 0; i < 5; i++) {
                if (i+page < t.length) {
                    a++;
                    g.drawString(t[i+page], x+15+ (Main.width/Main.SCALE) / 2 - dialog.getWidth() / 2, yOffset+lineSpacing*i);
                }
            }
            if (a == 0) nextPage();
        }
    }
    
    public static void display(String text, int linespacing) {
        Seshat.lineSpacing = linespacing + 10;
        Seshat.text = text;
        display = true;
    }

    public static void display(String text, int linespacing, String[] options) {
        Seshat.lineSpacing = linespacing + 10;
        Seshat.text = text;
        
        Seshat.options = new UIButton[options.length];
        ot = options;
        
        FontMetrics m = gfx.getFontMetrics();
        
        for (int i = 0; i < Seshat.options.length; i++) {
            int w = m.stringWidth(ot[i]);
            Seshat.options[i] = new UIButton(ot[i], String.valueOf(ot[i] + "," + i), width - (w + 8), (y - 20 * ot.length) + 15 * i, false, ui, ui);
            ui.addComponent(Seshat.options[i]);
            Seshat.options[i].setActive(true);
        }
        
        display = true;
    }
    
    public static void nextPage() {
        if (page < text.split("&n").length) page += 5;
        else {
            if (options != null) for (UIButton option : options) option.setActive(false);
            ot = null;
            hide();
            page = 0;
        }
    }
    
    public static void hide() {
        display = false;
    }
    
    public static boolean displaying() {
        return display;
    }
    
    public static boolean selected() {
        return selected;
    }
    
    public static int getSelection() {
        return selection;
    }
    
    public static void dealt() {
        selected = false;
    }
    
    protected static boolean rva(String response, int index) {
        return response.equals(ot[index]);
    }
    
    protected static void set(int selection) {
        selected = true;
        Seshat.selection = selection;
        for (UIButton option : options) option.setActive(false);
        ot = null;
        hide();
    }
}
