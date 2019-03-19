package juegito.core.net;

import com.amp.pre.Debug;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author josh
 */
public class GameClient {
    private DatagramSocket socket;
    private InetAddress address;
    
    private int port;
    
    public Debug debug;
    
    public GameClient(String address, int port, Debug debug) {
        try {
            this.address = InetAddress.getByAddress(address.getBytes());
            socket = new DatagramSocket();
        } catch (UnknownHostException ex) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.port = port;
        this.debug = debug;
    }
    
    public String sendPacket(String message) {
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, address, port);
        try {
            socket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] buf = new byte[256];
        packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (IOException ex) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new String(packet.getData());
    }
}
