package dialog.checkout;

import controlpanel.ControlPanel;
import datamodel.CafeRecord;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import datamodel.Customer;
import datamodel.InnerOrder;
import datamodel.Order;
import datamodel.Order.OrderKind;
import datamodel.Payment;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import static restaurant.dayorders.DayOrdersController.orderObservableList;
import restaurant.hall.TableOrderController;
import static restaurant.hall.TableOrderController.TableNumber_Label_Static;
import restaurant.takeaway.TakeAwayController;
import util.Logger;
import util.db.DBCRUD;
import util.db.DBField;
import util.db.Methods;
import util.gui.HelperMethods;
import datamodel.User;
import datamodel.settings.Preferences;
import datamodel.settings.PreferencesType;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import restaurant.RestaurantHomeController;
import storage.StorageController;
import static storage.StorageController.insertDataIntoDailyStorePullData;
import util.db.DatabaseHandler;
import util.gui.load.DialogHelper;
import util.printing.CashDrawer;
import util.printing.PrintIngController;

public class CheckOutBillController implements Initializable {

    public static int flag;
    private static OrderKind currentKind;
    private static HBox buttonsBoxStatic;
    private static JFXCheckBox printCheckBoxStatic;

    // ================ GUI ================
    private double yOffset = 0;
    private double xOffset = 0;
    @FXML
    private Label totalOrderLabel;
    @FXML
    private TextField discountTxt;
    @FXML
    private Label totalOrderLabelAfterDis;
    @FXML
    private TextField PaidOrderTexfield;
    @FXML
    private Label remainTName;
    @FXML
    private Label remainOrderLabel;
    @FXML
    private JFXCheckBox printCheckBox;
    @FXML
    private HBox buttonsBox;

    public static Customer currentCustomer;
    @FXML
    private JFXButton discardBTN;
    @FXML
    private JFXButton confirmBTN;
    @FXML
    private GridPane StackPaneContainer;
    @FXML
    private StackPane deptLabel;
    @FXML
    private StackPane deptLabelValue;

    public void setData(double total) {
        //        ---- Reset Discount And Paid ----
        discountTxt.setText("");
        PaidOrderTexfield.setText(HelperMethods.formatNum(total) + "");

        totalOrderLabel.setText(HelperMethods.formatNum(total) + "");
        totalOrderLabelAfterDis.setText(HelperMethods.formatNum(total) + "");
        remainOrderLabel.setText("0");
        printCheckBox.setSelected(true);

        confirmBTN.setDisable(false);
        printCheckBox.setDisable(false);
        discountTxt.setEditable(true);
        PaidOrderTexfield.setEditable(true);
        PaidOrderTexfield.requestFocus();
    }

    public void setPaymentDetails(Payment p) {
        totalOrderLabel.setText(String.valueOf(HelperMethods.formatNum(p.getTotal())));
        discountTxt.setText(String.valueOf(HelperMethods.formatNum(p.getDiscount())));
        discountTxt.setEditable(false);
        totalOrderLabelAfterDis.setText(String.valueOf(HelperMethods.formatNum(p.getRequired())));
        PaidOrderTexfield.setText(String.valueOf(HelperMethods.formatNum(p.getPaid())));
        PaidOrderTexfield.setEditable(false);
        remainOrderLabel.setText(String.valueOf(HelperMethods.formatNum(p.getDebt())));
        confirmBTN.setDisable(true);
        printCheckBox.setDisable(true);
        PaidOrderTexfield.requestFocus();
    }

