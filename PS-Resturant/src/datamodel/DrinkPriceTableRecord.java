package datamodel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ObservableList;
import util.gui.HelperMethods;
import util.gui.button.TableViewButton;

public class DrinkPriceTableRecord extends RecursiveTreeObject<DrinkPriceTableRecord> {

    public int id;
    public String DrinkName;
    public float Price;
    public float Price2;
    public String categ;
    public ObservableList<ArgumentsClass> argums;
    public String barCode;
    public TableViewButton EditBtn = new TableViewButton("تعديل", new FontAwesomeIconView(FontAwesomeIcon.EDIT));
    public TableViewButton DelBtn = new TableViewButton("حذف", new FontAwesomeIconView(FontAwesomeIcon.TRASH));

    public DrinkPriceTableRecord(int id, String DrinkName, float Price, float Price2, String categ, ObservableList<ArgumentsClass> argums) {
        this.id = id;
        this.DrinkName = DrinkName;
        this.Price = HelperMethods.formatNum(Price);
        this.Price2 = HelperMethods.formatNum(Price2);
        this.categ = categ;
        this.argums = argums;
    }

    public DrinkPriceTableRecord(int id, String DrinkName, float Price, float Price2, String categ, ObservableList<ArgumentsClass> argums, String barCode) {
        this.id = id;
        this.DrinkName = DrinkName;
        this.Price = Price;
        this.Price2 = Price2;
        this.categ = categ;
        this.argums = argums;
        this.barCode = barCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDrinkName() {
        return DrinkName;
    }

    public void setDrinkName(String DrinkName) {
        this.DrinkName = DrinkName;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float Price) {
        this.Price = HelperMethods.formatNum(Price);
    }

    public float getPrice2() {
        return Price2;
    }

    public void setPrice2(float Price2) {
        this.Price2 = HelperMethods.formatNum(Price2);
    }

    public String getCateg() {
        return categ;
    }

    public void setCateg(String categ) {
        this.categ = categ;
    }

    public ObservableList<ArgumentsClass> getArgums() {
        return argums;
    }

    public void setArgums(ObservableList<ArgumentsClass> argums) {
        this.argums = argums;
    }

    public JFXButton getEditBtn() {
        return EditBtn;
    }

    public void setEditBtn(JFXButton EditBtn) {
        this.EditBtn = (TableViewButton) EditBtn;
    }

    public JFXButton getDelBtn() {
        return DelBtn;
    }

    public void setDelBtn(JFXButton DelBtn) {
        this.DelBtn = (TableViewButton) DelBtn;
    }

    public String getBarCode() {
        return barCode;
    }

    public boolean haveBarCode() {
        return barCode != null;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    @Override
    public String toString() {
        return "DrinkPriceTableRecord{" + "id=" + id + ", DrinkName=" + DrinkName + ", Price=" + Price + ", Price2=" + Price2 + ", categ=" + categ + ", argums=" + argums + ", barCode=" + barCode + ", EditBtn=" + EditBtn + ", DelBtn=" + DelBtn + '}';
    }

}
