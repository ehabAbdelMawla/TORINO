package storage;

import util.gui.ComboBoxAutoComplete;
import datamodel.CafePullREcord;
import datamodel.StoreRecord;
import datamodel.storemoves;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import static customerdata.CustomersDataController.getFileDestenation;
import static customerdata.CustomersDataController.setSheetColumnWidth;
import static customerdata.CustomersDataController.typeInCell;
import datamodel.ArgumentsClass;
import datamodel.CafeRecord;
import datamodel.User;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import restaurant.RestaurantHomeController;
import util.Logger;
import util.db.DatabaseHandler;
import util.db.Methods;
import util.gui.DatePickerFromat;
import util.gui.button.TableViewButton;
import util.gui.load.DialogHelper;

public class StorageController implements Initializable {

    private boolean isExpanded = true;
    private JFXButton SelectedButton;
    private VBox SelectedBox;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    // =============================
    @FXML
    private JFXTreeTableView<StoreRecord> CurrentTable;
    @FXML
    private TreeTableColumn<StoreRecord, Float> CounCol;
    @FXML
    private TreeTableColumn<StoreRecord, String> proCol;
    @FXML
    private TreeTableColumn<StoreRecord, TableViewButton> editCol;
    @FXML
    private TreeTableColumn<StoreRecord, TableViewButton> delCol;
    @FXML
    private JFXTreeTableView<storemoves> enterrable;
    @FXML
    private TreeTableColumn<storemoves, Float> num1Col;
    @FXML
    private TreeTableColumn<storemoves, String> pro1Col;
    @FXML
    private TreeTableColumn<storemoves, String> user1Col;
    @FXML
    private TreeTableColumn<storemoves, String> time1Col;
    @FXML
    private TreeTableColumn<storemoves, String> day1Col;
    @FXML
    private JFXTreeTableView<storemoves> exitTable;
    @FXML
    private TreeTableColumn<storemoves, Float> num2Col;
    @FXML
    private TreeTableColumn<storemoves, String> pro2Col;
    @FXML
    private TreeTableColumn<storemoves, String> user2Col;
    @FXML
    private TreeTableColumn<storemoves, String> time2Col;
    @FXML
    private TreeTableColumn<storemoves, String> day2Col;
    @FXML
    private TextField newProTextF;
    @FXML
    private TextField addingNum;
    @FXML
    private TextField newnum;
    @FXML
    private JFXComboBox<String> myProductComBox;
    @FXML
    private JFXComboBox<String> myProductComBox2;
    @FXML
    private TextField numOfPull1;
    @FXML
    public TextField searchComBox;
    @FXML
    private VBox vb;
    @FXML
    private ScrollPane sp;
    @FXML
    private JFXTreeTableView<CafePullREcord> t4;
    @FXML
    private TreeTableColumn<CafePullREcord, Integer> t4ProNum;
    @FXML
    private TreeTableColumn<CafePullREcord, String> t4proNmae;
    @FXML
    private TreeTableColumn<CafePullREcord, Integer> t4argNum;
    @FXML
    private TreeTableColumn<CafePullREcord, String> t4argName;
    @FXML
    private TreeTableColumn<CafePullREcord, String> t4Day;

    ComboBoxAutoComplete customeProductComBox;
    ComboBoxAutoComplete customeProductComBox2;
    @FXML
    private JFXButton cashierSellsProducts_BTN;
    @FXML
    private JFXButton directSellProducts_BTN;
    @FXML
    private JFXButton incomeProducts_BTN;
    @FXML
    private JFXButton currentProducts_BTN;
    @FXML
    private VBox currentBox;
    @FXML
    private VBox incomeingBox;
    @FXML
    private VBox directExitBox;
    @FXML
    private VBox cashierExitBox;
    @FXML
    private HBox exportDataContainer, addAndPullContainer;
    @FXML
    private VBox addAndPullContainerParent;
    @FXML
    private FontAwesomeIconView arrowIcon;
    @FXML
    private JFXDatePicker fromDate, toDate;

