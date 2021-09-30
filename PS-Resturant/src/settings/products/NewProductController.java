package settings.products;

import util.gui.ComboBoxAutoComplete;
import datamodel.ArgumentsClass;
import datamodel.DrinkPriceTableRecord;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import controlpanel.ControlPanel;
import datamodel.StoreRecord;
import datamodel.User;
import datamodel.storemoves;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import restaurant.RestaurantHomeController;
import storage.StorageController;
import util.Logger;
import util.db.DatabaseHandler;
import util.db.Methods;
import util.gui.HelperMethods;
import util.gui.load.DialogHelper;
import util.gui.load.LoadHelper;

public class NewProductController implements Initializable {

    private ComboBoxAutoComplete customListOfArgs;
    // ===== Static Objects =====
    public static DrinkPriceTableRecord productToEdit = null;
    public static JFXTreeTableView<ArgumentsClass> argumentsTable_staic;
    public static TextField productName_static;
    public static TextField guestPrice_staic;
    public static TextField staffPrice_staic;
    public static JFXButton confirmButton_static;
    public static JFXButton saveAndAddAnotherButton_static;
    public static JFXButton AddIngredientsButton_static;
    public static JFXComboBox<String> litsOfArgsComboBox_static;
    public static TextField numOfArguments_static;
    public static ObservableList<ArgumentsClass> argumentsObservableList = FXCollections.observableArrayList();

    // ===== GUI Objects =====
    @FXML
    private TextField productName, guestPrice, staffPrice;
    @FXML
    private JFXComboBox<String> litsOfArgsComboBox;
    @FXML
    private TextField numOfArguments;
    @FXML
    private JFXTreeTableView<ArgumentsClass> argumentsTable;
    @FXML
    private TreeTableColumn<ArgumentsClass, String> argumentNameCol;
    @FXML
    private TreeTableColumn<ArgumentsClass, Integer> numCol;
    @FXML
    private TreeTableColumn<ArgumentsClass, JFXButton> delCol;
    @FXML
    private TreeTableColumn<ArgumentsClass, JFXButton> editCol;
    @FXML
    private JFXButton confirmButton, AddIngredientsButton, saveAndAddAnotherButton;
    @FXML
    private TextField initialCountOfSolidProduct;
    @FXML
    private HBox addInitCountContainer;
    private static HBox addInitCountContainerPointer;
    @FXML
    private TextField barCodeTextField;
    private static TextField barCodeTextFieldPointer;
    @FXML
    private VBox barcodeBox;
    @FXML
    private HBox priceAndAmountContainer;
    private static HBox priceAndAmountContainer_Pointer;
    @FXML
    private ScrollPane scrollPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            customListOfArgs = new ComboBoxAutoComplete(litsOfArgsComboBox);
            configureTable();
            productName_static = productName;
            guestPrice_staic = guestPrice;
            staffPrice_staic = staffPrice;
            argumentsTable_staic = argumentsTable;
            confirmButton_static = confirmButton;
            saveAndAddAnotherButton_static = saveAndAddAnotherButton;
            AddIngredientsButton_static = AddIngredientsButton;
            litsOfArgsComboBox_static = litsOfArgsComboBox;
            numOfArguments_static = numOfArguments;
            addInitCountContainerPointer = addInitCountContainer;
            barCodeTextFieldPointer = barCodeTextField;
            priceAndAmountContainer_Pointer = priceAndAmountContainer;

