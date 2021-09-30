package settings.employees;

import dialog.alert.confirm.ConfirmAlertController;
import dialog.edit.employee.EditEmployeeController;
import datamodel.User;
import dialog.admin_validation.AdminValidationController;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import datamodel.Order;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import login.LoginController;
import util.gui.load.Location;
import static settings.SettingsController.datSettingsButton;
import util.Logger;
import util.db.DatabaseHandler;
import util.db.Methods;
import util.gui.BuilderUI;
import util.gui.HelperMethods;
import util.gui.load.DialogHelper;
import util.gui.load.LoadHelper;
import util.validation.SingleInstance;

public class EmployeesSettingsController implements Initializable {

    @FXML
    private JFXTreeTableView<User> myTable;
    @FXML
    private TreeTableColumn<User, Button> DelCol;
    @FXML
    private TreeTableColumn<User, Button> EditCol;
    @FXML
    private TreeTableColumn<User, String> acessCol;
    @FXML
    private TreeTableColumn<User, String> NameCol;
    @FXML
    private JFXTextField UserName;
    @FXML
    private ToggleGroup sala7ia;
    @FXML
    private RadioButton adRadio;
    @FXML
    private RadioButton empradio;
    @FXML
    private JFXTextField PasswordTextField;
    @FXML
    private JFXPasswordField PasswordField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PasswordTextField.setVisible(false);
        PasswordTextField.textProperty().bindBidirectional(PasswordField.textProperty());
        NameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("userName"));
        acessCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("aceessConstrains"));
        EditCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("editBTN"));
        DelCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("DelBTN"));

        HelperMethods.TableColumnAlignment(NameCol,
                acessCol,
                EditCol,
                DelCol);

        fillEmployeeTable();
    }

    @FXML
    private void goToSettings() {
        try {
            homepage.HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_Settings"));
            homepage.HomePageController.title_Label.setText("الإعدادات");
        } catch (Exception e) {
            Logger.writeLog("Exception in EmployeeSettingsController -> goToSettings :- " + e);
        }
    }

    @FXML
    private void AddUser() {
        try {
            if (DatabaseHandler.canAddMoreRowsInTable("users")) {
                if (UserName.getText().trim().equals("") || PasswordTextField.getText().trim().equals("")) {
                    DialogHelper.getInstance().showOKAlert("- يجب ادخال جميع البيانات");
                    return;
                } else if (UserName.getText().trim().contains("#") || UserName.getText().trim().contains("@")) {
                    DialogHelper.getInstance().showOKAlert("-لا يمكن ان يحتوي الاسم علي @ او #");
                    return;
                }
                String access = "موظف";
                if (adRadio.isSelected()) {
                    access = "أدمن";
                }
                insertInUsersTable(new User(UserName.getText().trim(), PasswordTextField.getText(), access, 0));
            } else {
                DialogHelper.getInstance().showOKAlert("لا يمكن اضافة المزيد في النسخة التجريبية");
            }

        } catch (Exception e) {
            Logger.writeLog("Exception in EmployeeSettingsController -> AddUser :" + e);
        }
    }

    public void fillEmployeeTable() {
        ObservableList<User> sers = selectEmployeeData();
        TreeItem<User> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        myTable.setRoot(root);
        myTable.setShowRoot(false);
    }

    public ObservableList<User> selectEmployeeData() {
        ObservableList<User> list = FXCollections.observableArrayList();
        try {
            list.clear();
            String sqlState = "";
            if (AdminValidationController.tempAdminpass.equals(getPassOFOwner()) || User.CurrentUser.aceessConstrains.equalsIgnoreCase("مالك")) {
                sqlState = "SELECT * FROM users ORDER BY id ASC";
                EditEmployeeController.accessFalg = 1;
                datSettingsButton.setDisable(false);
                adRadio.setVisible(true);
            } else if (User.CurrentUser.aceessConstrains.equalsIgnoreCase("أدمن")) {
                sqlState = "SELECT * FROM users where accessConstrain='موظف' OR userName='" + User.CurrentUser.userName + "' ORDER BY id ASC";
                EditEmployeeController.accessFalg = 0;
                adRadio.setVisible(false);
                datSettingsButton.setDisable(true);
            } else if (!AdminValidationController.tempAdminpass.equals(getPassOFOwner()) && !AdminValidationController.tempAdminpass.equals("")) {
                sqlState = "SELECT * FROM users where accessConstrain='موظف' OR password='" + AdminValidationController.tempAdminpass + "' ORDER BY id ASC";
                EditEmployeeController.accessFalg = 0;
                adRadio.setVisible(false);
                datSettingsButton.setDisable(true);
            } else {
                sqlState = "SELECT * FROM users where accessConstrain='موظف' ORDER BY id ASC";
                adRadio.setVisible(false);
                datSettingsButton.setDisable(true);
            }
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();
            String a, b, c;
            int d;
            User rec;
            while (DatabaseHandler.result.next()) {
                a = DatabaseHandler.result.getString(1);
                b = DatabaseHandler.result.getString(2);
                c = DatabaseHandler.result.getString(3);
                d = DatabaseHandler.result.getInt(4);
                rec = new User(a, b, c, d);
                rec.DelBTN.setDisable(rec.aceessConstrains.equalsIgnoreCase("مالك"));
                rec.DelBTN.setOnAction(DeleteThisRecord(rec));
                rec.editBTN.setOnAction(EditUSerRecord(rec));
                list.add(rec);
            }
            DatabaseHandler.result.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Exception In EmployeeSettingsController -> selectEmployeeData:-" + e);
            return list;
        }
    }

    public EventHandler<ActionEvent> EditUSerRecord(User rec) {
        return (ActionEvent event) -> {
            try {
                EditEmployeeController.us = rec;
                boolean m = EditEmployeeController.us.equals(User.CurrentUser);
                Parent root = FXMLLoader.load(getClass().getResource(Location.getInstance().get("EDIT_EMPLOYEE")));
                Scene scene = new Scene(root);
                Stage stage = util.gui.BuilderUI.initStageUnDecorated(scene, "Edit Employee", "info");
                stage.showAndWait();
                if (EditEmployeeController.flag == 1) {
                    User NEWUSDtat = EditEmployeeController.us;
                    if (m) {
                        User.CurrentUser = NEWUSDtat;
                        homepage.HomePageController.username_Static_Label.setText(User.CurrentUser.getUserName());
                    }
                    String sql = "UPDATE users SET userName=? ,password=? ,accessConstrain=? Where userName=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                    DatabaseHandler.stat.setString(1, NEWUSDtat.userName);
                    DatabaseHandler.stat.setString(2, NEWUSDtat.password);
                    DatabaseHandler.stat.setString(3, NEWUSDtat.aceessConstrains);
                    DatabaseHandler.stat.setString(4, rec.userName);
                    DatabaseHandler.stat.execute();
                    DatabaseHandler.stat.close();
                    fillEmployeeTable();
                    if (!NEWUSDtat.userName.equals(rec.userName)) {
                        DatabaseHandler.con.prepareStatement("UPDATE resturantReceets SET userName='" + NEWUSDtat.userName + "' WHERE userName='" + rec.userName + "'").execute();
                        Order.fillDailyCafeTable();
                    }

                }
            } catch (org.sqlite.SQLiteException e) {
                DialogHelper.getInstance().showOKAlert("- هذا الاسم موجود بالفعل");
            } catch (Exception e) {
                Logger.writeLog("Exception In EmployeeSettingsController -> EditUSerRecord:-" + e);
            }
        };
    }

    public EventHandler<ActionEvent> DeleteThisRecord(User rec) {
        return (ActionEvent event) -> {
            try {
                if (rec.aceessConstrains.equalsIgnoreCase("مالك")) {
                    DialogHelper.getInstance().showOKAlert("لا يمكن حذف المالك");
                    return;
                }
                DialogHelper.getInstance().showConfirmAlert("هل انت متأكد من حذف المستحدم؟");
                if (ConfirmAlertController.flag == 1) {
                    ConfirmAlertController.flag = 0;
                    if (rec.userName.equalsIgnoreCase(User.CurrentUser.userName)) {
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
                        ((Stage) UserName.getScene().getWindow()).close();
                    }
                    String sql = "DELETE FROM users Where userName=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                    DatabaseHandler.stat.setString(1, rec.userName);
                    DatabaseHandler.stat.execute();
                    DatabaseHandler.stat.close();
                    fillEmployeeTable();
                }
            } catch (Exception e) {
                Logger.writeLog("Error In EmployeeSettingsController -> DeleteThisRecord:-" + e);
            }
        };
    }

    public void insertInUsersTable(User myrec) {
        try {
            String sqlString = "INSERT INTO users VALUES (?,?,?,?,?)";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlString);
            DatabaseHandler.stat.setString(1, myrec.userName);
            DatabaseHandler.stat.setString(2, myrec.password);
            DatabaseHandler.stat.setString(3, myrec.aceessConstrains);
            DatabaseHandler.stat.setInt(4, myrec.active);
            DatabaseHandler.stat.setInt(5, (int) Methods.GetMaximum("id", "users"));
            DatabaseHandler.stat.execute();
            DatabaseHandler.stat.close();
            UserName.setText("");
            PasswordTextField.setText("");
            empradio.setSelected(true);
            fillEmployeeTable();
        } catch (org.sqlite.SQLiteException e) {
            DialogHelper.getInstance().showOKAlert("- هذا الاسم موجود بالفعل");
        } catch (Exception e) {
            Logger.writeLog("Error In EmployeeSettingsController -> insertInUsersTable Method" + e);
        }
    }

    public static String getPassOFOwner() {
        try {
            String sql = "select password from users where accessConstrain='مالك'";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (DatabaseHandler.result.next()) {
                String pass = DatabaseHandler.result.getString(1);
                return pass;
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in in EmployeeSettingsController -> getPassOFOwner:-" + e);
        }
        return null;
    }

    @FXML
    private void eyeBTN() {
        PasswordField.setVisible(false);
        PasswordTextField.setVisible(true);
    }

    @FXML
    private void eyeBTN2() {
        PasswordField.setVisible(true);
        PasswordTextField.setVisible(false);
    }

    @FXML
    private void keyAct(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            AddUser();
        }
    }
}
