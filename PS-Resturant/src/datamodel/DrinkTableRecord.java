package datamodel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ObservableList;
import javafx.scene.control.ContentDisplay;
import util.gui.HelperMethods;

public class DrinkTableRecord extends RecursiveTreeObject<DrinkTableRecord> {

    public int id;
    public String DName;
    public int Num;
    public float Tprice;
    public String RoomName;
    public JFXButton EDitBTN = new JFXButton("تعديل");
    public JFXButton DELBTN = new JFXButton("حذف");
    public ObservableList<ArgumentsClass> proarguments;
    public String categ;

    public DrinkTableRecord(int id, String DName, int Num, float Tprice, String RoomName, String proarguments, String categ) {
        this.id = id;
        this.DName = DName;
        this.Num = Num;
        this.Tprice =  HelperMethods.formatNum(Tprice);
        this.RoomName = RoomName;
        this.proarguments = Product.convertStringToListOfArgs(proarguments);
        this.categ = categ;
        btnDecoration();
    }

    public ObservableList<ArgumentsClass> getProarguments() {
        return proarguments;
    }

    public void setProarguments(ObservableList<ArgumentsClass> proarguments) {
        this.proarguments = proarguments;
    }

    public String getCateg() {
        return categ;
    }

    public void setCateg(String categ) {
        this.categ = categ;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDName() {
        return DName;
    }

    public void setDName(String DName) {
        this.DName = DName;
    }

    public int getNum() {
        return Num;
    }

    public void setNum(int Num) {
        this.Num = Num;
    }

    public float getTprice() {
        return Tprice;
    }

    public void setTprice(float Tprice) {
        this.Tprice =  HelperMethods.formatNum(Tprice);
    }

    public String getRoomName() {
        return RoomName;
    }

    public void setRoomName(String RoomName) {
        this.RoomName = RoomName;
    }

    public JFXButton getEDitBTN() {
        return EDitBTN;
    }

    public void setEDitBTN(JFXButton EDitBTN) {
        this.EDitBTN = EDitBTN;
    }

    public JFXButton getDELBTN() {
        return DELBTN;
    }

    public void setDELBTN(JFXButton DELBTN) {
        this.DELBTN = DELBTN;
    }

    public void btnDecoration() {
        FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
        editIcon.setStyleClass("table-btn-icon");
        EDitBTN.setGraphic(editIcon);
        EDitBTN.getStyleClass().add("table-btn");
        EDitBTN.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        FontAwesomeIconView iconTrash = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
        iconTrash.setStyleClass("table-btn-icon");
        DELBTN.setGraphic(iconTrash);
        DELBTN.getStyleClass().add("table-btn");
        DELBTN.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }
}
