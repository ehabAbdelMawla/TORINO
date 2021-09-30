package dialog.edit.ingredient;

import com.jfoenix.controls.JFXTextField;
import datamodel.ArgumentsClass;
import datamodel.Product;
import datamodel.StoreRecord;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import static settings.products.NewProductController.convertArgsListToString;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.load.DialogHelper;

public class EditIngredientController implements Initializable {

    private StoreRecord temp;
    public int editNotation;
    @FXML
    private JFXTextField myTxt;
    private double xOffset = 0;
    private double yOffset = 0;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        editNotation = 0;
    }

    public void initData(StoreRecord temp) {
        this.temp = temp;
        myTxt.setText(temp.productName);
    }

    @FXML
    private void edeitNameAction(Event event) {
        try {
            if (myTxt.getText().trim().equals("")) {
                DialogHelper.getInstance().showOKAlert("- يجب ادخال جميع البيانات");
                return;
            }
            if (!myTxt.getText().trim().equals(temp.productName)) {
                String newName = myTxt.getText().trim();
                String sql = "UPDATE store SET Product=? WHERE Product=?";
                DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                DatabaseHandler.stat.setString(1, newName);
                DatabaseHandler.stat.setString(2, temp.productName);
                try {
                    DatabaseHandler.stat.execute();
                } catch (Exception e) {
                    DialogHelper.getInstance().showOKAlert("هذا الاسم موجود بالفعل ");
                    return;
                }
                sql = "UPDATE storeenter SET Product=? WHERE Product=?";
                DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                DatabaseHandler.stat.setString(1, newName);
                DatabaseHandler.stat.setString(2, temp.productName);
                DatabaseHandler.stat.execute();

                sql = "UPDATE storeexit SET Product=? WHERE Product=?";
                DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                DatabaseHandler.stat.setString(1, newName);
                DatabaseHandler.stat.setString(2, temp.productName);
                DatabaseHandler.stat.execute();

                sql = "UPDATE archpullstore SET argName=? WHERE argName=?";
                DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                DatabaseHandler.stat.setString(1, newName);
                DatabaseHandler.stat.setString(2, temp.productName);
                DatabaseHandler.stat.execute();

                sql = "UPDATE dailypullstore SET argName=? WHERE argName=?";
                DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                DatabaseHandler.stat.setString(1, newName);
                DatabaseHandler.stat.setString(2, temp.productName);
                DatabaseHandler.stat.execute();

                editINDrinkPriceTable(newName, temp.productName);
                editINRoom1Drink(newName, temp.productName);
                editINDailyCafeSheetTable(newName, temp.productName);
                editNotation = 1;
            }
            closeWindow(event);
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> edeitNameAction :-" + e);
        }
    }

    private void editINDailyCafeSheetTable(String newName, String prevName) {
        try {
            ObservableList<ArgumentsClass> argsList;
            int i = 0, foundNotation = 0;
            String sql = "SELECT receetId,productArgs FROM resturantDetails";

            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (DatabaseHandler.result.next()) {
                i = 0;
                foundNotation = 0;
                argsList = Product.convertStringToListOfArgs(DatabaseHandler.result.getString(2));
                for (i = 0; i < argsList.size(); i++) {
                    if (argsList.get(i).argName.equalsIgnoreCase(prevName)) {
                        argsList.get(i).argName = newName;
                        foundNotation = 1;
                        break;
                    }
                }
                if (foundNotation == 1) {
                    sql = "UPDATE dailycafesheet SET productArgs=? WHERE receetId=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                    DatabaseHandler.stat.setString(1, convertArgsListToString(argsList));
                    DatabaseHandler.stat.setInt(2, DatabaseHandler.result.getInt(1));
                    DatabaseHandler.stat.execute();
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> editINDailyCafeSheetTable :-" + e);
        }
    }

    private void editINRoom1Drink(String newName, String prevName) {
        try {
            ObservableList<ArgumentsClass> argsList;
            int i = 0, foundNotation = 0;
            String sql = "SELECT id,proArgs FROM room1drink";

            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (DatabaseHandler.result.next()) {
                i = 0;
                foundNotation = 0;
                argsList = Product.convertStringToListOfArgs(DatabaseHandler.result.getString(2));
                for (i = 0; i < argsList.size(); i++) {
                    if (argsList.get(i).argName.equalsIgnoreCase(prevName)) {
                        argsList.get(i).argName = newName;
                        foundNotation = 1;
                        break;
                    }
                }
                if (foundNotation == 1) {
                    sql = "UPDATE room1drink SET proArgs=? WHERE id=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                    DatabaseHandler.stat.setString(1, convertArgsListToString(argsList));
                    DatabaseHandler.stat.setInt(2, DatabaseHandler.result.getInt(1));
                    DatabaseHandler.stat.execute();
                }
            }

        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> editINRoom1Drink :-" + e);
        }
    }

    private void editINDrinkPriceTable(String newName, String prevName) {
        try {
            ObservableList<ArgumentsClass> argsList;
            int i = 0, foundNotation = 0;
            String sql = "SELECT drinkName,args FROM drinkprice";

            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (DatabaseHandler.result.next()) {
                i = 0;
                foundNotation = 0;
                argsList = Product.convertStringToListOfArgs(DatabaseHandler.result.getString(2));
                for (i = 0; i < argsList.size(); i++) {
                    if (argsList.get(i).argName.equalsIgnoreCase(prevName)) {
                        argsList.get(i).argName = newName;
                        foundNotation = 1;
                        break;
                    }
                }
                if (foundNotation == 1) {
                    sql = "UPDATE drinkprice SET args=? WHERE drinkName=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                    DatabaseHandler.stat.setString(1, convertArgsListToString(argsList));
                    DatabaseHandler.stat.setString(2, DatabaseHandler.result.getString(1));
                    DatabaseHandler.stat.execute();
                }
            }

        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> editINDrinkPriceTable :-" + e);
        }
    }

    @FXML
    private void closeWindow(Event event) {
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).close();
    }

    @FXML
    private void RootMouseDragged(MouseEvent event) {
        MouseEvent e = (MouseEvent) event;
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setX(e.getScreenX() - xOffset);
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setY(e.getScreenY() - yOffset);
    }

    @FXML
    private void RootMousePressed(MouseEvent event) {
        MouseEvent e = (MouseEvent) event;
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    }

    @FXML
    private void KEVENT(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            edeitNameAction(event);
        } else if (event.getCode().equals(KeyCode.ESCAPE)) {
            closeWindow(event);
        }
    }

}
