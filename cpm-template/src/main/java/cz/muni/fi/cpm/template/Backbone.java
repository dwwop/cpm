package cz.muni.fi.cpm.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.DctNamespaceConstants;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.core.json.serialization.deserial.CustomNamespacePrefixDeserializer;
import org.openprovenance.prov.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonPropertyOrder({"prefixes"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Backbone {
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

    private <T extends ToStatements> List<Statement> applyToStatements(List<T> object, ICpmProvFactory cF) {
        return object.stream()
                .map(x -> x.toStatements(cF))
                .flatMap(List::stream)
                .toList();
    }

    private void addKnownNamespaces() {
        namespace.addKnownNamespaces();

        namespace.getPrefixes().put(CpmNamespaceConstants.CPM_PREFIX, CpmNamespaceConstants.CPM_NS);
        namespace.getNamespaces().put(CpmNamespaceConstants.CPM_NS, CpmNamespaceConstants.CPM_PREFIX);
        namespace.getPrefixes().put(DctNamespaceConstants.DCT_PREFIX, DctNamespaceConstants.DCT_NS);
        namespace.getNamespaces().put(DctNamespaceConstants.DCT_NS, DctNamespaceConstants.DCT_PREFIX);
        namespace.setDefaultNamespace(CpmNamespaceConstants.CPM_PREFIX);
    }

    public Document toDocument(ICpmProvFactory cF) {
        List<Statement> statements = new ArrayList<>(mainActivity.toStatements(cF));

        if (backwardConnectors != null) {
            statements.addAll(applyToStatements(backwardConnectors, cF));
        }

        if (forwardConnectors != null) {
            statements.addAll(applyToStatements(forwardConnectors, cF));
        }
        if (receiverAgents != null) {
            statements.addAll(applyToStatements(receiverAgents, cF));
        }
        if (senderAgents != null) {
            statements.addAll(applyToStatements(senderAgents, cF));
        }
        if (identifierEntities != null) {
            statements.addAll(applyToStatements(identifierEntities, cF));
        }

        addKnownNamespaces();

        Namespace bundleNamespace = new Namespace();
        bundleNamespace.setParent(namespace);
        Bundle bBBundle = cF.getProvFactory().newNamedBundle(bundleName, bundleNamespace, statements);

        return cF.getProvFactory().newDocument(namespace, Collections.singletonList(bBBundle));
    }
}
