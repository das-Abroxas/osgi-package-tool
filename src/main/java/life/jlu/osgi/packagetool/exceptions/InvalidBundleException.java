package life.jlu.osgi.packagetool.exceptions;


/**
 * The InvalidBundleException wraps all unchecked standard Java exception.
 * This exception should be used if a loaded OSGi bundle has no Bundle-SymbolicName.
 *
 * @author Jannis Hochmuth
 */
public class InvalidBundleException extends RuntimeException {

    public InvalidBundleException(String message) {
        super(message);
    }
}
