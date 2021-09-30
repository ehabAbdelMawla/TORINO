package statistics.details;

import controlpanel.ControlPanel;
import datamodel.DrinkTableRecord;
import datamodel.statisticsRecord;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import statistics.StatisticsController;
import util.Logger;
import util.db.DatabaseHandler;

public class CategoryDetailsController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private JFXTreeTableView<statisticsRecord> myTable;
    @FXML
    private Label categName;
    @FXML
    private TreeTableColumn<statisticsRecord, Double> percentageFromAll;
    @FXML
    private TreeTableColumn<statisticsRecord, Double> percenFromSmall;
    @FXML
    private TreeTableColumn<statisticsRecord, Double> ValCol;
    @FXML
    private TreeTableColumn<statisticsRecord, JFXButton> productCol;
    ObservableList<statisticsRecord> myData = FXCollections.observableArrayList();
    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    public static String nameOfCateg = "";
    public static double valOFCateg = 0;
    public static double tOTalVal = 0;
    @FXML
    private Label valLabel;
    @FXML
    private TextField sTxt;
    @FXML
    private PieChart pieChartOfProducts;
    @FXML
    private TreeTableColumn<statisticsRecord, Long> counCol;
    @FXML
    private ScrollPane SP;

    @Override

    public void initialize(URL url, ResourceBundle rb) {
        categName.setText(nameOfCateg);
        percenFromSmall.setCellValueFactory(new TreeItemPropertyValueFactory<>("percentage"));
        ValCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("totalVal"));
        productCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("categName"));
        percentageFromAll.setCellValueFactory(new TreeItemPropertyValueFactory<>("percentageOFAll"));
        counCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("counter"));
        counCol.setStyle("-fx-alignment: center;");
        percenFromSmall.setStyle("-fx-alignment: center;");
        ValCol.setStyle("-fx-alignment: center;");
        productCol.setStyle("-fx-alignment: center;");
        percentageFromAll.setStyle("-fx-alignment: center;");
        myData.clear();
        if (StatisticsController.fff.equals("") && StatisticsController.ttt.equals("")) {
            myData = joinTwoList(selectDataOfCategFromCafe(nameOfCateg, ""), addRoomsData(nameOfCateg, ""));
            setPercentages(myData);
        } else {
            myData = joinTwoList(selectDataOfCategFromCafe(nameOfCateg, " AND resturantReceets.date BETWEEN '" + StatisticsController.fff + "' AND '" + StatisticsController.ttt + "'"), addRoomsData(nameOfCateg, " WHERE actDate BETWEEN '" + StatisticsController.fff + "'" + " AND '" + StatisticsController.ttt + "'"));
            setPercentages(myData);
        }
        fillTable(myData);
        valLabel.setText(valOFCateg + ControlPanel.getInstance().getCurrency());
        pieChartData.clear();
        for (int i = 0; i < myData.size(); i++) {
            pieChartData.add(new PieChart.Data(myData.get(i).categName.getText(), myData.get(i).totalVal));
        }
        pieChartOfProducts.setData(pieChartData);
        pieChartOfProducts.setTitle("الربح من المنتجات");
        pieChartOfProducts.setClockwise(true);
    }

    public static ObservableList<statisticsRecord> joinTwoList(ObservableList<statisticsRecord> l1, ObservableList<statisticsRecord> l2) {
        ObservableList<statisticsRecord> total = FXCollections.observableArrayList();
        try {
            for (int i = 0; i < l1.size(); i++) {
                total.add(new statisticsRecord(l1.get(i).categName.getText(), l1.get(i).totalVal, 0, 0, l1.get(i).counter, 1));
            }
            int x;
            for (int i = 0; i < l2.size(); i++) {
                x = 0;
                for (int j = 0; j < total.size(); j++) {
                    if (total.get(j).categName.getText().equalsIgnoreCase(l2.get(i).categName.getText())) {
                        total.get(j).totalVal += l2.get(i).totalVal;
                        total.get(j).counter += l2.get(i).counter;
                        x = 1;
                        break;
                    }
                }
                if (x == 0) {
                    statisticsRecord rec = new statisticsRecord(l2.get(i).categName.getText(), l2.get(i).totalVal, 0, 0, l2.get(i).counter, 1);
                    total.add(rec);
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in SectionStatController -> joinTwoList :-" + e);
        }
        return total;
    }

    public void setPercentages(ObservableList<statisticsRecord> ee) {
        int n1 = 0, n2 = 0;
        for (int i = 0; i < ee.size(); i++) {
            n1 = (int) ((ee.get(i).totalVal / valOFCateg) * 10000);
            n2 = (int) ((ee.get(i).totalVal / tOTalVal) * 10000);
            ee.get(i).percentage = n1 / 100.00;
            ee.get(i).percentageOFAll = n2 / 100.00;
        }
    }

    public void fillTable(ObservableList<statisticsRecord> data) {
        ObservableList<statisticsRecord> sers = data;
        TreeItem<statisticsRecord> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        myTable.setRoot(root);
        myTable.setShowRoot(false);
    }

    public static ObservableList<statisticsRecord> selectDataOfCategFromCafe(String categName, String q) {
        ObservableList<statisticsRecord> list = FXCollections.observableArrayList();
        try {

            DatabaseHandler.result = DatabaseHandler.con.prepareStatement("SELECT resturantDetails.productName,SUM(resturantDetails.price*resturantDetails.num),SUM(resturantDetails.num) from resturantDetails JOIN resturantReceets "
                    + "ON resturantDetails.receetId=resturantReceets.receetId AND resturantReceets.daily=0  AND resturantDetails.productCateg='" + categName + "' " + q
                    + " GROUP BY resturantDetails.productName").executeQuery();
            while (DatabaseHandler.result.next()) {
                list.add(new statisticsRecord(DatabaseHandler.result.getString(1), DatabaseHandler.result.getDouble(2), 0, 0, DatabaseHandler.result.getInt(3), 1));
            }

        } catch (Exception e) {
            Logger.writeLog("Exception in SectionStatController -> selectDataOfCategFromCafe :- " + e);
        }
        return list;
    }

    public static ObservableList<statisticsRecord> addRoomsData(String categ, String qq) {
        ObservableList<statisticsRecord> list = FXCollections.observableArrayList();
        list.clear();
        try {
            ObservableList<DrinkTableRecord> allRoomsData = StatisticsController.selectDataFromCafeOfRooms(qq);
            int x;
            for (int i = 0; i < allRoomsData.size(); i++) {
                if (allRoomsData.get(i).RoomName.equalsIgnoreCase(categ)) {
                    x = 0;
                    for (int j = 0; j < list.size(); j++) {
                        if (allRoomsData.get(i).DName.equalsIgnoreCase(list.get(j).categName.getText())) {
                            list.get(j).totalVal += allRoomsData.get(i).Tprice;
                            list.get(j).counter += allRoomsData.get(i).Num;
                            x = 1;
                            break;
                        }
                    }
                    if (x == 0) {
                        list.add(new statisticsRecord(allRoomsData.get(i).DName, allRoomsData.get(i).Tprice, 0, 0, allRoomsData.get(i).Num, 1));
                    }
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in SectionStatController -> addRoomsData :- " + e);
        }
        return list;
    }

    void RootMouseDragged(MouseEvent event) {
        MouseEvent e = (MouseEvent) event;
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setX(e.getScreenX() - xOffset);
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setY(e.getScreenY() - yOffset);
    }

    private void RootMousePressed(Event event) {
        MouseEvent e = (MouseEvent) event;
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    }

    void closeWindow(Event event) {
        ((Stage) ((Node) (event.getSource())).getScene().getWindow()).close();
    }

    @FXML
    private void searchEvent(KeyEvent event) {
        List<statisticsRecord> result = myData.stream() // convert list to stream
                .filter(rec -> rec.categName.getText().contains(sTxt.getText().trim())) // we dont like mkyong
                .collect(Collectors.toList());
        ObservableList<statisticsRecord> rr = FXCollections.observableArrayList();
        rr.addAll(result);
        fillTable(rr);
    }

    @FXML
    private void keyEv(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            closeWindow(event);
        }
    }

    @FXML
    private void scrollHandler(ScrollEvent event) {
        util.gui.HelperMethods.incrementScrollSpeed(event, SP);
    }

}
