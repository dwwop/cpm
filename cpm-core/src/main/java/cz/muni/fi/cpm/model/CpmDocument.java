package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.constants.CpmExceptionConstancts;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.vannila.Edge;
import cz.muni.fi.cpm.vannila.Node;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.model.extension.QualifiedAlternateOf;
import org.openprovenance.prov.model.extension.QualifiedHadMember;
import org.openprovenance.prov.model.extension.QualifiedSpecializationOf;

import java.util.*;

public class CpmDocument implements StatementAction {
    private static final ProvUtilities u = new ProvUtilities();
    private final ProvFactory pF;

    private final Map<QualifiedName, List<IEdge>> sourceEdges = new HashMap<>();
    private final Map<QualifiedName, List<IEdge>> targetEdges = new HashMap<>();

    private final Map<QualifiedName, INode> nodes = new HashMap<>();
    private final List<IEdge> edges = new ArrayList<>();
    private final List<INode> backbone = new ArrayList<>();
    private Namespace namespaces;
    private Bundle bundle;


    public CpmDocument(ProvFactory pF) {
        this.pF = pF;
    }

    public CpmDocument(final Document document, ProvFactory provFactory) {
        pF = provFactory;
        if (document == null) {
            throw new IllegalArgumentException(CpmExceptionConstancts.NOT_NULL_DOCUMENT);
        }

        if (document.getStatementOrBundle().size() != 1 ||
                !(document.getStatementOrBundle().getFirst() instanceof Bundle docBundle)) {
            throw new IllegalArgumentException(CpmExceptionConstancts.ONE_BUNDLE_REQUIRED);
        }

        this.bundle = pF.newNamedBundle(docBundle.getId(), pF.newNamespace(docBundle.getNamespace()), null);
        this.namespaces = pF.newNamespace(document.getNamespace());

        u.forAllStatement(docBundle.getStatement(), this);
    }

