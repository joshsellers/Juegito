package juegito.gfx;

import com.amp.pre.Debug;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {

    public int width;
    public int height;

    public int[] pixels;
    
    public SpriteSheet(Debug d) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(System.getProperty("user.home") + "/Library/Application Support/Juegito/sprite_sheet.png").getAbsoluteFile());
        } catch (IOException e) {
            if (d != null) d.printMessage(Debug.DebugType.ERROR, "SpriteSheet", "Error loading sprite sheet: " + e.getLocalizedMessage(), 5);
        }

        if (image == null) return;

        this.width = image.getWidth();
        this.height = image.getHeight();

        pixels = image.getRGB(0, 0, width, height, null, 0, width);
    }
}
