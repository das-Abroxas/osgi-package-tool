package life.jlu.osgi.packagetool.exceptions;


/**
 * The InvalidDistroJarException wraps all unchecked standard Java exception.
 * This exception should be used if a loaded OSGi bundle violates a condition of validation.
 *
 * @author Jannis Hochmuth
 */
public class InvalidDistroJarException extends RuntimeException {

    public InvalidDistroJarException(String message) {
        super(message);
    }
}
