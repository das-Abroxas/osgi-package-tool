package life.jlu.osgi.packagetool.model;

import java.util.ArrayList;
import java.util.List;


/**
 * An instance of this class represents an OSGi bundle with its most important attributes.
 *
 * @author Jannis Hochmuth
 */
public class Bundle {

    /* ----------------------------------------------------------------------------------------- */
    /* ----- Object attributes ----------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    private String bundleName;
    private String bundleSymbolicName;
    private String bundleVersion = "0.0.0";
    private String bundleURL;

    private List<ExportPackage> exports = new ArrayList<>();
    private List<ImportPackage> imports = new ArrayList<>();

    private List<ImportPackage> dynamicImports = new ArrayList<>();


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Constructors ---------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    public Bundle(String bundleSymbolicName) {
        this.bundleSymbolicName = bundleSymbolicName;
    }

    public Bundle(String bundleSymbolicName, String bundleVersion) {
        this.bundleSymbolicName = bundleSymbolicName;
        this.bundleVersion = bundleVersion;
    }

    public Bundle(String bundleName, String bundleSymbolicName, String bundleVersion) {
        this.bundleName = bundleName;
        this.bundleSymbolicName = bundleSymbolicName;
        this.bundleVersion = bundleVersion;
    }

    public Bundle(String bundleName, String bundleSymbolicName, String bundleVersion,
                  String bundleURL) {
        this.bundleName = bundleName;
        this.bundleSymbolicName = bundleSymbolicName;
        this.bundleVersion = bundleVersion;
        this.bundleURL = bundleURL;
    }

    public Bundle(String bundleName, String bundleSymbolicName, String bundleVersion,
                  String bundleURL,  List<ExportPackage> exports) {
        this.bundleName = bundleName;
        this.bundleSymbolicName = bundleSymbolicName;
        this.bundleVersion = bundleVersion;
        this.bundleURL = bundleURL;
        this.exports = exports;
    }

    public Bundle(String bundleName, String bundleSymbolicName, String bundleVersion,
                  String bundleURL, List<ExportPackage> exports, List<ImportPackage> imports) {
        this.bundleName = bundleName;
        this.bundleSymbolicName = bundleSymbolicName;
        this.bundleVersion = bundleVersion;
        this.bundleURL = bundleURL;
        this.exports = exports;
        this.imports = imports;
    }

    public Bundle(String bundleName, String bundleSymbolicName, String bundleVersion,
                  String bundleURL, List<ExportPackage> exports, List<ImportPackage> imports,
                  List<ImportPackage> dynamicImports) {
        this.bundleName         = bundleName;
        this.bundleSymbolicName = bundleSymbolicName;
        this.bundleVersion      = bundleVersion;
        this.bundleURL          = bundleURL;
        this.exports            = exports;
        this.imports            = imports;
        this.dynamicImports     = dynamicImports;
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Getter & Setter ------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    public String getBundleName()                { return bundleName; }
    public void setBundleName(String bundleName) { this.bundleName = bundleName; }

    public String getBundleSymbolicName()                        { return bundleSymbolicName; }
    public void setBundleSymbolicName(String bundleSymbolicName) { this.bundleSymbolicName = bundleSymbolicName; }

    public String getBundleVersion()                   { return bundleVersion; }
    public void setBundleVersion(String bundleVersion) { this.bundleVersion = bundleVersion; }

    public String getBundleURL()               { return bundleURL; }
    public void setBundleURL(String bundleURL) { this.bundleURL = bundleURL; }

    public List<ExportPackage> getExports()             { return exports; }
    public void setExports(List<ExportPackage> exports) { this.exports = exports; }

    public List<ImportPackage> getImports()             { return imports; }
    public void setImports(List<ImportPackage> imports) { this.imports = imports; }

    public List<ImportPackage> getDynamicImports()                    { return dynamicImports; }
    public void setDynamicImports(List<ImportPackage> dynamicImports) { this.dynamicImports = dynamicImports; }
}
