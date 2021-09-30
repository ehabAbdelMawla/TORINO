/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package settings.restaurant.hall;

import com.jfoenix.controls.JFXTreeTableView;
import datamodel.tablesNames;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.HelperMethods;
import util.gui.load.DialogHelper;
import util.gui.load.LoadHelper;
import util.gui.validation.ValidationMethods;

public class RestaurantHallSettingsController implements Initializable {

    @FXML
    private JFXTreeTableView<tablesNames> RestaurantTable;

    @FXML
    private TreeTableColumn<tablesNames, Button> del_Col;

    @FXML
    private TreeTableColumn<tablesNames, String> name_Col;
    @FXML
    private TextField name_TextField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        .... Map data & Column ....

        del_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("delButton"));
        name_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("tableName"));

        HelperMethods.SetTableData(RestaurantTable, false, true, tablesNames.tableNames);
//        .... Set Column Alignment ....
        HelperMethods.TableColumnAlignment(name_Col, del_Col);

//        ... set Table edit  
        editTable();
    }

    private void editTable() {
        name_Col.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        name_Col.setOnEditCommit((TreeTableColumn.CellEditEvent<tablesNames, String> event) -> {
            tablesNames row = null;
            try {
                row = RestaurantTable.getTreeItem(event.getTreeTablePosition().getRow()).getValue();
                if (event.getNewValue().trim().equals("")) {
                    DialogHelper.getInstance().showOKAlert("يجب ادخال اسم جديد");
                    tablesNames.fillData();
                    return;
                }
                if (!ValidationMethods.checkDataBaseValuesConstraint(event.getNewValue().trim())) {
                    DialogHelper.getInstance().showOKAlert("يجب ان تكون البيانات خالية من (' , \" , \\ , / , % , - )");
                    tablesNames.fillData();
                    return;
                }
                row.setTableName(event.getNewValue().trim());
                int result = row.update();
                switch (result) {
                    case 1:
                        tablesNames.updateFlag = 1;
                        break;
                    case -2:
                        DialogHelper.getInstance().showOKAlert("لا يمكن تكرار اسم الطاولة");
                        break;
                    default:
                        DialogHelper.getInstance().showOKAlert("حدثت مشكلة , اعد المحاولة في وقت لاحق");
                        break;
                }
                tablesNames.fillData();
            } catch (Exception e) {
                Logger.writeLog("EXCEPTION IN RestaurantHallSettingsController -> editTable():- " + e);
            }

        });
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

    @FXML
    private void addTableAction() {
        try {
            if (DatabaseHandler.canAddMoreRowsInTable(tablesNames.dbTableName)) {
                if (!ValidationMethods.checkTextFeildConstraint(name_TextField)) {
                    DialogHelper.getInstance().showOKAlert("يجب ادخال اسم الطاولة");
                    return;
                } else if (!ValidationMethods.checkDataBaseValuesConstraint(name_TextField)) {
                    DialogHelper.getInstance().showOKAlert("يجب ان تكون البيانات خالية من (' , \" , \\ , / , % , - )");
                    return;
                }
                tablesNames newOne = new tablesNames(name_TextField.getText().trim());
                int result = newOne.add();
                switch (result) {
                    case 1:
                        tablesNames.fillData();
                        tablesNames.updateFlag = 1;
                        HelperMethods.ResetTexts(name_TextField);
                        break;
                    case -2:
                        DialogHelper.getInstance().showOKAlert("لا يمكن تكرار اسم الطاولة");
                        break;
                    default:
                        DialogHelper.getInstance().showOKAlert("حدثت مشكلة , اعد المحاولة في وقت لاحق");
                        break;
                }
            } else {
                DialogHelper.getInstance().showOKAlert("لا يمكن اضافة المزيد في النسخة التجريبية");
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> addTableAction():- " + e);
        }
    }

    @FXML
    private void keyAct(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            addTableAction();
        }
    }

}
