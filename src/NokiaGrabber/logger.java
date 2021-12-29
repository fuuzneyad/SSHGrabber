package NokiaGrabber;

import java.io.*;
import java.text.*;
import java.util.*;

public class logger {

    private final static DateFormat df = new SimpleDateFormat ("[HH:mm:ss]");

    public logger() {
    }
    
    
    public void write(String msg) {
        tulis(msg);
    }
    
    public static void write(Exception e) {        
        tulis(stack2string(e));
    }

    
    public static void tulis(String msg) {
        try {
            Date now = new Date();
            String currentTime = logger.df.format(now);
            
            DateFormat tanggal = new SimpleDateFormat ("yyyyMMdd");
            String sekarang = tanggal.format(now);
            String file = "log/log"+sekarang+".log";

            FileWriter aWriter = new FileWriter(file, true);
            aWriter.write(currentTime + " " + msg
                    + System.getProperty("line.separator"));
            aWriter.flush();
            aWriter.close();
        }
        catch (Exception e) {
            System.out.println(stack2string(e));
        }
    }
    
    private static String stack2string(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "------\r\n" + sw.toString() + "------\r\n";
        }
        catch(Exception e2) {
            return "bad stack2string";
        }
    }
}