package juegito.core.net;

import com.amp.pre.Debug;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author josh
 */
public class HostServer extends Thread {
    public boolean running;
    
    public Debug debug;
    
    private DatagramSocket serverSocket;
    private byte[] buf = new byte[256];
    
    public HostServer(int port, Debug debug) throws IOException {
        serverSocket = new DatagramSocket(port);
        
        this.debug = debug;
    }
    
    @Override
    public void run() {
        running = true;
        
        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                serverSocket.receive(packet);
            } catch (IOException ex) {
                Logger.getLogger(HostServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
