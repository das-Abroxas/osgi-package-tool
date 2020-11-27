package life.jlu.osgi.packagetool.application;


/**
 * Alternative entry point, so that the JavaFX modules do not have to be
 * explicitly integrated and loaded via the JVM options
 *
 * @author Jannis Hochmuth
 */
public class Main {

    /**
     * Main method to launch the application
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        AppStart.main(args);
    }
}
