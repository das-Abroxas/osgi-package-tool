package life.jlu.osgi.packagetool.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import life.jlu.osgi.packagetool.model.ExportPackage;
import life.jlu.osgi.packagetool.model.ImportPackage;

/**
 * Adds the functionality to the package.fxml scene.
 *
 * @author Jannis Hochmuth
 */
public class PackageMetaController {

    /* ----------------------------------------------------------------------------------------- */
    /* ----- FXML Node References -------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    @FXML
    private Label container_name_value;
    @FXML
    private Label container_version_value;
    @FXML
    private VBox container_attrs;

    @FXML
    private Label bundle_name_value;
    @FXML
    private Label bundle_version_value;
    @FXML
    private VBox bundle_dynamic_attrs;


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Metadata Update ------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    /**
     * Updates the metadata information labels
     * @param iPkg Package with metadata attributes
     */
    public void update(ImportPackage iPkg) {

        if (iPkg != null) {

            bundle_name_value.setText( iPkg.getFqn() );
            bundle_version_value.setText( iPkg.getMinVersion() + "," + iPkg.getMaxVersion() );
            bundle_dynamic_attrs.getChildren().clear();

            iPkg.getAttributes().entrySet().stream()
                    .filter(e -> !e.getKey().equals("version"))
                    .forEach(e -> {
                        HBox row = createAttributeRow( e.getKey(), e.getValue() );
                        bundle_dynamic_attrs.getChildren().add(row);
                    });

            if (iPkg.isResolved()) {

                ExportPackage ePkg = iPkg.getResolvePkg();

                container_name_value.setText( ePkg.getFqn() );
                container_version_value.setText( ePkg.getVersion().toString() );
                container_attrs.getChildren().clear();

                ePkg.getAttributes().entrySet().stream()
                        .filter(e -> !e.getKey().equals("version"))
                        .forEach(e -> {
                            HBox row = createAttributeRow( e.getKey(), e.getValue() );
                            bundle_dynamic_attrs.getChildren().add(row);
                        });
            } else {
                container_name_value.setText( "/" );
                container_version_value.setText( "/" );
                container_attrs.getChildren().clear();
            }
        }
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Helper Methods -------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    private HBox createAttributeRow(String key, String value) {
        String label = capitalize(key);
        label += label.endsWith(":") ? "" : ":";

        Label attr_label = new Label(label);
        attr_label.setMinWidth(75);
        attr_label.setMaxWidth(75);

        Label attr_value = new Label(value.replace(",", System.lineSeparator()));
        attr_value.setWrapText(true);
        HBox.setHgrow(attr_value, Priority.ALWAYS);

        HBox row = new HBox();
        HBox.setHgrow(row, Priority.ALWAYS);
        row.getChildren().addAll(attr_label, attr_value);

        return row;
    }

    private String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
