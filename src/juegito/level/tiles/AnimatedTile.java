package juegito.level.tiles;

import juegito.gfx.Screen;

public class AnimatedTile extends BasicTile {

    private int[][] animationTileCoords;
    private int currentAnimationIndex;
    private long lastIterationTime;
    private int animationSwitchDelay;

    public AnimatedTile(int id, int[][] animationCoords, int color, int animationSwitchDelay, boolean isSolid, int flip, boolean top) {
        super(id, animationCoords[0][0], animationCoords[0][1], color, isSolid, flip, top);
        this.animationTileCoords = animationCoords;
        this.currentAnimationIndex = 0;
        this.lastIterationTime = System.currentTimeMillis();
        this.animationSwitchDelay = animationSwitchDelay;
    }

    @Override
    public void tick() {
        if ((System.currentTimeMillis() - lastIterationTime) >= (animationSwitchDelay)) {
            lastIterationTime = System.currentTimeMillis();
            currentAnimationIndex = (currentAnimationIndex + 1) % animationTileCoords.length;
            this.tileID = (animationTileCoords[currentAnimationIndex][0] + (animationTileCoords[currentAnimationIndex][1]) * (Screen.TILE_SHEET_SIZE / Screen.TILE_SIZE));
        }
    }
}
