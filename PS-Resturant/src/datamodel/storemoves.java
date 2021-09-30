package datamodel;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import util.db.Methods;
import util.gui.HelperMethods;

public class storemoves extends RecursiveTreeObject<storemoves> {

    public String Day;
    public String Time;
    public String User;
    public String ProName;
    public Float count;

    public storemoves() {
    }

    public storemoves(String Day, String Time, String User, String ProName, Float count) {
        this.Day = Day;
        this.Time = Time;
        this.User = User;
        this.ProName = ProName;
        this.count = HelperMethods.formatNum(count);
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String Day) {
        this.Day = Day;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String Time) {
        this.Time = Time;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String User) {
        this.User = User;
    }

    public String getProName() {
        return ProName;
    }

    public void setProName(String ProName) {
        this.ProName = ProName;
    }

    public Float getCount() {
        return count;
    }

    public void setCount(Float count) {
        this.count = HelperMethods.formatNum(count);
    }
}
