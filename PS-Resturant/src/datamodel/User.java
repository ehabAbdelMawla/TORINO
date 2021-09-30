package datamodel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.sql.ResultSet;
import util.gui.button.TableViewButton;
import util.Logger;
import util.db.DatabaseHandler;

public class User extends RecursiveTreeObject<User> {

//    public enum Privileges {
//        
//    }
    // =========== Helpers ===========
    public static User CurrentUser = null;

    public static User getActiveUser() {
        User act = null;
        try {
            String sql = "Select * from users where active=1";
            ResultSet result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (result.next()) {
                act = new User(result.getString(1), result.getString(2), result.getString(3), result.getInt(4));
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + User.class.getName() + ".getActiveUser() : " + e);
        }
        return act;
    }

    public String userName;
    public String password;
    public String aceessConstrains;
    public int active;
    public TableViewButton editBTN = new TableViewButton("تعديل", new FontAwesomeIconView(FontAwesomeIcon.EDIT));
    public TableViewButton DelBTN = new TableViewButton("حذف", new FontAwesomeIconView(FontAwesomeIcon.TRASH));

    // Used for Retrive product User 
    public User(String userName) {
        this.userName = userName;
    }

    public User(String userName, String password, String aceessConstrains, int active) {
        this.userName = userName;
        this.password = password;
        this.aceessConstrains = aceessConstrains;
        this.active = active;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAceessConstrains() {
        return aceessConstrains;
    }

    public void setAceessConstrains(String aceessConstrains) {
        this.aceessConstrains = aceessConstrains;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public JFXButton getEditBTN() {
        return editBTN;
    }

    public void setEditBTN(JFXButton editBTN) {
        this.editBTN = (TableViewButton) editBTN;
    }

    public JFXButton getDelBTN() {
        return DelBTN;
    }

    public void setDelBTN(JFXButton DelBTN) {
        this.DelBTN = (TableViewButton) DelBTN;
    }

    public boolean equals(User u) {
        try {
            return this.userName.equals(u.userName)
                    && this.password.equals(u.password)
                    && this.aceessConstrains.equals(u.aceessConstrains);
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + ".equals() :-" + e);
        }
        return false;
    }

}
