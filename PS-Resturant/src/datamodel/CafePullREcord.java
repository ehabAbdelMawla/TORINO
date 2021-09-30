package datamodel;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import util.db.Methods;
import util.gui.HelperMethods;

public class CafePullREcord extends RecursiveTreeObject<CafePullREcord> {

    public int id;
    public String day;
    public String argName;
    public double numOfArg;
    public String prodName;
    public int numOfProd;

    public CafePullREcord(int id, String day, String argName, double numOfArg, String prodName, int numOfProd) {
        this.id = id;
        this.day = day;
        this.argName = argName;
        this.numOfArg =  HelperMethods.formatNum(numOfArg);
        this.prodName = prodName;
        this.numOfProd = numOfProd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getArgName() {
        return argName;
    }

    public void setArgName(String argName) {
        this.argName = argName;
    }

    public double getNumOfArg() {
        return numOfArg;
    }

    public void setNumOfArg(double numOfArg) {
        this.numOfArg =  HelperMethods.formatNum(numOfArg);
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public int getNumOfProd() {
        return numOfProd;
    }

    public void setNumOfProd(int numOfProd) {
        this.numOfProd = numOfProd;
    }

}
