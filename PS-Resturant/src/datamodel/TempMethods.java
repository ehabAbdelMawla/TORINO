package datamodel;

import controlpanel.ControlPanel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.Logger;
import util.db.DatabaseHandler;

public class TempMethods {

    public static ObservableList<PlayingPriceTableRecord> selectPlayingData() {
        ObservableList<PlayingPriceTableRecord> list = FXCollections.observableArrayList();
        try {
            list.clear();
            String removeMatchDevice = ControlPanel.getInstance().MATCH_DEVICE ? "" : "WHERE id>0";
            String sqlState = "SELECT * FROM PlayingPrice " + removeMatchDevice + " ORDER BY id ASC";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();
            while (DatabaseHandler.result.next()) {
                list.add(new PlayingPriceTableRecord(DatabaseHandler.result.getString(1), DatabaseHandler.result.getFloat(2), DatabaseHandler.result.getFloat(3), DatabaseHandler.result.getInt(4)));
            }
            DatabaseHandler.result.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Exception In TempMethos -> selectPlayingData():-" + e);
            return list;
        }
    }

    public static ObservableList<DrinkPriceTableRecord> selectDrinksData(String cat) {
        ObservableList<DrinkPriceTableRecord> list = FXCollections.observableArrayList();
        try {
            list.clear();
            String sqlState = "SELECT * FROM DrinkPrice where category=?";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlState);
            DatabaseHandler.stat.setString(1, cat);
            DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
            String drinkName, categName, args;
            float p1, p2;
            int id;
            DrinkPriceTableRecord rec;
            while (DatabaseHandler.result.next()) {
                drinkName = DatabaseHandler.result.getString(1);
                p1 = DatabaseHandler.result.getFloat(2);
                id = DatabaseHandler.result.getInt(3);
                p2 = DatabaseHandler.result.getFloat(4);
                categName = DatabaseHandler.result.getString(5);
                args = DatabaseHandler.result.getString(6);
                rec = new DrinkPriceTableRecord(id, drinkName, p1, p2, categName, Product.convertStringToListOfArgs(args));
                list.add(rec);
            }
            DatabaseHandler.result.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Exception In TempMethods -> selectDrinksData :- " + e);
            return list;
        }
    }

    public static void ClearData(String tableName) {
        try {
            String sql = "DELETE FROM " + tableName;
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.execute();
            DatabaseHandler.stat.close();
        } catch (Exception e) {
            Logger.writeLog("Exception In TempMethods -> ClearData(" + tableName + "):-" + e);
        }
    }

}
