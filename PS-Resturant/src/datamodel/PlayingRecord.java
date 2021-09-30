package datamodel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import util.gui.button.TableViewButton;

public class PlayingRecord extends RecursiveTreeObject<PlayingRecord> {

    private int id;
    private String deviceName;
    private String fromTime;
    private String toTime;
    private String playingStatus;
    private String playingPrice;
    private String roomName;
    private int stat;
    private String finishTime;
    private JFXButton EditBtn;

    public PlayingRecord(int id, String deviceName, String fromTime, String toTime, String playingStatus, String Servprice, int stat, String roomName, String finishTime) {
        this.EditBtn = new TableViewButton("تعديل", new FontAwesomeIconView(FontAwesomeIcon.EDIT));
        this.id = id;
        this.deviceName = deviceName;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.playingStatus = playingStatus;
        this.playingPrice = Servprice;
        this.stat = stat;
        this.roomName = roomName;
        this.finishTime = finishTime;
    }

    public PlayingRecord(int id, String deviceName, String fromTime, String toTime, String playingStatus, String Servprice, int stat, String roomName) {
        this.EditBtn = new TableViewButton("تعديل", new FontAwesomeIconView(FontAwesomeIcon.EDIT));
        this.id = id;
        this.deviceName = deviceName;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.playingStatus = playingStatus;
        this.playingPrice = Servprice;
        this.stat = stat;
        this.roomName = roomName;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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

    public String getPlayingPrice() {
        return playingPrice;
    }

    public void setPlayingPrice(String Servprice) {
        this.playingPrice = Servprice;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public JFXButton getEditBtn() {
        return EditBtn;
    }

    public void setEditBtn(JFXButton EditBtn) {
        this.EditBtn = EditBtn;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

}
