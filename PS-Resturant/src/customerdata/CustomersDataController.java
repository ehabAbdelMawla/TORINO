package customerdata;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import controlpanel.ControlPanel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import datamodel.Customer;
import datamodel.Customer.CustomerType;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.HelperMethods;
import util.gui.load.DialogHelper;

public class CustomersDataController implements Initializable {

    private static final ObservableList<Customer> FILTERED_DATA = FXCollections.observableArrayList();
    @FXML
    private TextField searchComBox;
    @FXML
    private ToggleGroup debtGroup;
    @FXML
    private ToggleGroup customerTypeGroup;
    @FXML
    private JFXTreeTableView<Customer> cutomersTable;
    @FXML
    private TreeTableColumn<Customer, String> name_Col;
    @FXML
    private TreeTableColumn<Customer, String> phone_Col;
    @FXML
    private TreeTableColumn<Customer, String> email_Col;
    @FXML
    private TreeTableColumn<Customer, Double> amountPaid_Col;
    @FXML
    private TreeTableColumn<Customer, Double> amountDebt_Col;
    @FXML
    private TreeTableColumn<Customer, JFXButton> details_Col;
    @FXML
    private TreeTableColumn<Customer, Double> creditHourse_Col;
    @FXML
    private TreeTableColumn<Customer, Double> totalHourse_Col;

    public static CustomerType currentType = CustomerType.Guest;
    public static String currentDeptFilter = "آجل ومدفوع";
    public static String currentPeriodFilter = "صباحي";

    public static JFXTreeTableView<Customer> cutomersTablePointer;
    public static TextField searchComBoxPointer;
    @FXML
    private Label tableDataCount;

    private final static SimpleStringProperty FILERED_DATA_SIZE = new SimpleStringProperty("0");
    @FXML
    private JFXButton exportPhoneNumbersButton;
    @FXML
    private ToggleGroup dayNightGroup;
    @FXML
    private HBox dayNightBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cutomersTablePointer = cutomersTable;
        searchComBoxPointer = searchComBox;

        tableConfiguration();

        Customer.fillData();

        HelperMethods.SetTableData(cutomersTable, false, false, FILTERED_DATA);

        customerTypeGroup.selectedToggleProperty().addListener((currentSelected) -> {
            String currentVal = ((RadioButton) customerTypeGroup.getSelectedToggle()).getText();

            switch (currentVal) {
                case "موظف":
                    currentType = CustomerType.Staff;
                    break;
                case "عميل":
                    currentType = CustomerType.Guest;
                    break;
                default:
                    currentType = CustomerType.Owner;
                    break;
            }

            filterTable();

        });

        debtGroup.selectedToggleProperty().addListener((observable) -> {
            String currentVal = ((RadioButton) debtGroup.getSelectedToggle()).getText();

            currentDeptFilter = currentVal;
            filterTable();
        });

        dayNightGroup.selectedToggleProperty().addListener((observable) -> {
            String currentVal = ((RadioButton) dayNightGroup.getSelectedToggle()).getText();

            currentPeriodFilter = currentVal;
            filterTable();
        });
        if (!ControlPanel.getInstance().DAY_NIGHT_CUSTOMER) {
            ((HBox) dayNightBox.getParent()).getChildren().remove(dayNightBox);
            dayNightBox = null;
        }

        //        ==== Remove credit column  ====
        if (!ControlPanel.getInstance().HOUR_PROMOTIONS) {
            cutomersTable.getColumns().remove(creditHourse_Col);

        }
        if (!ControlPanel.getInstance().HAS_PLAYSTATION) {
            cutomersTable.getColumns().remove(creditHourse_Col);
            cutomersTable.getColumns().remove(totalHourse_Col);
        }
//       ==== ==== ==== ==== ====  ==== ====
        tableDataCount.textProperty().bind(FILERED_DATA_SIZE);

