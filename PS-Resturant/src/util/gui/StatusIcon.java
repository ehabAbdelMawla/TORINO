/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.gui;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import util.gui.button.TableViewButton;

/**
 *
 * @author Bayoumi
 */
public class StatusIcon extends TableViewButton {

    public enum StatusType {
        Success, Returned, None
    }

    private final StatusType type;

    public StatusIcon(StatusType type) {
        super();
        this.type = type;

        FontAwesomeIconView icon;
        switch (type) {
            case Success:
                icon = new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE);
                icon.setStyle("-fx-fill:green;");
                break;
            case Returned:
                icon = new FontAwesomeIconView(FontAwesomeIcon.CLOSE);
                icon.setStyle("-fx-fill:red;");
                break;
            default:
                icon = null;
                setDisable(true);
                break;
        }
        setGraphic(icon);
        makeIconLarge();
    }

    private void makeIconLarge() {
        if (this.getStyleClass().contains("table-btn")) {
            this.getStyleClass().remove("table-btn");
        }
        this.getStyleClass().add("table-btn-l");
    }

    public boolean isReturned() {
        return type.equals(StatusType.Returned);
    }

    public boolean isSuccess() {
        return type.equals(StatusType.Success);
    }

    public StatusType getType() {
        return type;
    }

}
