package datamodel;

import com.jfoenix.controls.JFXButton;
import customerdata.CustomersDataController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.sql.ResultSet;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContentDisplay;
import util.Logger;
import util.db.DBCRUD;
import util.db.DBField;
import util.db.DatabaseHandler;
import util.db.Methods;
import util.gui.HelperMethods;
import util.gui.load.DialogHelper;
import util.gui.load.LoadHelper;

public class Customer extends DBCRUD<Customer> {

    public static ObservableList<Customer> customersData = FXCollections.observableArrayList();

    public static final String dbTableName = "customers";
    // general person data
    private String fullName;
    private String phoneNumber;
    private String address;
    private String notes;
    private String email;
    // specific customer data
    private int code; // primary
    private String userWhoCreatedHim;
    private CustomerType customerType;
    private double amountPaid;
    private double amountDebt;
    private JFXButton details;
    private JFXButton choose;
    private double creditHourse;
    private double totalHourse;
    private int attendPeriod;

    public enum CustomerType {
        Owner, Guest, Staff
    }

//    .... Constructors .....
//    add temp fake customer to reciept with no customer Data Versions 
    public Customer(String fullName) {
        super(dbTableName);
        this.fullName = fullName;

    }

    public Customer(CustomerType customerType) {
        super(dbTableName);
        this.customerType = customerType;

    }

//    .... Creation Constructor....
    public Customer(String fullName, String phoneNumber, String email, String address, String notes, CustomerType C, String userWhoCreatedHim, int attendPeriod) {
        super(dbTableName);
        this.code = (int) Methods.GetMaximum("id", dbTableName);
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.notes = notes;
        this.customerType = C;
        this.userWhoCreatedHim = userWhoCreatedHim;
        this.creditHourse = 0;
        this.totalHourse = 0;
        this.attendPeriod = attendPeriod;
        setFields(createMap());
    }

    // ... Retrival Class ...
    public Customer(int code, String fullName, String phoneNumber, String email, String address, String notes, CustomerType customerType, String userWhoCreatedHim, double amountPaid, double amountDebt, double creditHourse, double totalHourse, int attendPeriod) {
        super(dbTableName);
        this.code = code;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.notes = notes;
        this.customerType = customerType;
        this.amountPaid = HelperMethods.formatNum(amountPaid);
        this.amountDebt = HelperMethods.formatNum(amountDebt);
        this.attendPeriod = attendPeriod;
        FontAwesomeIconView delIcon = new FontAwesomeIconView(FontAwesomeIcon.FILE);
        delIcon.setStyleClass("table-btn-icon");
        details = new JFXButton(this.phoneNumber);
        details.setGraphic(delIcon);
        details.getStyleClass().add("table-btn");
        details.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        details.setOnAction((event) -> {
            Customer viewCustomer = DialogHelper.getInstance().viewCustomer(this, true, false);
        });
        FontAwesomeIconView chooseICON = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
        chooseICON.setStyleClass("table-btn-icon");
        choose = new JFXButton(this.phoneNumber);
        choose.setGraphic(chooseICON);
        choose.getStyleClass().add("table-btn");
        choose.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        choose.setOnAction((event) -> {
            LoadHelper.getInstance().chooseCustomerController.hasData = true;
            LoadHelper.getInstance().chooseCustomerController.choosenCustomer = this;
            LoadHelper.getInstance().Stage_ChooseCustomer.close();
        });
        this.creditHourse = HelperMethods.formatNum(creditHourse);
        this.totalHourse = HelperMethods.formatNum(totalHourse);
        setFields(createMap());
    }

    public JFXButton getChoose() {
        return choose;
    }

    public void setChoose(JFXButton choose) {
        this.choose = choose;
    }

    public JFXButton getDetails() {
        return details;
    }

    public void setDetails(JFXButton details) {
        this.details = details;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = HelperMethods.formatNum(amountPaid);
    }

    public double getAmountDebt() {
        return amountDebt;
    }

