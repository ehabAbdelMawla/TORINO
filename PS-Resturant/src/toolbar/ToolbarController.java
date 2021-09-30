package toolbar;

import controlpanel.ControlPanel;
import dialog.admin_validation.AdminValidationController;
import static dialog.admin_validation.AdminValidationController.tempAdminpass;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import dailysheet.DailySheetController;
import datamodel.Customer;
import datamodel.Product;
import datamodel.Reservation;
import datamodel.tablesNames;
import expenses.ExpensesController;
import homepage.HomePageController;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.gui.load.Location;
import products.ProductsController;
import restaurant.hall.HallController;
import playing.rooms.RoomController;
import util.gui.button.CategoryButton;
import util.Logger;
import util.gui.load.LoadHelper;
import datamodel.User;
import datamodel.settings.Preferences;
import datamodel.settings.PreferencesType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import reservation.ReservationsController;
import restaurant.RestaurantHomeController;
import settings.devices.DevicesSettingsController;
import settings.products.AddCategoryController;
import util.printing.CashDrawer;
import util.validation.OnlineValidation;

public class ToolbarController implements Initializable {

    @FXML
    private JFXButton GamingHall_BTN;
    @FXML
    private JFXButton Restaurant_BTN;
    @FXML
    private JFXButton Exprenses_BTN;
    @FXML
    private JFXButton RestaurantMenu_BTN;
    @FXML
    private JFXButton Storage_BTN;
    @FXML
    private JFXButton Statistics_BTN;
    @FXML
    private JFXButton Archive_BTN;
    @FXML
    private JFXButton Settings_BTN;
    @FXML
    private JFXButton DailySheet_BTN;
    @FXML
    private JFXButton Reservation_BTN;
    @FXML
    private JFXButton CutomersData_BTN;
    @FXML
    private Separator separator;
    @FXML
    private VBox container;
    @FXML
    private Separator playstationSeparate;
    @FXML
    private JFXHamburger hambuger;
    @FXML
    private ImageView logoImage;

    private JFXDrawer drawer;
    private JFXButton currentBtn;
    public static JFXButton Exprenses_BTN_Static;
    public static JFXButton Restaurant_BTN_Static;
    public static JFXButton DailySheet_BTN_Static;

    private boolean isPoliceHere;

    public static void showDailyDetails(String category) {
        if (category.equalsIgnoreCase("ps")) {
            LoadHelper.getInstance().dailySheetController.playingBTN.fire();
        } else {
            LoadHelper.getInstance().dailySheetController.restaurantBTN.fire();
        }

        if (ControlPanel.getInstance().ALLOW_CLIENT_ACCESS_END_DAY_PAGE) {
            DailySheet_BTN_Static.fire();
        } else {
            LoadHelper.getInstance().homePageController.toolbarController.transferToDailySheet();
        }
    }
    @FXML
    private VBox trialBox;
    @FXML
    private Label trialEndDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            isPoliceHere = false;
            currentBtn = ControlPanel.getInstance().HAS_PLAYSTATION ? GamingHall_BTN : Restaurant_BTN;
            focusButton(currentBtn);

            Exprenses_BTN_Static = Exprenses_BTN;
            Restaurant_BTN_Static = Restaurant_BTN;
            DailySheet_BTN_Static = DailySheet_BTN;

            drawer = HomePageController.drawer1_Static;
            hambuger.setOnMouseClicked((event) -> {
                if (drawer.isOpened()) {
                    drawer.toggle();
                }
            });
            logoImage.setOnMouseClicked((event) -> {
                if (HomePageController.aboutDialog.isVisible()) {
                    return;
                }
                if (HomePageController.currentDialog != null && HomePageController.currentDialog.isVisible()) {
                    HomePageController.currentDialog.close();
                }
                HomePageController.currentDialog = HomePageController.aboutDialog;
                HomePageController.aboutDialog.show();
            });

