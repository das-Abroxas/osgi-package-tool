package life.jlu.osgi.packagetool.application;

import life.jlu.osgi.packagetool.util.DialogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;


/**
 * Static class to load and store user specific preferences for this application.
 *
 * @author Jannis Hochmuth
 */
public class AppPreferences<T> {

    private static final Preferences pref = Preferences.userNodeForPackage(AppPreferences.class);


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Constructor ----------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    private AppPreferences() {}  // Static class, no instances allowed.


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Static Methods -------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    /**
     * Associates the specified value with the specified key in this preference node and writes
     * it to persistent store.
     * @param prefName Key of the preference with which the value is associated
     * @param prefValue Value of the preference with which the key is associated
     * @param <T> Only instances of String, Integer, Long, Float, Double, Boolean
     *            and byte[] are permitted.
     */
    public static <T> void putPreference(String prefName, T prefValue) {

        try {
            if (prefValue instanceof String)
                pref.put(prefName, (String) prefValue);

            else if (prefValue instanceof Integer)
                pref.putInt(prefName, (Integer) prefValue);

            else if (prefValue instanceof Long)
                pref.putLong(prefName, (Long) prefValue);

            else if (prefValue instanceof Float)
                pref.putFloat(prefName, (Float) prefValue);

            else if (prefValue instanceof Double)
                pref.putDouble(prefName, (Double) prefValue);

            else if (prefValue instanceof Boolean)
                pref.putBoolean(prefName, (Boolean) prefValue);

            else if (prefValue instanceof byte[])
                pref.putByteArray(prefName, (byte[]) prefValue);

            pref.flush();

        } catch (NullPointerException npe) {
            DialogUtil.showErrorDialog("Key of preference is null.", npe.getMessage());
        } catch (IllegalArgumentException iae) {
            DialogUtil.showErrorDialog("Value of preference is invalid.", iae.getMessage());
        } catch (IllegalStateException ise) {
            DialogUtil.showErrorDialog("Node for user preferences was removed.", ise.getMessage());
        } catch (BackingStoreException bse) {
            DialogUtil.showErrorDialog("Persistent store not available.", bse.getMessage());
        }
    }

    /**
     * Returns the string value associated with the specified key in this users preference node.
     * @param prefName Key of the preference whose value is returned
     * @return Value of the specified key or empty string if key does not exist.
     */
    public static String getStringPreference(String prefName) {

        try {
            return pref.get(prefName, "");

        } catch (NullPointerException npe) {
            DialogUtil.showErrorDialog("Key of preference is null.", npe.getMessage());
        } catch (IllegalArgumentException iae) {
            DialogUtil.showErrorDialog("Value of preference is invalid.", iae.getMessage());
        } catch (IllegalStateException ise) {
            DialogUtil.showErrorDialog("Node for user preferences was removed.", ise.getMessage());
        }

       return "";
    }

    /**
     * Returns the integer value associated with the specified key in this users preference node.
     * @param prefName Key of the preference whose value is returned
     * @return Value of the specified key or 0 if key does not exist.
     */
    public static Integer getIntegerPreference(String prefName) {

        try {
            return pref.getInt(prefName, 0);

        } catch (NullPointerException npe) {
            DialogUtil.showErrorDialog("Key of preference is null.", npe.getMessage());
        } catch (IllegalArgumentException iae) {
            DialogUtil.showErrorDialog("Value of preference is invalid.", iae.getMessage());
        } catch (IllegalStateException ise) {
            DialogUtil.showErrorDialog("Node for user preferences was removed.", ise.getMessage());
        }

        return 0;
    }

    /**
     * Returns the boolean value associated with the specified key in this users preference node.
     * @param prefName Key of the preference whose value is returned
     * @return Value of the specified key or false if key does not exist.
     */
    public static Boolean getBooleanPreference(String prefName) {

        try {
            return pref.getBoolean(prefName, false);

        } catch (NullPointerException npe) {
            DialogUtil.showErrorDialog("Key of preference is null.", npe.getMessage());
        } catch (IllegalArgumentException iae) {
            DialogUtil.showErrorDialog("Value of preference is invalid.", iae.getMessage());
        } catch (IllegalStateException ise) {
            DialogUtil.showErrorDialog("Node for user preferences was removed.", ise.getMessage());
        }

        return false;
    }

    /**
     * Exports the user specific preferences to the specified file.
     * @param outputFile Exports all of the preferences to the specified preferences XML document
     */
    public static void exportPreferences(File outputFile) {

        try {
            pref.exportNode( new FileOutputStream(outputFile) );

        } catch (IOException ioe) {
            DialogUtil.showErrorDialog("Writing of user preferences failed.", ioe.getMessage());
        } catch (IllegalStateException ise) {
            DialogUtil.showErrorDialog("Node for user preferences was removed.", ise.getMessage());
        } catch (BackingStoreException bse) {
            DialogUtil.showErrorDialog("Persistent store not available.", bse.getMessage());
        }
    }

    /**
     * Imports the user specific preferences from the specified file.
     * @param inputFile Imports all of the preferences represented by the preferences XML document
     */
    public static void importPreferences(File inputFile) {

        try {
            Preferences.importPreferences(new FileInputStream(inputFile));

        } catch (IOException ioe) {
            DialogUtil.showErrorDialog("Reading of user preferences file failed.", ioe.getMessage());
        } catch (InvalidPreferencesFormatException ipfe) {
            DialogUtil.showErrorDialog("Input is no valid preferences XML.", ipfe.getMessage());
        } catch (SecurityException se) {
            DialogUtil.showErrorDialog("Preferences are not allowed.", se.getMessage());
        }
    }
}
