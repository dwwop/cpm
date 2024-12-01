package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.constants.CpmType;

public class SenderAgent extends CpmAgent {

    @Override
    public CpmType getType() {
        return CpmType.SENDER_AGENT;
    }
}
