package cz.muni.fi.cpm.bindings;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.cpm.CpmFactory;
import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.constants.CpmTypeConstants;
import org.openprovenance.prov.model.Attribute;
import org.openprovenance.prov.model.StatementOrBundle;
import org.openprovenance.prov.vanilla.QualifiedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IdentifierEntity implements ToStatements{
    private QualifiedName id;
    @JsonProperty(required = true)
    private QualifiedName externalId;

    @JsonProperty(required = true)
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

    public List<StatementOrBundle> toStatements(CpmFactory cF) {
        List<Attribute> attributes = new ArrayList<>();

        attributes.add(cF.newCpmType(CpmTypeConstants.IDENTIFIER));

        if (externalId != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.EXTERNAL_ID, externalId));
        }

        if (externalIdType != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.EXTERNAL_ID_TYPE, externalIdType));
        }

        if (comment != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.COMMENT, comment));
        }

        return Collections.singletonList(cF.getProvFactory().newEntity(id, attributes));
    }
}
