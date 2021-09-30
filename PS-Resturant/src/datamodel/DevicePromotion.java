package datamodel;

import java.sql.PreparedStatement;
import util.Logger;
import util.db.DatabaseHandler;

/**
 *
 * @author Ehab Abdel Mawla
 */
public class DevicePromotion {

    private String deviceName;
    private boolean isSelected;

    public DevicePromotion(String deviceName, boolean isSelected) {
        this.deviceName = deviceName;
        this.isSelected = isSelected;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void updateState() {
        try {
            PreparedStatement stat = DatabaseHandler.con.prepareStatement("UPDATE playingprice SET promotionState=" + (isSelected ? 1 : 0) + " WHERE device=?");
            stat.setString(1, deviceName);
            stat.execute();
        } catch (Exception e) {
            Logger.writeLog("Exception In " + getClass().getName() + ".updateState() :- " + e);
        }
    }

    @Override
    public String toString() {
        return "DevicePromotion{" + "deviceName=" + deviceName + ", isSelected=" + isSelected + '}';
    }
}
