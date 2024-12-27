package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.constants.CpmExceptionConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.exception.NoSpecificKind;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.model.extension.QualifiedAlternateOf;
import org.openprovenance.prov.model.extension.QualifiedHadMember;
import org.openprovenance.prov.model.extension.QualifiedSpecializationOf;

import java.util.*;

public class CpmDocument implements StatementAction {
    private static final ProvUtilities u = new ProvUtilities();
    private final ProvFactory pF;
    private final ICpmFactory cF;

    private final Map<QualifiedName, Map<StatementOrBundle.Kind, List<IEdge>>> effectEdges = new HashMap<>();
    private final Map<QualifiedName, Map<StatementOrBundle.Kind, List<IEdge>>> causeEdges = new HashMap<>();

    private final Map<QualifiedName, Map<StatementOrBundle.Kind, INode>> nodes = new HashMap<>();
    private final List<IEdge> edges = new ArrayList<>();
    private final List<INode> backbone = new ArrayList<>();
    private Namespace namespaces;
    private Bundle bundle;

    public CpmDocument(ProvFactory pF, ICpmFactory cF) {
        this.pF = pF;
        this.cF = cF;
    }

    public CpmDocument(final Document document, ProvFactory provFactory, ICpmFactory cpmFactory) {
        pF = provFactory;
        cF = cpmFactory;
        if (document == null) {
            throw new IllegalArgumentException(CpmExceptionConstants.NOT_NULL_DOCUMENT);
        }

        if (document.getStatementOrBundle().size() != 1 ||
                !(document.getStatementOrBundle().getFirst() instanceof Bundle docBundle)) {
            throw new IllegalArgumentException(CpmExceptionConstants.ONE_BUNDLE_REQUIRED);
        }

        this.bundle = pF.newNamedBundle(docBundle.getId(), pF.newNamespace(docBundle.getNamespace()), null);
        this.namespaces = pF.newNamespace(document.getNamespace());

        u.forAllStatement(docBundle.getStatement(), this);
    }

    public Document toDocument() {
        Document document = pF.newDocument();

        List<Statement> statements = new ArrayList<>();
        statements.addAll(nodes.entrySet().stream()
                .flatMap(entry -> entry.getValue().values().stream())
                .map(INode::getElement)
                .toList());
        statements.addAll(edges.stream().map(IEdge::getRelation).distinct().toList());

        Bundle newBundle = pF.newNamedBundle(bundle.getId(), statements);
        Namespace bundleNs = pF.newNamespace();
        newBundle.setNamespace(bundleNs);

        document.getStatementOrBundle().add(newBundle);

        Namespace ns = Namespace.gatherNamespaces(newBundle);
        ns.extendWith(namespaces);
        document.setNamespace(ns);
        bundleNs.setParent(ns);

        return document;
    }

    private void addEdge(Relation relation) {
        addEdge(cF.newEdge(relation), u.getEffect(relation), u.getCause(relation));
    }


    private void addEdge(IEdge edge, QualifiedName effect, QualifiedName cause) {
        try {
            StatementOrBundle.Kind nodeKind = ProvUtilities2.getEffectKind(edge.getRelation());

            if (nodes.containsKey(effect) && nodes.get(effect).containsKey(nodeKind)) {
                Map<StatementOrBundle.Kind, INode> kindToNodeMap = nodes.get(effect);
                INode node = kindToNodeMap.get(nodeKind);

                node.getEffectEdges().add(edge);
                edge.setEffect(node);
            } else {
                effectEdges.computeIfAbsent(effect, _ -> new HashMap<>())
                        .computeIfAbsent(nodeKind, _ -> new ArrayList<>())
                        .add(edge);
            }
        } catch (NoSpecificKind ignored) {
            // TODO how to handle
        }

        try {
            StatementOrBundle.Kind nodeKind = ProvUtilities2.getCauseKind(edge.getRelation());

            if (nodes.containsKey(cause) && nodes.get(cause).containsKey(nodeKind)) {
                Map<StatementOrBundle.Kind, INode> kindToNodeMap = nodes.get(cause);
                INode node = kindToNodeMap.get(nodeKind);

                node.getCauseEdges().add(edge);
                edge.setCause(node);
            } else {
                causeEdges.computeIfAbsent(cause, _ -> new HashMap<>())
                        .computeIfAbsent(nodeKind, _ -> new ArrayList<>())
                        .add(edge);
            }
        } catch (NoSpecificKind ignored) {
            // TODO how to handle
        }

        edges.add(edge);
    }

