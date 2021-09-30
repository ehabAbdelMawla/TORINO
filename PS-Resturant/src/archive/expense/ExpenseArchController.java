package archive.expense;

import controlpanel.ControlPanel;
import datamodel.ExpenseRecord;
import dialog.SpacificTime.SpacificTimeController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import util.gui.load.Location;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.BuilderUI;
import util.gui.load.LoadHelper;

public class ExpenseArchController implements Initializable {

    public static String from = "";
    public static String to = "";
    public double summition = 0;
    private ObservableList<ExpenseRecord> Data;

    @FXML
    private JFXTreeTableView<ExpenseRecord> myTable;
    @FXML
    private TreeTableColumn<ExpenseRecord, Float> priceCol;
    @FXML
    private TreeTableColumn<ExpenseRecord, String> needCol;
    @FXML
    private TreeTableColumn<ExpenseRecord, String> UsCol;
    @FXML
    private TreeTableColumn<ExpenseRecord, String> DateCol;
    @FXML
    private TextField searchTxt;
    @FXML
    private Label totalLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        priceCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("price"));
        needCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("need"));
        UsCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("userName"));
        DateCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("date"));
        priceCol.setStyle("-fx-alignment: center;");
        needCol.setStyle("-fx-alignment: center;");
        UsCol.setStyle("-fx-alignment: center;");
        DateCol.setStyle("-fx-alignment: center;");
        Data = selectExpenseData("");
        fillEXTable(Data);
    }

    @FXML
    private void goToSpacificTime(Event event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(Location.getInstance().get("SPACIFICTIMEVIEW")));
            Scene scene = new Scene(root);
            Stage window = BuilderUI.initStageUnDecorated(scene, null, "");
            window.showAndWait();
            from = SpacificTimeController.f;
            to = SpacificTimeController.t;
            if ((!from.equals("")) || (!to.equals(""))) {
                Data = selectExpenseData(" and currentdate BETWEEN '" + from + "' AND '" + to + "' ");
                fillEXTable(Data);
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in  " + getClass().getName() + " -> goToSpacificTime:-" + ex);
        }
    }

    @FXML
    private void SearchAvtiom(KeyEvent event) {
        if (!searchTxt.getText().trim().equals("")) {
            ObservableList<ExpenseRecord> filtered = FXCollections.observableArrayList();
            List<ExpenseRecord> result = Data.stream()
                    .filter(rec -> rec.need.contains(searchTxt.getText().trim()))
                    .collect(Collectors.toList());
            filtered.addAll(result);
            fillEXTable(filtered);
            return;
        }
        fillEXTable(Data);
    }

    public ObservableList<ExpenseRecord> selectExpenseData(String s) {
        ObservableList<ExpenseRecord> list = FXCollections.observableArrayList();
        try {
            list.clear();
            String sqlState = "SELECT * FROM dailyexpense WHERE daily=0 " + s + " ORDER BY currentdate ASC";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();
            int id;
            float price;
            String date, uN, need;
            ExpenseRecord rec;
            while (DatabaseHandler.result.next()) {
                id = DatabaseHandler.result.getInt(1);
                date = DatabaseHandler.result.getString(2);
                uN = DatabaseHandler.result.getString(3);
                need = DatabaseHandler.result.getString(4);
                price = DatabaseHandler.result.getFloat(5);
                rec = new ExpenseRecord(
                        id,
                        date,
                        uN,
                        need,
                        price
                );
                list.add(rec);
            }
            DatabaseHandler.result.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Exception In ExpensesController -> selectExpenseData:- " + e);
            return list;
        }
    }

    public void fillEXTable(ObservableList<ExpenseRecord> data) {
        ObservableList<ExpenseRecord> sers = data;
        TreeItem<ExpenseRecord> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        myTable.setRoot(root);
        myTable.setShowRoot(false);
        summition = 0;
        for (int i = 0; i < data.size(); i++) {
            summition += data.get(i).price;
        }
        totalLabel.setText(summition + ControlPanel.getInstance().getCurrency());
    }

    @FXML
    private void ShowAllAction(ActionEvent event) {
        Data = selectExpenseData("");
        fillEXTable(Data);
    }

    @FXML
    private void goToArchive(Event event) {
        homepage.HomePageController.title_Label.setText("الأرشيف");
        homepage.HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_ArchiveHome"));
    }

    @FXML
    private void keyActioN(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            goToArchive(event);
        }
    }
}
