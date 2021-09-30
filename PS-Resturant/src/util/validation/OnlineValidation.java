package util.validation;

import controlpanel.ControlPanel;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.ByteArrayInputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Duration;
import launcher.LauncherSceneController;
import util.Logger;
import static util.validation.Security.getResultOfCommand;
import util.db.DatabaseHandler;

/**
 *
 * @author Ehab Abdel Mawla
 */
public class OnlineValidation extends Observable {

    public static class Customer {

        public boolean licence;
        public String startDate, lastAccess;

        public Customer() {
        }

        @Override
        public String toString() {
            return "Customer{\n" + "licence :" + licence + "\n startDate: " + startDate + "\n lastAccess: " + lastAccess + "\n}";
        }

    }

    private static OnlineValidation onlineValid;

    public static OnlineValidation getInstance() {
        if (onlineValid == null) {
            onlineValid = new OnlineValidation();
        }
        return onlineValid;
    }

    private SimpleStringProperty instanceName = new SimpleStringProperty(ControlPanel.getInstance().INSTANCE_ONLINE_NAME);
    private SimpleStringProperty UUID = new SimpleStringProperty("");
    private StringExpression title;
    private boolean licence;
    private SimpleDateFormat simpleDateFormater = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
    private Customer programCustomer;
    /**
     * TimeLine to validate (Internet Connection & Local Time Change)
     */
    private Timeline checkConnections;

