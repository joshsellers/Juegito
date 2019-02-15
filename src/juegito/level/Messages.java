package juegito.level;

/**
 *
 * @author joshsellers
 */
public class Messages {
    
    public static int[] IDs;
    public static String[] messages;
    
    public static String getMessage(int ID) {
        for (int i = 0; i < messages.length; i++) {
            if (IDs[i] == ID) return messages[i];
        }
        
        return "NULL";
    }
    
    public static int getMessageID(char id, int x, int y, int w) {
        return x + y * w + (int)id;
    }
}
