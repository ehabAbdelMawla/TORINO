package settings.devices;

import dialog.alert.confirm.ConfirmAlertController;
import dialog.edit.playing.prices.EditPlayingPricesController;
import datamodel.PlayingPriceTableRecord;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import controlpanel.ControlPanel;
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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import util.Logger;
import util.db.DatabaseHandler;
import util.db.Methods;
import util.gui.load.DialogHelper;
import util.gui.load.LoadHelper;
import util.gui.load.Location;

public class DevicesSettingsController implements Initializable {

    @FXML
    private JFXTreeTableView<PlayingPriceTableRecord> myTable;
    @FXML
    private TreeTableColumn<PlayingPriceTableRecord, String> DNcol;
    @FXML
    private TreeTableColumn<PlayingPriceTableRecord, Float> Scol;
    @FXML
    private TreeTableColumn<PlayingPriceTableRecord, Float> Mcol;
    @FXML
    private TreeTableColumn<PlayingPriceTableRecord, Button> Ecol;
    @FXML
    private TreeTableColumn<PlayingPriceTableRecord, Button> Dcol;
    @FXML
    private TextField DNText;
    @FXML
    private TextField MText;
    @FXML
    private TextField SText;

    public static int actNot = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        actNot = 0;
        DNcol.setCellValueFactory(new TreeItemPropertyValueFactory<>("deviceName"));
        Scol.setCellValueFactory(new TreeItemPropertyValueFactory<>("singlePrice"));
        Mcol.setCellValueFactory(new TreeItemPropertyValueFactory<>("MultiPrice"));
        Ecol.setCellValueFactory(new TreeItemPropertyValueFactory<>("EditBtn"));
        Dcol.setCellValueFactory(new TreeItemPropertyValueFactory<>("DelBtn"));
        util.gui.HelperMethods.TableColumnAlignment(DNcol, Scol, Mcol, Ecol, Dcol);
        fillPlayingPriceTable();
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
    private void ADDRECORD(Event event) {
        try {
            if (DatabaseHandler.canAddMoreRowsInTable("PlayingPrice")) {
                if (DNText.getText().trim().equals("") || MText.getText().trim().equals("") || SText.getText().trim().equals("")) {
                    DialogHelper.getInstance().showOKAlert("- يجب ادخال جميع البيانات");
                    return;
                } else if (DNText.getText().trim().trim().contains("#") || DNText.getText().trim().trim().contains("@")) {
                    DialogHelper.getInstance().showOKAlert("- لا يمكن ان يحتوي النوع علي @ او # ");
                    return;
                }
                float s;
                float M;
                try {
                    s = Float.parseFloat(SText.getText().trim());
                    M = Float.parseFloat(MText.getText().trim());
                    if (s < 0 || M < 0) {
                        DialogHelper.getInstance().showOKAlert("- البيانات التي ادخلتها غير صحيحة");
                        return;
                    }
                } catch (NumberFormatException e) {
                    DialogHelper.getInstance().showOKAlert("- البيانات التي ادخلتها غير صحيحة");
                    return;
                }
                insertInPlayingTable(new PlayingPriceTableRecord(
                        DNText.getText(),
                        s,
                        M, -5
                ));
                fillPlayingPriceTable();
                DNText.setText("");
                MText.setText("");
                SText.setText("");
                actNot = 1;
            } else {
                DialogHelper.getInstance().showOKAlert("لا يمكن اضافة المزيد في النسخة التجريبية");
            }

        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> ADDRECORD:-" + e);
        }
    }

    public void fillPlayingPriceTable() {
        ObservableList<PlayingPriceTableRecord> sers = selectPlayingData();
        TreeItem<PlayingPriceTableRecord> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        myTable.setRoot(root);
        myTable.setShowRoot(false);
    }

    public ObservableList<PlayingPriceTableRecord> selectPlayingData() {
        ObservableList<PlayingPriceTableRecord> list = FXCollections.observableArrayList();
        try {
            list.clear();
            String removeMatchDevice = ControlPanel.getInstance().MATCH_DEVICE ? "" : "WHERE id>0 ";
            String sqlState = "SELECT * FROM PlayingPrice " + removeMatchDevice + " ORDER BY id ASC";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();
            PlayingPriceTableRecord rec;
            while (DatabaseHandler.result.next()) {
                rec = new PlayingPriceTableRecord(DatabaseHandler.result.getString(1), DatabaseHandler.result.getFloat(2), DatabaseHandler.result.getFloat(3), DatabaseHandler.result.getInt(4));
                rec.DelBtn.setDisable(rec.id == 0);
                rec.DelBtn.setOnAction(DeleteThisRecord(rec));
                rec.EditBtn.setOnAction(EditThisRecord(rec));
                list.add(rec);
            }
            DatabaseHandler.result.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Error In " + getClass().getName() + " -> selectPlayingData:-" + e);
            return list;
        }
    }

