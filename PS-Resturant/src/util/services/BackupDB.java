/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.services;

import datamodel.settings.Preferences;
import datamodel.settings.PreferencesType;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import util.file.Utility;

/**
 *
 * @author Bayoumi
 */
public class BackupDB {

    private static File location = null;

    public static void start() throws Exception {
        File selectedFile = getLocation();
        Path from = Paths.get(new File("db/ddd.db").getAbsolutePath());
        System.out.println(from.toString());
        Path to = Paths.get(selectedFile.getAbsolutePath() + "/PS-Restaurant - " + LocalDate.now() + " - " + System.currentTimeMillis() + ".db");
        Utility.copyIfNotExist(from, to);
    }

    public static File getLocation() throws Exception {
        if (location == null) {
            Utility.createDirectory(System.getenv("LOCALAPPDATA") + "/PS-Restaurant/db");
            return new File(Preferences.getInstance().get(PreferencesType.BackupLocation, System.getenv("LOCALAPPDATA") + "/PS-Restaurant/db"));
        }
        return location;
    }

    public static void setLocation(File target) throws Exception {
        location = target;
        if (location != null) {
            Preferences.getInstance().set(PreferencesType.BackupLocation, location.getAbsolutePath());
        } else {
            Preferences.getInstance().set(PreferencesType.BackupLocation, null);
        }
    }
}
