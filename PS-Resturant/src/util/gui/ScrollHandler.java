/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.gui;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;

public class ScrollHandler {

    private static final int DELTA = 4;

    public static void init(Node node) {
        if ((node.getParent() instanceof ScrollPane)) {
            node.setOnScroll((event) -> {
                incrementScrollSpeed(event, ((ScrollPane) node.getParent()));
            });
        }
    }

    private static void incrementScrollSpeed(ScrollEvent event, ScrollPane sp) {
        double deltaY = event.getDeltaY() * DELTA;
        double width = sp.getContent().getBoundsInLocal().getWidth();
        double vvalue = sp.getVvalue();
        sp.setVvalue(vvalue + -deltaY / width);
    }
}
