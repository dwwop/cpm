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

    public static final Map<CpmType, Kind> CPM_TYPE_TO_KIND = Map.of(
            BACKWARD_CONNECTOR, Kind.PROV_ENTITY,
            FORWARD_CONNECTOR, Kind.PROV_ENTITY,
            IDENTIFIER, Kind.PROV_ENTITY,
            MAIN_ACTIVITY, Kind.PROV_ACTIVITY,
            SENDER_AGENT, Kind.PROV_AGENT,
            RECEIVER_AGENT, Kind.PROV_AGENT
    );

    private final String value;

    CpmType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
