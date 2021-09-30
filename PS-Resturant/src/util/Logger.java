/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Bayoumi
 */
public class Logger {

    //========= Helper Objects =========
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("[dd-MM-yyyy] [hh:mm:ss a]");
    private static final Object LOCK = new Object();
    private static PrintWriter PRINT_WRITER;

    public static void init() {
        try {
            new File("logs").mkdir();
            PRINT_WRITER = new PrintWriter(new FileWriter("logs/Important.txt", true));
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void writeLog(String msg) {
        try {
            new Thread(() -> {
                synchronized (LOCK) {
                    System.out.println(msg);
                    String DataAndTime = DATE_TIME_FORMAT.format(new Date());
                    PRINT_WRITER.println(DataAndTime + " => " + msg);
                    PRINT_WRITER.flush();
                }
            }).start();
        } catch (Exception ex) {
            System.out.println("Exception in writeter: " + ex);
        }
    }
}
