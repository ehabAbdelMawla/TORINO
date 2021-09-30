/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package datamodel;

import dialog.alert.confirm.ConfirmAlertController;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.sql.ResultSet;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import static restaurant.transfer.TransferTableController.setAllTables;
import util.Logger;
import util.db.DBCRUD;
import util.db.DBField;
import util.db.DatabaseHandler;
import util.db.Methods;
import util.gui.button.TableViewButton;
import util.gui.load.DialogHelper;

/**
 *
 * @author Ehab Abdel Mawla
 */
public class tablesNames extends DBCRUD<tablesNames> {

    public static int updateFlag = 0;
    public static ObservableList<tablesNames> tableNames = FXCollections.observableArrayList();
    /**
     * Name of table in database
     */
    public static String dbTableName = "cafeTables";

    private int id;
    private String tableName;
    public TableViewButton delButton = new TableViewButton("حذف", new FontAwesomeIconView(FontAwesomeIcon.TRASH));
    private boolean free;

//    .... Contructor ....
//    New Table Name
    public tablesNames(String name) {
        super(dbTableName);
        this.tableName = name;
        this.id = (int) Methods.GetMaximum("id", dbTableName);;
        setFields(createMap());
    }
//    Prev Table Name

    public tablesNames(int id, String name) {
        super(dbTableName);
        this.id = id;
        this.tableName = name;
        this.delButton.setOnAction(deleteAction());
        setFields(createMap());
    }

    public tablesNames(int id, String name, boolean free) {
        super(dbTableName);
        this.id = id;
        this.tableName = name;
        this.delButton.setOnAction(deleteAction());
        this.free = free;
        setFields(createMap());
    }

// .... Getters & Setters .... 
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
        getFields().get(1).setVal(tableName);
    }

    public JFXButton getDelButton() {
        return delButton;
    }

    public void setDelButton(JFXButton delButton) {
        this.delButton = (TableViewButton) delButton;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    @Override
    public ArrayList<DBField> createMap() {
        ArrayList<DBField> map = new ArrayList<>();
        map.add(new DBField(id, "id", "PK"));
        map.add(new DBField(tableName, "tableName", "NN"));
        return map;
    }

    // ... button handle ...
    private EventHandler<ActionEvent> deleteAction() {
        return evene -> {

            if (ableToDelete()) {

                ConfirmAlertController.flag = 0;
                DialogHelper.getInstance().showConfirmAlert("هل انت متاكد من حذف الطاولة ؟");
                if (ConfirmAlertController.flag == 1) {
                    remove();
                    updateFlag = 1;
                    fillData();

                }
            } else {
                DialogHelper.getInstance().showOKAlert("لا يمكن حذف الطاولة حاليا");
            }
        };

    }

    private boolean ableToDelete() {
        try {
            return Methods.getCount("SELECT COUNT(*) FROM dailycafesheet WHERE tableName='" + tableName + "'") == 0;
        } catch (Exception e) {

        }
        return false;
    }

    public static void fillData() {
        tableNames.clear();
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT cafeTables.id,cafeTables.tableName,COUNT(dailycafesheet.tableName) FROM " + dbTableName + " LEFT JOIN dailycafesheet ON dailycafesheet.tableName=cafeTables.tableName  GROUP BY cafeTables.id ").executeQuery();
            int count = 0;
            while (res.next()) {
                count = res.getInt(3);
                tableNames.add(
                        new tablesNames(res.getInt(1), res.getString(2), (count == 0))
                );
            }
            setAllTables(tableNames);
        } catch (Exception e) {
            Logger.writeLog("Exception in tablesNames-> fillData() :" + e);
        }
    }

}
