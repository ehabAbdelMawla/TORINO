/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.printing;

import controlpanel.ControlPanel;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Map;
import javax.imageio.ImageIO;
import util.Logger;
import util.db.DatabaseHandler;

/**
 *
 * @author Ehab Abdel Mawla
 */
public class PrintingData {

    private static PrintingData instance;
    public String shopName;
    public String phoneNumbers;
    public String address;
    public Image image;
    public javafx.scene.image.Image fxImage;
    public boolean logoApperance;

    public PrintingData() {
        fetchData();
    }

    public static PrintingData getInstance() {
        if (instance == null) {
            instance = new PrintingData();
        }
        return instance;
    }

    public void fetchData() {
        try {
            ResultSet shopData = DatabaseHandler.con.prepareStatement("SELECT * FROM printData").executeQuery();
            while (shopData.next()) {
                shopName = shopData.getString(1);
                phoneNumbers = shopData.getString(2);
                address = shopData.getString(3);
                logoApperance = shopData.getString(5).equalsIgnoreCase("logo");
                byte[] bytes = shopData.getBytes(4);
                if (bytes != null) {
                    image = ImageIO.read(new ByteArrayInputStream(bytes));
                    fxImage = new javafx.scene.image.Image(new ByteArrayInputStream(bytes));
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in printingData -> fetchData():- " + e);
        }
    }

    public void setShopParameters(Map<String, Object> parameters) {
        try {
            parameters.put("address", address);
            parameters.put("phoneNumbers", phoneNumbers);
            parameters.put("image", image);
            parameters.put("shopName", shopName);
            parameters.put("currency", ControlPanel.getInstance().getCurrency());
            parameters.put("logoVisability", logoApperance);

        } catch (Exception e) {
            Logger.writeLog("Exception in printingData -> setShopParameters() :-" + e);
        }
    }

    public byte[] readFile(String file) {
        ByteArrayOutputStream bos = null;
        try {
            File f = new File(file);
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            bos = new ByteArrayOutputStream();
            for (int len; (len = fis.read(buffer)) != -1;) {
                bos.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e2) {
            System.err.println(e2.getMessage());
        }
        return bos != null ? bos.toByteArray() : null;
    }

}
