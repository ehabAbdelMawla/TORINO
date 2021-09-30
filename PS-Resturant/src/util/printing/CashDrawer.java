/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.printing;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import util.Logger;

/**
 *
 * @author Bayoumi
 */
public class CashDrawer {

    public static void openCashDrawer() {
        System.out.println("openCashDrawer() .. ");
        try {
            byte[] open = {27, 112, 0, 100, (byte) 250};
            openCashDrawer(open);
        } catch (Exception ex) {
            Logger.writeLog("openCashDrawer() 1 => failed");
            try {
                byte[] open = {27, 112, 0, (byte) 148, 49};
                openCashDrawer(open);
            } catch (Exception printException2) {
                printException2.printStackTrace();
                Logger.writeLog("openCashDrawer() 2 => failed");
                Logger.writeLog("Exception in " + CashDrawer.class.getName() + ".openCashDrawer() => " + printException2);
            }
        }
    }

    private static void openCashDrawer(byte[] open) throws Exception {
        PrintService pservice = PrintServiceLookup.lookupDefaultPrintService();
        DocPrintJob job = pservice.createPrintJob();
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(open, flavor, null);
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        job.print(doc, aset);
    }
}
