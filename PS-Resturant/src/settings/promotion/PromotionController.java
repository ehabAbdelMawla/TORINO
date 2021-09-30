package settings.promotion;

import datamodel.Promotion;
import com.jfoenix.controls.JFXDialog;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.db.DatabaseHandler;
import util.gui.load.DialogHelper;
import util.gui.load.LoadHelper;

public class PromotionController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;
    public JFXDialog dialog;

    @FXML
    private Spinner<Integer> hoursSpinner;
    @FXML
    private Spinner<Integer> pecentageSpinner;

    private final SpinnerValueFactory<Integer> HOURS_VALUES = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0);
    private final SpinnerValueFactory<Integer> PERCENTAGE_VALUES = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hoursSpinner.setValueFactory(HOURS_VALUES);
        pecentageSpinner.setValueFactory(PERCENTAGE_VALUES);
        hoursSpinner.setEditable(true);
        pecentageSpinner.setEditable(true);

        Promotion cuPeomotion = getTheCurrentPromotion();
        hoursSpinner.getValueFactory().setValue((int) cuPeomotion.getNumOfHours());
        pecentageSpinner.getValueFactory().setValue((int) cuPeomotion.getDiscountPercentage());
    }

    public static Promotion getTheCurrentPromotion() {
        Promotion promotion = new Promotion();
        try {
            ResultSet result = DatabaseHandler.con.prepareStatement("SELECT * FROM promotions").executeQuery();
            while (result.next()) {
                promotion.setNumOfHours(result.getDouble(1));
                promotion.setDiscountPercentage(result.getDouble(2));
            }
        } catch (Exception e) {
            util.Logger.writeLog("Exception in PromotionController -> getTheCurrentPromotion :-" + e);
        }
        return promotion;
    }

    @FXML
    private void editAction(Event event) {
        try {
            int hrs = Integer.parseInt(hoursSpinner.getEditor().getText());
            int percentage = Integer.parseInt(pecentageSpinner.getEditor().getText());

            if (percentage < 0 || hrs < 0) {
                throw new NumberFormatException();

            } else if (percentage > 100) {
                DialogHelper.getInstance().showOKAlert("الحد الاقصي لنسبة الخصم 100 ");
                return;
            }
            hoursSpinner.getValueFactory().setValue(hrs);
            pecentageSpinner.getValueFactory().setValue(percentage);

            PreparedStatement stat = DatabaseHandler.con.prepareStatement("UPDATE promotions SET hourse=?,percentage=?");
            stat.setDouble(1, hoursSpinner.getValue());
            stat.setDouble(2, pecentageSpinner.getValue());
            stat.execute();
            closeWindow(event);
        } catch (NumberFormatException ex) {
            DialogHelper.getInstance().showOKAlert("القيم التي ادخلتها غير صحيحة");
        } catch (Exception e) {
            util.Logger.writeLog("Exception in " + getClass().getName() + " -> editAction :- " + e);
        }
    }

    private void RootMousePressed(Event event) {
        MouseEvent e = (MouseEvent) event;
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    }

    private void RootMouseDragged(Event event) {
        MouseEvent e = (MouseEvent) event;
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setX(e.getScreenX() - xOffset);
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setY(e.getScreenY() - yOffset);
    }

    private void closeWindow(Event event) {
        dialog.close();
    }

    private void keyAction(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            editAction(event);
        } else if (event.getCode() == KeyCode.ESCAPE) {
            closeWindow(event);
        }
    }

}
