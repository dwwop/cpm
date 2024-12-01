package cz.muni.fi.cpm.bindings;


import cz.muni.fi.cpm.constants.CpmType;

public class BackwardConnector extends Connector {

    @Override
    public CpmType getType() {
        return CpmType.BACKWARD_CONNECTOR;
    }

}
