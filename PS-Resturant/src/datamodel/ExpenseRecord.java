package datamodel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import util.gui.HelperMethods;
import util.gui.button.TableViewButton;

public class ExpenseRecord extends RecursiveTreeObject<ExpenseRecord> {

    public int id;
    public String date;
    public String userName;
    public String need;
    public float price;
    public TableViewButton editBtn = new TableViewButton("تعديل", new FontAwesomeIconView(FontAwesomeIcon.EDIT));

    public ExpenseRecord(int id, String date, String userName, String need, float price) {
        this.id = id;
        this.date = date;
        this.userName = userName;
        this.need = need;
        this.price = HelperMethods.formatNum(price);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNeed() {
        return need;
    }

    public void setNeed(String need) {
        this.need = need;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = HelperMethods.formatNum(price);
    }

    public JFXButton getEditBtn() {
        return editBtn;
    }

    public void setEditBtn(JFXButton editBtn) {
        this.editBtn = (TableViewButton) editBtn;
    }

}
