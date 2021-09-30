package datamodel.settings;

import java.sql.ResultSet;
import util.Logger;
import util.db.DatabaseHandler;

public class Preferences {

    private static Preferences instance;

    public static Preferences getInstance() throws Exception {
        if (instance == null) {
            instance = new Preferences();
        }
        return instance;
    }

    private Preferences() throws Exception {
        if (DatabaseHandler.con == null) {
            throw new Exception("Database not connected");
        } else {
            DatabaseHandler.con.prepareStatement("CREATE TABLE IF NOT EXISTS \"preferences\" ( \"key\" TEXT, \"value\" TEXT, PRIMARY KEY(\"key\") );").execute();
        }
    }

    private void save(PreferencesType key, String value) {
        try {
            System.out.println("INSERT INTO preferences (key,value) VALUES('" + key + "','" + value + "')");
            DatabaseHandler.con.prepareStatement("INSERT INTO preferences (key,value) VALUES('" + key + "','" + value + "')").execute();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + ".save() : " + e);
        }
    }

    private void update(PreferencesType key, String value) {
        try {
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement("UPDATE preferences set value=? WHERE key=?");
            DatabaseHandler.stat.setString(1, value);
            DatabaseHandler.stat.setString(2, key.toString());
            DatabaseHandler.stat.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + ".update() : " + e);
        }
    }

    private boolean isExist(PreferencesType key) {
        try {
            ResultSet result = DatabaseHandler.con.prepareStatement("SELECT count(*) as exist FROM preferences WHERE key='" + key + "'").executeQuery();
            if (result.next()) {
                return result.getString("exist").equals("1");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + ".isExist() : " + e);
        }
        return false;
    }

    public void set(PreferencesType key, String value) {
        System.out.println("set >> " + key + ": [" + value + "]");
        if (isExist(key)) {
            System.out.println("Key Exists..");
            update(key, value);
        } else {
            System.out.println("New Key..");
            save(key, value);
        }
    }

    public String get(PreferencesType key, String defaultValue) {
        try {
            // if exist => return found value
            ResultSet result = DatabaseHandler.con.prepareStatement("SELECT value FROM preferences WHERE key='" + key + "'").executeQuery();
            if (result.next()) {
                if (result.getString("value") != null) {
                    return result.getString("value");
                }
                return defaultValue;
            }
            // not exist => insert & return default value
            save(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + ".get() : " + e);
        }
        return defaultValue;
    }

    public boolean getBoolean(PreferencesType key, String defaultValue) {
        return get(key, defaultValue).equalsIgnoreCase("true");
    }
}
