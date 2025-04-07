package cz.muni.fi.cpm.template.schema.v1_0;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import cz.muni.fi.cpm.template.constants.VersionConstants;
import cz.muni.fi.cpm.template.schema.ITraversalInformation;
import org.openprovenance.prov.core.json.serialization.deserial.CustomNamespacePrefixDeserializer;
import org.openprovenance.prov.model.Namespace;
import org.openprovenance.prov.model.QualifiedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonPropertyOrder({VersionConstants.VERSION, "prefixes"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TraversalInformation implements ITraversalInformation {
    private String version;
    @JsonIgnore
    @JsonDeserialize(using = CustomNamespacePrefixDeserializer.class)
    private Namespace namespace = new Namespace();
    @JsonProperty(required = true)
    private MainActivity mainActivity;
    @JsonProperty(required = true)
    private QualifiedName bundleName;
    private List<BackwardConnector> backwardConnectors;
    private List<ForwardConnector> forwardConnectors;
    private List<SenderAgent> senderAgents;
    private List<ReceiverAgent> receiverAgents;
    private List<IdentifierEntity> identifierEntities;

    @JsonDeserialize(using = CustomNamespacePrefixDeserializer.class)
    @JsonProperty(required = true)
    public Map<String, String> getPrefixes() {
        return namespace.getPrefixes();
    }

    public void setPrefixes(Map<String, String> prefixes) {
        for (Map.Entry<String, String> entry : prefixes.entrySet()) {
            this.namespace.register(entry.getKey(), entry.getValue());
        }
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public QualifiedName getBundleName() {
        return bundleName;
    }

    public void setBundleName(QualifiedName bundleName) {
        this.bundleName = bundleName;
    }

    public List<BackwardConnector> getBackwardConnectors() {
        if (backwardConnectors == null) {
            backwardConnectors = new ArrayList<>();
        }
        return backwardConnectors;
    }

    public void setBackwardConnectors(List<BackwardConnector> backwardConnectors) {
        this.backwardConnectors = backwardConnectors;
    }

    public List<ForwardConnector> getForwardConnectors() {
        if (forwardConnectors == null) {
            forwardConnectors = new ArrayList<>();
        }
        return forwardConnectors;
    }

    public void setForwardConnectors(List<ForwardConnector> forwardConnectors) {
        this.forwardConnectors = forwardConnectors;
    }

    public List<SenderAgent> getSenderAgents() {
        if (senderAgents == null) {
            senderAgents = new ArrayList<>();
        }
        return senderAgents;
    }

    public void setSenderAgents(List<SenderAgent> senderAgents) {
        this.senderAgents = senderAgents;
    }

    public List<ReceiverAgent> getReceiverAgents() {
        if (receiverAgents == null) {
            receiverAgents = new ArrayList<>();
        }
        return receiverAgents;
    }

    public void setReceiverAgents(List<ReceiverAgent> receiverAgents) {
        this.receiverAgents = receiverAgents;
    }

    public List<IdentifierEntity> getIdentifierEntities() {
        if (identifierEntities == null) {
            identifierEntities = new ArrayList<>();
        }
        return identifierEntities;
    }

    public void setIdentifierEntities(List<IdentifierEntity> identifierEntities) {
        this.identifierEntities = identifierEntities;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
