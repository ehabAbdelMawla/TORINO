package datamodel;

import controlpanel.ControlPanel;
import archive.debt.DebtDetailsController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.io.IOException;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import util.gui.button.TableViewButton;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.BuilderUI;
import util.gui.HelperMethods;
import util.gui.load.Location;

public class ArchOfDays extends RecursiveTreeObject<ArchOfDays> {

    public TableViewButton Day;
    public double PSIncome;
    public double cafeIncome;
    public TableViewButton deptVal;
    public double totIncome;
    public double netIncome;
    public double expanses;
    public double el3ohda;

    public ArchOfDays(String Day, double PSIncome, double cafeIncome, double deptVal, double expanses, double el3ohda) {
        this.Day = new TableViewButton(Day);
        this.PSIncome = HelperMethods.formatNum(PSIncome);
        this.cafeIncome = HelperMethods.formatNum(cafeIncome);
        this.totIncome = HelperMethods.formatNum(PSIncome
                + cafeIncome + deptVal);
        this.deptVal = new TableViewButton(HelperMethods.formatNum(deptVal) + "");
        this.deptVal.setOnAction(this::openDebtDetails);
        this.expanses = HelperMethods.formatNum(expanses);
        this.el3ohda = HelperMethods.formatNum(el3ohda);
        this.netIncome = HelperMethods.formatNum(ControlPanel.getInstance().EXPENSESS_FROM_DAILY_INCOME ?  HelperMethods.formatNum(totIncome - expanses) : totIncome);
    }

    private void openDebtDetails(ActionEvent event) {
        try {
            // load
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Location.getInstance().get("DEPT_DETAILS")));
            Scene Scene_DebtDetails = new Scene(fxmlLoader.load());
            Stage Stage_DebtDetails = BuilderUI.initStageDecorated(Scene_DebtDetails, "Data", "info");

            BuilderUI.HandlerCTRL_W(Scene_DebtDetails, Stage_DebtDetails, () -> {
                Stage_DebtDetails.close();
            });
            // config data
            DebtDetailsController controller = fxmlLoader.getController();
            ObservableList<deptDetails> listOfBills = getDebtRecords();
            if (listOfBills.isEmpty()) {
                return;
            }
            controller.setData(listOfBills);
            // show stage
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            Stage_DebtDetails.setX(bounds.getWidth() / 2 - (692 / 2));
            Stage_DebtDetails.setY(bounds.getHeight() / 2 - (442 / 2));
            Stage_DebtDetails.showAndWait();
        } catch (IOException ex) {
            Logger.writeLog("Exception in " + getClass().getName() + ".openDebtDetails() :-" + ex);
        }
    }

    private ObservableList<deptDetails> getDebtRecords() {
        ObservableList<deptDetails> deptData = FXCollections.observableArrayList();
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT receetId,customerName,price FROM deptData WHERE daily=0 AND DATE(date)='" + this.Day.getText() + "'").executeQuery();
            while (res.next()) {
                deptData.add(new deptDetails(res.getInt(1),
                        res.getString(2), res.getDouble(3)));
            }

        } catch (Exception e) {
            Logger.writeLog("Exception in ArchOfDays -> getDebtRecords() :- " + e);
        }
        return deptData;
    }

    public JFXButton getDay() {
        return Day;
    }

    public void setDay(JFXButton Day) {
        this.Day = (TableViewButton) Day;
    }

    public double getPSIncome() {
        return PSIncome;
    }

    public void setPSIncome(double PSIncome) {
        this.PSIncome = HelperMethods.formatNum(PSIncome);
    }

    public double getCafeIncome() {
        return cafeIncome;
    }

    public void setCafeIncome(double cafeIncome) {
        this.cafeIncome = HelperMethods.formatNum(cafeIncome);
    }

    public double getTotIncome() {
        return totIncome;
    }

    public void setTotIncome(double totIncome) {
        this.totIncome = HelperMethods.formatNum(totIncome);
    }

    public double getExpanses() {
        return expanses;
    }

    public void setExpanses(double expanses) {
        this.expanses = HelperMethods.formatNum(expanses);
    }

    public double getEl3ohda() {
        return el3ohda;
    }

    public void setEl3ohda(double el3ohda) {
        this.el3ohda = HelperMethods.formatNum(el3ohda);
    }

    public JFXButton getDeptVal() {
        return deptVal;
    }

    public void setDeptVal(JFXButton deptVal) {
        this.deptVal = (TableViewButton) deptVal;
    }

    public double getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(double netIncome) {
        this.netIncome = netIncome;
    }

    public void updateIncome() {
         this.netIncome = HelperMethods.formatNum(ControlPanel.getInstance().EXPENSESS_FROM_DAILY_INCOME ?  HelperMethods.formatNum(totIncome - expanses) : totIncome);
    }

}
