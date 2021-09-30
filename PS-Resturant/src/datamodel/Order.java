package datamodel;

import controlpanel.ControlPanel;
import com.jfoenix.controls.JFXButton;
import customerdata.ViewCustomerController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.sql.ResultSet;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleDoubleProperty;
import java.util.ArrayList;
import java.util.Random;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import restaurant.RestaurantHomeController;
import static restaurant.dayorders.DayOrdersController.DayOrdersTablePointer;
import static restaurant.dayorders.DayOrdersController.barSumitionPointer;
import static restaurant.dayorders.DayOrdersController.deliverySumitionPointer;
import static restaurant.dayorders.DayOrdersController.deptSumitionPointer;
import static restaurant.dayorders.DayOrdersController.hallSumitionPointer;
import static restaurant.dayorders.DayOrdersController.openCustomerDetails;
import static restaurant.dayorders.DayOrdersController.orderObservableList;
import static restaurant.dayorders.DayOrdersController.takAwaySumitionPointer;
import static restaurant.dayorders.DayOrdersController.totalPointer;
import static storage.StorageController.pullFromORPushTOStore;
import util.Logger;
import util.db.DBCRUD;
import util.db.DBField;
import util.db.DatabaseHandler;
import util.db.Methods;
import util.gui.HelperMethods;
import util.gui.StatusIcon;
import util.gui.button.TableViewButton;
import util.gui.load.DialogHelper;
import util.gui.load.LoadHelper;

public class Order extends DBCRUD<Order> {

    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
    public static final String DB_TABLENAME = "resturantReceets";
    private String time;
    private String date;
    private JFXButton billNumber; // primary
    private SimpleDoubleProperty subTotalAmount;
    private final SimpleDoubleProperty discountAmount;
    private SimpleDoubleProperty totalAmount;
    private final SimpleDoubleProperty paidAmount;
    private SimpleDoubleProperty debtAmount;
    private SimpleDoubleProperty tax_AND_Serv;
    private ArrayList<ProductOrder> products;
    private String orderKind;
    private String tableName;
    private User cashier;
    private Customer customer;
    private String status;
    private String customerCateg;
    private TableViewButton showCustomerData;
    private JFXButton paidButton;
    private StatusIcon statusIcon;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    // .... Constructors ....
    // ... Creation Constructor ...
    public Order(int billNumber, String date, OrderKind kind, double discountAmount, double paidAmount, double tax_AND_Serv, User cashier, String tableName, Customer customer) {
        super(DB_TABLENAME);
        this.date = date;
        this.billNumber = new JFXButton(String.valueOf(billNumber));
        this.orderKind = kind.toString();
        this.discountAmount = new SimpleDoubleProperty(HelperMethods.formatNum(discountAmount));
        this.paidAmount = new SimpleDoubleProperty(HelperMethods.formatNum(paidAmount));
        this.tax_AND_Serv = new SimpleDoubleProperty(HelperMethods.formatNum(tax_AND_Serv));
        this.cashier = cashier;
        this.tableName = tableName;
        this.customer = customer;
        this.status = "Confirm";
        this.statusIcon = new StatusIcon(StatusIcon.StatusType.Success);
        this.setFields(createMap());
    }

