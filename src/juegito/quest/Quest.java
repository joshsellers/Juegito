package juegito.quest;

/**
 *
 * @author joshsellers
 */
public class Quest {
    
    private byte ID;
    
    private String title;
    private int stepCount;
    
    private String[] quest;
    
    public int step;
    
    public Quest(byte ID, String[] quest, String title, int stepCount) {
        this.ID = ID;
        this.quest = quest;
        this.title = title;
        this.stepCount = stepCount;
    }
    
    public byte getID() {
        return ID;
    }
    
    public String getTitle() {
        return title;
    }
}
