package datamodel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import util.gui.button.TableViewButton;

public class CategRecord extends RecursiveTreeObject<CategRecord> {

    public int id;
    public String categName;

    public TableViewButton edButton = new TableViewButton("تعديل", new FontAwesomeIconView(FontAwesomeIcon.EDIT));
    public TableViewButton delButton = new TableViewButton("حذف", new FontAwesomeIconView(FontAwesomeIcon.TRASH));

    public CategRecord(int id, String categName) {
        this.id = id;
        this.categName = categName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategName() {
        return categName;
    }

    public void setCategName(String categName) {
        this.categName = categName;
    }

    public JFXButton getEdButton() {
        return edButton;
    }

    public void setEdButton(JFXButton edButton) {
        this.edButton = (TableViewButton) edButton;
    }

    public JFXButton getDelButton() {
        return delButton;
    }

    public void setDelButton(JFXButton delButton) {
        this.delButton = (TableViewButton) delButton;
    }

}
