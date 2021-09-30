/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.gui.load;

import archive.daydetails.ArchiveDayDetailsController;
import controlpanel.ControlPanel;
import archive.daydetails.record.PlayingRecordDetailsController;
import customerdata.ViewCustomerController;
import dailysheet.DailySheetController;
import datamodel.tablesNames;
import dialog.checkout.CheckOutBillController;
import homepage.HomePageController;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import restaurant.choosecustomer.ChooseCustomerController;
import restaurant.dayorders.details.BillDetailsController;
import restaurant.hall.TableOrderController;
import restaurant.takeaway.TakeAwayController;
import settings.products.ProductSettingsController;
import settings.products.category.CategorySettingsController;
import util.Logger;
import util.gui.BuilderUI;
import storage.StorageController;

/**
 *
 * @author Bayoumi
 */
public class LoadHelper {

//    ================= SINGLETON ================
    private static LoadHelper load;
    public static int firstTimeLoginNotation;

    private LoadHelper() {
    }

    public static LoadHelper getInstance() {
        if (load == null) {
            load = new LoadHelper();
        }
        return load;
    }
    public HashMap<String, Parent> screenMap = new HashMap<>();
    // ===========================================
    // =========== stage ===========
    public Stage Stage_TransferTable;
    public Stage Stage_ViewCustomer;
    public Stage Stage_ChooseCustomer;
    public Stage Stage_TableOrder;
    public Stage Stage_CheckOutBill;
    public Stage stage_transfer;
    public Stage Stage_billDetails;
    public Stage stage_confirmAlert;
    public Stage stage_okAlert;
    public Stage Stage_ArchDayDetails;
    public Stage Stage_TakeAway;
    public Stage stage_reasonConfirmAlert;
    public Stage RoomRecordDeails;
    // ====== controllers ==========
    public BillDetailsController billDetailsController;
    public ViewCustomerController viewCustomerController;
    public TakeAwayController takeAwayController;
    public DailySheetController dailySheetController;
    public CheckOutBillController checkOutBillController;
    public TableOrderController tableOrderController;
    public ArchiveDayDetailsController ArchDetailsController;
    public ChooseCustomerController chooseCustomerController;
    public PlayingRecordDetailsController recordDetailsController;
    public HomePageController homePageController;
    public StorageController storageController;
    public ProductSettingsController productSettingsController;

