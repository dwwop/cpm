package cz.muni.fi.cpm.constants;

public enum CpmType {
    BACKWARD_CONNECTOR("backwardConnector"),
    FORWARD_CONNECTOR("forwardConnector"),
    IDENTIFIER("id"),
    MAIN_ACTIVITY("mainActivity"),
    SENDER_AGENT("senderAgent"),
    RECEIVER_AGENT("receiverAgent");

    private final String value;

    CpmType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
