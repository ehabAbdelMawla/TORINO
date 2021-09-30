package util.printing;

import datamodel.outerCafeRecordForPrint;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import util.Logger;
import util.db.DatabaseHandler;
import util.db.Methods;

/**
 *
 * @author Ehab Abdel Mawla
 */
public class PrintIngController {

    private static PrintIngController PrintIngController;

    public static PrintIngController getInstance() {
        if (PrintIngController == null) {
            PrintIngController = new PrintIngController();
        }
        return PrintIngController;
    }

    /* Print Resturant Recipt */
    synchronized public void saveReceet(int receetId, boolean print) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String folderName = "الفواتير/" + Methods.getMyDay() + "/الكافيتريا";
                    new File(folderName).mkdirs();
                    String outputFile = folderName + "/Receet Num-" + receetId + " " + Methods.getMyDay() + ".pdf";
                    ArrayList<outerCafeRecordForPrint> data = getOuterCafeRecordOf(receetId);
                    JRBeanCollectionDataSource outer = new JRBeanCollectionDataSource(data, false);
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("outerObj", outer);
                    PrintingData.getInstance().setShopParameters(parameters);
                    JasperReport jr = JasperCompileManager.compileReport("reports/cafeReceet80.jrxml");
                    JasperPrint jasperPrint = JasperFillManager.fillReport(jr, parameters, new JREmptyDataSource());
                    OutputStream outputStream = new FileOutputStream(new File(outputFile));
                    JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
                    outputStream.close();
                    if (print && !data.isEmpty()) {
                        Runtime.getRuntime().exec("reports/PDFtoPrinter \"" + "../" + outputFile + "\"");
                    }
                } catch (Exception e) {
                    Logger.writeLog("Exception in CheckOutBillController -> saveReceet(" + receetId + ") :- " + e);
                }
            }
        }.start();
    }

    private ArrayList<outerCafeRecordForPrint> getOuterCafeRecordOf(int receetId) {
        ArrayList<outerCafeRecordForPrint> data = new ArrayList<>();
        try {
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT TIME(resturantReceets.date),Date(resturantReceets.date),resturantReceets.receetId,resturantReceets.sheftNum, "
                    + "resturantReceets.receetType,resturantReceets.tableName,resturantReceets.userName, "
                    + "VAT.tax,VAT.serv, "
                    + "(SUM((resturantDetails.price*resturantDetails.num))+resturantReceets.calcAdditions), "
                    + "resturantReceets.discount, "
                    + "((SUM((resturantDetails.price*resturantDetails.num))+resturantReceets.calcAdditions)-resturantReceets.discount), "
                    + "resturantReceets.paid, "
                    + "(((SUM((resturantDetails.price*resturantDetails.num))+resturantReceets.calcAdditions)-resturantReceets.discount)-resturantReceets.paid),resturantReceets.customerCateg "
                    + " FROM resturantReceets JOIN   resturantDetails JOIN VAT "
                    + " ON resturantReceets.receetId=resturantDetails.receetId  AND resturantReceets.receetId=VAT.receetId  AND resturantReceets.receetId=" + receetId + " "
                    + " GROUP BY resturantReceets.receetId ").executeQuery();
            while (res.next()) {
                data.add(new outerCafeRecordForPrint(
                        res.getString(1),
                        res.getString(2),
                        res.getInt(3),
                        res.getInt(4),
                        res.getString(5),
                        res.getString(15),
                        res.getString(6),
                        res.getString(7),
                        res.getDouble(8),
                        res.getDouble(9),
                        res.getDouble(10),
                        res.getDouble(11),
                        res.getDouble(12),
                        res.getDouble(13),
                        res.getDouble(14)));
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in CheckOutBillController -> getOuterCafeRecordOf(int) :- " + e);
        }
        return data;
    }
}
