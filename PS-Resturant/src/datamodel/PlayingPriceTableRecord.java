package datamodel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import util.gui.HelperMethods;
import util.gui.button.TableViewButton;

public class PlayingPriceTableRecord extends RecursiveTreeObject<PlayingPriceTableRecord> {

    public String deviceName;
    public float singlePrice;
    public float MultiPrice;
    public TableViewButton EditBtn;
    public TableViewButton DelBtn;
    public int id;

    public PlayingPriceTableRecord() {
    }

    public PlayingPriceTableRecord(String DeviceName, float singlePrice, float MultiPrice, int id) {
        this.deviceName = DeviceName;
        this.singlePrice = HelperMethods.formatNum(singlePrice);
        this.MultiPrice = HelperMethods.formatNum(MultiPrice);
        this.EditBtn = new TableViewButton("تعديل", new FontAwesomeIconView(FontAwesomeIcon.EDIT));
        this.DelBtn = new TableViewButton("حذف", new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String DN) {
        this.deviceName = DN;
    }

    public float getSinglePrice() {
        return singlePrice;
    }

    public void setSinglePrice(float singlePrice) {
        this.singlePrice = HelperMethods.formatNum(singlePrice);
    }

    public float getMultiPrice() {
        return MultiPrice;
    }

    public void setMultiPrice(float MultiPrice) {
        this.MultiPrice = HelperMethods.formatNum(MultiPrice);
    }

    public TableViewButton getEditBtn() {
        return EditBtn;
    }

    public void setEditBtn(JFXButton EditBtn) {
        this.EditBtn = (TableViewButton) EditBtn;
    }

    public TableViewButton getDelBtn() {
        return DelBtn;
    }

    public void setDelBtn(JFXButton DelBtn) {
        this.DelBtn = (TableViewButton) DelBtn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
