package main;

import java.util.Set;
import util.gui.load.Location;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.Logger;
import util.gui.BuilderUI;

public class MainClass extends Application {

    @Override
    public void start(Stage stage) {
        try {
            System.out.println(getClass().getName() + ".start() 1");
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource(Location.getInstance().get("LAUNCHERVIEW"))));
            stage.setScene(scene);
            scene.setFill(Color.TRANSPARENT);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initStyle(StageStyle.TRANSPARENT);
            BuilderUI.setAppDecoration(stage);
            stage.show();
            System.out.println(getClass().getName() + ".start() 2");
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + ".start() => " + e);
        }
    }

    public static void main(String[] args) {
        System.out.println("main.main() 1");
        launch(args);
        System.out.println("main.main() 2");
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        System.out.println("Running Threads");
        threadSet.forEach((t) -> {
            System.out.println("Thread: " + t.getName());
        });
    }

}