    //    .... Retrive Constuctor ....
    public Order(String time, String date, int billNumber, String orderKind, double subTotalAmount, double discountAmount, double totalAmount, double paidAmount, double debtAmount, String cashier, String customerName, String tableName, String customerCateg, double tax_AND_Serv, String status, boolean visabliltyofPaidButton, boolean ActionOfPaidButtonInCustomerData) {
        super(DB_TABLENAME);
        this.time = LocalTime.parse(time).format(timeFormat);
        this.date = date;
        this.billNumber = new JFXButton(String.valueOf(billNumber));
        this.billNumber.getStyleClass().add("table-btn");
        this.billNumber.setFocusTraversable(false);
        this.billNumber.setOnAction(e -> {
            DialogHelper.getInstance().showBillDetails(this, visabliltyofPaidButton);
        });

        this.tax_AND_Serv = new SimpleDoubleProperty(HelperMethods.formatNum(tax_AND_Serv));
        this.tableName = tableName;
        this.subTotalAmount = new SimpleDoubleProperty(HelperMethods.formatNum(subTotalAmount));
        this.discountAmount = new SimpleDoubleProperty(HelperMethods.formatNum(discountAmount));
        this.totalAmount = new SimpleDoubleProperty(HelperMethods.formatNum(totalAmount));
        this.paidAmount = new SimpleDoubleProperty(HelperMethods.formatNum(paidAmount));
        this.debtAmount = new SimpleDoubleProperty(HelperMethods.formatNum(debtAmount));
        this.status = status;
        this.orderKind = orderKind;
        this.customerCateg = customerCateg;
        this.cashier = new User(cashier);
        Customer tempCustomer = Customer.getDataOFCustomer(customerName);
        if (customerName != null && !customerName.trim().equalsIgnoreCase("") && tempCustomer != null) {
            this.customer = tempCustomer;
        } else {
            this.customer = new Customer("لا يوجد");
        }
        showCustomerData = new TableViewButton(customer.getFullName());
        showCustomerData.setOnAction((event) -> {
            openCustomerDetails(customer);
        });
        this.products = getProductsOFReceet(billNumber);
        if (this.status.equalsIgnoreCase("confirm")) {
            this.statusIcon = new StatusIcon(StatusIcon.StatusType.Success);
        } else {
            this.statusIcon = new StatusIcon(StatusIcon.StatusType.Returned);
        }

        if (ActionOfPaidButtonInCustomerData) {
            paidButton = new TableViewButton("دفع", new FontAwesomeIconView(FontAwesomeIcon.DOLLAR));
            if (debtAmount > 0 && status.equalsIgnoreCase("confirm")) {
                paidButton.setOnAction(event -> {
                    String s = "دفع " + HelperMethods.formatNum(debtAmount) + ControlPanel.getInstance().getCurrency() + " ؟";
                    dialog.alert.confirm.ConfirmAlertController.flag = 0;
                    DialogHelper.getInstance().showConfirmAlert(s);
                    if (dialog.alert.confirm.ConfirmAlertController.flag == 1) {
                        DBCRUD<Order> temp = new DBCRUD<Order>(DB_TABLENAME) {
                            @Override
                            public ArrayList<DBField> createMap() {
                                return null;
                            }
                        };
                        ArrayList<DBField> map = new ArrayList<>();
//                       .... UPDATE Receet Last Column ....
                        map.add(new DBField(billNumber, "receetId", "PK"));
                        map.add(new DBField(debtAmount, "deptPaid", "NN"));
                        temp.setFields(map);
                        temp.update();

//                       .... INSERT INTO DEPT DATA Table....
                        map.clear();
                        temp.setTableName("deptData");
                        map.add(new DBField(billNumber, "receetId", "PK"));
                        map.add(new DBField(customerName, "customerName", "NN"));
                        map.add(new DBField(debtAmount, "price", "NN"));
                        map.add(new DBField(Methods.getDateAndTime(), "date", "NN"));
                        map.add(new DBField(1, "daily", "NN"));
                        temp.setFields(map);
                        temp.add();
                        map.clear();

//                        ... Refetch Data For Outer Table ...
                        if (ControlPanel.getInstance().CUSTOMER_DATA) {
                            Customer.fillData();
                        }

//                        ... Update Details Table ...
                        ViewCustomerController.RESTAURANT_ORDERS.setAll(getOrdersOfCustomer(customerName));

                        LoadHelper.getInstance().viewCustomerController.setAllPaid();
                        LoadHelper.getInstance().viewCustomerController.getDebtSum();
                        LoadHelper.getInstance().viewCustomerController.resetFilters();

//                        .... UPDATE END DAY ....
                        LoadHelper.getInstance().dailySheetController.updateAllData();
                    }
                });
            } else {
                paidButton.setDisable(true);
            }
        }

    }

    public void setOnChangeState(EventHandler<ActionEvent> OnChangeState) {
        statusIcon.setOnAction(OnChangeState);
    }

