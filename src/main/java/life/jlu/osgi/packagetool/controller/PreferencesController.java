package life.jlu.osgi.packagetool.controller;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import life.jlu.osgi.packagetool.application.AppPreferences;
import life.jlu.osgi.packagetool.util.StringToIntegerConverter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Adds the functionality to the preferences.fxml scene.
 *
 * @author Jannis Hochmuth
 */
public class PreferencesController {

    /* ----------------------------------------------------------------------------------------- */
    /* ----- FXML Node References -------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    @FXML
    private GridPane main_pane;

    @FXML
    private TextField container_path;
    @FXML
    private TextField container_host;
    @FXML
    private Spinner<Integer> container_port;
    @FXML
    private Spinner<Integer> task_timeout;
    @FXML
    private CheckBox dynamic_include;

    @FXML
    private Button save_btn;


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Validation Properties ------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    private final BooleanProperty valid_path = new SimpleBooleanProperty(false);


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Initialization -------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    @FXML
    public void initialize() {
        addValidationListener();
        addValidationBinding();

        container_path.setText(
                AppPreferences.getStringPreference(container_path.getId()) );
        container_path.setTooltip(
                new Tooltip("Full path to the directory in which the OSGi container distro JAR will be created.") );

        container_host.setText( AppPreferences.getStringPreference(container_host.getId()) );
        container_host.setTooltip( new Tooltip("Either machine name or IP v4/v6 address of the host."));

        container_port.setPromptText("E.g. AQute Remote Agent default port is 29998");
        container_port.setTooltip( new Tooltip("E.g. AQute Remote Agent default port is 29998") );
        container_port.setValueFactory( new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 65535, 0) );
        container_port.getValueFactory().setConverter(new StringToIntegerConverter());
        container_port.getEditor().setOnAction((ActionEvent e) -> {
            String text        = container_port.getEditor().getText();
            Integer enterValue = container_port.getValueFactory().getConverter().fromString(text);

            container_port.getValueFactory().setValue( enterValue );
        });
        container_port.getValueFactory().setValue(AppPreferences.getIntegerPreference(container_port.getId()));

        dynamic_include.setSelected( AppPreferences.getBooleanPreference(dynamic_include.getId()) );

        task_timeout.setPromptText("Global task timeout in seconds.");
        task_timeout.setTooltip( new Tooltip("Global task timeout in seconds.") );
        task_timeout.setValueFactory( new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0) );
        task_timeout.getValueFactory().setConverter(new StringToIntegerConverter());
        task_timeout.getEditor().setOnAction((ActionEvent e) -> {
            String text        = task_timeout.getEditor().getText();
            Integer enterValue = task_timeout.getValueFactory().getConverter().fromString(text);

            task_timeout.getValueFactory().setValue( enterValue );
        });
        task_timeout.getValueFactory().setValue(AppPreferences.getIntegerPreference(task_timeout.getId()));
    }

    private void addValidationListener() {
        container_path.textProperty().addListener(event -> {
            valid_path.setValue( Files.exists(Paths.get(container_path.getText())) );

            if (!valid_path.getValue()) {
                if (!container_path.getStyleClass().contains("invalid-pref"))
                    container_path.getStyleClass().add("invalid-pref");
            } else {
                container_path.getStyleClass().remove("invalid-pref");
            }
        });
    }

    private void addValidationBinding() {
        save_btn.disableProperty().bind( valid_path.not() );
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- FXML defined actions -------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    @FXML
    private void savePreferences() {
        AppPreferences.putPreference(container_path.getId(), container_path.getText());
        AppPreferences.putPreference(container_host.getId(), container_host.getText());
        AppPreferences.putPreference(container_port.getId(), container_port.getValue());
        AppPreferences.putPreference(dynamic_include.getId(), dynamic_include.isSelected());
        AppPreferences.putPreference(task_timeout.getId(), task_timeout.getValue());

        ((Stage) main_pane.getScene().getWindow()).close();
    }

    @FXML
    private void exportPreferences() {
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(extFilter);

        File file = chooser.showSaveDialog(main_pane.getScene().getWindow());

        if (file != null)
            AppPreferences.exportPreferences( file );
    }

    @FXML
    private void importPreferences() {
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(extFilter);

        File file = chooser.showOpenDialog(main_pane.getScene().getWindow());

        if (file != null) {
            AppPreferences.importPreferences(file);
            initialize();
        }
    }
}
