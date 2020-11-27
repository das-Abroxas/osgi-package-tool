package life.jlu.osgi.packagetool.controller;

import javafx.application.HostServices;
import javafx.util.Callback;

import java.lang.reflect.Constructor;


/**
 * Factory to create Controller classes which contain a reference to the JavaFX HostServices.
 *
 * @author Jannis Hochmuth
 */
public class HostServicesControllerFactory implements Callback<Class<?>,Object> {

    private final HostServices hostServices;


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Constructor ----------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    /**
     * Constructor of class HostServicesControllerFactory
     *   with a reference to the JavaFX HostServices.
     * @param hostServices Provides HostServices for an Application
     */
    public HostServicesControllerFactory(HostServices hostServices) {
        this.hostServices = hostServices;
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Factory Call ---------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    @Override
    public Object call(Class<?> type) {
        try {
            for (Constructor<?> c : type.getConstructors()) {

                if (c.getParameterCount() == 1 && c.getParameterTypes()[0] == HostServices.class) {
                    return c.newInstance(hostServices) ;
                }
            }

            return type.getDeclaredConstructor().newInstance();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}