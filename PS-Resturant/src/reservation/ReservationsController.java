package reservation;

import controlpanel.ControlPanel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import datamodel.Customer;
import datamodel.Reservation;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.sql.ResultSet;
import java.time.LocalTime;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.VBox;
import util.db.DatabaseHandler;
import util.db.Methods;
import util.gui.button.TableViewButton;
import util.gui.load.DialogHelper;

public class ReservationsController implements Initializable {

    public static ObservableList<String> devicesList = FXCollections.observableArrayList();
    private Customer choosenCustomer;

    @FXML
    private JFXTimePicker time_Input;
    @FXML
    private JFXTextField customerNameTextField;
    @FXML
    private JFXComboBox<String> device_ComboBox;
    @FXML
    private ToggleGroup gameType;
    @FXML
    private JFXRadioButton single_radio;
    @FXML
    private JFXTextField specialGame_TextField;
    @FXML
    private JFXTextField notes_TextField;
    @FXML
    private TextField searchTxtF;
    @FXML
    private JFXTreeTableView<Reservation> reservationTable;
    @FXML
    private TreeTableColumn<Reservation, String> time_Col;
    @FXML
    private TreeTableColumn<Reservation, String> name_Col;
    @FXML
    private TreeTableColumn<Reservation, String> device_Col;
    @FXML
    private TreeTableColumn<Reservation, String> type_Col;
    @FXML
    private TreeTableColumn<Reservation, String> specialGame_Col;
    @FXML
    private TreeTableColumn<Reservation, String> notes_Col;
    @FXML
    private TreeTableColumn<Reservation, TableViewButton> start_Col;
    @FXML
    private TreeTableColumn<Reservation, TableViewButton> cancel_Col;
    @FXML
    private VBox parentBox;
    @FXML
    private FontAwesomeIconView arrowIcon;
    @FXML
    private VBox optionsBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reset();
        expandOptions();
        initDevices();

        tableConfiguration();
        Reservation.fetchData();

        searchTxtF.textProperty().addListener((observable, oldValue, newValue)
                -> reservationTable.setPredicate(orderTreeItem
                        -> orderTreeItem.getValue().getClient().getFullName().trim().toLowerCase()
                        .contains(searchTxtF.getText().trim().toLowerCase())
                || orderTreeItem.getValue().getClient().getPhoneNumber().trim().toLowerCase()
                        .contains(searchTxtF.getText().trim().toLowerCase())
                ));
    }

    private void tableConfiguration() {
        time_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("time"));
        name_Col.setCellValueFactory((param -> new SimpleObjectProperty<>(param.getValue().getValue().getClient().getFullName())));
        device_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("device"));
        type_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));
        specialGame_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("specialGame"));
        notes_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("notes"));
        start_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("startPlay"));
        cancel_Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("cancelReservation"));

        util.gui.HelperMethods.TableColumnAlignment(time_Col, name_Col, device_Col, type_Col, specialGame_Col, notes_Col, start_Col, cancel_Col);

        TreeItem<Reservation> root = new RecursiveTreeItem<>(Reservation.reservationObservableList, RecursiveTreeObject::getChildren);
        reservationTable.setRoot(root);
        reservationTable.setShowRoot(false);
    }

    private void initDevices() {
        device_ComboBox.setItems(devicesList);
        devicesList.setAll(getAllDevices());
    }

    @FXML
    private void addAction(Event event) {
        try {
            if (DatabaseHandler.canAddMoreRowsInTable("reservations")) {
                boolean err = false;
                String time = time_Input.getValue().toString();
                err = time.equals("") ? true : err;

                err = customerNameTextField.getText().trim().equals("") || choosenCustomer == null ? true : err;

                String device = device_ComboBox.getValue();
                err = (device == null || device.equals("")) ? true : err;

                String type = ((JFXRadioButton) gameType.getSelectedToggle()).getText();

                String specialGame = specialGame_TextField.getText().trim();

                String notes = notes_TextField.getText().trim();
                if (err) {
                    DialogHelper.getInstance().showOKAlert("برجاء ادخال البيانات بطريقة صحيحة");
                    return;
                }

//          ... Deprecated because it prevent [12:00 am in next day ]
//            if (LocalTime.parse(time).isBefore(LocalTime.now())) {
//                DialogHelper.getInstance().showOKAlert("برجاء التاكد من الوقت ");
//                return;
//            }
                Reservation reservation = new Reservation(Methods.getMyDay(), time, choosenCustomer, device, type, specialGame, notes);
                reservation.add();
                Reservation.fetchData();
                reset();
            } else {
                DialogHelper.getInstance().showOKAlert("لا يمكن اضافة المزيد في النسخة التجريبية");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            util.Logger.writeLog("Exception in " + getClass().getName() + ".addAction() :-" + ex);
        }
    }

    @FXML
    private void expandOptions() {
        if (parentBox.getChildren().size() == 1) {
            // add options
            parentBox.getChildren().add(1, optionsBox);
            arrowIcon.setIcon(FontAwesomeIcon.ARROW_UP);
        } else {
            // remove options
            parentBox.getChildren().remove(optionsBox);
            arrowIcon.setIcon(FontAwesomeIcon.ARROW_DOWN);
        }
    }

    @FXML
    private void chooseCustomer(Event event) {
        choosenCustomer = DialogHelper.getInstance().chooseCustomer();
        if (choosenCustomer != null) {
            customerNameTextField.setText(choosenCustomer.getFullName());
        } else {
            choosenCustomer = null;
            customerNameTextField.setText("");
        }
    }

    private void reset() {
        util.gui.HelperMethods.resetEmptyField("", customerNameTextField, specialGame_TextField, notes_TextField);
        time_Input.setValue(LocalTime.now());
        device_ComboBox.setValue(null);
        single_radio.setSelected(true);
        choosenCustomer = null;
    }

    private static ObservableList<String> getAllDevices() {
        ObservableList<String> devices = FXCollections.observableArrayList();
        try {
            String removeMatchDevice = ControlPanel.getInstance().MATCH_DEVICE ? "" : "WHERE id>0 ";
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT DISTINCT device FROM playingprice " + removeMatchDevice).executeQuery();
            while (res.next()) {
                devices.add(res.getString(1));
            }
        } catch (Exception e) {
            util.Logger.writeLog("Exception in ReservationsController -> getAllDevices :- " + e);
        }
        return devices;
    }

    public static void updateDeviceses() {
        try {
            devicesList.setAll(getAllDevices());
        } catch (Exception e) {
            util.Logger.writeLog("Exception in ReservationsController -> getAllDevices :- " + e);
        }
    }
}