    public void setAmountDebt(double amountDebt) {
        this.amountDebt = HelperMethods.formatNum(amountDebt);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getUserWhoCreatedHim() {
        return userWhoCreatedHim;
    }

    public void setUserWhoCreatedHim(String userWhoCreatedHim) {
        this.userWhoCreatedHim = userWhoCreatedHim;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public double getCreditHourse() {
        return creditHourse;
    }

    public void setCreditHourse(double creditHourse) {
        this.creditHourse = creditHourse;
    }

    public double getTotalHourse() {
        return totalHourse;
    }

    public void setTotalHourse(double totalHourse) {
        this.totalHourse = totalHourse;
    }

    public int getAttendPeriod() {
        return attendPeriod;
    }

    public void setAttendPeriod(int attendPeriod) {
        this.attendPeriod = attendPeriod;
    }

    // .... Create Map ....
    @Override
    public ArrayList<DBField> createMap() {
        ArrayList<DBField> arr = new ArrayList();
        arr.add(new DBField(code, "id", "PK"));
        arr.add(new DBField(fullName, "name", "NN"));
        arr.add(new DBField(phoneNumber, "phoneNumber", "NN"));
        arr.add(new DBField(email, "email", "NN"));
        arr.add(new DBField(address, "address", "NN"));
        arr.add(new DBField(notes, "notes", "NN"));
        arr.add(new DBField(customerType.toString(), "type", "NN"));
        arr.add(new DBField(userWhoCreatedHim, "userName", "NN"));
        arr.add(new DBField(creditHourse, "chargeMins", "NN"));
        arr.add(new DBField(totalHourse, "totalMins", "NN"));
        arr.add(new DBField(attendPeriod, "period", "NN"));
        return arr;
    }

    public ArrayList<DBField> createEditMap() {
        ArrayList<DBField> arr = new ArrayList();
        arr.add(new DBField(code, "id", "PK"));
        arr.add(new DBField(fullName, "name", "NN"));
        arr.add(new DBField(phoneNumber, "phoneNumber", "NN"));
        arr.add(new DBField(email, "email", "NN"));
        arr.add(new DBField(address, "address", "NN"));
        arr.add(new DBField(notes, "notes", "NN"));
        arr.add(new DBField(customerType.toString(), "type", "NN"));
        arr.add(new DBField(userWhoCreatedHim, "userName", "NN"));
        arr.add(new DBField(attendPeriod, "period", "NN"));
        return arr;
    }

    public static void addPredicate() {
        try {
            LoadHelper.getInstance().chooseCustomerController.Filter();
        } catch (Exception ee) {
        }
    }

    public static void fillData() {
        customersData.clear();
        try {
            CustomerType tempType;
            String tempStr;
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT  id,name,phoneNumber,email,address,notes,type,userName,SUM(total),SUM(dept),chargeMins,totalMins,period FROM "
                    + "(SELECT  customers.id,customers.name,customers.phoneNumber,customers.email, "
                    + "customers.address,customers.notes,customers.type,customers.userName, "
                    + "SUM(resturantDetails.price*resturantDetails.num)+resturantReceets.calcAdditions-resturantReceets.discount as total, "
                    + "(SUM((resturantDetails.price*resturantDetails.num))+(resturantReceets.calcAdditions-resturantReceets.discount)-(resturantReceets.paid+resturantReceets.deptPaid)) as dept "
                    + ",(chargeMins/60) as chargeMins,(totalMins/60) as totalMins,customers.period as period FROM customers  JOIN resturantDetails  JOIN resturantReceets  "
                    + "ON resturantReceets.customerName=customers.name AND resturantReceets.receetId=resturantDetails.receetId  AND resturantReceets.daily=0 AND resturantReceets.receetStatus='Confirm' "
                    + "GROUP BY resturantReceets.receetId)  "
                    + "GROUP BY name "
                    + "UNION SELECT  customers.id,customers.name,customers.phoneNumber,customers.email, "
                    + "customers.address,customers.notes,customers.type,customers.userName,0,0,(chargeMins/60) as chargeMins,(totalMins/60) as totalMins,customers.period as period FROM customers "
                    + "WHERE name Not IN  (SELECT  customers.name "
                    + "FROM customers  JOIN resturantDetails  JOIN resturantReceets "
                    + "ON resturantReceets.customerName=customers.name AND resturantReceets.receetId=resturantDetails.receetId  AND resturantReceets.daily=0 AND resturantReceets.receetStatus='Confirm' "
                    + "GROUP BY resturantReceets.receetId)").executeQuery();
            while (res.next()) {
                tempStr = res.getString(7);
                tempType = tempStr.equalsIgnoreCase("Guest") ? CustomerType.Guest : tempStr.equalsIgnoreCase("Staff") ? CustomerType.Staff : CustomerType.Owner;
                customersData.add(new Customer(res.getInt(1), res.getString(2), res.getString(3), res.getString(4), res.getString(5), res.getString(6), tempType, res.getString(8), res.getDouble(9), res.getDouble(10), res.getDouble(11), res.getDouble(12), res.getInt(13)));
            }
            CustomersDataController.filterTable();
            addPredicate();
        } catch (Exception e) {
            Logger.writeLog("Exception in Customer -> fillData() " + e);
        }
    }

//     For Retriving Receets
    public static Customer getDataOFCustomer(String fullName) {
        try {
            if (fullName != null) {
                Customer exist = customersData.stream().filter(cust -> cust.getFullName().equalsIgnoreCase(fullName.trim())).findAny().orElse(null);
                return exist;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in Customer -> getDataOFCustomer(fullName: " + fullName + ") " + e);
        }
        return null;
    }
}
