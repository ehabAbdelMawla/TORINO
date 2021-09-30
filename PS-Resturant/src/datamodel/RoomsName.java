package datamodel;

import com.jfoenix.controls.JFXButton;
import controlpanel.ControlPanel;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import playing.rooms.util.RoomBox;
import playing.hall.PlayingRoomsHallController;
import java.sql.ResultSet;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import util.Logger;
import util.db.DBCRUD;
import util.db.DBField;
import util.db.DatabaseHandler;
import util.gui.load.DialogHelper;

/**
 *
 * @author Ehab Abdel Mawla
 */
public class RoomsName extends DBCRUD<RoomsName> {

    public static ObservableList<RoomsName> roomNames = FXCollections.observableArrayList();

    public static final String TABLE_NAME = "roomNames";

    private int id;
    public String name;
    public Button DelBTN = new JFXButton("");

    public RoomsName(int id, String roomName) {
        super(TABLE_NAME);
        this.id = id;
        this.name = roomName;
        FontAwesomeIconView trashIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
        trashIcon.setStyleClass("table-btn-icon");
        DelBTN.setGraphic(trashIcon);
        DelBTN.getStyleClass().add("table-btn");
        DelBTN.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        DelBTN.setDisable(ControlPanel.getInstance().PREVENT_ADD_AND_DELETE_ROOMS);
        DelBTN.setOnAction(e -> {
            try {
                if (roomIsEmpty()) {
                    if (DialogHelper.getInstance().showConfirmAlert("هل انت متاكد من حذف الحجرة") == 1) {
                        DatabaseHandler.con.prepareStatement("DELETE FROM roomNames where roomName='" + roomName + "'").execute();
                        RoomsName.fillData();
                        RoomBox deletedBox = (RoomBox) PlayingRoomsHallController.flowPane_Pointer.getChildren()
                                .stream().filter(box
                                        -> ((RoomBox) box).getRoomNumber().equals(name)
                                )
                                .findAny().orElse(null);
                        if (deletedBox != null) {
                            PlayingRoomsHallController.roomBoxes.remove(deletedBox);
                            PlayingRoomsHallController.roomState_arrayList.remove(deletedBox.RoomStateText);
                            PlayingRoomsHallController.flowPane_Pointer.getChildren().remove(deletedBox);
                        }
                    }
                } else {
                    DialogHelper.getInstance().showOKAlert("لا يمكن حذف هذه الحجرة حاليا \n يجب مغادرتها اولا !");
                }
            } catch (Exception ex) {
                Logger.writeLog("Exception in RoomsName ->  DelBTN.setOnAction :- " + ex);
            }
        });
        setFields(createMap());

    }

    public Button getDelBTN() {
        return DelBTN;
    }

    public void setDelBTN(Button DelBTN) {
        this.DelBTN = DelBTN;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        getFields().get(1).setVal(name);
    }

    public static void fillData() {
        roomNames.clear();
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT * from " + TABLE_NAME).executeQuery();
            while (res.next()) {
                roomNames.add(new RoomsName(res.getInt(1), res.getString(2)));
            }
        } catch (Exception e) {

            Logger.writeLog("Exception in roomName-> fillData() " + e);
        }
    }

    @Override
    public ArrayList<DBField> createMap() {
        ArrayList<DBField> map = new ArrayList<>();
        map.add(new DBField(id, "id", "PK"));
        map.add(new DBField(name, "roomName", "NN"));
        return map;
    }

    private boolean roomIsEmpty() {
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT * FROM room1playing WHERE roomName='" + this.name + "'").executeQuery();
            while (res.next()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            Logger.writeLog("Exception in roomName-> roomIsEmpty() " + e);
            return false;
        }

    }

}
