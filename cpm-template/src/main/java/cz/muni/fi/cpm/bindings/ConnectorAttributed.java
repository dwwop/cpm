package cz.muni.fi.cpm.bindings;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openprovenance.prov.model.QualifiedName;

public class ConnectorAttributed {
    private QualifiedName id;
    @JsonProperty(required = true)
    private QualifiedName agentId;

    public QualifiedName getId() {
        return id;
    }

    public void setId(QualifiedName id) {
        this.id = id;
    }

    public QualifiedName getAgentId() {
        return agentId;
    }

    public void setAgentId(QualifiedName agentId) {
        this.agentId = agentId;
    }
}
