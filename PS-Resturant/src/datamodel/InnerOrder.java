package datamodel;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static restaurant.hall.TableOrderController.INNER_ORDERS;
import static restaurant.hall.TableOrderController.TableNumber_Label_Static;
import settings.products.NewProductController;
import storage.StorageController;
import util.Logger;
import util.db.DBCRUD;
import util.db.DBField;
import util.gui.HelperMethods;
import util.gui.button.TableViewButton;
import util.gui.load.LoadHelper;

public class InnerOrder extends DBCRUD<InnerOrder> {

    private Product product;
    private double amount;
    private double total;
    public TableViewButton delete;

    public InnerOrder(Product product, double amount, double total) throws Exception {
        super("");
        if (isProductAvailable() && pullFromStore()) {
            this.product = product;
            this.amount = HelperMethods.formatNum(amount);
            this.total = HelperMethods.formatNum(total);
            this.delete = new TableViewButton(product.getName(), new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        } else {
            throw new Exception("No Product in Store");
        }
    }

    public InnerOrder(String productName, double amount, double gPrice, double sPrice) throws Exception {
        super("");
        this.product = Product.ALL_PRODUCTS.stream()
                .filter(product -> product.getName().equalsIgnoreCase(productName))
                .findAny().orElse(null);
        product.setGuestPrice(HelperMethods.formatNum(gPrice));
        product.setStaffPrice(HelperMethods.formatNum(sPrice));
        this.amount = HelperMethods.formatNum(amount);
        this.total = HelperMethods.formatNum(amount * product.getPrice());
        this.delete = new TableViewButton(product.getName(), new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        ArrayList<DBField> fieldsMap = new ArrayList<>();
        fieldsMap.add(new DBField(TableNumber_Label_Static.getText().trim(), "tableName", "PK"));
        fieldsMap.add(new DBField(product.getName(), "drinkName", "PK"));
        fieldsMap.add(new DBField((int) amount, "numofdr", "NN"));
        fieldsMap.add(new DBField(product.getPrice(), "price", "NN"));
        fieldsMap.add(new DBField(product.getCategory(), "productCateg", "NN"));
        fieldsMap.add(new DBField(NewProductController.convertArgsListToString(product.getArguments()), "productArgs", "NN"));
        product.setFields(fieldsMap);
        delete.setOnAction((e) -> { // TODO #001
            // return to storage
            // remove from table "INNER_ORDERS"
            StorageController.pullFromORPushTOStore(product.getName(), product.getArguments(), ((int) amount), "push");
            product.remove();
            INNER_ORDERS.remove(this);
            LoadHelper.getInstance().tableOrderController.resetAdditionalData();
        });

    }

    private boolean pullFromStore() {
        // pull executed successfully
        return true;
    }

    private boolean isProductAvailable() {
        // search in Store
        // Database
        // #EHAB
        return true;
    }

    @Override
    public String toString() {
        return "InnerOrder{"
                + "product=" + product
                + ", amount=" + amount
                + ", total=" + total
                + ", delete=" + delete
                + '}';
    }

    public Product getProduct() {
        return product;
    }

    public double getProductPrice() {
        return product.getPrice();
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = HelperMethods.formatNum(amount);
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = HelperMethods.formatNum(total);
    }

    public TableViewButton getDelete() {
        return delete;
    }

    public void setDelete(TableViewButton delete) {
        this.delete = delete;
    }

    public static InnerOrder productOrder_To_InnerOrder(ProductOrder productOrder) throws Exception {
        return new InnerOrder(productOrder.getProduct(), productOrder.getAmount(), productOrder.getAmount() * productOrder.getProduct().getPrice());
    }

    public static ObservableList<InnerOrder> productOrderList_To_InnerOrderList(ArrayList<ProductOrder> productOrders) throws Exception {
        ObservableList<InnerOrder> innerOrders = FXCollections.observableArrayList();
        for (ProductOrder order : productOrders) {
            innerOrders.add(productOrder_To_InnerOrder(order));
        }
        return innerOrders;
    }

    @Override
    public ArrayList<DBField> createMap() {
        return null;
    }

    public static void setReceetDetails(int receetId, ObservableList<InnerOrder> receetsOrder) {
        try {
            ArrayList<DBField> map = new ArrayList<>();
            receetsOrder.forEach(inOrder -> {
                map.clear();
                map.add(new DBField(receetId, "receetId", "PK"));
                map.add(new DBField(inOrder.getProduct().getName(), "productName", "NN"));
                map.add(new DBField(inOrder.getAmount(), "num", "NN"));
                map.add(new DBField(inOrder.getProduct().getPrice(), "price", "NN"));
                map.add(new DBField(inOrder.getProduct().getCategory(), "productCateg", "NN"));
                map.add(new DBField(NewProductController.convertArgsListToString(inOrder.getProduct().getArguments()), "productArgs", "NN"));
                inOrder.setTableName("resturantDetails");
                inOrder.setFields(map);
                inOrder.add();
            });
        } catch (Exception e) {
            Logger.writeLog("Exception in InnerOrder -> setReceetDetails :- " + e);
        }
    }
}
