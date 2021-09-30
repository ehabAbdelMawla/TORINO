package datamodel;

import datamodel.DrinkTableRecordForPrint;
import java.util.ArrayList;
import java.util.List;
import util.Logger;
import util.gui.HelperMethods;

public class ReceetClass {

    public int id;
    public String actDate;
    public String userName;
    public String roomNum;
    public String from;
    public String to;
    public float preDis;
    public double tax;
    public double service;
    public float dis;
    public float inComeVal;
    public List<playingRecordForPrint> playing = new ArrayList<>();
    public List<DrinkTableRecordForPrint> drink = new ArrayList<>();

    public ReceetClass(int id, String actDate, String userName, String roomNum, String from, String to, float preDis, double tax, double service, float Dis, float inComeVal, List<playingRecordForPrint> playing, String drink) {
        this.id = id;
        this.actDate = actDate;
        this.userName = userName;
        this.roomNum = roomNum;
        this.from = from;
        this.to = to;
        this.tax = HelperMethods.formatNum(tax);
        this.service = HelperMethods.formatNum(service);
        this.preDis = HelperMethods.formatNum(preDis);
        this.dis = HelperMethods.formatNum(Dis);
        this.inComeVal = HelperMethods.formatNum(inComeVal);
        this.playing = playing;
        this.drink = workOnAllDrink(drink);
    }

    public String getActDate() {
        return actDate;
    }

    public void setActDate(String actDate) {
        this.actDate = actDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
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

    public float getPreDis() {
        return preDis;
    }

    public void setPreDis(float preDis) {
        this.preDis = HelperMethods.formatNum(preDis);
    }

    public float getDis() {
        return dis;
    }

    public void setDis(float Dis) {
        this.dis = HelperMethods.formatNum(Dis);
    }

    public float getInComeVal() {
        return inComeVal;
    }

    public void setInComeVal(float inComeVal) {
        this.inComeVal = HelperMethods.formatNum(inComeVal);
    }

    public List<playingRecordForPrint> getPlaying() {
        return playing;
    }

    public void setPlaying(List<playingRecordForPrint> playing) {
        this.playing = playing;
    }

    public List<DrinkTableRecordForPrint> getDrink() {
        return drink;
    }

    public void setDrink(ArrayList<DrinkTableRecordForPrint> drink) {
        this.drink = drink;
    }

    public List<DrinkTableRecordForPrint> workOnAllDrink(String AllDrinks) {
        List<DrinkTableRecordForPrint> list = new ArrayList<>();
        try {
            if (!AllDrinks.equals("")) {
                String[] records = AllDrinks.split("#");
                for (int i = 0; i < records.length; i++) {
                    String[] innerdata = records[i].split("@");
                    list.add(new DrinkTableRecordForPrint(
                            Integer.parseInt(innerdata[0]),
                            innerdata[2],
                            Integer.parseInt(innerdata[1]),
                            Float.parseFloat(innerdata[3]), "!"));
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in workOnAllDrink : " + e);
        }
        return list;
    }

    public List<playingRecordForPrint> workOnAllPlaying(String AllPlaying) {
        List<playingRecordForPrint> list = new ArrayList<>();
        try {
            if (!AllPlaying.equals("")) {
                String[] records = AllPlaying.split("#");
                for (int i = 0; i < records.length; i++) {
                    String[] innerdata = records[i].split("@");
                    list.add(
                            new playingRecordForPrint(innerdata[0],
                                    innerdata[1],
                                    innerdata[2],
                                    innerdata[3],
                                    innerdata[4], 0, "!"));
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in workOnAllPlaying: " + e);
        }
        return list;
    }

}
