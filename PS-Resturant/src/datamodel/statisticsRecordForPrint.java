package datamodel;

import util.db.Methods;
import util.gui.HelperMethods;

public class statisticsRecordForPrint {

    public String proName;
    public Long num;
    public double totalIncome;

    public statisticsRecordForPrint(String proName, Long num, double totalIncome) {
        this.proName = proName;
        this.num = num;
        this.totalIncome = HelperMethods.formatNum(totalIncome);
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = HelperMethods.formatNum(totalIncome);
    }

}
