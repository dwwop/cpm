package cz.muni.fi.cpm.template.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.cpm.constants.CpmType;
import org.openprovenance.prov.model.QualifiedName;

import java.util.List;

public abstract class Connector {
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

    public Connector() {
    }

    public Connector(QualifiedName id) {
        this.id = id;
    }

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
}
