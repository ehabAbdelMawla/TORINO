package datamodel;

import util.gui.HelperMethods;

public class Promotion {

    private double numOfHours;
    private double discountPercentage;

    public Promotion() {
    }

    public Promotion(double numOfHours, double discountPercentage) {
        this.numOfHours = HelperMethods.formatNum(numOfHours);
        this.discountPercentage = HelperMethods.formatNum(discountPercentage);
    }

    public double getNumOfHours() {
        return numOfHours;
    }

    public void setNumOfHours(double numOfHours) {
        this.numOfHours = HelperMethods.formatNum(numOfHours);
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = HelperMethods.formatNum(discountPercentage);
    }

}
