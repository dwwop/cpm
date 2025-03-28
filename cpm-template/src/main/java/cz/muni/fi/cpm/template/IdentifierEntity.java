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
    private String externalId;
    private String externalIdType;
    private String comment;

    public QualifiedName getId() {
        return id;
    }

    public void setId(QualifiedName id) {
        this.id = id;
    }

    public IdentifierEntity(String externalIdType, String externalId, QualifiedName id) {
        this.externalIdType = externalIdType;
        this.externalId = externalId;
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
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

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public IdentifierEntity() {
    }

    public List<Statement> toStatements(ICpmProvFactory cF) {
        List<Attribute> attributes = new ArrayList<>();

        if (externalId != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.EXTERNAL_ID, externalId, cF.getProvFactory().getName().XSD_STRING));
        }

        if (externalIdType != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.EXTERNAL_ID_TYPE, externalIdType, cF.getProvFactory().getName().XSD_STRING));
        }

        if (comment != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.COMMENT, comment, cF.getProvFactory().getName().XSD_STRING));
        }

        return Collections.singletonList(cF.newCpmEntity(id, CpmType.IDENTIFIER, attributes));
    }
}
