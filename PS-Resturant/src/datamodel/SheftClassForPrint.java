package datamodel;

public class SheftClassForPrint {

    public String userName;
    public double psIncome;
    public double cafeIncome;
    public double deptIncome;
    public double totalIncome;
    public double expensess;

    public SheftClassForPrint(String userName, double psIncome, double cafeIncome, double totalIncome, double deptIncome, double expensess) {
        this.userName = userName;
        this.psIncome = psIncome;
        this.cafeIncome = cafeIncome;
        this.totalIncome = totalIncome;
        this.deptIncome = deptIncome;
        this.expensess = expensess;
    }

    public double getExpensess() {
        return expensess;
    }

    public void setExpensess(double expensess) {
        this.expensess = expensess;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getPsIncome() {
        return psIncome;
    }

    public void setPsIncome(double psIncome) {
        this.psIncome = psIncome;
    }

    public double getCafeIncome() {
        return cafeIncome;
    }

    public void setCafeIncome(double cafeIncome) {
        this.cafeIncome = cafeIncome;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getDeptIncome() {
        return deptIncome;
    }

    public void setDeptIncome(double deptIncome) {
        this.deptIncome = deptIncome;
    }

}
