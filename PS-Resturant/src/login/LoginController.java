package login;

import controlpanel.ControlPanel;
import datamodel.User;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import static launcher.LauncherSceneController.setFirstStartTime;
import util.gui.load.Location;
import util.Logger;
import util.gui.BuilderUI;
import util.gui.load.LoadHelper;
import util.validation.SingleInstance;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.WindowEvent;
import util.db.DatabaseHandler;

public class LoginController implements Initializable {

    private ResultSet result = null;
    private final SimpleBooleanProperty isFinished = new SimpleBooleanProperty(false);
    // =============================
    @FXML
    private JFXTextField UserNameField, PasswordTextField;
    @FXML
    private JFXPasswordField PasswordField;
    @FXML
    private ImageView loadingIMG;
    @FXML
    private JFXCheckBox saveLoginCheckBox;
    @FXML
    private HBox errorBox;
    @FXML
    private Label errorLabel;
    @FXML
    private JFXButton logInBTN;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            errorBox.setVisible(false);
            loadingIMG.setVisible(false);
            PasswordTextField.setVisible(false);
            PasswordTextField.textProperty().bindBidirectional(PasswordField.textProperty());
            isFinished.addListener((o) -> {
                if (isFinished.getValue()) {
                    goToHomepage();
                }
            });

            PasswordTextField.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
                if (oldScene == null && newScene != null) {
                    newScene.windowProperty().addListener((observable, oldWindow, newWindow) -> {
                        if (oldWindow == null && newWindow != null) {
                            ((Stage) newWindow).setOnCloseRequest((WindowEvent event) -> {
                                closeWindow(event);
                            });
                        }
                    });
                }
            });
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> initialize:-" + e);
        }
    }

    @FXML
    private void loginAction(Event event) {
        try {
            if (!logInBTN.isDisable()) {
                User.CurrentUser = loginCheck(UserNameField.getText(), PasswordField.getText());
                if (User.CurrentUser == null) {
                    errorBox.setVisible(true);
                    return;
                }
                if (ControlPanel.getInstance().FREE_TRIAL_TIME) {
                    setFirstStartTime();
                }
                if (saveLoginCheckBox.isSelected()) {
                    changeState(User.CurrentUser, 1);
                }
                prepareHomepage(event);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> loginAction:-" + e);
        }
    }

    public void prepareHomepage(Event event) {
        try {
            loadingIMG.setVisible(true);
            UserNameField.setDisable(true);
            PasswordTextField.setDisable(true);
            PasswordField.setDisable(true);
            saveLoginCheckBox.setDisable(true);
            logInBTN.setDisable(true);
            errorBox.setVisible(false);
            if (LoadHelper.firstTimeLoginNotation == 0) {
                new Thread(() -> {
                    LoadHelper.getInstance().loadScreens(true);
                    isFinished.set(true);
                }).start();
            } else {
                LoadHelper.getInstance().screenMap.put("root_Homepage", FXMLLoader.load(getClass().getResource(Location.getInstance().get("HOMEPAGEVIEW"))));
                isFinished.set(true);
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> goToHomePage:-" + ex);
        }
    }

    private void goToHomepage() {
        Platform.runLater(() -> {
            Stage g = new Stage();
            SingleInstance.getInstance().setCurrentStage(g);
            BuilderUI.setAppDecoration(g);
            g.setScene(new Scene(LoadHelper.getInstance().screenMap.get("root_Homepage")));
            g.setMaximized(true);
            g.show();
            ((Stage) PasswordField.getScene().getWindow()).close();
        });
    }

    public User loginCheck(String Us, String pass) {
        User cu = null;
        try {
            String sqlState = "SELECT * FROM users where userName=? ";
            PreparedStatement stat = DatabaseHandler.con.prepareStatement(sqlState);
            stat.setString(1, Us);
            result = stat.executeQuery();
            while (result.next()) {
                cu = new User(result.getString(1), result.getString(2), result.getString(3), result.getInt(4));
                break;
            }
            if (cu != null) {
                if (!(cu.userName.equals(UserNameField.getText().trim()))) {
                    errorLabel.setText("اسم المستخدم غير صحيح!");
                    cu = null;
                } else if (!(cu.password.equals(PasswordField.getText()))) {
                    errorLabel.setText("كلمة السر غير صحيحه !");
                    cu = null;
                }
            } else {
                errorLabel.setText("اسم المستخدم غير صحيح !");
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> loginCheck:-" + e);
        }
        return cu;
    }

    public static void changeState(User user, int newValue) {
        try {
            String sqlState = "UPDATE users SET active=? where userName=?";
            PreparedStatement stat = DatabaseHandler.con.prepareStatement(sqlState);
            stat.setInt(1, newValue);
            stat.setString(2, user.getUserName());
            stat.execute();
        } catch (Exception e) {
            Logger.writeLog("Exception in LoginController -> changeState : " + e);
        }
    }

    @FXML
    private void eyeBTN(MouseEvent event) {
        PasswordField.setVisible(false);
        PasswordTextField.setVisible(true);
    }

    @FXML
    private void eyeBTN2(MouseEvent event) {
        PasswordField.setVisible(true);
        PasswordTextField.setVisible(false);
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
        ((Stage) (UserNameField.getScene().getWindow())).setX(e.getScreenX() - xOffset);
        ((Stage) (UserNameField.getScene().getWindow())).setY(e.getScreenY() - yOffset);
    }

    @FXML
    private void closeWindow(Event event) {
        System.exit(0);
    }

}
