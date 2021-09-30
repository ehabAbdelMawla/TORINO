/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.validation;

import java.util.Scanner;
import util.Logger;

/**
 *
 * @author Bayoumi
 */
public class Security {

    /**
     * @param productName
     * @param UUIDS :List all UUIDS
     * @return true if productName || localmacAddresses match the local ones
     */
    public boolean isValidProductNameAndUUID(String productName, String... UUIDS) {
        String serial = "";
        String UUID = "";
        try {
            //"wmic csproduct get UUID"
            UUID = getResultOfCommand(new String[]{"wmic", "csproduct", "get", "UUID"});
            //"wmic csproduct get name"
            serial = getResultOfCommand(new String[]{"wmic", "csproduct", "get", "name"});
            Logger.writeLog("UUID = " + UUID + " , Product Name = " + serial.trim());
            for (String localmacAddres : UUIDS) {
                if (serial.trim().equalsIgnoreCase(productName) || UUID.equalsIgnoreCase(localmacAddres)) {
                    return true;
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> macAddressConfiguration :- " + e);
        }
        return false;
    }

    public static String getResultOfCommand(String[] Command) {
        String result = "";
        try {
            Process process = Runtime.getRuntime().exec(Command);
            process.getOutputStream().close();
            Scanner sc = new Scanner(process.getInputStream());
            sc.nextLine();
            while (sc.hasNext()) {
                result += sc.next();
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in Security -> getResultOfCommand :- " + e);
        }
        return result;
    }
}
