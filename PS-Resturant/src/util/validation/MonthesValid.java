/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.validation;

import controlpanel.ControlPanel;
import com.google.api.client.util.Base64;
import java.io.File;
import java.io.PrintWriter;
import java.security.spec.KeySpec;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.util.Duration;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import util.Logger;
import util.db.DatabaseHandler;
import util.db.Methods;

/**
 *
 * @author Ehab Abdel Mawla
 */
public class MonthesValid {

    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private KeySpec ks;
    private SecretKeyFactory skf;
    private Cipher cipher;
    byte[] arrayBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;
    SecretKey key;
    public Timeline monthCheck = new Timeline(new KeyFrame(
            Duration.minutes(30),
            ae -> monthCheck(null)));

    public boolean checkResult = true;

    public void init() {
//        ==================== INIT ====================
        try {
            myEncryptionKey = "ThisIsSpartaThisIsSparta";
            myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;

            arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);

            ks = new DESedeKeySpec(arrayBytes);
            skf = SecretKeyFactory.getInstance(myEncryptionScheme);
            cipher = Cipher.getInstance(myEncryptionScheme);
            key = skf.generateSecret(ks);
        } catch (Exception ex) {
            Logger.writeLog("Exception in MonthesValid -> monthCheck -> runlater :- " + ex);
        }
//        ==============================================

        monthCheck.setCycleCount(Animation.INDEFINITE);
        monthCheck.play();
    }

    public void monthCheck(SimpleBooleanProperty allFine) {
        try {
            ResultSet resultSet = DatabaseHandler.con.prepareStatement("SELECT * FROM daysValid WHERE flag=1").executeQuery();
            boolean validIsActive = resultSet.next();
            while (validIsActive) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
                String dateString = LocalDate.now().toString();
                ResultSet res = DatabaseHandler.con.prepareStatement("SELECT MAX(day) FROM archofdays").executeQuery();

                Long lastEndDay = (sdf.parse(dateString)).getTime() / (60000 * 60 * 24);
                while (res.next()) {
                    if (res.getString(1) != null) {
                        dateString = res.getString(1);
                    }
                    lastEndDay = (sdf.parse(dateString)).getTime() / (60000 * 60 * 24);
                }

                res = DatabaseHandler.con.prepareStatement("SELECT * FROM myDay").executeQuery();
                Long systemcuDate = lastEndDay;

                while (res.next()) {
                    dateString = res.getString(1);
                    systemcuDate = (sdf.parse(dateString)).getTime() / (60000 * 60 * 24);
                }

                Long now = (new Date().getTime() / (60000 * 60 * 24));
                Long st = (sdf.parse(resultSet.getString(1))).getTime() / (60000 * 60 * 24);

                //
                if (now < st || now < lastEndDay
                        || now < systemcuDate
                        || (now - st >= ControlPanel.getInstance().MONTH_VALIDATION_DURATION)) {
                    checkResult = false;
                    if (allFine != null) {
                        allFine.set(checkResult);
                    }
                    Platform.runLater(() -> {
                        try {
                            TextInputDialog dialog = new TextInputDialog("");
                            dialog.setTitle("انتهت صلاحية البرنامج");
                            dialog.setHeaderText("يجب ادخال كود التفعيل");
                            dialog.setContentText(": كود التفعيل ");
                            Optional<String> result = dialog.showAndWait();
                            if (result.isPresent()) {
                                String temp = encrypt(result.get());
                                ResultSet resw;
                                resw = DatabaseHandler.con.prepareStatement("SELECT code from codes WHERE code='" + temp + "'").executeQuery();
                                while (resw.next()) {
                                    checkResult = true;
                                    if (allFine != null) {
                                        allFine.set(checkResult);
                                    }
                                    DatabaseHandler.con.prepareStatement("UPDATE daysValid SET start='" + LocalDate.now().toString() + "'").execute();
                                    DatabaseHandler.con.prepareStatement("DELETE FROM codes WHERE code='" + temp + "'").execute();
                                    if (!isEmpty("codes")) {
                                        DatabaseHandler.con.prepareStatement("UPDATE daysValid SET flag=0").execute();
                                    }
                                    return;
                                }
                            }
                            Alert errorMsg = new Alert(Alert.AlertType.ERROR);
                            errorMsg.setHeaderText("Incorrect Key!");
                            errorMsg.showAndWait();
                            System.exit(0);
                        } catch (SQLException ex) {
                            Logger.writeLog("Exception in MonthesValid -> monthCheck -> runlater :- " + ex);
                        }
                    });
                } else {
                    checkResult = true;
                    if (allFine != null) {
                        allFine.set(checkResult);
                    }
                }
                break;
            }
            if (!validIsActive) {
                checkResult = true;
                if (allFine != null) {
                    allFine.set(checkResult);
                }
            }

        } catch (Exception e) {

            Logger.writeLog("Exception in MonthesValid -> monthCheck :- " + e);
            System.exit(0);
        }
    }

    public String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(Base64.encodeBase64(encryptedText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    public String decrypt(String encryptedString) {
        String decryptedText = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.decodeBase64(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = new String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }

    public boolean isEmpty(String tableName) {
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT COUNT(*) FROM codes").executeQuery();
            while (res.next()) {
                if (res.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in MonthesValid -> isEmpty :- " + e);
        }
        return false;
    }

    public static void generateCodes(int numOfCodes, String startDate) {
        try {
            DatabaseHandler.connectToDataBase();
            MonthesValid obj = new MonthesValid();
            obj.init();
            Methods.clearData("codes");

            File outPutFile = new File(ControlPanel.getInstance().INSTANCE_ONLINE_NAME + ".hbe");
            PrintWriter pw = new PrintWriter(outPutFile);
            DatabaseHandler.con.prepareStatement("UPDATE daysValid SET start='" + startDate + "',flag=1 ").execute();
            pw.write("     PLAIN                      ENC");
            pw.write("\n");

            for (int i = 0; i < numOfCodes; i++) {
                String palinText = getRandomTextOfLength();
                String enc = obj.encrypt(palinText);
                pw.write(palinText + "                 " + enc);
                pw.write("\n");
                DatabaseHandler.con.prepareStatement("INSERT INTO codes VALUES('" + enc + "')").execute();
            }
            pw.write("PLAIN TEXT SEND TO USER ");

            pw.flush();
            pw.close();
        } catch (Exception ex) {
            System.out.println("Exception " + ex);
        }

    }

    public static void resetCodes() {
        try {
            DatabaseHandler.connectToDataBase();
            DatabaseHandler.con.prepareStatement("UPDATE daysValid SET flag=0").execute();
        } catch (Exception ex) {
            System.out.println("Exception " + ex);
        }
    }

    public static String getRandomTextOfLength() {
        String All_CHARACTERS = "abcdefghijklmnopqrstrzABCDEFGHIJKLMNOPQRST123456789";

        String randomText = "";
        for (int i = 0; i < 10; i++) {
            randomText += All_CHARACTERS.charAt((int) (Math.random() * All_CHARACTERS.length()));
        }
        return randomText;
    }

}
