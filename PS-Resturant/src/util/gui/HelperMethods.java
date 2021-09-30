/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.gui;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import util.Logger;

/**
 *
 * @author Bayoumi
 */
public class HelperMethods {

    private static final String CLASSNAME = HelperMethods.class.getName();

    //===== Method To Set Column Data center alignment =====
    public static void TableColumnAlignment(TreeTableColumn... cols) {
        try {
            for (TreeTableColumn col : cols) {
                col.setStyle("-fx-alignment: center;");
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  TableColumnAlignment()");
        }
    }

    //Reset Compobox
    public static void ResetComboBox(JFXComboBox... coms) {
        try {
            for (JFXComboBox com : coms) {
                com.setValue(null);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  ResetComboBox()");
        }
    }

    public static void setDisableCheckBoxs(boolean isDisabled,CheckBox... checkBoxs) {
        try {
            for (CheckBox checkBox : checkBoxs) {
                checkBox.setDisable(isDisabled);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + ".disableCheckBoxs() => " + e);
        }
    }

    public static void unSelectCheckBoxs(CheckBox... checkBoxs) {
        try {
            for (CheckBox checkBox : checkBoxs) {
                checkBox.setSelected(false);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + ".unSelectCheckBoxs() => " + e);
        }
    }

    //===== Reset TextField =====
    public static void ResetTexts(TextField... texts) {
        try {
            for (TextField txt : texts) {
                txt.setText("");
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  ResetTexts()");
        }
    }

    public static void resetEmptyField(String resetValue, TextField... textFields) {
        try {
            for (TextField field : textFields) {
                field.setText(resetValue);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  resetEmptyField()");
        }
    }

    //===== Reset TextField =====
    public static void resetEmptyLabel(String resetValue, Label... labels) {
        try {
            for (Label label : labels) {
                label.setText(resetValue);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  resetEmptyLabel()");
        }
    }

    //=====set Table Data=====
    public static <T extends RecursiveTreeObject<T>> void SetTableData(JFXTreeTableView<T> Table, boolean rootShow, boolean Edit, ObservableList<T> data) {
        try {
            TreeItem<T> root = new RecursiveTreeItem<>(data, RecursiveTreeObject::getChildren);
            Table.setRoot(root);
            Table.setShowRoot(rootShow);
            Table.setEditable(Edit);
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  SetTableData()");
        }
    }

    //    Number Formatt Methods
    public static double formatNum(double num) {
        double returnedVal = Double.parseDouble(String.format("%.2f", num));
        return Math.abs(returnedVal) == 0 ? 0 : returnedVal;
    }

    public static float formatNum(float num) {
        float returnedVal = Float.parseFloat(String.format("%.2f", num));
        return Math.abs(returnedVal) == 0 ? 0 : returnedVal;
    }

    public static void incrementScrollSpeed(ScrollEvent event, ScrollPane sp) {
        double deltaY = event.getDeltaY() * 4;
        double width = sp.getContent().getBoundsInLocal().getWidth();
        double vvalue = sp.getVvalue();
        sp.setVvalue(vvalue + -deltaY / width);
    }

    public static void setRelativeSize(Region parentNode, double factor, double defaultWidth, double defaultHeight) {
        parentNode.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.windowProperty().addListener((observable, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
                        double w = bounds.getWidth() * factor;
                        double h = bounds.getHeight() * factor;
                        parentNode.setPrefSize(w < defaultWidth ? defaultWidth : w, h < defaultHeight ? defaultHeight : h);
                    }
                });
            }
        });
    }

    public static void setStageListener(Node node, Callback<Stage, Void> callback) {
        node.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.windowProperty().addListener((observable, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        callback.call((Stage) newWindow);
                    }
                });
            }
        });
    }

}
