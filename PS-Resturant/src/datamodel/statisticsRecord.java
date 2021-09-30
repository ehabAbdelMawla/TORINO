package datamodel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import util.gui.HelperMethods;

public class statisticsRecord extends RecursiveTreeObject<statisticsRecord> {

    public JFXButton categName;
    public double totalVal;
    public double percentage;
    public double percentageOFAll;
    public long counter;

    public statisticsRecord(String categName, double totalVal, double percentage, double percentageOFAll, long counter, int x) {
        this.categName = new JFXButton(categName);
        this.totalVal = HelperMethods.formatNum(totalVal);
        this.percentage = HelperMethods.formatNum(percentage);
        this.percentageOFAll = HelperMethods.formatNum(percentageOFAll);
        this.counter = counter;
//        if (x == 0) {
//            this.categName.getStyleClass().add("light-button");
//        }
    }

    public JFXButton getCategName() {
        return categName;
    }

    public void setCategName(JFXButton categName) {
        this.categName = categName;
    }

    public double getTotalVal() {
        return totalVal;
    }

    public void setTotalVal(double totalVal) {
        this.totalVal = HelperMethods.formatNum(totalVal);
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = HelperMethods.formatNum(percentage);
    }

    public double getPercentageOFAll() {
        return percentageOFAll;
    }

    public void setPercentageOFAll(double percentageOFAll) {
        this.percentageOFAll = HelperMethods.formatNum(percentageOFAll);
    }

}
