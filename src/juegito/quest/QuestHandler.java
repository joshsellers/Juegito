package juegito.quest;

import com.amp.pre.Debug;
import com.amp.text.Text;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import juegito.core.Main;
import juegito.entities.Mob;
import juegito.entities.NPC;
import juegito.level.Level;

/**
 *
 * @author joshsellers
 */
public class QuestHandler {
    
    private List<Quest> quests = new ArrayList<>();
    
    private Debug d;
    public Level l;
    
    public QuestHandler(Debug d, Level l) {
        this.d = d;
        this.l = l;
    }
    
    public List<Quest> getQuests() {
        return this.quests;
    }
    
    public Quest loadQuest(byte questID) {
        try {
            File questFile = Text.getFile("/Library/Application Support/Juegito/quests/Q" + String.valueOf(questID) + ".txt");
            String data = new String(Main.getData(questFile.toURI().toURL().openStream()));
            Quest newQuest = new Quest(questID, null, data.split(":")[0], Integer.parseInt(data.split(":")[1]));
            if (l != null) {
                for (Mob m : l.getMobs()) {
                    if (m != null && m.getActive() && m instanceof NPC) {
                        m.questStartNotification(questID);
                    }
                }
            }
            return newQuest;
        } catch (MalformedURLException ex) {

        } catch (IOException ex) {

        }
        
        return null;
    }
    
    public void advanceQuest(byte questID, int step) {
        for (Quest q : this.getQuests()) {
            if (q.getID() == questID) {
                q.step = step;
                break;
            }
        }
    }
    
    public void addQuest(Quest q) {
        this.getQuests().add(q);
        //d.printPlainMessage("Started: " + q.getTitle(), 5);
    }
    
    public String completeStep(int stepID) {
        return "null";
    }
}
