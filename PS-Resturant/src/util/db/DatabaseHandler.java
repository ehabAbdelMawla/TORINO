/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.db;

import controlpanel.ControlPanel;
import datamodel.DailySheetRecord;
import datamodel.DrinkPriceTableRecord;
import datamodel.DrinkTableRecord;
import datamodel.PlayingPriceTableRecord;
import datamodel.ReceetClass;
import datamodel.TempMethods;
import datamodel.PlayingRecord;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import playing.price.PlayingPriceController;
import playing.rooms.RoomController;
import settings.products.NewProductController;
import util.Logger;
import util.gui.HelperMethods;
import util.gui.load.DialogHelper;
import util.printing.PrintingData;

/**
 *
 * @author Bayoumi
 */
public class DatabaseHandler {

    private static final String CLASS_NAME = DatabaseHandler.class.getName();
    // ==== DB Objects =====
    public static Connection con = null;
    public static ResultSet result = null;
    public static PreparedStatement stat = null;

    public static void connectToDataBase() {
        try {
            if (con == null) {
                Class.forName("org.sqlite.JDBC");
                con = DriverManager.getConnection("jdbc:sqlite:db/ddd.db");
                con.prepareStatement(" PRAGMA foreign_keys = ON").execute();
            }
        } catch (Exception ex) {
            con = null;
            Logger.writeLog("Exception In " + CLASS_NAME + " -> connectToDataBase:- " + ex);
        }
    }

