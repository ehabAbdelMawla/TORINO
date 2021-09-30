package dailysheet;

import controlpanel.ControlPanel;
import datamodel.ArchOfDays;
import datamodel.ArchieveReport;
import datamodel.CafePullREcord;
import datamodel.DailySheetRecord;
import datamodel.SheftClassForPrint;
import static datamodel.TempMethods.ClearData;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import datamodel.Order;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import static restaurant.dayorders.DayOrdersController.totalPointer;
import settings.financial.custody.FinancialCustodyController;
import util.Logger;
import util.gui.HelperMethods;
import util.gui.load.LoadHelper;
import util.mail.Mail;
import com.jfoenix.controls.JFXButton;
import datamodel.User;
import datamodel.settings.Preferences;
import datamodel.settings.PreferencesType;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import dialog.admin_validation.AdminValidationController;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import static toolbar.ToolbarController.Exprenses_BTN_Static;
import util.db.DatabaseHandler;
import util.db.Methods;
import util.gui.BuilderUI;
import util.gui.load.DialogHelper;
import util.printing.PrintingData;
import util.services.BackupDB;

public class DailySheetController implements Initializable {

    @FXML
    private Label SumLabel;
    @FXML
    private Label OutLabel;
    @FXML
    private Label cafeLabel;
    @FXML
    private Label INLabel;
    @FXML
    private JFXTreeTableView<DailySheetRecord> myTable;
    @FXML
    private TreeTableColumn<DailySheetRecord, Integer> RecieptIdCol;
    @FXML
    private TreeTableColumn<DailySheetRecord, String> DateCol;
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
    private TreeTableColumn<DailySheetRecord, String> customerCol;
    public static int endDayNot = 0;
    @FXML
    private Label deptLabel;
    @FXML
    private VBox tableContainer;
    @FXML
    private HBox buttonsContainer;
    @FXML
    private VBox playingIncome;
    @FXML
    private VBox deptIncomeLabel;
    @FXML
    private Label netIncome;
    @FXML
    private VBox netIncomeHBox;
    @FXML
    private AnchorPane ap;
    @FXML
    public JFXButton restaurantBTN;
    @FXML
    public JFXButton playingBTN;
    private static JFXButton SelectedButton;
    @FXML
    private HBox toggleContainer;
    @FXML
    private TextField filterField;
    @FXML
    private ScrollPane SP;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        RecieptIdCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("idButton"));
        DateCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("actDate"));
        UserCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("userName"));
        RoomNumCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("roomNum"));
        fromCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("from"));
        toCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("to"));
        preCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("preDis"));
        disCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("Dis"));
        INCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("inComeVal"));
        customerCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("customerName"));
        util.gui.HelperMethods.TableColumnAlignment(DateCol, UserCol, RoomNumCol, fromCol, toCol, preCol, disCol, INCol, customerCol);

        updateAllData();

        SelectedButton = playingBTN;
        SelectedButton.getStyleClass().add("cashier-buttons-focused");
        focusButton(SelectedButton);

        if (!ControlPanel.getInstance().HAS_PLAYSTATION) {
            tableContainer.getChildren().remove(myTable);
            buttonsContainer.getChildren().remove(playingIncome);
            restaurantBTN.fire();
            tableContainer.getChildren().remove(toggleContainer);
            tableContainer.setPadding(new Insets(30, 0, 10, 0));
        }
        if (!ControlPanel.getInstance().CUSTOMER_DATA) {
            buttonsContainer.getChildren().remove(deptIncomeLabel);
            myTable.getColumns().remove(customerCol);
        }
        if (!ControlPanel.getInstance().EXPENSESS_FROM_DAILY_INCOME) {
            buttonsContainer.getChildren().remove(netIncomeHBox);
        }

        filterField.textProperty().addListener((observable, oldValue, newValue)
                -> myTable.setPredicate(orderTreeItem
                        -> orderTreeItem.getValue().getIdButton().getText().contains(filterField.getText().trim())));
    }

    private void focusButton(JFXButton b) {
        SelectedButton.getStyleClass().remove("cashier-buttons-focused");
        b.getStyleClass().add("cashier-buttons-focused");
        SelectedButton = b;
    }

    @FXML
    public void EndDayAvtion(ActionEvent event) {
        try {
            int lengthOfCurrency = controlpanel.ControlPanel.getInstance().getCurrency().trim().length();
            double psIn = Double.parseDouble(INLabel.getText().trim().substring(0, INLabel.getText().length() - lengthOfCurrency));
            double CaIn = Double.parseDouble(cafeLabel.getText().trim().substring(0, cafeLabel.getText().length() - lengthOfCurrency));
            double total = Double.parseDouble(SumLabel.getText().trim().substring(0, SumLabel.getText().length() - lengthOfCurrency));
            double EX = Double.parseDouble(OutLabel.getText().trim().substring(0, OutLabel.getText().length() - lengthOfCurrency));
            double dept = Double.parseDouble(deptLabel.getText().trim().substring(0, deptLabel.getText().length() - lengthOfCurrency));
            double el3ohda = FinancialCustodyController.updateCurrentMoneyLabel();
            if (total == 0 && EX == 0) {
                return;
            }
            if (DialogHelper.getInstance().showConfirmAlert("هل انت متأكد من انهاء اليوم؟") == 1) {
                insertIntoarchOfDays(new ArchOfDays(Methods.getMyDay(),
                        psIn,
                        CaIn,
                        0,
                        EX,
                        el3ohda));
                new Thread(() -> {
                    showEndSheftPDF(new SheftClassForPrint(
                            User.CurrentUser.userName,
                            psIn,
                            CaIn,
                            total,
                            dept, EX
                    ));
                }).start();

//                ==== SEND MAIL ====
                if (ControlPanel.getInstance().SEND_DAILYSHEET_MAIL) {
                    Mail.data = new ArchieveReport(Methods.getMyDay(), false);
                    new Thread(Mail::sendMail).start();
                }

                DatabaseHandler.con.prepareStatement("UPDATE dailySheet SET daily=0").execute();
                DatabaseHandler.con.prepareStatement("UPDATE dailyexpense SET daily=0").execute();
                DatabaseHandler.con.prepareStatement("UPDATE resturantReceets SET daily=0 where daily=1").execute();
                DatabaseHandler.con.prepareStatement("UPDATE deptData SET daily=0 where daily=1").execute();
//               ==== ==== ==== ==== 
                mizRecords();
                ClearData("dailypullstore");
                ClearData("myDay");

                Platform.runLater(() -> {
                    Order.fillDailyCafeTable();
                    updateAllData();
                    endDayNot = 1;
//                    Customer.fillData();
                });
                if (Preferences.getInstance().getBoolean(PreferencesType.BackupOnEndShift, "true")) {
                    try {
                        BackupDB.start();
                        BuilderUI.showNotification(new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE), "تم حفظ نسخة إحتياطية من قاعدة البيانات بنجاح" + "\n" + BackupDB.getLocation().getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                        BuilderUI.showNotification(new MaterialDesignIconView(MaterialDesignIcon.CLOUD_OUTLINE_OFF), "فشل حفظ نسخة إحتياطية من قاعدة البيانات");
                        Logger.writeLog("Exception in " + getClass().getName() + ".EndDayAvtion() => BackupDB.start() => " + e);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + " -> EndDayAvtion:- " + e);
        }
    }

    synchronized public void showEndSheftPDF(SheftClassForPrint obj) {
        try {
            String folderName = "اليوميات/" + Methods.getMyDay();
            new File(folderName).mkdirs();
            String outputFile = folderName + "/" + User.CurrentUser.getUserName() + " " + Methods.getMyDay() + " Serial " + LocalTime.now().toString().replaceAll(":", "").replaceAll("[.]", "") + ".pdf";

            Map<String, Object> parameters = new HashMap<>();

            parameters.put("userName", obj.getUserName());
            parameters.put("psIncome", obj.getPsIncome());
            parameters.put("cafeIncome", obj.getCafeIncome());
            parameters.put("deptIncome", obj.getDeptIncome());
            parameters.put("totalIncome", obj.getTotalIncome());
            parameters.put("expensess", obj.getExpensess());
            parameters.put("netIncome", HelperMethods.formatNum(obj.getTotalIncome() - obj.getExpensess()));
            parameters.put("netIncomeVisibilty", ControlPanel.getInstance().EXPENSESS_FROM_DAILY_INCOME);
            parameters.put("playstation", ControlPanel.getInstance().HAS_PLAYSTATION);
            parameters.put("AccessCustomerDataPage", ControlPanel.getInstance().CUSTOMER_DATA);
            PrintingData.getInstance().setShopParameters(parameters);
            JasperReport jr = JasperCompileManager.compileReport("reports/EndSheftReport80.jrxml");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jr, parameters, new JREmptyDataSource());
            OutputStream outputStream = new FileOutputStream(new File(outputFile));
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            outputStream.close();

            boolean printValidation = Preferences.getInstance().getBoolean(PreferencesType.Print, "true")
                    && Preferences.getInstance().getBoolean(PreferencesType.Print_EndDay, "true");
            boolean isAdmin = !User.CurrentUser.aceessConstrains.equalsIgnoreCase("موظف");
            boolean isEmp = User.CurrentUser.aceessConstrains.equalsIgnoreCase("موظف");
            boolean empValidation = (isEmp && ControlPanel.getInstance().ALLOW_CLIENT_ACCESS_END_DAY_PAGE)
                    || (isEmp && !ControlPanel.getInstance().ALLOW_CLIENT_ACCESS_END_DAY_PAGE && AdminValidationController.flag);

            if (printValidation && (isAdmin || empValidation)) {
                System.out.println("PRINTING => " + outputFile);
                Runtime.getRuntime().exec("reports/PDFtoPrinter \"" + "../" + outputFile + "\"");
            }
        } catch (Exception e) {
            Logger.writeLog("EXception in " + getClass().getName() + ".showEndSheftPDF() => " + e);
        }
    }

    public ObservableList<DailySheetRecord> selectDailySheetData() {
        ObservableList<DailySheetRecord> list = FXCollections.observableArrayList();
        try {
            list.clear();
            String sqlState = "SELECT * FROM dailySheet WHERE daily=1";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();

            while (DatabaseHandler.result.next()) {
                DailySheetRecord rec = new DailySheetRecord(
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
            return list;
        } catch (Exception e) {
            Logger.writeLog("Error In " + getClass().getName() + " selectDailySheetData()" + e);
            return list;
        }
    }

    public void fillDailySheetTable() {
        ObservableList<DailySheetRecord> sers = selectDailySheetData();
        TreeItem<DailySheetRecord> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        myTable.setRoot(root);
        myTable.setShowRoot(false);
    }

    public void updateAllData() {

        fillDailySheetTable();
        double In = Methods.getTotalMoney("dailySheet WHERE daily=1");
        double out = Methods.getTotalMoney("dailyExpense WHERE daily=1");
        double cafeIn = Double.parseDouble(totalPointer.getText().replaceAll(ControlPanel.getInstance().getCurrency(), ""));
        double dept = Methods.getTotalMoney("deptData WHERE daily=1");
        if (ControlPanel.getInstance().SEPARATE_ROOMS_CAFE) {
            double CafeInRooms = getIncomeOfDrinksFromRooms("dailySheet WHERE daily=1");
            cafeIn += CafeInRooms;
            In -= CafeInRooms;
        }
        double sumition = (In + cafeIn + dept);

        INLabel.setText(HelperMethods.formatNum(In) + ControlPanel.getInstance().getCurrency());
        cafeLabel.setText(HelperMethods.formatNum(cafeIn) + ControlPanel.getInstance().getCurrency());
        OutLabel.setText(HelperMethods.formatNum(out) + ControlPanel.getInstance().getCurrency());
        SumLabel.setText(HelperMethods.formatNum(sumition) + ControlPanel.getInstance().getCurrency());
        deptLabel.setText(HelperMethods.formatNum(dept) + ControlPanel.getInstance().getCurrency());
        netIncome.setText(HelperMethods.formatNum(ControlPanel.getInstance().EXPENSESS_FROM_DAILY_INCOME ? sumition - out : sumition
        ) + ControlPanel.getInstance().getCurrency()
        );
    }

    public void insertIntoarchOfDays(ArchOfDays rec) {
        try {
            String sqlString = "INSERT INTO archofdays VALUES (?,?,?)";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlString);
            DatabaseHandler.stat.setString(1, rec.Day.getText());
            DatabaseHandler.stat.setDouble(2, getIncomeOfDrinksFromRooms("dailySheet WHERE daily=1"));
            DatabaseHandler.stat.setDouble(3, rec.el3ohda);
            DatabaseHandler.stat.execute();
        } catch (org.sqlite.SQLiteException e) {
            updateArchOfDays(rec);
        } catch (Exception e) {
            Logger.writeLog("Eception In " + getClass().getName() + " -> insertIntoarchOfDays:" + e);
        }
    }

    public void updateArchOfDays(ArchOfDays rec) {
        try {
            DatabaseHandler.con.prepareStatement("UPDATE archofdays set imp=" + rec.el3ohda + " WHERE day='" + rec.Day.getText() + "'").execute();
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> updateArchOfDays:-" + e);
        }
    }

    @FXML
    private void goToExpense() {
        try {
            Exprenses_BTN_Static.fire();
        } catch (Exception ex) {
            Logger.writeLog("Exception in  " + getClass().getName() + " -> goToExpense:- " + ex);
        }
    }

    /*
        Takes the data from dailypullstore to archpullstore
     */
    public void mizRecords() {
        ArrayList<CafePullREcord> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM dailypullstore";
            ResultSet result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (result.next()) {
                list.add(new CafePullREcord(result.getInt(1), result.getString(2), result.getString(3), result.getDouble(4), result.getString(5), result.getInt(6)));
            }
            int x = 0;
            double prevArgNum = 0;
            int prevProNum = 0;
            for (int i = 0; i < list.size(); i++) {
                x = 0;
                sql = "SELECT * From archpullstore WHERE Day=? AND proName=? AND argName=?";
                DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                DatabaseHandler.stat.setString(1, list.get(i).day);
                DatabaseHandler.stat.setString(2, list.get(i).prodName);
                DatabaseHandler.stat.setString(3, list.get(i).argName);
                result = DatabaseHandler.stat.executeQuery();
                while (result.next()) {
                    prevArgNum = result.getDouble(4);
                    prevProNum = result.getInt(6);
                    prevArgNum += list.get(i).numOfArg;
                    prevProNum += list.get(i).numOfProd;
                    sql = "UPDATE archpullstore SET numofPullofarg=? , numofpro=? where  Day=? AND proName=? AND argName=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                    DatabaseHandler.stat.setDouble(1, prevArgNum);
                    DatabaseHandler.stat.setInt(2, prevProNum);
                    DatabaseHandler.stat.setString(3, list.get(i).day);
                    DatabaseHandler.stat.setString(4, list.get(i).prodName);
                    DatabaseHandler.stat.setString(5, list.get(i).argName);
                    DatabaseHandler.stat.execute();
                    x = 1;
                }
                if (x == 0) {
                    sql = "INSERT INTO archpullstore VALUES(?,?,?,?,?,?)";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                    DatabaseHandler.stat.setInt(1, list.get(i).id);
                    DatabaseHandler.stat.setString(2, list.get(i).day);
                    DatabaseHandler.stat.setString(3, list.get(i).argName);
                    DatabaseHandler.stat.setDouble(4, list.get(i).numOfArg);
                    DatabaseHandler.stat.setString(5, list.get(i).prodName);
                    DatabaseHandler.stat.setInt(6, list.get(i).numOfProd);
                    DatabaseHandler.stat.execute();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + " -> mizRecords :- " + e);
        }
    }

    public static double getIncomeOfDrinksFromRooms(String ss) {
        double d1 = 0;
        try {
            String sql = "SELECT drink,price FROM " + ss;
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            String drink;
            double price = 0, temp = 0;

            while (DatabaseHandler.result.next()) {
                drink = DatabaseHandler.result.getString(1);
                price = DatabaseHandler.result.getDouble(2);
                if (!drink.equals("")) {
                    String[] records = drink.split("#");
                    for (int i = 0; i < records.length; i++) {
                        String[] innerdata = records[i].split("@");
                        temp = Double.parseDouble(innerdata[3]);
                        d1 += price >= temp ? temp : price;
                    }
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + DailySheetController.class.getName() + " -> getIncomeOfDrinksFromRooms :- " + e);
        }
        return d1;
    }

    @FXML
    private void goToRestaurant() {
        if (!SelectedButton.equals(restaurantBTN)) {
            focusButton(restaurantBTN);
            tableContainer.getChildren().remove(ap);
            tableContainer.getChildren().add(2, LoadHelper.getInstance().screenMap.get("root_DayOrders"));
            VBox.setVgrow(LoadHelper.getInstance().screenMap.get("root_DayOrders"), Priority.ALWAYS);
        }
    }

    @FXML
    private void goToPlaying() {
        if (!SelectedButton.equals(playingBTN)) {
            focusButton(playingBTN);
            tableContainer.getChildren().remove(LoadHelper.getInstance().screenMap.get("root_DayOrders"));
            tableContainer.getChildren().add(2, ap);
        }
    }

    @FXML
    private void scrollHandler(ScrollEvent event) {
        util.gui.HelperMethods.incrementScrollSpeed(event, SP);
    }

}
