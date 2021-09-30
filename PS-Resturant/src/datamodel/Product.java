package datamodel;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.sql.ResultSet;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.image.Image;
import javafx.util.StringConverter;
import util.Logger;
import util.db.DBCRUD;
import util.db.DBField;
import util.db.DatabaseHandler;
import util.gui.HelperMethods;
import util.gui.button.TableViewButton;

public class Product extends DBCRUD<Product> {

    public static final String DB_TABLENAME = "dailycafesheet";

    public static String custormerCateg = "guest";
    public static ObservableList<Product> ALL_PRODUCTS = FXCollections.observableArrayList();
    private int id;
    private String name;
    private String description;
    private String category;
    private double guestPrice;
    private double staffPrice;
    private String barCode;
    private ObservableList<ArgumentsClass> arguments;
    private TableViewButton edit;
    private TableViewButton del;

    public Product(String name, String category, double guestPrice, double staffPrice, String arguments) {
        super(DB_TABLENAME);
        this.name = name;
        this.category = category;
        this.guestPrice = HelperMethods.formatNum(guestPrice);
        this.staffPrice = HelperMethods.formatNum(staffPrice);
        this.arguments = convertStringToListOfArgs(arguments);
        this.edit = new TableViewButton("تعديل", new FontAwesomeIconView(FontAwesomeIcon.EDIT));
        this.edit.setOnAction(this::editAction);
        this.del = new TableViewButton("حذف", new FontAwesomeIconView(FontAwesomeIcon.TRASH));
    }

    public Product(int id, String name, String category, double guestPrice, double staffPrice, String arguments, String barCode) {
        super(DB_TABLENAME);
        this.id = id;
        this.name = name;
        this.category = category;
        this.guestPrice = HelperMethods.formatNum(guestPrice);
        this.staffPrice = HelperMethods.formatNum(staffPrice);
        this.arguments = convertStringToListOfArgs(arguments);
        this.edit = new TableViewButton("تعديل", new FontAwesomeIconView(FontAwesomeIcon.EDIT));
        this.edit.setOnAction(this::editAction);
        this.del = new TableViewButton("حذف", new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        this.barCode = barCode;
    }

    private void editAction(Event event) {
//        settings.NewProductController.productToEdit = new DrinkPriceTableRecord(0, name, (float) guestPrice, (float) staffPrice, category, arguments);
//        settings.NewProductController.fakeInitialize();
//        stg.showAndWait();
    }

    public Product(String name, String description, Category category, double price, Image image, double minLimit, ObservableList<ArgumentsClass> arguments) {
        super(DB_TABLENAME);
        this.name = name;
        this.description = description;
        this.category = category.getName();
        this.guestPrice = price;
        this.arguments = FXCollections.observableArrayList();
        if (arguments != null) {
            this.arguments.addAll(arguments);
        }
    }

    public static StringConverter<Product> getStringConverter() {
        return new StringConverter<Product>() {
            @Override
            public String toString(Product object) {
                return object.getName();
            }

            @Override
            public Product fromString(String string) {
                return null;
            }
        };
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public boolean haveBarCode() {
        return barCode != null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getGuestPrice() {
        return guestPrice;
    }

    public void setGuestPrice(double guestPrice) {
        this.guestPrice = HelperMethods.formatNum(guestPrice);
    }

    public double getStaffPrice() {
        return staffPrice;
    }

    public void setStaffPrice(double staffPrice) {
        this.staffPrice = HelperMethods.formatNum(staffPrice);
    }

    public double getPrice() {

        return custormerCateg.equalsIgnoreCase("guest") ? guestPrice : custormerCateg.equalsIgnoreCase("staff") ? staffPrice : 0;
    }

    public TableViewButton getEdit() {
        return edit;
    }

    public void setEdit(TableViewButton edit) {
        this.edit = edit;
    }

    public TableViewButton getDel() {
        return del;
    }

    public void setDel(TableViewButton del) {
        this.del = del;
    }

    public static String getCustormerCateg() {
        return Product.custormerCateg;
    }

    public static void setCustormerCateg(String custormerCateg) {
        Product.custormerCateg = custormerCateg;
    }

    public ObservableList<ArgumentsClass> getArguments() {
        return arguments;
    }

    public void setArguments(ObservableList<ArgumentsClass> arguments) {
        this.arguments = arguments;
    }

    public static void getAllProducts() {
        ALL_PRODUCTS.clear();
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT * FROM drinkprice").executeQuery();
            while (res.next()) {
//                  (String name, String category, double guestPrice, double staffPrice, ObservableList<ArgumentsClass> arguments) 
                ALL_PRODUCTS.add(new Product(res.getInt(3), res.getString(1), res.getString(5), res.getDouble(2), res.getDouble(4), res.getString(6), res.getString(7)));
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in Product -> getAllProducts() :- " + e);
        }
    }

    public static ObservableList<ArgumentsClass> convertStringToListOfArgs(String args) {
        ObservableList<ArgumentsClass> argsList = FXCollections.observableArrayList();
        argsList.clear();
        try {
            if (!(args.equals(""))) {
                String[] numOFArgs = args.split("#");
                for (int i = 0; i < numOFArgs.length; i++) {
                    argsList.add(new ArgumentsClass(Double.parseDouble((numOFArgs[i].split("@"))[0]), (numOFArgs[i].split("@"))[1]));
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in ProductController -> convertStringToListOfArgs:-" + e);
        }
        return argsList;
    }

    @Override
    public ArrayList<DBField> createMap() {
        ArrayList<DBField> map = new ArrayList<>();
        return map;
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", name=" + name + ", description=" + description + ", category=" + category + ", guestPrice=" + guestPrice + ", staffPrice=" + staffPrice + ", barCode=" + barCode + ", arguments=" + arguments + '}';
    }

}
