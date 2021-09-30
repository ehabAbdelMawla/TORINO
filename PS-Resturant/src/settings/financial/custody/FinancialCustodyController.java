package settings.financial.custody;

import controlpanel.ControlPanel;
import datamodel.imprestClass;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import datamodel.User;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.load.DialogHelper;
import util.gui.load.LoadHelper;

public class FinancialCustodyController implements Initializable {

    @FXML
    private TextField MoneyTextField;
    @FXML
    private Label CurrentMoney;
    @FXML
    private JFXTreeTableView<imprestClass> myTable;
    @FXML
    private TreeTableColumn<imprestClass, Double> moneyCol;
    @FXML
    private TreeTableColumn<imprestClass, String> timeCol;
    @FXML
    private TreeTableColumn<imprestClass, String> dateCol;
    @FXML
    private TreeTableColumn<imprestClass, String> userCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        moneyCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("price"));
        timeCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("acTime"));
        dateCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("acDate"));
        userCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("us"));
        util.gui.HelperMethods.TableColumnAlignment(moneyCol, timeCol, dateCol, userCol);
        fillTable();
    }

    public void fillTable() {
        ObservableList<imprestClass> sers = selectimprestData("");
        TreeItem<imprestClass> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        myTable.setRoot(root);
        myTable.setShowRoot(false);
        CurrentMoney.setText(updateCurrentMoneyLabel() + ControlPanel.getInstance().getCurrency());
    }

    private ObservableList<imprestClass> selectimprestData(String s) {
        ObservableList<imprestClass> data = FXCollections.observableArrayList();
        try {
            String sql = "SELECT * FROM add3ohda " + s + " ORDER BY actDate ASC";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            String s1, s2, s3;
            double d;
            while (DatabaseHandler.result.next()) {
                s1 = DatabaseHandler.result.getString(1);
                s2 = DatabaseHandler.result.getString(2);
                s3 = DatabaseHandler.result.getString(3);
                d = DatabaseHandler.result.getDouble(4);
                data.add(new imprestClass(s1, s2, s3, d));
            }

        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> selectimprestData :- " + e);
        }
        return data;
    }

    @FXML
    private void KEv(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            addImprest();
        }
    }

    public static double updateCurrentMoneyLabel() {
        double mon = 0;
        try {
            String sql = "SELECT * FROM el3ohda";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (DatabaseHandler.result.next()) {
                mon = DatabaseHandler.result.getDouble(1);
            }
        } catch (Exception e) {
            Logger.writeLog("EXception in ImprestSettingsController -> updateCurrentMoneyLabel :- " + e);
        }
        return mon;
    }

    @FXML
    private void addImprest() {
        try {
            if (MoneyTextField.getText().trim().equals("")) {
                DialogHelper.getInstance().showOKAlert("- يجب ادخال المبلغ");
                return;
            }
            double money = 0;
            try {
                money = Double.parseDouble(MoneyTextField.getText().trim());
                if (money <= 0) {
                    throw new Exception();
                }
            } catch (Exception e) {
                DialogHelper.getInstance().showOKAlert("-القيمة التي ادخلتها غير صحيحة");
                return;
            }
            insertInto3ohda(new imprestClass(User.CurrentUser.userName,
                    LocalDate.now().toString(),
                    LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")),
                    money));
            fillTable();
            MoneyTextField.setText("");
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> addImprest :-" + e);
        }
    }

    private void insertInto3ohda(imprestClass rec) {
        try {
            String sql = "INSERT INTO add3ohda VALUES (?,?,?,?)";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setString(1, rec.us);
            DatabaseHandler.stat.setString(2, rec.acDate);
            DatabaseHandler.stat.setString(3, rec.acTime);
            DatabaseHandler.stat.setDouble(4, rec.price);
            DatabaseHandler.stat.execute();
            double mon = 0;
            sql = "SELECT * FROM el3ohda";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (DatabaseHandler.result.next()) {
                mon = DatabaseHandler.result.getDouble(1);
            }
            mon += rec.price;
            sql = "UPDATE el3ohda SET price=?";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setDouble(1, mon);
            DatabaseHandler.stat.execute();
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> insertInto3ohda :- " + e);
        }
    }

    @FXML
    private void goToSettings() {
        try {
            homepage.HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_Settings"));
            homepage.HomePageController.title_Label.setText("الإعدادات");
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> goToSettings:- " + e);
        }
    }

}
