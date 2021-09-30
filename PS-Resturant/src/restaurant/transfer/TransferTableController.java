package restaurant.transfer;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import datamodel.tablesNames;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import restaurant.hall.HallController;
import util.db.DatabaseHandler;
import util.gui.load.DialogHelper;
import util.gui.load.LoadHelper;

public class TransferTableController implements Initializable {

    private static final ObservableList<String> ALL_TABLES = FXCollections.observableArrayList();
    @FXML
    private JFXTextField fromTextField;
    private static JFXTextField fromTextField_Static;
    @FXML
    private JFXComboBox<String> toComboBox;
    private static JFXComboBox<String> toComboBox_Static;

    public static int transferConfirmFlag;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        transferConfirmFlag = 0;
        fromTextField_Static = fromTextField;
        toComboBox_Static = toComboBox;

    }

    @FXML
    private void exchangeAction(Event event) {
        try {
            if (toComboBox.getValue() == null || toComboBox.getValue().trim().equals("")) {
                DialogHelper.getInstance().showOKAlert(" يجب اختيار طاولة!");
                return;
            }

//            Transfer orders to new Table
            DatabaseHandler.con.prepareStatement("UPDATE dailycafesheet SET tableName='" + toComboBox.getValue() + "' WHERE tableName='" + fromTextField.getText().trim() + "'").execute();

//            Deal With Customers 
            String fristCustomer = LoadHelper.getInstance().tableOrderController.checkTempCustomer(fromTextField.getText().trim());
            String secondCustomer = LoadHelper.getInstance().tableOrderController.checkTempCustomer(toComboBox.getValue());

            if (!fristCustomer.trim().equals("") && !secondCustomer.trim().equals("")) {
                //Remove First One 
                DatabaseHandler.con.prepareStatement("DELETE FROM tempTablesCustomers WHERE tableName='" + fromTextField.getText().trim() + "'").execute();
            } else if (!fristCustomer.trim().equals("")) {
                //Transfer Customer Too 
                DatabaseHandler.con.prepareStatement("UPDATE tempTablesCustomers SET tableName='" + toComboBox.getValue() + "' WHERE tableName='" + fromTextField.getText().trim() + "'").execute();
            }
            HallController.changeTableState(fromTextField.getText().trim(), 0);
            HallController.changeTableState(toComboBox.getValue(), 1);
            transferConfirmFlag = 1;
            closeAction(event);
        } catch (Exception e) {
            DialogHelper.getInstance().showOKAlert("Exception in TransferTableController -> exchangeAction() :- " + e);
        }
    }

    @FXML
    private void closeAction(Event event) {
        ((Stage) ((Node) (event.getSource())).getScene().getWindow()).close();
    }

    public static void setData(String currentTable) {
        fromTextField_Static.setText(currentTable);

        FilteredList<String> filtered = ALL_TABLES.filtered((table) -> {
            return !table.equals(currentTable);
        });
        toComboBox_Static.setItems(filtered);

    }

    public static void setAllTables(ObservableList<tablesNames> tables) {
        List<String> tableNames = tables.stream().map(tablesNames::getTableName).collect(Collectors.toList());
        ALL_TABLES.setAll(tableNames);
    }

    @FXML
    private void keyAct(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            exchangeAction(event);
        }
    }

}
