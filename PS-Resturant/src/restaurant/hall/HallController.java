package restaurant.hall;

import static datamodel.tablesNames.tableNames;
import homepage.HomePageController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import util.gui.button.RestaurantTableButton;
import util.gui.load.LoadHelper;

public class HallController implements Initializable {

    private final static ObservableList<RestaurantTableButton> TABLE_BUTTONS = FXCollections.observableArrayList();

    @FXML
    private FlowPane tablesFlowPane;
    public static FlowPane tablesFlowPanePointer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tablesFlowPanePointer = tablesFlowPane;
        initTables();

    }

    public static void initTables() {
        TABLE_BUTTONS.clear();
        for (int i = 0; i < tableNames.size(); i++) {
            RestaurantTableButton tableButton = new RestaurantTableButton(tableNames.get(i).getTableName(), tableNames.get(i).isFree());
            TABLE_BUTTONS.add(tableButton);

        }
        tablesFlowPanePointer.getChildren().setAll(TABLE_BUTTONS);

    }

    public static void changeTableState(String tableName, int state) {
        // 0 : free , 1 : busy
        TABLE_BUTTONS.forEach((tableButton) -> {
            if (tableButton.getTableName().equals(tableName)) {
                if (state == 1) {
                    tableButton.setBusy();

                } else {
                    tableButton.setFree();

                }
            }
        });
    }

    @FXML
    private void goToResturantHomePasge(ActionEvent event) {
        HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_RestaurantHome"));
        HomePageController.title_Label.setText("المطعم");
    }
}