    public void loadScreens(boolean DD) {
        System.out.println("Loading Start");
        try {
            FXMLLoader fxmlLoader;
            HashMap<String, String> locations = Location.getInstance();

            if (DD) {
                //homePageController
                fxmlLoader = new FXMLLoader(getClass().getResource(locations.get("HOMEPAGEVIEW")));
                screenMap.put("root_Homepage", fxmlLoader.load());
                homePageController = fxmlLoader.getController();
            }

            screenMap.put("root_Settings", FXMLLoader.load(getClass().getResource(locations.get("SETTINGSVIEW"))));

            screenMap.put("root_ArchiveHome", FXMLLoader.load(getClass().getResource(locations.get("ARCHEIVE"))));
            //storageController
            fxmlLoader = new FXMLLoader(getClass().getResource(locations.get("STORAGEVIEW")));
            screenMap.put("root_Storage", fxmlLoader.load());
            storageController = fxmlLoader.getController();

            screenMap.put("root_Expenses", FXMLLoader.load(getClass().getResource(locations.get("EXPENSESVIEW"))));
            screenMap.put("root_Product", FXMLLoader.load(getClass().getResource(locations.get("PRODUCTVIEW"))));

            fxmlLoader = new FXMLLoader(getClass().getResource(locations.get("ARCHEIVE_DAY_DETAILS")));
            Scene Scene_ArchDayDetails = new Scene(fxmlLoader.load());
            Scene_ArchDayDetails.getStylesheets().add("/resources/style/base.css");
            ArchDetailsController = fxmlLoader.getController();

//            screenMap.put("root_EmployeeSettings", FXMLLoader.load(getClass().getResource(Location.EMPLOYEESSETTINGSVIEW)));
            screenMap.put("root_DevicesSettings", FXMLLoader.load(getClass().getResource(locations.get("DEVICESSETTINGSVIEW"))));
            if (DD) {
                screenMap.put("root_DeleteData", FXMLLoader.load(getClass().getResource(locations.get("DELETEDATASETTINGSVIEW"))));
            }
            if (!ControlPanel.getInstance().EXPENSESS_FROM_DAILY_INCOME) {
                screenMap.put("root_ImprestSettings", FXMLLoader.load(getClass().getResource(locations.get("IMPREST"))));
            }
//            screenMap.put("root_ProductSettings", FXMLLoader.load(getClass().getResource(locations.get("PRODUCTSSETTINGS"))));
            screenMap.put("root_ExpenseArch", FXMLLoader.load(getClass().getResource(locations.get("EXPENSES_ARCHEIVE"))));
            if (ControlPanel.getInstance().HAS_STATISTICS) {
                screenMap.put("root_Statistics", FXMLLoader.load(getClass().getResource(locations.get("STATISTICS"))));
            }

            fxmlLoader = new FXMLLoader(getClass().getResource(locations.get("CAFESETTINGSVIEW")));
            screenMap.put("root_CafeSettings", fxmlLoader.load());
            productSettingsController = fxmlLoader.getController();

            fxmlLoader = new FXMLLoader(getClass().getResource("/settings/products/category/CategorySettings.fxml"));
            Parent root = fxmlLoader.load();
            CategorySettingsController categorySettingsController = fxmlLoader.getController();
            productSettingsController.setData(categorySettingsController, root);

            //===========
            if (ControlPanel.getInstance().ACCESS_ROOM_NAMES) {
                screenMap.put("root_RoomSettings", FXMLLoader.load(getClass().getResource(locations.get("ROOMS_SETTINGS"))));
            }

            if (ControlPanel.getInstance().SEND_DAILYSHEET_MAIL || ControlPanel.getInstance().ACCESS_RECIEPT_DATA) {
                screenMap.put("root_ShopData", FXMLLoader.load(getClass().getResource(locations.get("SHOP_DATA"))));
            }
            if (ControlPanel.getInstance().PLAYSTATION_RESERVATION) {
                screenMap.put("root_reservation", FXMLLoader.load(getClass().getResource(locations.get("RESERVATIONS"))));
            }

            fxmlLoader = new FXMLLoader(getClass().getResource(locations.get("TAKEAWAY")));
            Scene Scene_TakeAway = new Scene(fxmlLoader.load());
            takeAwayController = fxmlLoader.getController();

            fxmlLoader = new FXMLLoader(getClass().getResource(locations.get("TABLE_ORDER")));
            Scene Scene_TableOrder = new Scene(fxmlLoader.load());
            tableOrderController = fxmlLoader.getController();

            Scene Scene_TransferTable = new Scene(FXMLLoader.load(getClass().getResource(locations.get("TRANSFER_TABLE"))));

            fxmlLoader = new FXMLLoader(getClass().getResource(locations.get("CHOOSE_CUSTOMER")));
            Scene Scene_ChooseCustomer = new Scene(fxmlLoader.load());
            chooseCustomerController = fxmlLoader.getController();

            fxmlLoader = new FXMLLoader(getClass().getResource(locations.get("VIEW_CUSTOMER")));
            Scene Scene_ViewCustomer = new Scene(fxmlLoader.load());
            viewCustomerController = fxmlLoader.getController();
            // checkOutBill
            fxmlLoader = new FXMLLoader(getClass().getResource(locations.get("CHECKOUT_BILL")));
            Scene Scene_CheckOutBill = new Scene(fxmlLoader.load());
            checkOutBillController = fxmlLoader.getController();
            // billDetails
            fxmlLoader = new FXMLLoader(getClass().getResource(locations.get("BILL_DETAILS")));
            Scene Scene_billDetails = new Scene(fxmlLoader.load());
            billDetailsController = fxmlLoader.getController();

            Scene scene_okAlert = new Scene(FXMLLoader.load(getClass().getResource(locations.get("OK_ALERT"))));

            Scene scene_reasonConfirmAlert = new Scene(FXMLLoader.load(getClass().getResource(locations.get("REASON_CONFIRM_ALERT"))));
            Scene scene_transfer = new Scene(FXMLLoader.load(getClass().getResource(locations.get("TRANSFER"))));
            Scene scene_confirmAlert = new Scene(FXMLLoader.load(getClass().getResource(Location.getInstance().get("CONFIRM_ALERT"))));
            //recordDetailsController
            fxmlLoader = new FXMLLoader(getClass().getResource(locations.get("PLAYING_RECORD_DETAILS")));
            Scene RoomRecordDeailsScene = new Scene(fxmlLoader.load());
            recordDetailsController = fxmlLoader.getController();

            Platform.runLater(() -> {
                System.out.println("Stages init start");
                Stage_ArchDayDetails = BuilderUI.initStageDecorated(Scene_ArchDayDetails, "Day Details", "info");
                Stage_TakeAway = BuilderUI.initStageDecorated(Scene_TakeAway, "New Order", "info");
                Stage_TableOrder = BuilderUI.initStageDecorated(Scene_TableOrder, "New Order", "info");
                Stage_TableOrder.setOnCloseRequest((WindowEvent event) -> {
                    tableOrderController.closeWindowAction();
                });
                Stage_TransferTable = BuilderUI.initStageDecorated(Scene_TransferTable, "Transfer", "info");
                Stage_ChooseCustomer = BuilderUI.initStageDecorated(Scene_ChooseCustomer, "Choose Customer", "info");
                Stage_ViewCustomer = BuilderUI.initStageDecorated(Scene_ViewCustomer, "Customer Data", "info");
                Stage_CheckOutBill = BuilderUI.initStageTransparent(Scene_CheckOutBill, "CheckOut", "info");
                Stage_billDetails = BuilderUI.initStageDecorated(Scene_billDetails, "Data", "info");
                BuilderUI.HandlerCTRL_W(Scene_billDetails, Stage_billDetails, () -> {
                    Stage_billDetails.close();
                });
                stage_okAlert = BuilderUI.initStageUnDecorated(scene_okAlert, "", "info");
                stage_confirmAlert = BuilderUI.initStageUnDecorated(scene_confirmAlert, "", "info");
                stage_reasonConfirmAlert = BuilderUI.initStageUnDecorated(scene_reasonConfirmAlert, "", "info");
                stage_transfer = BuilderUI.initStageDecorated(scene_transfer, "Transfer", "info");

                RoomRecordDeails = util.gui.BuilderUI.initStageDecorated(RoomRecordDeailsScene, null, "info");
                System.out.println("Stages init finish");
            });

            System.out.println("Loading Finish");
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.writeLog("Exception in  " + getClass().getName() + ".loadScreens(): " + ex);
        }
    }

