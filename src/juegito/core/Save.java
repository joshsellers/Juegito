package juegito.core;

import com.amp.pre.Debug;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import juegito.gfx.Screen;
import juegito.level.Level;
import juegito.level.Player;
import juegito.level.items.StoredItem;

/**
 *
 * @author joshsellers
 */
//!Launch save as a seperate process.
public class Save {
    
    public static final int BUFFER_SIZE = 1;
    
    private File dir;
    private File saveFile;
    private Debug d;
    
    private char[] out = new char[BUFFER_SIZE];
    private int index = 0;
    
    private int checksumLocation = -1;
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Save(String username, Debug d) {
        this.d = d;
        dir = new File(System.getProperty("user.home") + "/Library/Application Support/Juegito/Save").getAbsoluteFile();
        saveFile = new File(dir.getAbsolutePath() + "/" + username + ".dat");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        char[] header = String.valueOf(username + System.nanoTime()).toCharArray();
        writeToBuffer(TYPE_HEAD, (char)0x01, header);
        char[] checkspace = new char[15];
        writeToBuffer(TYPE_CHECKSUM, (char)0x00, checkspace);
    }
    
    @SuppressWarnings("ImplicitArrayToString")
    public void save() {
        resizeOutBuffer();
        createChecksum();
        try (PrintWriter writer = new PrintWriter(saveFile, "UTF-8")) {
            d.printMessage(Debug.DebugType.INFO, "SAVE", "Writing buffer to file...", 5);
            writer.print(out);
        } catch (IOException ex) {
            d.printMessage(Debug.DebugType.ERROR, "SAVE", "Error saving", 5);
        }
        d.printMessage(Debug.DebugType.INFO, "SAVE", "Done.", 5);
        d.printPlainMessage("The game has been saved succesfully", 5);
    }
    
    public void writeLevel(Player p, Level l) {
        int lID = l.lID;
        int px = p.x;
        int py = p.y;
        int mdir = p.movingDir;
        if (lID == 0) {
            lID = 5;
            px = 12 << Screen.SHIFT;
            py = 9 << Screen.SHIFT;
            mdir = 1;
        }
        writeToBuffer(TYPE_LEVEL, (char)(lID<<4), new char[0]);
        String valuesString = String.valueOf(p.getTotalXP() + ";" + p.getHP() + ";" + p.getMaxHP() + ";" + p.getMana() + ";" + p.getBaseMana() + ";");
        writeToBuffer(TYPE_PLR, (char)0xA0, String.valueOf((char) ((px >> Screen.SHIFT) << 4) + "" + (char) ((py >> Screen.SHIFT) << 4) + "" + (char) (mdir+16) + "" + (char) (p.getLevel()<<4) + "" + (char) (p.getXP()<<4) + "" + valuesString).toCharArray());
        
        String inv = "";
        for (StoredItem si : p.getInventory()) {
            inv += (char) (si.getItem().getID() << 4);
            
            int abd = si.getAbundance();
            if (si.getAbundance() <= 0xFFF) {
                abd <<= 4;
                inv += (char) 0x21;
            } else {
                inv += (char) 0x20;
            }
            inv += (char) abd;
            inv += (char) (si.equippedAs << 4);
        }
        inv += (char) 0xED;
        writeToBuffer(TYPE_PLR, (char) 0xA1, inv.toCharArray());
        
        l.getMobs().stream().filter((m) -> (!(m instanceof Player) && m.getActive())).forEach((m) -> {            
            String data = "";
            data += (char) (m.x << 4);
            data += (char) (m.y << 4);
            data += (char) (m.movingDir << 4);
            data += (char) (m.getLevel() << 4);
            data += (char) (m.getHP() << 4);
            data += (char) (m.getXP() << 4);
            
            for (int i = 0; i < m.getSaveInfo().length; i++) {
                data += m.getSaveInfo()[i];
            }
            
            data += (char) 0xEE;
            String invt = "";
            for (StoredItem si : m.getInventory()) {
                invt += (char) (si.getItem().getID() << 4);

                int abd = si.getAbundance();
                if (si.getAbundance() <= 0xFFF) {
                    abd <<= 4;
                    invt += (char) 0x21;
                } else {
                    invt += (char) 0x20;
                }
                invt += (char) abd;
                invt += (char) (si.equippedAs << 4);
            }
            invt += (char) 0xED;
            data += invt;
            
            String mValueString = String.valueOf(m.getTotalXP() + ";" + m.getHP() + ";" + m.getMaxHP() + ";" + m.getMana() + ";" + m.getBaseMana() + ";");
            data += mValueString;
            
            writeToBuffer(TYPE_MOB, m.getSaveID(), data.toCharArray());
        });
        
        l.getEntities().stream().filter((e) -> (e.getActive())).forEach((e) -> {
            String data = "";
            data += (char)(e.x << 4);
            data += (char)(e.y << 4);
            for (int i = 0; i < e.getSaveInfo().length; i++) {
                data += e.getSaveInfo()[i];
            }
            writeToBuffer(TYPE_ENT, e.getSaveID(), data.toCharArray());
        });
    }
    
