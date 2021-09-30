package datamodel;

import java.io.IOException;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import reservation.ReservationNotificationController;

public class ReservationAlarm extends TimerTask {

    private final String clientName;
    private final String deviceName;
    private final String time;

    public ReservationAlarm(String clientName, String deviceName, String time) {
        this.clientName = clientName;
        this.deviceName = deviceName;
        this.time = time;
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation/ReservationNotification.fxml"));
                Parent root = loader.load();
                ReservationNotificationController controller = loader.getController();
                controller.setData(clientName, deviceName, time);
                util.gui.BuilderUI.showCustomNotification(root, false);
            } catch (IOException e) {
                util.Logger.writeLog("Exception in " + getClass().getName() + ".run() :- " + e);
            }
        });

    }

}