            currentBtn.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
                if (oldScene == null && newScene != null) {
                    newScene.setOnKeyReleased(keyEventEventHandler(newScene));
                    if (ControlPanel.getInstance().POLICE_WARNING) {
                        policeAlert(newScene);
                    }
                }
            });

            if (!ControlPanel.getInstance().PLAYSTATION_RESERVATION) {
                container.getChildren().remove(Reservation_BTN);
            }

            if (!ControlPanel.getInstance().HAS_RESTAURANT) {
                container.getChildren().remove(Restaurant_BTN);
                if (!ControlPanel.getInstance().HAS_PLAYSTATION) {
                    container.getChildren().remove(RestaurantMenu_BTN);
                }
            }

            if (!ControlPanel.getInstance().HAS_PLAYSTATION) {
                container.getChildren().remove(GamingHall_BTN);
                container.getChildren().remove(Reservation_BTN);
                container.getChildren().remove(playstationSeparate);
            }
            if (!ControlPanel.getInstance().HAS_STATISTICS) {
                container.getChildren().remove(Statistics_BTN);
            }
            if (!ControlPanel.getInstance().CUSTOMER_DATA) {
                container.getChildren().remove(CutomersData_BTN);
            }

            if (!ControlPanel.getInstance().IS_DEMO) {
                if (trialBox != null) {
                    container.getChildren().remove(trialBox);
                }
            } else {
                OnlineValidation.getInstance().addObserver((o, arg) -> {
                    Platform.runLater(() -> trialEndDate.setText(LocalDate.parse(
                            OnlineValidation.getInstance().getProgramCustomer().startDate,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"))
                            .plusDays(ControlPanel.getInstance().DEMO_NUM_OF_DAYS)
                            .format(DateTimeFormatter.ISO_DATE)));
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    int x = 0;

    private void changeTheme(Scene scene) {
//        if (x % 2 == 0) {
//            scene.getStylesheets().remove("/resources/style/base.css");
//            scene.getStylesheets().add("/resources/style/dark.css");
//            System.out.println(x + " : " + scene.getStylesheets());
//        } else {
//            scene.getStylesheets().remove("/resources/style/dark.css");
//            scene.getStylesheets().add("/resources/style/base.css");
//            System.out.println(x + " : " + scene.getStylesheets());
//        }
//        x++;
    }

    public void policeAlert(Scene scene) {
        HashMap<KeyCombination, Runnable> hashMap = new HashMap<>();
        // CTRL + E
        KeyCombination kc1 = new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN);
        Runnable rn1 = () -> {
            isPoliceHere = !isPoliceHere;
            if (isPoliceHere) {
                (ControlPanel.getInstance().HAS_PLAYSTATION ? GamingHall_BTN : Restaurant_BTN).fire();
                Settings_BTN.setVisible(false);
                Statistics_BTN.setVisible(false);
                Archive_BTN.setVisible(false);
                CutomersData_BTN.setVisible(false);
                DailySheet_BTN.setVisible(false);
                separator.setVisible(false);
            } else {
                Settings_BTN.setVisible(true);
                Statistics_BTN.setVisible(true);
                Archive_BTN.setVisible(true);
                CutomersData_BTN.setVisible(true);
                DailySheet_BTN.setVisible(true);
                separator.setVisible(true);
            }
        };
        hashMap.put(kc1, rn1);
        scene.getAccelerators().putAll(hashMap);
    }

    public static void checkDialog() {
        if (HomePageController.currentDialog != null && HomePageController.currentDialog.isVisible()) {
            HomePageController.currentDialog.close();
        }
    }

    private EventHandler<KeyEvent> keyEventEventHandler(Scene scene) {
        return (event) -> {
            try {

                switch (event.getCode()) {
                    case F1:
                        if (ControlPanel.getInstance().HAS_PLAYSTATION) {
                            goToGamingHall();
                            break;
                        }
                    case F2:
                        if (ControlPanel.getInstance().PLAYSTATION_RESERVATION) {
                            goToReservation();
                            break;
                        }
                    case F3:
                        if (ControlPanel.getInstance().HAS_RESTAURANT) {
                            goToRestaurant();
                        }
                        break;
                    case F4:
                        if (ControlPanel.getInstance().HAS_RESTAURANT || ControlPanel.getInstance().HAS_PLAYSTATION) {
                            goToRestaurantMenu();
                        }
                        break;
                    case F5:
                        goToExpense();
                        break;
                    case F6:
                        goToStorage();
                        break;
                    case F7:
                        if (!isPoliceHere) {
                            goToStatistics();
                        }
                        break;
                    case F8:
                        if (!isPoliceHere) {
                            goToArchive();
                        }
                        break;
                    case F9:
                        if (!isPoliceHere) {
                            goToSettings();
                        }
                        break;
                    case F10:
                        if (ControlPanel.getInstance().CUSTOMER_DATA) {
                            if (!isPoliceHere) {
                                goToCutomersData();
                            }
                            break;
                        }
                    case F11:
                        if (!isPoliceHere) {
                            goToDailySheet();
                        }
                        break;
                    case F12:
                        if (Preferences.getInstance().getBoolean(PreferencesType.OpenCashDrawerWithoutSale, "true")) {
                            CashDrawer.openCashDrawer();
                        }
                        break;
                    case INSERT:
                        drawer.toggle();
                        break;
                    case ESCAPE:
                        if (drawer.isOpened()) {
                            drawer.toggle();
                        }
                        break;
                }
                switch (event.getCode()) {
                    case F1:
                    case F2:
                    case F3:
                    case F4:
                    case F5:
                    case F6:
                    case F7:
                    case F8:
                    case F9:
                    case F10:
                    case F11:
                    case F12:
                    case INSERT:
                    case ESCAPE:
                        checkDialog();

                }

            } catch (Exception ex) {
                Logger.writeLog("Exception in : " + getClass().getName() + ".keyEventEventHandler():- " + ex);
            }
        };
    }

    @FXML
    private void goToGamingHall() {
        try {
            AdminValidationController.flag = false;
            if (!HomePageController.borderPane_Static.getCenter().equals(LoadHelper.getInstance().screenMap.get("defaultCenter"))) {
                HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("defaultCenter"));
                focusButton(GamingHall_BTN);
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + " ->  goToGamingHall:-" + ex);
        }
    }

    @FXML
    private void goToRestaurant() {
        try {
            AdminValidationController.flag = false;
            if (!HomePageController.borderPane_Static.getCenter().equals(LoadHelper.getInstance().screenMap.get("root_RestaurantHome"))) {
                updateNeededPages();
                HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_RestaurantHome"));
                focusButton(Restaurant_BTN);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in" + getClass().getName() + " -> goToRestaurant:- " + e);
        }
    }

    @FXML
    private void goToExpense() {
        try {
            AdminValidationController.flag = false;
            if (!HomePageController.borderPane_Static.getCenter().equals(LoadHelper.getInstance().screenMap.get("root_Expenses"))) {
                updateNeededPages();
                HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_Expenses"));
                focusButton(Exprenses_BTN);
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in  " + getClass().getName() + " -> goToExpense:- " + ex);
        }
    }

    @FXML
    private void goToRestaurantMenu() {
        try {
            AdminValidationController.flag = false;
            if (!HomePageController.borderPane_Static.getCenter().equals(LoadHelper.getInstance().screenMap.get("root_Product"))) {
                updateNeededPages();
                HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_Product"));
                focusButton(RestaurantMenu_BTN);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + " -> goToProducts:-" + ex);
        }
    }

    @FXML
    private void goToStorage() {
        try {
            AdminValidationController.flag = false;
            if (!HomePageController.borderPane_Static.getCenter().equals(LoadHelper.getInstance().screenMap.get("root_Storage"))) {
                updateNeededPages();
                HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_Storage"));
                focusButton(Storage_BTN);
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> goToStorage:-");
        }
    }

    @FXML
    private void goToStatistics() {
        try {
            if (!HomePageController.borderPane_Static.getCenter().equals(LoadHelper.getInstance().screenMap.get("root_Statistics"))) {
                if ((!User.CurrentUser.aceessConstrains.equalsIgnoreCase("موظف")) || AdminValidationController.flag) {
                    updateNeededPages();
                    HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_Statistics"));
                    focusButton(Statistics_BTN);
                    tempAdminpass = "";
                } else {
                    Parent root = FXMLLoader.load(getClass().getResource(Location.getInstance().get("ADMIN_VALIDATION")));
                    Scene scene = new Scene(root);
                    Stage s = util.gui.BuilderUI.initStageDecorated(scene, "Admin Validation", "info");
                    s.showAndWait();
                    if (AdminValidationController.flag) {
                        goToStatistics();
                    }
                }
            }

        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> goToStat:-" + ex);
        }
    }

    @FXML
    private void goToArchive() {
        try {
            if (!HomePageController.borderPane_Static.getCenter().equals(LoadHelper.getInstance().screenMap.get("root_ArchiveHome"))) {
                if ((!User.CurrentUser.aceessConstrains.equalsIgnoreCase("موظف")) || AdminValidationController.flag) {
                    updateNeededPages();
                    HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_ArchiveHome"));
                    focusButton(Archive_BTN);
                    tempAdminpass = "";
                } else {
                    Parent root = FXMLLoader.load(getClass().getResource(Location.getInstance().get("ADMIN_VALIDATION")));
                    Scene scene = new Scene(root);
                    Stage s = util.gui.BuilderUI.initStageDecorated(scene, "Admin Validation", "info");
                    s.showAndWait();
                    if (AdminValidationController.flag) {
                        goToArchive();
                    }
                }
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> goToArchive:-" + ex);
        }
    }

    @FXML
    private void goToSettings() {
        try {
            if (!HomePageController.borderPane_Static.getCenter().equals(LoadHelper.getInstance().screenMap.get("root_Settings"))) {
                if ((!User.CurrentUser.aceessConstrains.equalsIgnoreCase("موظف")) || AdminValidationController.flag) {
                    updateNeededPages();
                    HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_Settings"));
                    focusButton(Settings_BTN);
                    LoadHelper.getInstance().screenMap.put("root_EmployeeSettings", FXMLLoader.load(getClass().getResource(Location.getInstance().get("EMPLOYEESSETTINGSVIEW"))));
                    tempAdminpass = "";
                } else {
                    Parent root = FXMLLoader.load(getClass().getResource(Location.getInstance().get("ADMIN_VALIDATION")));
                    Scene scene = new Scene(root);
                    Stage s = util.gui.BuilderUI.initStageDecorated(scene, "Admin Validation", "info");
                    s.showAndWait();
                    if (AdminValidationController.flag) {
                        goToSettings();
                    }
                }
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> goToSettings:-" + ex);
        }
    }

    public void transferToDailySheet() {
        try {
            if (!HomePageController.borderPane_Static.getCenter().equals(LoadHelper.getInstance().screenMap.get("root_DailySheet"))) {
                if ((!User.CurrentUser.aceessConstrains.equalsIgnoreCase("موظف")) || AdminValidationController.flag) {
                    updateNeededPages();
                    HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_DailySheet"));
                    focusButton(DailySheet_BTN);
                    tempAdminpass = "";
                } else {
                    Parent root = FXMLLoader.load(getClass().getResource(Location.getInstance().get("ADMIN_VALIDATION")));
                    Scene scene = new Scene(root);
                    Stage s = util.gui.BuilderUI.initStageDecorated(scene, "Admin Validation", "info");
                    s.showAndWait();
                    if (AdminValidationController.flag) {
                        transferToDailySheet();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + " -> goToDailySheetAction:-" + ex);
        }
    }

    @FXML
    private void goToDailySheet() {
        try {

            if (!HomePageController.borderPane_Static.getCenter().equals(LoadHelper.getInstance().screenMap.get("root_DailySheet"))) {

                if (!ControlPanel.getInstance().ALLOW_CLIENT_ACCESS_END_DAY_PAGE && User.CurrentUser.aceessConstrains.equalsIgnoreCase("موظف") && !AdminValidationController.flag) {
                    // End Day Option
                    LoadHelper.getInstance().dailySheetController.EndDayAvtion(new ActionEvent());
                } else {
                    // goToDailySheet
                    updateNeededPages();
                    HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_DailySheet"));
                    focusButton(DailySheet_BTN);
                    tempAdminpass = "";
                }
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> goToDailySheet:-" + ex);
        }
    }

    public void updateNeededPages() {
        try {
            HashMap<String, String> locations = Location.getInstance();
            if (ExpensesController.addingExp == 1) {
                LoadHelper.getInstance().screenMap.put("root_ImprestSettings", FXMLLoader.load(getClass().getResource(locations.get("IMPREST"))));
            }
            if (RestaurantHomeController.anyAct == 1 || RoomController.innerEffectedAction == 1) {
                LoadHelper.getInstance().storageController.fillCurrentStoreTable("");
                LoadHelper.getInstance().storageController.fillEnterStoreTable("storeenter");
                LoadHelper.getInstance().storageController.fillExitStoreTable("storeexit");
                LoadHelper.getInstance().storageController.fillt4("");
                LoadHelper.getInstance().storageController.searchComBox.setText("");
                RoomController.innerEffectedAction = 0;
            }
            if (RoomController.leaveRoomNotation == 1 || RestaurantHomeController.anyAct == 1 || ExpensesController.addingExp == 1) {
                LoadHelper.getInstance().dailySheetController.updateAllData();
                RoomController.leaveRoomNotation = 0;
                RestaurantHomeController.anyAct = 0;
                ExpensesController.addingExp = 0;
            }
            if (ProductsController.addingNotation == 1) {
                LoadHelper.getInstance().screenMap.put("root_CafeSettings", FXMLLoader.load(getClass().getResource(locations.get("CAFESETTINGSVIEW"))));
                ProductsController.addingNotation = 0;
            }
            if (AddCategoryController.anyActNotation == 1) {

//              ... update Products ...
                Product.getAllProducts();

//                .... update Categories ....
                CategoryButton.getAllCategories();

                // update Menu Page
                LoadHelper.getInstance().screenMap.put("root_Product", FXMLLoader.load(getClass().getResource(locations.get("PRODUCTVIEW"))));

                LoadHelper.getInstance().tableOrderController.initCategoryButtons();
                LoadHelper.getInstance().takeAwayController.initCategoryButtons();

//                stage_NewOrderCafe.getScene().setRoot(LoginController.root_Cafe);
                AddCategoryController.anyActNotation = 0;
            }

            if (DailySheetController.endDayNot == 1) {
                LoadHelper.getInstance().screenMap.put("root_ArchiveHome", FXMLLoader.load(getClass().getResource(locations.get("ARCHEIVE"))));
                LoadHelper.getInstance().screenMap.put("root_Expenses", FXMLLoader.load(getClass().getResource(locations.get("EXPENSESVIEW"))));
                LoadHelper.getInstance().screenMap.put("root_Statistics", FXMLLoader.load(getClass().getResource(locations.get("STATISTICS"))));
                LoadHelper.getInstance().screenMap.put("root_ExpenseArch", FXMLLoader.load(getClass().getResource(locations.get("EXPENSES_ARCHEIVE"))));
                LoadHelper.getInstance().storageController.fillCurrentStoreTable("");
                LoadHelper.getInstance().storageController.fillEnterStoreTable("storeenter");
                LoadHelper.getInstance().storageController.fillExitStoreTable("storeexit");
                LoadHelper.getInstance().storageController.fillt4("");

                Customer.fillData();
                DailySheetController.endDayNot = 0;
            }
            if (DevicesSettingsController.actNot == 1) {
                ReservationsController.updateDeviceses();
                Reservation.fetchData();
                DevicesSettingsController.actNot = 0;
            }
            if (tablesNames.updateFlag == 1) {
                tablesNames.updateFlag = 0;
                HallController.initTables();
            }

        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> updateNeededPages:-" + e);
        }
        System.gc();
    }

    public void focusButton(JFXButton b) {
        // removeFocus
        currentBtn.getStyleClass().remove("NavBTN-FOCUSED");
        // putFocus
        b.getStyleClass().add("NavBTN-FOCUSED");
        try {
            HomePageController.title_Label.setText(b.getText());
        } catch (Exception e) {
            Logger.writeLog("Exception in ToolBarController -> focusButton:-" + e);
        }
        currentBtn = b;
    }

    @FXML
    private void goToCutomersData() {
        try {
            AdminValidationController.flag = false;
            if (!HomePageController.borderPane_Static.getCenter().equals(LoadHelper.getInstance().screenMap.get("root_CutomersData"))) {
                updateNeededPages();
                HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_CutomersData"));
                focusButton(CutomersData_BTN);
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> goToCutomersData() :-" + ex);
        }
    }

    @FXML
    private void goToReservation() {
        try {
            AdminValidationController.flag = false;
            if (!HomePageController.borderPane_Static.getCenter().equals(LoadHelper.getInstance().screenMap.get("root_reservation"))) {
                updateNeededPages();
                HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_reservation"));
                focusButton(Reservation_BTN);
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> goToReservation() :-" + ex);
        }
    }

}
