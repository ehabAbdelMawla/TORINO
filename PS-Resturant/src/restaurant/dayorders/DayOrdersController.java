package restaurant.dayorders;

import controlpanel.ControlPanel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import datamodel.Customer;
import datamodel.Order;
import datamodel.Product;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import util.gui.button.CategoryButton;
import util.gui.button.TableViewButton;
import util.gui.load.DialogHelper;

public class DayOrdersController implements Initializable {

    public static Label totalPointer, deptSumitionPointer, takAwaySumitionPointer, hallSumitionPointer, barSumitionPointer, deliverySumitionPointer;
    public static JFXTreeTableView<Order> DayOrdersTablePointer;
    public static ObservableList<Order> orderObservableList = FXCollections.observableArrayList();
    // ================
    @FXML
    private JFXTreeTableView<Order> DayOrdersTable;
    @FXML
    private TreeTableColumn<Order, String> time_Col;
    @FXML
    private TreeTableColumn<Order, String> date_Col;
    @FXML
    private TreeTableColumn<Order, JFXButton> billNumber_Col;
    @FXML
    private TreeTableColumn<Order, String> billKind_Col;
    @FXML
    private TreeTableColumn<Order, Double> subTotalAmount_Col;
    @FXML
    private TreeTableColumn<Order, Double> discount_Col;
    @FXML
    private TreeTableColumn<Order, Double> totalAmount_Col;
    @FXML
    private TreeTableColumn<Order, Double> amountPaid_Col;
    @FXML
    private TreeTableColumn<Order, Double> debt_Col;
    @FXML
    private TreeTableColumn<Order, String> cashier_Col;
    @FXML
    private TreeTableColumn<Order, TableViewButton> customer_Col;
    @FXML
    private TextField filterField;
    @FXML
    private Label total, deptSumition, takAwaySumition, hallSumition, barSumition, deliverySumition;
    @FXML
    private HBox labelsContainer;
    @FXML
    private VBox deptVBox, hallFilterBox, barFilterBox, deliveryFilterBox;
    @FXML
    private TreeTableColumn<Order, FontAwesomeIconView> statusBox_Col;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DayOrdersTablePointer = DayOrdersTable;
        totalPointer = total;
        deptSumitionPointer = deptSumition;

        takAwaySumitionPointer = takAwaySumition;
        hallSumitionPointer = hallSumition;
        barSumitionPointer = barSumition;
        deliverySumitionPointer = deliverySumition;
        if (!ControlPanel.getInstance().ACCESS_HALL_TABLES) {
            ((HBox) hallFilterBox.getParent()).getChildren().remove(hallFilterBox);
        }
        if (!ControlPanel.getInstance().RESTAURANT_BAR) {
            ((HBox) barFilterBox.getParent()).getChildren().remove(barFilterBox);
        }
        if (!ControlPanel.getInstance().RESTAURANT_Delivery) {
            ((HBox) deliveryFilterBox.getParent()).getChildren().remove(deliveryFilterBox);
        }
        CategoryButton.getAllCategories();
        Product.getAllProducts();
        if (ControlPanel.getInstance().CUSTOMER_DATA) {
            Customer.fillData();
        }
        Order.fillDailyCafeTable();
        tableConfiguration();
        if (!ControlPanel.getInstance().CUSTOMER_DATA) {
            DayOrdersTable.getColumns().remove(customer_Col);
            DayOrdersTable.getColumns().remove(debt_Col);
            labelsContainer.getChildren().remove(deptVBox);
        }

        filterField.textProperty().addListener((observable, oldValue, newValue)
                -> DayOrdersTable.setPredicate(orderTreeItem
                        -> orderTreeItem.getValue().getBillNumber().getText().contains(filterField.getText().trim())));
    }

    private void tableConfiguration() {
        time_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("time"));
        date_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("date"));
        billNumber_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("billNumber"));
        billKind_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("orderKind"));
        subTotalAmount_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("subTotalAmount"));
        discount_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("discountAmount"));
        totalAmount_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("totalAmount"));
        amountPaid_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("paidAmount"));
        debt_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("debtAmount"));
        cashier_Col.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getValue().getCashier().getUserName()));
        customer_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("showCustomerData"));
        statusBox_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("statusIcon"));
        
        TreeItem<Order> root = new RecursiveTreeItem<>(orderObservableList, RecursiveTreeObject::getChildren);
        DayOrdersTable.setRoot(root);
        DayOrdersTable.setShowRoot(false);
    }

    @FXML
    private void filterDebt() {
        DayOrdersTable.setPredicate(orderTreeItem
                -> orderTreeItem.getValue().getDebtAmount() > 0);
    }

    @FXML
    private void filterHall() {
        DayOrdersTable.setPredicate(orderTreeItem
                -> orderTreeItem.getValue().getOrderKind().contains("Hall"));
    }

    @FXML
    private void filterTakeAway() {
        DayOrdersTable.setPredicate(orderTreeItem
                -> orderTreeItem.getValue().getOrderKind().contains("TakeAway"));
    }

    @FXML
    private void filterBarOrders() {
        DayOrdersTable.setPredicate(orderTreeItem
                -> orderTreeItem.getValue().getOrderKind().contains("Bar"));
    }

    @FXML
    private void filterdeliveryOrders() {
        DayOrdersTable.setPredicate(orderTreeItem
                -> orderTreeItem.getValue().getOrderKind().contains("Delivery"));
    }

    @FXML
    private void selectAll() {
        DayOrdersTable.setPredicate(orderTreeItem -> true);
    }

    public static void openCustomerDetails(Customer customer) {
        if (customer != null && customer.getFullName() != null && !customer.getFullName().equals("لا يوجد") && !customer.getFullName().equals("")) {
            DialogHelper.getInstance().viewCustomer(customer, false, false);
        }
    }

}
