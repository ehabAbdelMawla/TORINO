package archive.daydetails;

import datamodel.CafeRecord;
import datamodel.DailySheetRecord;
import datamodel.ExpenseRecord;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import controlpanel.ControlPanel;
import datamodel.Order;
import datamodel.Product;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.StatusIcon;
import util.gui.button.TableViewButton;

public class ArchiveDayDetailsController implements Initializable {

    @FXML
    private JFXTreeTableView<DailySheetRecord> myTable;
    @FXML
    private TreeTableColumn<DailySheetRecord, String> UserCol;
    @FXML
    private TreeTableColumn<DailySheetRecord, String> RoomNumCol;
    @FXML
    private TreeTableColumn<DailySheetRecord, String> fromCol;
    @FXML
    private TreeTableColumn<DailySheetRecord, String> toCol;
    @FXML
    private TreeTableColumn<DailySheetRecord, Float> preCol;
    @FXML
    private TreeTableColumn<DailySheetRecord, Float> disCol;
    @FXML
    private TreeTableColumn<DailySheetRecord, Float> INCol;
    @FXML
    private TreeTableColumn<DailySheetRecord, Integer> RecieptIdCol;
    @FXML
    private TreeTableColumn<DailySheetRecord, String> customerName;

    @FXML
    private JFXTreeTableView<ExpenseRecord> table3;
    @FXML
    private TreeTableColumn<ExpenseRecord, String> U3COL;
    @FXML
    private TreeTableColumn<ExpenseRecord, String> N3Col;
    @FXML
    private TreeTableColumn<ExpenseRecord, Float> P3Col;

    public static String myDay = "";

    @FXML
    private JFXTextArea EXNotes;

