package juegito.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import juegito.core.Main;

/**
 *
 * @author joshsellers
 */
public class UITextInput extends UIComponent implements KeyListener {
    
    private String input = "";
    
    private int charssincestart;
    
    private int background = Color.BLACK.getRGB();
    private int foreground = Color.GREEN.getRGB();

    public UITextInput(Canvas c, JFrame f) {
        super(f.getWidth() / 2, f.getHeight() / 2, 0, 0, "textin", c);
        f.addKeyListener(this);
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics g) {
        g.setFont(g.getFont().deriveFont(12f));
        FontMetrics m = g.getFontMetrics();
        width = m.stringWidth(input) + 4;
        height = 14;
        
        x = c.getWidth() / 2;
        y = c.getHeight() / 2;
        
        float scalex = ((float) c.getWidth()) / ((float) (Main.width/Main.SCALE));
        float scaley = ((float) c.getHeight()) / ((float) (Main.height/Main.SCALE));

        x = (int) (x / scalex) - width / 2 - 2;
        y = (int) (y / scaley) - 7 - 2;
        
        g.setColor(new Color(background));
        g.fillRect(x, y, width, height);
        g.setColor(new Color(((background & 0xFF0000) >> 24) + 0x55, ((background & 0x00FF00) >> 16) + 0x55, ((background & 0x0000FF) >> 8) + 0x55));
        g.drawRect(x, y, width, height);
        g.setColor(new Color(foreground));
        g.drawString(input, x + 2, y + 12);
    }
    
    @Override
    public void setActive(boolean active) {
        charssincestart = 0;
        this.active = active;
    }
    
    public void setBackground(int color) {
        this.background = color;
    }
    
    public void getForeground(int color) {
        this.foreground = color;
    }
    
    public String getInput() {
        return input;
    }
    
    public void clearInput() {
        input = "";
    }

    @Override
    protected void mousePressed(MouseEvent e) {

    }

    @Override
    protected void mouseReleased(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (active) {
            if (charssincestart == 0 && e.getKeyCode() == KeyEvent.VK_T) {
                charssincestart++;
            } else if (String.valueOf(e.getKeyChar()).matches("[ \\w\\#:-]")) {
                input += e.getKeyChar();
            }
            
            if (active && !input.equals("") && e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                input = input.substring(0, input.length() - 1);
            }
        }
    }

    @Override
    public void sendMessage(String message) {

    }
    
}
