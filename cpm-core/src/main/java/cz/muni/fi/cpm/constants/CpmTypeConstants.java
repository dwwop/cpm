package cz.muni.fi.cpm.constants;

import cz.muni.fi.cpm.model.ICpmFactory;
import cz.muni.fi.cpm.vannila.CpmFactory;
import org.openprovenance.prov.model.Type;

public class CpmTypeConstants {
    public static final String BACKWARD_CONNECTOR = "backwardConnector";
    public static final String FORWARD_CONNECTOR = "forwardConnector";
    public static final String IDENTIFIER = "id";
    public static final String MAIN_ACTIVITY = "mainActivity";
    public static final String SENDER_AGENT = "senderAgent";
    public static final String RECEIVER_AGENT = "receiverAgent";

    // TODO lepsi sposob pomocou abstrakcii / cpmfactory use?
    private static final ICpmFactory cF = new CpmFactory();
    public static final Type BACKWARD_CONNECTOR_TYPE = cF.newCpmType(BACKWARD_CONNECTOR);
    public static final Type FORWARD_CONNECTOR_TYPE = cF.newCpmType(FORWARD_CONNECTOR);
    public static final Type IDENTIFIER_TYPE = cF.newCpmType(IDENTIFIER);
    public static final Type MAIN_ACTIVITY_TYPE = cF.newCpmType(MAIN_ACTIVITY);
    public static final Type SENDER_AGENT_TYPE = cF.newCpmType(SENDER_AGENT);
    public static final Type RECEIVER_AGENT_TYPE = cF.newCpmType(RECEIVER_AGENT);
}
