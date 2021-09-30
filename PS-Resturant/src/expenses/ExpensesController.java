package expenses;

import controlpanel.ControlPanel;
import util.gui.ComboBoxAutoComplete;
import dialog.edit.expenses.EditExpensesController;
import datamodel.ExpenseRecord;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import settings.financial.custody.FinancialCustodyController;
import util.Logger;
import datamodel.User;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.layout.VBox;
import util.db.DatabaseHandler;
import util.db.Methods;
import util.gui.load.DialogHelper;
import util.gui.load.Location;

public class ExpensesController implements Initializable {

    private ComboBoxAutoComplete customerCombo;
    private JFXButton currentBTN;
    public static int addingExp = 0;
    @FXML
    private JFXTreeTableView<ExpenseRecord> myTable;
    @FXML
    private TreeTableColumn<ExpenseRecord, String> DateCol;
    @FXML
    private TreeTableColumn<ExpenseRecord, String> UserCol;
    @FXML
    private TreeTableColumn<ExpenseRecord, String> NeedCol;
    @FXML
    private TreeTableColumn<ExpenseRecord, Float> PriceCol;
    @FXML
    private TreeTableColumn<ExpenseRecord, JFXButton> EDITCOL;
    @FXML
    private TextField NeedText;
    @FXML
    private TextField CostText;
    @FXML
    private JFXComboBox<String> NeedOld;
    @FXML
    private TextField CostTextOld;