    public boolean loadNeccessaryRoots() {
        try {

            tablesNames.fillData();

            if (ControlPanel.getInstance().HAS_PLAYSTATION) {
                screenMap.put("root_PlayingPrice", FXMLLoader.load(getClass().getResource(Location.getInstance().get("PLAYINGPRICEVIEW"))));
                screenMap.put("defaultCenter", FXMLLoader.load(getClass().getResource(Location.getInstance().get("HOMEPAGE"))));
            }

            if (ControlPanel.getInstance().ACCESS_TABLES_NAMES) {
                screenMap.put("root_RestaurantHallSettings", FXMLLoader.load(getClass().getResource(Location.getInstance().get("RESTURNT_HALL_SETTINGS"))));
            }
            if (ControlPanel.getInstance().ACCESS_HALL_TABLES) {
                screenMap.put("root_Hall", FXMLLoader.load(getClass().getResource(Location.getInstance().get("RESTURNT_HALL"))));
            }
            if (ControlPanel.getInstance().CUSTOMER_DATA) {
                screenMap.put("root_CutomersData", FXMLLoader.load(getClass().getResource(Location.getInstance().get("CUSTOMER_DATA"))));
            }
            screenMap.put("root_DayOrders", FXMLLoader.load(getClass().getResource(Location.getInstance().get("RESTURANT_DAY_ORDERS"))));
            screenMap.put("root_RestaurantHome", FXMLLoader.load(getClass().getResource(Location.getInstance().get("RESTURANT_HOME"))));
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Location.getInstance().get("DAILYSHEETVIEW")));
            screenMap.put("root_DailySheet", fxmlLoader.load());
            dailySheetController = fxmlLoader.getController();

            return true;
        } catch (Exception e) {
            Logger.writeLog("Exception in  " + getClass().getName() + ".loadNeccessaryRoots(): " + e);
        }
        return false;
    }
}