    public String getCustomerCateg() {
        return customerCateg;
    }

    public void setCustomerCateg(String customerCateg) {
        this.customerCateg = customerCateg;
    }

    public StatusIcon getStatusIcon() {
        return statusIcon;
    }

    public void setStatusIcon(StatusIcon statusIcon) {
        this.statusIcon = statusIcon;
    }

    public JFXButton getPaidButton() {
        return paidButton;
    }

    public void setPaidButton(JFXButton paidButton) {
        this.paidButton = paidButton;
    }

    public void setBillDetailsHandler(EventHandler<ActionEvent> billNumberHandler) {
        this.billNumber.setOnAction(billNumberHandler);
    }

    @Override
    public String toString() {
        return "Order{"
                + "time='" + time + '\''
                + ", date='" + date + '\''
                + ", billNumber='" + billNumber + '\''
                + ", subTotalAmount=" + subTotalAmount.get()
                + ", discountAmount=" + discountAmount.get()
                + ", totalAmount=" + totalAmount.get()
                + ", paidAmount=" + paidAmount.get()
                + ", debtAmount=" + debtAmount.get()
                + ", products=" + products
                + ", customer=" + customer
                + ", cashier=" + cashier
                + '}';
    }

    public String getOrderKind() {
        return orderKind;
    }

    public void setOrderKind(String orderKind) {
        this.orderKind = orderKind;
    }

    private double calcSubTotal() {
        // calculated from Products price
        return 500;
    }

    private String generateBillNumber() {
        return String.valueOf(new Random().nextInt(5000));
    }

    public double getSubTotalAmount() {
        return subTotalAmount.get();
    }

    public void setSubTotalAmount(double subTotalAmount) {
        this.subTotalAmount.set(HelperMethods.formatNum(subTotalAmount));
    }

    public SimpleDoubleProperty subTotalAmountProperty() {
        return subTotalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount.get();
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount.set(HelperMethods.formatNum(discountAmount));
    }

    public SimpleDoubleProperty discountAmountProperty() {
        return discountAmount;
    }

    public double getTotalAmount() {
        return totalAmount.get();
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount.set(HelperMethods.formatNum(totalAmount));
    }

    public SimpleDoubleProperty totalAmountProperty() {
        return totalAmount;
    }

