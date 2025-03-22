package cz.muni.fi.cpm.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.model.Attribute;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IdentifierEntity implements ToStatements {
    @JsonProperty(required = true)
    private QualifiedName id;
    private QualifiedName externalId;
    private String externalIdType;
    private String comment;

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

    public List<Statement> toStatements(ICpmProvFactory cF) {
        List<Attribute> attributes = new ArrayList<>();

        if (externalId != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.EXTERNAL_ID, externalId));
        }

        if (externalIdType != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.EXTERNAL_ID_TYPE, externalIdType, cF.getProvFactory().getName().XSD_STRING));
        }

        if (comment != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.COMMENT, comment, cF.getProvFactory().getName().XSD_STRING));
        }

        return Collections.singletonList(cF.newCpmEntity(id, CpmType.IDENTIFIER, attributes));
    }

    public IdentifierEntity() {
    }

    public IdentifierEntity(String externalIdType, QualifiedName externalId, QualifiedName id) {
        this.externalIdType = externalIdType;
        this.externalId = externalId;
        this.id = id;
    }
}
