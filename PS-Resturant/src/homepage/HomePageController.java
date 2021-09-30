package homepage;

import playing.hall.PlayingRoomsHallController;
import controlpanel.ControlPanel;
import dialog.alert.confirm.ConfirmAlertController;
import datamodel.TempMethods;
import dialog.admin_validation.AdminValidationController;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDrawer;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import login.LoginController;
import util.gui.load.Location;
import util.db.Methods;
import util.Logger;
import util.gui.BuilderUI;
import util.gui.load.LoadHelper;
import util.validation.SingleInstance;
import com.jfoenix.controls.JFXButton;
import datamodel.User;
import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import launcher.LauncherSceneController;
import playing.rooms.util.RoomBox;
import toolbar.ToolbarController;
import util.gui.load.DialogHelper;
import util.validation.OnlineValidation;

public class HomePageController implements Initializable {

    // ==== Helper Objects =====
    public static JFXDialog aboutDialog;
    public static JFXDialog currentDialog;
    // ===== GUI Objects ======
    @FXML
    public BorderPane borderPane;
    public static BorderPane borderPane_Static;
    @FXML
    private Label usernameLabel;
    public static Label username_Static_Label;
    @FXML
    private Label ptitle;
    public static Label title_Label;
    @FXML
    private StackPane AP;
    public static StackPane homepageStackPane;
    @FXML
    private JFXDrawer drawer1;
    public static JFXDrawer drawer1_Static;
    @FXML
    private HBox logoImage;
    @FXML
    private JFXButton logoutBTN;
    @FXML
    public VBox licenseBox;
    @FXML
    private Label licenseText;
    @FXML
    private Label trialEndDate;
    @FXML
    private VBox trialBox;
    public ToolbarController toolbarController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            final Tooltip menuTooltip = new Tooltip("Open Menu" + " - " + "فتح القائمة");
            Tooltip.install(logoImage, menuTooltip);
            menuTooltip.getStyleClass().add("tooltip-b");

            final Tooltip logoutTooltip = new Tooltip("Logout" + " - " + "تسجيل الخروج");
            Tooltip.install(logoutBTN, logoutTooltip);

            // assign static vairables
            homepageStackPane = AP;
            drawer1_Static = drawer1;
            borderPane_Static = borderPane;
            title_Label = ptitle;
            username_Static_Label = usernameLabel;
            // ===================================
            borderPane.setCenter(ControlPanel.getInstance().HAS_PLAYSTATION ? LoadHelper.getInstance().screenMap.get("defaultCenter") : LoadHelper.getInstance().screenMap.get("root_RestaurantHome"));
            initDrawer();
            initAboutDialog();
            checkOfMyDay();

            usernameLabel.setText(User.CurrentUser.getUserName());

            // relative size handle
            util.gui.HelperMethods.setRelativeSize(AP, 0.8, 1000, 700);

            // (Stage & Scene) Listener
            util.gui.HelperMethods.setStageListener(AP, (stage) -> {
                Scene scene = stage.getScene();
                KeyCombinationHandler(scene, stage);
                scene.getStylesheets().add("/resources/style/base.css");
                stage.setOnCloseRequest((WindowEvent event) -> {
                    closeWindow(event);
                });
                if (ControlPanel.getInstance().HAS_PLAYSTATION && RoomBox.isResumeOptionNeeded() && LoadHelper.firstTimeLoginNotation == 0) {
                    int resumeAll = DialogHelper.getInstance().showConfirmAlert("هل تريد استئناف الاوقات");
                    if (resumeAll == 1) {
                        PlayingRoomsHallController.roomBoxes.forEach(roomBox -> {
                            roomBox.roomView.getRoomController().resumeCurrentTime();
                        });
                    } else {
                        PlayingRoomsHallController.roomBoxes.forEach(roomBox -> {
                            roomBox.roomView.getRoomController().EndCurrntTimer();
                            roomBox.roomView.getRoomController().fillPlayingTable();
                            roomBox.roomView.getRoomController().updateTotalPRice();
                            roomBox.roomView.getRoomController().UpdateFinalPriceLabel();
                        });
                    }
                }
                return null;
            });
            LauncherSceneController.LICENSE.addListener((observable, oldValue, newValue) -> {
                if (this.licenseBox != null) {
                    validateLicense((int) newValue);
                }
            });
            validateLicense(LauncherSceneController.LICENSE.getValue());

