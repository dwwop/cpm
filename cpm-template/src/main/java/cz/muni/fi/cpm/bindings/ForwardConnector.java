package cz.muni.fi.cpm.bindings;


import cz.muni.fi.cpm.constants.CpmType;

public class ForwardConnector extends Connector {

    @Override
    public CpmType getType() {
        return CpmType.FORWARD_CONNECTOR;
    }
}
