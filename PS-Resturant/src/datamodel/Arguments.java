package datamodel;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import util.db.Methods;
import util.gui.HelperMethods;

public class Arguments extends RecursiveTreeObject<Arguments> {
    private String name;
    private double amount;

    public Arguments(String name, double amount) {
        this.name = name;
        this.amount = HelperMethods.formatNum(amount);
    }

    @Override
    public String toString() {
        return "Arguments{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
