package restaurant.hall;

import controlpanel.ControlPanel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import datamodel.ArgumentsClass;
import datamodel.Customer;
import datamodel.InnerOrder;
import datamodel.Order;
import datamodel.Order.OrderKind;
import datamodel.Payment;
import datamodel.Product;
import static datamodel.Product.ALL_PRODUCTS;
import dialog.checkout.CheckOutBillController;
import dialog.number.NumberDialogController;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import settings.products.NewProductController;
import util.Logger;
import util.db.DBCRUD;
import util.db.DBField;
import util.gui.button.CategoryButton;
import util.gui.HelperMethods;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.util.Duration;
import storage.StorageController;
import util.db.DatabaseHandler;
import util.gui.BuilderUI;
import util.gui.ScrollHandler;
import util.gui.button.ProductButton;
import util.gui.load.DialogHelper;
import util.gui.load.Location;

public class TableOrderController implements Initializable {

    /**
     * Calculation Parameters
     */
    private int lastValue_ProductAmount = 0;
    private boolean isUIButtonReleased = false;
    // ================================
    public static Label TableNumber_Label_Static;
    private static CategoryButton focusedButton;
    private final ObservableList<Product> productFocused_ObservableList = FXCollections.observableArrayList();
    public static final ObservableList<InnerOrder> INNER_ORDERS = FXCollections.observableArrayList();
    private final BoxBlur effect = new BoxBlur(5, 5, 1);
    // ================================
    @FXML
    private JFXTreeTableView<InnerOrder> TableOrder_Table;
    @FXML
    private TreeTableColumn<InnerOrder, Double> total_Col;
    @FXML
    private TreeTableColumn<InnerOrder, Double> price_Col;
    @FXML
    private TreeTableColumn<InnerOrder, Double> amount_Col;
    @FXML
    private TreeTableColumn<InnerOrder, String> productName_Col;
    @FXML
    private Label TableNumber_Label;
    @FXML
    private FlowPane categoryFlowPane;
    @FXML
    private Label totalLabel;
    @FXML
    private Label productsNumberLabel;
    @FXML
    private JFXTextField customerNameTextField;
    public static JFXTextField customerNameTextField_Static;
    @FXML
    private HBox customerContainer;
    @FXML
    private JFXButton transferByn;
    @FXML
    public AnchorPane AP;
    @FXML
    private HBox customerTypeContainer;
    @FXML
    private ToggleGroup customerCategToggleGroup;
    @FXML
    private JFXRadioButton customerRadioBtn;
    @FXML
    private HBox footerContainer;
    @FXML
    private JFXRadioButton ownerRadioBtn;
    @FXML
    private JFXRadioButton staffRadioBtn;
    @FXML
    private HBox editProductBox;
    @FXML
    private TextField amountTextFiled;
    @FXML
    private FlowPane productsFlowPane;
    @FXML
    private HBox barcodeBox;
    @FXML
    private TextField barocdeTextField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableConfiguration();
        TableNumber_Label_Static = TableNumber_Label;
        customerNameTextField_Static = customerNameTextField;
        initCategoryButtons();
        initAmountTextFiled();
        ((VBox) editProductBox.getParent()).getChildren().remove(editProductBox);
        TableOrder_Table.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<InnerOrder>> observable, TreeItem<InnerOrder> oldValue, TreeItem<InnerOrder> newValue) -> {
            if (newValue != null) {
                amountTextFiled.setText(String.valueOf((int) newValue.getValue().getAmount()));
                if (!((VBox) TableOrder_Table.getParent()).getChildren().contains(editProductBox)) {
                    ((VBox) TableOrder_Table.getParent()).getChildren().add(1, editProductBox);
                }
            } else {
                amountTextFiled.setText("0");
                ((VBox) editProductBox.getParent()).getChildren().remove(editProductBox);
            }
        });
        if (!ControlPanel.getInstance().RESTAURANT_TABLE_TRANSFER) {
            ((HBox) transferByn.getParent()).getChildren().remove(transferByn);
        }
        if (!ControlPanel.getInstance().BARCODE_OPTION) {
            ((VBox) barcodeBox.getParent()).getChildren().remove(barcodeBox);
            barcodeBox = null;
        }
        if (ControlPanel.getInstance().CUSTOMER_DATA) {
            footerContainer.getChildren().remove(customerTypeContainer);
        } else {
            footerContainer.getChildren().remove(customerContainer);
            //        Customer Category Radio Buttons Listener
            customerCategToggleGroup.selectedToggleProperty().addListener((observable) -> {
                String selectedType = ((RadioButton) customerCategToggleGroup.getSelectedToggle()).getText();
                switch (selectedType) {
                    case "موظف":
                        INNER_ORDERS.forEach(innerObj -> {
                            innerObj.getProduct().setCustormerCateg("staff");
                            innerObj.setTotal(innerObj.getAmount() * innerObj.getProduct().getPrice());
                        });
                        CheckOutBillController.currentCustomer = new Customer(Customer.CustomerType.Staff);
                        setTempTableCustomer("staff");
                        break;
                    case "عميل":
                        INNER_ORDERS.forEach(innerObj -> {
                            innerObj.getProduct().setCustormerCateg("guest");
                            innerObj.setTotal(innerObj.getAmount() * innerObj.getProduct().getPrice());
                        });
                        CheckOutBillController.currentCustomer = new Customer(Customer.CustomerType.Guest);
                        setTempTableCustomer("guest");
                        break;
                    default:
                        INNER_ORDERS.forEach(innerObj -> {
                            innerObj.getProduct().setCustormerCateg("owner");
                            innerObj.setTotal(innerObj.getAmount() * innerObj.getProduct().getPrice());
                        });
                        CheckOutBillController.currentCustomer = new Customer(Customer.CustomerType.Owner);
                        setTempTableCustomer("owner");
                        break;
                }
                ObservableList<InnerOrder> copyList = FXCollections.observableArrayList(INNER_ORDERS);
                INNER_ORDERS.setAll(copyList);
                resetAdditionalData();
            });
        }

        // Open Stage with relative dimensions to the Screen
        util.gui.HelperMethods.setRelativeSize(AP, 0.75, 1000, 550);
        ScrollHandler.init(categoryFlowPane);
        ScrollHandler.init(productsFlowPane);
    }

    private int handleChangeInTextField(String newValue) {
        // handle wrong inputs
        try {
            lastValue_ProductAmount = Integer.parseInt(newValue);
        } catch (NumberFormatException ex) {
            System.out.println(ex.getLocalizedMessage());
            amountTextFiled.setText(lastValue_ProductAmount + "");
        }
        return lastValue_ProductAmount;
    }

    private void initAmountTextFiled() {
        amountTextFiled.setOnKeyReleased((event) -> {
            handleChangeInTextField(amountTextFiled.getText());
            // handle confirm action
            if (event.getCode().equals(KeyCode.ENTER)) {
                TreeItem<InnerOrder> treeItem = getSelected();
                if (treeItem != null) {
                    int newAmount = Integer.parseInt(amountTextFiled.getText());
                    if (treeItem.getValue().getAmount() != newAmount) {
                        modifyInnerOrder(treeItem.getValue(), (int) (newAmount - (int) treeItem.getValue().getAmount()));
                        TableOrder_Table.getSelectionModel().clearSelection();
                    }
                }
            }
        });
    }

    private void tableConfiguration() {
        productName_Col.setCellValueFactory(param
                -> new SimpleObjectProperty<>(param.getValue().getValue().getProduct().getName()));
        amount_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));
        price_Col.setCellValueFactory(param
                -> new SimpleObjectProperty<>(param.getValue().getValue().getProduct().getPrice()));
        total_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("total"));

        TreeItem<InnerOrder> root = new RecursiveTreeItem<>(INNER_ORDERS, RecursiveTreeObject::getChildren);
        TableOrder_Table.setRoot(root);
        TableOrder_Table.setShowRoot(false);
    }

    public void initCategoryButtons() {
        categoryFlowPane.getChildren().clear();
        CategoryButton categoryButton;
        for (String category : CategoryButton.ALL_CATEGORIES) {
            categoryButton = new CategoryButton(category);
            categoryButton.setOnAction(openCategory(categoryButton));
            categoryFlowPane.getChildren().add(categoryButton);
        }
        if (!categoryFlowPane.getChildren().isEmpty()) {
            focusedButton = (CategoryButton) categoryFlowPane.getChildren().get(0);
            focusedButton.setFocus();
            setProductsOfSelectedCategory(focusedButton.getText());
        } else {
            productsFlowPane.getChildren().clear();
        }
    }

    private EventHandler<ActionEvent> openCategory(CategoryButton b) {
        return (ActionEvent e) -> {
            try {
                focusedButton.delFocus();
                b.setFocus();
                focusedButton = b;
                setProductsOfSelectedCategory(focusedButton.getText());
            } catch (Exception ex) {
                Logger.writeLog("Exception in :" + getClass().getName() + ".openCategory() :- " + ex);
            }
        };
    }

    private void setProductsOfSelectedCategory(String CategoryName) {
        productFocused_ObservableList.clear();
        ALL_PRODUCTS.forEach((product) -> {
            if (product.getCategory().equals(CategoryName)) {
                productFocused_ObservableList.add(product);
            }
        });

        // init Product Buttons 
        productsFlowPane.getChildren().clear();
        for (Product product : productFocused_ObservableList) {
            ProductButton productButton = new ProductButton(product);

            if (ControlPanel.getInstance().NUMNBER_DIALOG_NORMAL) {
                productButton.setOnAction((event) -> {
                    AP.setEffect(effect);
                    showNumberDialog(productButton);
                    AP.setEffect(null);
                });
            } else {
                if (ControlPanel.getInstance().NUMNBER_DIALOG_LONGPRESS) {
                    productButton.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {

                        Timeline longPressTimeLine = new Timeline(new KeyFrame(Duration.millis(750), e -> {
                            Platform.runLater(() -> {
                                AP.setEffect(effect);
                                showNumberDialog(productButton);
                                AP.setEffect(null);
                                isUIButtonReleased = true;
                                Event.fireEvent(productButton, new MouseEvent(MouseEvent.MOUSE_RELEASED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
                            });
                        }));

                        @Override
                        public void handle(MouseEvent event) {
                            if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                                longPressTimeLine.setCycleCount(1);
                                longPressTimeLine.play();
                            } else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                                longPressTimeLine.stop();
                                if (isUIButtonReleased) {
                                    isUIButtonReleased = false;
                                } else {
                                    addProduct(productButton.getProduct(), 1);
                                }
                            }
                        }
                    });
                } else {
                    productButton.setOnAction((event) -> {
                        addProduct(productButton.getProduct(), 1);
                    });
                }
            }
            productsFlowPane.getChildren().add(productButton);
        }
    }

    private void showNumberDialog(ProductButton p) {
        try {
            if (p != null) {
                // TODO make load in LoaderHelper class and make stage, controller static
                // show number dialog
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Location.getInstance().get("NUMBER_DIALOG")));
                Stage stage = BuilderUI.initStageTransparent(new Scene(fxmlLoader.load()), p.getProduct().getName(), "");
                NumberDialogController controller = (NumberDialogController) fxmlLoader.getController();
                controller.setData(p.getProduct().getName());
                Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
                stage.setX(bounds.getWidth() / 2 - (600 / 2));
                stage.setY(bounds.getHeight() / 2 - (400 / 2));
                stage.showAndWait();
                if (controller.isConfirmed && p.getProduct() != null) {
                    addProduct(p.getProduct(), controller.lastValue_ProductAmount);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.writeLog("Exception in :" + getClass().getName() + ".showNumberDialog() :- " + ex);
        }
    }

    private boolean addProduct(Product product, int amount) {
        if (product != null) {
            try {
                TableOrder_Table.getSelectionModel().clearSelection();
                InnerOrder order = new InnerOrder(product, amount, amount * product.getPrice());
                boolean orderedBefore = false;
                for (InnerOrder innerOrder : INNER_ORDERS) {
                    // if product is ordered before
                    if (innerOrder.getProduct().getName().equals(order.getProduct().getName())) {
                        modifyInnerOrder(innerOrder, amount);
                        orderedBefore = true;
//                        StorageController.pullFromORPushTOStore(innerOrder.getProduct().getName(), innerOrder.getProduct().getArguments(), amount, "pull");

//                        innerOrder.delete.setOnAction((e) -> {
//                            // return to storage
//                            // remove from table "INNER_ORDERS"
//                            StorageController.pullFromORPushTOStore(innerOrder.getProduct().getName(), innerOrder.getProduct().getArguments(), (int) (innerOrder.getAmount() + amount), "push");
//                            product.remove();
//                            INNER_ORDERS.remove(order);
//                            resetAdditionalData();
//                        });
                        break;
                    }
                }
                // product is not ordered before
                if (!orderedBefore) {
                    if (!showIfAbleToBuy(order.getProduct().getName(), order.getProduct().getArguments(), amount)) {
                        return false;
                    }

                    // IDK WHAT IS THIS!
                    //                 ... Create Map ...
                    ArrayList<DBField> fieldsMap = new ArrayList<>();
                    fieldsMap.add(new DBField(TableNumber_Label_Static.getText().trim(), "tableName", "PK"));
                    fieldsMap.add(new DBField(product.getName(), "drinkName", "PK"));
                    fieldsMap.add(new DBField(amount, "numofdr", "NN"));
                    fieldsMap.add(new DBField(product.getGuestPrice(), "guestPrice", "NN"));
                    fieldsMap.add(new DBField(product.getStaffPrice(), "staffPrice", "NN"));
                    fieldsMap.add(new DBField(focusedButton.getText(), "productCateg", "NN"));
                    fieldsMap.add(new DBField(NewProductController.convertArgsListToString(product.getArguments()), "productArgs", "NN"));
                    product.setFields(fieldsMap);
                    product.add();

                    StorageController.pullFromORPushTOStore(product.getName(), product.getArguments(), amount, "pull");

                    // #EHAB#
//                    order.delete.setOnAction((e) -> {
//                        // return to storage
//                        // remove from table "INNER_ORDERS"
//                        INNER_ORDERS.remove(order);
//                        resetAdditionalData();
//                    });
                    INNER_ORDERS.add(order);
                    TableOrder_Table.refresh();
                    // init additional data
                    resetAdditionalData();

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.writeLog("Exception in :" + getClass().getName() + ".addProduct() :- " + ex);
            }

        }
        return true;
    }

    private void modifyInnerOrder(InnerOrder innerOrder, int amount) {
        System.out.println("InnerOrder: " + innerOrder);
        System.out.println("------------------------------");
        System.out.println("amount: " + amount);
        System.out.println("innerOrder.getAmount()  " + innerOrder.getAmount());
        if (!showIfAbleToBuy(innerOrder.getProduct().getName(), innerOrder.getProduct().getArguments(), (int) amount)) {
            return;
        }

        innerOrder.setAmount(innerOrder.getAmount() + amount);
        innerOrder.setTotal(innerOrder.getAmount() * innerOrder.getProductPrice());

        if (amount > 0) {
            StorageController.pullFromORPushTOStore(innerOrder.getProduct().getName(), innerOrder.getProduct().getArguments(), amount, "pull");
        } else if (amount < 0) {
            StorageController.pullFromORPushTOStore(innerOrder.getProduct().getName(), innerOrder.getProduct().getArguments(), (-1 * amount), "push");
        }
        if (innerOrder.getAmount() == 0) {
            innerOrder.getProduct().remove();
            INNER_ORDERS.remove(innerOrder);
        } else {
            /* Update DB Num */
            Product product = innerOrder.getProduct();
            ArrayList<DBField> fieldsMap = new ArrayList<>();
            fieldsMap.add(new DBField(TableNumber_Label_Static.getText().trim(), "tableName", "PK"));
            fieldsMap.add(new DBField(product.getName(), "drinkName", "PK"));
            fieldsMap.add(new DBField(innerOrder.getAmount(), "numofdr", "NN"));
            fieldsMap.add(new DBField(product.getGuestPrice(), "guestPrice", "NN"));
            fieldsMap.add(new DBField(product.getStaffPrice(), "staffPrice", "NN"));
            fieldsMap.add(new DBField(focusedButton.getText(), "productCateg", "NN"));
            fieldsMap.add(new DBField(NewProductController.convertArgsListToString(product.getArguments()), "productArgs", "NN"));
            product.setFields(fieldsMap);
            product.update();
        }
        TableOrder_Table.refresh();
        resetAdditionalData();
    }

    private boolean showIfAbleToBuy(String pName, ObservableList<ArgumentsClass> argsList, int num) {
        int ableToBuyFlag = StorageController.ableToBuy(pName, argsList, num);
        if (ableToBuyFlag == -5) {
            DialogHelper.getInstance().showOKAlert("هذا العدد غير متوافر!");
            return false;
        }
        return true;
    }

    public void resetAdditionalData() {
        productsNumberLabel.setText(String.valueOf(INNER_ORDERS.size()));
        double sum = INNER_ORDERS.stream().mapToDouble(InnerOrder::getTotal).sum();
        totalLabel.setText(HelperMethods.formatNum(sum) + ControlPanel.getInstance().getCurrency());

    }

    @FXML
    private void MoveAction(Event event) {
        if (!INNER_ORDERS.isEmpty()) {
            if (DialogHelper.getInstance().openTrasferTableOrder(TableNumber_Label.getText()) == 1) {
                ((Stage) TableNumber_Label_Static.getScene().getWindow()).close();
            }
        }
    }

    @FXML
    private void leaveAction() {
        if (!INNER_ORDERS.isEmpty()) {
            CheckOutBillController.setOrderKind(OrderKind.Hall);
            try {
                double additions = Order.getTaxAndServantPercentage("restaurant") * Double.parseDouble(totalLabel.getText().replace(ControlPanel.getInstance().getCurrency(), "").trim());
                // TODO why there is an Object of Payment here and not used!
                Payment payment = DialogHelper.getInstance().openCheckOutBill(Double.parseDouble(totalLabel.getText().replace(ControlPanel.getInstance().getCurrency(), "").trim()) + additions, null);
                if (CheckOutBillController.flag == 1) {
                    INNER_ORDERS.clear();
                    resetAdditionalData();
                    closeWindow();
                    // create order with 1-payment and 2-innerOrderTable data 3-Customer Data
                    // CODE
                }
            } catch (Exception ex) {
                Logger.writeLog("Exception in :" + getClass().getName() + ".leaveAction() :- " + ex);
            }
        }
    }

    @FXML
    private void closeWindow() {
        closeWindowAction();
    }

    @FXML
    private void chooseCustomer() {
        Customer chooseCustomer = DialogHelper.getInstance().chooseCustomer();
        if (chooseCustomer != null) {
            Product.setCustormerCateg(chooseCustomer.getCustomerType().toString());
            INNER_ORDERS.forEach(innerObj -> {
                innerObj.setTotal(innerObj.getAmount() * innerObj.getProduct().getPrice());
            });
            ObservableList<InnerOrder> copyList = FXCollections.observableArrayList(INNER_ORDERS);
            INNER_ORDERS.setAll(copyList);
            resetAdditionalData();
            customerNameTextField.setText(chooseCustomer.getFullName());
            CheckOutBillController.currentCustomer = chooseCustomer;
            setTempTableCustomer(chooseCustomer.getFullName());
        } else {
            Product.setCustormerCateg("guest");
            INNER_ORDERS.forEach(innerObj -> {
                innerObj.setTotal(innerObj.getAmount() * innerObj.getProduct().getPrice());
            });
            ObservableList<InnerOrder> copyList = FXCollections.observableArrayList(INNER_ORDERS);
            INNER_ORDERS.setAll(copyList);
            resetAdditionalData();
            customerNameTextField.setText("");
            setTempTableCustomer(null);
            CheckOutBillController.currentCustomer = null;
        }
    }

    public void closeWindowAction() {
        int flag = 1;
        if (INNER_ORDERS.isEmpty()) {
            flag = 0;
            try {
                DatabaseHandler.con.prepareStatement("DELETE FROM tempTablesCustomers WHERE tableName='" + TableNumber_Label_Static.getText().trim() + "'").execute();
            } catch (Exception e) {
                Logger.writeLog("Exception in TableOrderController -> closeWindowAction() :- " + e);
            }
        }
        HallController.changeTableState(TableNumber_Label_Static.getText(), flag);
        ((Stage) TableNumber_Label_Static.getScene().getWindow()).close();
    }

    public static void setTempTableCustomer(String fullName) {
        try {
            ArrayList<DBField> fieldsMap = new ArrayList<>();
            fieldsMap.add(new DBField(TableNumber_Label_Static.getText(), "tableName", "PK"));
            TempCustomer customer = new TempCustomer();
            if (fullName != null) {
                fieldsMap.add(new DBField(fullName, "customerName", "NN"));
                customer.setFields(fieldsMap);
                customer.add();
                customer.update();
            } else {
                customer.setFields(fieldsMap);
                customer.remove();
            }

        } catch (Exception e) {
            Logger.writeLog("Exception in TableOrderController -> setTempTableCustomer() :- " + e);
        }
    }

    public void checkTempCustomer() {
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT * FROM tempTablesCustomers WHERE tableName='" + TableNumber_Label_Static.getText().trim() + "'").executeQuery();
            String tempName;
            while (res.next()) {
                tempName = res.getString(2);
                TableOrderController.customerNameTextField_Static.setText(tempName);

                CheckOutBillController.currentCustomer = Customer.customersData.stream().filter(cust -> cust.getFullName().equals(tempName)).findAny().orElse(null);

                String customerCateg
                        = CheckOutBillController.currentCustomer == null
                                ? tempName.equalsIgnoreCase("guest") || tempName.equalsIgnoreCase("staff") || tempName.equalsIgnoreCase("owner")
                                ? tempName : "guest"
                                : CheckOutBillController.currentCustomer.getCustomerType().toString();
                switch (customerCateg) {
                    case "guest":
                        customerRadioBtn.setSelected(true);
                        break;
                    case "staff":
                        staffRadioBtn.setSelected(true);
                        break;
                    default:
                        ownerRadioBtn.setSelected(true);
                        break;
                }
                if (ControlPanel.getInstance().CUSTOMER_DATA) {
                    Product.setCustormerCateg(customerCateg);
                    INNER_ORDERS.forEach(innerObj -> {
                        innerObj.setTotal(innerObj.getAmount() * innerObj.getProduct().getPrice());
                    });
                    ObservableList<InnerOrder> copyList = FXCollections.observableArrayList(INNER_ORDERS);
                    INNER_ORDERS.setAll(copyList);
                }
                return;
            }
            TableOrderController.customerNameTextField_Static.setText("");
            CheckOutBillController.currentCustomer = null;
        } catch (Exception e) {
            Logger.writeLog("Exception in TableOrderController -> checkTempCustomer() :- " + e);
        }
    }
