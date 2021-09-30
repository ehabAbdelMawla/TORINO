package datamodel;

import dialog.edit.playing.room.playing.EditPlayingRecordController;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import util.db.DatabaseHandler;

public class playingRecordForPrint2 extends playingRecordForPrint {

    DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("hh:mm a");
    public String differ;

    public playingRecordForPrint2(String DiveiceName, String fromTime, String toTime, String playingStatus, String Servprice, int stat, String roomName) {
        super(DiveiceName, fromTime, toTime, playingStatus, Servprice, stat, roomName);
        LocalTime t = LocalTime.parse(toTime, dtf2);
        LocalTime f = LocalTime.parse(fromTime, dtf2);

        LocalTime dif = EditPlayingRecordController.getDifferrance(f, t);
        this.differ = dif.getHour() + " H" + "/" + DatabaseHandler.modifiMinutes(dif.getMinute()) + " M";
    }

    public DateTimeFormatter getDtf2() {
        return dtf2;
    }

    public void setDtf2(DateTimeFormatter dtf2) {
        this.dtf2 = dtf2;
    }

    public String getDiffer() {
        return differ;
    }

    public void setDiffer(String differ) {
        this.differ = differ;
    }

}