        if (!ControlPanel.getInstance().CUSTOMER_DATA_EXPORT) {
            ((HBox) exportPhoneNumbersButton.getParent()).getChildren().remove(exportPhoneNumbersButton);
            exportPhoneNumbersButton = null;
        }
    }

    private void tableConfiguration() {
        name_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("fullName"));
        phone_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("phoneNumber"));
        email_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("email"));
        amountPaid_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("amountPaid"));
        amountDebt_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("amountDebt"));
        details_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("details"));
        creditHourse_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("creditHourse"));
        totalHourse_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("totalHourse"));
    }

    @FXML
    private void searchEvent(KeyEvent event) {
        filterTable();
    }

    @FXML
    private void AddCustomer(ActionEvent event) {
        if (DatabaseHandler.canAddMoreRowsInTable("customers")) {
            DialogHelper.getInstance().viewCustomer(null, true, false);
        } else {
            DialogHelper.getInstance().showOKAlert("لا يمكن اضافة المزيد في النسخة التجريبية");
        }
    }

    public static void filterTable() {
        try {
            FILTERED_DATA.setAll(Customer.customersData.filtered((customer) -> {
                boolean customerTypeCheck = customer.getCustomerType().equals(currentType),
                        nameFilterCheck = true, deptCheck = true, periodCheck = true;
                String strFilter = searchComBoxPointer == null ? "" : searchComBoxPointer.getText().trim();
                if (!strFilter.trim().equals("")) {
                    nameFilterCheck = customer.getFullName().contains(strFilter.trim()) || customer.getEmail().contains(strFilter.trim()) || customer.getPhoneNumber().contains(strFilter.trim());
                }
                if (!currentDeptFilter.trim().equals("آجل ومدفوع")) {
                    deptCheck = customer.getAmountDebt() > 0;
                }
                if (ControlPanel.getInstance().DAY_NIGHT_CUSTOMER) {
                    if (currentPeriodFilter.equalsIgnoreCase("صباحي")) {
                        periodCheck = customer.getAttendPeriod() == 0;
                    } else {
                        periodCheck = customer.getAttendPeriod() == 1;
                    }
                }
                return (customerTypeCheck && nameFilterCheck && deptCheck && periodCheck);
            }));
            FILERED_DATA_SIZE.set(String.format("عدد %s: %d", currentType.equals(CustomerType.Guest) ? "العملاء" : currentType.equals(CustomerType.Staff) ? "الموظفين" : "المُلاك", FILTERED_DATA.size()));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in CustomersDataController -> filterTable() " + e);
        }
    }

    public static void typeInCell(Cell cell, String data, HSSFCellStyle style) {
        cell.setAsActiveCell();
        cell.setCellValue(data);
        cell.setCellStyle(style);
    }

    /**
     * Moved This Method to
     *
     * @param text
     */
    public static void copyToClipboard(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(text), null);
    }

    @FXML
    private void exportPhoneNumbers() {
        //  ..... Excel Work .....

        /**
         * Get File Name With Name And destination From User
         */
        File outputFile = getFileDestenation(searchComBox.getScene().getWindow(), "Export Customer Data", "customer_data");

        if (outputFile != null) {

            new Thread(() -> {
                try {
                    /**
                     * Create Workbook & sheet
                     */
                    Workbook wb = new HSSFWorkbook();
                    Sheet sheet = wb.createSheet("Sheet1");
                    /**
                     * Create Style to align Data to center of cell
                     */
                    HSSFCellStyle centerAlignData = (HSSFCellStyle) wb.createCellStyle();
                    centerAlignData.setAlignment(HorizontalAlignment.CENTER);

                    setSheetColumnWidth(sheet, 8000, 0, 1, 2);
                    int rowIndex = 0;    // indecate row index to type in 
                    /**
                     * Type Titles
                     */
                    Row row = sheet.createRow(rowIndex);
                    typeInCell(row.createCell(0), "اسم العميل", centerAlignData);
                    typeInCell(row.createCell(1), "رقم الموبايل", centerAlignData);
                    typeInCell(row.createCell(2), "الايميل", centerAlignData);
                    /**
                     * Add Table Data
                     */
                    for (Customer customer : FILTERED_DATA) {
                        row = sheet.createRow(++rowIndex);
                        typeInCell(row.createCell(0), customer.getFullName(), centerAlignData);
                        typeInCell(row.createCell(1), customer.getPhoneNumber(), centerAlignData);
                        typeInCell(row.createCell(2), customer.getEmail().trim().equals("") ? "لا يوجد" : customer.getEmail(), centerAlignData);
                    }
                    OutputStream fos = new FileOutputStream(outputFile);
                    wb.write(fos);
                    fos.close();
                    Platform.runLater(() -> {
                        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE_ALT);
                        icon.setStyle("-fx-fill:green;");
                        util.gui.BuilderUI.showNotification(icon, "تم تصدير الملف بنجاح");
                    });
                } catch (Exception e) {
                    Logger.writeLog("Exception in " + getClass().getName() + " -> exportPhoneNumbers() " + e);
                }
            }).start();
        }

    }

    public static void setSheetColumnWidth(Sheet sheet, int width, int... columns) {
        for (int colIndex : columns) {
            sheet.setColumnWidth(colIndex, width);
        }
    }

    public static File getFileDestenation(javafx.stage.Window window, String title, String initialFileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xls"));
        fileChooser.setInitialFileName(initialFileName);

        return fileChooser.showSaveDialog(window);
    }

}