    public double getPaidAmount() {
        if (status.equalsIgnoreCase("Confirm")) {
            return paidAmount.get();
        }
        return 0;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount.set(HelperMethods.formatNum(paidAmount));
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SimpleDoubleProperty paidAmountProperty() {
        return paidAmount;
    }

    public double getDebtAmount() {
        if (status.equalsIgnoreCase("Confirm")) {
            return debtAmount.get();
        }
        return 0;
    }

    public void setDebtAmount(double debtAmount) {
        this.debtAmount.set(HelperMethods.formatNum(debtAmount));
    }

    public SimpleDoubleProperty debtAmountProperty() {
        return debtAmount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public JFXButton getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(JFXButton billNumber) {
        this.billNumber = billNumber;
    }

    public ArrayList<ProductOrder> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<ProductOrder> products) {
        this.products = products;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public User getCashier() {
        return cashier;
    }

    public void setCashier(User cashier) {
        this.cashier = cashier;
    }

    public double getTaxANDServ() {
        return tax_AND_Serv.get();
    }

    public void setTax_AND_Serv(double tax_AND_Serv) {
        this.tax_AND_Serv.set(HelperMethods.formatNum(tax_AND_Serv));
    }

    private ArrayList<ProductOrder> getProductsOFReceet(int billNumber) {
        ArrayList<ProductOrder> receetDetails = new ArrayList<>();
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT * FROM resturantDetails WHERE receetId=" + billNumber).executeQuery();
            Product temp;
            while (res.next()) {
                temp = new Product(res.getString(2), res.getString(5), res.getDouble(4), res.getDouble(4), res.getString(6));

                receetDetails.add(new ProductOrder(temp, res.getDouble(3)));
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in Order -> getProductsOFReceet() :-  " + e);
        }
        return receetDetails;
    }

    public enum OrderKind {
        Hall, TakeAway, Bar, Delivery
    }

    @Override
    public ArrayList<DBField> createMap() {
        ArrayList<DBField> map = new ArrayList<>();
        map.add(new DBField(Integer.parseInt(billNumber.getText().trim()), "receetId", "PK"));
        map.add(new DBField(date, "date", "NN"));
        map.add(new DBField(orderKind, "receetType", "NN"));
        map.add(new DBField(tax_AND_Serv.get(), "calcAdditions", "NN"));
        map.add(new DBField(discountAmount.get(), "discount", "NN"));
        map.add(new DBField(paidAmount.get(), "paid", "NN"));
        map.add(new DBField(cashier.getUserName(), "userName", "NN"));
        if (customer != null && customer.getFullName() != null) {
            map.add(new DBField(customer.getFullName(), "customerName", "NN"));
            map.add(new DBField(customer.getCustomerType().equals(Customer.CustomerType.Guest)
                    ? "عميل" : customer.getCustomerType().equals(Customer.CustomerType.Staff) ? "موظف" : "مالك", "customerCateg", "NN"));
        } else if (customer != null) {
            map.add(new DBField(customer.getCustomerType().equals(Customer.CustomerType.Guest)
                    ? "عميل" : customer.getCustomerType().equals(Customer.CustomerType.Staff) ? "موظف" : "مالك", "customerCateg", "NN"));
        } else {
            map.add(new DBField("عميل", "customerCateg", "NN"));
        }
        map.add(new DBField(tableName, "tableName", "NN"));
        map.add(new DBField(1, "daily", "NN"));
        map.add(new DBField(0, "deptPaid", "NN"));
        map.add(new DBField(Methods.GetMaximum("sheftNum", DB_TABLENAME, " WHERE daily=1 "), "sheftNum", "NN"));
        map.add(new DBField(status, "receetStatus", "NN"));
        return map;
    }

    public static void fillDailyCafeTable() {
        orderObservableList.clear();
        try {
            double barPaid = 0,
                    hallPaid = 0,
                    takeAwayPaid = 0,
                    deliveryPaid = 0,
                    dept = 0;

            double tempPaid, tempDept;
            String tempKind;
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT Time(resturantReceets.date),Date(resturantReceets.date),resturantReceets.receetId,resturantReceets.receetType, "
                    + "(SUM(resturantDetails.price*resturantDetails.num)+resturantReceets.calcAdditions),resturantReceets.discount, "
                    + "((SUM(resturantDetails.price*resturantDetails.num)+resturantReceets.calcAdditions)-resturantReceets.discount), "
                    + "resturantReceets.paid, "
                    + "((SUM(resturantDetails.price*resturantDetails.num)+resturantReceets.calcAdditions)-resturantReceets.discount)-resturantReceets.paid, "
                    + "resturantReceets.userName ,resturantReceets.customerName,resturantReceets.tableName,resturantReceets.calcAdditions,resturantReceets.customerCateg ,resturantReceets.receetStatus "
                    + "FROM resturantReceets JOIN resturantDetails ON resturantReceets.receetId=resturantDetails.receetId AND resturantReceets.daily=1 GROUP BY resturantReceets.receetId").executeQuery();

            while (res.next()) {
                tempPaid = res.getDouble(8);
                tempKind = res.getString(4);
                tempDept = res.getDouble(9);
                if (res.getString(15).equalsIgnoreCase("confirm")) {
                    if (tempKind.equalsIgnoreCase(OrderKind.Bar.toString())) {
                        barPaid += tempPaid;
                    } else if (tempKind.equalsIgnoreCase(OrderKind.Hall.toString())) {
                        hallPaid += tempPaid;
                    } else if (tempKind.equalsIgnoreCase(OrderKind.Delivery.toString())) {
                        deliveryPaid += tempPaid;
                    } else {
                        takeAwayPaid += tempPaid;
                    }
                    dept += tempDept;
                }
                Order order = new Order(
                        res.getString(1),
                        res.getString(2),
                        res.getInt(3),
                        tempKind,
                        res.getDouble(5),
                        res.getDouble(6),
                        res.getDouble(7),
                        tempPaid,
                        tempDept,
                        res.getString(10),
                        res.getString(11),
                        res.getString(12),
                        res.getString(14),
                        res.getDouble(13),
                        res.getString(15),
                        true,
                        false
                );
                order.setOnChangeState((event) -> {
                    if (order.statusIcon.isSuccess()) {
                        if (DialogHelper.getInstance().showConfirmAlert("الغاء الفاتورة؟") == 1) {
                            // Remove from db
                            order.delete();
                            // update UI
                            Platform.runLater(() -> {
                                orderObservableList.remove(order);
                                Order.fillDailyCafeTable();
                                RestaurantHomeController.anyAct = 1;
                            });
                        }
                    } else {
                        DialogHelper.getInstance().showOKAlert("الفاتورة مرتجعة");
                    }
                });
                orderObservableList.add(order);
            }
            barSumitionPointer.setText(HelperMethods.formatNum(barPaid) + ControlPanel.getInstance().getCurrency());
            hallSumitionPointer.setText(HelperMethods.formatNum(hallPaid) + ControlPanel.getInstance().getCurrency());
            deliverySumitionPointer.setText(HelperMethods.formatNum(deliveryPaid) + ControlPanel.getInstance().getCurrency());
            takAwaySumitionPointer.setText(HelperMethods.formatNum(takeAwayPaid) + ControlPanel.getInstance().getCurrency());
            totalPointer.setText(HelperMethods.formatNum(barPaid + hallPaid + takeAwayPaid + deliveryPaid) + ControlPanel.getInstance().getCurrency());

            deptSumitionPointer.setText(HelperMethods.formatNum(dept) + ControlPanel.getInstance().getCurrency());
            DayOrdersTablePointer.setPredicate(treeItems -> true);

            if (LoadHelper.getInstance().dailySheetController != null) {
                LoadHelper.getInstance().dailySheetController.updateAllData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in Order -> fillDailyCafeTable() :-  " + e);
        }
    }

    public void delete() {
        try {
            System.out.println("---- RETURNED ----");
            this.products.forEach(System.out::println);
            DatabaseHandler.con.prepareStatement("UPDATE resturantReceets SET receetStatus='returned' WHERE resturantReceets.receetId=" + this.billNumber.getText()).execute();
            DatabaseHandler.con.prepareStatement("DELETE FROM 'dailypullstore' WHERE receiptID=" + this.billNumber.getText()).execute();
            this.products.forEach((productOrder) -> {
                pullFromORPushTOStore(productOrder.getProduct().getName(), productOrder.getProduct().getArguments(), (int) productOrder.getAmount(), "push");
            });
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + ".delete() => " + e);
        }
    }

    public static ObservableList<Order> fillDailyCafeTable(String day) {
        ObservableList<Order> orders = FXCollections.observableArrayList();
        try {

            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT Time(resturantReceets.date),Date(resturantReceets.date),resturantReceets.receetId,resturantReceets.receetType,"
                    + "(SUM(resturantDetails.price*resturantDetails.num)+resturantReceets.calcAdditions),resturantReceets.discount,"
                    + "((SUM(resturantDetails.price*resturantDetails.num)+resturantReceets.calcAdditions)-resturantReceets.discount),"
                    + "resturantReceets.paid,"
                    + "((SUM(resturantDetails.price*resturantDetails.num)+resturantReceets.calcAdditions)-resturantReceets.discount)-resturantReceets.paid,"
                    + "resturantReceets.userName ,resturantReceets.customerName,resturantReceets.tableName,resturantReceets.calcAdditions,resturantReceets.customerCateg,resturantReceets.receetStatus "
                    + " FROM resturantReceets JOIN resturantDetails ON resturantReceets.receetId=resturantDetails.receetId AND Date(resturantReceets.date)='" + day + "' AND resturantReceets.daily=0  GROUP BY resturantReceets.receetId").executeQuery();

            while (res.next()) {
                orders.add(
                        new Order(
                                res.getString(1),
                                res.getString(2),
                                res.getInt(3),
                                res.getString(4),
                                res.getDouble(5),
                                res.getDouble(6),
                                res.getDouble(7),
                                res.getDouble(8),
                                res.getDouble(9),
                                res.getString(10),
                                res.getString(11),
                                res.getString(12),
                                res.getString(14),
                                res.getDouble(13),
                                res.getString(15),
                                false,
                                false
                        )
                );
            }

        } catch (Exception e) {

            Logger.writeLog("Exception in Order -> fillDailyCafeTable(day) :-  " + e);
        }
        return orders;
    }

