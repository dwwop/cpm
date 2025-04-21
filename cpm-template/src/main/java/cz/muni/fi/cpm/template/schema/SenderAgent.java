package cz.muni.fi.cpm.template.schema;

import cz.muni.fi.cpm.constants.CpmType;
import org.openprovenance.prov.model.QualifiedName;

public class SenderAgent extends CpmAgent {

    public SenderAgent() {
    }

    public SenderAgent(QualifiedName id) {
        super(id);
    }

    @Override
    public CpmType getType() {
        return CpmType.SENDER_AGENT;
    }

    public SenderAgent(QualifiedName id, String contactIdPid) {
        super(id, contactIdPid);
    }
}
