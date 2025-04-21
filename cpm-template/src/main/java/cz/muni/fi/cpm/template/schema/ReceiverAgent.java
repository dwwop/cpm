package cz.muni.fi.cpm.template.schema;

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

    public ReceiverAgent(QualifiedName id, String contactIdPid) {
        super(id, contactIdPid);
    }
}
