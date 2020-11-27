package life.jlu.osgi.packagetool.controller;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;


/**
 * Adds the functionality to the about.fxml scene.
 *
 * @author Jannis Hochmuth
 */
public class AboutController {

    /* ----------------------------------------------------------------------------------------- */
    /* ----- FXML Node References -------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    @FXML
    private Hyperlink url_link;


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Global Variables ------------------------------------------------------------------ */
    /* ----------------------------------------------------------------------------------------- */
    private final HostServices hostServices;


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Constructor ----------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    public AboutController(HostServices hostServices) {
        this.hostServices = hostServices;
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- FXML defined actions -------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    @FXML
    private void openLink() {
        hostServices.showDocument( url_link.getText() );
    }
}
