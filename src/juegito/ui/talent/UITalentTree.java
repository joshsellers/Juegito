package juegito.ui.talent;

import com.amp.pre.Debug;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import juegito.core.Main;
import juegito.ui.*;

/**
 *
 * @author joshsellers
 */
public class UITalentTree extends UIComponent {
    
    private UIHandler ui;
    
    protected int dispx;
    protected int dispy;

    public UITalentTree(UIHandler ui, Canvas c) {
        super(0, 0, c.getWidth() / Main.SCALE, c.getHeight() / Main.SCALE, "talenttreeui", c);
        
        this.ui = ui;
        
        textSync = true;
    }

    @Override
    public void tick() {
        getChildren().stream().filter((child) -> (child.getActive())).forEach((child) -> {
            child.superTick();
        });
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.blue.darker().darker());
        g.fillRect(0, 0, width, height);
        getChildren().stream().filter((child) -> (child.getActive())).forEach((child) -> {
            child.render(g);
        });
    }
    
    @Override
    public void renderText(Graphics g) {
        getChildren().stream().filter((child) -> (child.getActive() && child.textSync())).forEach((child) -> {
            child.renderText(g);
        });
    }
    
    int lastX;
    int lastY;
    @Override
    public void mouseDragged(MouseEvent e) {
        float scalex = ((float) c.getWidth()) / ((float) (Main.width/Main.SCALE));
        float scaley = ((float) c.getHeight()) / ((float) (Main.height/Main.SCALE));

        int mx = (int) (e.getX() / scalex);
        int my = (int) (e.getY() / scaley);
        
        dispx += -mx + lastX;
        dispy += -my + lastY;
        
        lastX = mx;
        lastY = my;
        
        ui.getSource().l.getDebug().printMessage(Debug.DebugType.INFO, "", String.valueOf(dispx + " " + dispy), 1);
    }

    @Override
    protected void mousePressed(MouseEvent e) {
        
    }

    @Override
    protected void mouseReleased(MouseEvent e) {

    }

    @Override
    public void sendMessage(String message) {

    }
    
}