//    Over loading

    public String checkTempCustomer(String tableName) {
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT * FROM tempTablesCustomers WHERE tableName='" + tableName + "'").executeQuery();
            while (res.next()) {
                return res.getString(2);
            }
            TableOrderController.customerNameTextField_Static.setText("");
        } catch (Exception e) {
            Logger.writeLog("Exception in TableOrderController -> checkTempCustomer(String) :- " + e);
        }
        return "";
    }

    private TreeItem<InnerOrder> getSelected() {
        TreeItem<InnerOrder> selectedItem = TableOrder_Table.getSelectionModel().getSelectedItem();
        return selectedItem;
    }

    @FXML
    private void plusProduct() {
        TreeItem<InnerOrder> treeItem = getSelected();
        if (treeItem != null) {
            amountTextFiled.setText(handleChangeInTextField(String.valueOf((int) treeItem.getValue().getAmount() + 1)) + "");
            modifyInnerOrder(treeItem.getValue(), 1);
        }
    }

    @FXML
    private void minusProduct() {
        TreeItem<InnerOrder> treeItem = getSelected();
        if (treeItem.getValue().getAmount() == 1) {
            return;
        }
        if (treeItem != null) {
            amountTextFiled.setText(handleChangeInTextField(treeItem.getValue().getAmount() > 1 ? String.valueOf((int) treeItem.getValue().getAmount() - 1) : "0") + "");
            modifyInnerOrder(treeItem.getValue(), -1);
        }
    }

    @FXML
    private void delProduct() {
        TreeItem<InnerOrder> treeItem = getSelected();
        if (treeItem != null) {
            amountTextFiled.setText("0");
            modifyInnerOrder(treeItem.getValue(), (int) (-1 * treeItem.getValue().getAmount()));
//            INNER_ORDERS.remove(treeItem.getValue());
            TableOrder_Table.getSelectionModel().clearSelection();
//            TableOrder_Table.refresh();
//            resetAdditionalData();
        }
    }

    @FXML
    private void barcodeAction() {
        Product product = ALL_PRODUCTS.stream().filter(pro -> pro.haveBarCode() && pro.getBarCode().equalsIgnoreCase(barocdeTextField.getText().trim())).findAny().orElse(null);
        if (product != null) {
            boolean result = addProduct(product, 1);
            if (result) {
                barocdeTextField.setText("");
            }
        } else {
            DialogHelper.getInstance().showOKAlert("هذا الكود غير موجود!");
        }
    }

}

/// Class TO Add Temp Customer
class TempCustomer extends DBCRUD<TempCustomer> {

    public TempCustomer() {
        super("tempTablesCustomers");
    }

    @Override
    public ArrayList<DBField> createMap() {
        return null;
    }

}
