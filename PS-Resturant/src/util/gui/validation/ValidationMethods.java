/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.gui.validation;

import javafx.scene.control.TextField;
import util.Logger;

/**
 *
 * @author Bayoumi
 */
public class ValidationMethods {

    private static final String CLASSNAME = ValidationMethods.class.getName();

    //  Mail Validation
    public static boolean isMail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public static boolean IsEmpty(TextField... texts) {
        try {
            for (TextField txt : texts) {
                if (!txt.getText().trim().equals("")) {
                    return false;
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  IsEmpty()");
        }
        return true;
    }

    //==== Vailid DataBase Values ====
    public static boolean checkDataBaseValuesConstraint(TextField... texts) {
        try {
            for (TextField txt : texts) {
                if (txt.getText().matches(".*[-'\\\"/%].*") || txt.getText().contains("\\")) {
                    return false;
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  checkDataBaseValuesConstraint()");
        }
        return true;
    }
    //      LoginController.customAlert("يجب ان تكون البيانات خالية من (' , \" , \\ , / , % , - )");

    public static boolean checkDataBaseValuesConstraint(String texts) {
        try {
            if (texts.matches(".*[-'\\\"/%].*") || texts.contains("\\")) {
                return false;
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  checkDataBaseValuesConstraint()");
        }
        return true;
    }

    //Phone Validation
    public static boolean isPhoneNumber(String phNum) {
        return phNum.matches("01[0-9]{9}");
    }

    //==== Vailid textFields Values ====
    public static boolean checkTextFeildConstraint(TextField... texts) {
        try {
            for (TextField txt : texts) {
                if (txt.getText().trim().equals("")) {
                    return false;
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  checkTextFeildConstraint()");
        }
        return true;
    }

}
