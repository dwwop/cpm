package cz.muni.fi.cpm.template.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import cz.muni.fi.cpm.constants.CpmType;
import org.openprovenance.prov.model.QualifiedName;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class CpmAgent {
    @JsonProperty(required = true)
    @JsonPropertyDescription("The identifier of the agent")
    private QualifiedName id;
    private String contactIdPid;

    public QualifiedName getId() {
        return id;
    }

    public void setId(QualifiedName id) {
        this.id = id;
    }

    public Object getContactIdPid() {
        return contactIdPid;
    }

    public CpmAgent(QualifiedName id, String contactIdPid) {
        this.id = id;
        this.contactIdPid = contactIdPid;
    }

    @JsonIgnore
    public abstract CpmType getType();

    public CpmAgent() {
    }

    public CpmAgent(QualifiedName id) {
        this.id = id;
    }

    public void setContactIdPid(String contactIdPid) {
        this.contactIdPid = contactIdPid;
    }
}
