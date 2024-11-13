package cz.muni.fi.cpm.bindings;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.cpm.CpmFactory;
import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.DctNamespaceConstants;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Namespace;
import org.openprovenance.prov.model.StatementOrBundle;

import java.util.ArrayList;
import java.util.List;

public class Backbone {
    private Namespace namespace;
    @JsonProperty(required = true)
    private MainActivity mainActivity;
    private List<BackwardConnector> backwardConnectors;
    private List<ForwardConnector> forwardConnectors;
    private List<SenderAgent> senderAgents;
    private List<ReceiverAgent> receiverAgents;
    private List<IdentifierEntity> identifierEntities;

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

    public List<BackwardConnector> getBackwardConnectors() {
        return backwardConnectors;
    }

    public void setBackwardConnectors(List<BackwardConnector> backwardConnectors) {
        this.backwardConnectors = backwardConnectors;
    }

    public List<ForwardConnector> getForwardConnectors() {
        return forwardConnectors;
    }

    public void setForwardConnectors(List<ForwardConnector> forwardConnectors) {
        this.forwardConnectors = forwardConnectors;
    }

    public List<SenderAgent> getSenderAgents() {
        return senderAgents;
    }

    public void setSenderAgents(List<SenderAgent> senderAgents) {
        this.senderAgents = senderAgents;
    }

    public List<ReceiverAgent> getReceiverAgents() {
        return receiverAgents;
    }

    public void setReceiverAgents(List<ReceiverAgent> receiverAgents) {
        this.receiverAgents = receiverAgents;
    }

    public List<IdentifierEntity> getIdentifierEntities() {
        return identifierEntities;
    }

    public void setIdentifierEntities(List<IdentifierEntity> identifierEntities) {
        this.identifierEntities = identifierEntities;
    }

    private <T extends ToStatements> List<StatementOrBundle> applyToStatements(List<T> object, CpmFactory cF) {
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
    }

    public Document toDocument(CpmFactory cF) {
        List<StatementOrBundle> statements = new ArrayList<>(mainActivity.toStatements(cF));

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
        
        return cF.getProvFactory().newDocument(namespace, statements);
    }
}
