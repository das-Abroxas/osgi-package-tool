package life.jlu.osgi.packagetool.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Util class with static methods to create and show different JavaFX Dialogs/Alerts.
 *
 * @author Jannis Hochmuth
 */
public class DialogUtil {

    private DialogUtil() {}

    /**
     * Creates a standard JavaFX error alert with a custom header text.
     * @param header Header message
     */
    public static void showErrorDialog(String header) {
        Alert error = new Alert( Alert.AlertType.ERROR);
        error.setHeaderText(header);
        error.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        error.showAndWait();
    }

    /**
     * Creates a standard JavaFX error alert with a custom header and content text.
     * @param header Header message
     * @param content Content message
     */
    public static void showErrorDialog(String header, String content) {
        Alert error = new Alert( Alert.AlertType.ERROR );
        error.setHeaderText(header);
        error.setContentText(content);
        error.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        ((Stage) error.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/images/OSGi-logo.png"));

        error.showAndWait();
    }

    /**
     * Creates a standard JavaFX information alert with a custom header and content text.
     * @param header Header message
     * @param content Content message
     */
    public static void showInformationDialog(String header, String content) {
        Alert error = new Alert( Alert.AlertType.INFORMATION );
        error.setHeaderText(header);
        error.setContentText(content);
        error.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        error.showAndWait();
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- "Generic" Dialog for Exceptions with expandable content for stacktrace	------------ */
    /* ------- Should only be used for debug purposes. ----------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    /**
     * Creates an alert for an Exception with the stacktrace as expandable content and an individual message.
     * @param ex - An Exception
     * @param alertContent - Individual message
     */
    public static void createExceptionDialog(Exception ex, String alertContent) {
        Alert alert = new Alert(Alert.AlertType.ERROR);        // Create new simple Error dialog
        alert.setTitle("Something went wrong.");               // Static title of dialog
        alert.setHeaderText(ex.getClass().toGenericString());  // Dynamic text in header panel
        alert.setContentText(alertContent);                    // Dynamic text in content panel
        alert.getDialogPane().getStylesheets().add(            // Add CSS to dialog
                DialogUtil.class.getResource("/css/main.css").toExternalForm());

        // Create expandable Exception
        // --------------------------------------------------------------------
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Stacktrace of the Exception:");
        label.getStyleClass().add("exception-header");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set stacktrace of Exception into the expandable part of the dialog pane
        // --------------------------------------------------------------------
        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().setExpanded(false);

        Stage stage = (Stage)alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/images/OSGi-logo.png"));

        alert.showAndWait();
    }

    /**
     * Creates an alert for an Exception with the stacktrace as expandable content and an individual message.
     * @param ex - An Exception
     * @param alertContent - Individual message
     */
    public static void createExceptionDialog(Exception ex, String alertHeader, String alertContent) {
        Alert alert = new Alert(Alert.AlertType.ERROR);  // Create new simple Error dialog
        alert.setTitle("Something went wrong.");         // Static title of dialog
        alert.setHeaderText(alertHeader);                // Dynamic text in header panel
        alert.setContentText(alertContent);              // Dynamic text in content panel
        alert.getDialogPane().getStylesheets().add(      // Add CSS to dialog
                DialogUtil.class.getResource("/css/main.css").toExternalForm());

        // Create expandable Exception
        // --------------------------------------------------------------------
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Stacktrace of the Exception:");
        label.getStyleClass().add("exception-header");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set stacktrace of Exception into the expandable part of the dialog pane
        // --------------------------------------------------------------------
        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().setExpanded(false);

        Stage stage = (Stage)alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/images/OSGi-logo.png"));

        alert.showAndWait();
    }
}
