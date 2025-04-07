package cz.muni.fi.cpm.template.schema.v1_0;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openprovenance.prov.model.QualifiedName;

public class IdentifierEntity {
    @JsonProperty(required = true)
    private QualifiedName id;
    private String externalId;
    private String externalIdType;
    private String comment;

    public IdentifierEntity(String externalIdType, String externalId, QualifiedName id) {
        this.externalIdType = externalIdType;
        this.externalId = externalId;
        this.id = id;
    }

    public IdentifierEntity() {
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

    public String getExternalIdType() {
        return externalIdType;
    }

    public void setExternalIdType(String externalIdType) {
        this.externalIdType = externalIdType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
