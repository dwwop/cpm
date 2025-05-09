package cz.muni.fi.cpm.template.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.openprovenance.prov.model.QualifiedName;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IdentifierEntity {
    @JsonProperty(required = true)
    @JsonPropertyDescription("The identifier of the entity")
    private QualifiedName id;
    @JsonPropertyDescription("The external identifier")
    private String externalId;
    @JsonPropertyDescription("The type of the external identifier")
    private String externalIdType;
    @JsonPropertyDescription("A comment")
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
