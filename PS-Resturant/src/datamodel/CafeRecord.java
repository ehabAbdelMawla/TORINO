package datamodel;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.ObservableList;
import util.gui.HelperMethods;

public class CafeRecord extends RecursiveTreeObject<CafeRecord> {

    public int ReceetNUm;
    public String DrinkName;
    public int numOfDrink;
    public float totalprice;
    public String productCateg;
    public ObservableList<ArgumentsClass> ListOFArgs;

    public CafeRecord(int ReceetNUm, String DrinkName, int numOfDrink, float totalprice, String productCateg, ObservableList<ArgumentsClass> ListOFArgs) {
        this.ReceetNUm = ReceetNUm;
        this.DrinkName = DrinkName;
        this.numOfDrink = numOfDrink;
        this.totalprice = HelperMethods.formatNum(totalprice);
        this.productCateg = productCateg;
        this.ListOFArgs = ListOFArgs;

    }

    public String getDrinkName() {
        return DrinkName;
    }

    public void setDrinkName(String DrinkName) {
        this.DrinkName = DrinkName;
    }

    public int getNumOfDrink() {
        return numOfDrink;
    }

    public void setNumOfDrink(int numOfDrink) {
        this.numOfDrink = numOfDrink;
    }

    public float getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(float totalprice) {
        this.totalprice = HelperMethods.formatNum(totalprice);
    }

    public String getProductCateg() {
        return productCateg;
    }

    public void setProductCateg(String productCateg) {
        this.productCateg = productCateg;
    }

    public static boolean isIdentical(CafeRecord rec1, CafeRecord rec2) {
        return rec1.numOfDrink == rec2.numOfDrink && rec1.DrinkName.equals(rec2.DrinkName) && rec1.productCateg.equals(rec2.productCateg);
    }

    @Override
    public String toString() {
        return "CafeRecord{" + "ReceetNUm=" + ReceetNUm + ", DrinkName=" + DrinkName + ", numOfDrink=" + numOfDrink + ", totalprice=" + totalprice + ", productCateg=" + productCateg + ", ListOFArgs=" + ListOFArgs + '}';
    }

}
