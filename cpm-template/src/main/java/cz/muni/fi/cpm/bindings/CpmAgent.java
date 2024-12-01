package cz.muni.fi.cpm.bindings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.model.ICpmFactory;
import org.openprovenance.prov.model.Attribute;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CpmAgent implements ToStatements{

    private QualifiedName id;
    private Object contactIdPid;

    public QualifiedName getId() {
        return id;
    }

    public void setId(QualifiedName id) {
        this.id = id;
    }

    public Object getContactIdPid() {
        return contactIdPid;
    }

    public void setContactIdPid(Object contactIdPid) {
        this.contactIdPid = contactIdPid;
    }

    @JsonIgnore
    public abstract String getType();

    public List<Statement> toStatements(ICpmFactory cF) {
        List<Attribute> attributes = new ArrayList<>();

        if (contactIdPid != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.CONTACT_ID_PID, contactIdPid, cF.getProvFactory().getName().XSD_STRING));
        }

        return Collections.singletonList(cF.newCpmAgent(id, getType(), attributes));
    }
}
