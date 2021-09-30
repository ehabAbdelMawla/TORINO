/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package datamodel;

import java.util.ArrayList;
import util.db.DBCRUD;
import util.db.DBField;

/**
 *
 * @author LENOVO
 */
public class deptDetails extends DBCRUD<deptDetails> {

    private int billNumber;
    private String cashierName;
    private double deptVal;

    public deptDetails(int billNumber, String cashierName, double deptVal) {
        super("");
        this.billNumber = billNumber;
        this.cashierName = cashierName;
        this.deptVal = deptVal;
    }

    public int getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(int billNumber) {
        this.billNumber = billNumber;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public double getDeptVal() {
        return deptVal;
    }

    public void setDeptVal(double deptVal) {
        this.deptVal = deptVal;
    }

    @Override
    public ArrayList<DBField> createMap() {
        return null;
    }

}
