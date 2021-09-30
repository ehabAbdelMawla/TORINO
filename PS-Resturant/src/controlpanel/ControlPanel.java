package controlpanel;

import util.db.DatabaseHandler;
import util.db.Methods;
import static util.validation.MonthesValid.generateCodes;
import static util.validation.MonthesValid.resetCodes;

/**
 *
 * @author Ehab Abdel Mawla
 */
public class ControlPanel {

    private static ControlPanel controlPanel;

    public static ControlPanel getInstance() {
        if (controlPanel == null) {
            controlPanel = new ControlPanel();
        }
        return controlPanel;
    }

    private ControlPanel() {
    }

    // -----------------------------------------
    // ---------- PLAYSTATION OPTIONS ----------
    // -----------------------------------------
    // Is the Application Has Playstation or any related information?
    public int NUM_OF_ROOMS = 0;
    public final boolean HAS_PLAYSTATION = true,
            // Playstation Room Transfer Option
            ROOM_TRANSFER = true,
            // Playstation Reservations Page
            PLAYSTATION_RESERVATION = HAS_PLAYSTATION && false,
            // Access {Room Names Page} to ( add , edit , delete ) room
            ACCESS_ROOM_NAMES = HAS_PLAYSTATION && true,
            // Prevent add & delete rooms
            PREVENT_ADD_AND_DELETE_ROOMS = ACCESS_ROOM_NAMES && false,
            // Enable Promotions and discount Hours ? 
            HOUR_PROMOTIONS = HAS_PLAYSTATION && true,
            // Enable Promotions and discount Device Pirce ? 
            DEVICE_PROMOTIONS = HAS_PLAYSTATION && true,
            // Add Drinks in a room without starting playing time
            DRINKS_WITHOUT_TIMER = true,
            // Modifiy Minutes
            MODIFIY_MINUTES = true,
            // Modifiy Price
            MODIFIY_PRICE = false,
            // MATCH DEVICE VISIBILITY
            MATCH_DEVICE = true;
    /*  true: cafe income in playing rooms will be with restaruant income
        false: cafe orders in playing rooms will be with playing rooms income
     */
    public boolean SEPARATE_ROOMS_CAFE = true;

    /*
    Example of Modifiy Minutes:
    NUM_OF_MODIFIED = 5, CONSTRAINT_MODIFIED_MINUTES = 2;    ====>  (37/5=7 + 2  return 2 > 2 ? 40:35 )
     */
    public final int NUM_OF_MODIFIED = 5, CONSTRAINT_MODIFIED_MINUTES = 2;

    /*
    Example of Modifiy Price:
    PRICE_SEGMENT = 5, CONSTRAINT_MODIFIED_PRICE = 2.5;    ====>  (38/5=7 + 3  return 3 > 2.5 ? 40:35 )
     */
    public final int PRICE_SEGMENT = 5;
    public final double CONSTRAINT_MODIFIED_PRICE = 2.5;

    // -----------------------------------------
    // ----------- Resturant OPTIONS -----------
    // -----------------------------------------
    // Note: Program Must have one of HAS_RESTAURANT or HAS_PLAYSTATION = true
    // Is the Application Has Resturant or any related information?
    public final boolean HAS_RESTAURANT = true,
            // Access Resturant Hall Tables Page?
            ACCESS_HALL_TABLES = HAS_RESTAURANT && false,
            // Resturant Hall: Table Transfer Option
            RESTAURANT_TABLE_TRANSFER = ACCESS_HALL_TABLES && false,
            // Access Bar Popup Window
            RESTAURANT_BAR = HAS_RESTAURANT && false,
            // Access Delivery Popup Window
            RESTAURANT_Delivery = HAS_RESTAURANT && false,
            // Access {Hall Tables Names Page} to ( add , edit , delete ) Tables
            ACCESS_TABLES_NAMES = ACCESS_HALL_TABLES && false,
            /*
                if (NUMNBER_DIALOG_NORMAL = true) => {
                    will show anyway
                }else{
                    if(NUMNBER_DIALOG_LONGPRESS == true) => {
                        will show after Long Press
                    }else{
                        Will add Product instantly to the table
                    }
                }
             */
            NUMNBER_DIALOG_NORMAL = false,
            NUMNBER_DIALOG_LONGPRESS = !NUMNBER_DIALOG_NORMAL && true;

    // -----------------------------------------
    // ----------- GENERAL OPTIONS -----------
    // ----------------------------------------- 
    public final String PATH_OF_LAUNCHER_IMAGE = HAS_PLAYSTATION ? "/img/armyJoystick.png" : "/img/Restaurant-logo.png";


