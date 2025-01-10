package cz.muni.fi.cpm.constants;

import org.openprovenance.prov.model.StatementOrBundle;

public class CpmExceptionConstants {
    public static final String ONE_BUNDLE_REQUIRED = "Cpm Document must consist of exactly one statement of type " + StatementOrBundle.Kind.PROV_BUNDLE;
    public static final String NOT_NULL_DOCUMENT = "Document cannot be null";
    public static final String NOT_SUPPORTED = "Not supported";
    public static final String MULTIPLE_NODES = "Multiple nodes found for the given QualifiedName";
    public static final String MULTIPLE_EDGES = "Multiple edges found for the given QualifiedName";
    public static final String MULTIPLE_EDGES_BETWEEN_NODES = "Multiple edges between specified nodes";
    public static final String NOT_NULL_PARTS = "Document parts cannot be null";
}
