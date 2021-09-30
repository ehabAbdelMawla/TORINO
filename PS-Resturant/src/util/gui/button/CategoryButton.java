package util.gui.button;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;
import util.db.DatabaseHandler;

public class CategoryButton extends JFXButton {

    public static ObservableList<String> ALL_CATEGORIES = FXCollections.observableArrayList();

    public CategoryButton(String CategoryName) {
        this.setText(CategoryName);
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add("category-button-normal");
        this.setFocusTraversable(false);
        this.setWrapText(true);
        this.setTextAlignment(TextAlignment.CENTER);
    }

    public void setFocus() {
        this.getStyleClass().clear();
        this.getStyleClass().add("category-button-focus");
    }

    public void delFocus() {
        this.getStyleClass().clear();
        this.getStyleClass().add("category-button-normal");
    }

    public static void getAllCategories() {
        ALL_CATEGORIES.setAll(DatabaseHandler.selectCategotiesNames(""));
    }

}
