package juegito.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

/**
 *
 * @author joshsellers
 */
public class UIIndicator extends UIComponent {

    private boolean indication;
        
    public UIIndicator(int x, int y, Canvas c) {
        super(x, y, 10, 10, "indicator", c);
    }

    @Override
    public void tick() {}

    @Override
    public void render(Graphics g) {
        if (indication) g.setColor(Color.GREEN);
        else g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
        g.setColor(g.getColor().darker().darker());
        g.drawRect(x, y, width, height);
    }

    public boolean getIndication() {
        return indication;
    }

    public void setIndication(boolean indication) {
        this.indication = indication;
    }

    @Override
    protected void mousePressed(MouseEvent e) {}

    @Override
    protected void mouseReleased(MouseEvent e) {}

    @Override
    public void sendMessage(String message) {

    }
    
}