    /**
     * initialize the object with instanceName & UUID
     */
    public OnlineValidation() {
        this.title = Bindings.format("%s/%s/%s", "PS-Resturant-Touch", this.instanceName, this.UUID);
        try {
            this.UUID.setValue(getResultOfCommand(new String[]{"wmic", "csproduct", "get", "UUID"}));
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + ".OnlineValidation() => " + e);
        }
    }

    /**
     * Checks whether if Local License is TRUE or FALSE
     *
     * @return Local License is TRUE or FALSE
     */
    public boolean localValidation() {
        System.out.println("LOCAL VALIDATION");
        try {
            ResultSet result = DatabaseHandler.con.prepareStatement("SELECT * FROM licence").executeQuery();
            while (result.next()) {
                this.licence = result.getString(1).equalsIgnoreCase("true");
            }
            if (!this.licence) {
                LauncherSceneController.LICENSE.set(2);
                return false;
            } else {
                LauncherSceneController.LICENSE.set(1);
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + ".localValidation() => " + e);
        }
        return true;
    }

    public void showLicenceErrorAlert() {
        LauncherSceneController.LICENSE.setValue(2);
        // OLD => Show Licence Error Alert
    }

    private void timeline(DatabaseReference ref, boolean updateLastAccess) {
        try {
            System.out.println("timeline() => updateLastAccess: " + updateLastAccess);
            NTPClient.getInstance().requestServerDate();
            if (Math.abs(NTPClient.getInstance().getServerDate().getTime() - new Date().getTime()) > 300000) {
                // time changed
                throw new Exception("Please adjust PC Time.");
            }
            if (updateLastAccess) {
                ref.child("lastAccess").setValueAsync(simpleDateFormater.format(NTPClient.getInstance().getServerDate()));
            }
        } catch (Exception e) {
            Logger.writeLog("timeline() Exception => " + e);
            this.licence = false;
            Platform.runLater(() -> {
                if (e.getMessage().equals("")) {
                    // Internet ERROR
                    LauncherSceneController.LICENSE.setValue(3);
                } else {
                    // Incorrect Time 
                    LauncherSceneController.LICENSE.setValue(4);
                }
            });
        }
    }

    public void init(SimpleBooleanProperty isAllFine) {
        try {
            if (ControlPanel.getInstance().IS_DEMO) {
                try {
                    NTPClient.getInstance().requestServerDate();
                } catch (Exception ex) {
                    // TODO Add Exception Handler
                    System.out.println("requestServerDate => " + ex);
                }
            }

            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

            String serviceAccountKeyContent = "{\n"
                    + "  \"type\": \"service_account\",\n"
                    + "  \"project_id\": \"sunglassesvalidation\",\n"
                    + "  \"private_key_id\": \"df27779454fadd815bd6cede1fca43d88a224e55\",\n"
                    + "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDNl9dRSBrBLytG\\nYHEdqZS7TQnrwCBxrSFntJFEqYqyHengHKHQg9xd5erbTD/PbefXpBD4mNxwh+cR\\nHAXnkVchYkYZ0IaUl2H+KANq/mSxojPNSkEBvSbclpD531mSvswUxBtPW5dN1hsX\\nWQ6V+IDaLp6pr8enO4kC33INidN1CYrxpoOJ+m5Gruv2hh6hl1JKW0JoP4Ht+qL4\\nd0RFkOjbaU9o1RFsf6zqfekqjnfH2o+zWMxgsZgxD6eMrj++TNtI3TwAqW2DhWY2\\nDvX3dGSKv5f0CAGDBHomo4Z8KUbFBX55XQucvcWrllmpgQHPIdW1QmIOMpvVlIE4\\nEIkj7hOhAgMBAAECggEACxfpnB7sJKbnKp+7Esrq9dVRFvmxtniNUCN34DDWxUgk\\nbb2VxKLM0fs9OjKJ0edABox5hvR2lpLatrtgnS9kOG6tUijEjkGB3d0qXAxYghY6\\nvdXHHrEmZuov7e+tjWPCrs8rGxPZ3O8T3KVnSlPTnhI8aPMNbpphiPhmFtFsSckO\\ndiP1zxtISCxNjfg8SJHkLs5mEsWojHVv6tk73Vmoltijf6KHbP0rjAhL/w2YwLp6\\nuiz/8pLCWhLKBspe1UyByeRlTj+iXwjy2IQEtWDeoXI+7QR5DBTsgNS8uu5vXK4a\\nwS+lJXUZdENvFgjozMdopL4xNQpktRMu7Gsm5FnssQKBgQD2dWVOIlcMjt+CzU0o\\nlaFF/gKYA4xiHXkFVt77ObTCIAdIk7OBFYnJGl2W2k1acaNN0UeDv9TZIptiu+Dm\\nYpE+nqQ1nOOh+oAqxvrK5bG2WGeWR0/1XPOTdMAcvYxUOUpBd/RMGRUpE6fQevpE\\nzvELcc/PZ8cPTwkEN+7sMrr3yQKBgQDVjW+Glr3Lj1YZhuoSDw+2EFObV2F1s0v2\\nninQ8Ky4WawGDXgRtG5eEfqjjKQ8GTCVEc/56ej30yLNIELQ4pg5OOVqLFLqq4wA\\nIOLQ7euU9oByhDRJkfzzYaQsXHRHCc5tXHNiSF54MJAE/oew/Or0WVu8h9zqqC3r\\nnBjCNM9ZGQKBgEjs88zq3Fca74gEt5AKtm2/e4T/h8qAbg7mZZRwvsuyLnWLPtIz\\nTVH0Oz0n2zun5GpiQob5fEHDu7a7YRDKma+twWkbwz9qoAIIXq0qyirqfbw53a78\\nkhZPtYjLjXpeeVpl5COgSGKfLkjCGsvBcBGy24rDamNdP42//zJk0G4BAoGBAKGT\\ne9iHYV8NOoXh8GRthOOqArg8Kv9VA5q7HMR79RMetet2cWxj6kW+sLbiCLl4Dawc\\naBGdC6nG1EQDrspBSA+zntDwbGQmG61VNIgEHAT/ZTxADxfDH3I+ZGs0IL5HCyrv\\nklXAj8m8ZJuCwcQF+PecTK6e1WWujeAAoTL/pgqxAoGAZwDwbsUD/Jl90699HNzy\\n3cRWh4iT4fwwsm4AI4+Fu18J9UrzWo6iPE3KE0cM1YgARztiWwKJOnQcRfh1ZGjE\\ndWdNi6xIdzpvYWTqwGviKSmplmsmgHNbyK9f3VHT0Uq3k9DZ0Dnp0KePHgWaYL6O\\nR743r/3rdXwoOPR9AblnvWM=\\n-----END PRIVATE KEY-----\\n\",\n"
                    + "  \"client_email\": \"firebase-adminsdk-6ium6@sunglassesvalidation.iam.gserviceaccount.com\",\n"
                    + "  \"client_id\": \"103809881036936233998\",\n"
                    + "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n"
                    + "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n"
                    + "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n"
                    + "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-6ium6%40sunglassesvalidation.iam.gserviceaccount.com\"\n"
                    + "}";

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(serviceAccountKeyContent.getBytes())))
                    .setDatabaseUrl("https://sunglassesvalidation.firebaseio.com/")
                    .build();

            FirebaseApp.initializeApp(options);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(title.get());

            if (ControlPanel.getInstance().IS_DEMO) {
                checkConnections = new Timeline(new KeyFrame(Duration.millis(120000), ae -> {
                    System.out.println("------------------------ Timeline -----------------------");
                    timeline(ref, localValidation());
                }));
                checkConnections.setCycleCount(Animation.INDEFINITE);
                checkConnections.play();
                // call timeline() one time before Timeline Duration comes
                timeline(ref, false);
                System.out.println("checkConnections.play() ...");
            }

            ref.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    programCustomer = dataSnapshot.getValue(Customer.class);
                    System.out.println(programCustomer);
                    if ((programCustomer == null || programCustomer.startDate == null) && ControlPanel.getInstance().IS_DEMO) {
                        System.out.println("document = null");
                        // Add Instance at first time   
                        String serverDateString = simpleDateFormater.format(NTPClient.getInstance().getServerDate());
                        Map<String, Object> data = new HashMap<>();
                        data.put("licence", true);
                        data.put("lastAccess", serverDateString);
                        data.put("startDate", serverDateString);
                        ref.setValueAsync(data);
                        updateLocalLicence(true);
                    } else if (programCustomer != null && programCustomer.licence) {
                        System.out.println("Instance = Exist & Licence = TRUE");
                        if (ControlPanel.getInstance().IS_DEMO) {
                            LocalDate StartDate = LocalDate.parse(programCustomer.startDate, dateTimeFormatter);
                            LocalDate currentServerDate = LocalDate.parse(simpleDateFormater.format(NTPClient.getInstance().getServerDate()), dateTimeFormatter);
                            // differance between ServerNowDate & InstanceStartDate
                            Period differance = Period.between(StartDate, currentServerDate);
                            if (differance.getDays() > ControlPanel.getInstance().DEMO_NUM_OF_DAYS) {
                                ref.child("licence").setValueAsync(false);
                                licence = false;
                                updateLocalLicence(false);
                                return;
                            }
                        }

                        updateLocalLicence(programCustomer.licence);
                        licence = programCustomer.licence;
                        ref.child("lastAccess").setValueAsync(LocalDateTime.now().format(dateTimeFormatter));
                    } else {
                        System.out.println("Instance = Exist & Licence = FALSE");
                        updateLocalLicence(false);
                        ref.child("lastAccess").setValueAsync(LocalDateTime.now().format(dateTimeFormatter));
                        licence = false;
                    }
                    // fire change event to all listeners to update their values
                    setChanged();
                    notifyObservers();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Logger.writeLog("Error in " + getClass().getName() + " -> init -> DatabaseError -> onCancelled() " + error);
                }
            });
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> init :- " + e);
        }
    }

    private void updateLocalLicence(boolean newLicence) {
        try {
            System.out.println("updateLocalLicence(" + newLicence + ")");
            creatLocalLicenceTable();
            DatabaseHandler.con.prepareStatement("UPDATE licence SET value='" + newLicence + "'").execute();
            localValidation();
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + ".updateLocalLicence :- " + e);
        }
    }

    private void creatLocalLicenceTable() {
        try {
            DatabaseHandler.con.prepareStatement("CREATE TABLE licence (value varchar(255) NOT NULL)").execute();
            DatabaseHandler.con.prepareStatement("INSERT INTO licence VALUES ('false')").execute();
        } catch (Exception e) {
//           Do No Thing
        }
    }

    public Customer getProgramCustomer() {
        return programCustomer;
    }
}
