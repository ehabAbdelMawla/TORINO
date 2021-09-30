/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package util.gui.load;

import dialog.alert.confirm.ConfirmAlertController;
import dialog.alert.confirm.reason.ConfirmReasonAlertController;
import dialog.alert.ok.OkAlertController;
import datamodel.Customer;
import datamodel.Order;
import datamodel.Payment;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import restaurant.transfer.TransferTableController;
import util.Logger;
import util.validation.SingleInstance;

/**
 *
 * @author Bayoumi
 */
public class DialogHelper {

    private static DialogHelper dialogHelper;

    private DialogHelper() {
    }

    public static DialogHelper getInstance() {
        if (dialogHelper == null) {
            dialogHelper = new DialogHelper();
        }
        return dialogHelper;
    }

    public int openTrasferTableOrder(String currentTable) {
        LoadHelper loadHelper = LoadHelper.getInstance();
        TransferTableController.transferConfirmFlag = 0;
        try {
            // config data
            TransferTableController.setData(currentTable);
            // show stage
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            loadHelper.Stage_TransferTable.setX(bounds.getWidth() / 2 - (520 / 2));
            loadHelper.Stage_TransferTable.setY(bounds.getHeight() / 2 - (191 / 2));
            if (loadHelper.Stage_TransferTable.getOwner() == null) {
                loadHelper.Stage_TransferTable.initOwner(loadHelper.Stage_TableOrder);
            }
            loadHelper.Stage_TransferTable.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + ".openTrasferTableOrder() :- " + e);
        }
        return TransferTableController.transferConfirmFlag;
    }

    public Payment openCheckOutBill(double total, Payment p) {
        LoadHelper loadHelper = LoadHelper.getInstance();
        try {
            // config data
            if (p == null) {
                loadHelper.checkOutBillController.setData(total);
            } else {
                loadHelper.checkOutBillController.setPaymentDetails(p);
            }
            // show stage
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            loadHelper.Stage_CheckOutBill.setX(bounds.getWidth() / 2 - (436 / 2));
            loadHelper.Stage_CheckOutBill.setY(bounds.getHeight() / 2 - (416 / 2));
            loadHelper.Stage_CheckOutBill.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + ".openCheckOutBill() :- " + e);
        }
        return loadHelper.checkOutBillController.getData();
    }

    public void showBillDetails(Order order, boolean visabliltyofPaidButton) {
        LoadHelper loadHelper = LoadHelper.getInstance();
        try {
            loadHelper.billDetailsController.setData(order, visabliltyofPaidButton);
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            loadHelper.Stage_billDetails.setX(bounds.getWidth() / 2 - (800 / 2));
            loadHelper.Stage_billDetails.setY(bounds.getHeight() / 2 - (500 / 2));
            if (loadHelper.Stage_billDetails.getOwner() == null) {
                loadHelper.Stage_billDetails.initOwner(SingleInstance.getInstance().getCurrentStage());
            }
            loadHelper.Stage_billDetails.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + ".showBillDetails() :- " + ex);
        }
    }

    public Customer chooseCustomer() {
        LoadHelper loadHelper = LoadHelper.getInstance();
        try {
            //   ===== Open and Config Stage =====
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            loadHelper.Stage_ChooseCustomer.setX(bounds.getWidth() / 2 - (loadHelper.chooseCustomerController.parentNode.getPrefWidth() / 2));
            loadHelper.Stage_ChooseCustomer.setY(bounds.getHeight() / 2 - (loadHelper.chooseCustomerController.parentNode.getPrefHeight() / 2));
            if (loadHelper.Stage_ChooseCustomer.getOwner() == null) {
                loadHelper.Stage_ChooseCustomer.initOwner(SingleInstance.getInstance().getCurrentStage());
            }
            loadHelper.chooseCustomerController.Filter();
            loadHelper.Stage_ChooseCustomer.showAndWait();
            if (loadHelper.chooseCustomerController.hasData) {
                return loadHelper.chooseCustomerController.choosenCustomer;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + ".chooseCustomer() :-" + e);
        } finally {
            new Thread() {
                public void run() {
                    loadHelper.chooseCustomerController.hasData = false;
                    loadHelper.chooseCustomerController.choosenCustomer = null;
                    //   ===== Reset Filtering Inputs =====
                    loadHelper.chooseCustomerController.searchComBox.setText("");
                    loadHelper.chooseCustomerController.customerTypeGroup.selectToggle(loadHelper.chooseCustomerController.customerTypeGroup.getToggles().get(0));

                }
            }.start();
        }
        return null;
    }

    public Customer viewCustomer(Customer customer, boolean isEditableView, boolean isPlaystaionData) {
        try {
            LoadHelper loadHelper = LoadHelper.getInstance();
            // set customer Data
            loadHelper.viewCustomerController.setData(customer, isEditableView, isPlaystaionData);
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            if (loadHelper.Stage_ViewCustomer.getOwner() == null) {
                loadHelper.Stage_ViewCustomer.initOwner(SingleInstance.getInstance().getCurrentStage());
            }
            loadHelper.Stage_ViewCustomer.setX(bounds.getWidth() / 2 - (1200 / 2));
            loadHelper.Stage_ViewCustomer.setY(bounds.getHeight() / 2 - (510 / 2));
            loadHelper.Stage_ViewCustomer.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + ".viewCustomer() :-" + e);
        }
        return null;
    }

    public void showConfirmAlertWithReason(String text) {
        try {
            ConfirmReasonAlertController.delReson = "";
            ConfirmReasonAlertController.text.setText(text);
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            Stage stage = LoadHelper.getInstance().stage_reasonConfirmAlert;
            stage.setX(bounds.getWidth() / 2 - (460 / 2));
            stage.setY(bounds.getHeight() / 2 - (193 / 2));
            stage.showAndWait();
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + ".showConfirmAlertWithReason(String text: " + text + ") :- " + e);
        }
    }

    public int showConfirmAlert(String text) {
        try {
            ConfirmAlertController.flag = 0;
            ConfirmAlertController.text.setText(text);
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            Stage stage = LoadHelper.getInstance().stage_confirmAlert;
            stage.setX(bounds.getWidth() / 2 - (460 / 2));
            stage.setY(bounds.getHeight() / 2 - (193 / 2));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + ".showConfirmAlert(String text: " + text + ") :- " + e);
        }
        return ConfirmAlertController.flag;
    }

    public void showOKAlert(String text) {
        try {
            OkAlertController.text.setText(text);
            Stage stage = LoadHelper.getInstance().stage_okAlert;
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            stage.setX(bounds.getWidth() / 2 - (459 / 2));
            stage.setY(bounds.getHeight() / 2 - (193 / 2));
            stage.showAndWait();
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + ".showOKAlert(String text: " + text + ") :- " + e);
        }
    }

    // OverLoading Method to show alert without stop other code execution
    public void showOKAlert(String text, boolean f) {
        try {
            OkAlertController.text.setText(text);
            Stage stage = LoadHelper.getInstance().stage_okAlert;
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            stage.setX(bounds.getWidth() / 2 - (459 / 2));
            stage.setY(bounds.getHeight() / 2 - (193 / 2));
            stage.show();
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + ".showOKAlert(String text: " + text + ",boolean) :- " + e);
        }
    }
}
