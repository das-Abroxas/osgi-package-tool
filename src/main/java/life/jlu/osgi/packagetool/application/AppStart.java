package life.jlu.osgi.packagetool.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import life.jlu.osgi.packagetool.controller.HostServicesControllerFactory;


/**
 * Entry point of the application which calls the JavaFX launch method.
 *
 * @author Jannis Hochmuth
 */
public class AppStart extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(AppStart.class.getResource("/fxml/main.fxml"));
        loader.setControllerFactory(new HostServicesControllerFactory(getHostServices()));

        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("OSGi Package Tool");
        stage.getIcons().add(new Image("/images/OSGi-logo.png"));
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        stage.setOnCloseRequest(event -> {
            System.setSecurityManager(null);  // Disable catch System.exit()
            Platform.exit();                  // Exit application
        });

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Launches the JavaFX application.
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
