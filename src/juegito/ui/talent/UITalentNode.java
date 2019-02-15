package juegito.ui.talent;

import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import juegito.ui.*;

/**
 *
 * @author joshsellers
 */
public class UITalentNode extends UIButton {
    
    protected int period;
    protected int index;
    private String description;
    
    private UITalentTree tt;

    public UITalentNode(String title, int x, int y, int index, int period, ActionListener al, UIHandler ui, UITalentTree tt) {
        super(title, title + "tnode", x, y, false, al, ui);
        
        this.index = index;
        this.period = period;
        
        this.tt = tt;
        
        width = 16;
        height = 16;
        
        textSync = true;
        
        overrideBoundCoordsPersistance = true;
    }
    
    @Override
    public void tick() {
        
    }
    
    @Override
    public void render(Graphics g) {
        int mx = ui.getMouseX();
        int my = ui.getMouseY();
        
        g.setColor(Color.BLUE);
        g.fillRect(bounds.x + tt.dispx, bounds.y + tt.dispy, bounds.width, bounds.height);
    }

    @Override
    public void renderText(Graphics g) {
        int mx = ui.getRealMouseX();
        int my = ui.getRealMouseY();

        if (new java.awt.Rectangle(ui.getMouseX(), ui.getMouseY(), 1, 1).intersects(bounds)) {
            String[] lines = description.split("&n");
            
            int w = 0;
            int h = 0;
            FontMetrics fm = g.getFontMetrics();

            int longest = 0;
            for (String line : lines) {
                if (fm.stringWidth(line) > longest) {
                    longest = fm.stringWidth(line);
                }
            }   
            w = longest;
            h = 14 * (lines.length) + 6;
            
            g.setColor(new Color(0xFFFFAA).darker().darker());
            g.fillRect(mx - w / 2 - 2, my - h - 2 - 2, w + 8, h + 4);
            g.setColor(new Color(0xFFFFAA));
            g.fillRect(mx - w / 2, my - h - 2, w + 4, h);
            
            g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(Font.BOLD));
            g.drawString(title, mx + 2 - w / 2, my + 12 - h - 2);
            g.setFont(g.getFont().deriveFont(Font.PLAIN));
            for (int i = 0; i < lines.length; i++) {
                g.drawString(lines[i], mx + 2 - w / 2, (my + 12 * (i + 2)) - h - 2);
            }
        }
    }
    
    public void setDescription(String text) {
        description = text;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public void sendMessage(String message) {
        
    }
    
}
