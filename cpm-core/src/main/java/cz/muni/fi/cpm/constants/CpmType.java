package cz.muni.fi.cpm.constants;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum CpmType {
    BACKWARD_CONNECTOR("backwardConnector"),
    FORWARD_CONNECTOR("forwardConnector"),
    IDENTIFIER("id"),
    MAIN_ACTIVITY("mainActivity"),
    SENDER_AGENT("senderAgent"),
    RECEIVER_AGENT("receiverAgent");

    public static final Set<String> STRING_VALUES = Arrays.stream(CpmType.values())
            .map(CpmType::toString)
            .collect(Collectors.toSet());

    private final String value;

    CpmType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
