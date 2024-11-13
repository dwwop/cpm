package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.CpmFactory;
import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import org.openprovenance.prov.model.Attribute;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.StatementOrBundle;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public abstract class Connector implements ToStatements {

    private QualifiedName id;
    private QualifiedName externalId;
    private QualifiedName referencedBundleId;
    private QualifiedName referencedMetaBundleId;
    private Object referencedBundleHashValue;
    private HashAlgorithms hashAlg;
    private URI provenanceServiceUri;
    private List<QualifiedName> derivedFrom;
    private QualifiedName attributedTo;

    public QualifiedName getAttributedTo() {
        return attributedTo;
    }

    public void setAttributedTo(QualifiedName attributedTo) {
        this.attributedTo = attributedTo;
    }

    public QualifiedName getId() {
        return id;
    }

    public void setId(QualifiedName id) {
        this.id = id;
    }

    public QualifiedName getExternalId() {
        return externalId;
    }

    public void setExternalId(QualifiedName externalId) {
        this.externalId = externalId;
    }

    public QualifiedName getReferencedBundleId() {
        return referencedBundleId;
    }

    public void setReferencedBundleId(QualifiedName referencedBundleId) {
        this.referencedBundleId = referencedBundleId;
    }

    public QualifiedName getReferencedMetaBundleId() {
        return referencedMetaBundleId;
    }

    public void setReferencedMetaBundleId(QualifiedName referencedMetaBundleId) {
        this.referencedMetaBundleId = referencedMetaBundleId;
    }

    public Object getReferencedBundleHashValue() {
        return referencedBundleHashValue;
    }

    public void setReferencedBundleHashValue(Object referencedBundleHashValue) {
        this.referencedBundleHashValue = referencedBundleHashValue;
    }

    public HashAlgorithms getHashAlg() {
        return hashAlg;
    }

    public void setHashAlg(HashAlgorithms hashAlg) {
        this.hashAlg = hashAlg;
    }

    public URI getProvenanceServiceUri() {
        return provenanceServiceUri;
    }

    public void setProvenanceServiceUri(URI provenanceServiceUri) {
        this.provenanceServiceUri = provenanceServiceUri;
    }

    public List<QualifiedName> getDerivedFrom() {
        return derivedFrom;
    }

    public void setDerivedFrom(List<QualifiedName> derivedFrom) {
        this.derivedFrom = derivedFrom;
    }

    public abstract String getConnectorType();

    public List<StatementOrBundle> toStatements(CpmFactory cF) {
        List<StatementOrBundle> statements = new ArrayList<>();
        List<Attribute> attributes = new ArrayList<>();

        attributes.add(cF.newCpmType(getConnectorType()));

        if (externalId != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.EXTERNAL_ID, externalId));
        }

        if (referencedBundleId != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.REFERENCED_BUNDLE_ID, referencedBundleId));
        }

        if (referencedMetaBundleId != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.REFERENCED_META_BUNDLE_ID, referencedMetaBundleId));
        }

        if (referencedBundleHashValue != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.REFERENCED_BUNDLE_HASH_VALUE, referencedBundleHashValue));
        }

        if (hashAlg != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.HASH_ALG, hashAlg.toString()));
        }

        if (provenanceServiceUri != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.PROVENANCE_SERVICE_URI, provenanceServiceUri));
        }

        statements.add(cF.getProvFactory().newEntity(id, attributes));

        if (derivedFrom != null) {
            for (QualifiedName derivedFromElement : derivedFrom) {
                statements.add(cF.getProvFactory().newWasDerivedFrom(id, derivedFromElement));
            }
        }

        if (attributedTo != null) {
            // TODO attributedTo IDs
            statements.add(cF.getProvFactory().newWasAttributedTo(null, id, attributedTo));
        }

        return statements;
    }
}