            if (!ControlPanel.getInstance().BARCODE_OPTION) {
                ((HBox) barcodeBox.getParent()).getChildren().remove(barcodeBox);
                barcodeBox = null;
            }
            // (Stage & Scene) Listener
            if (scrollPane.getScene() != null) {
                System.out.println("DELETE THIS PRINT 11111111");
                KeyCombinationHandler(scrollPane.getScene(), (Stage) scrollPane.getScene().getWindow());
            } else {
                util.gui.HelperMethods.setStageListener(scrollPane, (stage) -> {
                    System.out.println("DELETE THIS PRINT 222222222");
                    KeyCombinationHandler(stage.getScene(), stage);
                    return null;
                });
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + ".initialize() :- " + e);
        }
    }

    public void KeyCombinationHandler(Scene scene, Stage stage) {
        System.out.println("KeyCombinationHandler");
        HashMap<KeyCombination, Runnable> hashMap = new HashMap<>();
        hashMap.put(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN), () -> {
            System.out.println("3333333");
            saveAndAddAnotherButton.fire();
        });
        scene.getAccelerators().putAll(hashMap);
    }

    private void configureTable() {
        argumentNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("argName"));
        numCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("numOfArg"));
        editCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("editBtn"));
        delCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("delBtn"));
        HelperMethods.TableColumnAlignment(argumentNameCol, numCol, editCol, delCol);
    }

    private void addOrEditProductHandler(boolean closeAfterAdd) {
        try {
            // Validate Inputs
            if (productName.getText().trim().equals("") || guestPrice.getText().trim().equals("") || staffPrice.getText().trim().equals("")) {
                DialogHelper.getInstance().showOKAlert("يجب ادخال جميع البيانات");
                return;
            }
            if (productName.getText().trim().contains("@") || productName.getText().trim().contains("#")) {
                DialogHelper.getInstance().showOKAlert("لا يمكن ان بحتوي المنتج  علي @او #");
                return;
            }
            float guestPriceValue, staffPriceValue;
            try {
                guestPriceValue = Float.parseFloat(guestPrice.getText().trim());
                staffPriceValue = Float.parseFloat(staffPrice.getText().trim());
                if (guestPriceValue <= 0 || staffPriceValue <= 0) {
                    throw new Exception();
                }
            } catch (Exception e) {
                DialogHelper.getInstance().showOKAlert("البيانات التي ادخلتها غير صحيحة");
                return;
            }
            int initCount = 0;
            if (confirmButton.getText().equals("حفظ")) {
                // add product
                try {
                    if (initialCountOfSolidProduct.getText().trim().equals("")) {
                        initCount = 0;
                    } else {
                        initCount = Integer.parseInt(initialCountOfSolidProduct.getText().trim());
                    }
                    if (initCount < 0) {
                        throw new NullPointerException();
                    }
                    int x = insertIntoProductsTable(new DrinkPriceTableRecord(
                            (int) Methods.GetMaximum("id", "drinkprice"),
                            productName.getText().trim(),
                            guestPriceValue, staffPriceValue,
                            util.gui.load.LoadHelper.getInstance().productSettingsController.categorySettingsController.FocusedButton.getText(),
                            argumentsObservableList, barCodeTextField.getText().trim()));
                    if (x == 0) {
                        return;
                    }
                } catch (NumberFormatException e) {
                    DialogHelper.getInstance().showOKAlert("العدد غير صحيح");
                    return;
                }
            } else {
                // edit product
                try {
                    String sql = "UPDATE drinkprice SET drinkName=? ,Price=?,staffPrice=?,args=?,barCode=? WHERE drinkName=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                    DatabaseHandler.stat.setString(1, productName.getText().trim());
                    DatabaseHandler.stat.setFloat(2, guestPriceValue);
                    DatabaseHandler.stat.setFloat(3, staffPriceValue);
                    DatabaseHandler.stat.setString(4, convertArgsListToString(argumentsObservableList));
                    DatabaseHandler.stat.setString(5, barCodeTextField.getText() != null ? barCodeTextField.getText().trim() : null);
                    DatabaseHandler.stat.setString(6, productToEdit.DrinkName);
                    DatabaseHandler.stat.execute();
                } catch (org.sqlite.SQLiteException e) {
                    if (e.toString().contains("drinkprice.drinkName")) {
                        DialogHelper.getInstance().showOKAlert("هذا الاسم موجود بالفعل!");
                    } else {
                        DialogHelper.getInstance().showOKAlert("هذا الباركود موجود بالفعل!");
                    }
                    return;
                }
            }
            // if product with no ingredients
            if (argumentsObservableList.isEmpty()) {
                try {
                    String sqlString = "INSERT INTO store VALUES (?,?,?)";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlString);
                    DatabaseHandler.stat.setString(1, productName.getText().trim());
                    DatabaseHandler.stat.setFloat(2, confirmButton.getText().equals("حفظ") ? initCount : 0);
                    DatabaseHandler.stat.setInt(3, (int) Methods.GetMaximum("id", "store"));
                    DatabaseHandler.stat.execute();

                    if (confirmButton.getText().equals("حفظ")) {
                        StorageController.insertIntoEnterStoreTable(new storemoves(
                                LocalDate.now().toString(),
                                LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")),
                                User.CurrentUser.userName,
                                productName.getText().trim(),
                                (float) initCount
                        ), "storeenter");
                    }

                } catch (SQLException e) {
                    // Debug
                    if (confirmButton.getText().equals("حفظ") && initCount > 0) {
                        LoadHelper.getInstance().storageController.updateCurrentStoreTable(new StoreRecord(
                                productName.getText().trim(),
                                initCount));
                    } else {
                        Logger.writeLog("Exception in " + getClass().getName() + " -> addOrEditProductHandler -> inserting to store :- " + e);
                    }
                }
            }

            productToEdit = null;
            resetFields();

            AddCategoryController.anyActNotation = 1;
            RestaurantHomeController.anyAct = 1;
            util.gui.load.LoadHelper.getInstance().productSettingsController.setProductsOfSelectedCategory(
                    util.gui.load.LoadHelper.getInstance().productSettingsController.categorySettingsController.FocusedButton.getText());
            if (closeAfterAdd) {
                closeWindow();
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> addOrEditProductHandler :- " + e);
        }
    }

    private void resetFields() {
        HelperMethods.resetEmptyField("", productName, barCodeTextField, guestPrice, staffPrice, initialCountOfSolidProduct, numOfArguments);
        litsOfArgsComboBox.setValue(null);
        argumentsObservableList.clear();
    }

    @FXML
    private void saveAndAddAnother() {
        addOrEditProductHandler(false);
        addProductNumberTextField();
        productName.requestFocus();
    }

    @FXML
    private void addProduct() {
        addOrEditProductHandler(true);
    }

    public int insertIntoProductsTable(DrinkPriceTableRecord rec) {
        try {
            String sql = "INSERT INTO drinkprice VALUES(?,?,?,?,?,?,?)";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setString(1, rec.DrinkName);
            DatabaseHandler.stat.setFloat(2, rec.Price);
            DatabaseHandler.stat.setInt(3, rec.id);
            DatabaseHandler.stat.setFloat(4, rec.Price2);
            DatabaseHandler.stat.setString(5, rec.categ);
            DatabaseHandler.stat.setString(6, convertArgsListToString(rec.argums));
            if (!rec.barCode.trim().equals("")) {
                DatabaseHandler.stat.setString(7, rec.barCode);
            } else {
                DatabaseHandler.stat.setNull(7, java.sql.Types.VARCHAR);
            }

            DatabaseHandler.stat.execute();
            return 1;
        } catch (org.sqlite.SQLiteException e) {
            if (e.toString().contains("drinkprice.drinkName")) {
                DialogHelper.getInstance().showOKAlert("هذا الاسم موجود بالفعل!");
            } else {
                DialogHelper.getInstance().showOKAlert("هذا الباركود موجود بالفعل!");
            }
        } catch (Exception e) {
            Logger.writeLog("EXception in " + getClass().getName() + " ->  insertIntoProductsTable :- " + e);
        }
        return 0;
    }

    public static String convertArgsListToString(ObservableList<ArgumentsClass> argums) {
        String s = "";
        for (int i = 0; i < argums.size(); i++) {
            s += argums.get(i).numOfArg + "@" + argums.get(i).argName + "#";
        }
        return s;
    }

    @FXML
    private void AddIngredients() {
        try {
            // Validate Inputs
            if (litsOfArgsComboBox.getValue() == null) {
                DialogHelper.getInstance().showOKAlert("يجب اختيار اسم المكون");
                return;
            } else if (numOfArguments.getText().trim().equals("")) {
                DialogHelper.getInstance().showOKAlert("- يجب ادخال عدد المكون");
                return;
            }
            // check if obj already exist ?
            ArgumentsClass existingObj = argumentsObservableList.stream().filter(tablevalue
                    -> litsOfArgsComboBox.getValue().equals(tablevalue.argName))
                    .findAny().orElse(null);
            if (existingObj != null) {
                DialogHelper.getInstance().showOKAlert("هذا المكون موجود بالفعل ");
                return;
            }
            // Validate Number
            double num;
            try {
                num = Double.parseDouble(numOfArguments.getText().trim());
                if (num <= 0) {
                    throw new Exception();
                }
            } catch (Exception e) {
                DialogHelper.getInstance().showOKAlert("-العدد غير صحيح ");
                return;
            }
            // Create Argument Object
            ArgumentsClass a = new ArgumentsClass(num, litsOfArgsComboBox.getValue());
            a.delBtn.setOnAction((e) -> {
                if (DialogHelper.getInstance().showConfirmAlert("هل انت متأكد من حذف المكون؟") == 1) {
                    argumentsObservableList.remove(a);
                    if (argumentsObservableList.isEmpty()) {
                        addProductNumberTextField();
                    }
                }
            });
            a.editBtn.setOnAction((e) -> {
                litsOfArgsComboBox.setValue(a.argName);
                numOfArguments.setText(String.valueOf(a.numOfArg));
                AddIngredientsButton.setText("تعديل");
                argumentsObservableList.remove(a);
                litsOfArgsComboBox.requestFocus();
            });
            // add obj to list/table
            argumentsObservableList.add(a);
            // reset data
            litsOfArgsComboBox.setValue(null);
            numOfArguments.setText("");
            AddIngredientsButton.setText("اضافة مكون");
            // relod table
            fillArgumentsTable();
            removeProductNumberTextField();
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> AddIngredients:- " + e);
        }
    }

    private void closeWindow() {
        ((Stage) productName.getScene().getWindow()).close();
        productToEdit = null;
    }

    @FXML
    private void fillArgs() {
        litsOfArgsComboBox.setItems(StorageController.getMy5amsNames(""));
        customListOfArgs.setItems(litsOfArgsComboBox.getItems());
    }

    private static void addProductNumberTextField() {
        if (!priceAndAmountContainer_Pointer.getChildren().contains(addInitCountContainerPointer)) {
            priceAndAmountContainer_Pointer.getChildren().add(addInitCountContainerPointer);
        }
    }

    private static void removeProductNumberTextField() {
        if (priceAndAmountContainer_Pointer.getChildren().contains(addInitCountContainerPointer)) {
            priceAndAmountContainer_Pointer.getChildren().remove(addInitCountContainerPointer);
        }
    }

    public static void fakeInitialize() {
        try {
            litsOfArgsComboBox_static.setValue(null);
            numOfArguments_static.setText("");
            productName_static.requestFocus();

            if (productToEdit == null) {
                productName_static.setText("");
                guestPrice_staic.setText("");
                staffPrice_staic.setText("");
                barCodeTextFieldPointer.setText("");
                argumentsObservableList.clear();
                fillArgumentsTable();
                confirmButton_static.setText("حفظ");
                addProductNumberTextField();
                saveAndAddAnotherButton_static.setVisible(true);
            } else {
                System.out.println("++++++++++" + productToEdit);
                removeProductNumberTextField();
                productName_static.setText(productToEdit.DrinkName);
                guestPrice_staic.setText(productToEdit.Price + "");
                staffPrice_staic.setText(productToEdit.Price2 + "");
                barCodeTextFieldPointer.setText(productToEdit.barCode);
                argumentsObservableList.clear();
                for (int i = 0; i < productToEdit.argums.size(); i++) {
                    ArgumentsClass a = productToEdit.argums.get(i);
                    a.delBtn.setOnAction((ActionEvent event) -> {
                        DialogHelper.getInstance().showConfirmAlert("هل انت متأكد من حذف المكون؟");
                        if (dialog.alert.confirm.ConfirmAlertController.flag == 1) {
                            dialog.alert.confirm.ConfirmAlertController.flag = 0;
                            argumentsObservableList.remove(a);
                        }
                    });
                    a.editBtn.setOnAction((ActionEvent event) -> {
                        litsOfArgsComboBox_static.setValue(a.argName);
                        numOfArguments_static.setText(a.numOfArg + "");
                        AddIngredientsButton_static.setText("تعديل");
                        argumentsObservableList.remove(a);
                        litsOfArgsComboBox_static.requestFocus();
                    });
                    argumentsObservableList.add(a);
                }
                fillArgumentsTable();
                confirmButton_static.setText("تعديل المنتج");
                saveAndAddAnotherButton_static.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in NewProductController -> fakeInitialize :-" + e);
        }
    }

    public static void fillArgumentsTable() {
        TreeItem<ArgumentsClass> root = new RecursiveTreeItem<>(argumentsObservableList, RecursiveTreeObject::getChildren);
        argumentsTable_staic.setRoot(root);
        argumentsTable_staic.setShowRoot(false);
    }

    // ======= Key Events =======
    @FXML
    private void AddargKeyRelased(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            AddIngredients();
        }
    }

    @FXML
    private void KeyPrs(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            closeWindow();
        }
    }

}