    public static void insertIntoRoomsNote(String section, String note, String roomName) {
        try {
            String sql = "INSERT INTO roomsnotes VALUES(?,?,?,?)";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setInt(1, (int) Methods.GetMaximum("id", "roomsnotes"));
            DatabaseHandler.stat.setString(2, roomName);
            DatabaseHandler.stat.setString(3, section);
            DatabaseHandler.stat.setString(4, note);
            DatabaseHandler.stat.execute();
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASS_NAME + ".insertIntoRoomsNote:-" + e);
        }
    }

    public static ObservableList<String> selectPlayingPriceDeviceNames() {
        ObservableList<String> list = FXCollections.observableArrayList();
        try {
            list.clear();
            String removeMatchDevice = ControlPanel.getInstance().MATCH_DEVICE ? "" : "WHERE id>0 ";
            String sqlState = "SELECT device FROM PlayingPrice " + removeMatchDevice + " order by device ASC";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();
            String a;
            while (DatabaseHandler.result.next()) {
                a = DatabaseHandler.result.getString(1);
                list.add(a);
            }
            DatabaseHandler.result.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Error In " + CLASS_NAME + " selectPlayingPriceDeviceNames" + e);
            return list;
        }
    }

    public static String getPrice(int h, int m, String Kind, String DeviceName) {
        ObservableList<PlayingPriceTableRecord> list2 = TempMethods.selectPlayingData();
        double price = 0;
        double price2 = -5;
        int deviceId = -10;
        for (int i = 0; i < list2.size(); i++) {
            if (list2.get(i).deviceName.equalsIgnoreCase(DeviceName)) {
                if (Kind.equalsIgnoreCase("Single")) {
                    price2 = list2.get(i).singlePrice;
                } else {
                    price2 = list2.get(i).MultiPrice;
                }
                deviceId = list2.get(i).id;
                break;
            }
        }
        if (price2 < 0) {
            DialogHelper.getInstance().showOKAlert("- \u0647\u0630\u0627 \u0627\u0644\u062c\u0647\u0627\u0632 \u063a\u064a\u0631 \u0645\u0648\u062c\u0648\u062f!");
            return "0.0";
        }
        m = modifiMinutes(m);
        price += deviceId == 0 ? price2 : ((m / 60.0) + h) * price2;
        String st = EditPrice(price) + "";
        return HelperMethods.formatNum(price) + "";
    }

    public static int modifiMinutes(int m) {
        if (ControlPanel.getInstance().MODIFIY_MINUTES) {
            return (m % ControlPanel.getInstance().NUM_OF_MODIFIED > ControlPanel.getInstance().CONSTRAINT_MODIFIED_MINUTES) ? (((m / ControlPanel.getInstance().NUM_OF_MODIFIED) + 1) * ControlPanel.getInstance().NUM_OF_MODIFIED) : (((m / ControlPanel.getInstance().getInstance().NUM_OF_MODIFIED)) * ControlPanel.getInstance().NUM_OF_MODIFIED);
        }
        return m;
    }

    public static ObservableList<String> selectCategotiesNames(String s) {
        ObservableList<String> list = FXCollections.observableArrayList();
        try {
            list.clear();
            String sqlState = "SELECT categName FROM cafecategories " + s + " ORDER BY id ASC";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();
            while (DatabaseHandler.result.next()) {
                list.add(DatabaseHandler.result.getString(1));
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASS_NAME + " -> selectCategotiesNames :- " + e);
        }
        return list;
    }

    public static int getReceetID() {
        int reciptId = -100;
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT MAX(id) from dailySheet").executeQuery();
            while (res.next()) {
                reciptId = res.getInt(1) + 1;
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASS_NAME + " -> getReceetID :- " + e);
        }
        return reciptId < 0 ? 1 : reciptId;
    }

    public static double EditPrice(double num) {
        if (ControlPanel.getInstance().MODIFIY_PRICE) {
            double remain = num % ControlPanel.getInstance().PRICE_SEGMENT;
            if (remain >= ControlPanel.getInstance().CONSTRAINT_MODIFIED_PRICE) {
                num = (num) + (ControlPanel.getInstance().PRICE_SEGMENT - remain);
            }
        }
        return num;
    }

    public static ArrayList<String> getMyNotesForsection(String sec, String roomName) {
        ArrayList<String> notes = new ArrayList<>();
        try {
            String sql = "SELECT note FROM roomsnotes WHERE section=? And RoomNum=?";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setString(1, sec);
            DatabaseHandler.stat.setString(2, roomName);
            DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
            while (DatabaseHandler.result.next()) {
                notes.add(DatabaseHandler.result.getString(1));
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASS_NAME + " -> getMyNotesForsection :-" + e);
        }
        return notes;
    }

    public static float getDrinkPrice(String cusomeCateg, String cat, String DrinkName) {
        ObservableList<DrinkPriceTableRecord> list = TempMethods.selectDrinksData(cat);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).DrinkName.equalsIgnoreCase(DrinkName)) {
                if (cusomeCateg.equals("\u0639\u0645\u064a\u0644")) {
                    return list.get(i).Price;
                } else if (cusomeCateg.equals("\u0645\u0648\u0638\u0641")) {
                    return list.get(i).Price2;
                } else {
                    return 0;
                }
            }
        }
        return -5;
    }

    public static ObservableList<String> selectDrinksPriceDeviceNames(String s) {
        ObservableList<String> list = FXCollections.observableArrayList();
        try {
            list.clear();
            String sqlState = "SELECT drinkName FROM DrinkPrice " + s + " order by drinkName ASC";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();
            String a;
            while (DatabaseHandler.result.next()) {
                a = DatabaseHandler.result.getString(1);
                list.add(a);
            }
            DatabaseHandler.result.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Error In " + CLASS_NAME + " -> selectDrinksPriceDeviceNames:-" + e);
            return list;
        }
    }

    public static void insertInDrinkTable(DrinkTableRecord myserv) {
        try {
            String sqlString = "INSERT INTO " + RoomController.DRINKS_TABLENAME + " VALUES (?,?,?,?,?,?,?)"; //Insert Statement
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlString);
            DatabaseHandler.stat.setInt(1, myserv.id);
            DatabaseHandler.stat.setString(2, myserv.RoomName);
            DatabaseHandler.stat.setString(3, myserv.DName);
            DatabaseHandler.stat.setInt(4, myserv.Num);
            DatabaseHandler.stat.setFloat(5, myserv.Tprice);
            DatabaseHandler.stat.setString(6, NewProductController.convertArgsListToString(myserv.proarguments));
            DatabaseHandler.stat.setString(7, myserv.categ);
            DatabaseHandler.stat.execute();
            DatabaseHandler.stat.close();
        } catch (Exception e) {
            Logger.writeLog("Exception In " + CLASS_NAME + " insertInDrinkTable:-" + e);
        }
    }

    public static synchronized void ShowReceet(ReceetClass obj, String roomName, boolean printOption) {
        try {
            ObservableList<ReceetClass> r = FXCollections.observableArrayList();
            r.add(obj);
            String folderName = "\u0627\u0644\u0641\u0648\u0627\u062a\u064a\u0631/" + Methods.getMyDay() + "/\u0627\u0644\u0644\u0639\u0628";
            new File(folderName).mkdirs();
            String outputFile = folderName + "/" + roomName + " " + LocalDate.now() + " Serial " + LocalTime.now().toString().replaceAll(":", "").replaceAll("[.]", "") + ".pdf";
            JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(r, false);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("myObj", itemsJRBean);
            PrintingData.getInstance().setShopParameters(parameters);
            JasperReport jr = JasperCompileManager.compileReport("reports/leaveRoom.jrxml");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jr, parameters, new JREmptyDataSource());
            OutputStream outputStream = new FileOutputStream(new File(outputFile));
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            outputStream.close();
            if (printOption) {
                Runtime.getRuntime().exec("reports/PDFtoPrinter \"" + "../" + outputFile + "\"");
            }

        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASS_NAME + " -> showReceet :-" + e);
        }
    }

    public static void insertInDailySheet(DailySheetRecord myserv, String tableName, String customerName) {
        try {
            String sqlString = "INSERT INTO " + tableName + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlString);
            DatabaseHandler.stat.setInt(1, myserv.id);
            DatabaseHandler.stat.setString(2, myserv.actDate);
            DatabaseHandler.stat.setString(3, myserv.userName);
            DatabaseHandler.stat.setString(4, myserv.roomNum);
            DatabaseHandler.stat.setString(5, myserv.from);
            DatabaseHandler.stat.setString(6, myserv.to);
            DatabaseHandler.stat.setDouble(7, myserv.preDis);
            DatabaseHandler.stat.setDouble(8, myserv.tax);
            DatabaseHandler.stat.setDouble(9, myserv.service);
            DatabaseHandler.stat.setDouble(10, myserv.dis);
            DatabaseHandler.stat.setDouble(11, myserv.inComeVal);
            DatabaseHandler.stat.setString(12, myserv.playing);
            DatabaseHandler.stat.setString(13, myserv.drink);
            DatabaseHandler.stat.setString(14, myserv.playingNotes);
            DatabaseHandler.stat.setString(15, myserv.drinkNotes);
            DatabaseHandler.stat.setString(16, customerName);
            DatabaseHandler.stat.setInt(17, 1);
            DatabaseHandler.stat.execute();
            DatabaseHandler.stat.close();
        } catch (Exception e) {
            Logger.writeLog("Exception  In  " + CLASS_NAME + "->insertInDailySheet:-" + e);
        }
    }

    public static void insertStatementForDailyPullTable(DrinkTableRecord rec, int itterator, int flag) {
        String sql;
        try {
            sql = "INSERT INTO dailypullstore VALUES (?,?,?,?,?,?,?)";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setInt(1, (int) Methods.GetMaximum("id", "dailypullstore"));
            DatabaseHandler.stat.setString(2, Methods.getMyDay());
            if (flag == 0) {
                DatabaseHandler.stat.setString(3, rec.proarguments.get(itterator).argName);
                DatabaseHandler.stat.setDouble(4, rec.Num * rec.proarguments.get(itterator).numOfArg);
            } else {
                DatabaseHandler.stat.setString(3, rec.DName);
                DatabaseHandler.stat.setDouble(4, rec.Num);
            }
            DatabaseHandler.stat.setString(5, rec.DName);
            DatabaseHandler.stat.setInt(6, rec.Num);
            DatabaseHandler.stat.setInt(7, -1); // -1 for PS
            DatabaseHandler.stat.execute();
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASS_NAME + " -> insertStatementForDailyPullTable :-" + e);
        }
    }

    public static void insertDataIntoDailyStorePullData(ObservableList<DrinkTableRecord> data) {
        try {
            DrinkTableRecord rec;
            for (int i = 0; i < data.size(); i++) {
                rec = data.get(i);
                if (rec.proarguments.size() > 0) {
                    for (int j = 0; j < rec.proarguments.size(); j++) {
                        DatabaseHandler.insertStatementForDailyPullTable(rec, j, 0);
                    }
                } else {
                    DatabaseHandler.insertStatementForDailyPullTable(rec, 0, 1);
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASS_NAME + " -> insertDataIntoDailyStorePullData :- " + e);
        }
    }

    public static void insertInPlayingTable(PlayingRecord myserv) {
        try {
            String sqlString = "INSERT INTO " + RoomController.PLAY_TABLENAME + " VALUES (?,?,?,?,?,?,?,?,?)";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlString);
            DatabaseHandler.stat.setInt(1, (int) Methods.GetMaximum("id", RoomController.PLAY_TABLENAME));
            DatabaseHandler.stat.setString(2, myserv.getDeviceName());
            DatabaseHandler.stat.setString(3, myserv.getFromTime());
            DatabaseHandler.stat.setString(4, myserv.getToTime());
            DatabaseHandler.stat.setString(5, myserv.getPlayingStatus());
            DatabaseHandler.stat.setString(6, myserv.getPlayingPrice());
            DatabaseHandler.stat.setString(7, myserv.getRoomName());
            DatabaseHandler.stat.setInt(8, myserv.getStat());
            DatabaseHandler.stat.setString(9, myserv.getFinishTime());
            DatabaseHandler.stat.execute();
            DatabaseHandler.stat.close();
        } catch (Exception e) {
            Logger.writeLog("Exception  In " + CLASS_NAME + " -> insertInPlayingTable :-" + e);
        }
    }

    public static void updateDevicePromotionsNotations() {
        try {
            result = con.prepareStatement("SELECT * FROM devicePromotions ").executeQuery();
            while (result.next()) {
                PlayingPriceController.devicePercentage.set(result.getInt(1) + "%");
                PlayingPriceController.IS_DISCOUNT_ACTIVE.set(result.getInt(2) == 1);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception  In " + CLASS_NAME + " -> getDevicePormotionPercantage :-" + e);
        }

    }

    public static void updateDevicePromotionState(int i) {
        try {
            con.prepareStatement("UPDATE devicePromotions SET state=" + i).execute();
        } catch (Exception e) {
            Logger.writeLog("Exception  In " + CLASS_NAME + " -> updateDevicePromotionState :-" + e);
        }

    }

    public static Map<String, Boolean> selectDeviceNamesWhichApplayPormotion() {
        Map<String, Boolean> map = new HashMap<>();
        try {
            map.clear();
            String removeMatchDevice = ControlPanel.getInstance().MATCH_DEVICE ? "WHERE promotionState=1 " : "WHERE id>0 and promotionState=1 ";
            String sqlState = "SELECT device FROM PlayingPrice " + removeMatchDevice + " ORDER BY id ASC";
            result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();
            while (DatabaseHandler.result.next()) {
                map.put(result.getString(1), true);
            }
            result.close();
        } catch (Exception e) {
            Logger.writeLog("Exception In DatabaseHandler -> selectDeviceNamesWhichApplayPormotion():-" + e);
        }
        return map;
    }

    public static boolean canAddMoreRowsInTable(String tableName) {
        if (ControlPanel.getInstance().IS_DEMO) {
            try {

                ResultSet res = con.prepareStatement("SELECT COUNT(*) as count FROM " + tableName).executeQuery();
                while (res.next()) {
                    return !(res.getInt(1) >= ControlPanel.getInstance().MAX_NUM_OF_ROWS + (tableName.equalsIgnoreCase("PlayingPrice") || tableName.equalsIgnoreCase("users") ? 1 : 0));
                }
            } catch (Exception e) {
                System.out.println("Exception In DatabaseHandler -> canAddMoreRowsInTable() :- " + e);
            }
        }
        return true;
    }

    public static String[] getProductByBarCode(String barCode) {

        try {
            String[] data = new String[2];
            ResultSet res = con.prepareStatement("SELECT category,drinkName FROM drinkprice WHERE barCode='" + barCode + "'").executeQuery();
            while (res.next()) {
                data[0] = res.getString(1);
                data[1] = res.getString(2);
                return data;
            }
        } catch (Exception e) {
            Logger.writeLog("Exception In DatabaseHandler -> getProductByBarCode():-" + e);
        }
        return null;
    }
}
