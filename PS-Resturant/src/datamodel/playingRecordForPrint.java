package datamodel;

import dialog.edit.playing.room.playing.EditPlayingRecordController;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import util.db.DatabaseHandler;

public class playingRecordForPrint {

    public String diveiceName;
    public String fromTime;
    public String toTime;
    public String playingStatus;
    public String servprice;
    public int stat;
    public String roomName;
    DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("hh:mm a");
    public String differ;

    public playingRecordForPrint(String DiveiceName, String fromTime, String toTime, String playingStatus, String Servprice, int stat, String roomName) {
        this.diveiceName = DiveiceName;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.playingStatus = playingStatus;
        this.servprice = Servprice;
        this.stat = stat;
        this.roomName = roomName;
        LocalTime t = LocalTime.parse(toTime, dtf2);
        LocalTime f = LocalTime.parse(fromTime, dtf2);
        LocalTime dif = EditPlayingRecordController.getDifferrance(f, t);
        this.differ = dif.getHour() + " H" + "/" + DatabaseHandler.modifiMinutes(dif.getMinute()) + " M";
    }

    public String getDiveiceName() {
        return diveiceName;
    }

    public void setDiveiceName(String DiveiceName) {
        this.diveiceName = DiveiceName;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getPlayingStatus() {
        return playingStatus;
    }

    public void setPlayingStatus(String playingStatus) {
        this.playingStatus = playingStatus;
    }

    public String getServprice() {
        return servprice;
    }

    public void setServprice(String servprice) {
        this.servprice = servprice;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getDiffer() {
        return differ;
    }

    public void setDiffer(String differ) {
        this.differ = differ;
    }
}
