package cz.muni.fi.cpm.bindings;


import cz.muni.fi.cpm.constants.CpmTypeConstants;

public class ForwardConnector extends Connector {

    @Override
    public String getType() {
        return CpmTypeConstants.FORWARD_CONNECTOR;
    }
}
