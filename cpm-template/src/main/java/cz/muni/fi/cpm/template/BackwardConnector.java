package cz.muni.fi.cpm.template;


import cz.muni.fi.cpm.constants.CpmType;
import org.openprovenance.prov.model.QualifiedName;

public class BackwardConnector extends Connector {

    @Override
    public CpmType getType() {
        return CpmType.BACKWARD_CONNECTOR;
    }

    public BackwardConnector() {
    }

    public BackwardConnector(QualifiedName id) {
        super(id);
    }
}
