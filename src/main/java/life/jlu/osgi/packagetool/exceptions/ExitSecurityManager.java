package life.jlu.osgi.packagetool.exceptions;

import java.security.Permission;

/**
 * Security Manager that catches all System.exit(int) calls and instead throws a SecurityException.
 *
 * @author Jannis Hochmuth
 */
public class ExitSecurityManager extends SecurityManager {

    @Override
    public void checkExit(int status) {
        throw new SecurityException();
    }

    @Override
    public void checkPermission(Permission perm) {
        // Allow other activities by default
    }
}