    public static ObservableList<Order> getOrdersOfCustomer(String name) {
        ObservableList<Order> orders = FXCollections.observableArrayList();
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT Time(resturantReceets.date),Date(resturantReceets.date),resturantReceets.receetId,resturantReceets.receetType,"
                    + "(SUM(resturantDetails.price*resturantDetails.num)+resturantReceets.calcAdditions),resturantReceets.discount,"
                    + "((SUM(resturantDetails.price*resturantDetails.num)+resturantReceets.calcAdditions)-resturantReceets.discount),"
                    + "resturantReceets.paid+resturantReceets.deptPaid,"
                    + "((SUM(resturantDetails.price*resturantDetails.num)+resturantReceets.calcAdditions)-resturantReceets.discount)-(resturantReceets.paid+resturantReceets.deptPaid),"
                    + "resturantReceets.userName ,resturantReceets.customerName,resturantReceets.tableName,resturantReceets.calcAdditions,resturantReceets.customerCateg,resturantReceets.receetStatus "
                    + " FROM resturantReceets JOIN resturantDetails ON resturantReceets.receetId=resturantDetails.receetId AND resturantReceets.daily=0 AND resturantReceets.customerName='" + name + "' GROUP BY resturantReceets.receetId").executeQuery();

            while (res.next()) {
                orders.add(
                        new Order(
                                res.getString(1),
                                res.getString(2),
                                res.getInt(3),
                                res.getString(4),
                                res.getDouble(5),
                                res.getDouble(6),
                                res.getDouble(7),
                                res.getDouble(8),
                                res.getDouble(9),
                                res.getString(10),
                                res.getString(11),
                                res.getString(12),
                                res.getString(14),
                                res.getDouble(13),
                                res.getString(15),
                                false, true
                        )
                );
            }

        } catch (Exception e) {
            Logger.writeLog("Exception in Order -> fillDailyCafeTable(day) :-  " + e);
        }
        return orders;
    }

    //    .....  الضريبة والخدمه .....
    public static double getTax(String target) {
        double tax = 0;
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT tax FROM taxAndServ WHERE target='" + target + "'").executeQuery();
            while (res.next()) {
                tax = res.getDouble(1);
            }

        } catch (Exception e) {
            Logger.writeLog("Exception in Order -> getTax() " + e);
        }
        return tax;
    }

    public static double getServant(String target) {
        double Servant = 0;
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT serv FROM taxAndServ where target='" + target + "'").executeQuery();
            while (res.next()) {
                Servant = res.getDouble(1);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in Order -> getServant() " + e);
        }
        return HelperMethods.formatNum(Servant);
    }

    public static double getTaxAndServantPercentage(String target) {
        double percentage = 0;
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT (serv/100),(tax/100) FROM taxAndServ where target='" + target + "'").executeQuery();

            while (res.next()) {
                percentage = res.getDouble(1);
                percentage += res.getDouble(2);
            }

        } catch (Exception e) {
            Logger.writeLog("Exception in Order -> getTaxAndServantPercentage() " + e);
        }
        return HelperMethods.formatNum(percentage);
    }

    public TableViewButton getShowCustomerData() {
        return showCustomerData;
    }

    public void setShowCustomerData(TableViewButton showCustomerData) {
        this.showCustomerData = showCustomerData;
    }

}
