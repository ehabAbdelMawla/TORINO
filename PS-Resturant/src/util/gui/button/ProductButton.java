/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.gui.button;

import com.jfoenix.controls.JFXButton;
import datamodel.Product;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Bayoumi
 */
public class ProductButton extends JFXButton {

    private Product product;

    public ProductButton(Product product) {
        this.product = product;
        this.setText(product.getName());
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

    public Product getProduct() {
        return product;
    }
}
