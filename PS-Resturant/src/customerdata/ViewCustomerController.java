package customerdata;

import controlpanel.ControlPanel;
import datamodel.DailySheetRecord;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import datamodel.Customer;
import datamodel.Order;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.Logger;
import util.gui.HelperMethods;
import util.gui.load.LoadHelper;
import util.gui.validation.ValidationMethods;
import datamodel.User;
import settings.devices.DevicesSettingsController;
import util.gui.StatusIcon;
import util.gui.load.DialogHelper;

public class ViewCustomerController implements Initializable {

    public static final ObservableList<Order> RESTAURANT_ORDERS = FXCollections.observableArrayList();
    public static final ObservableList<DailySheetRecord> PLAYING_RECORDS = FXCollections.observableArrayList();
    public boolean hasData;
    public int customerId = 0;
    @FXML
    private ToggleGroup debtGroup;
    @FXML
    private TextField filterField;
    @FXML
    private JFXTreeTableView<Order> OrdersTable;
    @FXML
    private TreeTableColumn<Order, String> debt_Col;
    @FXML
    private TreeTableColumn<Order, String> amountPaid_Col;
    @FXML
    private TreeTableColumn<Order, String> subTotalAmount_Col;
    @FXML
    private TreeTableColumn<Order, String> billKind_Col;
    @FXML
    private TreeTableColumn<Order, JFXButton> billNumber_Col;
    @FXML
    private TreeTableColumn<Order, String> date_Col;
    @FXML
    private TreeTableColumn<Order, String> time_Col;
    @FXML
    private TreeTableColumn<Order, JFXButton> paidButton_Col;
    @FXML
    private TreeTableColumn<Order, Double> discount_Col;
    @FXML
    private TreeTableColumn<Order, StatusIcon> statusBox_Col;
    @FXML
    private JFXButton data_Btn;
    @FXML
    private JFXButton RestaurantTransactions_Btn;
    @FXML
    private VBox data_Box;
    @FXML
    private VBox RestaurantTransactions_Box;
    @FXML
    private Label totalDebt_Label;
    @FXML
    private Label totalPaid_Label;

    @FXML
    private JFXRadioButton totalRadio;
    @FXML
    private JFXTextField name_TextField;
    @FXML
    private JFXTextField phone_TextField;
    @FXML
    private JFXTextField email_TextField;
    @FXML
    private JFXTextField address_TextField;
    @FXML
    private JFXTextField notes_TextField;
    private static JFXButton SelectedButton;
    @FXML
    private JFXRadioButton client_Radio;
    @FXML
    private JFXRadioButton staff_Radio;
    @FXML
    private JFXRadioButton owner_Radio;
    @FXML
    private ToggleGroup customerTypeGroup;
    @FXML
    private HBox radioBox;
    @FXML
    private HBox buttons_box;
    @FXML
    private VBox PlayingRoomsTransactions_Box;

    @FXML
    private JFXTreeTableView<DailySheetRecord> playingRoomsTable;

    @FXML
    private TreeTableColumn<DailySheetRecord, Double> incomeCol;

    @FXML
    private TreeTableColumn<DailySheetRecord, Double> disCol;

    @FXML
    private TreeTableColumn<DailySheetRecord, Double> preCol;

    @FXML
    private TreeTableColumn<DailySheetRecord, String> toCol;

    @FXML
    private TreeTableColumn<DailySheetRecord, String> fromCol;

    @FXML
    private TreeTableColumn<DailySheetRecord, String> RoomNumCol;

    @FXML
    private TreeTableColumn<DailySheetRecord, String> UserCol;

    @FXML
    private TreeTableColumn<DailySheetRecord, String> DateCol;
    @FXML
    private TreeTableColumn<DailySheetRecord, Integer> RecieptIdCol;
    @FXML
    private Label totalPlayingRooms;
    @FXML
    private JFXButton playingRoomsTransactions_Btn;
    @FXML
    private VBox ButtonsSideBar;
    @FXML
    private HBox dayNightBox;
    @FXML
    private ToggleGroup dayNightGroup;
    @FXML
    private JFXRadioButton nightRadioButton;
    @FXML
    private JFXRadioButton dayRadioButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableConfiguration();
        HelperMethods.SetTableData(OrdersTable, false, false, RESTAURANT_ORDERS);
        HelperMethods.SetTableData(playingRoomsTable, false, false, PLAYING_RECORDS);