    @FXML
    private JFXTreeTableView<Order> DayOrdersTable;
    @FXML
    private TreeTableColumn<Order, String> cashier_Col;
    @FXML
    private TreeTableColumn<Order, Double> debt_Col;
    @FXML
    private TreeTableColumn<Order, Double> amountPaid_Col;
    @FXML
    private TreeTableColumn<Order, Double> totalAmount_Col;
    @FXML
    private TreeTableColumn<Order, Double> discount_Col;
    @FXML
    private TreeTableColumn<Order, Double> subTotalAmount_Col;
    @FXML
    private TreeTableColumn<Order, String> billKind_Col;
    @FXML
    private TreeTableColumn<Order, JFXButton> billNumber_Col;
    @FXML
    private TreeTableColumn<Order, String> date_Col;
    @FXML
    private TreeTableColumn<Order, String> time_Col;
    @FXML
    private TreeTableColumn<Order, TableViewButton> customer_Col;
    @FXML
    private TreeTableColumn<Order, StatusIcon> statusBox_Col;
    @FXML
    private VBox playingBox;
    @FXML
    private ScrollPane scrollPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (controlpanel.ControlPanel.getInstance().HAS_PLAYSTATION) {
            UserCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("userName"));
            RoomNumCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("roomNum"));
            fromCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("from"));
            toCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("to"));
            preCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("preDis"));
            disCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("Dis"));
            INCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("inComeVal"));
            RecieptIdCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("idButton"));
            customerName.setCellValueFactory(new TreeItemPropertyValueFactory<>("customerName"));
            util.gui.HelperMethods.TableColumnAlignment(UserCol, RoomNumCol, fromCol, toCol, preCol, disCol, INCol, RecieptIdCol, customerName);
        } else {
            ((VBox) playingBox.getParent()).getChildren().remove(playingBox);
        }

        U3COL.setCellValueFactory(new TreeItemPropertyValueFactory<>("userName"));
        N3Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("need"));
        P3Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("price"));
        util.gui.HelperMethods.TableColumnAlignment(U3COL, N3Col, P3Col);

        // ... Cafe Table ...
        time_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("time"));
        date_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("date"));
        billNumber_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("billNumber"));
        billKind_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("orderKind"));
        subTotalAmount_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("subTotalAmount"));
        discount_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("discountAmount"));
        totalAmount_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("totalAmount"));
        amountPaid_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("paidAmount"));
        debt_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("debtAmount"));
        cashier_Col.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getValue().getCashier().getUserName()));
        customer_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("showCustomerData"));
        statusBox_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("statusIcon"));

        fillEXTable();
        if (!ControlPanel.getInstance().CUSTOMER_DATA) {
            DayOrdersTable.getColumns().remove(customer_Col);
            DayOrdersTable.getColumns().remove(debt_Col);
        }

        // Open Stage with relative dimensions to the Screen
        util.gui.HelperMethods.setRelativeSize(scrollPane, 0.6, 1000, 535);
    }

    private String getNotesOfSections(String sec) {
        String notes = "";
        try {
            String sql = "Select note FROM cafeandexpnotes where source=? AND Day=?";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setString(1, sec);
            DatabaseHandler.stat.setString(2, myDay);
            DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
            while (DatabaseHandler.result.next()) {
                notes += DatabaseHandler.result.getString(1) + "#";
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> getNotesOfSections :- " + e);
        }
        return notes;
    }

    @FXML
    private void closeWindow(Event event) {
        ((Stage) ((Node) (event.getSource())).getScene().getWindow()).close();
    }

    private ObservableList<DailySheetRecord> selectDailySheetData() {
        ObservableList<DailySheetRecord> list = FXCollections.observableArrayList();
        try {
            list.clear();
            String sqlState = "SELECT * FROM dailysheet where actDate=? and daily=0";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlState);
            DatabaseHandler.stat.setString(1, myDay);
            DatabaseHandler.result = DatabaseHandler.stat.executeQuery();

            while (DatabaseHandler.result.next()) {

                DailySheetRecord rec
                        = new DailySheetRecord(
                                DatabaseHandler.result.getInt(1),
                                DatabaseHandler.result.getString(2),
                                DatabaseHandler.result.getString(3),
                                DatabaseHandler.result.getString(4),
                                DatabaseHandler.result.getString(5),
                                DatabaseHandler.result.getString(6),
                                DatabaseHandler.result.getDouble(7),
                                DatabaseHandler.result.getDouble(8),
                                DatabaseHandler.result.getDouble(9),
                                DatabaseHandler.result.getDouble(10),
                                DatabaseHandler.result.getDouble(11),
                                DatabaseHandler.result.getString(12),
                                DatabaseHandler.result.getString(13),
                                DatabaseHandler.result.getString(14),
                                DatabaseHandler.result.getString(15),
                                DatabaseHandler.result.getString(16)
                        );

                list.add(rec);
            }
            DatabaseHandler.result.close();
            DatabaseHandler.stat.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Exception in arcDetailsController -> selectDailySheetData:-" + e);
            return list;
        }
    }

    public void fillDailySheetTable() {
        ObservableList<DailySheetRecord> sers = selectDailySheetData();
        TreeItem<DailySheetRecord> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        myTable.setRoot(root);
        myTable.setShowRoot(false);
    }

    private ObservableList<CafeRecord> selectData(String tableName, String s) {
        ObservableList<CafeRecord> list = FXCollections.observableArrayList();
        try {
            list.clear();
            String sqlState = "SELECT * FROM " + tableName + s;
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();
            int receetNum, num;
            float totalPrice;
            String drinkName, productCateg, productArgs;
            CafeRecord rec;
            while (DatabaseHandler.result.next()) {
                receetNum = DatabaseHandler.result.getInt(1);
                drinkName = DatabaseHandler.result.getString(2);
                num = DatabaseHandler.result.getInt(3);
                totalPrice = DatabaseHandler.result.getFloat(4);
                productCateg = DatabaseHandler.result.getString(5);
                productArgs = DatabaseHandler.result.getString(6);
                rec = new CafeRecord(receetNum, drinkName, num, totalPrice, productCateg, Product.convertStringToListOfArgs(productArgs));
                list.add(rec);
            }
            DatabaseHandler.result.close();
            DatabaseHandler.stat.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> selectData:- " + e);
            return list;
        }
    }

    private ObservableList<ExpenseRecord> selectExpenseData() {
        ObservableList<ExpenseRecord> list = FXCollections.observableArrayList();
        try {
            list.clear();
            String sqlState = "SELECT * FROM dailyexpense where currentdate=? and daily=0";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlState);
            DatabaseHandler.stat.setString(1, myDay);
            DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
            int id;
            String date, userName, need;
            float price;
            ExpenseRecord rec;
            while (DatabaseHandler.result.next()) {
                id = DatabaseHandler.result.getInt(1);
                date = DatabaseHandler.result.getString(2);
                userName = DatabaseHandler.result.getString(3);
                need = DatabaseHandler.result.getString(4);
                price = DatabaseHandler.result.getFloat(5);
                rec = new ExpenseRecord(id, date, userName, need, price);
                list.add(rec);
            }
            DatabaseHandler.result.close();
            DatabaseHandler.stat.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> selectExpenseData :-" + e);
            return list;
        }
    }

    public void fillEXTable() {
        ObservableList<ExpenseRecord> sers = selectExpenseData();
        TreeItem<ExpenseRecord> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        table3.setRoot(root);
        table3.setShowRoot(false);
        EXNotes.setText(getNotesOfSections("EX").replaceAll("#", "\n"));
    }

    @FXML
    private void KeyPrs(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            closeWindow((Event) event);
        }
    }

    public void fillCafeTable() {
        //DayOrdersTable
        ObservableList<Order> sers = Order.fillDailyCafeTable(myDay);
        TreeItem<Order> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        DayOrdersTable.setRoot(root);
        DayOrdersTable.setShowRoot(false);
    }

}
