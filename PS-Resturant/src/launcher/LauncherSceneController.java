package launcher;

import controlpanel.ControlPanel;
import util.validation.OnlineValidation;
import datamodel.User;
import com.jfoenix.controls.JFXProgressBar;
import java.net.URL;
import java.sql.ResultSet;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.gui.load.Location;
import util.Logger;
import util.gui.BuilderUI;
import util.gui.load.LoadHelper;
import util.validation.MinutesValid;
import util.validation.MonthesValid;
import util.validation.Security;
import util.validation.SingleInstance;
import util.db.DatabaseHandler;

public class LauncherSceneController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private JFXProgressBar progressbar;
    @FXML
    private ImageView imageView;

    private final SimpleBooleanProperty isFinished = new SimpleBooleanProperty(false);
    public static final SimpleIntegerProperty LICENSE = new SimpleIntegerProperty(1);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            System.out.println(getClass().getName() + ".initialize() 1");
            imageView.setImage(new Image(ControlPanel.getInstance().PATH_OF_LAUNCHER_IMAGE));
            LoadHelper.firstTimeLoginNotation = 0;
            isFinished.addListener((o) -> {
                if (isFinished.getValue()) {
                    proceedToApplication(getExpectedRoot(true));
                }
            });
            // init logger
            Logger.init();
            SingleInstance instance = SingleInstance.getInstance();
            progressbar.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
                if (oldScene == null && newScene != null) {
                    newScene.windowProperty().addListener((observable, oldWindow, newWindow) -> {
                        if (oldWindow == null && newWindow != null) {
                            instance.setCurrentStage((Stage) newWindow);
                        }
                    });
                }
            });

            DatabaseHandler.connectToDataBase();
            if (DatabaseHandler.con != null) {
                boolean isloaded = LoadHelper.getInstance().loadNeccessaryRoots();
                System.out.println("isloaded: " + isloaded);
            }
            new Thread(() -> {
                try {
                    SimpleBooleanProperty isAllFine = new SimpleBooleanProperty(true);
                    double startTime = System.currentTimeMillis();

                    System.out.println(getClass().getName() + ".initialize() START HARD WORK");
                    Security security = new Security();

                    if (!(security.isValidProductNameAndUUID(
                            "HPProDesk400G1SFF",
                            // Ehab
                            "C51B0B53-B83E-11E6-801F-C85B76AB9DB8",
                            // Bayoumi
                            "33D2EF28-FCC8-11E7-8A95-8C16451B62A5",
                            // Hassan
                            "ADA75FFF-B6A2-11E6-801F-C85B76A9B52E",
                            // Client
                            "4C4C4544-0058-5210-8048-C8C04F303832") || ControlPanel.getInstance().IS_DEMO)) {
                        isAllFine.set(false);
                        System.out.println("inside UUID if condition");
                        LICENSE.setValue(2);

                        return;
                    }

                    if (ControlPanel.getInstance().ONLINE_VALIDATION) {
                        OnlineValidation.getInstance().init(isAllFine);
                        boolean isLicenceExist = OnlineValidation.getInstance().localValidation();
                        if (!isLicenceExist && isAllFine.get()) {
                            isAllFine.set(false);
                            LICENSE.setValue(2);
                        }
                    }

                    if (ControlPanel.getInstance().MONTH_VALIDATION) {
                        MonthesValid monthCheck = new MonthesValid();
                        monthCheck.init();
                        monthCheck.monthCheck(isFinished);
                        isAllFine.set(false);
                    }

                    MinutesValid minutesValid = new MinutesValid();
                    if (ControlPanel.getInstance().FREE_TRIAL_TIME) {
                        minutesValid.init();
                    }

                    if (isAllFine.get()) {
                        isFinished.set(true);
                    } else {
                        System.out.println("isAllFine: " + isAllFine);
                        // isAllFine=false then => LICENSE=2=false
//                        LICENSE.setValue(2);
//                        isFinished.set(true);
                    }
                    System.out.println(getClass().getName() + ".initialize() END HARD WORK\n"
                            + "With time: " + (System.currentTimeMillis() - startTime));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
            System.out.println(getClass().getName() + ".initialize() 2");
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + ".initailize() : " + ex);
        }
    }

    private Parent getExpectedRoot(boolean minutesValidResult) {
        Parent root = null;
        try {
            if (DatabaseHandler.con != null) {
                User.CurrentUser = minutesValidResult ? User.getActiveUser() : null;
                if (User.CurrentUser != null) {
                    LoadHelper.getInstance().loadScreens(true);
                    root = LoadHelper.getInstance().screenMap.get("root_Homepage");
                } else {
                    root = FXMLLoader.load(getClass().getResource(Location.getInstance().get("LOGINVIEW")));
                }

            } else {
                // TODO this does not happpend in sqlite db
                // TODO need to impl the correct handler for database error
                Platform.runLater(() -> {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("خطأ");
                    a.setHeaderText("قاعدة البيانات لا تستجيب");
                    a.setContentText("حدث خطأ في الاتصال بقاعدة البيانات ");
                    ((Stage) progressbar.getScene().getWindow()).close();
                    a.show();
                });
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + ".getExpectedRoot() : " + ex);
        }
        return root;
    }

    private void proceedToApplication(Parent root) {
        try {
            Platform.runLater(() -> {
                Scene scene = new Scene(root);
                Stage window = new Stage(StageStyle.UNDECORATED);
                BuilderUI.setAppDecoration(window);
                if (root.equals(LoadHelper.getInstance().screenMap.get("root_Homepage"))) {
                    window.setMaximized(true);
                    window.initStyle(StageStyle.DECORATED);
                } else {
                    scene.setFill(Color.TRANSPARENT);
                    window.initStyle(StageStyle.TRANSPARENT);
                }
                window.setScene(scene);
                SingleInstance.getInstance().getCurrentStage().close();
                window.show();
                System.out.println("Window " + window);
                SingleInstance.getInstance().setCurrentStage(window);
            });
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + ".proceedToApplication() : " + ex);
        }
    }

    public static void setFirstStartTime() {
        try {
            String sql = "SELECT COUNT(*) FROM security ";
            ResultSet res = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (res.next()) {
                int x = -1;
                x = res.getInt(1);
                if (x > 0) {
                    return;
                }
            }
            sql = "INSERT INTO security VALUES (?,?)";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setLong(1, new Date().getTime());
            DatabaseHandler.stat.setInt(2, 0);
            DatabaseHandler.stat.execute();
            new MinutesValid().init();
        } catch (Exception e) {
            Logger.writeLog("Exception in LuncherSceneController -> setFirstStartTime :- " + e);
        }
    }

    @FXML
    private void RootMousePressed(Event event) {
        MouseEvent e = (MouseEvent) event;
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    }

    @FXML
    private void RootMouseDragged(Event event) {
        MouseEvent e = (MouseEvent) event;
        ((Stage) (((Node) event.getSource()).getScene().getWindow())).setX(e.getScreenX() - xOffset);
        ((Stage) (((Node) event.getSource()).getScene().getWindow())).setY(e.getScreenY() - yOffset);
    }
}
