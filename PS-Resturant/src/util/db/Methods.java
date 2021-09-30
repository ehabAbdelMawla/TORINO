/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.db;

/**
 *
 * @author Ehab Abdel Mawla
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import util.Logger;

public class Methods {

    private static final String CLASSNAME = Methods.class.getName();

    // ########### DATABASE ##################
    //===== Get Maximum Value in Field In Table 
    public static double GetMaximum(String FieldName, String tableName) {
        try {
            ResultSet result = DatabaseHandler.con.prepareStatement("SELECT MAX(" + FieldName + ") FROM " + tableName).executeQuery();
            while (result.next()) {
                return (result.getDouble(1) + 1);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  GetMaximum() " + e);
        }
        return 1;
    }

    // OverLoding
    public static double GetMaximum(String FieldName, String tableName, String condistion) {
        try {
            ResultSet result = DatabaseHandler.con.prepareStatement("SELECT MAX(" + FieldName + ") FROM " + tableName + " " + condistion).executeQuery();
            while (result.next()) {
                return (result.getDouble(1) + 1);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  GetMaximum() "+e);
        }

        return 1;
    }

    //get the Day and Time For Sql Column
    public static String getDateAndTime() {
        return getMyDay() + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static String getMyDay() {
        try {
            String sql = "SELECT * FROM Myday";
            ResultSet result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (result.next()) {
                return result.getString(1);
            }
            sql = "INSERT INTO Myday Values (?)";
            PreparedStatement stat = DatabaseHandler.con.prepareStatement(sql);
            stat.setString(1, LocalDate.now().toString());
            stat.execute();
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  GetMaximum()");
        }
        return getMyDay();
    }

    public static String getMxOrMnDay(String op) {
        try {
            ResultSet result = DatabaseHandler.con.prepareStatement("SELECT " + op + "(Date(date)) FROM Receets").executeQuery();

            while (result.next()) {
                return result.getString(1);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  getMxOrMnDay()");
        }
        return "";
    }

//    Get Data Of Receet
    public static String getThisFromReceertData(String target) {
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT " + target + " FROM receetdata").executeQuery();
            while (res.next()) {
                return res.getString(1);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  getThisFromReceertData()");
        }
        return "";
    }

    //Claer Data
    public static void clearData(String tableName) {
        try {
            DatabaseHandler.con.prepareStatement("DELETE FROM " + tableName).execute();
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  ExitKeyCodeCombination()");
        }
    }

//    Get Count of Any Critrial in any Table
    public static int getCount(String sql) {
        int count = 0;
        try {
            ResultSet result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (result.next()) {
                count = result.getInt(1);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  getCount()");
        }
        return count;
    }

// .... Get Char of Category .....
    public static String getCharacterOfCardCateg(int id, boolean isDigit) {
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT categoryCharacter from cardsCategories WHERE id=" + id + "").executeQuery();
            while (res.next()) {
                return res.getString(1) + (isDigit ? "" : "X");
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  getCharacterOfCardCateg()");
        }
        return " ";
    }

    // .... Get National Currency .....
    public static double getNationalCurrency() {
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT value from nationalCurrency").executeQuery();
            while (res.next()) {
                return res.getDouble(1);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  getNationalCurrency()");
        }
        return 1;
    }

    public static double get(String sql) {
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (res.next()) {
                return res.getDouble(1);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + CLASSNAME + "->  get()");
        }
        return 0;
    }
//
////    .... check Permision  ....
//    public static boolean isAdmin(){
//        try {
//            return LoginContr.getAccess().equalsIgnoreCase("ادمن");
//        }
//        catch(Exception e){
//            Logger.error(null,e,Methods.class.getName()+" .isAdmin()");
//        }
//return false;
//    }

    public static int abilityForDelete(String sql) {
        try {
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (DatabaseHandler.result.next()) {
                return 1;
            }
        } catch (SQLException e) {
            Logger.writeLog("Exception in " + Methods.class.getName() + " -> abilityForDelete :- " + e);
        }
        return 0;
    }

    public static float getTotalMoney(String tableName) {
        float myMoney = 0;
        try {
            String sqlState = "SELECT SUM(price) FROM " + tableName;
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();
            String s;
            while (DatabaseHandler.result.next()) {
                s = DatabaseHandler.result.getString(1);
                try {
                    myMoney += Float.parseFloat(s);
                } catch (Exception e) {
                }
            }
            DatabaseHandler.result.close();
        } catch (Exception e) {
            Logger.writeLog("Exception in  " + Methods.class.getName() + " -> getTotalMoney:- " + e + "  " + tableName);
        }
        return myMoney;
    }
}
