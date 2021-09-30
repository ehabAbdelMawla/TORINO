package archive;

import archive.daydetails.ArchiveDayDetailsController;
import controlpanel.ControlPanel;
import datamodel.ArchOfDays;
import datamodel.ArchieveReport;
import datamodel.DailySheetForPrint;
import dialog.SpacificTime.SpacificTimeController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.gui.load.Location;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import util.Logger;
import util.gui.HelperMethods;
import util.gui.load.LoadHelper;
import util.printing.PrintingData;
import util.db.DatabaseHandler;
import util.gui.load.DialogHelper;

public class ArchiveHomeController implements Initializable {

    @FXML
    private JFXTreeTableView<ArchOfDays> myTable;
    @FXML
    private TreeTableColumn<ArchOfDays, String> dayCol;
    @FXML
    private TreeTableColumn<ArchOfDays, Double> PICol;
    @FXML
    private TreeTableColumn<ArchOfDays, Double> CICol;
    @FXML
    private TreeTableColumn<ArchOfDays, Double> BioCOL;
    @FXML
    private TreeTableColumn<ArchOfDays, Double> netIncome;
    @FXML
    private TreeTableColumn<ArchOfDays, Double> EXCol;
    @FXML
    private TreeTableColumn<ArchOfDays, Double> TotCol;
    @FXML
    private VBox TableBox;
    @FXML
    private VBox StatisticsBox;
    @FXML
    private AreaChart<String, Double> linech;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private CategoryAxis xAxis;
    XYChart.Series<String, Double> series = new XYChart.Series<>();
    int x = 0;
    int month;
    int year;
    public static String from = "";
    public static String to = "";
    ArchOfDays TEMP;
    ObservableList<ArchOfDays> monthes;
    public static ObservableList<ArchOfDays> Days;
    ObservableList<ArchOfDays> Years;
    @FXML
    private TreeTableColumn<ArchOfDays, JFXButton> debtIncome_Col;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            series.setName("احصائيات");
            yAxis.setLabel("صافي الربح");
            Days = selectArchDataOfDay("");
            monthes = selectArchDataOfMonth(Days);
            Years = selectArchDataOfYears();
            StatisticsBox.setVisible(false);
            dayCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("Day"));
            PICol.setCellValueFactory(new TreeItemPropertyValueFactory<>("PSIncome"));
            CICol.setCellValueFactory(new TreeItemPropertyValueFactory<>("cafeIncome"));
            BioCOL.setCellValueFactory(new TreeItemPropertyValueFactory<>("totIncome"));
            EXCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("expanses"));
            TotCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("el3ohda"));
            debtIncome_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("deptVal"));
            netIncome.setCellValueFactory(new TreeItemPropertyValueFactory<>("netIncome"));

            HelperMethods.TableColumnAlignment(dayCol,
                    PICol,
                    CICol,
                    BioCOL,
                    EXCol,
                    TotCol,
                    debtIncome_Col);

            fillArchDataOFDays();
            if (!ControlPanel.getInstance().HAS_PLAYSTATION) {
                myTable.getColumns().remove(PICol);
            }
            if (!ControlPanel.getInstance().CUSTOMER_DATA) {
                myTable.getColumns().remove(debtIncome_Col);
            }
            if (!ControlPanel.getInstance().EXPENSESS_FROM_DAILY_INCOME) {
                myTable.getColumns().remove(netIncome);
            } else {
                myTable.getColumns().remove(TotCol);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + " -> initialize:-" + e);
        }
    }

    public ObservableList<ArchOfDays> selectArchDataOfDay(String addingString) {
        ObservableList<ArchOfDays> list = FXCollections.observableArrayList();
        try {

            list.clear();
            String sqlState = "SELECT archive.day,archive.PsIncome,archive.ResturantIncome,archive.dept,archive.Expenses,archive.imp,archive.PsIncomeFROMCafe FROM archive  ORDER BY day ASC";
            if (!addingString.trim().equals("")) {
                sqlState = "SELECT * FROM (" + sqlState + ") " + addingString;
            }
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();

            while (DatabaseHandler.result.next()) {

                ArchOfDays rec = new ArchOfDays(
                        DatabaseHandler.result.getString(1),
                        DatabaseHandler.result.getDouble(2) - (ControlPanel.getInstance().SEPARATE_ROOMS_CAFE ? DatabaseHandler.result.getDouble(7) : 0),
                        DatabaseHandler.result.getDouble(3) + (ControlPanel.getInstance().SEPARATE_ROOMS_CAFE ? DatabaseHandler.result.getDouble(7) : 0),
                        DatabaseHandler.result.getDouble(4),
                        DatabaseHandler.result.getDouble(5),
                        DatabaseHandler.result.getDouble(6));
                rec.Day.setOnMouseClicked(ShowDataOfDay(rec));
                list.add(rec);
            }
            DatabaseHandler.result.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Exception In " + getClass().getName() + " -> selectArchDataOfDay:-" + e);
            return list;
        }
    }

    public EventHandler<MouseEvent> ShowDataOfDay(ArchOfDays rec) {
        return (MouseEvent event) -> {
            try {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    ArchiveDayDetailsController.myDay = rec.Day.getText();

                    if (ControlPanel.getInstance().HAS_PLAYSTATION) {
                        LoadHelper.getInstance().ArchDetailsController.fillDailySheetTable();
                    }
                    LoadHelper.getInstance().ArchDetailsController.fillEXTable();
                    LoadHelper.getInstance().ArchDetailsController.fillCafeTable();
                    LoadHelper.getInstance().Stage_ArchDayDetails.showAndWait();
                }
            } catch (Exception e) {
                Logger.writeLog("Exception in " + getClass().getName() + " -> ShowDataOfDay:- " + e);
            }
        };
    }

    public void fillArchDataOFDays() {
        ObservableList<ArchOfDays> sers = Days;
        if (x != 5 && !from.equals("") && !to.equals("")) {
            from = "";
            to = "";
        }
        switch (x) {
            case 0:
                dayCol.setText("اليوم");
                break;
            case 1:
                sers = monthes;
                dayCol.setText("الشهر");
                break;
            case 2:
                sers = Years;
                dayCol.setText("السنه");
                break;
            case 3:
                sers = selectArchDataOfDayForSpMonth(month, year);
                dayCol.setText("اليوم");
                break;
            case 4:
                sers = selectArchDataOfMonth(selectArchDataOfMonthsForYera(year));
                dayCol.setText("الشهر");
                break;
            case 5:
                sers = selectArchDataOfDay(" WHERE  day between '" + from + "' AND '" + to + "'");
                dayCol.setText("اليوم");
                break;
        }
        fillLineChart(sers);
        TreeItem<ArchOfDays> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        myTable.setRoot(root);
        myTable.setShowRoot(false);
    }

    @FXML
    private void DaylyArch() {
        x = 0;
        fillArchDataOFDays();
        goToTableBox();
    }

    public ObservableList<ArchOfDays> selectArchDataOfMonth(ObservableList<ArchOfDays> dataOfDays) {
        ObservableList<ArchOfDays> DataOfMonthes = FXCollections.observableArrayList();
        try {
            int m, y, flag, month2, year2;
            for (int i = 0; i < dataOfDays.size(); i++) {
                try {
                    LocalDate day = LocalDate.parse(dataOfDays.get(i).Day.getText());
                    m = day.getMonthValue();
                    y = day.getYear();
                    flag = 0;
                    for (int j = 0; j < DataOfMonthes.size(); j++) {
                        String[] d_y = DataOfMonthes.get(j).Day.getText().split("-");
                        month2 = Integer.parseInt(d_y[1]);
                        year2 = Integer.parseInt(d_y[0]);
                        if (m == month2 && y == year2) {
                            flag = 1;
//                            ...... 
                            DataOfMonthes.get(j).setPSIncome(DataOfMonthes.get(j).PSIncome + dataOfDays.get(i).PSIncome);
                            DataOfMonthes.get(j).getDeptVal().setText(String.valueOf(HelperMethods.formatNum(Double.parseDouble(DataOfMonthes.get(j).deptVal.getText())
                                    + Double.parseDouble(dataOfDays.get(i).deptVal.getText()))));
                            DataOfMonthes.get(j).setCafeIncome(DataOfMonthes.get(j).cafeIncome + dataOfDays.get(i).cafeIncome);
                            DataOfMonthes.get(j).setExpanses(DataOfMonthes.get(j).expanses + dataOfDays.get(i).expanses);
                            DataOfMonthes.get(j).setTotIncome(DataOfMonthes.get(j).totIncome + dataOfDays.get(i).totIncome);
                            DataOfMonthes.get(j).setEl3ohda(DataOfMonthes.get(j).el3ohda + dataOfDays.get(i).el3ohda);
                            DataOfMonthes.get(j).updateIncome();
                            break;
                        }
                    }
                    if (flag == 0) {
                        TEMP = new ArchOfDays(
                                (y + "-" + m),
                                dataOfDays.get(i).PSIncome,
                                dataOfDays.get(i).cafeIncome,
                                Double.parseDouble(dataOfDays.get(i).deptVal.getText()),
                                dataOfDays.get(i).expanses,
                                dataOfDays.get(i).el3ohda
                        );
                        TEMP.Day.setOnMouseClicked(monthEvent(TEMP));
                        DataOfMonthes.add(TEMP);
                    }
                } catch (Exception e) {
                    Logger.writeLog("Exception in ArchiveController -> selectArchDataOfMonth-> for Loop:-" + e);
                }
            }
            return DataOfMonthes;
        } catch (Exception e) {
            Logger.writeLog("Exception in selectArchDataOfMonth : " + e);
            return DataOfMonthes;
        }
    }

    public EventHandler<MouseEvent> monthEvent(ArchOfDays rec) {
        return (MouseEvent event) -> {
            try {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    x = 3;
                    month = Integer.parseInt(rec.getDay().getText().split("-")[1]);
                    year = Integer.parseInt(rec.getDay().getText().split("-")[0]);
                    fillArchDataOFDays();
                }
            } catch (Exception e) {
                Logger.writeLog("Exception in " + getClass().getName() + " -> monthEvent:-" + e);
            }
        };
    }

    public ObservableList<ArchOfDays> selectArchDataOfDayForSpMonth(int month, int year) {
        ObservableList<ArchOfDays> DataOfMontheDays = FXCollections.observableArrayList();
        try {
            int month2, year2;
            for (int i = 0; i < Days.size(); i++) {
                try {
                    LocalDate day = LocalDate.parse(Days.get(i).Day.getText());
                    month2 = day.getMonthValue();
                    year2 = day.getYear();
                    if (month == month2 && year == year2) {
                        DataOfMontheDays.add(Days.get(i));
                    }
                } catch (Exception e) {
                }
            }
            return DataOfMontheDays;
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> selectArchDataOfDayForSpMonth :- " + e);
            return DataOfMontheDays;
        }
    }

    @FXML
    private void month() {
        x = 1;
        fillArchDataOFDays();
        goToTableBox();
    }

    public ObservableList<ArchOfDays> selectArchDataOfYears() {
        ObservableList<ArchOfDays> DataOfYears = FXCollections.observableArrayList();
        try {
            int year, flag;
            for (int i = 0; i < Days.size(); i++) {
                try {
                    LocalDate day = LocalDate.parse(Days.get(i).Day.getText());
                    year = day.getYear();
                    flag = 0;
                    for (int j = 0; j < DataOfYears.size(); j++) {
                        int year2 = Integer.parseInt(DataOfYears.get(j).Day.getText());
                        if (year == year2) {
                            flag = 1;

                            DataOfYears.get(j).setPSIncome(DataOfYears.get(j).PSIncome + Days.get(i).PSIncome);
                            DataOfYears.get(j).getDeptVal().setText(String.valueOf(HelperMethods.formatNum(Double.parseDouble(DataOfYears.get(j).deptVal.getText())
                                    + Double.parseDouble(Days.get(i).deptVal.getText()))));
                            DataOfYears.get(j).setCafeIncome(DataOfYears.get(j).cafeIncome + Days.get(i).cafeIncome);
                            DataOfYears.get(j).setExpanses(DataOfYears.get(j).expanses + Days.get(i).expanses);
                            DataOfYears.get(j).setTotIncome(DataOfYears.get(j).totIncome + Days.get(i).totIncome);
                            DataOfYears.get(j).setEl3ohda(DataOfYears.get(j).el3ohda + Days.get(i).el3ohda);
                            DataOfYears.get(j).updateIncome();
                            break;
                        }
                    }
                    if (flag == 0) {
                        TEMP = new ArchOfDays(
                                year + "",
                                Days.get(i).PSIncome,
                                Days.get(i).cafeIncome,
                                Double.parseDouble(Days.get(i).deptVal.getText()),
                                Days.get(i).expanses,
                                Days.get(i).el3ohda);
                        TEMP.Day.setOnMouseClicked(YeraEvent(TEMP));
                        DataOfYears.add(TEMP);
                    }
                } catch (Exception e) {
                    Logger.writeLog("Exception in " + getClass().getName() + " -> selectArchDataOfMonth -> for Loop:- " + e);
                }
            }
            return DataOfYears;
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> selectArchDataOfMonth :- " + e);
            return DataOfYears;
        }
    }

    public ObservableList<ArchOfDays> selectArchDataOfMonthsForYera(int year) {
        ObservableList<ArchOfDays> DataOfyearsMonth = FXCollections.observableArrayList();
        try {
            int year2;
            for (int i = 0; i < Days.size(); i++) {
                try {
                    LocalDate day = LocalDate.parse(Days.get(i).Day.getText());
                    year2 = day.getYear();
                    if (year == year2) {
                        DataOfyearsMonth.add(Days.get(i));
                    }
                } catch (Exception e) {
                }
            }
            return DataOfyearsMonth;
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> selectArchDataOfMonthsForYera:- " + e);
            return DataOfyearsMonth;
        }
    }

    public EventHandler<MouseEvent> YeraEvent(ArchOfDays rec) {
        return (MouseEvent event) -> {
            try {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    x = 4;
                    year = Integer.parseInt(rec.getDay().getText());
                    fillArchDataOFDays();
                }
            } catch (Exception e) {
                Logger.writeLog("Exception in " + getClass().getName() + " -> YeraEvent:-" + e);
            }
        };
    }

    @FXML
    private void years() {
        x = 2;
        fillArchDataOFDays();
        goToTableBox();
    }

    @FXML
    private void goToSpacificTime() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(Location.getInstance().get("SPACIFICTIMEVIEW")));
            Scene scene = new Scene(root);
            Stage window = util.gui.BuilderUI.initStageUnDecorated(scene, null, "");
            window.showAndWait();
            from = SpacificTimeController.f;
            to = SpacificTimeController.t;
            if ((!from.equals("")) || (!to.equals(""))) {
                x = 5;
                fillArchDataOFDays();
                goToTableBox();
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in  " + getClass().getName() + " -> goToSpacificTime:-" + ex);
        }
    }

    @FXML
    private void goToStatisticsBox() {
        StatisticsBox.setVisible(true);
        TableBox.setVisible(false);
    }

    @FXML
    private void goToTableBox() {
        TableBox.setVisible(true);
        StatisticsBox.setVisible(false);
    }

    public void fillLineChart(ObservableList<ArchOfDays> currentData) {
        series.getData().clear();
        xAxis.getCategories().clear();
        linech.getData().clear();
        try {
            for (int i = 0; i < currentData.size(); i++) {
                series.getData().add(new XYChart.Data<>(currentData.get(i).getDay().getText(), currentData.get(i).getNetIncome()));
            }
            linech.getData().add(series);
            linech.setCreateSymbols(true);
            linech.setAnimated(false);
            switch (x) {
                case 0:
                    xAxis.setLabel("اليوم");
                    break;
                case 1:
                    xAxis.setLabel("الشهر");
                    break;
                case 2:
                    xAxis.setLabel("السنه");
                    break;
                case 3:
                    xAxis.setLabel("اليوم");
                    break;
                case 4:
                    xAxis.setLabel("الشهر");
                    break;
                case 5:
                    xAxis.setLabel("اليوم");
                    break;
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> fillLineChart:-");
        }
    }

    @FXML
    private void goToExpenseArch() {
        try {
            homepage.HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_ExpenseArch"));
            homepage.HomePageController.title_Label.setText("أرشيف المصروفات");
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> goToSettings");
        }
    }

    public static ObservableList<DailySheetForPrint> getDataForPrint(String sqlQuery) {
        ObservableList<DailySheetForPrint> printList = FXCollections.observableArrayList();
        try {
            String sql = sqlQuery;
            ResultSet result = DatabaseHandler.con.prepareStatement(sql).executeQuery();

            while (result.next()) {

                printList.add(new DailySheetForPrint(
                        result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getString(5),
                        result.getString(6),
                        result.getFloat(7),
                        result.getDouble(8),
                        result.getDouble(9),
                        result.getFloat(10),
                        result.getFloat(11),
                        result.getString(12),
                        result.getString(13)
                )
                );
            }

        } catch (Exception e) {
            Logger.writeLog("Exception in " + ArchiveHomeController.class.getName() + " -> getDataForPrint:-" + e);
        }
        return printList;
    }

    @FXML
    private void openA4PDF() {
        new Thread() {
            @Override
            public void run() {
                openA4PDF("reports/DailySheet", true);
            }
        }.start();
    }

    synchronized public void openA4PDF(String reportName, boolean showPdf) {
        try {
            ArrayList<ArchieveReport> days = new ArrayList<>();
            String fromDate = "", toDate = "";
            if (from.equals("") && to.equals("")) {
                ResultSet result = DatabaseHandler.con.prepareStatement("SELECT MIN(day),MAX(day),day FROM archive GROUP BY day ORDER BY day ASC ").executeQuery();
                while (result.next()) {
                    if (fromDate.equals("") && toDate.equals("")) {
                        fromDate = result.getString(1);
                        toDate = result.getString(2);
                    }
                    days.add(new ArchieveReport(result.getString(3), true));
                }
            } else {
                ResultSet result = DatabaseHandler.con.prepareStatement("SELECT MIN(day),MAX(day),day FROM archive GROUP BY day HAVING day BETWEEN '" + from + "' AND '" + to + "' ORDER BY day ASC ").executeQuery();
                while (result.next()) {
                    if (fromDate.equals("") && toDate.equals("")) {
                        fromDate = from;
                        toDate = to;
                    }
                    days.add(new ArchieveReport(result.getString(3), true));
                }
            }
            if (days.isEmpty()) {
                Platform.runLater(() -> {
                    DialogHelper.getInstance().showOKAlert("لا يوجد بيانات لعرضها");
                });

                return;
            }

            String outputFile = "reports/DailySheet.pdf";
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("days", new JRBeanCollectionDataSource(days, false));
            parameters.put("fromDate", fromDate);
            parameters.put("toDate", toDate);
            parameters.put("playstation", ControlPanel.getInstance().HAS_PLAYSTATION);
            parameters.put("AccessCustomerDataPage", ControlPanel.getInstance().CUSTOMER_DATA);
            parameters.put("netIncomeVisibilty", ControlPanel.getInstance().EXPENSESS_FROM_DAILY_INCOME);
            PrintingData.getInstance().setShopParameters(parameters);
            JasperReport jr = JasperCompileManager.compileReport(reportName + ".jrxml");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jr, parameters, new JREmptyDataSource());
            OutputStream outputStream = new FileOutputStream(new File(outputFile));
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            if (showPdf) {
                Desktop.getDesktop().open(new File(outputFile));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + " -> openA4PDF :-" + e);
        }
    }

}
