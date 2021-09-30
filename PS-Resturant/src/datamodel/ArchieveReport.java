/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package datamodel;

import controlpanel.ControlPanel;
import static archive.ArchiveHomeController.getDataForPrint;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import util.Logger;
import util.db.DatabaseHandler;
import util.db.Methods;
import util.gui.HelperMethods;

/**
 *
 * @author Ehab Abdel Mawla
 */
public class ArchieveReport {

    public String day;
    public double psIncome;
    public double cafeIncome;
    public double deptIncome;
    public double expensess;
    public double totalIncome;
    public double netIncome;
    public List<DailySheetForPrint> psDetails = new ArrayList<>();
    public List<outerCafeRecordForPrint> cafeDetails = new ArrayList<>();
    public List<ExpenseRecord> expensesDetails = new ArrayList<>();

    public ArchieveReport(String day, boolean Arch) {
        try {
            this.day = day;
            psDetails.addAll(getDataForPrint("SELECT * FROM dailysheet WHERE actDate='" + this.day + "' and daily=" + (Arch ? 0 : 1)));
            psIncome = HelperMethods.formatNum(psDetails.stream().mapToDouble(DailySheetForPrint::getInComeVal).sum());
            cafeDetails.addAll(getResturantDataForPrint(Arch));
            cafeIncome = HelperMethods.formatNum(cafeDetails.stream().mapToDouble(outerCafeRecordForPrint::getPaid).sum());
            expensesDetails = selectDayxpensess("SELECT * FROM dailyexpense where currentdate='" + this.day + "' and daily=" + (Arch ? 0 : 1));
            expensess = HelperMethods.formatNum(expensesDetails.stream().mapToDouble(ExpenseRecord::getPrice).sum());
            totalIncome = HelperMethods.formatNum(psIncome + cafeIncome);
            deptIncome = Methods.get("SELECT SUM(price) FROM deptData WHERE date='" + this.day + "' AND daily=" + (Arch ? 0 : 1));
            netIncome = ControlPanel.getInstance().EXPENSESS_FROM_DAILY_INCOME ? HelperMethods.formatNum(totalIncome - expensess) : totalIncome;

        } catch (Exception e) {
            Logger.writeLog("Exception in ArchieveReport -> ArchieveReport(day) :-" + e);
        }
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<DailySheetForPrint> getPsDetails() {
        return psDetails;
    }

    public void setPsDetails(List<DailySheetForPrint> psDetails) {
        this.psDetails = psDetails;
    }

    public List<ExpenseRecord> getExpensesDetails() {
        return expensesDetails;
    }

    public void setExpensesDetails(List<ExpenseRecord> expensesDetails) {
        this.expensesDetails = expensesDetails;
    }

    public List<outerCafeRecordForPrint> getCafeDetails() {
        return cafeDetails;
    }

    public void setCafeDetails(List<outerCafeRecordForPrint> cafeDetails) {
        this.cafeDetails = cafeDetails;
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

    public double getExpensess() {
        return expensess;
    }

    public void setExpensess(double expensess) {
        this.expensess = expensess;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(double netIncome) {
        this.netIncome = netIncome;
    }

    public double getDeptIncome() {
        return deptIncome;
    }

    public void setDeptIncome(double deptIncome) {
        this.deptIncome = deptIncome;
    }

    private Collection< outerCafeRecordForPrint> getResturantDataForPrint(boolean arch) {
        ArrayList< outerCafeRecordForPrint> data = new ArrayList<>();
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT TIME(resturantReceets.date),Date(resturantReceets.date),resturantReceets.receetId,resturantReceets.sheftNum, "
                    + "resturantReceets.receetType,resturantReceets.tableName,resturantReceets.userName, "
                    + "VAT.tax,VAT.serv, "
                    + "(SUM((resturantDetails.price*resturantDetails.num))+resturantReceets.calcAdditions), "
                    + "resturantReceets.discount, "
                    + "((SUM((resturantDetails.price*resturantDetails.num))+resturantReceets.calcAdditions)-resturantReceets.discount), "
                    + "resturantReceets.paid, "
                    + "(((SUM((resturantDetails.price*resturantDetails.num))+resturantReceets.calcAdditions)-resturantReceets.discount)-resturantReceets.paid),resturantReceets.customerCateg "
                    + "FROM resturantReceets JOIN   resturantDetails JOIN VAT "
                    + "ON resturantReceets.receetId=resturantDetails.receetId  AND resturantReceets.receetId=VAT.receetId AND resturantReceets.daily=" + (arch ? 0 : 1) + " AND DATE(resturantReceets.date)='" + day + "' "
                    + "GROUP BY resturantReceets.receetId").executeQuery();
            while (res.next()) {
                data.add(new outerCafeRecordForPrint(
                        res.getString(1),
                        res.getString(2),
                        res.getInt(3),
                        res.getInt(4),
                        res.getString(5),
                        res.getString(15),
                        res.getString(6),
                        res.getString(7),
                        res.getDouble(8),
                        res.getDouble(9),
                        res.getDouble(10),
                        res.getDouble(11),
                        res.getDouble(12),
                        res.getDouble(13),
                        res.getDouble(14)));
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in ArchieveReport -> getResturantDataForPrint(str) " + e);
        }
        return data;
    }

    private List<ExpenseRecord> selectDayxpensess(String sql) {
        List<ExpenseRecord> data = new ArrayList<>();
        try {
            ResultSet result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (result.next()) {

                data.add(new ExpenseRecord(
                        result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getFloat(5)
                ));
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in ArchieveReport -> selectDayxpensess(str) " + e);
        }
        return data;
    }
}