    private void addNode(Element element) {
        INode newNode = cF.newNode(element);
        QualifiedName id = element.getId();
        StatementOrBundle.Kind kind = element.getKind();

        if (nodes.containsKey(id)) {
            INode existingNode = nodes.get(id).get(kind);
            if (existingNode != null) {
                ProvUtilities2.mergeAttributes(existingNode.getElement(), newNode.getElement());
                return;
            }
        }

        nodes.computeIfAbsent(id, _ -> new HashMap<>())
                .putIfAbsent(kind, newNode);

        if (effectEdges.containsKey(id) && effectEdges.get(id).containsKey(kind)) {
            List<IEdge> edgesToUpdate = effectEdges.get(id).get(kind);
            for (IEdge edge : edgesToUpdate) {
                edge.setEffect(newNode);
                newNode.getEffectEdges().add(edge);
            }
            effectEdges.get(id).remove(kind);
            if (effectEdges.get(id).isEmpty()) {
                effectEdges.remove(id);
            }
        }

        if (causeEdges.containsKey(id) && causeEdges.get(id).containsKey(kind)) {
            List<IEdge> edgesToUpdate = causeEdges.get(id).get(kind);
            for (IEdge edge : edgesToUpdate) {
                edge.setCause(newNode);
                newNode.getCauseEdges().add(edge);
            }
            causeEdges.get(id).remove(kind);
            if (causeEdges.get(id).isEmpty()) {
                causeEdges.remove(id);
            }
        }

        if (CpmUtilities.isCpmElement(element)) {
            backbone.add(newNode);
        }
    }

    @Override
    public void doAction(Activity activity) {
        addNode(activity);
    }

    @Override
    public void doAction(Used used) {
        addEdge(used);
    }

    @Override
    public void doAction(WasStartedBy wasStartedBy) {
        addEdge(wasStartedBy);
    }

    @Override
    public void doAction(Agent agent) {
        addNode(agent);
    }

    @Override
    public void doAction(AlternateOf alternateOf) {
        addEdge(alternateOf);
    }

    @Override
    public void doAction(WasAssociatedWith wasAssociatedWith) {
        addEdge(wasAssociatedWith);
    }

    @Override
    public void doAction(WasAttributedTo wasAttributedTo) {
        addEdge(wasAttributedTo);
    }

    @Override
    public void doAction(WasInfluencedBy wasInfluencedBy) {
        addEdge(wasInfluencedBy);
    }

    @Override
    public void doAction(ActedOnBehalfOf actedOnBehalfOf) {
        addEdge(actedOnBehalfOf);
    }

    @Override
    public void doAction(WasDerivedFrom wasDerivedFrom) {
        addEdge(wasDerivedFrom);
    }

    @Override
    public void doAction(DictionaryMembership dictionaryMembership) {
        throw new UnsupportedOperationException(CpmExceptionConstants.NOT_SUPPORTED);
    }

    @Override
    public void doAction(DerivedByRemovalFrom derivedByRemovalFrom) {
        throw new UnsupportedOperationException(CpmExceptionConstants.NOT_SUPPORTED);
    }

    @Override
    public void doAction(WasEndedBy wasEndedBy) {
        addEdge(wasEndedBy);
    }

    @Override
    public void doAction(Entity entity) {
        addNode(entity);
    }

    @Override
    public void doAction(WasGeneratedBy wasGeneratedBy) {
        addEdge(wasGeneratedBy);
    }

    @Override
    public void doAction(WasInvalidatedBy wasInvalidatedBy) {
        addEdge(wasInvalidatedBy);
    }

    @Override
    public void doAction(HadMember hadMember) {
        for (QualifiedName member : hadMember.getEntity()) {
            addEdge(cF.newEdge(hadMember), hadMember.getCollection(), member);
        }
    }

    @Override
    public void doAction(MentionOf mentionOf) {
        addEdge(mentionOf);
    }

    @Override
    public void doAction(SpecializationOf specializationOf) {
        addEdge(specializationOf);
    }

    @Override
    public void doAction(QualifiedSpecializationOf qualifiedSpecializationOf) {
        addEdge(qualifiedSpecializationOf);
    }

    @Override
    public void doAction(QualifiedAlternateOf qualifiedAlternateOf) {
        addEdge(qualifiedAlternateOf);
    }

    @Override
    public void doAction(QualifiedHadMember qualifiedHadMember) {
        doAction((HadMember) qualifiedHadMember);
    }

