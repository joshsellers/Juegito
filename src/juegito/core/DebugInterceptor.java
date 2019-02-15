package juegito.core;

import com.amp.pre.ABFrame;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author josh
 */

/*
    debug stopped displaying correctly and i dont have the source anymore
    so this just intercepts all the messages sent to debug and prints them 
    to stdout
*/
public class DebugInterceptor extends com.amp.pre.Debug {
    
    public List<DebugListener> debugListeners = new ArrayList<>();
    
    public DebugInterceptor(ABFrame frame) {
        super(frame);
    }
    
    @Override
    public synchronized void printMessage(DebugType type, String sender, String message, long displayTime) {
        System.out.println(type.toString() + " - " + sender + ": " + message);
    }
    
    @Override
    public synchronized void printPlainMessage(String message, long displayTime) {
        System.out.println(message);
        getDebugListeners().forEach((dl) -> {dl.messageIntercepted(message);});
    }
    
    public synchronized List<DebugListener> getDebugListeners() {
        return this.debugListeners;
    }
    
    public synchronized void addDebugListener(DebugListener dl) {
        this.getDebugListeners().add(dl);
    }
    
}
