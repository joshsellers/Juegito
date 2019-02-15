package juegito.ui;

import com.amp.pre.Debug;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joshsellers
 */
public abstract class UIComponent {
    
    protected Canvas c;
    
    protected int x, y;
    protected int width, height;
    
    protected boolean active;
    
    protected Rectangle bounds;
    
    protected UIComponent parent;
    protected List<UIComponent> children = new ArrayList<>();
    
    private String ID;
    
    protected boolean textSync = false;
    
    protected boolean overrideBoundCoordsPersistance;
    protected boolean overrideBoundsPersistance;
    
    public UIComponent(int x, int y, int width, int height, String ID, Canvas c) {
        this.c = c;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        
        this.ID = ID;
        
        bounds = new Rectangle(x, y, width, height);
    }
    
    public void superTick() {
        if (!overrideBoundCoordsPersistance) {
            bounds.x = x;
            bounds.y = y;
        }
        if (!overrideBoundsPersistance) {
            bounds.width = width;
            bounds.height = height;
        }
        tick();
    }
    
    public abstract void tick();
    public abstract void render(Graphics g);
    public void renderText(Graphics g) {};
    
    protected abstract void mousePressed(MouseEvent e);
    protected abstract void mouseReleased(MouseEvent e);
    public void mouseDragged(MouseEvent e) {};
    
    public void setActive(boolean active) {
        this.active = active;
        getChildren().stream().forEach((e) -> {
            e.active = active;
        });
    }
    
    public boolean getActive() {
        return active;
    }
    
    public List<UIComponent> getChildren() {
        return this.children;
    }
    
    public void addChild(UIComponent e) {
        this.getChildren().add(e);
        e.setParent(this);
    }
    
    private void setParent(UIComponent e) {
        this.parent = e;
    }
    
    public UIComponent getParent() {
       return parent;
    }
    
    public String getID() {
        return ID;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public boolean textSync() {
        return textSync;
    }
    
    public abstract void sendMessage(String message);
}
