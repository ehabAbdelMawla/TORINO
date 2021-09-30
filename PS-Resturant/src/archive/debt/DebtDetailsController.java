/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package archive.debt;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author Bayoumi
 */
import com.jfoenix.controls.JFXTreeTableView;
import datamodel.deptDetails;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import util.db.Methods;
import util.gui.HelperMethods;

public class DebtDetailsController implements Initializable {

    private final ObservableList<deptDetails> data = FXCollections.observableArrayList();

    @FXML
    private JFXTreeTableView<deptDetails> debtTable;

    @FXML
    private TreeTableColumn<deptDetails, Double> debtAmount_Col;

    @FXML
    private TreeTableColumn<deptDetails, String> username_Col;

    @FXML
    private TreeTableColumn<deptDetails, Integer> billNumber_Col;

    public void setData(ObservableList list) {
        data.setAll(list);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

//        Columns Configuration
        debtAmount_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("deptVal"));
        username_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("cashierName"));
        billNumber_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("billNumber"));
//        AlignData In Center
        HelperMethods.TableColumnAlignment(debtAmount_Col,
                username_Col,
                billNumber_Col);

//        set Table Data
        HelperMethods.SetTableData(debtTable, false, false, data);

    }

}
