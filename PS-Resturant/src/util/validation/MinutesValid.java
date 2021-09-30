/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.validation;

import controlpanel.ControlPanel;
import java.sql.ResultSet;
import java.util.Date;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.load.LoadHelper;

/**
 *
 * @author Ehab Abdel Malwa
 */
public class MinutesValid {

    public Timeline timeline = new Timeline(new KeyFrame(
            Duration.millis(1000),
            ae -> checkTimeOut()));
    public boolean status = true;

    public void init() {
        checkTimeOut();
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void checkTimeOut() {
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT * FROM security WHERE stat=0").executeQuery();
            boolean stop = true;
            while (res.next()) {
                stop = false;
                Long now = (new Date().getTime() / 60000);
                Long st = (res.getLong(1) / 60000);
                if ((now - st) >= ControlPanel.getInstance().NUM_OF_TRAIL_MINUTES) {
                    //LogOut
                    status = false;

                    LoadHelper.firstTimeLoginNotation = 1;
                    if (LoadHelper.getInstance().homePageController != null) {
                        LoadHelper.getInstance().homePageController.logout();
                    }
                    DatabaseHandler.con.prepareStatement("DELETE FROM users WHERE accessConstrain!='مالك'").execute();
                    DatabaseHandler.con.prepareStatement("UPDATE users SET  active=0").execute();
                    DatabaseHandler.con.prepareStatement("UPDATE security SET stat=1").execute();

                }
            }
            if (stop) {
                timeline.stop();
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in MinutesValid -> checkTimeOut :- " + e);
        }
    }
}