    private void focusButton(JFXButton b, VBox box) {
        SelectedButton.getStyleClass().remove("cashier-buttons-focused");
        b.getStyleClass().add("cashier-buttons-focused");
        SelectedButton = b;

        SelectedBox.setVisible(false);
        box.setVisible(true);
        SelectedBox = box;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SelectedButton = currentProducts_BTN;
        SelectedBox = currentBox;
        currentProducts_BTN.getStyleClass().add("cashier-buttons-focused");
        currentBox.setVisible(true);
        focusButton(currentProducts_BTN, currentBox);

        // Current Storage Table
        proCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("productName"));
        CounCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("Count"));
        editCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("editBtn"));
        delCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("delBtn"));
        fillCurrentStoreTable("");

        // Incoming Table
        day1Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("Day"));
        time1Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("Time"));
        user1Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("User"));
        pro1Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("ProName"));
        num1Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("Count"));
        fillEnterStoreTable("storeenter");

        // Direct Exit Table
        day2Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("Day"));
        time2Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("Time"));
        user2Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("User"));
        pro2Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("ProName"));
        num2Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("Count"));
        fillExitStoreTable("storeexit");

        // Cashier Exit Table
        t4ProNum.setCellValueFactory(new TreeItemPropertyValueFactory<>("numOfProd"));
        t4proNmae.setCellValueFactory(new TreeItemPropertyValueFactory<>("prodName"));
        t4argNum.setCellValueFactory(new TreeItemPropertyValueFactory<>("numOfArg"));
        t4argName.setCellValueFactory(new TreeItemPropertyValueFactory<>("argName"));
        t4Day.setCellValueFactory(new TreeItemPropertyValueFactory<>("day"));
        fillt4("");

        customeProductComBox = new ComboBoxAutoComplete(myProductComBox);
        customeProductComBox2 = new ComboBoxAutoComplete(myProductComBox2);

        if (!controlpanel.ControlPanel.getInstance().EXPORT_STORAGE_DATA) {
            ((VBox) exportDataContainer.getParent()).getChildren().remove(exportDataContainer);
            exportDataContainer.getChildren().clear();
            exportDataContainer = null;
        }
        expandOptions();
        DatePickerFromat.set(toDate);
        DatePickerFromat.set(fromDate);
        toDate.setValue(LocalDate.now());
    }

    private ObservableList<CafePullREcord> selectarchOfPull(String adStr) {
        ObservableList<CafePullREcord> list = FXCollections.observableArrayList();
        try {
            String sql = "SELECT * FROM archpullstore " + adStr;
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            String s1, s2, s3;
            int n1, n3;
            double n2;
            while (DatabaseHandler.result.next()) {
                n1 = DatabaseHandler.result.getInt(1);
                s1 = DatabaseHandler.result.getString(2);
                s2 = DatabaseHandler.result.getString(3);
                n2 = DatabaseHandler.result.getDouble(4);
                s3 = DatabaseHandler.result.getString(5);
                n3 = DatabaseHandler.result.getInt(6);
                list.add(new CafePullREcord(n1, s1, s2, n2, s3, n3));
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> selectarchOfPull:- " + e);
        }
        return list;
    }

    public void fillt4(String adStr) {
        ObservableList<CafePullREcord> sers = selectarchOfPull(adStr);
        TreeItem<CafePullREcord> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        t4.setRoot(root);
        t4.setShowRoot(false);
    }

    public void insertIntoCurrentStoreTable(StoreRecord rec) {
        try {
            String sqlString = "INSERT INTO store VALUES (?,?,?)";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlString);
            DatabaseHandler.stat.setString(1, rec.productName);
            DatabaseHandler.stat.setFloat(2, rec.Count);
            DatabaseHandler.stat.setInt(3, (int) Methods.GetMaximum("id", "store"));
            DatabaseHandler.stat.execute();
            storemoves newProduct = new storemoves(
                    LocalDate.now().toString(),
                    LocalTime.now().format(dateTimeFormatter),
                    User.CurrentUser.userName,
                    rec.productName,
                    rec.Count
            );
            insertIntoEnterStoreTable(newProduct, "storeenter");
            fillEnterStoreTable("storeenter");
            newProTextF.setText("");
            newnum.setText("");
        } catch (org.sqlite.SQLiteException ex) {
            DialogHelper.getInstance().showOKAlert("- المنتج الذي ادخلته موجود بالفعل");
        } catch (Exception e) {
            Logger.writeLog("Exception In " + getClass().getName() + " -> insertIntoCurrentStoreTable:-" + e);
        }
    }

    public ObservableList<StoreRecord> selectCurrentStore(String tab) {
        ObservableList<StoreRecord> list = FXCollections.observableArrayList();
        try {
            list.clear();
            String sqlState = "SELECT * FROM store " + tab + " ORDER BY id";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();
            String PName;
            float Count;
            StoreRecord rec;
            while (DatabaseHandler.result.next()) {
                PName = DatabaseHandler.result.getString(1);
                Count = DatabaseHandler.result.getFloat(2);
                rec = new StoreRecord(PName, Count);
                list.add(rec);
            }
            DatabaseHandler.result.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Exception In " + getClass().getName() + " -> selectCurrentStore:-" + e);
            return list;
        }
    }

    public void fillCurrentStoreTable(String adStr) {
        ObservableList<StoreRecord> sers = selectCurrentStore(adStr);
        TreeItem<StoreRecord> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        CurrentTable.setRoot(root);
        CurrentTable.setShowRoot(false);
    }

    public static void insertIntoEnterStoreTable(storemoves rec, String tableName) {
        try {
            String sqlString = "INSERT INTO " + tableName + " VALUES (?,?,?,?,?)";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlString);
            DatabaseHandler.stat.setString(1, rec.Day);
            DatabaseHandler.stat.setString(2, rec.Time);
            DatabaseHandler.stat.setString(3, rec.User);
            DatabaseHandler.stat.setString(4, rec.ProName);
            DatabaseHandler.stat.setFloat(5, rec.count);
            DatabaseHandler.stat.execute();
        } catch (Exception e) {
            Logger.writeLog("Exception In " + StorageController.class.getName() + " -> insertIntoEnterStoreTable :-" + e);
        }
    }

    public ObservableList<storemoves> selectEnterStoreTable(String tableName) {
        try {
            ObservableList<storemoves> list = FXCollections.observableArrayList();
            list.clear();
            String sqlState = "SELECT * FROM " + tableName;
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();
            String Day, Time, USer, Pro;
            float Count;
            storemoves rec;
            while (DatabaseHandler.result.next()) {
                Day = DatabaseHandler.result.getString(1);
                Time = DatabaseHandler.result.getString(2);
                USer = DatabaseHandler.result.getString(3);
                Pro = DatabaseHandler.result.getString(4);
                Count = DatabaseHandler.result.getFloat(5);
                rec = new storemoves(Day, Time, USer, Pro, Count);
                list.add(rec);
            }
            DatabaseHandler.result.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Exception In " + getClass().getName() + " -> selectEnterStoreTable:-" + e);
            return null;
        }
    }

    public void fillEnterStoreTable(String adStr) {
        ObservableList<storemoves> sers = selectEnterStoreTable(adStr);
        TreeItem<storemoves> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        enterrable.setRoot(root);
        enterrable.setShowRoot(false);
    }

    public void fillExitStoreTable(String adStr) {
        ObservableList<storemoves> sers = selectEnterStoreTable(adStr);
        TreeItem<storemoves> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        exitTable.setRoot(root);
        exitTable.setShowRoot(false);
    }

    @FXML
    private void pushEvent() {
        try {
            if (myProductComBox.getValue() == null || addingNum.getText().trim().equals("")) {
                DialogHelper.getInstance().showOKAlert("-يجب ادخال جميع البيانات ");
                return;
            }
            float num = -1;
            try {
                num = Float.parseFloat(addingNum.getText().trim());
                if (num <= 0) {
                    DialogHelper.getInstance().showOKAlert("- البيانات التي ادخلتها غير صحيحة ");
                    return;
                }
            } catch (NumberFormatException e) {
                DialogHelper.getInstance().showOKAlert("- البيانات التي ادخلتها غير صحيحة ");
                return;
            }
            String product = myProductComBox.getValue();
            StoreRecord cuRec = new StoreRecord(product, num);
            updateCurrentStoreTable(cuRec);
            fillCurrentStoreTable("");
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> pushEvent:-" + e);
        }
    }

    @FXML
    private void AddEvent() {
        try {
            if (DatabaseHandler.canAddMoreRowsInTable("store")) {
                if (newProTextF.getText().trim().equals("") || newnum.getText().trim().equals("")) {
                    DialogHelper.getInstance().showOKAlert("- يجب ادخال جميع البيانات ");
                    return;
                }
                if (newProTextF.getText().trim().contains("@") || newProTextF.getText().trim().contains("#")) {
                    DialogHelper.getInstance().showOKAlert("لا يمكن ان يحتوي المكون علي @او #");
                    return;
                }
                float num = -1;
                try {
                    num = Float.parseFloat(newnum.getText().trim());
                    if (num < 0) {
                        DialogHelper.getInstance().showOKAlert("- البيانات التي ادخلتها غير صحيحة ");
                        return;
                    }
                } catch (Exception e) {
                    DialogHelper.getInstance().showOKAlert("- البيانات التي ادخلتها غير صحيحة ");
                    return;
                }
                String product = newProTextF.getText().trim();
                StoreRecord cuRec = new StoreRecord(product, num);
                insertIntoCurrentStoreTable(cuRec);
                fillCurrentStoreTable("");
            } else {
                DialogHelper.getInstance().showOKAlert("لا يمكن اضافة المزيد في النسخة التجريبية");
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> pushEvent: " + e);
        }
    }

    public static ObservableList<String> getMy5amsNames(String addingString) {
        ObservableList<String> list = FXCollections.observableArrayList();
        try {
            String sqlState = "SELECT Product FROM store " + addingString;
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();
            String PName;
            while (DatabaseHandler.result.next()) {
                PName = DatabaseHandler.result.getString(1);
                list.add(PName);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in StorageController -> getMy5amsNames:-" + e);
        }
        return list;
    }

    public void updateCurrentStoreTable(StoreRecord rec) {
        try {
            String sql = "SELECT * FROM store where product=?";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setString(1, rec.productName);
            DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
            float prev = 0.0f;
            while (DatabaseHandler.result.next()) {
                prev = DatabaseHandler.result.getFloat(2);
            }
            prev += rec.Count;
            sql = "update store set count=? where product=?";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setFloat(1, prev);
            DatabaseHandler.stat.setString(2, rec.productName);
            DatabaseHandler.stat.execute();
            storemoves newProduct = new storemoves(
                    LocalDate.now().toString(),
                    LocalTime.now().format(dateTimeFormatter),
                    User.CurrentUser.userName,
                    rec.productName,
                    rec.Count
            );
            insertIntoEnterStoreTable(newProduct, "storeenter");
            fillEnterStoreTable("storeenter");
            myProductComBox.setValue(null);
            addingNum.setText("");
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " updateCurrentStoreTable:" + e);
        }
    }

    @FXML
    private void updateData() {
        myProductComBox.setItems(getMy5amsNames(" Order By Product ASC"));
        customeProductComBox.setItems(myProductComBox.getItems());
    }

    @FXML
    private void updateData2() {
        myProductComBox2.setItems(getMy5amsNames(" where count!=0 Order By Product ASC"));
        customeProductComBox2.setItems(myProductComBox2.getItems());
    }

    @FXML
    private void pull() {
        if (User.CurrentUser.getAceessConstrains().equalsIgnoreCase("موظف")) {
            DialogHelper.getInstance().showOKAlert("غير مسموح للموظف بالسحب المباشر من المخزن");
            return;
        }
        try {
            if (myProductComBox2.getValue() == null || numOfPull1.getText().trim().equals("")) {
                DialogHelper.getInstance().showOKAlert("-يجب ادخال جميع البيانات ");
                return;
            }
            float pn = 0.0f;
            try {
                pn += Float.parseFloat(numOfPull1.getText().trim());
                if (pn <= 0) {
                    DialogHelper.getInstance().showOKAlert("- البيانات التي ادخلتها غير صحيحة ");
                    return;
                }
            } catch (Exception e) {
                DialogHelper.getInstance().showOKAlert("- البيانات التي ادخلتها غير صحيحة ");
                return;
            }
            String sql = "SELECT * FROM store where product=?";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setString(1, myProductComBox2.getValue());
            DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
            float prev = 0.0f;
            while (DatabaseHandler.result.next()) {
                prev = DatabaseHandler.result.getFloat(2);
            }
            if (prev < pn) {
                DialogHelper.getInstance().showOKAlert("- هذا العدد اكبر من الرصيد الحالي للمنتج");
                return;
            }
            prev -= pn;
            sql = "update store set count=? where product=?";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setFloat(1, prev);
            DatabaseHandler.stat.setString(2, myProductComBox2.getValue());
            DatabaseHandler.stat.execute();
            storemoves newProduct = new storemoves(
                    LocalDate.now().toString(),
                    LocalTime.now().format(dateTimeFormatter),
                    User.CurrentUser.userName,
                    myProductComBox2.getValue(),
                    pn
            );
            insertIntoEnterStoreTable(newProduct, "storeexit");
            fillExitStoreTable("storeexit");
            fillCurrentStoreTable("");
            myProductComBox2.setValue(null);
            numOfPull1.setText("");
            myProductComBox.setValue(null);
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> pull:-" + e);
        }
    }

    @FXML
    private void scrollHandler(ScrollEvent event) {
        double deltaY = event.getDeltaY() * 4; // *6 to make the scrolling a bit faster
        double width = sp.getContent().getBoundsInLocal().getWidth();
        double vvalue = sp.getVvalue();
        sp.setVvalue(vvalue + -deltaY / width); // deltaY/width to make the scrolling equally fast regardless of the actual width of the component
    }

    @FXML
    private void pullKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            pull();
        }
    }

    @FXML
    private void addOldKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            pushEvent();
        }
    }

    @FXML
    private void addnewKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            AddEvent();
        }
    }

    @FXML
    private void goToCashierSellsProducts() {
        focusButton(cashierSellsProducts_BTN, cashierExitBox);
    }

    @FXML
    private void goToDirectSellProducts() {
        focusButton(directSellProducts_BTN, directExitBox);
    }

    @FXML
    private void goToIncomeProducts() {
        focusButton(incomeProducts_BTN, incomeingBox);
    }

    @FXML
    private void goToCurrentProducts() {
        focusButton(currentProducts_BTN, currentBox);
    }

    public static void pullFromORPushTOStore(String prodcutName, ObservableList<ArgumentsClass> argsList, int amount, String operation) {
        try {
            String sql;
            double prev = 0;
            double newVal = 0;
            if (argsList.size() > 0) {
                for (int i = 0; i < argsList.size(); i++) {
                    sql = "SELECT count FROM store WHERE Product=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                    DatabaseHandler.stat.setString(1, argsList.get(i).argName);
                    DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
                    while (DatabaseHandler.result.next()) {
                        prev = DatabaseHandler.result.getDouble(1);
                        if (operation.equals("push")) {
                            newVal = prev + (amount * argsList.get(i).numOfArg);
                        } else if (operation.equals("pull")) {
                            newVal = prev - (amount * argsList.get(i).numOfArg);
                        }
                        sql = "UPDATE store SET count=? WHERE Product=?";
                        DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                        DatabaseHandler.stat.setDouble(1, newVal);
                        DatabaseHandler.stat.setString(2, argsList.get(i).argName);
                        DatabaseHandler.stat.execute();
                        break;
                    }
                }
            } else {
                sql = "SELECT count FROM store WHERE Product=?";
                DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                DatabaseHandler.stat.setString(1, prodcutName);
                DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
                while (DatabaseHandler.result.next()) {
                    prev = DatabaseHandler.result.getDouble(1);
                    if (operation.equals("push")) {
                        newVal = prev + amount;
                    } else if (operation.equals("pull")) {
                        newVal = prev - amount;
                    }
                    sql = "UPDATE store SET count=? WHERE Product=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                    DatabaseHandler.stat.setDouble(1, newVal);
                    DatabaseHandler.stat.setString(2, prodcutName);
                    DatabaseHandler.stat.execute();
                    break;
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in CafeController -> pullFromORPushTOStore :- " + e);
        }
        RestaurantHomeController.anyAct = 1;
    }

    public static int ableToBuy(String pName, ObservableList<ArgumentsClass> argsList, int num) {
        try {
            String s;
            double ExistiungNum;
            if (argsList.size() > 0) {
                for (int i = 0; i < argsList.size(); i++) {
                    ExistiungNum = -10;
                    s = "SELECT count FROM store WHERE Product=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(s);
                    DatabaseHandler.stat.setString(1, argsList.get(i).argName);
                    DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
                    while (DatabaseHandler.result.next()) {
                        ExistiungNum = DatabaseHandler.result.getDouble(1);
                    }
                    if (ExistiungNum < 0 || (ExistiungNum < argsList.get(i).numOfArg * num)) {
                        return -5;
                    }
                }
                return 1;
            } else {
                ExistiungNum = -10;
                s = "SELECT count FROM store WHERE Product=?";
                DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(s);
                DatabaseHandler.stat.setString(1, pName);
                DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
                while (DatabaseHandler.result.next()) {
                    ExistiungNum = DatabaseHandler.result.getDouble(1);
                }
                if (ExistiungNum < 0 || (ExistiungNum < num)) {
                    return -5;
                }
                return 1;
            }
        } catch (Exception e) {
            Logger.writeLog("EXception in CafeController -> ableToBuy :- " + e);
        }
        return -5;
    }

    public static int ableToBuy(String pName, ObservableList<ArgumentsClass> argsList, int num, Hashtable<String, Double> prevPulls) {
        try {
            String s;
            double ExistiungNum;
            double ee;
            if (argsList.size() > 0) {
                for (int i = 0; i < argsList.size(); i++) {
                    ee = 0;
                    ExistiungNum = -10;
                    s = "SELECT count FROM store WHERE Product=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(s);
                    DatabaseHandler.stat.setString(1, argsList.get(i).argName);
                    DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
                    while (DatabaseHandler.result.next()) {
                        ExistiungNum = DatabaseHandler.result.getDouble(1);
                    }
                    ee = prevPulls.get(argsList.get(i).argName) == null ? 0 : prevPulls.get(argsList.get(i).argName);
                    if (ExistiungNum < 0 || (ExistiungNum < ((argsList.get(i).numOfArg * num) + ee))) {
                        return -5;
                    }
                }
                return 1;
            } else {
                ee = 0;
                ExistiungNum = -10;
                s = "SELECT count FROM store WHERE Product=?";
                DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(s);
                DatabaseHandler.stat.setString(1, pName);
                DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
                while (DatabaseHandler.result.next()) {
                    ExistiungNum = DatabaseHandler.result.getDouble(1);
                }
                ee = prevPulls.get(pName) == null ? 0 : prevPulls.get(pName);
                if (ExistiungNum < 0 || (ExistiungNum < (num + ee))) {
                    return -5;
                }
                return 1;
            }
        } catch (Exception e) {
            Logger.writeLog("EXception in CafeController -> ableToBuy :- " + e);
        }
        return -5;
    }

    public static void insertStatementForDailyPullTable(int receiptID, CafeRecord rec, int itterator, int flag) {
        System.out.println("receiptID: " + receiptID);
        System.out.println("CafeRecord: " + rec);
        String sql;
        try {
            sql = "INSERT INTO dailypullstore VALUES (?,?,?,?,?,?,?)";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setInt(1, (int) Methods.GetMaximum("id", "dailypullstore"));
            DatabaseHandler.stat.setString(2, Methods.getMyDay());
            if (flag == 0) {
                DatabaseHandler.stat.setString(3, rec.ListOFArgs.get(itterator).argName);
                DatabaseHandler.stat.setDouble(4, rec.numOfDrink * rec.ListOFArgs.get(itterator).numOfArg);
            } else {
                DatabaseHandler.stat.setString(3, rec.DrinkName);
                DatabaseHandler.stat.setDouble(4, rec.numOfDrink);
            }
            DatabaseHandler.stat.setString(5, rec.DrinkName);
            DatabaseHandler.stat.setInt(6, rec.numOfDrink);
            DatabaseHandler.stat.setInt(7, receiptID);
            DatabaseHandler.stat.execute();
        } catch (Exception e) {
            Logger.writeLog("Exception in CafeController -> insertStatementForDailyPullTable :-" + e);
        }
    }

    public static void insertDataIntoDailyStorePullData(int receiptID, ObservableList<CafeRecord> data) {
        try {
            CafeRecord rec;
            for (int i = 0; i < data.size(); i++) {
                rec = data.get(i);
                if (rec.ListOFArgs.size() > 0) {
                    for (int j = 0; j < rec.ListOFArgs.size(); j++) {
                        StorageController.insertStatementForDailyPullTable(receiptID, rec, j, 0);
                    }
                } else {
                    StorageController.insertStatementForDailyPullTable(receiptID, rec, 0, 1);
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in CafeController -> insertDataIntoDailyStorePullData :- " + e);
        }
    }

    public static String getArgsOfProduct(String categ, String ProName) {
        try {
            String sql = "SELECT args FROM drinkprice WHERE category=? AND drinkName=?";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setString(1, categ);
            DatabaseHandler.stat.setString(2, ProName);
            DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
            while (DatabaseHandler.result.next()) {
                String s = DatabaseHandler.result.getString(1);
                return s;
            }
        } catch (Exception e) {
            Logger.writeLog("EXception in CafeController -> getArgsOfProduct:-" + e);
        }
        return "Ehab";
    }

    public static void putFocusHere(JFXButton get) {
        get.setStyle("-fx-background-color: #1e2b3c;" + "-fx-text-fill: white;" + "-fx-background-radius: 15;" + "-fx-padding:10 25;" + "-fx-font-size:21;" + "-fx-font-weight: BOLD;");
    }

    public static void delFocus(JFXButton btn) {
        btn.setStyle("-fx-background-color: #B2E0FF;" + "-fx-text-fill: black;" + "-fx-background-radius: 15;" + "-fx-padding:10 25;" + "-fx-font-size:21;" + "-fx-font-weight: BOLD;");
    }

    @FXML
    private void exportData() {
        File outputFile = getFileDestenation(searchComBox.getScene().getWindow(), "Export  Storage Data", "storage_data");

        if (outputFile != null) {

            new Thread(() -> {
                try {
                    /**
                     * Create Workbook & sheets
                     */
                    Workbook wb = new HSSFWorkbook();

                    Sheet cuurentDataSheet = wb.createSheet("الرصيد الحالي");

                    Sheet pushDataSheet = wb.createSheet("الواردات");
                    Sheet pullDataSheet = wb.createSheet("السحب المباشر");
                    Sheet cafePullSheet = wb.createSheet("مبيعات الكاشير");

                    /**
                     * Create Style to align Data to center of cell
                     */
                    HSSFCellStyle centerAlignData = (HSSFCellStyle) wb.createCellStyle();
                    centerAlignData.setAlignment(HorizontalAlignment.CENTER);

                    setSheetColumnWidth(cuurentDataSheet, 8000, 0, 1);
                    setSheetColumnWidth(pushDataSheet, 8000, 0, 1, 2, 3, 4, 5);
                    setSheetColumnWidth(pullDataSheet, 8000, 0, 1, 2, 3, 4, 5);
                    setSheetColumnWidth(cafePullSheet, 8000, 0, 1, 2, 3, 4, 5);

                    fillCurrentSheetData(cuurentDataSheet, centerAlignData);
                    fillStoreMovesSheets(pushDataSheet, centerAlignData, enterrable, "push");
                    fillStoreMovesSheets(pullDataSheet, centerAlignData, exitTable, "pull");
                    fillCafePullSheet(cafePullSheet, centerAlignData);
                    OutputStream fos = new FileOutputStream(outputFile);
                    wb.write(fos);
                    fos.close();
                    Platform.runLater(() -> {
                        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE_ALT);
                        icon.setStyle("-fx-fill:green;");
                        util.gui.BuilderUI.showNotification(icon, "تم تصدير الملف بنجاح");
                    });
                } catch (Exception e) {
                    Logger.writeLog("Exception in " + StorageController.this.getClass().getName() + " -> extractCurrentData() " + e);
                }
            }).start();
        }

    }

    public void fillCurrentSheetData(Sheet sheet, HSSFCellStyle centerAlignData) {
        /* ==== current sheet data ==== */
        int writeIndex = 0;
        /* Titles */
        Row row = sheet.createRow(writeIndex);
        typeInCell(row.createCell(0), "المكون", centerAlignData);
        typeInCell(row.createCell(1), "العدد", centerAlignData);

        /**
         * Add current Table Data
         */
        ObservableList<TreeItem<StoreRecord>> currentData = CurrentTable.getRoot().getChildren();
        for (TreeItem<StoreRecord> item : currentData) {
            row = sheet.createRow(++writeIndex);
            typeInCell(row.createCell(0), item.getValue().getProductName(), centerAlignData);
            typeInCell(row.createCell(1), String.valueOf(item.getValue().getCount()), centerAlignData);
        }
    }

    private void fillStoreMovesSheets(Sheet sheet, HSSFCellStyle centerAlignData, JFXTreeTableView<storemoves> table, String action) {
        /* ==== current sheet data ==== */
        int writeIndex = 0;
        /* Titles */
        Row row = sheet.createRow(writeIndex);
        typeInCell(row.createCell(0), "اليوم", centerAlignData);
        typeInCell(row.createCell(1), "الوقت", centerAlignData);
        typeInCell(row.createCell(2), "المستخدم", centerAlignData);
        typeInCell(row.createCell(3), "المكون", centerAlignData);
        typeInCell(row.createCell(4), action.equalsIgnoreCase("push") ? "العدد المضاف" : "العدد المسحوب", centerAlignData);

        /**
         * Add current Table Data
         */
        ObservableList<TreeItem<storemoves>> currentData = table.getRoot().getChildren();
        for (TreeItem<storemoves> item : currentData) {
            row = sheet.createRow(++writeIndex);
            typeInCell(row.createCell(0), item.getValue().getDay(), centerAlignData);
            typeInCell(row.createCell(1), item.getValue().getTime(), centerAlignData);
            typeInCell(row.createCell(2), item.getValue().getUser(), centerAlignData);
            typeInCell(row.createCell(3), item.getValue().getProName(), centerAlignData);
            typeInCell(row.createCell(4), String.valueOf(item.getValue().getCount()), centerAlignData);
        }
    }

    public void fillCafePullSheet(Sheet sheet, HSSFCellStyle centerAlignData) {
        /* ==== current sheet data ==== */
        int writeIndex = 0;
        /* Titles */
        Row row = sheet.createRow(writeIndex);
        typeInCell(row.createCell(0), "اليوم", centerAlignData);
        typeInCell(row.createCell(1), "المكون", centerAlignData);
        typeInCell(row.createCell(2), "العدد", centerAlignData);
        typeInCell(row.createCell(3), "المنتج", centerAlignData);
        typeInCell(row.createCell(4), "العدد", centerAlignData);

        /**
         * Add current Table Data
         */
        ObservableList<TreeItem<CafePullREcord>> currentData = t4.getRoot().getChildren();
        for (TreeItem<CafePullREcord> item : currentData) {
            row = sheet.createRow(++writeIndex);
            typeInCell(row.createCell(0), item.getValue().getDay(), centerAlignData);
            typeInCell(row.createCell(1), item.getValue().getArgName(), centerAlignData);
            typeInCell(row.createCell(2), String.valueOf(item.getValue().getNumOfArg()), centerAlignData);
            typeInCell(row.createCell(3), item.getValue().getProdName(), centerAlignData);
            typeInCell(row.createCell(4), String.valueOf(item.getValue().getNumOfProd()), centerAlignData);
        }
    }

    @FXML
    private void expandOptions() {
        if (isExpanded) {
            // remove options
            addAndPullContainerParent.getChildren().remove(addAndPullContainer);
            arrowIcon.setIcon(FontAwesomeIcon.ARROW_DOWN);
            isExpanded = false;
        } else {
            // add options
            addAndPullContainerParent.getChildren().add(1, addAndPullContainer);
            arrowIcon.setIcon(FontAwesomeIcon.ARROW_UP);
            isExpanded = true;
        }
    }

    @FXML
    private void showAll() {
        searchComBox.setText("");
        fillCurrentStoreTable("");
        fillEnterStoreTable("storeenter");
        fillExitStoreTable("storeexit");
        fillt4("");
        fromDate.setValue(null);
        toDate.setValue(LocalDate.now());
    }

    @FXML
    private void searchSpecificPeriod() {
        if (toDate == null || toDate.getValue() == null || fromDate == null || fromDate.getValue() == null) {
            DialogHelper.getInstance().showOKAlert("يجب إدخال تاريخ بداية ونهاية الفترة");
            return;
        }
        if (toDate.getValue().isBefore(fromDate.getValue())) {
            DialogHelper.getInstance().showOKAlert("الفترة غير صحيحة" + "\n" + "تاريخ البداية يجب أن يكون قبل تاريخ النهاية");
            return;
        }
        fillCurrentStoreTable("where Product like '%" + searchComBox.getText().trim() + "%'");
        fillEnterStoreTable(" storeenter WHERE (DATE(storeenter.day) BETWEEN "
                + "'" + fromDate.getValue().toString() + "' AND '" + toDate.getValue().toString() + "') "
                + "AND product like '%" + searchComBox.getText().trim() + "%'");

        fillExitStoreTable(" storeexit WHERE (DATE(storeexit.day) BETWEEN "
                + "'" + fromDate.getValue().toString() + "' AND '" + toDate.getValue().toString() + "') "
                + "AND product like '%" + searchComBox.getText().trim() + "%' ");

        fillt4(" WHERE (DATE(archpullstore.Day) BETWEEN '" + fromDate.getValue().toString() + "' AND '" + toDate.getValue().toString() + "') "
                + "AND archpullstore.argName like '%" + searchComBox.getText().trim() + "%'");
    }

    @FXML
    private void searchEvent() {
        if (toDate == null || toDate.getValue() == null || fromDate == null || fromDate.getValue() == null) {
            // normal search
            if (searchComBox.getText().trim().equals("")) {
                fillCurrentStoreTable("");
                fillEnterStoreTable("storeenter");
                fillExitStoreTable("storeexit");
                fillt4("");
            } else {
                fillCurrentStoreTable("where Product like '%" + searchComBox.getText().trim() + "%'");
                fillEnterStoreTable("storeenter where Product like '%" + searchComBox.getText().trim() + "%'");
                fillExitStoreTable("storeexit where Product like '%" + searchComBox.getText().trim() + "%'");
                fillt4(" where argName like '%" + searchComBox.getText().trim() + "%'");
            }
        } else {
            // search with date perids
            if (toDate.getValue().isBefore(fromDate.getValue())) {
                DialogHelper.getInstance().showOKAlert("الفترة غير صحيحة" + "\n" + "تاريخ البداية يجب أن يكون قبل تاريخ النهاية");
                return;
            }
            fillCurrentStoreTable("where Product like '%" + searchComBox.getText().trim() + "%'");
            fillEnterStoreTable(" storeenter WHERE (DATE(storeenter.day) BETWEEN "
                    + "'" + fromDate.getValue().toString() + "' AND '" + toDate.getValue().toString() + "') "
                    + "AND product like '%" + searchComBox.getText().trim() + "%'");

            fillExitStoreTable(" storeexit WHERE (DATE(storeexit.day) BETWEEN "
                    + "'" + fromDate.getValue().toString() + "' AND '" + toDate.getValue().toString() + "') "
                    + "AND product like '%" + searchComBox.getText().trim() + "%' ");

            fillt4(" WHERE (DATE(archpullstore.Day) BETWEEN '" + fromDate.getValue().toString() + "' AND '" + toDate.getValue().toString() + "') "
                    + "AND archpullstore.argName like '%" + searchComBox.getText().trim() + "%'");
        }

    }

}
