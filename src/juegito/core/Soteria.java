package juegito.core;

/**
 *
 * @author joshsellers
 */
public class Soteria {
    
    public static void notifyResponce() {
        Hermes.deliver("RESP_" + System.currentTimeMillis());
    }
}
