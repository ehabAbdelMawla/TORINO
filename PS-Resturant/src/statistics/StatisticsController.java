package statistics;

import statistics.details.CategoryDetailsController;
import controlpanel.ControlPanel;
import datamodel.DrinkTableRecord;
import datamodel.caefSettingsRcordForPrint;
import datamodel.statisticsRecord;
import datamodel.statisticsRecordForPrint;
import dialog.SpacificTime.SpacificTimeController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import util.gui.load.Location;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import static statistics.details.CategoryDetailsController.addRoomsData;
import static statistics.details.CategoryDetailsController.joinTwoList;
import static statistics.details.CategoryDetailsController.selectDataOfCategFromCafe;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.load.DialogHelper;
import util.printing.PrintingData;

public class StatisticsController implements Initializable {

    // ==== GUI Objects ====
    @FXML
    private Label totalLabel;
    @FXML
    private JFXTreeTableView<statisticsRecord> myTable;
    @FXML
    private TreeTableColumn<statisticsRecord, Double> perCol;
    @FXML
    private TreeTableColumn<statisticsRecord, Double> valCol;
    @FXML
    private TreeTableColumn<statisticsRecord, JFXButton> categCol;
    @FXML
    private TextField sTxt;
    @FXML
    private PieChart piChartOfCateg;
    @FXML
    private NumberAxis YAx;
    @FXML
    private CategoryAxis xAx;
    @FXML
    private AreaChart<String, Double> LineChartOdCateg;
    @FXML
    private JFXButton pieChartBTN;
    @FXML
    private JFXButton lineChartBTN;
    @FXML
    private JFXButton tableBTN;
    // ===
    public static String fff = "";
    public static String ttt = "";
    private static double totalLabel_Static;
    private ObservableList<statisticsRecord> statisticsRecords;
    private ObservableList<PieChart.Data> PIE_CHART_DATA;
    private XYChart.Series<String, Double> SERIES;
    private JFXButton currentBTN;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PIE_CHART_DATA = FXCollections.observableArrayList();
        SERIES = new XYChart.Series<>();
        SERIES.setName("مؤشر الربح من الفئات");
        YAx.setLabel("الربح");
        xAx.setLabel("الفئة");
        LineChartOdCateg.setCreateSymbols(true);
        LineChartOdCateg.setAnimated(false);
        piChartOfCateg.setTitle("الربح من الفئات");
        piChartOfCateg.setClockwise(true);
        perCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("percentage"));
        valCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("totalVal"));
        categCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("categName"));
        util.gui.HelperMethods.TableColumnAlignment(perCol, valCol, categCol);
        statisticsRecords = addingRoomsCafeData(selectDataFromCafe(""), convertedRoomsData(selectDataFromCafeOfRooms("")));
        totalLabel_Static = getSumition(statisticsRecords);
        totalLabel.setText(totalLabel_Static + ControlPanel.getInstance().getCurrency());
        setPercentages(statisticsRecords);
        fillStatisticTable(statisticsRecords);
        fillCharts(statisticsRecords);
        addActions(statisticsRecords);

        currentBTN = tableBTN;
    }

    public void setPercentages(ObservableList<statisticsRecord> ss) {
        int n = 0;
        for (int i = 0; i < ss.size(); i++) {
            n = (int) ((ss.get(i).totalVal / totalLabel_Static) * 10000);
            ss.get(i).percentage = n / 100.00;
        }
    }

    public double getSumition(ObservableList<statisticsRecord> ss) {
        double sum = 0;
        for (int i = 0; i < ss.size(); i++) {
            sum += ss.get(i).totalVal;
        }
        return sum;
    }

    public ObservableList<statisticsRecord> selectDataFromCafe(String condition) {
        ObservableList<statisticsRecord> list = FXCollections.observableArrayList();
        try {
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement("SELECT resturantDetails.productCateg,SUM(resturantDetails.price*resturantDetails.num) from resturantDetails JOIN resturantReceets "
                    + "ON resturantDetails.receetId=resturantReceets.receetId AND resturantReceets.daily=0 " + condition
                    + "GROUP BY resturantDetails.productCateg").executeQuery();
            statisticsRecord rec;
            while (DatabaseHandler.result.next()) {
                rec = new statisticsRecord(DatabaseHandler.result.getString(1), DatabaseHandler.result.getDouble(2), 0, 0, 0, 1);
                list.add(rec);
            }

        } catch (Exception e) {
            Logger.writeLog("EXception in " + getClass().getName() + " -> selectDataFromCafe :- " + e);
        }
        return list;
    }

    public static ObservableList<DrinkTableRecord> selectDataFromCafeOfRooms(String ss) {
        ObservableList<DrinkTableRecord> list = FXCollections.observableArrayList();
        try {
            String sql = "SELECT drink FROM dailysheet " + ss;
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            String s = "";
            String[] record;
            while (DatabaseHandler.result.next()) {
                s = DatabaseHandler.result.getString(1);
                if (!s.equals("")) {
                    record = s.split("#");
                    for (int i = 0; i < record.length; i++) {
                        String[] innerdata = record[i].split("@");
                        list.add(new DrinkTableRecord(
                                Integer.parseInt(innerdata[0]),
                                innerdata[2],
                                Integer.parseInt(innerdata[1]),
                                Float.parseFloat(innerdata[3]), innerdata[4], "", ""));
                    }
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + StatisticsController.class.getName() + " -> selectDataFromCafeOfRooms :- " + e);
        }
        return list;
    }

    public static ObservableList<statisticsRecord> convertedRoomsData(ObservableList<DrinkTableRecord> roomData) {
        ObservableList<statisticsRecord> list = FXCollections.observableArrayList();
        try {
            int x;
            DrinkTableRecord rec;
            for (int i = 0; i < roomData.size(); i++) {
                x = 0;
                rec = roomData.get(i);
                for (int j = 0; j < list.size(); j++) {
                    if (rec.RoomName.equalsIgnoreCase(list.get(j).categName.getText())) {
                        list.get(j).totalVal += rec.Tprice;
                        x = 1;
                        break;
                    }
                }
                if (x == 0) {
                    list.add(new statisticsRecord(roomData.get(i).RoomName, roomData.get(i).Tprice, 0, 0, 0, 1));
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + StatisticsController.class.getName() + " ->convertRoomsData :-" + e);
        }
        return list;
    }

    public ObservableList<statisticsRecord> addingRoomsCafeData(ObservableList<statisticsRecord> l1, ObservableList<statisticsRecord> l2) {
        ObservableList<statisticsRecord> total = FXCollections.observableArrayList();
        try {
            for (int i = 0; i < l1.size(); i++) {
                total.add(new statisticsRecord(l1.get(i).categName.getText(), l1.get(i).totalVal, 0, 0, 0, 0));
            }
            int x;
            for (int i = 0; i < l2.size(); i++) {
                x = 0;
                for (int j = 0; j < total.size(); j++) {
                    if (total.get(j).categName.getText().equalsIgnoreCase(l2.get(i).categName.getText())) {
                        total.get(j).totalVal += l2.get(i).totalVal;
                        x = 1;
                        break;
                    }
                }
                if (x == 0) {
                    statisticsRecord rec = new statisticsRecord(l2.get(i).categName.getText(), l2.get(i).totalVal, 0, 0, 0, 0);
                    total.add(rec);
                }
            }
            for (int i = 0; i < total.size(); i++) {
                total.get(i).categName.setOnAction(ShowCategDetails(total.get(i)));
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> addingRoomsCafeData :-" + e);
        }
        return total;
    }

    public void fillStatisticTable(ObservableList<statisticsRecord> dd) {
        ObservableList<statisticsRecord> sers = dd;
        TreeItem<statisticsRecord> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        myTable.setRoot(root);
        myTable.setShowRoot(false);
    }

    @FXML
    private void goToSpacificTime() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(Location.getInstance().get("SPACIFICTIMEVIEW")));
            Scene scene = new Scene(root);
            Stage window = util.gui.BuilderUI.initStageUnDecorated(scene, "Select Specific Time", "info");
            window.showAndWait();
            String from = SpacificTimeController.f;
            String to = SpacificTimeController.t;
            if ((!from.equals("")) || (!to.equals(""))) {
                fff = from;
                ttt = to;
                statisticsRecords = addingRoomsCafeData(selectDataFromCafe(" AND  resturantReceets.date BETWEEN '" + from + "'" + " AND '" + to + "'"), convertedRoomsData(selectDataFromCafeOfRooms(" WHERE actDate BETWEEN '" + from + "'" + " AND '" + to + "'")));
                totalLabel_Static = getSumition(statisticsRecords);
                totalLabel.setText(totalLabel_Static + ControlPanel.getInstance().getCurrency());
                setPercentages(statisticsRecords);
                fillStatisticTable(statisticsRecords);
                fillCharts(statisticsRecords);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> goToSpacificTime" + e);
        }
    }

    public void fillCharts(ObservableList<statisticsRecord> dd) {
        PIE_CHART_DATA.clear();
        SERIES.getData().clear();
        LineChartOdCateg.getData().clear();
        for (int i = 0; i < dd.size(); i++) {
            PIE_CHART_DATA.add(new PieChart.Data(dd.get(i).categName.getText(), dd.get(i).totalVal));
            SERIES.getData().add(new XYChart.Data<>(dd.get(i).categName.getText(), dd.get(i).totalVal));
        }
        LineChartOdCateg.getData().add(SERIES);
        piChartOfCateg.setData(PIE_CHART_DATA);
    }

    @FXML
    private void searchEvent() {
        if (!sTxt.getText().trim().equalsIgnoreCase("")) {
            List<statisticsRecord> result = statisticsRecords.stream()
                    .filter(rec -> rec.categName.getText().contains(sTxt.getText().trim()))
                    .collect(Collectors.toList());
            ObservableList<statisticsRecord> rr = FXCollections.observableArrayList();
            rr.addAll(result);
            fillStatisticTable(rr);
            return;
        }
        fillStatisticTable(statisticsRecords);
    }

    public EventHandler<ActionEvent> ShowCategDetails(statisticsRecord rec) {
        return (ActionEvent event) -> {
            try {
                CategoryDetailsController.nameOfCateg = rec.categName.getText();
                CategoryDetailsController.valOFCateg = rec.totalVal;
                CategoryDetailsController.tOTalVal = totalLabel_Static;
                Parent root = FXMLLoader.load(getClass().getResource(Location.getInstance().get("STATISTICS_CATEGORY_DETAILS")));
                Scene sc = new Scene(root);
                Stage s = util.gui.BuilderUI.initStageDecorated(sc, "Details", "info");
                s.showAndWait();
            } catch (Exception e) {
                Logger.writeLog("Exception in " + getClass().getName() + " -> ShowCategDetails :- " + e);
            }
        };
    }

    public void addActions(ObservableList<statisticsRecord> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).categName.setOnAction(ShowCategDetails(list.get(i)));
        }
    }

    @FXML
    private void ShowAllAction() {
        fff = "";
        ttt = "";
        statisticsRecords = addingRoomsCafeData(selectDataFromCafe(""), convertedRoomsData(selectDataFromCafeOfRooms("")));
        totalLabel_Static = getSumition(statisticsRecords);
        totalLabel.setText(totalLabel_Static + ControlPanel.getInstance().getCurrency());
        setPercentages(statisticsRecords);
        fillStatisticTable(statisticsRecords);
        fillCharts(statisticsRecords);
        addActions(statisticsRecords);
    }

    @FXML
    private void CreatePDF() {
        new Thread() {
            @Override
            public void run() {
                ShowPdf("reports/StatisticsReport");
            }
        }.start();
    }

    synchronized public void ShowPdf(String fileName) {
        try {
            ObservableList<caefSettingsRcordForPrint> listItems;
            String fromDate, toDate;
            if (fff.equals("") && ttt.equals("")) {
                String sql = "SELECT MIN(actDate) FROM dailysheet where daily=0", f = "";
                DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
                while (DatabaseHandler.result.next()) {
                    f = DatabaseHandler.result.getString(1);
                }
                fromDate = f;
                toDate = LocalDate.now().toString();
            } else {
                fromDate = fff;
                toDate = ttt;
            }
            listItems = getcafeStattisticsData(statisticsRecords);

            if (listItems.isEmpty()) {
                Platform.runLater(() -> {
                    DialogHelper.getInstance().showOKAlert("لا يوجد بيانات لعرضها");
                });

                return;
            }
            String outputFile = "reports/Statistics.pdf";
            JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(listItems, false);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("cafeStatisticsData", itemsJRBean);
            parameters.put("fromDate", fromDate);
            parameters.put("toDate", toDate);
            PrintingData.getInstance().setShopParameters(parameters);
            JasperReport jr = JasperCompileManager.compileReport(fileName + ".jrxml");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jr, parameters, new JREmptyDataSource());
            OutputStream outputStream = new FileOutputStream(new File(outputFile));
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            Desktop.getDesktop().open(new File(outputFile));
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> ShowPdf :- " + e);
        }
    }

    public ObservableList<caefSettingsRcordForPrint> getcafeStattisticsData(ObservableList<statisticsRecord> categList) {
        ObservableList<caefSettingsRcordForPrint> printData = FXCollections.observableArrayList();
        try {
            String s0 = "", s2 = "", c;
            if (!StatisticsController.fff.equals("") && !StatisticsController.ttt.equals("")) {
                s0 = " AND actDate BETWEEN '" + StatisticsController.fff + "' AND '" + StatisticsController.ttt + "'";

                s2 = " WHERE actDate BETWEEN '" + StatisticsController.fff + "'" + " AND '" + StatisticsController.ttt + "'";
            }
            for (int i = 0; i < categList.size(); i++) {
                c = categList.get(i).categName.getText();
                printData.add(new caefSettingsRcordForPrint(c, prepareInnerCategDataFrPrint(joinTwoList(selectDataOfCategFromCafe(c, s0), addRoomsData(c, s2)))));
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> getcafeStattisticsData :- " + e);
        }
        return printData;
    }

    public List<statisticsRecordForPrint> prepareInnerCategDataFrPrint(ObservableList<statisticsRecord> prevForm) {
        List<statisticsRecordForPrint> newList = new ArrayList<>();
        try {
            for (int i = 0; i < prevForm.size(); i++) {
                newList.add(new statisticsRecordForPrint(prevForm.get(i).categName.getText(), prevForm.get(i).counter, prevForm.get(i).totalVal));
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> prepareInnerCategDataFrPrint : " + e);
        }
        return newList;
    }

    @FXML
    private void goToPieChart() {
        toggleBTN(pieChartBTN);
        piChartOfCateg.setVisible(true);
        LineChartOdCateg.setVisible(false);
        myTable.setVisible(false);
    }

    @FXML
    private void goToLineChart() {
        toggleBTN(lineChartBTN);
        LineChartOdCateg.setVisible(true);
        myTable.setVisible(false);
        piChartOfCateg.setVisible(false);
    }

    @FXML
    private void goToTable() {
        toggleBTN(tableBTN);
        myTable.setVisible(true);
        LineChartOdCateg.setVisible(false);
        piChartOfCateg.setVisible(false);
    }

    private void toggleBTN(JFXButton b) {
        currentBTN.getStyleClass().remove("dark-button");
        currentBTN = b;
        currentBTN.getStyleClass().add("dark-button");
    }
}
