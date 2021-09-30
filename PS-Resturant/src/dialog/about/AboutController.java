package dialog.about;

import static homepage.HomePageController.aboutDialog;
import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import util.Logger;

public class AboutController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void okAction() {
        aboutDialog.close();
    }

    @FXML
    private void mailAction() {
        try {
            Desktop desktop;
            if (Desktop.isDesktopSupported()
                    && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) {
                URI mailto = new URI("mailto:codeclinicstartup@gmail.com?");
                desktop.mail(mailto);
            } else {
                throw new RuntimeException("desktop doesn't support mailto; mail is dead anyway ;)");
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in AboutController -> mailAction:- " + e);
        }
    }

    @FXML
    private void githubAction() {
        try {
            Desktop.getDesktop().browse(new URI("https://www.youtube.com/channel/UCEPv0mBuSDtzc1xnwRPhWmA"));
        } catch (Exception e) {
            Logger.writeLog("Exception in AboutController -> githubAction" + e);
        }
    }

    @FXML
    private void facebookAction() {
        try {
            Desktop.getDesktop().browse(new URI("https://www.facebook.com/Code-Clinic-110943976922650"));
        } catch (Exception e) {
            Logger.writeLog("Exception in AboutController -> facebookAction" + e);
        }
    }
}
