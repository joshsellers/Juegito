package juegito.entities;

import juegito.gfx.Screen;
import juegito.level.Level;

/**
 *
 * @author josh
 */
public class Archer extends Enemy {

    public Archer(int x, int y, int width, int height, int maxHP, int level, int damage, int manaBase, char saveID, String ID, Level l, Mob target) {
        super(x, y, width, height, maxHP, level, damage, manaBase, saveID, ID, l, target);
    }

    @Override
    protected void die(Mob source) {

    }

    @Override
    protected void attackResponse(Mob source) {

    }

    @Override
    public void interact() {

    }

    @Override
    public void levelInitializationNotification() {

    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Screen s) {

    }

    @Override
    public char[] getSaveInfo() {
        return "TODO".toCharArray();
    }
    
}
