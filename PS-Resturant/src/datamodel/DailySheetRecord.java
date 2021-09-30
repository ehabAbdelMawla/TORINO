package datamodel;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.sql.ResultSet;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.HelperMethods;
import util.gui.button.TableViewButton;
import util.gui.load.DialogHelper;
import util.gui.load.LoadHelper;

public class DailySheetRecord extends RecursiveTreeObject<DailySheetRecord> {

    public TableViewButton idButton;
    public int id;
    public String actDate;
    public String userName;
    public String roomNum;
    public String from;
    public String to;
    public double preDis;
    public double tax;
    public double service;
    public double dis;
    public double inComeVal;
    public String playing;
    public String drink;
    public String playingNotes;
    public String drinkNotes;
    public TableViewButton customerName;

    public DailySheetRecord(int id, String actDate, String userName, String roomNum, String from, String to, double preDis, double tax, double service, double dis, double inComeVal, String playing, String drink, String playingNotes, String drinkNotes, String customerName) {
        this.id = id;
        this.idButton = new TableViewButton(String.valueOf(id));
        this.actDate = actDate;
        this.userName = userName;
        this.roomNum = roomNum;
        this.from = from;
        this.to = to;
        this.preDis = HelperMethods.formatNum(preDis);
        this.tax = HelperMethods.formatNum(tax);
        this.service = HelperMethods.formatNum(service);
        this.dis = HelperMethods.formatNum(dis);
        this.inComeVal = HelperMethods.formatNum(inComeVal);
        this.playing = playing;
        this.drink = drink;
        this.playingNotes = playingNotes;
        this.drinkNotes = drinkNotes;
        this.customerName = new TableViewButton((customerName == null || customerName.trim().equals("")) ? "لا يوجد" : customerName);
        this.customerName.setOnAction((event) -> {
            String customer = this.customerName.getText();
            if (customer != null && !customer.equals("لا يوجد") && !customer.equals("")) {
                Customer dataOFCustomer = Customer.getDataOFCustomer(customer);
                if (dataOFCustomer == null) {
                    return;
                }
                DialogHelper.getInstance().viewCustomer(dataOFCustomer, false, true);
            }
        });

        this.idButton.setOnAction((ActionEvent event) -> {
            try {
                LoadHelper.getInstance().recordDetailsController.setData(this.drink, this.playing, this.drinkNotes, this.playingNotes);
                LoadHelper.getInstance().RoomRecordDeails.showAndWait();

            } catch (Exception e) {
                Logger.writeLog("Exception in " + getClass().getName() + " -> (setOnAction) showDetails:-" + e);
            }
        });
    }

    public String getActDate() {
        return actDate;
    }

    public void setActDate(String actDate) {
        this.actDate = actDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public double getPreDis() {
        return preDis;
    }

    public void setPreDis(double preDis) {
        this.preDis = HelperMethods.formatNum(preDis);
    }

    public double getDis() {
        return dis;
    }

    public void setDis(double dis) {
        this.dis = HelperMethods.formatNum(dis);
    }

    public double getInComeVal() {
        return inComeVal;
    }

    public void setInComeVal(double inComeVal) {
        this.inComeVal = HelperMethods.formatNum(inComeVal);
    }

    public TableViewButton getIdButton() {
        return idButton;
    }

    public void setIdButton(TableViewButton idButton) {
        this.idButton = idButton;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getService() {
        return service;
    }

    public void setService(double service) {
        this.service = service;
    }

    public String getPlaying() {
        return playing;
    }

    public void setPlaying(String playing) {
        this.playing = playing;
    }

    public String getDrink() {
        return drink;
    }

    public void setDrink(String drink) {
        this.drink = drink;
    }

    public String getPlayingNotes() {
        return playingNotes;
    }

    public void setPlayingNotes(String playingNotes) {
        this.playingNotes = playingNotes;
    }

    public String getDrinkNotes() {
        return drinkNotes;
    }

    public void setDrinkNotes(String drinkNotes) {
        this.drinkNotes = drinkNotes;
    }

    public TableViewButton getCustomerName() {
        return customerName;
    }

    public void setCustomerName(TableViewButton customerName) {
        this.customerName = customerName;
    }

    public static ArrayList<DailySheetRecord> getCustomerData(String customerName) {
        ArrayList<DailySheetRecord> data = new ArrayList<>();
        try {
            ResultSet result = DatabaseHandler.con.prepareStatement("SELECt * FROM dailysheet WHERE customerName='" + customerName + "'").executeQuery();
            while (result.next()) {
                data.add(new DailySheetRecord(
                        result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getString(5),
                        result.getString(6),
                        result.getDouble(7),
                        result.getDouble(8),
                        result.getDouble(9),
                        result.getDouble(10),
                        result.getDouble(11),
                        result.getString(12),
                        result.getString(13),
                        result.getString(14),
                        result.getString(15),
                        result.getString(16)
                ));
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in DailySheetRecord -> getCustomerData() :- " + e);
        }
        return data;
    }
}
