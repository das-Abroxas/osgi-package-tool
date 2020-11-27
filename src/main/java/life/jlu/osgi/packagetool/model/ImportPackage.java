package life.jlu.osgi.packagetool.model;


import aQute.bnd.header.Attrs;
import aQute.bnd.version.maven.ComparableVersion;


/**
 * This class represents a package from the Import-Package Bundle Header of an OSGi Bundle
 *   with just its most essential informations,
 */
public class ImportPackage extends Package {

    /* ----------------------------------------------------------------------------------------- */
    /* ----- Object Attributes ----------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    private final ComparableVersion minVersion;
    private final ComparableVersion maxVersion;
    private boolean minIncluded;
    private boolean maxIncluded;

    private boolean anyExport = false;
    private boolean optional = false;
    private boolean dynamic = false;

    private boolean resolved = false;

    private Attrs attributes = new Attrs();
    private ExportPackage resolvePkg;


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Constructors ---------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    public ImportPackage(String name, Attrs attrs) {
        super(name);
        attributes = attrs;

        if (attrs.getVersion() == null) {
            this.minVersion  = new ComparableVersion("0.0.0");
            this.maxVersion  = new ComparableVersion("0.0.0");
            this.minIncluded = true;
            this.maxIncluded = true;
            this.anyExport   = true;

        } else {
            String version = attrs.getVersion();
            this.minVersion  = new ComparableVersion(version.split(",")[0]);
            this.maxVersion  = new ComparableVersion(version.split(",")[1]);
            this.minIncluded = version.startsWith("[");
            this.maxIncluded = version.endsWith("]");
        }

        this.optional = "optional".equals( attrs.get("resolution:") );
        this.dynamic  = "dynamic".equals( attrs.get("resolution:") );
    }

    public ImportPackage(String name, String minVersion, String maxVersion,
                         boolean minIncluded, boolean maxIncluded) {
        super(name);
        this.minVersion  = new ComparableVersion(minVersion);
        this.maxVersion  = new ComparableVersion(maxVersion);
        this.minIncluded = minIncluded;
        this.maxIncluded = maxIncluded;

        this.anyExport = minVersion.equals("0.0.0") && maxVersion.equals("0.0.0");
    }

    public ImportPackage(String name, String minVersion, String maxVersion,
                         boolean minIncluded, boolean maxIncluded, boolean optional) {
        super(name);
        this.minVersion  = new ComparableVersion(minVersion);
        this.maxVersion  = new ComparableVersion(maxVersion);
        this.minIncluded = minIncluded;
        this.maxIncluded = maxIncluded;
        this.optional    = optional;

        this.anyExport = minVersion.equals("0.0.0") && maxVersion.equals("0.0.0");
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Getter ---------------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    public ComparableVersion getMinVersion() {
        return minVersion;
    }

    public ComparableVersion getMaxVersion() {
        return maxVersion;
    }

    public boolean isMinIncluded() {
        return minIncluded;
    }

    public boolean isMaxIncluded() {
        return maxIncluded;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isDynamic() { return dynamic; }

    public boolean isResolved() {
        return resolved;
    }

    public Attrs getAttributes() {
        return attributes;
    }

    public ExportPackage getResolvePkg() {
        return resolvePkg;
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Object Methods -------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    /**
     * Resolve this Package-Import against all Package-Exports of an OSGi bundle.
     * @param bundle OSGi bundle with package exports
     * @return True if resolve succeeds; false else
     */
    public boolean resolve(Bundle bundle) {

        for (ExportPackage ep : bundle.getExports()) {
            if (resolve(ep))
                return true;
        }

        return false;
    }

    /**
     * Resolve this Package-Import against a single Package-Export.
     * @param ePkg Exported package from an OSGi bundle
     * @return True if resolve succeeds; false else
     */
    public boolean resolve(ExportPackage ePkg) {

        boolean match = getFqn().equals( ePkg.getFqn() );

        if (anyExport && match) {
            resolved(ePkg);
            return true;

        } else if (match) {
            int lower = minVersion.compareTo(ePkg.getVersion());
            int upper = maxVersion.compareTo(ePkg.getVersion());

            if ((minIncluded && lower <= 0 && maxIncluded && upper >= 0) ||
                (minIncluded && lower <= 0 && !maxIncluded && upper > 0) ||
                (!minIncluded && lower < 0 && maxIncluded && upper >= 0) ||
                (!minIncluded && lower < 0 && !maxIncluded && upper > 0)) {

                resolved(ePkg);
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("%s - %s,%s", getFqn(), minVersion.toString(), maxVersion.toString());
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Helper ---------------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    /**
     * Marks this package as resolved and saves a reference to the exported package
     *   this package successfully resolved against
     * @param ePkg Exported package this package successfully resolved against
     */
    private void resolved(ExportPackage ePkg) {
        resolvePkg = ePkg;
        this.resolved = true;
    }
}
