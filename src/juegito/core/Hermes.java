package juegito.core;

import com.amp.pre.Debug;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.*;


/**
 *
 * @author joshsellers
 */
public class Hermes implements Runnable {
    public static final String VERSION = "0.1";
    private static JChannel channel;

    public static void start(final Debug d) {
        try {            
            System.setProperty("java.net.preferIPv4Stack", "true");
            
            channel = new JChannel("udp.xml");
            channel.setDiscardOwnMessages(true);
            channel.printProtocolSpec(false);
            channel.connect("hermeslink");
            channel.setReceiver(new ReceiverAdapter() {
                @Override
                public void viewAccepted(View newView) {}
                
                @Override
                public void receive(Message msg) {
                    d.printMessage(Debug.DebugType.INFO, "HERMES:" + msg.src().toString(), new String(msg.getBuffer()), 5);
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(Hermes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static int deliver(String msg) {
        try {
            Message message = new Message();
            message.setBuffer(msg.getBytes());
            channel.send(message);
        } catch (Exception ex) {
            Logger.getLogger(Hermes.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }
        return 0;
    }

    @Override
    public void run() {

    }
}
