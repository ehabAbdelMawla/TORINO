package util.gui.load;

import java.util.HashMap;

public class Location {

    public static Location location;
    public HashMap<String, String> pathesMap = new HashMap<>();

    public static HashMap<String, String> getInstance() {
        if (location == null) {
            location = new Location();
        }
        return location.pathesMap;
    }

    private Location() {
        pathesMap.put("LOGINVIEW", "/login/Login.fxml");
        pathesMap.put("LAUNCHERVIEW", "/launcher/LauncherScene.fxml");
        pathesMap.put("TOOL_BAR", "/toolbar/Toolbar.fxml");
        pathesMap.put("ABOUT", "/dialog/about/About.fxml");
        pathesMap.put("HOMEPAGEVIEW", "/homepage/HomePage.fxml");
        pathesMap.put("PRODUCTVIEW", "/products/Products.fxml");
        pathesMap.put("PLAYINGPRICEVIEW", "/playing/price/PlayingPrice.fxml");
        pathesMap.put("EXPENSESVIEW", "/expenses/Expenses.fxml");
        pathesMap.put("SPACIFICTIMEVIEW", "/dialog/SpacificTime/SpacificTime.fxml");
        pathesMap.put("DAILYSHEETVIEW", "/dailysheet/DailySheet.fxml");
        pathesMap.put("STORAGEVIEW", "/storage/Storage.fxml");
        pathesMap.put("SETTINGSVIEW", "/settings/Settings.fxml");

//        pathesMap.put("CAFESETTINGSVIEW", "/settings/CafeSettings.fxml");
        pathesMap.put("CAFESETTINGSVIEW", "/settings/products/ProductSettings.fxml");

        pathesMap.put("DEVICESSETTINGSVIEW", "/settings/devices/DevicesSettings.fxml");
        pathesMap.put("EMPLOYEESSETTINGSVIEW", "/settings/employees/EmployeesSettings.fxml");
        pathesMap.put("DELETEDATASETTINGSVIEW", "/settings/deletedata/DeleteData.fxml");
        pathesMap.put("CAFE", "/cafe/Cafe.fxml");
        pathesMap.put("IMPREST", "/settings/financial/custody/FinancialCustody.fxml");
        pathesMap.put("PRODUCTSSETTINGS", "/settings/ProductSettings.fxml");
        pathesMap.put("SHOP_DATA", "/settings/shop/data/ShopData.fxml");
        pathesMap.put("ARCHEIVE", "/archive/ArchiveHome.fxml");
        pathesMap.put("ARCHEIVE_DAY_DETAILS", "/archive/daydetails/ArchiveDayDetails.fxml");
        pathesMap.put("EXPENSES_ARCHEIVE", "/archive/expense/ExpenseArch.fxml");
        pathesMap.put("STATISTICS", "/statistics/Statistics.fxml");
        pathesMap.put("STATISTICS_CATEGORY_DETAILS", "/statistics/details/CategoryDetails.fxml");
        pathesMap.put("ROOMS_SETTINGS", "/settings/rooms/RoomsSettings.fxml");
        pathesMap.put("RESERVATIONS", "/reservation/Reservations.fxml");
        pathesMap.put("TAKEAWAY", "/restaurant/takeaway/TakeAway.fxml");
        pathesMap.put("TABLE_ORDER", "/restaurant/hall/TableOrder.fxml");
        pathesMap.put("TRANSFER_TABLE", "/restaurant/transfer/TransferTable.fxml");
        pathesMap.put("CHOOSE_CUSTOMER", "/restaurant/choosecustomer/ChooseCustomer.fxml");
        pathesMap.put("VIEW_CUSTOMER", "/customerdata/ViewCustomer.fxml");
        pathesMap.put("CHECKOUT_BILL", "/dialog/checkout/CheckOutBill.fxml");
        pathesMap.put("BILL_DETAILS", "/restaurant/dayorders/details/BillDetails.fxml");
        pathesMap.put("OK_ALERT", "/dialog/alert/ok/OkAlert.fxml");
        pathesMap.put("CONFIRM_ALERT", "/dialog/alert/confirm/ConfirmAlert.fxml");
        pathesMap.put("REASON_CONFIRM_ALERT", "/dialog/alert/confirm/reason/ConfirmReasonAlert.fxml");
        pathesMap.put("TRANSFER", "/playing/rooms/transfer/Transfer.fxml");
        pathesMap.put("TRANSFER_CHOOSE_ROOM", "/reservation/ChooseRoom.fxml");
        pathesMap.put("PLAYING_RECORD_DETAILS", "/archive/daydetails/record/PlayingRecordDetails.fxml");
        pathesMap.put("HOMEPAGE", "/playing/hall/PlayingRoomsHall.fxml");
        pathesMap.put("RESTURNT_HALL_SETTINGS", "/settings/restaurant/hall/RestaurantHallSettings.fxml");
        pathesMap.put("RESTURNT_HALL", "/restaurant/hall/Hall.fxml");
        pathesMap.put("CUSTOMER_DATA", "/customerdata/CustomersData.fxml");
        pathesMap.put("RESTURANT_DAY_ORDERS", "/restaurant/dayorders/DayOrders.fxml");
        pathesMap.put("RESTURANT_HOME", "/restaurant/RestaurantHome.fxml");
        pathesMap.put("ADMIN_VALIDATION", "/dialog/admin_validation/AdminValidation.fxml");
        pathesMap.put("ROOM", "/playing/rooms/Room.fxml");
        pathesMap.put("EDIT_ROOMS_DRINK", "/dialog/edit/playing/room/cafe/EditRoomsDrinks.fxml");
        pathesMap.put("EDIT_ROOMS_PLAYING", "/dialog/edit/playing/room/playing/EditPlayingRecord.fxml");
        pathesMap.put("CHOOSE_PROMO_TYPE", "/settings/promotion/ChoosePromoType.fxml");
        pathesMap.put("SHOW_PROMOTED_DEVICES", "/settings/promotion/device/show/ShowDevices.fxml");
        pathesMap.put("DEVICE_PROMOTIONS", "/settings/promotion/device/DevicePromo.fxml");
        pathesMap.put("HOUR_PROMOTIONS", "/settings/promotion/HourPromotion.fxml");
        pathesMap.put("TAX_SERVICE", "/settings/tax/TaxSettings.fxml");
        pathesMap.put("EDIT_EMPLOYEE", "/dialog/edit/employee/EditEmployee.fxml");
        pathesMap.put("EDIT_PLAYING_PRICE", "/dialog/edit/playing/prices/EditPlayingPrices.fxml");
        pathesMap.put("EDIT_EXPENSES_RECORD", "/dialog/edit/expenses/EditExpenses.fxml");
        pathesMap.put("EDIT_INGERDIENTS", "/dialog/edit/ingredient/EditIngredient.fxml");
        pathesMap.put("DEPT_DETAILS", "/archive/debt/DebtDetails.fxml");
        pathesMap.put("New_PRODUCT", "/settings/products/NewProduct.fxml");

        pathesMap.put("NUMBER_DIALOG", "/dialog/number/NumberDialog.fxml");
        pathesMap.put("OTHER_SETTINGS", "/settings/other/OtherSettings.fxml");
    }

}
