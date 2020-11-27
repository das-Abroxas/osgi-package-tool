package life.jlu.osgi.packagetool.model;


import aQute.bnd.header.Attrs;
import aQute.bnd.version.maven.ComparableVersion;


/**
 * This class represents a package from the Export-Package Bundle Header of an OSGi Bundle
 *   with just its most essential informations,
 */
public class ExportPackage extends Package {

    /* ----------------------------------------------------------------------------------------- */
    /* ----- Object Attributes ----------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    private final ComparableVersion version;
    private final Attrs attributes;


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Constructors ---------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    public ExportPackage(String name, String version) {
        super(name);
        this.version    = new ComparableVersion(version);
        this.attributes = new Attrs();
    }

    public ExportPackage(String name, Attrs attributes) {
        super(name);
        this.version  = new ComparableVersion( attributes.getVersion() );
        this.attributes = attributes;
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Getter ---------------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    public ComparableVersion getVersion() {
        return version;
    }

    public Attrs getAttributes() {
        return attributes;
    }


    /* ----------------------------------------------------------------------------------------- */
    /* ----- Object Methods -------------------------------------------------------------------- */
    /* ----------------------------------------------------------------------------------------- */
    @Override
    public String toString() {
        return String.format("%s - %s", getFqn(), version);
    }
}
