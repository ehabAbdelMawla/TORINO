package dialog.edit.expenses;

import controlpanel.ControlPanel;
import datamodel.ExpenseRecord;
import com.jfoenix.controls.JFXTextField;
import static expenses.ExpensesController.abilityToExpanse;
import static expenses.ExpensesController.dealWithIprest;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import datamodel.User;
import restaurant.RestaurantHomeController;
import util.gui.load.DialogHelper;

public class EditExpensesController implements Initializable {

    // editExpensesTable
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    public static ExpenseRecord previousRecord;
    public static int EXFlag;

    // === GUI Objects ===
    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private JFXTextField nameOfExpensessTF;
    @FXML
    private JFXTextField moneyOfExpenssess;
    @FXML
    private TextField reason;
    @FXML
    private Label error;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        EXFlag = 0;
        nameOfExpensessTF.setText(previousRecord.need);
        moneyOfExpenssess.setText(previousRecord.price + "");
        error.setVisible(false);
//        nameOfExpensessTF.setDisable(true);
    }

    @FXML
    private void closeWindow(Event event) {
        error.setVisible(false);
        ((Stage) ((Node) (event.getSource())).getScene().getWindow()).close();
    }

    @FXML
    private void RootMousePressed(Event event) {
        MouseEvent e = (MouseEvent) event;
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    }

    @FXML
    private void RootMouseDragged(Event event) {
        MouseEvent e = (MouseEvent) event;
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setX(e.getScreenX() - xOffset);
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setY(e.getScreenY() - yOffset);
    }

    @FXML
    private void editExpensesss(Event event) {
        if ("".equals(reason.getText().trim())) {
            error.setVisible(true);
            return;
        }
        if (nameOfExpensessTF.getText().trim().equals("") || moneyOfExpenssess.getText().trim().equals("")) {
            DialogHelper.getInstance().showOKAlert("- يجب ادخال جميع البيانات");
            return;
        }
        float pr = 0.0f;
        try {
            pr = Float.parseFloat(moneyOfExpenssess.getText().trim());
            if (pr < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            DialogHelper.getInstance().showOKAlert("- التكلفة التي ادخلتها خاطئة.");
            return;
        }

        if (!ControlPanel.getInstance().EXPENSESS_FROM_DAILY_INCOME) {
            if (pr > previousRecord.price) {
                int x = abilityToExpanse(pr - previousRecord.price);
                if (x == -5) {
                    DialogHelper.getInstance().showOKAlert("- هذا المبلغ غير متوافر ف العهدة!");
                    return;
                }

            }
            dealWithIprest(previousRecord.price, "push");
            dealWithIprest(pr, "pull");
        }

        String note = "";
        if (previousRecord.price != pr && !nameOfExpensessTF.getText().trim().equalsIgnoreCase(previousRecord.need)) {
            note = String.format("قام %s بتعديل اسم الغرض من %s الي %s ,وتعديل سعر الغرض من  %s الي  %s السبب: %s وقت التعديل: %s",
                    User.CurrentUser.userName, previousRecord.need, nameOfExpensessTF.getText().trim(), previousRecord.price, pr, reason.getText().trim(), LocalTime.now().format(DATE_TIME_FORMATTER));
        } else if (previousRecord.price != pr) {
            note = String.format("قام %s بتعديل سعر الغرض من  %s الي  %s السبب: %s وقت التعديل: %s",
                    User.CurrentUser.userName,  previousRecord.price, pr, reason.getText().trim(), LocalTime.now().format(DATE_TIME_FORMATTER));
        } else {
            note = String.format("قام %s بتعديل اسم الغرض من %s الي %s السبب: %s وقت التعديل: %s",
                    User.CurrentUser.userName, previousRecord.need, nameOfExpensessTF.getText().trim(), reason.getText().trim(), LocalTime.now().format(DATE_TIME_FORMATTER));
        }
        if (!note.equals("")) {
            RestaurantHomeController.insertaNoteForCafe("EX", note);
            previousRecord.need = nameOfExpensessTF.getText().trim();
            previousRecord.price = pr;
            EXFlag = 1;
        }
        closeWindow(event);
    }

    @FXML
    private void KEVENT(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            editExpensesss((Event) event);
        } else if (event.getCode().equals(KeyCode.ESCAPE)) {
            closeWindow((Event) event);
        }
    }
}
