package juegito.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 *
 * @author joshsellers
 */
public class UIButton extends UIComponent {
    
    protected UIHandler ui;
    protected ActionListener al;
    protected String title;
    protected String actionCommand;
    
    protected boolean pressed;
    private boolean toggler;
    private boolean condition;
    
    protected BufferedImage image;
    public boolean useImage;
    
    private UIIndicator ind;

    public UIButton(String title, String actionCommand, int x, int y, boolean toggler, ActionListener al, UIHandler ui) {
        super(x, y, 0, 14, "button:" + actionCommand, ui.c);
        this.ui = ui;
        this.al = al;
        this.title = title;
        if (actionCommand != null) {
            this.actionCommand = actionCommand;
        } else {
            this.actionCommand = title;
        }
        this.toggler = toggler;
        
        if (this.toggler) {
            ind = new UIIndicator(0, y, ui.c);
            addChild(ind);
            ui.addComponent(ind);
        }
    }

    @Override
    public void tick() {
        if (toggler) ind.setIndication(condition);
    }

    @Override
    public void render(Graphics g) {
        g.setFont(g.getFont().deriveFont(12f));
        if (!useImage && width == 0 && title != null) {
            FontMetrics m = g.getFontMetrics();
            width = m.stringWidth(title) + 4;
            bounds.width = width;
            
            if (toggler) ind.x = width + 10;
        }
        
        int yOffset = bounds.y;
        if (pressed) yOffset += 3;
        g.setColor(Color.BLUE);
        g.fillRect(bounds.x, yOffset, bounds.width, bounds.height);
        g.setColor(Color.BLUE.darker().darker());
        g.drawRect(bounds.x, yOffset, bounds.width, bounds.height);
        if (!useImage) {
            g.setColor(Color.WHITE);
            g.drawString(title, x + 2, yOffset + 12);        
        } else if (image != null) {
            g.drawImage(image, x + 1, yOffset + 1, null);
        }
    }
    
    public void setCondition(boolean condition) {
        this.condition = condition;
    }
    
    public void setImage(BufferedImage image) {
        this.image = image;
        width = image.getWidth() + 1;
        height = image.getHeight() + 1;
    }

    @Override
    protected void mousePressed(MouseEvent e) {
        pressed = true;
    }

    @Override
    protected void mouseReleased(MouseEvent e) {
        pressed = false;
        if (al != null) al.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionCommand));
        if (toggler) condition = !condition;
    }

    @Override
    public void sendMessage(String message) {
        if (message.contains("setCondition")) {
            setCondition(message.split(":")[1].equals("true"));
        }
    }
}
