package cz.muni.fi.cpm.bindings;


import cz.muni.fi.cpm.constants.CpmTypeConstants;
import org.openprovenance.prov.model.StatementOrBundle;

import java.util.List;

public class BackwardConnector extends Connector {

    @Override
    public String getConnectorType() {
        return CpmTypeConstants.BACKWARD_CONNECTOR;
    }

}
