/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package datamodel;

import util.gui.HelperMethods;

public class Payment {

    private double total;
    private double discount;
    private double required;
    private double paid;
    private double debt;

    public Payment(double total, double discount, double required, double paid, double debt) {
        this.total =  HelperMethods.formatNum(total);
        this.discount =  HelperMethods.formatNum(discount);
        this.required =  HelperMethods.formatNum(required);
        this.paid =  HelperMethods.formatNum(paid);
        this.debt =  HelperMethods.formatNum(debt);
    }

    @Override
    public String toString() {
        return "Payment{" + "total=" + total + ", discount=" + discount + ", required=" + required + ", paid=" + paid + ", debt=" + debt + '}';
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total =  HelperMethods.formatNum(total);
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount =  HelperMethods.formatNum(discount);
    }

    public double getRequired() {
        return required;
    }

    public void setRequired(double required) {
        this.required =  HelperMethods.formatNum(required);
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid =  HelperMethods.formatNum(paid);
    }

    public double getDebt() {
        return debt;
    }

    public void setDebt(double debt) {
        this.debt =  HelperMethods.formatNum(debt);
    }

}
