package datamodel;

import util.db.Methods;
import util.gui.HelperMethods;


public class DrinkTableRecordForPrint {
    public int id;
    public String dName;
    public int num;
    public float totlaprice;
    public String RoomName;

    

    public DrinkTableRecordForPrint(int id, String DName, int Num, float Tprice, String RoomName) {
        this.id = id;
        this.dName = DName;
        this.num = Num;
        this.totlaprice =  HelperMethods.formatNum(Tprice);
        this.RoomName = RoomName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public float getTotlaprice() {
        return totlaprice;
    }

    public void setTotlaprice(float totlaprice) {
        this.totlaprice =  HelperMethods.formatNum(totlaprice);
    }

    public String getRoomName() {
        return RoomName;
    }

    public void setRoomName(String RoomName) {
        this.RoomName = RoomName;
    }


}