    public Payment getData() {
        double total, discount, required, paid, debt;
        try {
            total = Double.parseDouble(totalOrderLabel.getText());
        } catch (NumberFormatException e) {
            total = 0;
        }
        try {
            discount = Double.parseDouble(discountTxt.getText());
        } catch (NumberFormatException e) {
            discount = 0;
        }
        try {
            required = Double.parseDouble(totalOrderLabelAfterDis.getText());
        } catch (NumberFormatException e) {
            required = 0;
        }
        try {
            paid = Double.parseDouble(PaidOrderTexfield.getText());
        } catch (NumberFormatException e) {
            paid = 0;
        }
        try {
            debt = Double.parseDouble(remainOrderLabel.getText());
        } catch (NumberFormatException e) {
            debt = 0;
        }
        return new Payment(HelperMethods.formatNum(total), HelperMethods.formatNum(discount), HelperMethods.formatNum(required), HelperMethods.formatNum(paid), HelperMethods.formatNum(debt));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            printCheckBoxStatic = printCheckBox;
            buttonsBoxStatic = buttonsBox;
            flag = 0;
            if (!Preferences.getInstance().getBoolean(PreferencesType.Print, "true")) {
                removePrintCheckBox();
            }
            if (!ControlPanel.getInstance().CUSTOMER_DATA) {
                StackPaneContainer.getChildren().remove(deptLabel);
                StackPaneContainer.getChildren().remove(deptLabelValue);
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + ".initialize() => " + ex);
        }
    }

    private static void removePrintCheckBox() {
        if (buttonsBoxStatic.getChildren().contains(printCheckBoxStatic)) {
            buttonsBoxStatic.getChildren().remove(printCheckBoxStatic);
        }
    }

    private static void addPrintCheckBox() {
        if (!buttonsBoxStatic.getChildren().contains(printCheckBoxStatic)) {
            buttonsBoxStatic.getChildren().add(2, printCheckBoxStatic);
        }
    }

    private static void setPrintCheckBoxVisable(boolean isVisable) {
        if (isVisable) {
            addPrintCheckBox();
        } else {
            removePrintCheckBox();
        }
    }

    private boolean isPrintCheckBoxVisable() {
        return buttonsBoxStatic.getChildren().contains(printCheckBoxStatic);
    }

    @FXML
    private void discount_Handler(KeyEvent event) {
        double total = 0;
        double paidAmount = 0;
        try {
            total = Double.parseDouble(totalOrderLabel.getText().replace(ControlPanel.getInstance().getCurrency(), "").trim());
            paidAmount = PaidOrderTexfield.getText().replace(ControlPanel.getInstance().getCurrency(), "").trim().equals("") ? 0
                    : Double.parseDouble(PaidOrderTexfield.getText().replace(ControlPanel.getInstance().getCurrency(), "").trim());
            double discount = Double.parseDouble(discountTxt.getText().replace(ControlPanel.getInstance().getCurrency(), "").trim());
            if (discount >= 0 && discount < total) {
                totalOrderLabelAfterDis.setText(String.valueOf(HelperMethods.formatNum(total - discount)));
                remainOrderLabel.setText(String.valueOf(HelperMethods.formatNum(total - discount - paidAmount)));
                return;
            } else if (discount >= 0) {
                DialogHelper.getInstance().showOKAlert("قيمة الخصم اكبر من اجمالي الحساب");
            }
            throw new NumberFormatException();
        } catch (NumberFormatException e) {
            totalOrderLabelAfterDis.setText(String.valueOf(HelperMethods.formatNum(total)));
            remainOrderLabel.setText(String.valueOf(HelperMethods.formatNum(total - paidAmount)));
            discountTxt.setText("");
        }
    }

    @FXML
    private void payed_Handler(KeyEvent event) {
        double requiredAmount = 0;
        try {
            requiredAmount = Double.parseDouble(totalOrderLabelAfterDis.getText().replace(ControlPanel.getInstance().getCurrency(), "").trim());
            double paidAmount = Double.parseDouble(PaidOrderTexfield.getText().replace(ControlPanel.getInstance().getCurrency(), "").trim());
            if (paidAmount >= 0 && requiredAmount >= paidAmount) {
                remainOrderLabel.setText(String.valueOf(HelperMethods.formatNum(requiredAmount - paidAmount)));
                return;
            } else if (paidAmount >= 0) {
                DialogHelper.getInstance().showOKAlert("قيمة المدفوع اكبر من اجمالي المطلوب");
            }
            throw new NumberFormatException();
        } catch (NumberFormatException e) {
            PaidOrderTexfield.setText("");
            remainOrderLabel.setText(String.valueOf(HelperMethods.formatNum(requiredAmount)));
        }
    }

    @FXML
    private void discardAction(Event event) {
        flag = 0;
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void confirmAction(Event event) {
        try {
//            if (!customerNameTextField_Static.getText().trim().equals("") && currentKind.equals(OrderKind.Hall)) {
//                customer = new Customer(customerNameTextField_Static.getText().trim());
//            } else if (!TakeAwayController.customerNameTextField_Static.getText().trim().equals("")) {
//                customer = new Customer(TakeAwayController.customerNameTextField_Static.getText().trim());
//            }
            double afterDiscount = totalOrderLabelAfterDis.getText().replace(ControlPanel.getInstance().getCurrency(), "").trim().equals("") ? 0
                    : Double.parseDouble(totalOrderLabelAfterDis.getText().replace(ControlPanel.getInstance().getCurrency(), "").trim());
            double paidAmount = PaidOrderTexfield.getText().replace(ControlPanel.getInstance().getCurrency(), "").trim().equals("") ? 0
                    : Double.parseDouble(PaidOrderTexfield.getText().replace(ControlPanel.getInstance().getCurrency(), "").trim());

            if (currentCustomer == null && afterDiscount > paidAmount) {
                DialogHelper.getInstance().showOKAlert(ControlPanel.getInstance().CUSTOMER_DATA ? "يجب ادخال اسم عميل او دفع المبلغ كامل" : "يجب دفع المبلغ كامل");
                return;
            } else if (afterDiscount < paidAmount) {
                DialogHelper.getInstance().showOKAlert("قيمة المدفوع اكبر من القيمة المطلوبة");
                return;
            }

            double taxAndServePercentage = Order.getTaxAndServantPercentage("restaurant");
            double taxAndServe;
            switch (currentKind) {
                case Hall:
                    taxAndServe = (taxAndServePercentage * TableOrderController.INNER_ORDERS.stream().mapToDouble(InnerOrder::getTotal).sum());
                    break;
                case Bar:
                    taxAndServe = (taxAndServePercentage * TakeAwayController.INNER_ORDERS.stream().mapToDouble(InnerOrder::getTotal).sum());
                    break;
                default:
                    taxAndServe = 0;
                    break;
            }

            double discount = discountTxt.getText().replace(ControlPanel.getInstance().getCurrency(), "").trim().equals("") ? 0
                    : Double.parseDouble(discountTxt.getText().replace(ControlPanel.getInstance().getCurrency(), "").trim());

//            Add Order Obj TO resturantReceets
            int receetId = (int) Methods.GetMaximum("receetId", "resturantReceets");
            String tableName = currentKind.equals(OrderKind.Hall) ? TableNumber_Label_Static.getText().trim() : "";
            Order order = new Order(
                    receetId,
                    Methods.getDateAndTime(),
                    currentKind,
                    discount,
                    paidAmount,
                    taxAndServe,
                    User.CurrentUser,
                    tableName,
                    currentCustomer
            );

            int additionflag = 0;
            if (currentKind.equals(OrderKind.Bar) && currentCustomer != null) {
                try {
                    final String custoemrName = currentCustomer.getFullName();
                    Order prevOrder = orderObservableList.stream()
                            .filter(obj
                                    -> obj.getCustomer() != null
                            && !obj.getCustomer().getFullName().equals("لا يوجد")
                            && obj.getCustomer().getFullName().equals(custoemrName)
                            && obj.getOrderKind().equals(OrderKind.Bar.toString())
                            && obj.getStatusIcon().isSuccess()).findAny().orElse(null);

                    if (prevOrder != null) {
                        receetId = Integer.parseInt(prevOrder.getBillNumber().getText());
                        order.setBillNumber(new JFXButton(receetId + ""));
                        order.setDiscountAmount(prevOrder.getDiscountAmount() + discount);
                        order.setPaidAmount(prevOrder.getPaidAmount() + paidAmount);
                        double updatePrevAdditions = HelperMethods.formatNum((prevOrder.getSubTotalAmount() - prevOrder.getTaxANDServ()) * taxAndServePercentage);

                        order.setTax_AND_Serv(HelperMethods.formatNum(updatePrevAdditions + taxAndServe));
                        order.setFields(order.createMap());
                        order.update();
                        additionflag = 1;
                        setTaxAndServ(receetId, Order.getTax("restaurant"), Order.getServant("restaurant"), false);
                    }

                } catch (Exception e) {
                    Logger.writeLog("EXCEPTION IN ADD TO PREVRECCET LINE 246 'CheckOutBillController' " + e
                    );
                }
            }
            if (additionflag == 0) {
                order.add();
                setTaxAndServ(receetId, Order.getTax("restaurant"), Order.getServant("restaurant"), true);
            }

            if (currentKind.equals(OrderKind.Hall)) {
                // Add Order Details 
                InnerOrder.setReceetDetails(receetId, TableOrderController.INNER_ORDERS);
                // Remove From TempProduct table 
                DatabaseHandler.con.prepareStatement("DELETE FROM dailycafesheet WHERE tableName='" + TableNumber_Label_Static.getText().trim() + "'").execute();
                // Remove Temp Customer 
                DatabaseHandler.con.prepareStatement("DELETE FROM tempTablesCustomers WHERE tableName='" + TableNumber_Label_Static.getText().trim() + "'").execute();

                addToDailyPull(receetId, TableOrderController.INNER_ORDERS);
            } else {
                // Pull the arguments of the products from the Storage
                TakeAwayController.INNER_ORDERS.forEach(inOrder -> {
                    StorageController.pullFromORPushTOStore(inOrder.getProduct().getName(), inOrder.getProduct().getArguments(), (int) (inOrder.getAmount()),
                            "pull");
                });
                // Add Order Details 
                InnerOrder.setReceetDetails(receetId, TakeAwayController.INNER_ORDERS);

                addToDailyPull(receetId, TakeAwayController.INNER_ORDERS);
            }

            if (Preferences.getInstance().getBoolean(PreferencesType.OpenCashDrawerWithSale, "true")) {
                CashDrawer.openCashDrawer();
            }

            PrintIngController.getInstance().saveReceet(receetId, printCheckBox.isSelected() && Preferences.getInstance().getBoolean(PreferencesType.Print, "true") && isPrintCheckBoxVisable());
            Platform.runLater(() -> {
                Order.fillDailyCafeTable();
            });
            RestaurantHomeController.anyAct = 1;
            flag = 1;
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        } catch (Exception e) {
            Logger.writeLog("Exception in CheckOutBillController -> confirmAction :- " + e);
        }
        currentCustomer = null;
    }

    @FXML
    private void RootMousePressed(Event event) {
        MouseEvent e = (MouseEvent) event;
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    }

    @FXML
    private void RootMouseDragged(Event event) {
        MouseEvent e = (MouseEvent) event;
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setX(e.getScreenX() - xOffset);
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setY(e.getScreenY() - yOffset);
    }

    @FXML
    private void keyEv(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            confirmAction(event);
        } else if (event.getCode().equals(KeyCode.ESCAPE)) {
            discardAction(event);
        }
    }

    public static void setOrderKind(OrderKind cu) {
        currentKind = cu;
        try {
            switch (currentKind) {
                case Hall:
                    setPrintCheckBoxVisable(Preferences.getInstance().getBoolean(PreferencesType.Print_RestaurantHall, "true"));
                    break;
                case Bar:
                    setPrintCheckBoxVisable(Preferences.getInstance().getBoolean(PreferencesType.Print_Bar, "true"));
                    break;
                case TakeAway:
                    setPrintCheckBoxVisable(Preferences.getInstance().getBoolean(PreferencesType.Print_TakeAway, "true"));
                    break;
                case Delivery:
                    setPrintCheckBoxVisable(Preferences.getInstance().getBoolean(PreferencesType.Print_Delivery, "true"));
                    break;
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in CheckOutBillController.setOrderKind() =>  " + e);
        }
    }

    public static void addToDailyPull(int receiptID, ObservableList<InnerOrder> INNER_ORDERS) {
        List<CafeRecord> reformat = INNER_ORDERS.stream().map((order)
                -> new CafeRecord(
                        0,
                        order.getProduct().getName(),
                        (int) order.getAmount(),
                        0,
                        order.getProduct().getCategory(),
                        order.getProduct().getArguments()
                )
        ).collect(Collectors.toList());

        try {
            ObservableList<CafeRecord> l = FXCollections.observableArrayList();
            l.setAll(reformat);
            insertDataIntoDailyStorePullData(receiptID, l);
        } catch (Exception e) {
            Logger.writeLog("Exception in CheckOutBillController -> addToDailyPull:- " + e);
        }
    }

    private void setTaxAndServ(int receetId, double tax, double servant, boolean insert) {
        try {
            DBCRUD temp = new DBCRUD("VAT") {
                @Override
                public ArrayList createMap() {
                    return null;
                }
            };
            ArrayList<DBField> map = new ArrayList<>();
            map.add(new DBField(receetId, "receetId", "PK"));
            map.add(new DBField(tax, "tax", "NN"));
            map.add(new DBField(servant, "serv", "NN"));
            temp.setFields(map);
            if (insert) {
                temp.add();
            } else {
                temp.update();
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in CheckOutBillController -> setTaxAndServ() :- " + e);
        }
    }

}
