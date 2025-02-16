package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.constants.CpmType;
import org.openprovenance.prov.model.QualifiedName;

public class ReceiverAgent extends CpmAgent {
    public ReceiverAgent() {
    }

    public ReceiverAgent(QualifiedName id) {
        super(id);
    }

    @Override
    public CpmType getType() {
        return CpmType.RECEIVER_AGENT;
    }

    public ReceiverAgent(QualifiedName id, Object contactIdPid) {
        super(id, contactIdPid);
    }
}