            if (!ControlPanel.getInstance().IS_DEMO) {
                removeTrialBox();
            } else {
                OnlineValidation.getInstance().addObserver((o, arg) -> {
                    Platform.runLater(() -> trialEndDate.setText(LocalDate.parse(
                            OnlineValidation.getInstance().getProgramCustomer().startDate,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"))
                            .plusDays(ControlPanel.getInstance().DEMO_NUM_OF_DAYS)
                            .format(DateTimeFormatter.ISO_DATE)));
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + ".initialize() :- " + ex);
        }
    }

    private void validateLicense(int value) {
        switch (value) {
            case 1:
                // License = True
                hideLicenseBox();
                break;
            case 2:
                // License = False (Blocked From Server)
                showLicenseBox("License Expired");
                break;
            case 3:
                // License = False (Internet Error)
                showLicenseBox("Cannot connect to Internet\nPlease fix the internet connection!");
                break;
            case 4:
                // License = False (Incorrect Time)
                showLicenseBox("Incorrect Time\nMake sure that PC time is correct");
                break;
            default:
                System.out.println("default condition");
        }
    }

    private void showLicenseBox(String msg) {
        System.out.println("----------------------------------------------------");
        System.out.println("showLicenseBox(" + msg + ")");
        licenseText.setText("");
        licenseText.setText(msg);
        System.out.println("licenseText: " + licenseText.getText());
        licenseBox.setVisible(true);
    }

    private void hideLicenseBox() {
        System.out.println("hideLicenseBox() ...");
        licenseBox.setVisible(false);
    }

    public void KeyCombinationHandler(Scene scene, Stage stage) {
        HashMap<KeyCombination, Runnable> hashMap = new HashMap<>();
        // CTRL + W
        hashMap.put(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN), () -> {
            closeWindow(new ActionEvent());
        });
        // CTRL + Q
        hashMap.put(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN), () -> {
            logout();
        });
        // CTRL + I
        hashMap.put(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN), () -> {
            aboutWidow();
        });
        // CTRL + Enter
        hashMap.put(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN), () -> {
            SingleInstance.getInstance().getCurrentStage().setFullScreen(!SingleInstance.getInstance().getCurrentStage().isFullScreen());
        });
        scene.getAccelerators().putAll(hashMap);
    }

    private void initAboutDialog() {
        try {
            AnchorPane aboutPane = FXMLLoader.load(getClass().getResource(Location.getInstance().get("ABOUT")));
            aboutDialog = new JFXDialog(AP, aboutPane, JFXDialog.DialogTransition.TOP);
        } catch (IOException ex) {
            Logger.writeLog("Exception in " + getClass().getName() + ".initAboutDialog() : " + ex);
        }
    }

    private void initDrawer() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Location.getInstance().get("TOOL_BAR")));
            VBox toolbar = fxmlLoader.load();
            toolbarController = fxmlLoader.getController();
            drawer1.setSidePane(toolbar);
            logoImage.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> drawer1.toggle());
            drawer1.setOnDrawerOpening(e -> drawer1.toFront());
            drawer1.setOnDrawerClosed(e -> drawer1.toBack());
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + ".initDrawer() :-" + e);
        }
    }

    public void closeWindow(Event event) {
        try {
            if (DialogHelper.getInstance().showConfirmAlert("هل انت متأكد من اغلاق البرنامج؟") == 1) {
                System.exit(0);
            } else {
                event.consume();
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> closeWindow() :- " + e);
        }
    }

    @FXML
    public void logout() {
        try {
            AdminValidationController.flag = false;
            dialog.admin_validation.AdminValidationController.tempAdminpass = "";
            PlayingRoomsHallController.getPlayingTimer();
            int flag = PlayingRoomsHallController.flagForPlayingTimers;
            if (flag == 0) {
                exit();
            } else {
                DialogHelper.getInstance().showConfirmAlert("هل انت متأكد من تسجيل الخروج؟");
                if (ConfirmAlertController.flag == 1) {
                    exit();
                    ConfirmAlertController.flag = 0;
                }
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> logout:-" + ex);
        }
    }

    public void aboutWidow() {
        try {
            if (aboutDialog.isVisible()) {
                return;
            }
            if (currentDialog != null && currentDialog.isVisible()) {
                currentDialog.close();
            }
            currentDialog = aboutDialog;
            aboutDialog.show();
            title_Label.requestFocus();
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> aboutWidow:-" + ex);
        }
    }

    public void exit() {
        try {
            LoadHelper.firstTimeLoginNotation = 1;
            Parent root = FXMLLoader.load(getClass().getResource(Location.getInstance().get("LOGINVIEW")));
            Stage window = new Stage();
            SingleInstance.getInstance().setCurrentStage(window);
            Scene scene = new Scene(root);
            window.setScene(scene);
            window.initStyle(StageStyle.UNDECORATED);
            BuilderUI.setAppDecoration(window);
            window.show();
            LoginController.changeState(User.CurrentUser, 0);
            ((Stage) (usernameLabel).getScene().getWindow()).close();
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> exit:-" + e);
        }
    }

    private void checkOfMyDay() {
        try {
            float m = 0;
            m += Methods.getTotalMoney("dailySheet WHERE daily=1");
            m += Methods.getCount("SELECT COUNT(*) From resturantReceets WHERE daily=1");
            m += Methods.getCount("SELECT COUNT(*) From deptData WHERE daily=1");
            m += Methods.getTotalMoney("dailyExpense  WHERE daily=1");
            if (m <= 0) {
                TempMethods.ClearData("myDay");
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> checkOfMyDay:- " + e);
        }
    }

    @FXML
    private void removeTrialBox() {
        if (trialBox != null) {
            ((AnchorPane) trialBox.getParent()).getChildren().remove(trialBox);
        }
    }

    @FXML
    private void openProgramWebpage() {
        try {
            trialEndDate.setText("hhh " + LocalTime.now());
            Desktop.getDesktop().browse(new URI("https://codeclinic-eg.github.io/"));
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + ".openProgramWebpage() => " + e);
        }
    }

}