    // Send dailysheet report daily with e-mail
    public final boolean SEND_DAILYSHEET_MAIL = false,
            // Customer Data feature
            CUSTOMER_DATA = true,
            CUSTOMER_DATA_EXPORT = CUSTOMER_DATA && true,
            // Statistics Page
            HAS_STATISTICS = true,
            // Access to edit reciept data
            ACCESS_RECIEPT_DATA = false,
            // if true: expenses will be taken from : 'FROM daily income'
            // if false: expenses will be taken from : '3ohda' 
            EXPENSESS_FROM_DAILY_INCOME = true,
            /* 
                Police Alert to hide:
                    - Settings
                    - Statistics
                    - Archive
                    - CutomersData
                    - DailySheet
             */
            POLICE_WARNING = false,
            // shows/hides barcode feature
            BARCODE_OPTION = false,
            // show/hide export storage data button
            EXPORT_STORAGE_DATA = true;

    // -----------------------------------------
    // ----------- ONLINE VALIDATION -----------
    // -----------------------------------------
    public final boolean ONLINE_VALIDATION = true;

    // INSTANCE_ONLINE_NAME = "develop" in developing mode
    // INSTANCE_ONLINE_NAME = "client's shop name" in production mode
    public final String INSTANCE_ONLINE_NAME = "TORINO";

    // === Free trail Option ===
    /*
        if FREE_TRIAL_TIME = true;
        Then please make this changes:
            1-  In Database -> security Table
                make sure that this table is empty
                
            2-  In Database -> users Table
                make sure that all users are in inactive state
                make all users[active] column in users table equal to 0
                
            3-  Make Owner account with STRONG username and password
                and keep this account with you till payment is complete
    
            4-  Make a (Admin or Employee) account (NOT OWNER) and send the account to 
                the client.
     */
    public final boolean FREE_TRIAL_TIME = false;
    // Number of free trail minutes 
    public final int NUM_OF_TRAIL_MINUTES = 5;

    // -----------------------------------------
    // --------- (Installment - تقسيط) ---------
    // ------------ MONTH_VALIDATION -----------
    // -----------------------------------------
    /* 
    if MONTH_VALIDATION = true;
    Then please make this changes:
        1-  In Database -> daysValid Table
            make the record in this form: [startDate=Delivery Date,flag=1]
            startDate: is the Delivery Date where MONTH_VALIDATION count from.
            flag=1 : value(1) means that validation is still happening and 
            the (Installment - تقسيط) did not finish.
    
        2-  In Database -> codes Table
            Fill the table with encrypted keys.
            Remember that Num of keys is equal to Num of validations.
    
        3-  Use encrypt and decrypt Methods from util.validation.MonthesValid class.
            plainText: CodeClinic
            encrypted : CjBHb3Znc4JEM/eW8gVbhQ==
            decrypted: CodeClinic
            IMPORTANT !
            Give the clinet the PlainText and INSERT in the DB the encrypted code.
     */
    // Enable Month Validation in order to guarantee the (installment - تقسيط)
    public boolean MONTH_VALIDATION = false;
    // The time duration required to request the activation code
    public int MONTH_VALIDATION_DURATION = 10,
            MONTH_VALIDATION_NUM = 3;            // عدد الدفعات

    /* Format => YYYY-MM-dd */
    public String startDate = "2021-07-30";

    /**
     * @return Currency for all program
     */
    public String getCurrency() {
        return "ج";
    }

    // Champlion Features 
    // filter customers as a ('صباحي','مسائي')
    public final boolean DAY_NIGHT_CUSTOMER = false;

    /*
        When Demo:
            >  No UUID Valid
            >  Internet Connection Validation
            >  Local Time Change Validation
            >  Instance Add With Licence True on CodeClinicValidationApp
            >  Licence Will Change Automatically when Trying Period Finished
     */
    public final boolean IS_DEMO = false;
    public final int DEMO_NUM_OF_DAYS = 3;
    public final int MAX_NUM_OF_ROWS = 3;

    /* Prevent Clients From */
    public final boolean ALLOW_CLIENT_ACCESS_END_DAY_PAGE = false;

    public static void main(String[] args) {
        if (ControlPanel.getInstance().MONTH_VALIDATION) {
            generateCodes(ControlPanel.getInstance().MONTH_VALIDATION_NUM, ControlPanel.getInstance().startDate);
        } else {
            resetCodes();
        }
        try {
            DatabaseHandler.connectToDataBase();
            Methods.clearData("roomNames");
            for (int i = 1; i <= ControlPanel.getInstance().NUM_OF_ROOMS; i++) {
                DatabaseHandler.con.prepareStatement("INSERT INTO roomNames VALUES(" + i + ",'Room " + i + "')").execute();
            }
        } catch (Exception e) {

        }

    }

}
