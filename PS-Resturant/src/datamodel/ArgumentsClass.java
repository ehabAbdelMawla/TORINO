package datamodel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import util.gui.HelperMethods;
import util.gui.button.TableViewButton;

public class ArgumentsClass extends RecursiveTreeObject<ArgumentsClass> {

    public double numOfArg;
    public String argName;
    public TableViewButton editBtn = new TableViewButton("تعديل", new FontAwesomeIconView(FontAwesomeIcon.EDIT));
    public TableViewButton delBtn = new TableViewButton("حذف", new FontAwesomeIconView(FontAwesomeIcon.TRASH));

    public ArgumentsClass(double numOfArg, String argName) {
        this.numOfArg = HelperMethods.formatNum(numOfArg);
        this.argName = argName;
    }

    public JFXButton getEditBtn() {
        return editBtn;
    }

    public double getNumOfArg() {
        return numOfArg;
    }

    public void setNumOfArg(double numOfArg) {
        this.numOfArg = HelperMethods.formatNum(numOfArg);
    }

    public void setEditBtn(JFXButton editBtn) {
        this.editBtn = (TableViewButton) editBtn;
    }

    public JFXButton getDelBtn() {
        return delBtn;
    }

    public void setDelBtn(JFXButton delBtn) {
        this.delBtn = (TableViewButton) delBtn;
    }

    public String getArgName() {
        return argName;
    }

    public void setArgName(String argName) {
        this.argName = argName;
    }

    @Override
    public String toString() {
        return "ArgumentsClass{" + "numOfArg=" + numOfArg + ", argName=" + argName + '}';
    }

}
