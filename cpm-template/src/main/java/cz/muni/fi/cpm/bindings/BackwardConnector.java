package cz.muni.fi.cpm.bindings;


import cz.muni.fi.cpm.constants.CpmTypeConstants;

public class BackwardConnector extends Connector {

    @Override
    public String getType() {
        return CpmTypeConstants.BACKWARD_CONNECTOR;
    }

}
