package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.constants.CpmType;

public class ReceiverAgent extends CpmAgent {
    @Override
    public CpmType getType() {
        return CpmType.RECEIVER_AGENT;
    }
}
