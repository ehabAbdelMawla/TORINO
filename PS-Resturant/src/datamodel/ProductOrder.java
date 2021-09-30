package datamodel;

import util.gui.HelperMethods;

public class ProductOrder {
    private Product product;
    private double amount;

    public ProductOrder(Product product, double amount) {
        this.product = product;
        this.amount =  HelperMethods.formatNum(amount);
    }

    @Override
    public String toString() {
        return "ProductOrder{" +
                "product=" + product +
                ", amount=" + amount +
                '}';
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount =  HelperMethods.formatNum(amount);
    }
}
