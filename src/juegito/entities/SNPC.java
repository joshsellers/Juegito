package juegito.entities;

import com.amp.mathem.Statc;

/**
 *
 * @author joshsellers
 */
public class SNPC {
    
    public static final int TRAIT_COUNT = 7;
    
    private int[] traits;
    //luck, skill, intellegence, response, maxHP, maxSamina, statCount, statWeight0, ...
    private int[][] stats;
    
    public SNPC(int ID, int[] traits, int x, int y) {
        this.traits = traits;
        
        stats = new int[traits[6]][2];
        for (int i = 0; i < stats.length; i++) {
            stats[i][1] = traits[TRAIT_COUNT + i];
        }
    }
    
    public int[] breed(int[] pTraits) {
        int statCount = 0;
        
        if (traits.length > pTraits.length) {
            statCount = traits[6];
        } else {
            statCount = pTraits[6];
        }
        
        int[] offSpring = new int[TRAIT_COUNT + statCount];
        
        for (int i = 0; i < offSpring.length; i++) {
            if (Statc.intRandom(0, 1) == 0) {
                offSpring[i] = traits[i];
            } else { 
                offSpring[i] = pTraits[i];
            }
            
            if (Statc.intRandom(0, 5) == 0) {
                offSpring[i] += Statc.intRandom(-10, 10);
            }
        }
        
        return offSpring;
    }
}
