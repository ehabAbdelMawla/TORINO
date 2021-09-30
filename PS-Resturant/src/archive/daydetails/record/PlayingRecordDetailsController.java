package archive.daydetails.record;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import datamodel.DrinkTableRecord;
import datamodel.PlayingRecord;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import util.Logger;
import util.db.DatabaseHandler;

public class PlayingRecordDetailsController implements Initializable {

    @FXML
    private JFXTreeTableView<PlayingRecord> playingTable;
    @FXML
    private TreeTableColumn<PlayingRecord, String> DNCol;
    @FXML
    private TreeTableColumn<PlayingRecord, String> FromCol;
    @FXML
    private TreeTableColumn<PlayingRecord, String> TOCol;
    @FXML
    private TreeTableColumn<PlayingRecord, String> kindCol;
    @FXML
    private TreeTableColumn<PlayingRecord, String> playingPriceCol;
    @FXML
    private JFXTreeTableView<DrinkTableRecord> DrinkTabel;
    @FXML
    private TreeTableColumn<DrinkTableRecord, String> DrinknameCol;
    @FXML
    private TreeTableColumn<DrinkTableRecord, Integer> DrinkNumCol;
    @FXML
    private TreeTableColumn<DrinkTableRecord, Float> DrinkPriceCol;
    public static String AllPlaying = "";
    public static String AllDrinks = "";
    public static String PlayingNotes = "";
    public static String DrinksNotes = "";
    @FXML
    private Text PlayTxtArea;
    @FXML
    private Text cafeTxtArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DrinknameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("DName"));
        DrinkNumCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("Num"));
        DrinkPriceCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("Tprice"));
        DNCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("deviceName"));
        FromCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("fromTime"));
        TOCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("toTime"));
        kindCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("playingStatus"));
        playingPriceCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("playingPrice"));
        util.gui.HelperMethods.TableColumnAlignment(DrinknameCol, DrinkNumCol, DrinkPriceCol, DNCol, FromCol, TOCol, kindCol, playingPriceCol);
    }

    public void setData(String drink, String playing, String drinkNotes, String playingNotes) {
        PlayingRecordDetailsController.AllDrinks = drink;
        PlayingRecordDetailsController.AllPlaying = playing;
        PlayingRecordDetailsController.DrinksNotes = drinkNotes;
        PlayingRecordDetailsController.PlayingNotes = playingNotes;
        fillDrinkTable();
        fillPlayingTable();
        PlayTxtArea.setText(PlayingNotes.replaceAll("#", "\n"));
        cafeTxtArea.setText(DrinksNotes.replaceAll("#", "\n"));
    }

    public ObservableList<DrinkTableRecord> workOnAllDrink() {
        ObservableList<DrinkTableRecord> list = FXCollections.observableArrayList();
        try {
            if (!AllDrinks.equals("")) {
                String[] records = AllDrinks.split("#");
                for (String record : records) {
                    String[] innerdata = record.split("@");
                    list.add(new DrinkTableRecord(
                            Integer.parseInt(innerdata[0]),
                            innerdata[2],
                            Integer.parseInt(innerdata[1]),
                            Float.parseFloat(innerdata[3]), "!", "", ""));
                }
            }
        } catch (NumberFormatException e) {
            Logger.writeLog("Exception in workOnAllDrink : " + e);
        }
        return list;
    }

    public static String getCategNameFromDrinkName(String DwN) {
        try {
            String sql = "SELECT category FROM drinkprice WHERE drinkName=?";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setString(1, DwN);
            DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
            String s;
            while (DatabaseHandler.result.next()) {
                s = DatabaseHandler.result.getString(1);
                return s;
            }

        } catch (SQLException e) {
            Logger.writeLog("Exception in " + PlayingRecordDetailsController.class.getName() + " getCategNameFromDrinkName: " + e);
        }
        return "";
    }

    public void fillDrinkTable() {
        ObservableList<DrinkTableRecord> sers = workOnAllDrink();
        TreeItem<DrinkTableRecord> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        DrinkTabel.setRoot(root);
        DrinkTabel.setShowRoot(false);
    }

    public ObservableList<PlayingRecord> workOnAllPlaying() {
        ObservableList<PlayingRecord> list = FXCollections.observableArrayList();
        try {
            if (!AllPlaying.equals("")) {
                String[] records = AllPlaying.split("#");
                for (String record : records) {
                    String[] innerdata = record.split("@");
                    list.add(new PlayingRecord(0, innerdata[0],
                            innerdata[1],
                            innerdata[2],
                            innerdata[3],
                            innerdata[4], 0, "!"));
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in workOnAllPlaying: " + e);
        }
        return list;
    }

    public void fillPlayingTable() {
        ObservableList<PlayingRecord> sers = workOnAllPlaying();
        TreeItem<PlayingRecord> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        playingTable.setRoot(root);
        playingTable.setShowRoot(false);
    }

    @FXML
    private void closeWindow(Event event) {
        ((Stage) ((Node) (event.getSource())).getScene().getWindow()).close();
    }

    @FXML
    private void KeyReleasedAction(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            closeWindow((Event) event);
        }
    }
}
