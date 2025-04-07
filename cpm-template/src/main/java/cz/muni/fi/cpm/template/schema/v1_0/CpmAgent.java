package cz.muni.fi.cpm.template.schema.v1_0;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.cpm.constants.CpmType;
import org.openprovenance.prov.model.QualifiedName;

public abstract class CpmAgent {
    @JsonProperty(required = true)
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
    public abstract CpmType getType();

    public CpmAgent() {
    }

    public CpmAgent(QualifiedName id) {
        this.id = id;
    }

    public CpmAgent(QualifiedName id, Object contactIdPid) {
        this.id = id;
        this.contactIdPid = contactIdPid;
    }
}
