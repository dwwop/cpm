package cz.muni.fi.cpm.vannila;

import cz.muni.fi.cpm.constants.CpmTypeConstants;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.model.extension.QualifiedAlternateOf;
import org.openprovenance.prov.model.extension.QualifiedHadMember;
import org.openprovenance.prov.model.extension.QualifiedSpecializationOf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CpmDocument implements StatementAction {
    private static final ProvUtilities u = new ProvUtilities();

    private final Map<QualifiedName, List<Edge>> sourceEdges = new HashMap<>();
    private final Map<QualifiedName, List<Edge>> targetEdges = new HashMap<>();
    private final Map<QualifiedName, Node> nodes = new HashMap<>();
    private final List<Edge> edges = new ArrayList<>();
    private Namespace namespaces;
    private Bundle bundle;
    private Activity mainActivity;


    public CpmDocument() {
    }

    public CpmDocument(final Document document, ProvFactory pF) {
        if (document != null) {
            if (document.getStatementOrBundle().size() != 1 || !(document.getStatementOrBundle().getFirst() instanceof Bundle)) {
                // TODO throw exception
            }

            Bundle bundle = (Bundle) document.getStatementOrBundle().getFirst();
            this.bundle = pF.newNamedBundle(bundle.getId(), pF.newNamespace(bundle.getNamespace()), null);
            this.namespaces = pF.newNamespace(document.getNamespace());

            u.forAllStatement(bundle.getStatement(), this);
        }

        if (!sourceEdges.isEmpty() || !targetEdges.isEmpty()) {
            // TODO throw exception
        }
    }

    public Document toDocument(ProvFactory pF) {
        Document document = pF.newDocument();

        List<Statement> statements = new ArrayList<>();
        statements.addAll(nodes.values().stream().map(Node::getElement).toList());
        statements.addAll(edges.stream().map(Edge::getRelation).toList());

        Bundle newBundle = pF.newNamedBundle(bundle.getId(), pF.newNamespace(bundle.getNamespace()), statements);
        document.getStatementOrBundle().add(newBundle);

        Namespace ns = Namespace.gatherNamespaces(newBundle);
        ns.extendWith(namespaces);
        document.setNamespace(ns);

        return document;
    }

    private void processEdge(Edge edge, QualifiedName source, QualifiedName target) {
        if (nodes.containsKey(source)) {
            Node sourceNode = nodes.get(source);
            sourceNode.getEdges().add(edge);
            edge.setSource(sourceNode);
        } else {
            sourceEdges.computeIfAbsent(source, _ -> new ArrayList<>()).add(edge);
        }

        if (target != null && nodes.containsKey(target)) {
            Node targetNode = nodes.get(target);
            edge.setTarget(targetNode);
        } else {
            targetEdges.computeIfAbsent(target, _ -> new ArrayList<>()).add(edge);
        }

        edges.add(edge);
    }

    private void processNode(Element element) {
        Node node = new Node(element);
        nodes.put(node.getElement().getId(), node);
        if (sourceEdges.containsKey(node.getElement().getId())) {
            List<Edge> edgesToUpdate = sourceEdges.get(node.getElement().getId());
            for (Edge edge : edgesToUpdate) {
                edge.setSource(node);
                node.getEdges().add(edge);
            }
            sourceEdges.remove(node.getElement().getId());
        }

        if (targetEdges.containsKey(node.getElement().getId())) {
            List<Edge> edgesToUpdate = targetEdges.get(node.getElement().getId());
            for (Edge edge : edgesToUpdate) {
                edge.setTarget(node);
            }
            targetEdges.remove(node.getElement().getId());
        }
    }

    @Override
    public void doAction(Activity activity) {
        processNode(activity);

        if (activity.getType().stream().anyMatch(x -> CpmTypeConstants.MAIN_ACTIVITY_TYPE.equals(x.getValue()))) {
            if (mainActivity == null) {
                this.mainActivity = activity;
            } else {
                // TODO throw multiple main activities exception
            }
        }
    }

    @Override
    public void doAction(Used used) {
        processEdge(new Edge(used), used.getActivity(), used.getEntity());
    }

    @Override
    public void doAction(WasStartedBy wasStartedBy) {
        processEdge(new Edge(wasStartedBy), wasStartedBy.getStarter(), wasStartedBy.getTrigger());
    }

    @Override
    public void doAction(Agent agent) {
        processNode(agent);
    }

    @Override
    public void doAction(AlternateOf alternateOf) {
        processEdge(new Edge(alternateOf), alternateOf.getAlternate1(), alternateOf.getAlternate2());
    }

    @Override
    public void doAction(WasAssociatedWith wasAssociatedWith) {
        processEdge(new Edge(wasAssociatedWith), wasAssociatedWith.getActivity(), wasAssociatedWith.getAgent());
    }

    @Override
    public void doAction(WasAttributedTo wasAttributedTo) {
        processEdge(new Edge(wasAttributedTo), wasAttributedTo.getEntity(), wasAttributedTo.getAgent());
    }

    @Override
    public void doAction(WasInfluencedBy wasInfluencedBy) {
        processEdge(new Edge(wasInfluencedBy), wasInfluencedBy.getInfluencee(), wasInfluencedBy.getInfluencer());
    }

    @Override
    public void doAction(ActedOnBehalfOf actedOnBehalfOf) {
        // TODO
    }

    @Override
    public void doAction(WasDerivedFrom wasDerivedFrom) {
        processEdge(new Edge(wasDerivedFrom), wasDerivedFrom.getGeneratedEntity(), wasDerivedFrom.getUsedEntity());
    }

    @Override
    public void doAction(DictionaryMembership dictionaryMembership) {
        // TODO
    }

    @Override
    public void doAction(DerivedByRemovalFrom derivedByRemovalFrom) {
        // TODO
    }

    @Override
    public void doAction(WasEndedBy wasEndedBy) {
        processEdge(new Edge(wasEndedBy), wasEndedBy.getActivity(), wasEndedBy.getTrigger());
    }

    @Override
    public void doAction(Entity entity) {
        processNode(entity);
    }

    @Override
    public void doAction(WasGeneratedBy wasGeneratedBy) {
        processEdge(new Edge(wasGeneratedBy), wasGeneratedBy.getEntity(), wasGeneratedBy.getActivity());
    }

    @Override
    public void doAction(WasInvalidatedBy wasInvalidatedBy) {
        processEdge(new Edge(wasInvalidatedBy), wasInvalidatedBy.getEntity(), wasInvalidatedBy.getActivity());
    }

    @Override
    public void doAction(HadMember hadMember) {
        // TODO
    }

    @Override
    public void doAction(MentionOf mentionOf) {
        processEdge(new Edge(mentionOf), mentionOf.getSpecificEntity(), mentionOf.getGeneralEntity());
    }

    @Override
    public void doAction(SpecializationOf specializationOf) {
        processEdge(new Edge(specializationOf), specializationOf.getSpecificEntity(), specializationOf.getGeneralEntity());
    }

    @Override
    public void doAction(QualifiedSpecializationOf qualifiedSpecializationOf) {
        processEdge(new Edge(qualifiedSpecializationOf), qualifiedSpecializationOf.getSpecificEntity(), qualifiedSpecializationOf.getGeneralEntity());
    }

    @Override
    public void doAction(QualifiedAlternateOf qualifiedAlternateOf) {
        processEdge(new Edge(qualifiedAlternateOf), qualifiedAlternateOf.getAlternate1(), qualifiedAlternateOf.getAlternate2());
    }

    @Override
    public void doAction(QualifiedHadMember qualifiedHadMember) {
        // TODO
    }

    @Override
    public void doAction(DerivedByInsertionFrom derivedByInsertionFrom) {
        // TODO
    }

    @Override
    public void doAction(WasInformedBy wasInformedBy) {
        processEdge(new Edge(wasInformedBy), wasInformedBy.getInformed(), wasInformedBy.getInformant());
    }

    @Override
    public void doAction(Bundle bundle, ProvUtilities provUtilities) {
        throw new UnsupportedOperationException("Not supported");
    }

    public Map<QualifiedName, Node> getNodes() {
        return nodes;
    }

    public Namespace getNamespaces() {
        return namespaces;
    }

    public Activity getMainActivity() {
        return mainActivity;
    }
}
