package datamodel;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import util.db.Methods;
import util.gui.HelperMethods;

public class imprestClass extends RecursiveTreeObject<imprestClass> {

    public String us;
    public String acDate;
    public String acTime;
    public Double price;

    public imprestClass(String us, String acDate, String acTime, Double price) {
        this.us = us;
        this.acDate = acDate;
        this.acTime = acTime;
        this.price =  HelperMethods.formatNum(price);
    }

    public String getUs() {
        return us;
    }

    public void setUs(String us) {
        this.us = us;
    }

    public String getAcDate() {
        return acDate;
    }

    public void setAcDate(String acDate) {
        this.acDate = acDate;
    }

    public String getAcTime() {
        return acTime;
    }

    public void setAcTime(String acTime) {
        this.acTime = acTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price =  HelperMethods.formatNum(price);
    }

}
