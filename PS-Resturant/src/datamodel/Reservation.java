package datamodel;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import playing.rooms.util.RoomBox;
import playing.hall.PlayingRoomsHallController;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import reservation.ChooseRoomController;
import util.db.DBCRUD;
import util.db.DBField;
import util.db.DatabaseHandler;
import util.db.Methods;
import util.gui.button.TableViewButton;
import util.gui.load.DialogHelper;
import util.gui.load.Location;

public class Reservation extends DBCRUD<Reservation> {

    DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("hh:mm a");
    private static final String DB_TABLENAME = "reservations";
    public static ObservableList<Reservation> reservationObservableList = FXCollections.observableArrayList();
    private static Timer alarm = new Timer();
    private ReservationAlarm task;
//    Instance Attributes
    private int id;
    private String day;
    private String time;
    private Customer client;
    private String device;
    private String type;
    private String specialGame;
    private String notes;
    private TableViewButton startPlay;
    private TableViewButton cancelReservation;
    public static int mins = 2;

//    Creation Constructor 
    public Reservation(String day, String time, Customer client, String device, String type, String specialGame, String notes) {
        super(DB_TABLENAME);
        this.id = (int) Methods.GetMaximum("id", "reservations");
        this.day = day;
        this.time = time;
        this.client = client;
        this.device = device;
        this.type = type;
        this.specialGame = specialGame;
        this.notes = notes;
        this.setFields(createMap());
    }

//    Retrive Constructor
    public Reservation(int id, String day, String time, int clientId, String device, String type, String specialGame, String notes) throws ParseException {
        super(DB_TABLENAME);
        this.id = id;
        this.day = day;
        this.time = LocalTime.parse(time).format(dtf2);
        this.client = Customer.customersData.stream().filter((t) -> {
            return t.getCode() == clientId;
        }).findAny().orElse(null);

        this.device = device;
        this.type = type;
        this.specialGame = specialGame;
        this.notes = notes;
        this.startPlay = new TableViewButton("start", new FontAwesomeIconView(FontAwesomeIcon.PLAY));
        this.startPlay.setOnAction(this::startAction);
        this.cancelReservation = new TableViewButton("cancel", new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        this.cancelReservation.setOnAction(this::cancelAction);
        task = new ReservationAlarm(client.getFullName(), device, this.time);
        alarm.schedule(task, new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(LocalDate.now().plusDays((LocalTime.parse(time).minusMinutes(mins).isBefore(LocalTime.now()) && !LocalTime.parse(time).minusMinutes(mins).isAfter(LocalTime.parse(time))) ? 1 : 0) + " " + LocalTime.parse(time).minusMinutes(mins)));
        this.setFields(createMap());
    }

    public void startAction(ActionEvent event) {

        try {
            ObservableList<String> availableRooms = FXCollections.observableArrayList(PlayingRoomsHallController.roomBoxes.stream().filter(room -> room.isAvailable()).map(RoomBox::getRoomNumber).collect(Collectors.toList()));
            if (availableRooms.isEmpty()) {
                DialogHelper.getInstance().showOKAlert("جميع الغرف مشغولة حالياً");
                return;
            }
            String choosenRoom = chooseRoom(availableRooms);
            if (!choosenRoom.equals("")) {
                startTime(choosenRoom);
            }
        } catch (Exception e) {
            util.Logger.writeLog("Exception in " + getClass().getName() + " startAction() :- " + e);
        }
    }

    private void clearTask() {
        if (task != null) {
            task.cancel();
        }
    }

    private void startTime(String roomNum) {
        RoomBox roomBox = PlayingRoomsHallController.roomBoxes.stream().filter(room -> room.getRoomNumber().equalsIgnoreCase(roomNum)).findAny().orElse(null);
        roomBox.roomView.getRoomController().setDataAndStart(device, type, client.getFullName(), true);
        // Remove Reservation
        this.remove();
        clearTask();
        Reservation.fetchData();
    }

    private String chooseRoom(ObservableList<String> availableRooms) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Location.getInstance().get("TRANSFER_CHOOSE_ROOM")));
            Scene scene = new Scene(loader.load());
            ChooseRoomController controller = loader.getController();
            controller.initData(availableRooms);
            Stage stage = util.gui.BuilderUI.initStageUnDecorated(scene, "Choose Room", "info");
            stage.showAndWait();
            return controller.roomChoosen;
        } catch (Exception ex) {
            util.Logger.writeLog("Exception in " + getClass().getName() + " () :- " + ex);
        }
        return "";
    }

    public void cancelAction(ActionEvent event) {
        if (DialogHelper.getInstance().showConfirmAlert("حذف الحجز ؟") == 1) {
            this.remove();
            clearTask();
            Reservation.fetchData();
        }
    }

    public Customer getClient() {
        return client;
    }

    public void setClient(Customer client) {
        this.client = client;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpecialGame() {
        return specialGame;
    }

    public void setSpecialGame(String specialGame) {
        this.specialGame = specialGame;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public TableViewButton getStartPlay() {
        return startPlay;
    }

    public void setStartPlay(TableViewButton startPlay) {
        this.startPlay = startPlay;
    }

    public TableViewButton getCancelReservation() {
        return cancelReservation;
    }

    public void setCancelReservation(TableViewButton cancelReservation) {
        this.cancelReservation = cancelReservation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public ArrayList<DBField> createMap() {
        ArrayList<DBField> map = new ArrayList<>();
        map.add(new DBField(id, "id", "PK"));
        map.add(new DBField(day, "date", "NN"));
        map.add(new DBField(time, "time", "NN"));
        map.add(new DBField(client.getCode(), "clientId", "NN"));
        map.add(new DBField(device, "device", "NN"));
        map.add(new DBField(type, "type", "NN"));
        map.add(new DBField(specialGame, "specialGame", "NN"));
        map.add(new DBField(notes, "notes", "NN"));
        return map;
    }

    public static void customeClear() {
        reservationObservableList.forEach(Reservation::clearTask);
        reservationObservableList.clear();
        alarm.purge();
    }

    public static void fetchData() {
        customeClear();
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT * FROM reservations").executeQuery();
            while (res.next()) {
                reservationObservableList.add(new Reservation(
                        res.getInt(1),
                        res.getString(2),
                        res.getString(3),
                        res.getInt(4),
                        res.getString(5),
                        res.getString(6),
                        res.getString(7),
                        res.getString(8)
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            util.Logger.writeLog("Exception in Reservation -> fetchData : " + e);
        }
    }

}
