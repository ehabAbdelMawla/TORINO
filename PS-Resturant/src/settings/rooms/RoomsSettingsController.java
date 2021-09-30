/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package settings.rooms;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXTreeTableView;
import controlpanel.ControlPanel;
import datamodel.RoomsName;
import playing.rooms.util.RoomBox;
import playing.hall.PlayingRoomsHallController;
import static playing.hall.PlayingRoomsHallController.roomBoxes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import util.Logger;
import util.db.DatabaseHandler;
import util.db.Methods;
import util.gui.HelperMethods;
import util.gui.load.LoadHelper;
import util.gui.validation.ValidationMethods;
import static playing.hall.PlayingRoomsHallController.roomState_arrayList;
import util.gui.load.DialogHelper;
import static playing.hall.PlayingRoomsHallController.flowPane_Pointer;

public class RoomsSettingsController implements Initializable {

    @FXML
    private JFXTreeTableView<RoomsName> RoomNamesTable;

    @FXML
    private TreeTableColumn<RoomsName, String> name_Col;
    @FXML
    private TreeTableColumn<RoomsName, Button> del_Col;
    @FXML
    private TextField name_TextField;
    @FXML
    private HBox addRoomsContriner;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ... get Data  ...
        RoomsName.fillData();
        // ....  AlignCenter      ....
        HelperMethods.TableColumnAlignment(name_Col, del_Col);
        // ....  setTable   ....        
        HelperMethods.SetTableData(RoomNamesTable, false, true, RoomsName.roomNames);
        // ... Map Data & Columns ....
        name_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        del_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("DelBTN"));
        // .... Edit Columns ....
        editColumns();

        // Remove add Rooms if not available
        if (ControlPanel.getInstance().PREVENT_ADD_AND_DELETE_ROOMS) {
            if (addRoomsContriner != null && addRoomsContriner.getParent() != null) {
                ((VBox) addRoomsContriner.getParent().getParent()).getChildren().remove(addRoomsContriner.getParent());
            } else {
                addRoomsContriner.setVisible(!ControlPanel.getInstance().PREVENT_ADD_AND_DELETE_ROOMS);
            }
        }
    }

    @FXML
    private void backToSettings() {
        try {
            homepage.HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_Settings"));
            homepage.HomePageController.title_Label.setText("الإعدادات");
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> backToSettings():- " + e);
        }
    }

    private void editColumns() {
        name_Col.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        name_Col.setOnEditCommit((TreeTableColumn.CellEditEvent<RoomsName, String> event) -> {
            RoomsName row = null;
            try {
                row = RoomNamesTable.getTreeItem(event.getTreeTablePosition().getRow()).getValue();
                if (event.getNewValue().trim().equals("")) {
                    DialogHelper.getInstance().showOKAlert("يجب ادخال اسم جديد");
                    RoomsName.fillData();
                    return;
                }
                if (!ValidationMethods.checkDataBaseValuesConstraint(event.getNewValue().trim())) {
                    DialogHelper.getInstance().showOKAlert("يجب ان تكون البيانات خالية من (' , \" , \\ , / , % , - )");
                    RoomsName.fillData();
                    return;
                }
                String prevName = row.getName();
                row.setName(event.getNewValue().trim());
                int result = row.update();
                switch (result) {
                    case 1:
                        RoomBox editedBox = (RoomBox) PlayingRoomsHallController.flowPane_Pointer.getChildren()
                                .stream().filter(box
                                        -> ((RoomBox) box).getRoomNumber().equals(prevName)
                                )
                                .findAny().orElse(null);
                        editedBox.setRoomNumber(event.getNewValue().trim());
                        editedBox.roomView.getRoomController().roomName.setText(event.getNewValue().trim());
                        editedBox.getStage().setTitle(event.getNewValue().trim());
                        updateRoomName(prevName, event.getNewValue().trim());

                        break;
                    case -2:
                        DialogHelper.getInstance().showOKAlert("لا يمكن تكرار اسم الحجرة");
                        break;
                    default:
                        DialogHelper.getInstance().showOKAlert("حدثت مشكلة , اعد المحاولة في وقت لاحق");
                        break;
                }
                RoomsName.fillData();
            } catch (Exception e) {
                Logger.writeLog("EXCEPTION IN RestaurantHallSettingsController -> editTable():- " + e);
            }
        });
    }

    private void updateRoomName(String prevVal, String newVal) {

        String[] tables = {"dailysheet", "roomsnotes"};
        for (int i = 0; i < tables.length; i++) {
            try {
                DatabaseHandler.con.prepareStatement("UPDATE " + tables[i] + " SET roomNum='" + newVal + "' WHERE roomNum='" + prevVal + "'").execute();

            } catch (Exception e) {
                Logger.writeLog("Exception in RoomsSettingsController -> updateRoomName(1): " + e);
            }
        }
        String[] tables2 = {"room1drink", "room1playing"};
        for (int i = 0; i < tables2.length; i++) {
            try {
                DatabaseHandler.con.prepareStatement("UPDATE " + tables2[i] + " SET roomName='" + newVal + "' WHERE roomName='" + prevVal + "'").execute();

            } catch (Exception e) {
                Logger.writeLog("Exception in RoomsSettingsController -> updateRoomName(2): " + e);
            }
        }

    }

    @FXML
    private void addRoom() {
        try {
            if (DatabaseHandler.canAddMoreRowsInTable(RoomsName.TABLE_NAME)) {
                if (ValidationMethods.checkTextFeildConstraint(name_TextField)) {
                    int roomId = (int) Methods.GetMaximum("id", "roomNames");
                    RoomsName newRoom = new RoomsName(roomId, name_TextField.getText().trim());
                    int result = newRoom.add();
                    if (result < 1) {
                        DialogHelper.getInstance().showOKAlert("هذا الاسم موجود بالفعل !");
                        return;
                    }
                    RoomsName.fillData();
                    RoomBox box = new RoomBox(newRoom.name);
                    roomBoxes.add(box);
                    roomState_arrayList.add(box.RoomStateText);
                    flowPane_Pointer.getChildren().add(box);
                    HelperMethods.ResetTexts(name_TextField);
                } else {
                    DialogHelper.getInstance().showOKAlert("يجب ادخال اسم الحجرة !");
                }
            } else {
                DialogHelper.getInstance().showOKAlert("لا يمكن اضافة المزيد في النسخة التجريبية");
            }

        } catch (Exception e) {
            Logger.writeLog("Exception in RoomsSettingsController -> addRoom(): " + e);
        }
    }

    @FXML
    private void keyAct(KeyEvent event) {
        if (!ControlPanel.getInstance().PREVENT_ADD_AND_DELETE_ROOMS && event.getCode().equals(KeyCode.ENTER)) {
            addRoom();
        }
    }
}