    public EventHandler<ActionEvent> EditThisRecord(PlayingPriceTableRecord rec) {
        return (ActionEvent event) -> {
            try {
                EditPlayingPricesController.Temp = rec;
                Parent root = FXMLLoader.load(getClass().getResource(Location.getInstance().get("EDIT_PLAYING_PRICE")));
                Scene scene = new Scene(root);
                Stage stage = util.gui.BuilderUI.initStageUnDecorated(scene, "Edit", "info");
                stage.showAndWait();
                if (EditPlayingPricesController.flag == 1) {
                    PlayingPriceTableRecord NewRec = EditPlayingPricesController.Temp;
                    String sql = "Update PlayingPrice SET device=? , SinglePrice=?,MultiPrice=?  Where device=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                    DatabaseHandler.stat.setString(1, NewRec.deviceName);
                    DatabaseHandler.stat.setFloat(2, NewRec.singlePrice);
                    DatabaseHandler.stat.setFloat(3, NewRec.MultiPrice);
                    DatabaseHandler.stat.setString(4, rec.deviceName);
                    DatabaseHandler.stat.execute();
                    DatabaseHandler.stat.close();
                    fillPlayingPriceTable();
                    actNot = 1;
                }
            } catch (org.sqlite.SQLiteException e) {
                DialogHelper.getInstance().showOKAlert("- هذا الجهاز موجود بالفعل");
            } catch (Exception e) {
                Logger.writeLog("Exception  In " + getClass().getName() + " -> EditThisRecord:-" + e);
            }
        };
    }

    public EventHandler<ActionEvent> DeleteThisRecord(PlayingPriceTableRecord rec) {
        return (ActionEvent event) -> {
            try {
                if (ableToRemove(rec.deviceName)) {
                    DialogHelper.getInstance().showConfirmAlert("هل انت متأكد من حذف هذا الجهاز؟");
                    if (ConfirmAlertController.flag == 1) {
                        ConfirmAlertController.flag = 0;
                        String sql = "DELETE FROM PlayingPrice Where id=? ";
                        DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                        DatabaseHandler.stat.setInt(1, rec.id);
                        DatabaseHandler.stat.execute();
                        DatabaseHandler.stat.close();
                        fillPlayingPriceTable();
                        actNot = 1;
                    }
                } else {
                    DialogHelper.getInstance().showOKAlert("- لا يمكن حذف هذا الجهاز الان");
                }
            } catch (Exception e) {
                Logger.writeLog("Exception In " + getClass().getName() + " -> DeleteThisRecord:-" + e);
            }
        };
    }

    public void insertInPlayingTable(PlayingPriceTableRecord myrec) {
        try {
            String sqlString = "INSERT INTO PlayingPrice VALUES (?,?,?,?,0)";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlString);
            DatabaseHandler.stat.setString(1, myrec.deviceName);
            DatabaseHandler.stat.setFloat(2, myrec.singlePrice);
            DatabaseHandler.stat.setFloat(3, myrec.MultiPrice);
            DatabaseHandler.stat.setInt(4, (int) Methods.GetMaximum("id", "playingprice"));
            DatabaseHandler.stat.execute();
            DatabaseHandler.stat.close();
        } catch (org.sqlite.SQLiteException e) {
            DialogHelper.getInstance().showOKAlert("- هذا الاسم موجود بالفعل");
        } catch (Exception e) {
            Logger.writeLog("Exception In " + getClass().getName() + " -> insertInPlayingTable:-" + e);
        }
    }

    public boolean ableToRemove(String DeviceName) {
        int count = 0;
        try {
            String sql = "Select count(price) from room1playing where device=?";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setString(1, DeviceName);
            DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
            while (DatabaseHandler.result.next()) {
                count += DatabaseHandler.result.getInt(1);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> ableToRemove:-" + e);
        }
        return count == 0;
    }

    @FXML
    private void ADEVE(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            ADDRECORD((Event) event);
        }
    }

}
