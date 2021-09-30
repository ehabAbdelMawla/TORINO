/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package datamodel.settings;

/**
 *
 * @author Bayoumi
 */
public enum PreferencesType {

    Print("print"),
    Print_PlayingRooms("print_PlayingRooms"),
    Print_EndDay("print_EndDay"),
    Print_RestaurantHall("print_RestaurantHall"),
    Print_TakeAway("print_TakeAway"),
    Print_Bar("print_Bar"),
    Print_Delivery("print_Delivery"),
    OpenCashDrawerWithSale("open_cash_drawer_with_sale"),
    OpenCashDrawerWithoutSale("open_cash_drawer_without_sale"),
    BackupOnEndShift("backup_on_endshift"),
    BackupLocation("backup_location");

    private final String name;

    private PreferencesType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
