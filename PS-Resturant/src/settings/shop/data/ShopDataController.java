/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package settings.shop.data;

import controlpanel.ControlPanel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.Logger;
import util.db.DBCRUD;
import util.db.DBField;
import util.gui.HelperMethods;
import util.gui.load.LoadHelper;
import util.gui.validation.ValidationMethods;
import util.printing.PrintingData;
import util.db.DatabaseHandler;
import util.gui.load.DialogHelper;

/**
 * FXML Controller class
 *
 * @author Bayoumi w ana kman
 */
public class ShopDataController implements Initializable {

    @FXML
    private ImageView imageView;
    @FXML
    private JFXTextField placeName;
    @FXML
    private JFXTextField placeAddress;
    @FXML
    private JFXTextField phone;
    @FXML
    private JFXRadioButton addressRadio;
    @FXML
    private ToggleGroup headerContent;
    @FXML
    private JFXRadioButton logoRadio;
    @FXML
    private JFXTextField mailSubject;
    @FXML
    private JFXTextField mail1;
    @FXML
    private JFXTextField mail2;
    @FXML
    private JFXButton receipt_BTN;
    @FXML
    private HBox receipt_BOX;
    @FXML
    private VBox mail_BOX;

    private JFXButton currentBtn;
    @FXML
    private JFXButton mail_BTN;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentBtn = ControlPanel.getInstance().ACCESS_RECIEPT_DATA ? receipt_BTN : mail_BTN;
        currentBtn.getStyleClass().add("dark-button");
        if (ControlPanel.getInstance().ACCESS_RECIEPT_DATA) {
            receipt_BOX.setVisible(true);
        } else {
            mail_BOX.setVisible(true);
        }
        if(!ControlPanel.getInstance().SEND_DAILYSHEET_MAIL || !ControlPanel.getInstance().ACCESS_RECIEPT_DATA){
            ((VBox) mail_BTN.getParent().getParent().getParent()).getChildren().remove(mail_BTN.getParent().getParent());
        }
//        mail_BTN.setDisable(!ControlPanel.getInstance().SEND_DAILYSHEET_MAIL);
//        receipt_BTN.setDisable(!ControlPanel.getInstance().ACCESS_RECIEPT_DATA);
        setReciptPageData();
        setMailPageData();
    }

    @FXML
    private void changeImage(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png"));
            File path = fileChooser.showOpenDialog(((Stage) ((Node) event.getSource()).getScene().getWindow()));
            if (path != null) {
                PreparedStatement stat = DatabaseHandler.con.prepareStatement("UPDATE printData SET image=?");
                stat.setBytes(1, PrintingData.getInstance().readFile(path.toString()));
                stat.execute();
                PrintingData.getInstance().fetchData();
                imageView.setImage(PrintingData.getInstance().fxImage);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception In " + getClass().getName() + " -> chnageImage:-" + e);
        }
    }

    @FXML
    private void saveDataAction() {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (!ValidationMethods.checkTextFeildConstraint(placeName,
                            placeAddress,
                            phone)) {
                        Platform.runLater(() -> {
                            DialogHelper.getInstance().showOKAlert("يجب ادخال جميع البيانات");
                        });

                        return;
                    }

                    if (PrintingData.getInstance().fxImage == null && logoRadio.isSelected()) {
                        Platform.runLater(() -> {
                            DialogHelper.getInstance().showOKAlert("يجب ادخال صورة او اختيار اسم المكان");
                        });
                        return;
                    }

                    DBCRUD dbObj = new DBCRUD("printData") {
                        @Override
                        public ArrayList createMap() {
                            ArrayList<DBField> map = new ArrayList();
                            map.add(new DBField(placeName.getText().trim(), "shopName", "NN"));
                            map.add(new DBField(placeAddress.getText().trim(), "address", "NN"));
                            map.add(new DBField(phone.getText().trim(), "phoneNumbers", "NN"));
                            map.add(new DBField(logoRadio.isSelected() ? "logo" : "shopName", "headerBand", "NN"));
                            return map;
                        }
                    };
                    dbObj.setFields(dbObj.createMap());
                    int result = dbObj.update();
                    if (result <= 0) {
                        Platform.runLater(() -> {
                            DialogHelper.getInstance().showOKAlert("حدث خطأ,اعد المحاولة في وقت لاحق");
                        });
                    }
                    PrintingData.getInstance().fetchData();
                    Platform.runLater(() -> {
                        DialogHelper.getInstance().showOKAlert("تم الحفظ بنجاح");
                    });
                } catch (Exception e) {
                    Logger.writeLog("Exception in " + getClass().getName() + " -> saveDataAction :- " + e);
                }
            }
        }.start();
    }

    @FXML
    private void deleteDataAction() {
        HelperMethods.ResetTexts(placeName, placeAddress, phone);
    }

    @FXML
    private void saveEmailAction() {
        try {
            if ((!ValidationMethods.checkTextFeildConstraint(mailSubject) && (ValidationMethods.checkTextFeildConstraint(mail1) || ValidationMethods.checkTextFeildConstraint(mail2)))
                    || (ValidationMethods.checkTextFeildConstraint(mailSubject) && (!ValidationMethods.checkTextFeildConstraint(mail1) && !ValidationMethods.checkTextFeildConstraint(mail2)))) {
                DialogHelper.getInstance().showOKAlert("يجب ادخال اسم الفرع و بريد الكتروني لاستلام التقرير");
                return;
            } else if ((!mail1.getText().equals("") && !ValidationMethods.isMail(mail1.getText())) || (!mail2.getText().equals("") && !ValidationMethods.isMail(mail2.getText()))) {
                DialogHelper.getInstance().showOKAlert("البريد الذي ادخلته غير صحيح!");
                return;
            } else if ((!mail1.getText().equals("") && ValidationMethods.isMail(mail1.getText())) && (!mail2.getText().equals("") && ValidationMethods.isMail(mail2.getText())) && mail2.getText().equalsIgnoreCase(mail1.getText())) {
                DialogHelper.getInstance().showOKAlert("البريد مكرر!");
                return;
            }

            PreparedStatement stat = DatabaseHandler.con.prepareStatement("UPDATE sendMailData SET shopName=?,mail1=?,mail2=?");
            stat.setString(1, mailSubject.getText());
            stat.setString(2, mail1.getText());
            stat.setString(3, mail2.getText());

            stat.execute();
            DialogHelper.getInstance().showOKAlert("تم الحفظ بنجاح");

        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> saveData():- " + e);
        }
    }

    @FXML
    private void goToSettings() {
        try {
            setReciptPageData();
            homepage.HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_Settings"));
            homepage.HomePageController.title_Label.setText("الإعدادات");
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + " => goToSettings() => " + ex);
        }
    }

    @FXML
    private void sendMailToggle(ActionEvent event) {
        toggleButton((JFXButton) event.getSource());
    }

    @FXML
    private void receiptToggle(ActionEvent event) {
        setReciptPageData();
        toggleButton((JFXButton) event.getSource());
    }

    private void toggleButton(JFXButton b) {
        // removeFocus
        currentBtn.getStyleClass().remove("dark-button");
        // putFocus
        b.getStyleClass().add("dark-button");
        currentBtn = b;
        if (b.equals(receipt_BTN)) {
            receipt_BOX.setVisible(true);
            mail_BOX.setVisible(false);
        } else {
            mail_BOX.setVisible(true);
            receipt_BOX.setVisible(false);
        }
    }

    private void setReciptPageData() {
        PrintingData shopData = PrintingData.getInstance();
        placeName.setText(shopData.shopName);
        placeAddress.setText(shopData.address);
        phone.setText(shopData.phoneNumbers);
        imageView.setImage(shopData.fxImage);
        addressRadio.setSelected(!shopData.logoApperance);
        logoRadio.setSelected(shopData.logoApperance);
    }

    private void setMailPageData() {
        try {
            ResultSet result = DatabaseHandler.con.prepareStatement("SELECT * FROM sendMailData").executeQuery();
            int flag = 0;
            while (result.next()) {
                mailSubject.setText(result.getString(1));
                mail1.setText(result.getString(2));
                mail2.setText(result.getString(3));

                flag = 1;
            }
            if (flag == 0) {
                DatabaseHandler.con.prepareStatement("INSERT INTO sendMailData VALUES('shopName','','')").execute();
                setMailPageData();
            };
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> initData() :- " + e);
        }
    }

    @FXML
    private void keyAct(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            if (receipt_BOX.isVisible()) {
                saveDataAction();
            } else {
                saveEmailAction();
            }
        }
    }

}
