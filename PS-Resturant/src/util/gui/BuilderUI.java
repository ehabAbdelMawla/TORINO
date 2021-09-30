/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.gui;

import controlpanel.ControlPanel;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.File;
import java.util.HashMap;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 *
 * @author Bayoumi
 */
public class BuilderUI {
    
    private static final String DEFAULT_TITLE = "PS System - Code Clinic";
    
    public static Image infoImage = new Image("/img/infoPNG.png");
    public static Image appImage = new Image(ControlPanel.getInstance().PATH_OF_LAUNCHER_IMAGE);
    private static final MediaPlayer finishTimeNotificationSound = new MediaPlayer(new Media(new File("audio/timeFinished.mp3").toURI().toString()));
    private static final MediaPlayer reservationNotificationSound = new MediaPlayer(new Media(new File("audio/reservation_notification.mp3").toURI().toString()));
    
    public static Alert buildErrorAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert a = new Alert(alertType);
        a.setTitle(title);
        a.setHeaderText(header);
        a.setContentText(content);
        return a;
    }
    
    public static void showCustomNotification(Node node, boolean isDark) {
        Notifications notification = Notifications.create()
                .graphic(node)
                .hideAfter(Duration.seconds(10))
                .position(Pos.BOTTOM_RIGHT);
        if (isDark) {
            notification.darkStyle();
        }
        notification.show();
        reservationNotificationSound.play();
        reservationNotificationSound.setOnEndOfMedia(() -> {
            reservationNotificationSound.stop();
        });
        
    }
    
    public static void showNotification(Node icon, String msg) {
        icon.setStyle("-fx-font-size:80;");
        Label msgLabel = new Label(msg);
        msgLabel.setAlignment(Pos.CENTER);
        msgLabel.setTextAlignment(TextAlignment.CENTER);
        msgLabel.setWrapText(true);
        VBox vb = new VBox(icon, msgLabel);
        vb.setAlignment(Pos.CENTER);
        vb.setSpacing(5);
        Notifications notification = Notifications.create()
                .graphic(vb)
                .hideAfter(Duration.seconds(10))
                .position(Pos.BOTTOM_RIGHT);
        notification.show();
    }
    
    public static Stage initStageDecorated(Scene scene, String title, String Icon) {
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(title == null ? DEFAULT_TITLE : title);
        stage.initModality(Modality.APPLICATION_MODAL);
        setIcon(stage, Icon);
        return stage;
    }
    
    public static Stage initStageTransparent(Scene scene, String title, String Icon) {
        scene.setFill(Color.TRANSPARENT);
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title == null ? DEFAULT_TITLE : title);
        setIcon(stage, Icon);
        return stage;
    }
    
    public static Stage initStageUnDecorated(Scene scene, String title, String Icon) {
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setTitle(title == null ? DEFAULT_TITLE : title);
        stage.initModality(Modality.APPLICATION_MODAL);
        setIcon(stage, Icon);
        return stage;
    }
    
    public static void setAppDecoration(Stage s) {
        s.setTitle(DEFAULT_TITLE);
        setIcon(s, "");
    }
    
    public static void setIcon(Stage s, String iconName) {
        s.getIcons().clear();
        Image icon;
        if (iconName.equalsIgnoreCase("info")) {
            icon = infoImage;
        } else {
            icon = appImage;
        }
        s.getIcons().add(icon);
    }
    
    public static void HandlerCTRL_W(Scene scene, Stage stage, Runnable rn) {
        HashMap<KeyCombination, Runnable> hashMap = new HashMap<>();
        // CTRL + W
        KeyCombination kc1 = new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN);
        hashMap.put(kc1, rn);
        scene.getAccelerators().putAll(hashMap);
    }
    
    public static void showTimerNotification(String roomName) {
        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.TIMER);
        icon.setStyle("-fx-font-size:80;");
        Label msg = new Label("انتهى وقت  : " + roomName);
        msg.setAlignment(Pos.CENTER);
        VBox vb = new VBox(msg, icon);
        vb.setAlignment(Pos.CENTER);
        vb.setSpacing(5);
        Notifications notification = Notifications.create()
                .graphic(vb)
                .hideAfter(Duration.seconds(10))
                .position(Pos.BOTTOM_RIGHT)
                .onAction((ActionEvent event) -> {
                    finishTimeNotificationSound.stop();
                });
        notification.show();
        finishTimeNotificationSound.play();
        finishTimeNotificationSound.setOnEndOfMedia(() -> {
            finishTimeNotificationSound.stop();
        });
    }
}
