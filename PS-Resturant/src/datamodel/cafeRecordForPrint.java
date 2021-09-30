package datamodel;

import util.db.Methods;
import util.gui.HelperMethods;

public class cafeRecordForPrint {

    public String drinkName;
    public int numOfDrink;
    public double price;
    public double totalprice;

    public cafeRecordForPrint(String drinkName, int numOfDrink, double price, double totalprice) {
        this.drinkName = drinkName;
        this.numOfDrink = numOfDrink;
        this.price = HelperMethods.formatNum(price);
        this.totalprice = HelperMethods.formatNum(totalprice);
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    
    
    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public int getNumOfDrink() {
        return numOfDrink;
    }

    public void setNumOfDrink(int numOfDrink) {
        this.numOfDrink = numOfDrink;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = HelperMethods.formatNum(totalprice);
    }

}
