/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package restaurant.dayorders.details;

import controlpanel.ControlPanel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import datamodel.InnerOrder;
import datamodel.Order;
import datamodel.Payment;
import javafx.fxml.FXML;
import javafx.scene.control.TreeTableColumn;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import util.Logger;
import util.db.DBField;
import util.gui.load.DialogHelper;
import util.printing.PrintIngController;

public class BillDetailsController implements Initializable {

    @FXML
    private HBox LabelsContainer;
    @FXML
    private HBox ClientNameContainer;
    @FXML
    private Label customerCateg;
    @FXML
    private JFXButton printButton;

    @FXML
    private JFXButton pay_BTN;
    @FXML
    private Label billNumber_Label;
    @FXML
    private Label customerName;
    @FXML
    private Label billKind;
    @FXML
    private Label cashierName;
    @FXML
    private JFXTreeTableView<InnerOrder> innerOrder_Table;
    @FXML
    private TreeTableColumn<InnerOrder, Double> total_Col;
    @FXML
    private TreeTableColumn<InnerOrder, Double> price_Col;
    @FXML
    private TreeTableColumn<InnerOrder, Double> amount_Col;
    @FXML
    private TreeTableColumn<InnerOrder, String> productName_Col;
    private static final ObservableList<InnerOrder> INNER_ORDERS = FXCollections.observableArrayList();
    private static Payment orderPayment;
    private Order currentOrder;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ==== tableConfiguration =====
        productName_Col.setCellValueFactory(param
                -> new SimpleObjectProperty<>(param.getValue().getValue().getProduct().getName()));
        amount_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));
        price_Col.setCellValueFactory(param
                -> new SimpleObjectProperty<>(param.getValue().getValue().getProduct().getPrice()));
        total_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("total"));
        TreeItem<InnerOrder> root = new RecursiveTreeItem<>(INNER_ORDERS, RecursiveTreeObject::getChildren);
        innerOrder_Table.setRoot(root);
        innerOrder_Table.setShowRoot(false);

        if (!ControlPanel.getInstance().CUSTOMER_DATA) {
            LabelsContainer.getChildren().remove(ClientNameContainer);
        }
//        ((HBox) printButton.getParent()).getChildren().remove(printButton);
    }

    public void setData(Order order, boolean visabliltyofPaidButton) {
        try {
            currentOrder = order;
            INNER_ORDERS.setAll(InnerOrder.productOrderList_To_InnerOrderList(order.getProducts()));
            pay_BTN.setVisible(order.getDebtAmount() != 0 && visabliltyofPaidButton);
            billNumber_Label.setText(order.getBillNumber().getText());
            if (order.getCustomer() == null) {
                customerName.setText("لايوجد");
            } else {
                customerName.setText(order.getCustomer().getFullName());
            }
            customerCateg.setText(order.getCustomerCateg());
            // BillKind = TakeAway | Bar | TableName
            if (!order.getOrderKind().equals("Hall")) {
                billKind.setText(order.getOrderKind());
            } else {
                billKind.setText(order.getTableName());
            }
            cashierName.setText(order.getCashier().getUserName());
            // Payment(double total, double discount, double required, double paid, double debt)
            orderPayment = new Payment(order.getSubTotalAmount(), order.getDiscountAmount(), order.getTotalAmount(), order.getPaidAmount(), order.getDebtAmount());
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.writeLog("Exception in : " + getClass().getName() + ".setData() :" + ex);
        }
    }

    @FXML
    private void keyEv(KeyEvent event) {
//        if (event.getCode().equals(KeyCode.ESCAPE)) {
//            ((Stage) innerOrder_Table.getScene().getWindow()).close();
//        }
    }

    @FXML
    private void payAction() {
        // open Confirm Alert
        double debtAmount = orderPayment.getDebt();
        String s = "دفع " + debtAmount + ControlPanel.getInstance().getCurrency() + " ؟";
        innerOrder_Table.requestFocus();
        if (DialogHelper.getInstance().showConfirmAlert(s) == 1) {
            currentOrder.setPaidAmount(currentOrder.getPaidAmount() + debtAmount);

            ArrayList<DBField> map = new ArrayList<>();
            map.add(new DBField(Integer.parseInt(currentOrder.getBillNumber().getText().trim()), "receetId", "PK"));
            map.add(new DBField(currentOrder.getPaidAmount(), "paid", "NN"));

            currentOrder.setFields(map);
            currentOrder.update();

            Order.fillDailyCafeTable();
            ((Stage) innerOrder_Table.getScene().getWindow()).close();
        }
    }

    @FXML
    private void showPaymentDetails() {
        Payment payment = DialogHelper.getInstance().openCheckOutBill(-1, orderPayment);
    }

    @FXML
    private void print() {

        PrintIngController.getInstance().saveReceet(Integer.parseInt(currentOrder.getBillNumber().getText()), true);
    }
}
