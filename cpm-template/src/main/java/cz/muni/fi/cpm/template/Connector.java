package cz.muni.fi.cpm.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.model.Attribute;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Statement;

import java.util.ArrayList;
import java.util.List;

public abstract class Connector implements ToStatements {
    @JsonProperty(required = true)
    private QualifiedName id;
    private String externalId;
    private QualifiedName referencedBundleId;
    private QualifiedName referencedMetaBundleId;
    private Object referencedBundleHashValue;
    private HashAlgorithms hashAlg;
    private String provenanceServiceUri;
    private List<QualifiedName> derivedFrom;
    private ConnectorAttributed attributedTo;

    public ConnectorAttributed getAttributedTo() {
        return attributedTo;
    }

    public void setAttributedTo(ConnectorAttributed attributedTo) {
        this.attributedTo = attributedTo;
    }

    public QualifiedName getId() {
        return id;
    }

    public void setId(QualifiedName id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
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
    public abstract CpmType getType();

    public List<Statement> toStatements(ICpmProvFactory cF) {
        List<Statement> statements = new ArrayList<>();
        List<Attribute> attributes = new ArrayList<>();


        if (externalId != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.EXTERNAL_ID, externalId, cF.getProvFactory().getName().XSD_STRING));
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
            statements.add(cF.getProvFactory().newWasAttributedTo(attributedTo.getId(), id, attributedTo.getAgentId()));
        }

        return statements;
    }

    public Connector() {
    }

    public Connector(QualifiedName id) {
        this.id = id;
    }
}
