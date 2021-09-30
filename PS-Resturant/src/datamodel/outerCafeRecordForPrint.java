package datamodel;

import java.sql.ResultSet;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.HelperMethods;

public class outerCafeRecordForPrint {

    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");

//    Receet Upper Part
    String time;
    String day;
    int receetNum;
    int sheftNum;
    String receetKind;
    String customerCateg;
    String tableName;
    String cahier;

    //    Details Band
    ArrayList<cafeRecordForPrint> innerData = new ArrayList<>();

//    Receet Lower Part
    double tax;
    double serv;
    double total;
    double discount;
    double required;
    double paid;
    double remain;

    public outerCafeRecordForPrint(String time, String day, int receetNum, int sheftNum, String receetKind, String customerCateg, String tableName, String cahier, double tax, double serv, double total, double discount, double required, double paid, double remain) {
        this.time = LocalTime.parse(time).format(timeFormat);
        this.day = day;
        this.receetNum = receetNum;
        this.sheftNum = sheftNum;
        this.receetKind = receetKind.equalsIgnoreCase("Bar") ? "بار" : receetKind.equalsIgnoreCase("Hall") ? "صالة" : receetKind.equalsIgnoreCase("Delivery") ? "توصيل طلبات" : "تيك اوي";
        this.customerCateg = customerCateg;
        this.tableName = tableName;
        this.cahier = cahier;
        this.tax = HelperMethods.formatNum(tax);
        this.serv = HelperMethods.formatNum(serv);
        this.total = HelperMethods.formatNum(total);
        this.discount = HelperMethods.formatNum(discount);
        this.required = HelperMethods.formatNum(required);
        this.paid = HelperMethods.formatNum(paid);
        this.remain = HelperMethods.formatNum(remain);
        innerData = selectDetailsof(receetNum);

    }

    private ArrayList<cafeRecordForPrint> selectDetailsof(int receetNum) {
        ArrayList<cafeRecordForPrint> innerData = new ArrayList();
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT resturantDetails.productName,SUM(resturantDetails.num),resturantDetails.price,SUM(resturantDetails.num*resturantDetails.price) FROM resturantDetails WHERE receetId=" + receetNum + " GROUP BY resturantDetails.productName").executeQuery();
            while (res.next()) {
                innerData.add(new cafeRecordForPrint(res.getString(1), res.getInt(2), res.getDouble(3), res.getDouble(4)));
            }
        } catch (Exception e) {

            Logger.writeLog("Exception in outerCafeRecordForPrint -> selectDetailsof :- " + e);
        }
        return innerData;
    }

    public ArrayList<cafeRecordForPrint> getInnerData() {
        return innerData;
    }

    public void setInnerData(ArrayList<cafeRecordForPrint> innerData) {
        this.innerData = innerData;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSheftNum() {
        return sheftNum;
    }

    public void setSheftNum(int sheftNum) {
        this.sheftNum = sheftNum;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getReceetNum() {
        return receetNum;
    }

    public void setReceetNum(int receetNum) {
        this.receetNum = receetNum;
    }

    public String getReceetKind() {
        return receetKind;
    }

    public void setReceetKind(String receetKind) {
        this.receetKind = receetKind;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getCahier() {
        return cahier;
    }

    public void setCahier(String cahier) {
        this.cahier = cahier;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getServ() {
        return serv;
    }

    public void setServ(double serv) {
        this.serv = serv;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getRequired() {
        return required;
    }

    public void setRequired(double required) {
        this.required = required;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public double getRemain() {
        return remain;
    }

    public void setRemain(double remain) {
        this.remain = remain;
    }

    public String getCustomerCateg() {
        return customerCateg;
    }

    public void setCustomerCateg(String customerCateg) {
        this.customerCateg = customerCateg;
    }

}
