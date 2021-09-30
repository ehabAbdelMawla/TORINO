package settings.deletedata;

import controlpanel.ControlPanel;
import dialog.alert.confirm.ConfirmAlertController;
import datamodel.ArchOfDays;
import static datamodel.TempMethods.ClearData;
import static archive.ArchiveHomeController.Days;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDatePicker;
import dailysheet.DailySheetController;
import datamodel.Order;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.File;
import playing.hall.PlayingRoomsHallController;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import util.gui.load.Location;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.BuilderUI;
import util.gui.load.DialogHelper;
import util.gui.load.LoadHelper;
import util.services.BackupDB;

public class DeleteDataController implements Initializable {

    private int Arcflag = 0;
    private int strflag = 0;
    private ArrayList<LocalDate> myDays;
    private JFXButton selectedButton;
    @FXML
    private JFXCheckBox archBox, StoreBox;
    @FXML
    private JFXDatePicker fromDate, TODate;
    @FXML
    private VBox deleteDataBox, exportBackupBox;
    @FXML
    private JFXButton exportBackupTab, exportBackupButton, deleteDataTab;
    @FXML
    private Label statusText;
    @FXML
    private FontAwesomeIconView statusIcon;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            TODate.setValue(LocalDate.now());
            selectedButton = deleteDataTab;
            statusText.setText(BackupDB.getLocation().getAbsolutePath());
            statusIcon.setVisible(false);
        } catch (Exception ex) {
            showNotSelecedStatus();
            exportBackupButton.setDisable(true);
            ex.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + "initialize() " + ex);
        }
    }

    @FXML
    private void goToSettings() {
        try {
            homepage.HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_Settings"));
            homepage.HomePageController.title_Label.setText("الإعدادات");
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> goToSettings");
        }
    }

    @FXML
    private void DeleteAllData() {
        try {
            PlayingRoomsHallController.getPlayingStatus();
            int flag = PlayingRoomsHallController.flagForPlayingTimers;
            if (flag == 1) {
                DialogHelper.getInstance().showOKAlert("-يجب مغادرة جميع الغُرف أولا.");
                return;
            }
            DialogHelper.getInstance().showConfirmAlert("هل انت متأكد من حذف جميع البيانات ؟ \n سيتم غلق البرنامج");
            if (ConfirmAlertController.flag == 1) {
                ConfirmAlertController.flag = 0;

                ClearData("VAT");
                ClearData("add3ohda");
                ClearData("archofdays");
                ClearData("archpullstore");
                if (ControlPanel.getInstance().ACCESS_TABLES_NAMES) {
                    ClearData("cafeTables");
                } else {
                    DatabaseHandler.con.prepareStatement("UPDATE cafeTables SET tableName='table '||id").execute();
                }
                ClearData("cafeandexpnotes");
                ClearData("cafecategories");

                ClearData("customers");
                ClearData("dailycafesheet");
                ClearData("dailyexpense");
                ClearData("dailypullstore");
                ClearData("dailysheet");

                ClearData("deptData");
                ClearData("drinkprice");
                DatabaseHandler.con.prepareStatement("UPDATE el3ohda SET price=0").execute();

                ClearData("myday");
                ClearData("playingprice WHERE id!=0");
                DatabaseHandler.con.prepareStatement("UPDATE printData SET  shopName='',phoneNumbers='',address='',headerBand='shopName',image=null").execute();
                DatabaseHandler.con.prepareStatement("UPDATE promotions SET hourse=0, percentage=0").execute();
                ClearData("reservations");
                ClearData("resturantDetails");
                ClearData("resturantReceets");
                ClearData("room1drink");
                ClearData("room1playing");
                if (ControlPanel.getInstance().ACCESS_ROOM_NAMES) {
                    ClearData("roomNames");
                } else {
                    DatabaseHandler.con.prepareStatement("UPDATE roomNames SET roomName='Room '||id").execute();
                }

                ClearData("roomsnotes");
                DatabaseHandler.con.prepareStatement("UPDATE sendMailData SET shopName='',mail1='',mail2=''").execute();
                DatabaseHandler.con.prepareStatement("UPDATE taxAndServ SET serv=0, tax=0").execute();
                ClearData("store");
                ClearData("storeenter");
                ClearData("storeexit");
                ClearData("tempRoomsCutomers");
                ClearData("tempTablesCustomers");
//                *******************************************
                ClearData("users where accessConstrain != 'مالك'");
                System.exit(0);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + ".DeleteAllData() :-" + e);
        }
    }

    @FXML
    private void DeleteAction() {
        try {
            strflag = 0;
            Arcflag = 0;
            if (fromDate.getValue() == null || TODate.getValue() == null) {
                DialogHelper.getInstance().showOKAlert("- يجب ادخال جميع البيانات");
                return;
            } else if (fromDate.getValue().isAfter(TODate.getValue())) {
                DialogHelper.getInstance().showOKAlert("- الفترة التي ادخلتها غير صحيحة");
                return;
            }
            if (!(archBox.isSelected() || StoreBox.isSelected())) {
                DialogHelper.getInstance().showOKAlert("- يجب تحديد نوع البيانات !");
                return;
            }
            DialogHelper.getInstance().showConfirmAlert("هل انت متأكد من حذف البيانات؟ ");
            if (ConfirmAlertController.flag == 1) {
                ConfirmAlertController.flag = 0;
                if (archBox.isSelected()) {
                    myDays = getMuDays(Days);
                    if (myDays.size() > 0) {
                        deleteFromArch(fromDate.getValue(), TODate.getValue());

                    }
                }
                if (StoreBox.isSelected()) {
                    ArrayList<LocalDate> d1 = getStrDays("storeexit");
                    ArrayList<LocalDate> d2 = getStrDays("storeenter");
                    ArrayList<LocalDate> d3 = getStrDays("archpullstore");
                    if (d1.size() > 0 || d2.size() > 0 || d3.size() > 0) {
                        deletFromStorage(fromDate.getValue(), TODate.getValue(), "storeexit", d1);
                        deletFromStorage(fromDate.getValue(), TODate.getValue(), "storeenter", d2);
                        deletFromStorage(fromDate.getValue(), TODate.getValue(), "archpullstore", d3);

                        if (strflag != 0) {
                            LoadHelper.getInstance().screenMap.put("root_Storage", FXMLLoader.load(getClass().getResource(Location.getInstance().get("STORAGEVIEW"))));
                        }
                    }
                }
                if (Arcflag == 0 && strflag == 0) {
                    DialogHelper.getInstance().showOKAlert("- لا يوجد بيانات لهذه الفترة ");
                } else {
                    fromDate.setValue(null);
                    TODate.setValue(LocalDate.now());
                    StoreBox.setSelected(false);
                    archBox.setSelected(false);
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> DeleteAction:-" + e);
        }
    }

    public void deleteFromArch(LocalDate from, LocalDate TO) {
        try {
            if (from.isAfter(myDays.get(myDays.size() - 1))
                    || TO.isBefore(myDays.get(0))) {
                return;
            }
            Arcflag++;
            ClearData("dailypullstore where DATE(dailypullstore.Day) between '" + from.toString() + "' AND '" + TO.toString() + "'");
            ClearData("resturantReceets where DATE(date) between '" + from.toString() + "' AND '" + TO.toString() + "'");
            ClearData("dailyexpense where currentdate between '" + from.toString() + "' AND '" + TO.toString() + "' and daily=0");
            ClearData("archofdays where day between '" + from.toString() + "' AND '" + TO.toString() + "'");
            ClearData("dailysheet where actDate between '" + from.toString() + "' AND '" + TO.toString() + "' and daily=0");
            ClearData("cafeandexpnotes where Day between '" + from.toString() + "' AND '" + TO.toString() + "'");
            if (Arcflag != 0) {
                LoadHelper.getInstance().screenMap.put("root_ArchiveHome", FXMLLoader.load(getClass().getResource(Location.getInstance().get("ARCHEIVE"))));
                Order.fillDailyCafeTable();
                DailySheetController.endDayNot = 1;
                LoadHelper.getInstance().dailySheetController.updateAllData();
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> DeleteFromArch:-" + e);
        }
    }

    public ArrayList<LocalDate> getMuDays(ObservableList<ArchOfDays> s) {
        ArrayList<LocalDate> list = new ArrayList<>();
        for (int i = 0; i < s.size(); i++) {
            try {
                list.add(LocalDate.parse(s.get(i).Day.getText()));
            } catch (Exception e) {
                Logger.writeLog("Exception in " + getClass().getName() + " -> getMuDays:-" + e);
            }
        }
        return list;
    }

    public void deletFromStorage(LocalDate from, LocalDate To, String Table, ArrayList<LocalDate> Ddays) {
        try {
            if (!Ddays.isEmpty()) {
                if (from.isAfter(Ddays.get(Ddays.size() - 1))
                        || To.isBefore(Ddays.get(0))) {
                    return;
                }
                strflag++;
                ClearData(Table + " where day between '" + from.toString() + "' AND '" + To.toString() + "'");
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + ".deletFromStorage() :-" + e);
        }
    }

    private ArrayList<LocalDate> getStrDays(String Table) {
        ArrayList<LocalDate> list = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT day From " + Table;
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (DatabaseHandler.result.next()) {
                try {
                    list.add(LocalDate.parse(DatabaseHandler.result.getString(1)));
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + ".getStrDays() :-" + e);
        }
        return list;
    }

    @FXML
    private void KEv(KeyEvent event) {
        if (event.getCode() == KeyCode.BACK_SPACE) {
            goToSettings();
        }
    }

    @FXML
    private void goToExportBackup() {
        if (!selectedButton.equals(exportBackupTab)) {
            toggleButtons(exportBackupTab);
            exportBackupBox.setVisible(true);
            deleteDataBox.setVisible(false);
        }
    }

    @FXML
    private void goToDeleteData() {
        if (!selectedButton.equals(deleteDataTab)) {
            toggleButtons(deleteDataTab);
            deleteDataBox.setVisible(true);
            exportBackupBox.setVisible(false);
        }
    }

    @FXML
    private void chooseFolder(ActionEvent event) {
        try {
            System.out.println("===============================");

            Stage mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose Folder");
            File selectedFile = directoryChooser.showDialog(mainStage);
            if (selectedFile != null) {
                if (Files.isWritable(Paths.get(selectedFile.toURI()))) {
                    statusText.setText(selectedFile.getAbsolutePath());
                    statusIcon.setVisible(false);
                    exportBackupButton.setDisable(false);
                    BackupDB.setLocation(selectedFile);
                } else {
                    statusText.setText("لا يمكن التصدير في هذا المجلد.. أختر مجلد آخر");
                    statusIcon.setVisible(true);
                    statusIcon.setIcon(FontAwesomeIcon.CLOSE);
                    statusIcon.setStyle("-fx-fill:red;");
                    exportBackupButton.setDisable(true);
                    BackupDB.setLocation(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + " -> chooseFolder() " + e);
        }
    }

    private void showNotSelecedStatus() {
        System.out.println("showNotSelecedStatus() ");
        statusText.setText("لم يتم اختيار مكان للحفظ بعد");
        statusIcon.setVisible(true);
        statusIcon.setIcon(FontAwesomeIcon.CLOSE);
        statusIcon.setStyle("-fx-fill:red;");
        exportBackupButton.setDisable(true);
    }

    @FXML
    private void exportBackup() {
        try {
            exportBackupButton.setDisable(true);
            BackupDB.start();
            BuilderUI.showNotification(new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE),
                    "تم تصدير الملف بنجاح" + "\n" + BackupDB.getLocation().getAbsolutePath());
            exportBackupButton.setDisable(false);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + " -> exportBackup() " + e);
        }
    }

    private void toggleButtons(JFXButton button) {
        if (selectedButton.getStyleClass().contains("dark-button")) {
            selectedButton.getStyleClass().remove("dark-button");
        }
        selectedButton = button;
        if (!selectedButton.getStyleClass().contains("dark-button")) {
            selectedButton.getStyleClass().add("dark-button");
        }
    }
}
