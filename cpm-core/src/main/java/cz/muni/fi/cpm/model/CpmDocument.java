package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.constants.CpmExceptionConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.exception.NoSpecificKind;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.model.extension.QualifiedAlternateOf;
import org.openprovenance.prov.model.extension.QualifiedHadMember;
import org.openprovenance.prov.model.extension.QualifiedSpecializationOf;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CpmDocument implements StatementAction {
    private static final ProvUtilities u = new ProvUtilities();
    private final ProvFactory pF;
    private final ICpmProvFactory cPF;
    private final ICpmFactory cF;

    private final Map<QualifiedName, Map<StatementOrBundle.Kind, List<IEdge>>> effectEdges = new HashMap<>();
    private final Map<QualifiedName, Map<StatementOrBundle.Kind, List<IEdge>>> causeEdges = new HashMap<>();
    private final Map<QualifiedName, Map<QualifiedName, List<IEdge>>> causeInfluences = new HashMap<>();
    private final Map<QualifiedName, Map<QualifiedName, List<IEdge>>> effectInfluences = new HashMap<>();


    private final Map<QualifiedName, Map<StatementOrBundle.Kind, INode>> nodes = new HashMap<>();
    private final List<INode> listOfNodes = new ArrayList<>();
    private final List<IEdge> edges = new ArrayList<>();
    private QualifiedName bundleId;

    public CpmDocument(ProvFactory pF, ICpmProvFactory cPF, ICpmFactory cF) {
        this.pF = pF;
        this.cF = cF;
        this.cPF = cPF;
    }

    public CpmDocument(final Document document, ProvFactory provFactory, ICpmProvFactory cPF, ICpmFactory cF) {
        pF = provFactory;
        this.cPF = cPF;
        this.cF = cF;
        if (document == null) {
            throw new IllegalArgumentException(CpmExceptionConstants.NOT_NULL_DOCUMENT);
        }

        if (document.getStatementOrBundle().size() != 1 ||
                !(document.getStatementOrBundle().getFirst() instanceof Bundle docBundle)) {
            throw new IllegalArgumentException(CpmExceptionConstants.ONE_BUNDLE_REQUIRED);
        }

        this.bundleId = docBundle.getId();

        u.forAllStatement(docBundle.getStatement(), this);
    }

    public CpmDocument(final QualifiedName bundleId, List<INode> backbone, List<INode> domainSpecificPart, List<IEdge> crossPartEdges, ProvFactory provFactory, ICpmProvFactory cPF, ICpmFactory cF) {
        this.pF = provFactory;
        this.cF = cF;
        this.cPF = cPF;
        this.bundleId = bundleId;

        if (bundleId == null || backbone == null || domainSpecificPart == null || crossPartEdges == null) {
            throw new IllegalArgumentException(CpmExceptionConstants.NOT_NULL_PARTS);
        }

        addNodes(backbone);
        addNodes(domainSpecificPart);
        addEdges(crossPartEdges);
    }

    public CpmDocument(List<Statement> backbone, List<Statement> domainSpecificPart, List<Statement> crossPartEdges, final QualifiedName bundleId, ProvFactory provFactory, ICpmProvFactory cPF, ICpmFactory cF) {
        this.pF = provFactory;
        this.cF = cF;
        this.cPF = cPF;
        this.bundleId = bundleId;

        if (bundleId == null || backbone == null || domainSpecificPart == null || crossPartEdges == null) {
            throw new IllegalArgumentException(CpmExceptionConstants.NOT_NULL_PARTS);
        }

        u.forAllStatement(backbone, this);
        u.forAllStatement(domainSpecificPart, this);
        u.forAllStatement(crossPartEdges, this);
    }

    /**
     * Converts the current object to a {@link Document} representation.
     * This method creates a new {@link Document} and populates it with statements
     * from nodes and edges, and then wraps them in a new {@link Bundle}. The resulting
     * document includes the relevant gathered namespaces.
     *
     * @return the created {@link Document} containing the bundle and namespaces
     */
    public Document toDocument() {
        Document document = pF.newDocument();

        List<Component> components = new ArrayList<>();
        components.addAll(listOfNodes);
        components.addAll(edges);

        List<Statement> statements = cF.getComponentsTransformer().apply(components);

        Bundle newBundle = pF.newNamedBundle(bundleId, statements);
        Namespace bundleNs = pF.newNamespace();
        newBundle.setNamespace(bundleNs);

        document.getStatementOrBundle().add(newBundle);

        Namespace ns = Namespace.gatherNamespaces(newBundle);
        ns.extendWith(cPF.newCpmNamespace());
        document.setNamespace(ns);
        bundleNs.setParent(ns);

        return document;
    }


    private void addEdges(List<IEdge> edges) {
        for (IEdge edge : edges) {
            for (Relation r : edge.getRelations()) {
                u.doAction(r, this);
            }
        }
    }

    private void addEdge(Relation relation) {
        addEdge(cF.newEdge(relation), u.getEffect(relation), u.getCause(relation));
    }


    private void addEdge(IEdge edge, QualifiedName effect, QualifiedName cause) {
        if (!StatementOrBundle.Kind.PROV_MEMBERSHIP.equals(edge.getRelation().getKind())) {
            for (IEdge existingEdge : edges) {
                if (ProvUtilities2.sameEdge(u, edge.getRelation(), existingEdge.getRelation())) {
                    existingEdge.handleDuplicate(edge.getRelation());
                    return;
                }
            }
        }

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
            // handled in doAction(WasInfluencedBy)
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
            // handled in doAction(WasInfluencedBy)
        }

        edges.add(edge);
    }

    private void addNodes(List<INode> nodes) {
        for (INode node : nodes) {
            for (Element e : node.getElements()) {
                u.doAction(e, this);
            }
            addEdges(node.getCauseEdges());
        }
    }

    private void processEffectInfluence(QualifiedName effect, INode newNode) {
        if (!effectInfluences.containsKey(effect)) {
            return;
        }
        for (QualifiedName cause : effectInfluences.get(effect).keySet()) {
            List<IEdge> influences = effectInfluences.get(effect).get(cause);

            if (influences.getFirst().getEffect() == null && influences.getFirst().getCause() == null) {
                // exactly one edge in influences list, no need to update causeInfluences because the object is the same
                influences.getFirst().setEffect(newNode);
                newNode.getEffectEdges().add(influences.getFirst());
                continue;
            }

            if (influences.getFirst().getEffect() != null && influences.getFirst().getCause() == null) {
                // need to handle separately in order to not create duplicate edges
                // since there is no cause edge, all other edges (1 or 2) will be the same
                IEdge edge = cF.newEdge(influences.getFirst());
                edge.setEffect(newNode);
                newNode.getEffectEdges().add(edge);
                influences.add(edge);
                causeInfluences.get(cause).get(effect).add(edge);
                edges.add(edge);
                continue;
            }
            if (influences.getFirst().getCause() != null && influences.getFirst().getEffect() != null) {
                // since we can have any situation between no. of effect nodes : no. of cause nodes
                // we need to get all ca

                // effect - cause actual edges + edges to be added
                // 1 - 1 1 + 1
                // 1 - 2 2 + 2
                // 1 - 3 3 + 3
                // 2 - 1 2 + 1
                // 2 - 2 4 + 2
                // 2 - 3 6 + 3
                for (INode causeNode : influences.stream().map(IEdge::getCause).distinct().toList()) {
                    IEdge edge = cF.newEdge(influences.getFirst());
                    edge.setEffect(newNode);
                    edge.setCause(causeNode);
                    newNode.getEffectEdges().add(edge);
                    causeNode.getCauseEdges().add(edge);
                    influences.add(edge);
                    causeInfluences.get(cause).get(effect).add(edge);
                    edges.add(edge);
                }
                continue;
            }

            // cause != null && effect == null
            for (IEdge edge : effectInfluences.get(effect).get(cause)) {
                edge.setEffect(newNode);
                newNode.getEffectEdges().add(edge);
            }
        }
    }


    private void processCauseInfluence(QualifiedName cause, INode newNode) {
        if (!causeInfluences.containsKey(cause)) {
            return;
        }
        for (QualifiedName effect : causeInfluences.get(cause).keySet()) {
            List<IEdge> influences = causeInfluences.get(cause).get(effect);

            if (influences.getFirst().getEffect() == null && influences.getFirst().getCause() == null) {
                // exactly one edge in influences list
                influences.getFirst().setCause(newNode);
                newNode.getCauseEdges().add(influences.getFirst());
                continue;
            }

            if (influences.getFirst().getCause() != null && influences.getFirst().getEffect() == null) {
                // need to handle separately in order to not create duplicate edges
                // since there is no effect edge, all other edges (1 or 2) will be the same
                IEdge edge = cF.newEdge(influences.getFirst());
                edge.setCause(newNode);
                newNode.getCauseEdges().add(edge);
                influences.add(edge);
                effectInfluences.get(effect).get(cause).add(edge);
                edges.add(edge);
                continue;
            }
            if (influences.getFirst().getCause() != null && influences.getFirst().getEffect() != null) {
                // since we can have any situation between no. of cause nodes : no. of effect nodes
                // we need to get all ca

                // effect - cause actual edges + edges to be added
                // 1 - 1 1 + 1
                // 1 - 2 2 + 2
                // 1 - 3 3 + 3
                // 2 - 1 2 + 1
                // 2 - 2 4 + 2
                // 2 - 3 6 + 3
                for (INode effectNode : influences.stream().map(IEdge::getEffect).distinct().toList()) {
                    IEdge edge = cF.newEdge(influences.getFirst());
                    edge.setCause(newNode);
                    edge.setEffect(effectNode);
                    newNode.getCauseEdges().add(edge);
                    effectNode.getEffectEdges().add(edge);
                    influences.add(edge);
                    effectInfluences.get(effect).get(cause).add(edge);
                    edges.add(edge);
                }
                continue;
            }

            // cause == null && effect != null
            for (IEdge edge : causeInfluences.get(cause).get(effect)) {
                edge.setCause(newNode);
                newNode.getCauseEdges().add(edge);
            }
        }
    }

    private void addNode(Element element) {
        INode newNode = cF.newNode(element);
        QualifiedName id = element.getId();
        StatementOrBundle.Kind kind = element.getKind();

        if (nodes.containsKey(id)) {
            INode existingNode = nodes.get(id).get(kind);
            if (existingNode != null) {
                existingNode.handleDuplicate(element);
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

        processCauseInfluence(id, newNode);
        processEffectInfluence(id, newNode);

        listOfNodes.add(newNode);
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
        QualifiedName effect = u.getEffect(wasInfluencedBy);
        QualifiedName cause = u.getCause(wasInfluencedBy);
        List<INode> effectNodes = new ArrayList<>();
        List<INode> causeNodes = new ArrayList<>();
        List<IEdge> finalEdges = new ArrayList<>();

        if (nodes.containsKey(effect)) {
            effectNodes.addAll(nodes.get(effect).values());
        }


        if (nodes.containsKey(cause)) {
            causeNodes.addAll(nodes.get(cause).values());
        }

        if (effectNodes.isEmpty() && causeNodes.isEmpty()) {
            IEdge edge = cF.newEdge(wasInfluencedBy);
            finalEdges.add(edge);
        } else if (effectNodes.isEmpty()) {
            for (INode causeNode : causeNodes) {
                IEdge edge = cF.newEdge(wasInfluencedBy);
                edge.setCause(causeNode);
                causeNode.getCauseEdges().add(edge);
                finalEdges.add(edge);
            }
        } else if (causeNodes.isEmpty()) {
            for (INode effectNode : effectNodes) {
                IEdge edge = cF.newEdge(wasInfluencedBy);
                edge.setEffect(effectNode);
                effectNode.getEffectEdges().add(edge);
                finalEdges.add(edge);
            }
        } else {
            for (INode effectNode : effectNodes) {
                for (INode causeNode : causeNodes) {
                    IEdge edge = cF.newEdge(wasInfluencedBy);
                    edge.setEffect(effectNode);
                    effectNode.getEffectEdges().add(edge);
                    edge.setCause(causeNode);
                    causeNode.getCauseEdges().add(edge);
                    finalEdges.add(edge);
                }
            }
        }

        for (IEdge edge : finalEdges) {
            causeInfluences.computeIfAbsent(cause, _ -> new HashMap<>())
                    .computeIfAbsent(effect, _ -> new ArrayList<>())
                    .add(edge);
            effectInfluences.computeIfAbsent(effect, _ -> new HashMap<>())
                    .computeIfAbsent(cause, _ -> new ArrayList<>())
                    .add(edge);
            edges.add(edge);
        }
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

    /**
     * Gathers the namespaces from the current set of nodes and edges, and extends them with CPM and PROV namespaces.
     *
     * @return the extended Namespace that includes the gathered namespaces, CPM namespace and PROV namespace
     */
    public Namespace getNamespaces() {
        List<StatementOrBundle> statements = new ArrayList<>();
        statements.addAll(listOfNodes.stream().flatMap(x -> x.getElements().stream()).distinct().toList());
        statements.addAll(edges.stream().flatMap(x -> x.getRelations().stream()).distinct().toList());

        NamespaceGatherer gatherer = new NamespaceGatherer();
        u.forAllStatementOrBundle(statements, gatherer);
        Namespace ns = gatherer.getNamespace();
        ns.extendWith(cPF.newCpmNamespace());
        return ns;
    }

    /**
     * Checks if all relations are mapped. When all relations are mapped, both effect and cause nodes on all relations are not {@code null}
     *
     * @return {@code true} if all relations are mapped;
     * {@code false} otherwise
     */
    public boolean areAllRelationsMapped() {
        return edges.stream().allMatch(x -> x.getCause() != null && x.getEffect() != null);
    }


    /**
     * Retrieves the main activity node from the backbone.
     * This method iterates through the nodes in the backbone and returns the first node
     * that has the {@link CpmType#MAIN_ACTIVITY} type. If no such node is found, it returns {@code null}.
     *
     * @return the main activity node, or {@code null} if not found
     */
    public INode getMainActivity() {
        for (INode node : listOfNodes) {
            if (CpmUtilities.isBackbone(node) &&
                    CpmUtilities.hasCpmType(node, CpmType.MAIN_ACTIVITY)) {
                return node;
            }
        }
        return null;
    }

    private List<INode> getConnectors(CpmType type) {
        List<INode> result = new ArrayList<>();
        for (INode node : listOfNodes) {
            if (CpmUtilities.isBackbone(node) &&
                    CpmUtilities.hasCpmType(node, type)) {
                result.add(node);
            }
        }
        return result;
    }

    /**
     * Retrieves a list of forward connector nodes from the backbone.
     *
     * @return a list of forward connector nodes
     */
    public List<INode> getForwardConnectors() {
        return getConnectors(CpmType.FORWARD_CONNECTOR);
    }

    /**
     * Retrieves a list of backward connector nodes from the backbone.
     *
     * @return a list of backward connector nodes
     */
    public List<INode> getBackwardConnectors() {
        return getConnectors(CpmType.BACKWARD_CONNECTOR);
    }


    private List<INode> getFilteredNodes(Function<INode, Boolean> nodeFilter) {
        Map<INode, INode> clonedNodeMap = listOfNodes.stream().filter(nodeFilter::apply)
                .collect(Collectors.toMap(node -> node, cF::newNode));

        List<IEdge> edges = clonedNodeMap.keySet().stream()
                .flatMap(x -> x.getCauseEdges().stream()
                        .filter(e -> clonedNodeMap.containsKey(e.getEffect())))
                .toList();

        for (IEdge edge : edges) {
            IEdge clonedEdge = cF.newEdge(edge);
            clonedEdge.setCause(clonedNodeMap.get(edge.getCause()));
            clonedEdge.getCause().getCauseEdges().add(clonedEdge);
            clonedEdge.setEffect(clonedNodeMap.get(edge.getEffect()));
            clonedEdge.getEffect().getEffectEdges().add(clonedEdge);
        }

        return clonedNodeMap.values().stream().toList();
    }

    /**
     * Creates a clone of each node in the backbone, keeping only relations that are between backbone nodes.
     *
     * @return a list of cloned backbone nodes
     */
    public List<INode> getBackbonePart() {
        return getFilteredNodes(CpmUtilities::isBackbone);
    }

    /**
     * Creates a clone of each node in the domain specific part of the document, keeping only relations that are between DS nodes.
     *
     * @return a list of cloned domain specific nodes
     */
    public List<INode> getDomainSpecificPart() {
        return getFilteredNodes(x -> !CpmUtilities.isBackbone(x));
    }

    /**
     * Retrieves all original edges that are between backbone nodes and domain specific nodes.
     *
     * @return a list of original cross part edges
     */
    public List<IEdge> getCrossPartEdges() {
        Map<INode, Boolean> nodeBackboneMap = listOfNodes.stream()
                .collect(Collectors.toMap(node -> node, CpmUtilities::isBackbone));

        return edges.stream().filter(e -> {
            if (e.getEffect() == null || e.getCause() == null) {
                return false;
            }
            boolean isCauseBackbone = nodeBackboneMap.get(e.getCause());
            boolean isEffectBackbone = nodeBackboneMap.get(e.getEffect());
            return ((isCauseBackbone && !isEffectBackbone) || (!isCauseBackbone && isEffectBackbone));
        }).toList();
    }

    /**
     * Removes a node with the specified ID and kind from the graph structure.
     * <p>
     * This method removes the node identified by the given {@code id} and {@code kind}.
     * It removes the node from the graph and its associated parts and updates the corresponding collections
     *
     * @param id   the qualified name of the node to be removed
     * @param kind the kind of the element within the node
     * @return {@code true} if the node was successfully removed; {@code false} otherwise
     */
    public boolean removeNode(QualifiedName id, StatementOrBundle.Kind kind) {
        INode node = getNode(id, kind);
        if (node == null) {
            return false;
        }
        node.getEffectEdges().forEach(e -> {
            e.setEffect(null);
            effectEdges.computeIfAbsent(id, _ -> new HashMap<>())
                    .computeIfAbsent(kind, _ -> new ArrayList<>())
                    .add(e);
        });
        node.getCauseEdges().forEach(e -> {
            e.setCause(null);
            causeEdges.computeIfAbsent(id, _ -> new HashMap<>())
                    .computeIfAbsent(kind, _ -> new ArrayList<>())
                    .add(e);
        });

        nodes.remove(id);
        listOfNodes.remove(node);
        return true;
    }

    /**
     * Removes a node from the graph based on its identifier.
     * If there are multiple nodes associated with the identifier,
     * the node will not be removed and the method will return false.
     *
     * @param id the unique identifier of the node to be removed
     * @return true if the node was successfully removed, false otherwise
     */
    public boolean removeNode(QualifiedName id) {
        if (!nodes.containsKey(id) || nodes.get(id).size() != 1) {
            return false;
        }

        return removeNode(id, getNode(id).getElement().getKind());
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
        return new ArrayList<>();
    }

    /**
     * Retrieves the entire mapping of {@link QualifiedName} to their associated nodes.
     *
     * @return a {@link Map} where keys are {@link QualifiedName}s and values are maps of
     * {@link StatementOrBundle.Kind} to {@link INode}.
     */
    public Map<QualifiedName, Map<StatementOrBundle.Kind, INode>> getNodes() {
        return nodes;
    }

    /**
     * Retrieves all edges associated with a given relation identifier.
     * This method searches through the list of edges and returns those whose relation ID matches the specified {@link QualifiedName}.
     *
     * @param id the identifier of the relation to search for
     * @return a list of {@link IEdge} objects matching the specified identifier, or an empty list if none are found
     */
    public List<IEdge> getEdges(QualifiedName id) {
        List<IEdge> result = new ArrayList<>();
        for (IEdge edge : edges) {
            if (edge.getRelation() instanceof Identifiable relWithId
                    && Objects.equals(id, relWithId.getId())
            ) {
                result.add(edge);
            }
        }
        return result;
    }

    /**
     * Retrieves a single edge associated with a given relation identifier.
     * This method searches for an edge whose relation ID matches the specified {@link QualifiedName}.
     * If multiple edges are found, an {@link IllegalStateException} is thrown.
     *
     * @param id the identifier of the relation to search for
     * @return the matching {@link IEdge}, or {@code null} if no matching edge is found
     * @throws IllegalStateException if multiple edges are found with the same identifier
     */
    public IEdge getEdge(QualifiedName id) {
        List<IEdge> edges = getEdges(id);
        if (edges.isEmpty()) {
            return null;
        }

        if (edges.size() != 1) {
            throw new IllegalStateException(CpmExceptionConstants.MULTIPLE_EDGES);
        }

        return edges.getFirst();
    }

    /**
     * Retrieves all edges that match the specified effect and cause identifiers.
     * This method searches for edges where:
     * <ul>
     *     <li>The relation is a {@link HadMember} and the collection matches the effect and the cause is contained within the entity.</li>
     *     <li>The cause and effect of the relation match the specified identifiers.</li>
     * </ul>
     *
     * @param effect the identifier of the effect
     * @param cause  the identifier of the cause
     * @return a list of {@link IEdge} objects matching the specified effect and cause, or an empty list if none are found
     */
    public List<IEdge> getEdges(QualifiedName effect, QualifiedName cause) {
        List<IEdge> result = new ArrayList<>();
        for (IEdge edge : edges) {
            if (edge.getRelation() instanceof HadMember hM) {
                if (Objects.equals(hM.getCollection(), effect) &&
                        hM.getEntity() != null && hM.getEntity().contains(cause) &&
                        (edge.getCause() != null && Objects.equals(edge.getCause().getElement().getId(), cause) ||
                                edge.getCause() == null && causeEdges.getOrDefault(cause, Map.of())
                                        .getOrDefault(StatementOrBundle.Kind.PROV_ENTITY, List.of())
                                        .stream().anyMatch(x -> x == edge))) {
                    result.add(edge);
                }
            } else if (Objects.equals(u.getCause(edge.getRelation()), cause) &&
                    Objects.equals(u.getEffect(edge.getRelation()), effect)) {
                result.add(edge);
            }
        }
        return result;
    }

    /**
     * Retrieves a single edge that matches the specified effect and cause identifiers.
     * This method searches for an edge whose effect and cause match the specified {@link QualifiedName} values.
     * If multiple edges are found, an {@link IllegalStateException} is thrown.
     *
     * @param effect the identifier of the effect
     * @param cause  the identifier of the cause
     * @return the matching {@link IEdge}, or {@code null} if no matching edge is found
     * @throws IllegalStateException if multiple edges are found between the specified effect and cause
     */
    public IEdge getEdge(QualifiedName effect, QualifiedName cause) {
        List<IEdge> edges = getEdges(effect, cause);
        if (edges.isEmpty()) {
            return null;
        }

        if (edges.size() != 1) {
            throw new IllegalStateException(CpmExceptionConstants.MULTIPLE_EDGES_BETWEEN_NODES);
        }

        return edges.getFirst();
    }

    private boolean removeEdge(List<IEdge> edgesToRemove) {
        if (edgesToRemove == null || edgesToRemove.isEmpty()) {
            return false;
        }
        for (IEdge edge : edgesToRemove) {
            if (edge.getCause() != null) {
                edge.getCause().getCauseEdges().remove(edge);
            } else {
                causeEdges.computeIfPresent(u.getCause(edge.getRelation()), (_, nodeKindMap) -> {
                    try {
                        nodeKindMap.computeIfPresent(ProvUtilities2.getCauseKind(edge.getRelation()), (_, edgeList) -> {
                            edgeList.remove(edge);
                            return edgeList.isEmpty() ? null : edgeList;
                        });
                    } catch (NoSpecificKind ignored) {
                    }
                    return nodeKindMap.isEmpty() ? null : nodeKindMap;
                });
            }

            if (edge.getEffect() != null) {
                edge.getEffect().getEffectEdges().remove(edge);
            } else {
                effectEdges.computeIfPresent(u.getEffect(edge.getRelation()), (_, nodeKindMap) -> {
                    try {
                        nodeKindMap.computeIfPresent(ProvUtilities2.getEffectKind(edge.getRelation()), (_, edgeList) -> {
                            edgeList.remove(edge);
                            return edgeList.isEmpty() ? null : edgeList;
                        });
                    } catch (NoSpecificKind ignored) {
                    }
                    return nodeKindMap.isEmpty() ? null : nodeKindMap;
                });
            }

            if (StatementOrBundle.Kind.PROV_INFLUENCE.equals(edge.getRelation().getKind())) {
                causeInfluences.computeIfPresent(u.getCause(edge.getRelation()), (_, causeEdgeMap) -> {
                    causeEdgeMap.computeIfPresent(u.getEffect(edge.getRelation()), (_, _) -> null);
                    return causeEdgeMap.isEmpty() ? null : causeEdgeMap;
                });
                effectInfluences.computeIfPresent(u.getEffect(edge.getRelation()), (_, effectEdgeMap) -> {
                    effectEdgeMap.computeIfPresent(u.getCause(edge.getRelation()), (_, _) -> null);
                    return effectEdgeMap.isEmpty() ? null : effectEdgeMap;
                });
            }

            edges.remove(edge);
        }
        return true;
    }

    /**
     * Removes all edges identified by its unique ID.
     *
     * @param id the unique identifier of the edge to be removed
     * @return {@code true} if the edge was successfully removed, {@code false} otherwise
     */
    public boolean removeEdge(QualifiedName id) {
        return removeEdge(getEdges(id));
    }

    /**
     * Removes an edge based on its effect and cause identifiers.
     *
     * @param effect the identifier of the effect node
     * @param cause  the identifier of the cause node
     * @return {@code true} if the edge was successfully removed, {@code false} otherwise
     */
    public boolean removeEdge(QualifiedName effect, QualifiedName cause) {
        return removeEdge(getEdges(effect, cause));
    }

    private List<INode> getRelatedConnectors(QualifiedName id, Function<IEdge, INode> extractNode, Function<INode, List<IEdge>> extractEdges) {
        INode node = getNode(id, StatementOrBundle.Kind.PROV_ENTITY);
        if (!CpmUtilities.isConnector(node)) {
            return null;
        }

        List<INode> result = new ArrayList<>();
        List<INode> toProcess = new ArrayList<>(extractEdges.apply(node).stream()
                .map(extractNode)
                .toList());

        while (!toProcess.isEmpty()) {
            INode current = toProcess.removeFirst();
            result.add(current);
            toProcess.addAll(extractEdges.apply(current).stream()
                    .filter(x -> StatementOrBundle.Kind.PROV_DERIVATION.equals(x.getRelation().getKind()))
                    .map(extractNode)
                    .filter(x -> CpmUtilities.isConnector(x) && !result.contains(x))
                    .toList());
        }
        return result;
    }

    /**
     * Retrieves a list of precursor nodes for the specified connector, based on the provided {@link QualifiedName}.
     * A precursor connector is defined as a connector node related to the given node via a derivation relation.
     *
     * @param id the identifier of the node for which to find the precursor nodes
     * @return a list of precursor nodes, or {@code null} the node is not a connector or is {@code null}
     */
    public List<INode> getPrecursors(QualifiedName id) {
        // look at david rypar thesis
        return getRelatedConnectors(id, IEdge::getEffect, INode::getCauseEdges);
    }

    /**
     * Retrieves a list of successor connectors for the specified connector, based on the provided {@link QualifiedName}.
     * A successor connector is defined as a connector node related to the given node via a derivation relation.
     *
     * @param id the identifier of the node for which to find the successor nodes
     * @return a list of successor nodes, or {@code null} the node is not a connector or is {@code null}
     */
    public List<INode> getSuccessors(QualifiedName id) {
        return getRelatedConnectors(id, IEdge::getCause, INode::getEffectEdges);
    }

    public QualifiedName getBundleId() {
        return this.bundleId;
    }

    public void setBundleId(QualifiedName id) {
        this.bundleId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CpmDocument document = (CpmDocument) o;
        return Objects.equals(effectEdges, document.effectEdges) &&
                Objects.equals(causeEdges, document.causeEdges) &&
                Objects.equals(nodes, document.nodes) &&
                Objects.equals(edges.size(), document.edges.size()) &&
                Objects.equals(new HashSet<>(edges), new HashSet<>(document.edges)) &&
                Objects.equals(bundleId, document.bundleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(effectEdges, causeEdges, nodes, edges, bundleId);
    }
}
