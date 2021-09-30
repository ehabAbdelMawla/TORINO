/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package dialog.number;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.gui.Draggable;

/**
 * FXML Controller class
 *
 * @author Bayoumi
 */
public class NumberDialogController implements Initializable {

    public int lastValue_ProductAmount = 1;
    public boolean isConfirmed;
    @FXML
    private TextField amountTextFiled;
    @FXML
    private Label label;
    @FXML
    private AnchorPane AP;
    @FXML
    private JFXButton confirmButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        amountTextFiled.textProperty().addListener((obs) -> {
            if (amountTextFiled.getText().equals("0")) {
                confirmButton.setDisable(true);
            } else {
                confirmButton.setDisable(false);
            }
        });
        new Draggable(AP);
    }

    public void setData(String text) {
        if (text != null && !text.equals("")) {
            label.setText(text);
        } else {
            label.setText("العدد");
        }
        isConfirmed = false;
    }

    private int handleChangeInTextField(String newValue) {
        // handle wrong inputs
        try {
            lastValue_ProductAmount = Integer.parseInt(newValue);
            if (lastValue_ProductAmount == 0) {
                throw new NumberFormatException("Must be Positive");
            }
        } catch (NumberFormatException ex) {
            System.out.println(ex.getLocalizedMessage());
            amountTextFiled.setText(lastValue_ProductAmount + "");
        }
        return lastValue_ProductAmount;
    }

    private void appendNumber(String n) {
        try {
            lastValue_ProductAmount = Integer.parseInt(lastValue_ProductAmount + n);
            amountTextFiled.setText(lastValue_ProductAmount + "");
        } catch (NumberFormatException ex) {
            System.out.println(ex.getLocalizedMessage());
            amountTextFiled.setText(lastValue_ProductAmount + "");
        }

    }

    @FXML
    private void plusProduct() {
        lastValue_ProductAmount = Integer.parseInt(amountTextFiled.getText());
        amountTextFiled.setText(handleChangeInTextField(String.valueOf(lastValue_ProductAmount + 1)) + "");
    }

    @FXML
    private void minusProduct() {
        lastValue_ProductAmount = Integer.parseInt(amountTextFiled.getText());
        amountTextFiled.setText(handleChangeInTextField(lastValue_ProductAmount > 1 ? String.valueOf(lastValue_ProductAmount - 1) : "1") + "");
    }

    @FXML
    private void clear() {
        lastValue_ProductAmount = 0;
        amountTextFiled.setText("0");
    }

    @FXML
    private void zero() {
        appendNumber("0");
    }

    @FXML
    private void one() {
        appendNumber("1");
    }

    @FXML
    private void two() {
        appendNumber("2");
    }

    @FXML
    private void three() {
        appendNumber("3");
    }

    @FXML
    private void four() {
        appendNumber("4");
    }

    @FXML
    private void five() {
        appendNumber("5");
    }

    @FXML
    private void six() {
        appendNumber("6");
    }

    @FXML
    private void seven() {
        appendNumber("7");
    }

    @FXML
    private void eight() {
        appendNumber("8");
    }

    @FXML
    private void nine() {
        appendNumber("9");
    }

    @FXML
    private void confirm() {
        isConfirmed = true;
        ((Stage) amountTextFiled.getScene().getWindow()).close();
    }

    @FXML
    private void discard() {
        isConfirmed = false;
        ((Stage) amountTextFiled.getScene().getWindow()).close();
    }

    @FXML
    private void keyEv(KeyEvent event) {
        switch (event.getCode()) {
            case NUMPAD0:
            case DIGIT0:
                zero();
                break;
            case NUMPAD1:
            case DIGIT1:
                one();
                break;
            case NUMPAD2:
            case DIGIT2:
                two();
                break;
            case NUMPAD3:
            case DIGIT3:
                three();
                break;
            case NUMPAD4:
            case DIGIT4:
                four();
                break;
            case NUMPAD5:
            case DIGIT5:
                five();
                break;
            case NUMPAD6:
            case DIGIT6:
                six();
                break;
            case NUMPAD7:
            case DIGIT7:
                seven();
                break;
            case NUMPAD8:
            case DIGIT8:
                eight();
                break;
            case NUMPAD9:
            case DIGIT9:
                nine();
                break;
            case ADD:
                plusProduct();
                break;
            case MINUS:
            case SUBTRACT:
                minusProduct();
                break;
            case ENTER:
                confirm();
                break;
            case DELETE:
                clear();
                break;
            case ESCAPE:
                discard();
        }
    }

}
