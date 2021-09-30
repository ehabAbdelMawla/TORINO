package util.gui.button;

import datamodel.InnerOrder;
import datamodel.Product;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import restaurant.hall.TableOrderController;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.load.LoadHelper;

public final class RestaurantTableButton extends VBox {

    private final FontAwesomeIconView freeIcon;
    private final MaterialDesignIconView busyIcon;
    private final Label tableName;
    private boolean free;

    public RestaurantTableButton(String TableNumber, boolean free) {
        this.tableName = new Label(TableNumber);
        this.setAlignment(Pos.CENTER);
        this.setFocusTraversable(false);
        this.setSpacing(10);
        this.free = free;
        this.freeIcon = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
        this.freeIcon.setSize("60");
        this.busyIcon = new MaterialDesignIconView(MaterialDesignIcon.FOOD);
        this.busyIcon.setSize("60");
        this.setOnMouseClicked(openThisTable());
        if (free) {
            setFree();
        } else {
            setBusy();
        }
    }

    public void setFree() {
        this.free = true;
        this.getStyleClass().clear();
        this.getStyleClass().add("free-table-button");
        this.getChildren().setAll(freeIcon, tableName);
    }

    public void setBusy() {
        this.free = false;
        this.getStyleClass().clear();
        this.getStyleClass().add("free-table-button");
        this.getStyleClass().add("busy-table-button");
        this.getChildren().setAll(busyIcon, tableName);
    }

    public boolean isFree() {
        return this.free;
    }

    public String getTableName() {
        return tableName.getText();
    }

    public void setTableName(String tableName) {
        this.tableName.setText(tableName);
    }

    private EventHandler<? super MouseEvent> openThisTable() {
        return e -> {
            try {
                TableOrderController.TableNumber_Label_Static.setText(tableName.getText());
                Product.setCustormerCateg("guest");
                LoadHelper.getInstance().tableOrderController.checkTempCustomer();
                TableOrderController.INNER_ORDERS.setAll(getOrdersOfThisTable());
                LoadHelper.getInstance().tableOrderController.resetAdditionalData();
                Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
                LoadHelper.getInstance().Stage_TableOrder.setX(bounds.getWidth() / 2 - (LoadHelper.getInstance().tableOrderController.AP.getPrefWidth() / 2));
                LoadHelper.getInstance().Stage_TableOrder.setY(bounds.getHeight() / 2 - (LoadHelper.getInstance().tableOrderController.AP.getPrefHeight() / 2));
                LoadHelper.getInstance().Stage_TableOrder.showAndWait();
            } catch (Exception ex) {
                Logger.writeLog("Exception in :" + getClass().getName() + ".openTableOrder() :- " + ex);
            }
        };
    }

    private ObservableList<InnerOrder> getOrdersOfThisTable() {
        ObservableList<InnerOrder> currentOrders = FXCollections.observableArrayList();
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT tableName,drinkName,SUM(numofdr),guestPrice,staffPrice FROM dailyCafesheet WHERE tableName='" + tableName.getText() + "'  GROUP BY drinkName,tableName").executeQuery();
            while (res.next()) {
                currentOrders.add(new InnerOrder(res.getString(2), res.getInt(3), res.getDouble(4), res.getDouble(5)));
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in :" + getClass().getName() + ".openTableOrder() :- " + ex);
        }
        return currentOrders;
    }
}