        SelectedButton = data_Btn;
        data_Btn.getStyleClass().add("cashier-buttons-focused");
        focusButton(data_Btn);
        data_Box.setVisible(true);
        debtGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
            if (debtGroup.getSelectedToggle() != null) {

                if (((RadioButton) debtGroup.getSelectedToggle()).getText().equals("آجل")) {
                    OrdersTable.setPredicate(orderTreeItem
                            -> orderTreeItem.getValue().getDebtAmount() > 0);
                    getDebtSum();
                    setPaidWithDebt();
                } else {
                    OrdersTable.setPredicate(orderTreeItem -> true);
                    getDebtSum();
                    setAllPaid();
                }
            }
        });
        if (!ControlPanel.getInstance().HAS_PLAYSTATION) {
            ButtonsSideBar.getChildren().remove(playingRoomsTransactions_Btn);
        }
        if (!ControlPanel.getInstance().DAY_NIGHT_CUSTOMER) {
            ((HBox) dayNightBox.getParent()).getChildren().remove(dayNightBox);
            dayNightBox = null;
            nightRadioButton = null;
            dayRadioButton = null;
        }

        totalDebt_Label.setText("0 " + ControlPanel.getInstance().getCurrency());
        totalPaid_Label.setText("0 " + ControlPanel.getInstance().getCurrency());
        filterField.textProperty().addListener((observable, oldValue, newValue)
                -> OrdersTable.setPredicate(orderTreeItem
                        -> orderTreeItem.getValue().getBillNumber().getText().contains(filterField.getText().trim())));
    }

    private void editable(boolean b) {
        radioBox.setDisable(!b);
        buttons_box.setDisable(!b);
        name_TextField.setEditable(b);
        phone_TextField.setEditable(b);
        email_TextField.setEditable(b);
        address_TextField.setEditable(b);
        notes_TextField.setEditable(b);
        if (ControlPanel.getInstance().DAY_NIGHT_CUSTOMER) {
            dayRadioButton.setDisable(!b);
            nightRadioButton.setDisable(!b);
        }
    }

    public void setData(Customer customer, boolean isEditableView, boolean isPlaystaionData) {
        hasData = false;
        filterField.setText("");
        totalRadio.setSelected(true);
        editable(isEditableView);
        if (customer != null) {
            customerId = customer.getCode();
            name_TextField.setText(customer.getFullName());
            phone_TextField.setText(customer.getPhoneNumber());
            email_TextField.setText(customer.getEmail());
            address_TextField.setText(customer.getAddress());
            notes_TextField.setText(customer.getNotes());
            RESTAURANT_ORDERS.setAll(Order.getOrdersOfCustomer(customer.getFullName()));
            PLAYING_RECORDS.setAll(DailySheetRecord.getCustomerData(customer.getFullName()));
            totalPlayingRooms.setText(PLAYING_RECORDS.stream().mapToDouble(DailySheetRecord::getInComeVal).sum() + ControlPanel.getInstance().getCurrency());

            if (ControlPanel.getInstance().DAY_NIGHT_CUSTOMER) {
                dayRadioButton.setSelected(customer.getAttendPeriod() == 0);
                nightRadioButton.setSelected(customer.getAttendPeriod() == 1);
            }
            switch (customer.getCustomerType()) {
                case Guest:
                    client_Radio.setSelected(true);
                    break;
                case Staff:
                    staff_Radio.setSelected(true);
                    break;
                case Owner:
                    owner_Radio.setSelected(true);
                    break;
            }
            getDebtSum();
            setAllPaid();
        } else {
            customerId = 0;
            HelperMethods.ResetTexts(name_TextField, phone_TextField, email_TextField, address_TextField, notes_TextField);
            RESTAURANT_ORDERS.clear();
            PLAYING_RECORDS.clear();
            HelperMethods.resetEmptyLabel("0" + ControlPanel.getInstance().getCurrency(),
                    totalPaid_Label, totalDebt_Label, totalPlayingRooms);
        }
        if (isEditableView) {
            data_Btn.fire();
        } else if (isPlaystaionData) {
            playingRoomsTransactions_Btn.fire();
        } else {
            RestaurantTransactions_Btn.fire();
        }
    }

    private void tableConfiguration() {
        // Resturant Table
        time_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("time"));
        date_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("date"));
        billNumber_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("billNumber"));
        billKind_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("orderKind"));
        subTotalAmount_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("subTotalAmount"));
        amountPaid_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("paidAmount"));
        debt_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("debtAmount"));
        discount_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("discountAmount"));
        paidButton_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("paidButton"));
        statusBox_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("statusIcon"));

        // playing Table
        DateCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("actDate"));
        UserCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("userName"));
        RoomNumCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("roomNum"));
        fromCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("from"));
        toCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("to"));
        preCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("preDis"));
        disCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("Dis"));
        incomeCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("inComeVal"));
        RecieptIdCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("idButton"));
    }

    @FXML
    private void goToData() {
        focusButton(data_Btn);
        data_Box.setVisible(true);
        RestaurantTransactions_Box.setVisible(false);
        PlayingRoomsTransactions_Box.setVisible(false);
    }

    @FXML
    private void goToTransactions() {
        focusButton(RestaurantTransactions_Btn);
        RestaurantTransactions_Box.setVisible(true);
        data_Box.setVisible(false);
        PlayingRoomsTransactions_Box.setVisible(false);
    }

    private void focusButton(JFXButton b) {
        SelectedButton.getStyleClass().remove("cashier-buttons-focused");
        SelectedButton = b;
        SelectedButton.getStyleClass().add("cashier-buttons-focused");
    }

    @FXML
    private void closeAction(Event event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void discardAction(Event event) {
        hasData = false;
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void saveAction(Event event) {
        try {
            if (!ValidationMethods.checkTextFeildConstraint(name_TextField, phone_TextField)) {
                DialogHelper.getInstance().showOKAlert("يجب ادخال الاسم ورقم الموبايل ");
                return;
            } else if (!ValidationMethods.isPhoneNumber(phone_TextField.getText().trim())) {
                DialogHelper.getInstance().showOKAlert("رقم الموبايل غير صحيح ");
                return;
            } else if (ValidationMethods.checkTextFeildConstraint(email_TextField) && !ValidationMethods.isMail(email_TextField.getText())) {
                DialogHelper.getInstance().showOKAlert("الايميل غير صحيح ");
                return;
            }

            Customer newCutomer = new Customer(
                    name_TextField.getText().trim(),
                    phone_TextField.getText().trim(),
                    email_TextField.getText().trim(),
                    address_TextField.getText().trim(),
                    notes_TextField.getText().trim(),
                    owner_Radio.isSelected() ? Customer.CustomerType.Owner : staff_Radio.isSelected() ? Customer.CustomerType.Staff : Customer.CustomerType.Guest,
                    User.CurrentUser.userName, ((RadioButton) dayNightGroup.getSelectedToggle()).getText().equals("صباحي") ? 0 : 1);

            int result;
            if (customerId != 0) {
                newCutomer.setCode(customerId);
                newCutomer.setFields(newCutomer.createEditMap());
                result = newCutomer.update();
            } else {
                result = newCutomer.add();
            }
            switch (result) {
                case 1:
                    hasData = true;
                    Platform.runLater(Customer::fillData);
                    Platform.runLater(Order::fillDailyCafeTable);
                    Platform.runLater(LoadHelper.getInstance().dailySheetController::fillDailySheetTable);
                    ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
                    DevicesSettingsController.actNot = 1;   //to update reservation data if needed
                    break;
                case -2:
                    DialogHelper.getInstance().showOKAlert("اسم العميل او رقم الهاتف مستخدم من قبل");
                    break;
                default:
                    DialogHelper.getInstance().showOKAlert("حدثت مشكلة ,اعد المحاولة لاحقا ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + " -> saveAction() :- " + e);
        }
    }

    @FXML
    private void KeyPrs(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            saveAction(event);
        } else if (event.getCode() == KeyCode.ESCAPE) {
            discardAction(event);
        }
    }

    public void getDebtSum() {
        double debtSum = RESTAURANT_ORDERS.stream().mapToDouble(Order::getDebtAmount).sum();
        totalDebt_Label.setText(HelperMethods.formatNum(debtSum) + " " + ControlPanel.getInstance().getCurrency());
    }

    public void setAllPaid() {
        double paidSum = RESTAURANT_ORDERS.stream().mapToDouble((order) -> {
            if (order instanceof Order) {
                return order.getPaidAmount();
            }
            return 0;
        }).sum();
        totalPaid_Label.setText(HelperMethods.formatNum(paidSum) + " " + ControlPanel.getInstance().getCurrency());
    }

    private void setPaidWithDebt() {
        double paidSum = RESTAURANT_ORDERS.stream().mapToDouble((order) -> {
            if (order instanceof Order) {
                if (order.getDebtAmount() > 0) {
                    return order.getPaidAmount();
                }
            }
            return 0;
        }).sum();
        totalPaid_Label.setText(paidSum + " " + ControlPanel.getInstance().getCurrency());
    }

    public void resetFilters() {
        filterField.setText("");
        totalRadio.setSelected(true);
    }

    @FXML
    private void goToPlayingRooms(Event event) {
        focusButton(playingRoomsTransactions_Btn);
        PlayingRoomsTransactions_Box.setVisible(true);
        RestaurantTransactions_Box.setVisible(false);
        data_Box.setVisible(false);
    }
}
