package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.CpmFactory;
import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import org.openprovenance.prov.model.Attribute;
import org.openprovenance.prov.model.StatementOrBundle;
import org.openprovenance.prov.vanilla.QualifiedName;

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

    public abstract String getType();

    public List<StatementOrBundle> toStatements(CpmFactory cF) {
        List<Attribute> attributes = new ArrayList<>();

        attributes.add(cF.newCpmType(getType()));

        if (contactIdPid != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.CONTACT_ID_PID, contactIdPid));
        }

        return Collections.singletonList(cF.getProvFactory().newAgent(id, attributes));
    }
}