    public Document toDocument() {
        Document document = pF.newDocument();

        List<Statement> statements = new ArrayList<>();
        statements.addAll(nodes.values().stream().map(INode::getElement).toList());
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

    private void addEdge(IEdge edge, QualifiedName source, QualifiedName target) {
        if (nodes.containsKey(source)) {
            INode sourceINode = nodes.get(source);
            sourceINode.getEdges().add(edge);
            edge.setSource(sourceINode);
        } else {
            sourceEdges.computeIfAbsent(source, _ -> new ArrayList<>()).add(edge);
        }

        if (target != null && nodes.containsKey(target)) {
            INode targetINode = nodes.get(target);
            edge.setTarget(targetINode);
        } else {
            targetEdges.computeIfAbsent(target, _ -> new ArrayList<>()).add(edge);
        }

        edges.add(edge);
    }

    private void addNode(Element element) {
        INode node = new Node(element);
        nodes.put(node.getElement().getId(), node);
        if (sourceEdges.containsKey(node.getElement().getId())) {
            List<IEdge> edgesToUpdate = sourceEdges.get(node.getElement().getId());
            for (IEdge edge : edgesToUpdate) {
                edge.setSource(node);
                node.getEdges().add(edge);
            }
            sourceEdges.remove(node.getElement().getId());
        }

        if (targetEdges.containsKey(node.getElement().getId())) {
            List<IEdge> edgesToUpdate = targetEdges.get(node.getElement().getId());
            for (IEdge edge : edgesToUpdate) {
                edge.setTarget(node);
            }
            targetEdges.remove(node.getElement().getId());
        }

        if (CpmUtilities.isCpmElement(element)) {
            backbone.add(node);
        }
    }

    @Override
    public void doAction(Activity activity) {
        addNode(activity);
    }

    @Override
    public void doAction(Used used) {
        addEdge(new Edge(used), used.getActivity(), used.getEntity());
    }

    @Override
    public void doAction(WasStartedBy wasStartedBy) {
        addEdge(new Edge(wasStartedBy), wasStartedBy.getStarter(), wasStartedBy.getTrigger());
    }

    @Override
    public void doAction(Agent agent) {
        addNode(agent);
    }

    @Override
    public void doAction(AlternateOf alternateOf) {
        addEdge(new Edge(alternateOf), alternateOf.getAlternate1(), alternateOf.getAlternate2());
    }

    @Override
    public void doAction(WasAssociatedWith wasAssociatedWith) {
        addEdge(new Edge(wasAssociatedWith), wasAssociatedWith.getActivity(), wasAssociatedWith.getAgent());
    }

    @Override
    public void doAction(WasAttributedTo wasAttributedTo) {
        addEdge(new Edge(wasAttributedTo), wasAttributedTo.getEntity(), wasAttributedTo.getAgent());
    }

    @Override
    public void doAction(WasInfluencedBy wasInfluencedBy) {
        addEdge(new Edge(wasInfluencedBy), wasInfluencedBy.getInfluencee(), wasInfluencedBy.getInfluencer());
    }

    @Override
    public void doAction(ActedOnBehalfOf actedOnBehalfOf) {
        addEdge(new Edge(actedOnBehalfOf), actedOnBehalfOf.getDelegate(), actedOnBehalfOf.getResponsible());
    }

    @Override
    public void doAction(WasDerivedFrom wasDerivedFrom) {
        addEdge(new Edge(wasDerivedFrom), wasDerivedFrom.getGeneratedEntity(), wasDerivedFrom.getUsedEntity());
    }

    @Override
    public void doAction(DictionaryMembership dictionaryMembership) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void doAction(DerivedByRemovalFrom derivedByRemovalFrom) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void doAction(WasEndedBy wasEndedBy) {
        addEdge(new Edge(wasEndedBy), wasEndedBy.getActivity(), wasEndedBy.getTrigger());
    }

    @Override
    public void doAction(Entity entity) {
        addNode(entity);
    }

    @Override
    public void doAction(WasGeneratedBy wasGeneratedBy) {
        addEdge(new Edge(wasGeneratedBy), wasGeneratedBy.getEntity(), wasGeneratedBy.getActivity());
    }

    @Override
    public void doAction(WasInvalidatedBy wasInvalidatedBy) {
        addEdge(new Edge(wasInvalidatedBy), wasInvalidatedBy.getEntity(), wasInvalidatedBy.getActivity());
    }

    @Override
    public void doAction(HadMember hadMember) {
        for (QualifiedName member : hadMember.getEntity()) {
            addEdge(new Edge(hadMember), hadMember.getCollection(), member);
        }
    }

    @Override
    public void doAction(MentionOf mentionOf) {
        addEdge(new Edge(mentionOf), mentionOf.getSpecificEntity(), mentionOf.getGeneralEntity());
    }

    @Override
    public void doAction(SpecializationOf specializationOf) {
        addEdge(new Edge(specializationOf), specializationOf.getSpecificEntity(), specializationOf.getGeneralEntity());
    }

    @Override
    public void doAction(QualifiedSpecializationOf qualifiedSpecializationOf) {
        addEdge(new Edge(qualifiedSpecializationOf), qualifiedSpecializationOf.getSpecificEntity(), qualifiedSpecializationOf.getGeneralEntity());
    }

    @Override
    public void doAction(QualifiedAlternateOf qualifiedAlternateOf) {
        addEdge(new Edge(qualifiedAlternateOf), qualifiedAlternateOf.getAlternate1(), qualifiedAlternateOf.getAlternate2());
    }

    @Override
    public void doAction(QualifiedHadMember qualifiedHadMember) {
        doAction((HadMember) qualifiedHadMember);
    }

    @Override
    public void doAction(DerivedByInsertionFrom derivedByInsertionFrom) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void doAction(WasInformedBy wasInformedBy) {
        addEdge(new Edge(wasInformedBy), wasInformedBy.getInformed(), wasInformedBy.getInformant());
    }

    @Override
    public void doAction(Bundle bundle, ProvUtilities provUtilities) {
        throw new UnsupportedOperationException("Not supported");
    }

    public boolean areAllRelationsMapped() {
        return sourceEdges.isEmpty() && targetEdges.isEmpty();
    }

    public Map<QualifiedName, INode> getNodes() {
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

    public INode getNode(QualifiedName id) {
        return nodes.get(id);
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

    public IEdge getEdge(QualifiedName source, QualifiedName target) {
        for (IEdge edge : edges) {
            if (edge.getSource() != null &&
                    Objects.equals(source, edge.getSource().getElement().getId())
                    && edge.getTarget() != null &&
                    Objects.equals(target, edge.getTarget().getElement().getId())) {
                return edge;
            }
        }
        return null;
    }
}
