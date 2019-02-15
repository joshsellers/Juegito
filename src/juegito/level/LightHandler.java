package juegito.level;

/**
 *
 * @author joshsellers
 */
public class LightHandler {
    
    protected Level l;
    public int[] light;
    public int[] lightColor;
    
    public LightHandler(Level l) {
        light = new int[l.width * l.height];
        lightColor = new int[l.width * l.height];
        this.l = l;
    }
    
    public enum LightType {
        NULL, TYPE_SUN, TYPE_FIRE, TYPE_PORTAL
    }
    
    public void updateLighting() {
        for (int y = 0; y < l.height; y++) {
            for (int x = 0; x < l.width; x++) {
                lightColor[x + y * l.width] = 0;
//                if (l.getTile(x, y).isEmitter()) {
//                    int strength = light[x + y * l.width];
//                    int col = 0;
//                    if (l.getTile(x, y).getLightType() == LightType.TYPE_SUN) {
//                        light[x + y * l.width] = 1;
//                        col = 0x000000;
//                    }
//                    if (l.getTile(x, y).getLightType() == LightType.TYPE_FIRE) {
//                        light[x + y * l.width] = 5;
//                        col = 0x00FF00;
//                    }
//                    if (l.getTile(x, y).getLightType() == LightType.TYPE_PORTAL) {
//                        light[x + y * l.width] = 3;
//                        col = 0x4D0650;
//                    }
//                    for (int ya = (y - strength + 1); ya < (y + strength) && ya > 0 && ya < l.height; ya++) {
//                        for (int xa = (x - strength + 1); xa < (x + strength) && xa > 0 && xa < l.width; xa++) {
//                            if (xa + ya * l.width > 0 && xa + ya * l.width < light.length) {
//                                Color c = new Color(col);
//                                float mod = 1.2f;
//                                int r = (int) ((c.getRed() / (Math.sqrt(Math.pow(Math.abs(xa - x), 2) + Math.pow(Math.abs(ya - y), 2)))) * mod);
//                                int g = (int) ((c.getGreen() / (Math.sqrt(Math.pow(Math.abs(xa - x), 2) + Math.pow(Math.abs(ya - y), 2)))) * mod);
//                                int b = (int) ((c.getBlue() / (Math.sqrt(Math.pow(Math.abs(xa - x), 2) + Math.pow(Math.abs(ya - y), 2)))) * mod);
//                                while (r < 0) r++;
//                                while (g < 0) g++;
//                                while (b < 0) b++;
//                                while (r > 255) r--;
//                                while (g > 255) g--;
//                                while (b > 255) b--;
//                                c = new Color(r, g, b);
//                                lightColor[xa + ya * l.width] = c.getRGB();
//                            }
//                        }
//                    }
//                }
            }
        }    
    }
    
    public int getLightColor(int x, int y) {
        if (0 > x || x >= l.width || 0 > y || y >= l.height)
            return 0;
        return lightColor[x + y * l.width];
    }
}