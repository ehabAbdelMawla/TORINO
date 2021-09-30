/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package restaurant.choosecustomer;

import controlpanel.ControlPanel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import datamodel.Customer;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import util.gui.load.DialogHelper;

public class ChooseCustomerController implements Initializable {

    private static final ObservableList<Customer> FILTERED_DATA = FXCollections.observableArrayList();
    @FXML
    private JFXTreeTableView<Customer> cutomersTable;
    @FXML
    private TreeTableColumn<Customer, String> name_Col;
    @FXML
    private TreeTableColumn<Customer, String> phone_Col;
    @FXML
    private TreeTableColumn<Customer, String> email_Col;
    @FXML
    private TreeTableColumn<Customer, Double> amountPaid_Col;
    @FXML
    private TreeTableColumn<Customer, Double> amountDebt_Col;
    @FXML
    private TreeTableColumn<Customer, JFXButton> choose_Col;
    @FXML
    private TreeTableColumn<Customer, Double> creditHourse_Col;
    @FXML
    private TreeTableColumn<Customer, Double> totalHourse_Col;
    @FXML
    public ToggleGroup customerTypeGroup;
    @FXML
    public TextField searchComBox;

    public boolean hasData;
    public Customer choosenCustomer;
    public Customer.CustomerType currentType = Customer.CustomerType.Guest;
    @FXML
    public VBox parentNode;
    @FXML
    private JFXRadioButton guestRadio;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        util.gui.HelperMethods.setRelativeSize(parentNode, 0.6, 900, 500);
        // ## tableConfiguration ##
        name_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("fullName"));
        phone_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("phoneNumber"));
        email_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("email"));
        amountPaid_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("amountPaid"));
        amountDebt_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("amountDebt"));
        choose_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("choose"));
        creditHourse_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("creditHourse"));
        totalHourse_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("totalHourse"));

//        ==== Remove credit column  ====
        if (!ControlPanel.getInstance().HOUR_PROMOTIONS) {
            cutomersTable.getColumns().remove(creditHourse_Col);
        }

        if (!ControlPanel.getInstance().HAS_PLAYSTATION) {
            cutomersTable.getColumns().remove(creditHourse_Col);
            cutomersTable.getColumns().remove(totalHourse_Col);
        }
//       ==== ==== ==== ==== ====  ==== ====

        TreeItem<Customer> root = new RecursiveTreeItem<>(FILTERED_DATA, RecursiveTreeObject::getChildren);
        cutomersTable.setRoot(root);
        cutomersTable.setShowRoot(false);

        customerTypeGroup.selectedToggleProperty().addListener((currentSelected) -> {
            String currentVal = ((RadioButton) customerTypeGroup.getSelectedToggle()).getText();

            switch (currentVal) {
                case "موظف":
                    currentType = Customer.CustomerType.Staff;
                    break;
                case "عميل":
                    currentType = Customer.CustomerType.Guest;
                    break;
                default:
                    currentType = Customer.CustomerType.Owner;
                    break;

            }
            Filter();

        });
        searchComBox.textProperty().addListener((observable, oldValue, newValue)
                -> cutomersTable.setPredicate((customer) -> {
                    return customer.getValue().getCustomerType().equals(currentType) && (customer.getValue().getFullName().toLowerCase().contains(searchComBox.getText().trim().toLowerCase())
                            || customer.getValue().getPhoneNumber().toLowerCase().contains(searchComBox.getText().trim().toLowerCase())
                            || customer.getValue().getEmail().toLowerCase().contains(searchComBox.getText().trim().toLowerCase()));
                }));

        customerTypeGroup.selectToggle(customerTypeGroup.getToggles().get(0));
        Filter();
    }

    @FXML
    private void AddCustomer(ActionEvent event) {
        choosenCustomer = DialogHelper.getInstance().viewCustomer(null, true, false);
        if (choosenCustomer != null) {
            hasData = true;
            ((Stage) cutomersTable.getScene().getWindow()).close();
        }
    }

    @FXML
    private void KeyEv(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ESCAPE)) {
            hasData = false;
            ((Stage) cutomersTable.getScene().getWindow()).close();
        }
    }

    public void Filter() {
        FILTERED_DATA.setAll(Customer.customersData.filtered((customer) -> customer.getCustomerType().equals(currentType)));

    }

    public void addPredicate() {
        guestRadio.setSelected(true);
        Filter();
        searchComBox.setText("");

    }

}
