package juegito.gfx;

import java.awt.Color;

public class Screen {
    public final static int SHIFT = 4;
    public final static int TILE_SIZE = 16;
    public final static int TILE_SHEET_SIZE = 1024;
    
    public int[] pixels;
    
    public int width;
    public int height;
    
    public static final byte BIT_MIRROR_X = 0b01;
    public static final byte BIT_MIRROR_Y = 0b10;
    
    private int xOffset = 0;
    private int yOffset = 0;
    
    public boolean fading = false;
    public boolean tripping = false;
    
    private long duration;

    public SpriteSheet sheet;

    public Screen(int width, int height, SpriteSheet sheet) {
        this.width = width;
        this.height = height;
        this.sheet = sheet;

        pixels = new int[width * height];
    }
    
    int timerR = 0;
    int timerG = 1;
    int timerB = 0;
    public void trip(long duration) {
        if (!tripping) this.duration = duration;
        for (int j = 0; j < pixels.length; j++) {
            int c0 = j % pixels.length;
            int c1 = j / pixels.length;
            if (pixels[j] < 0xFF0000) {
                pixels[j] -= (((c0 / (c1 + 1)) ^ pixels[j] % ((c0 - c1) + 1))) - timerR >> timerG - timerB;
                timerR++;
            }
            if (pixels[j] > 0x0000FF) {
                pixels[j] -= (((c0 / (c1 + 1)) ^ pixels[j] % ((c0 - c1) + 1))) / timerG + timerB;
                timerG++;
            }
            timerB *= 5;
        }
        tripping = this.duration != 0;
        this.duration--;
    }
    
    public void render(int x, int y, int tile) {
        render(x, y, tile, 0, 0, 1);
    }

    public void render(int xPos, int yPos, int tile, int mirrorDir, int hue, int scale) {
        if (!fading) {
            xPos -= xOffset;
            yPos -= yOffset;

            boolean mirrorX = (mirrorDir & BIT_MIRROR_X) > 0;
            boolean mirrorY = (mirrorDir & BIT_MIRROR_Y) > 0;

            int scaleMap = scale - 1;
            int xTile = tile % (TILE_SHEET_SIZE / TILE_SIZE);
            int yTile = tile / (TILE_SHEET_SIZE / TILE_SIZE);
            int tileOffset = (xTile << SHIFT) + (yTile << SHIFT) * sheet.width;
            for (int y = 0; y < TILE_SIZE; y++) {
                int ySheet = y;
                if (mirrorY) {
                    ySheet = (TILE_SIZE - 1) - y;
                }

                int yPixel = y + yPos + (y * scaleMap) - ((scaleMap << SHIFT) / 2);

                for (int x = 0; x < TILE_SIZE; x++) {
                    int xSheet = x;
                    if (mirrorX) {
                        xSheet = (TILE_SIZE - 1) - x;
                    }
                    int xPixel = x + xPos + (x * scaleMap) - ((scaleMap << SHIFT) / 2);
                    int col = sheet.pixels[xSheet + ySheet * sheet.width + tileOffset];
                    if (col < 255) {
                        for (int yScale = 0; yScale < scale; yScale++) {
                            if (yPixel + yScale < 0 || yPixel + yScale >= height) {
                                continue;
                            }
                            for (int xScale = 0; xScale < scale; xScale++) {
                                if (xPixel + xScale < 0 || xPixel + xScale >= width) {
                                    continue;
                                }
                                if (col != 0xFF000001) {
                                    pixels[(xPixel + xScale) + (yPixel + yScale) * width] = col | hue;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void renderNumber(int number, int x, int y, Color color) {
        int[] zero = {
            1,1,1,1,1,
            1,0,0,0,1,
            1,0,0,0,1,
            1,0,0,0,1,
            1,0,0,0,1,
            1,1,1,1,1,
        };
        
        int[] one = {
            0,0,1,0,0,
            0,1,1,0,0,
            1,0,1,0,0,
            0,0,1,0,0,
            0,0,1,0,0,
            1,1,1,1,1,
        };
        
        int[] two = {
            0,1,1,1,0,
            1,0,0,0,1,
            1,0,0,1,1,
            0,0,1,0,0,
            0,1,0,0,0,
            1,1,1,1,1,
        };
        
        int[] three = {
            1,1,1,1,1,
            0,0,0,0,1,
            0,0,0,0,1,
            0,0,1,1,1,
            0,0,0,0,1,
            1,1,1,1,1,
        };
        
        int[] four = {
            1,0,0,0,1,
            1,0,0,0,1,
            1,0,0,0,1,
            1,1,1,1,1,
            0,0,0,0,1,
            0,0,0,0,1,
        };
        
        int[] five = {
            1,1,1,1,1,
            1,0,0,0,0,
            1,1,1,1,1,
            0,0,0,0,1,
            0,0,0,0,1,
            1,1,1,1,1,
        };
        
        int[] six = {
            1,1,1,1,1,
            1,0,0,0,0,
            1,0,0,0,0,
            1,1,1,1,1,
            1,0,0,0,1,
            1,1,1,1,1,
        };
        
        int[] seven = {
            1,1,1,1,1,
            0,0,0,0,1,
            0,0,0,1,0,
            0,0,0,1,0,
            0,0,0,1,0,
            0,0,0,1,0,
        };
        
        int[] eight = {
            1,1,1,1,1,
            1,0,0,0,1,
            1,0,0,0,1,
            1,1,1,1,1,
            1,0,0,0,1,
            1,1,1,1,1,
        };
        
        int[] nine = {
            1,1,1,1,1,
            1,0,0,0,1,
            1,0,0,0,1,
            1,1,1,1,1,
            0,0,0,0,1,
            0,0,0,0,1,
        };
         
        int[][] numbers = new int[][] {zero, one, two, three, four, five, six, seven, eight, nine};
        
        String snumber = Integer.toString(number, 10);
        
        int xPos = x - xOffset;
        int yPos = y - yOffset;
       
        for (int i = 0; i < snumber.length(); i++) {
            int[] n = numbers[Character.digit(snumber.charAt(i), 10)];
            
            if (i > 0) xPos += 0; //this seems like it should do something but it doesnt
            for (int ya = yPos; ya < height; ya++) {
                for (int xa = xPos; xa < width; xa++) {
                    if (xa > -1) {
                        if ((xa - xPos < 5 && ya - yPos < 6) && n[(xa - xPos) + (ya - yPos) * 5] != 0) {
                            if (xa + ya * width < pixels.length && xa + ya * width > 0) {
                                pixels[xa + ya * width] = color.getRGB();
                            }
                        }
                    }
                }
            }
        }
    }

    public void setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
    
    public int getXOffset() {
        return xOffset;
    }
    
    public int getYOffset() {
        return yOffset;
    }
}
