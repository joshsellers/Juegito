package juegito.core.net;

import com.amp.AmpIO.hard.KeyIN;
import juegito.level.Level;
import juegito.level.Player;
import juegito.quest.QuestHandler;

/**
 *
 * @author josh
 */
public class RemotePlayer extends Player {
    
    public RemotePlayer(int x, int y, int level, Level l, KeyIN k, QuestHandler qh) {
        super(x, y, level, l, k, qh);
    }
    
}
