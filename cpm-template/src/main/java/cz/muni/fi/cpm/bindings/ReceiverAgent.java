package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.constants.CpmTypeConstants;

public class ReceiverAgent extends CpmAgent {
    @Override
    public String getType() {
        return CpmTypeConstants.RECEIVER_AGENT;
    }
}