    @FXML
    private JFXButton new_expenseBTN;
    @FXML
    private JFXButton prev_expenseBTN;
    @FXML
    private VBox new_expenseBox;
    @FXML
    private VBox prev_expenseBox;
    @FXML
    private VBox parentBox;
    @FXML
    private FontAwesomeIconView arrowIcon;
    @FXML
    private VBox optionsBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        expandOptions();
        currentBTN = prev_expenseBTN;
        addingExp = 0;
        DateCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("date"));
        UserCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("userName"));
        NeedCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("need"));
        PriceCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("price"));
        EDITCOL.setCellValueFactory(new TreeItemPropertyValueFactory<>("editBtn"));
        util.gui.HelperMethods.TableColumnAlignment(DateCol, UserCol, NeedCol, PriceCol, EDITCOL);
        customerCombo = new ComboBoxAutoComplete(NeedOld);
        fillEXTable();
    }

    private ObservableList<String> selectPrevExpensesNames() {
        ObservableList<String> list = FXCollections.observableArrayList();
        try {
            String sql = "SELECT DISTINCT needs FROM dailyexpense";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (DatabaseHandler.result.next()) {
                list.add(DatabaseHandler.result.getString(1));
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in ExpensesController -> selectPrevExpensesNames :- " + e);
        }
        return list;
    }

    @FXML
    private void AddAction(Event event) {
        try {
            if (DatabaseHandler.canAddMoreRowsInTable("dailyExpense")) {
                if (NeedText.getText().trim().equals("") || CostText.getText().trim().equals("")) {
                    DialogHelper.getInstance().showOKAlert("- يجب ادخال جميع البيانات ");
                    return;
                }

                float price = -1;
                try {
                    price = Float.parseFloat(CostText.getText().trim());
                    if (price <= 0) {
                        DialogHelper.getInstance().showOKAlert("- البيانات التي ادخلتها غير صحيحة ");
                        return;
                    }
                } catch (Exception e) {
                    DialogHelper.getInstance().showOKAlert("- البيانات التي ادخلتها غير صحيحة ");
                    return;
                }
                if (addExpense(NeedText.getText().trim(), price)) {
                    CostText.setText("");
                    NeedText.setText("");
                }
            } else {
                DialogHelper.getInstance().showOKAlert("لا يمكن اضافة المزيد في النسخة التجريبية");
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in ExpensesController -> AddAction");
        }
    }

    public static void insertStatement(ExpenseRecord rec, String tableName) {
        try {
            String sqlString = "INSERT INTO " + tableName + " VALUES (?,?,?,?,?,?)";    //Insert Statement
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlString);
            DatabaseHandler.stat.setInt(1, rec.id);
            DatabaseHandler.stat.setString(2, rec.date);
            DatabaseHandler.stat.setString(3, rec.userName);
            DatabaseHandler.stat.setString(4, rec.need);
            DatabaseHandler.stat.setFloat(5, rec.price);
            DatabaseHandler.stat.setInt(6, 1);
            DatabaseHandler.stat.execute();
            DatabaseHandler.stat.close();
        } catch (Exception e) {
            Logger.writeLog("Exception  In ExpensesConrtoller -> insertStatement:-" + e);
        }
    }

    private ObservableList<ExpenseRecord> selectExpenseData() {
        ObservableList<ExpenseRecord> list = FXCollections.observableArrayList();
        try {
            list.clear();
            String sqlState = "SELECT * FROM dailyExpense where daily=1 ORDER BY currentdate ASC";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();
            int id;
            float price;
            String date, uN, need;
            ExpenseRecord rec;
            while (DatabaseHandler.result.next()) {
                id = DatabaseHandler.result.getInt(1);
                date = DatabaseHandler.result.getString(2);
                uN = DatabaseHandler.result.getString(3);
                need = DatabaseHandler.result.getString(4);
                price = DatabaseHandler.result.getFloat(5);
                rec = new ExpenseRecord(
                        id,
                        date,
                        uN,
                        need,
                        price
                );
                rec.editBtn.setOnAction(editExEvenet(rec));
                list.add(rec);
            }
            DatabaseHandler.result.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Exception In ExpensesController -> selectExpenseData:- " + e);
            return list;
        }
    }

    public EventHandler<ActionEvent> editExEvenet(ExpenseRecord rec) {
        return (ActionEvent event) -> {
            try {
                EditExpensesController.previousRecord = rec;
                Parent root;
                root = FXMLLoader.load(getClass().getResource(Location.getInstance().get("EDIT_EXPENSES_RECORD")));
                Scene scene = new Scene(root);
                Stage stage = util.gui.BuilderUI.initStageUnDecorated(scene, "Edit", "info");
                stage.showAndWait();
                if (EditExpensesController.EXFlag == 1) {
                    String newN = EditExpensesController.previousRecord.need;
                    float newpr = EditExpensesController.previousRecord.price;
                    String sql = "update dailyexpense SET needs=?,price=? where id=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                    DatabaseHandler.stat.setString(1, EditExpensesController.previousRecord.need);
                    DatabaseHandler.stat.setFloat(2, EditExpensesController.previousRecord.price);
                    DatabaseHandler.stat.setInt(3, EditExpensesController.previousRecord.id);
                    DatabaseHandler.stat.execute();
                    fillEXTable();
                    addingExp = 1;
                }
            } catch (Exception ex) {
                Logger.writeLog("Exception in ExpenssessController -> editExEvenet:-" + ex);
            }
        };
    }

    private void fillEXTable() {
        ObservableList<ExpenseRecord> sers = selectExpenseData();
        TreeItem<ExpenseRecord> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        myTable.setRoot(root);
        myTable.setShowRoot(false);
    }

    @FXML
    private void updateData(MouseEvent event) {
        NeedOld.setItems(selectPrevExpensesNames());
        customerCombo.setItems(NeedOld.getItems());
    }

    @FXML
    private void AddActionOld(Event event) {
        try {
            if (DatabaseHandler.canAddMoreRowsInTable("dailyExpense")) {
                if (NeedOld.getValue() == null || CostTextOld.getText().trim().equals("")) {
                    DialogHelper.getInstance().showOKAlert("- يجب ادخال جميع البيانات");
                    return;
                }
                float cost = 0;
                try {
                    cost = Float.parseFloat(CostTextOld.getText().trim());
                    if (cost <= 0) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    DialogHelper.getInstance().showOKAlert("- التكلفة التي ادخلتها غير صحيحة");
                    return;
                }

                if (addExpense(NeedOld.getValue().trim(), cost)) {
                    NeedOld.setValue(null);
                    CostTextOld.setText("");
                }
            } else {
                DialogHelper.getInstance().showOKAlert("لا يمكن اضافة المزيد في النسخة التجريبية");
            }

        } catch (Exception e) {
            Logger.writeLog("EXception in ExpensesController -> AddActionOld :-" + e);
        }
    }

    public static int abilityToExpanse(float num) {
        try {
            double cuMoney = FinancialCustodyController.updateCurrentMoneyLabel();
            if (num > cuMoney) {
                return - 5;
            }
            return 1;
        } catch (Exception e) {
            Logger.writeLog("Exception in ExpensesController -> abilityToExpanse :-" + e);
            return - 5;
        }

    }

    public static void dealWithIprest(float num, String op) {
        try {
            double cuMoney = FinancialCustodyController.updateCurrentMoneyLabel();
            if (op.equalsIgnoreCase("pull")) {
                cuMoney -= num;
            } else if (op.equalsIgnoreCase("push")) {
                cuMoney += num;
            }
            String sql = "UPDATE el3ohda SET price=?";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setDouble(1, cuMoney);
            DatabaseHandler.stat.execute();
        } catch (Exception e) {
            Logger.writeLog("Exception in ExpensesController -> dealWithIprest :-" + e);
        }
    }

    @FXML
    private void oldAddKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            AddActionOld(event);
        }
    }

    @FXML
    private void newAddKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            AddAction(event);
        }
    }

    @FXML
    private void goToNewExpense() {
        toggleBTN(new_expenseBTN);
        new_expenseBox.setVisible(true);
        prev_expenseBox.setVisible(false);
    }

    @FXML
    private void goToPrevExpense() {
        toggleBTN(prev_expenseBTN);
        prev_expenseBox.setVisible(true);
        new_expenseBox.setVisible(false);
    }

    private void toggleBTN(JFXButton b) {
        currentBTN.getStyleClass().remove("dark-button");
        currentBTN = b;
        currentBTN.getStyleClass().add("dark-button");
    }

    @FXML
    private void expandOptions() {
        if (parentBox.getChildren().size() == 1) {
            // add options
            parentBox.getChildren().add(1, optionsBox);
            arrowIcon.setIcon(FontAwesomeIcon.ARROW_UP);
        } else {
            // remove options
            parentBox.getChildren().remove(optionsBox);
            arrowIcon.setIcon(FontAwesomeIcon.ARROW_DOWN);
        }
    }

    public boolean addExpense(String needName, float cost) {
        if (!ControlPanel.getInstance().EXPENSESS_FROM_DAILY_INCOME) {
            int x = abilityToExpanse(cost);
            if (x == -5) {
                DialogHelper.getInstance().showOKAlert("- هذا المبلغ غير متوافر ف العهدة!");
                return false;
            }
            dealWithIprest(cost, "pull");
        }
//        String d = Methods.getMyDay() + " " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
        String d = Methods.getMyDay();
        String userName = User.CurrentUser.userName;
        ExpenseRecord rec = new ExpenseRecord(
                (int) Methods.GetMaximum("id", "dailyexpense"),
                d,
                userName,
                needName,
                cost
        );
        insertStatement(rec, "dailyExpense");
        addingExp = 1;
        fillEXTable();
        return true;
    }
}
