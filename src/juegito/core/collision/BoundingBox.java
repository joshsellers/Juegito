package juegito.core.collision;

import juegito.gfx.Screen;

/**
 *
 * @author joshsellers
 */
public class BoundingBox {
    
    public int x;
    public int y;
    public int width;
    public int height;
    
    public BoundingBox(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public boolean intersects(BoundingBox b) {
        int tw = this.width;
        int th = this.height;
        int rw = b.width;
        int rh = b.height;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        int tx = this.x;
        int ty = this.y;
        int rx = b.x;
        int ry = b.y;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;

        return ((rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty || th > ry));
    }
    
    public void render(Screen s, int color) {
        int xPos = x-s.getXOffset();
        int yPos = y-s.getYOffset();
        
        drawLine(s, xPos, yPos, xPos + width, yPos, color);
        drawLine(s, xPos, yPos, xPos, yPos + height, color);
        drawLine(s, xPos, yPos + height, xPos + width + 1, yPos + height, color);
        drawLine(s, xPos + width, yPos, xPos + width, yPos + height + 1, color);
    }
    
    private void drawLine(Screen s, int x0, int y0, int x1, int y1, int color) {
        if (x0 == x1) x1++;
        if (y0 == y1) y1++;
        for (int ya = y0; ya < y1 && ya < s.height && ya > 0; ya++) {
            for (int xa = x0; xa < x1 && xa < s.width && xa > 0; xa++) {
                s.pixels[xa + ya * s.width] = color;
            }
        }
    }
}
