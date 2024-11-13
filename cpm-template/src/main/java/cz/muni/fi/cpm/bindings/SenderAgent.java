package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.constants.CpmTypeConstants;

public class SenderAgent extends CpmAgent {

    @Override
    public String getType() {
        return CpmTypeConstants.SENDER_AGENT;
    }
}
