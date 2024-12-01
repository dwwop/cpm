package cz.muni.fi.cpm.bindings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.model.ICpmFactory;
import org.openprovenance.prov.model.Attribute;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Statement;

import java.util.ArrayList;
import java.util.List;

public abstract class Connector implements ToStatements {

    private QualifiedName id;
    private QualifiedName externalId;
    private QualifiedName referencedBundleId;
    private QualifiedName referencedMetaBundleId;
    private Object referencedBundleHashValue;
    private HashAlgorithms hashAlg;
    private String provenanceServiceUri;
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

    public String getProvenanceServiceUri() {
        return provenanceServiceUri;
    }

    public void setProvenanceServiceUri(String provenanceServiceUri) {
        this.provenanceServiceUri = provenanceServiceUri;
    }

    public List<QualifiedName> getDerivedFrom() {
        return derivedFrom;
    }

    public void setDerivedFrom(List<QualifiedName> derivedFrom) {
        this.derivedFrom = derivedFrom;
    }

    @JsonIgnore
    public abstract String getType();

    public List<Statement> toStatements(ICpmFactory cF) {
        List<Statement> statements = new ArrayList<>();
        List<Attribute> attributes = new ArrayList<>();


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
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.REFERENCED_BUNDLE_HASH_VALUE, referencedBundleHashValue, cF.getProvFactory().getName().XSD_BYTE));
        }

        if (hashAlg != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.HASH_ALG, hashAlg.toString(), cF.getProvFactory().getName().XSD_STRING));
        }

        if (provenanceServiceUri != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.PROVENANCE_SERVICE_URI, provenanceServiceUri, cF.getProvFactory().getName().XSD_ANY_URI));
        }

        statements.add(cF.newCpmEntity(id, getType(), attributes));

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
