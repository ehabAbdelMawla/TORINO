package util.mail;

import controlpanel.ControlPanel;
import datamodel.ArchieveReport;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javafx.application.Platform;
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
import util.gui.BuilderUI;
import util.printing.PrintingData;

/**
 *
 * @author Ehab Abdel Mawla
 */
public class Mail {

    public static ArchieveReport data;

    synchronized public static void sendMail() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Logger.writeLog("--------- SEND MAIL ---------");
                    ResultSet res = DatabaseHandler.con.prepareStatement("SELECT mail1,mail2,shopName FROM sendMailData").executeQuery();
                    String mail1 = "", mail2 = "", shopName = "";
                    while (res.next()) {
                        mail1 = res.getString(1);
                        mail2 = res.getString(2);
                        shopName = res.getString(3);
                    }
                    preparecurrentSheftReport();
                    sendMail(mail1, shopName, "Reports/DailySheet.pdf");
                    sendMail(mail2, shopName, "Reports/DailySheet.pdf");
                } catch (Exception e) {
                    Logger.writeLog("Exception in " + Mail.class.getName() + ".sendMail() :- " + e);
                }
            }
        }.start();
    }

    synchronized public static void sendMail(String recepient, String shopName, String attachmentFile) {
        try {
            if (recepient == null || recepient.equals("")) {
                return;
            }
            Logger.writeLog("..... try To Send .....");
            Logger.writeLog("mail: " + recepient);
            Logger.writeLog("attachmentFile: " + attachmentFile);
            Properties properties = new Properties();

            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");

            final String myAccountEmail = "codeclinicreports@gmail.com";
            final String password = "Bido556613";

            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(myAccountEmail, password);
                }
            });

            Message message = prepareMessage(session, myAccountEmail, recepient, shopName, attachmentFile);
            Transport.send(message);
            Platform.runLater(() -> {
                BuilderUI.showNotification(new MaterialDesignIconView(MaterialDesignIcon.GMAIL), "Report sent Successfully\n" + recepient);
            });
            Logger.writeLog("Email sent successfully to : " + recepient + ",Email subject: " + shopName);
        } catch (Exception e) {
            Platform.runLater(() -> {
                BuilderUI.showNotification(new MaterialDesignIconView(MaterialDesignIcon.CLOUD_OUTLINE_OFF), "Error in E-mail sending");
            });
            Logger.writeLog("Exception in " + Mail.class.getName() + ".sendMail(String recepient: " + recepient + ",String shopName: " + shopName + ") : " + e);
        }
    }

    synchronized public static Message prepareMessage(Session session, String myAccountEmail, String recepient, String shopName, String attachmentFile) {
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(myAccountEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.attachFile(new File(attachmentFile));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setSubject(shopName);
            message.setContent(multipart);

//        message.setContent("<h1 style='color:red'>Ehab Abdel Mawla</h1><p>This Mail Send By java</p>","text/html");
//        message.setText("ehab Abdel Mawla \n This Mail Is Send From Java ");
        } catch (Exception e) {
            Logger.writeLog("Exception in " + Mail.class.getName() + " .prepareMessage(Session session,String myAccountEmail,String recepient,String shopName)");
        }
        return message;
    }

//    Get Daily Data
    public static void preparecurrentSheftReport() {
        try {
            ArrayList<ArchieveReport> days = new ArrayList<>();
            days.add(data);
            String outputFile = "Reports/DailySheet.pdf";
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("days", new JRBeanCollectionDataSource(days, false));
            parameters.put("fromDate", Methods.getMyDay());
            parameters.put("toDate", Methods.getMyDay());
            parameters.put("playstation", ControlPanel.getInstance().HAS_PLAYSTATION);
            parameters.put("AccessCustomerDataPage", ControlPanel.getInstance().CUSTOMER_DATA);
            parameters.put("netIncomeVisibilty", ControlPanel.getInstance().EXPENSESS_FROM_DAILY_INCOME);
            PrintingData.getInstance().setShopParameters(parameters);
            JasperReport jr = JasperCompileManager.compileReport("Reports/DailySheet.jrxml");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jr, parameters, new JREmptyDataSource());
            OutputStream outputStream = new FileOutputStream(new File(outputFile));
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
        } catch (Exception e) {
            Logger.writeLog("Exception in " + Mail.class.getName() + " preparecurrentSheftReport()");
        }
    }
}