    public void writeToBuffer(char type, char id, char[] data) {
        if (type == TYPE_CHECKSUM && checksumLocation == -1) checksumLocation = index+1;
        
        char[] outap = {};
        if (id != '¿') {
            outap = new char[data.length + 2];
            for (int i = 2; i < outap.length; i++) {
                int j = i - 2;
                outap[i] = data[j];
            }
            outap[0] = type;
            outap[1] = id;
        } else {
            outap = data;
        }
        
        for (int i = index; i < out.length; i++) {
            int j = i-index;
            if (j < outap.length) {
                out[i] = outap[j];
            }
        }
        
        index += outap.length;
        
        if (index >= out.length) {
            if (d != null) {
                d.printMessage(Debug.DebugType.WARNING, "SAVE", "Buffer overflow, buffer expansion will be attempted", 5);
            }
            index -= outap.length;
            expandBuffer(outap);
        }
    }
    
    private void expandBuffer(char[] data) {
        char[] temp = new char[out.length * 2];
        System.arraycopy(out, 0, temp, 0, out.length);
        out = temp;
        writeToBuffer('¿', '¿', data);
        
        if (d != null) {
            d.printMessage(Debug.DebugType.INFO, "SAVE", "Buffer expanded successfully", 5);
        }
    }
    
    public void overwriteArea(char[] data, int index) {
        for (int i = index; i < out.length; i++) {
            int j = i-index;
            if (j < data.length) {
                out[i] = data[j];
            }
        }
    }
    
    private void resizeOutBuffer() {
        d.printMessage(Debug.DebugType.INFO, "SAVE", "Resizing buffer...", 5);
        int trimat = out.length;
        for (int i = out.length-1; i > 0; i--) {
            if (out[i] != 0x00) {trimat = i; break;}
        }
        char[] rout = new char[trimat+1];
        System.arraycopy(out, 0, rout, 0, rout.length);
        out = rout;
    }
    //1570687031
    private void createChecksum() {
        d.printMessage(Debug.DebugType.INFO, "SAVE", "Creating checksum...", 5);
        int indx = 0;
        for (int i = 0; i < out.length; i++) {
            if (out[i] == 0x00) {
                indx = i;
                break;
            }
        }
        String excl = "";
        for (int i = indx; i < out.length; i++) {
            excl += out[i];
        }
        int checks = excl.hashCode();
        char[] checksOut = new char[String.valueOf(checks).toCharArray().length + 1];
        checksOut[checksOut.length - 1] = TYPE_ENDCHECKS;
        System.arraycopy(String.valueOf(checks).toCharArray(), 0, checksOut, 0, checksOut.length - 1);
        overwriteArea(checksOut, checksumLocation);
    }
    
    public static boolean checksum(char[] in, Debug d) {
        int checksum = 0;
        String chs = "";
        boolean a = false;
        for (int i = 0; i < in.length; i++) {
            if (in[i] == TYPE_ENDCHECKS) break;
            if (a) chs += in[i];
            if (in[i] == TYPE_CHECKSUM) a = true;
        }
        checksum = Integer.parseInt(chs);
        int checks = 0;
        int indx = 0;
        for (int i = 0; i < in.length; i++) {
            if (in[i] == TYPE_ENDCHECKS) {
                indx = i + 1;
                break;
            }
        }
        String excl = "";
        for (int i = indx; i < in.length; i++) {
            excl += in[i];
        }
        checks = excl.hashCode();
        if (checks != checksum) {
            d.displayDebug = true;
            d.printMessage(Debug.DebugType.ERROR, "SAVE", String.valueOf("Checksum failure: " + checks + " != " + checksum), 5);
        }
        
        return checks == checksum;
    }
    
    public static void delete() {
        File dir = new File(System.getProperty("user.home") + "/Library/Application Support/Juegito/Save").getAbsoluteFile();
        deleteFolder(dir);
    }
    
    private static void deleteFolder(File f) {
        File[] files = f.listFiles();
        if (files != null) {
            for (File fi : files) {
                if (fi.isDirectory()) {
                    deleteFolder(fi);
                } else {
                    fi.delete();
                }
            }
        }
        f.delete();
    }
    
    public static boolean exists() {
        File dir = new File(System.getProperty("user.home") + "/Library/Application Support/Juegito/Save/0.dat").getAbsoluteFile();
        return dir.exists();
    }
    
    public final static char TYPE_HEAD = 0xFF;
    public final static char TYPE_ERR = 'E';
    public final static char TYPE_OPT = 'O';
    public final static char TYPE_MOB = 0xF0;
    public final static char TYPE_ENT = 0xF1;
    public final static char TYPE_PLR = 0xF2;
    public final static char TYPE_CHECKSUM = 0xFE;
    public final static char TYPE_ENDCHECKS = 'N';
    public final static char TYPE_LEVEL = 0x9F;
}