    @Override
    public void doAction(DerivedByInsertionFrom derivedByInsertionFrom) {
        throw new UnsupportedOperationException(CpmExceptionConstants.NOT_SUPPORTED);
    }

    @Override
    public void doAction(WasInformedBy wasInformedBy) {
        addEdge(wasInformedBy);
    }

    @Override
    public void doAction(Bundle bundle, ProvUtilities provUtilities) {
        throw new UnsupportedOperationException(CpmExceptionConstants.NOT_SUPPORTED);
    }

    public boolean areAllRelationsMapped() {
        return effectEdges.isEmpty() && causeEdges.isEmpty();
    }

    public Map<QualifiedName, Map<StatementOrBundle.Kind, INode>> getNodes() {
        return nodes;
    }

    public Namespace getNamespaces() {
        return namespaces;
    }

    public INode getMainActivity() {
        for (INode node : backbone) {
            if (CpmUtilities.hasCpmType(node.getElement(), CpmType.MAIN_ACTIVITY)) {
                return node;
            }
        }
        throw new RuntimeException("No main activity found");
        // TODO throw correct exception
        // TODO multiple main activities?
    }

    private List<INode> getConnectors(CpmType type) {
        List<INode> result = new ArrayList<>();
        for (INode node : backbone) {
            if (CpmUtilities.hasCpmType(node.getElement(), type)) {
                result.add(node);
            }
        }
        return result;
    }

    public List<INode> getForwardConnectors() {
        return getConnectors(CpmType.FORWARD_CONNECTOR);
    }

    public List<INode> getBackwardConnectors() {
        return getConnectors(CpmType.BACKWARD_CONNECTOR);
    }

    public List<INode> getBackbone() {
        return backbone;
    }

    /**
     * Retrieves a single {@link INode} associated with the given {@link QualifiedName}, if exactly one exists.
     *
     * <p>This method should be used when exactly one node is expected. If multiple nodes exist for the given ID,
     * an {@link IllegalStateException} is thrown. Use {@link #getNodes(QualifiedName)} to retrieve all nodes.</p>
     *
     * @param id the {@link QualifiedName} to look up
     * @return the single {@link INode} for the given ID, or {@code null} if none exists
     * @throws IllegalStateException if multiple nodes are associated with the given ID
     */
    public INode getNode(QualifiedName id) {
        if (nodes.containsKey(id)) {
            if (nodes.get(id).size() == 1) {
                return nodes.get(id).values().iterator().next();
            }
            throw new IllegalStateException(CpmExceptionConstants.MULTIPLE_NODES);
        }
        return null;
    }

    /**
     * Retrieves a single {@link INode} associated with the given {@link QualifiedName} and {@link StatementOrBundle.Kind}.
     *
     * @param id   the {@link QualifiedName} to look up
     * @param kind the {@link StatementOrBundle.Kind} to look up
     * @return the {@link INode} matching the given ID and kind, or {@code null} if none exists
     */
    public INode getNode(QualifiedName id, StatementOrBundle.Kind kind) {
        if (nodes.containsKey(id)) {
            return nodes.get(id).get(kind);
        }
        return null;
    }

    /**
     * Retrieves all {@link INode}s associated with the given {@link QualifiedName}.
     *
     * <p>Use this method to fetch all nodes for the given ID. For a specific node by kind,
     * see {@link #getNode(QualifiedName, StatementOrBundle.Kind)}. To ensure only one node exists, use {@link #getNode(QualifiedName)}.</p>
     *
     * @param id the {@link QualifiedName} to look up
     * @return a {@link List} of {@link INode}s associated with the given ID, or {@code null} if none exist
     */
    public List<INode> getNodes(QualifiedName id) {
        if (nodes.containsKey(id)) {
            return nodes.get(id).values().stream().toList();
        }
        return null;
    }

    public IEdge getEdge(QualifiedName id) {
        for (IEdge edge : edges) {
            if (edge.getRelation() instanceof Identifiable relWithId
                    && Objects.equals(id, relWithId.getId())) {
                return edge;
            }
        }
        return null;
    }

    public IEdge getEdge(QualifiedName effect, QualifiedName cause) {
        for (IEdge edge : edges) {
            if (edge.getEffect() != null &&
                    Objects.equals(effect, edge.getEffect().getElement().getId())
                    && edge.getCause() != null &&
                    Objects.equals(cause, edge.getCause().getElement().getId())) {
                return edge;
            }
        }
        return null;
    }
}
