package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.exception.NoSpecificKind;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.model.extension.QualifiedHadMember;
import org.openprovenance.prov.vanilla.ProvFactory;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public abstract class CpmDocumentTest {
    protected final ICpmFactory cF;
    protected final DatatypeFactory datatypeFactory;
    protected final org.openprovenance.prov.model.ProvFactory pF;
    protected final ProvUtilities u;
    protected final ICpmProvFactory cPF;

    public CpmDocumentTest(ICpmFactory cF) throws Exception {
        this.cF = cF;
        pF = cF.getProvFactory();
        u = new ProvUtilities();
        datatypeFactory = DatatypeFactory.newInstance();
        cPF = new CpmProvFactory(pF);
    }

    private static Stream<Object[]> provideRelations() {
        ProvFactory pF = new ProvFactory();
        QualifiedName entityId1 = pF.newQualifiedName("uri", "entity1", "ex");
        QualifiedName entityId2 = pF.newQualifiedName("uri", "entity2", "ex");

        QualifiedName activityId1 = pF.newQualifiedName("uri", "activity1", "ex");
        QualifiedName activityId2 = pF.newQualifiedName("uri", "activity2", "ex");

        QualifiedName agentId1 = pF.newQualifiedName("uri", "agent1", "ex");
        QualifiedName agentId2 = pF.newQualifiedName("uri", "agent2", "ex");


        return Stream.of(
                new Object[]{pF.newUsed(pF.newQualifiedName("uri", "used", "ex"), activityId1, entityId1)},
                new Object[]{pF.newWasStartedBy(pF.newQualifiedName("uri", "wasStartedBy", "ex"), activityId1, entityId2)},
                new Object[]{pF.newWasAssociatedWith(pF.newQualifiedName("uri", "associatedWith", "ex"), activityId1, agentId1)},
                new Object[]{pF.newWasAttributedTo(pF.newQualifiedName("uri", "attributedTo", "ex"), entityId1, agentId1)},
                new Object[]{pF.newWasDerivedFrom(pF.newQualifiedName("uri", "wasDerivedFrom", "ex"), entityId1, entityId2)},
                new Object[]{pF.newActedOnBehalfOf(pF.newQualifiedName("uri", "actedOnBehalfOf", "ex"), agentId1, agentId2)},
                new Object[]{pF.newAlternateOf(entityId1, entityId2)},
                new Object[]{pF.newWasGeneratedBy(pF.newQualifiedName("uri", "wasGeneratedBy", "ex"), entityId1, activityId1)},
                new Object[]{pF.newWasInvalidatedBy(pF.newQualifiedName("uri", "wasInvalidatedBy", "ex"), entityId1, activityId1)},
                new Object[]{pF.newWasEndedBy(pF.newQualifiedName("uri", "wasEndedBy", "ex"), activityId1, entityId1)},
                new Object[]{pF.newWasInformedBy(pF.newQualifiedName("uri", "wasInformedBy", "ex"), activityId1, activityId2)},
                new Object[]{pF.newSpecializationOf(entityId1, entityId2)},
                new Object[]{pF.newQualifiedSpecializationOf(pF.newQualifiedName("uri", "qualifiedSpecializationOf", "ex"), entityId1, entityId2, Collections.emptyList())},
                new Object[]{pF.newQualifiedAlternateOf(pF.newQualifiedName("uri", "qualifiedAlternateOf", "ex"), entityId1, entityId2, Collections.emptyList())}
        );
    }

    @Test
    public void constructor_nullDocument_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new CpmDocument(null, pF, cPF, cF));
    }

    @Test
    public void constructor_withoutBundle_throwsIllegalArgumentException() {
        Document document = pF.newDocument();
        document.setNamespace(cPF.newCpmNamespace());

        assertThrows(IllegalArgumentException.class, () -> new CpmDocument(document, pF, cPF, cF));
    }

    @Test
    public void constructor_graphPartsNullDocument_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new CpmDocument(null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), pF, cPF, cF));
        assertThrows(IllegalArgumentException.class, () -> new CpmDocument(pF.newQualifiedName("uri", "bundle", "ex"), null, new ArrayList<>(), new ArrayList<>(), pF, cPF, cF));
        assertThrows(IllegalArgumentException.class, () -> new CpmDocument(pF.newQualifiedName("uri", "bundle", "ex"), new ArrayList<>(), null, new ArrayList<>(), pF, cPF, cF));
        assertThrows(IllegalArgumentException.class, () -> new CpmDocument(pF.newQualifiedName("uri", "bundle", "ex"), new ArrayList<>(), new ArrayList<>(), null, pF, cPF, cF));
    }


    @Test
    public void constructor_statementPartsNullDocument_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new CpmDocument(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, pF, cPF, cF));
        assertThrows(IllegalArgumentException.class, () -> new CpmDocument(null, new ArrayList<>(), new ArrayList<>(), pF.newQualifiedName("uri", "bundle", "ex"), pF, cPF, cF));
        assertThrows(IllegalArgumentException.class, () -> new CpmDocument(new ArrayList<>(), null, new ArrayList<>(), pF.newQualifiedName("uri", "bundle", "ex"), pF, cPF, cF));
        assertThrows(IllegalArgumentException.class, () -> new CpmDocument(new ArrayList<>(), new ArrayList<>(), null, pF.newQualifiedName("uri", "bundle", "ex"), pF, cPF, cF));
    }


    @Test
    public void constructor_graph_returnsExpectedDocument() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity entity1 = cPF.getProvFactory().newEntity(id1);
        INode node1 = cF.newNode(entity1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Entity entity2 = cPF.newCpmEntity(id2, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());
        INode node2 = cF.newNode(entity2);

        QualifiedName id3 = cPF.newCpmQualifiedName("qN3");
        Agent agent = cPF.getProvFactory().newAgent(id3);
        INode node3 = cF.newNode(agent);

        QualifiedName id4 = cPF.newCpmQualifiedName("qN4");
        Entity entity4 = cPF.newCpmEntity(id4, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());
        INode node4 = cF.newNode(entity4);

        Relation relation1 = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id1, id3);
        IEdge edge1 = cF.newEdge(relation1);
        edge1.setEffect(node1);
        edge1.setCause(node3);
        node1.getEffectEdges().add(edge1);
        node3.getCauseEdges().add(edge1);

        Relation relation2 = cPF.getProvFactory().newWasDerivedFrom(id2, id4);
        IEdge edge2 = cF.newEdge(relation2);
        edge2.setEffect(node2);
        edge2.setCause(node4);
        node2.getEffectEdges().add(edge2);
        node4.getCauseEdges().add(edge2);

        Relation relation3 = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id2, id3);
        IEdge edge3 = cF.newEdge(relation3);
        edge3.setEffect(node2);
        edge3.setCause(node3);
        node2.getEffectEdges().add(edge3);
        node3.getCauseEdges().add(edge3);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(entity2, entity4, relation2), List.of(entity1, agent, relation1), List.of(relation3), bundleId, pF, cPF, cF);

        assertEquals(node1, doc.getNode(id1));
        assertEquals(node2, doc.getNode(id2));
        assertEquals(node3, doc.getNode(id3));
        assertEquals(node4, doc.getNode(id4));
        assertEquals(edge1, doc.getEdge(id1, id3));
        assertEquals(edge2, doc.getEdge(id2, id4));
        assertEquals(edge3, doc.getEdge(id2, id3));
        assertNotNull(doc.getBundleId());
    }

    @Test
    public void constructor_statements_returnsExpectedDocument() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity entity1 = cPF.getProvFactory().newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Entity entity2 = cPF.newCpmEntity(id2, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        QualifiedName id3 = cPF.newCpmQualifiedName("qN3");
        Agent agent = cPF.getProvFactory().newAgent(id3);

        QualifiedName id4 = cPF.newCpmQualifiedName("qN4");
        Entity entity4 = cPF.newCpmEntity(id4, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        Relation relation1 = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id1, id3);

        Relation relation2 = cPF.getProvFactory().newWasDerivedFrom(id2, id4);

        Relation relation3 = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id2, id3);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(entity2, entity4, relation2), List.of(entity1, agent, relation1), List.of(relation3), bundleId, pF, cPF, cF);

        assertNotNull(doc.getNode(id1));
        assertNotNull(doc.getNode(id2));
        assertNotNull(doc.getNode(id3));
        assertNotNull(doc.getNode(id4));
        assertNotNull(doc.getEdge(id1, id3));
        assertNotNull(doc.getEdge(id2, id4));
        assertNotNull(doc.getEdge(id2, id3));
        assertNotNull(doc.getBundleId());
    }

    @Test
    public void equals_graphAndStatementsAndDocument_returnsTrue() {
        QualifiedName id1Stat = cPF.newCpmQualifiedName("qN1");
        Entity entity1Stat = cPF.getProvFactory().newEntity(id1Stat);

        QualifiedName id2Stat = cPF.newCpmQualifiedName("qN2");
        Entity entity2Stat = cPF.newCpmEntity(id2Stat, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        QualifiedName id3Stat = cPF.newCpmQualifiedName("qN3");
        Agent agentStat = cPF.getProvFactory().newAgent(id3Stat);

        QualifiedName id4Stat = cPF.newCpmQualifiedName("qN4");
        Entity entity4Stat = cPF.newCpmEntity(id4Stat, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        Relation relation1Stat = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id1Stat, id3Stat);

        Relation relation2Stat = cPF.getProvFactory().newWasDerivedFrom(id2Stat, id4Stat);

        Relation relation3Stat = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id2Stat, id3Stat);

        QualifiedName bundleIdStat = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument docStat = new CpmDocument(List.of(entity2Stat, entity4Stat, relation2Stat), List.of(entity1Stat, agentStat, relation1Stat), List.of(relation3Stat), bundleIdStat, pF, cPF, cF);


        QualifiedName id1Graph = cPF.newCpmQualifiedName("qN1");
        Entity entity1Graph = cPF.getProvFactory().newEntity(id1Graph);
        INode node1Graph = cF.newNode(entity1Graph);

        QualifiedName id2Graph = cPF.newCpmQualifiedName("qN2");
        Entity entity2Graph = cPF.newCpmEntity(id2Graph, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());
        INode node2Graph = cF.newNode(entity2Graph);

        QualifiedName id3Graph = cPF.newCpmQualifiedName("qN3");
        Agent agentGraph = cPF.getProvFactory().newAgent(id3Graph);
        INode node3Graph = cF.newNode(agentGraph);

        QualifiedName id4Graph = cPF.newCpmQualifiedName("qN4");
        Entity entity4Graph = cPF.newCpmEntity(id4Graph, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());
        INode node4Graph = cF.newNode(entity4Graph);

        Relation relation1Graph = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id1Graph, id3Graph);
        IEdge edge1Graph = cF.newEdge(relation1Graph);
        edge1Graph.setEffect(node1Graph);
        edge1Graph.setCause(node3Graph);
        node1Graph.getEffectEdges().add(edge1Graph);
        node3Graph.getCauseEdges().add(edge1Graph);

        Relation relation2Graph = cPF.getProvFactory().newWasDerivedFrom(id2Graph, id4Graph);
        IEdge edge2 = cF.newEdge(relation2Graph);
        edge2.setEffect(node2Graph);
        edge2.setCause(node4Graph);
        node2Graph.getEffectEdges().add(edge2);
        node4Graph.getCauseEdges().add(edge2);

        Relation relation3Graph = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id2Graph, id3Graph);
        IEdge edge3 = cF.newEdge(relation3Graph);
        edge3.setEffect(node2Graph);
        edge3.setCause(node3Graph);
        node2Graph.getEffectEdges().add(edge3);
        node3Graph.getCauseEdges().add(edge3);

        QualifiedName bundleIdGraph = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument docGraph = new CpmDocument(List.of(entity2Graph, entity4Graph, relation2Graph), List.of(entity1Graph, agentGraph, relation1Graph), List.of(relation3Graph), bundleIdGraph, pF, cPF, cF);

        QualifiedName id1Doc = cPF.newCpmQualifiedName("qN1");
        Entity entity1Doc = cPF.getProvFactory().newEntity(id1Doc);

        QualifiedName id2Doc = cPF.newCpmQualifiedName("qN2");
        Entity entity2Doc = cPF.newCpmEntity(id2Doc, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        QualifiedName id3Doc = cPF.newCpmQualifiedName("qN3");
        Agent agentDoc = cPF.getProvFactory().newAgent(id3Doc);

        QualifiedName id4Doc = cPF.newCpmQualifiedName("qN4");
        Entity entity4Doc = cPF.newCpmEntity(id4Doc, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        Relation relation1Doc = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id1Doc, id3Doc);

        Relation relation2Doc = cPF.getProvFactory().newWasDerivedFrom(id2Doc, id4Doc);

        Relation relation3Doc = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id2Doc, id3Doc);

        Document document = pF.newDocument();
        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        bundle.getStatement().addAll(List.of(entity1Doc, agentDoc, entity2Doc, entity4Doc));
        bundle.getStatement().addAll(List.of(relation1Doc, relation2Doc, relation3Doc));
        document.setNamespace(Namespace.gatherNamespaces(document));
        bundle.setNamespace(Namespace.gatherNamespaces(document));

        CpmDocument docDoc = new CpmDocument(document, pF, cPF, cF);

        assertEquals(docDoc, docGraph);
        assertEquals(docStat, docGraph);
        assertEquals(docStat, docDoc);
    }

    @Test
    public void equals_unmappedRelations_returnsTrue() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");

        QualifiedName id3 = cPF.newCpmQualifiedName("qN3");

        QualifiedName id4 = cPF.newCpmQualifiedName("qN4");

        Relation relation1 = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id1, id3);

        Relation relation2 = cPF.getProvFactory().newWasDerivedFrom(id2, id4);
        Document document = pF.newDocument();
        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);
        bundle.getStatement().addAll(List.of(relation1, relation2));
        CpmDocument doc1 = new CpmDocument(document, pF, cPF, cF);

        Document document2 = pF.newDocument();
        Bundle bundle2 = pF.newNamedBundle(id, new ArrayList<>());
        document2.getStatementOrBundle().add(bundle2);
        CpmDocument doc2 = new CpmDocument(document2, pF, cPF, cF);
        bundle2.getStatement().add(relation1);

        assertNotEquals(doc1, doc2);
    }

    @Test
    public void constructor_namespace_returnsExpectedNamespaces() {
        Document document = pF.newDocument();
        document.setNamespace(cPF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        assertEquals(document.getNamespace().getNamespaces(), doc.getNamespaces().getNamespaces());
    }

    @Test
    public void constructor_mainActivity_returnsExpectedMainActivity() {
        Document document = pF.newDocument();
        document.setNamespace(cPF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName activityId = pF.newQualifiedName("uri", "activity", "ex");
        XMLGregorianCalendar startTime = datatypeFactory.newXMLGregorianCalendar("2024-11-13T10:00:00");
        XMLGregorianCalendar endTime = datatypeFactory.newXMLGregorianCalendar("2024-11-13T12:00:00");
        CpmType type = CpmType.MAIN_ACTIVITY;
        Collection<Attribute> attributes = new ArrayList<>();
        Activity activity = cPF.newCpmActivity(activityId, startTime, endTime, type, attributes);
        bundle.getStatement().add(activity);

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        assertNotNull(doc.getNode(activityId));
        assertFalse(doc.getBackbonePart().isEmpty());
        assertNotNull(doc.getMainActivity());
        assertEquals(activity, doc.getNode(activityId).getElement());
    }


    @Test
    public void constructor_regularActivity_returnsNull() {
        Document document = pF.newDocument();
        document.setNamespace(cPF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName activityId = pF.newQualifiedName("uri", "activity", "ex");
        Activity activity = pF.newActivity(activityId);
        bundle.getStatement().add(activity);

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        assertNotNull(doc.getNode(activityId));
        assertTrue(doc.getBackbonePart().isEmpty());
        assertNull(doc.getMainActivity());
    }


    @Test
    public void constructor_forwardConnector_returnsExpectedForwardConnectors() {
        Document document = pF.newDocument();
        document.setNamespace(cPF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName entityId = pF.newQualifiedName("uri", "entity", "ex");
        CpmType type = CpmType.FORWARD_CONNECTOR;
        Collection<Attribute> attributes = new ArrayList<>();
        Entity entity = cPF.newCpmEntity(entityId, type, attributes);
        bundle.getStatement().add(entity);

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        assertNotNull(doc.getNode(entityId));
        assertFalse(doc.getBackbonePart().isEmpty());
        assertFalse(doc.getForwardConnectors().isEmpty());
        assertEquals(entity, doc.getNode(entityId).getElement());
    }


    @Test
    public void constructor_regularEntity_returnsExpectedRegularEntity() {
        Document document = pF.newDocument();
        document.setNamespace(cPF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName entityId = pF.newQualifiedName("uri", "entity", "ex");
        Entity entity = pF.newEntity(entityId);
        bundle.getStatement().add(entity);

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        assertNotNull(doc.getNode(entityId));
        assertTrue(doc.getBackbonePart().isEmpty());
        assertTrue(doc.getForwardConnectors().isEmpty());
        assertEquals(entity, doc.getNode(entityId).getElement());
        assertNotNull(doc.getBundleId());
    }




    @Test
    public void constructor_identicalIdDifferentKind_returnsExpectedTypes() {
        Document document = pF.newDocument();
        document.setNamespace(cPF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName identicalId = pF.newQualifiedName("uri", "id", "ex");

        Entity entity = pF.newEntity(identicalId);
        bundle.getStatement().add(entity);

        Activity activity = pF.newActivity(identicalId);
        bundle.getStatement().add(activity);

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        assertThrows(IllegalStateException.class, () -> doc.getNode(identicalId));
        assertEquals(2, doc.getNodes(identicalId).size());
        assertEquals(entity, doc.getNode(identicalId, StatementOrBundle.Kind.PROV_ENTITY).getElement());
        assertEquals(activity, doc.getNode(identicalId, StatementOrBundle.Kind.PROV_ACTIVITY).getElement());
    }


    @Test
    public void constructor_senderAgent_returnsExpectedSenderAgent() {
        Document document = pF.newDocument();
        document.setNamespace(cPF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName agentId = pF.newQualifiedName("uri", "agent", "ex");
        CpmType type = CpmType.SENDER_AGENT;
        Collection<Attribute> attributes = new ArrayList<>();
        Agent agent = cPF.newCpmAgent(agentId, type, attributes);
        bundle.getStatement().add(agent);

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        assertNotNull(doc.getNode(agentId));
        assertFalse(doc.getBackbonePart().isEmpty());
        assertEquals(agent, doc.getNode(agentId).getElement());
    }


    @Test
    public void constructor_regularAgent_returnsExpectedRegularAgent() {
        Document document = pF.newDocument();
        document.setNamespace(cPF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName agentId = pF.newQualifiedName("uri", "agent", "ex");
        Agent agent = pF.newAgent(agentId);
        bundle.getStatement().add(agent);

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        assertNotNull(doc.getNode(agentId));
        assertTrue(doc.getBackbonePart().isEmpty());
        assertEquals(agent, doc.getNode(agentId).getElement());
    }

    private Element getElement(StatementOrBundle.Kind kind, QualifiedName id) {
        if (StatementOrBundle.Kind.PROV_ENTITY == kind) {
            return pF.newEntity(id);
        } else if (StatementOrBundle.Kind.PROV_AGENT == kind) {
            return pF.newAgent(id);
        } else if (StatementOrBundle.Kind.PROV_ACTIVITY == kind) {
            return pF.newActivity(id);
        }
        throw new UnsupportedOperationException();
    }

    @ParameterizedTest
    @MethodSource("provideRelations")
    public void constructor_validRelationsMapping_returnsMappedRelations(Relation relation) throws NoSpecificKind {
        Document document = pF.newDocument();
        document.setNamespace(cPF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName effectId = u.getEffect(relation);
        QualifiedName causeId = u.getCause(relation);
        Element effect = getElement(ProvUtilities2.getEffectKind(relation), effectId);
        Element cause = getElement(ProvUtilities2.getCauseKind(relation), causeId);
        bundle.getStatement().add(effect);
        bundle.getStatement().add(cause);

        bundle.getStatement().add(relation);

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        assertNotNull(doc.getNode(effectId));
        assertNotNull(doc.getNode(causeId));
        assertNotNull(doc.getEdge(effectId, causeId));
        assertEquals(relation, doc.getNode(effectId).getEffectEdges().getFirst().getRelation());
        assertEquals(relation, doc.getNode(causeId).getCauseEdges().getFirst().getRelation());
        assertTrue(doc.areAllRelationsMapped());

        if (relation instanceof Identifiable relWithId) {
            assertNotNull(doc.getEdge(relWithId.getId()));
            assertEquals(relation, doc.getEdge(relWithId.getId()).getRelation());
            assertEquals(effect, doc.getEdge(relWithId.getId()).getEffect().getElement());
            assertEquals(cause, doc.getEdge(relWithId.getId()).getCause().getElement());
        } else {
            assertEquals(relation, doc.getEdge(effectId, causeId).getRelation());
            assertEquals(effect, doc.getEdge(effectId, causeId).getEffect().getElement());
            assertEquals(cause, doc.getEdge(effectId, causeId).getCause().getElement());
        }
    }


    @Test
    public void constructor_unfinishedRelationsMappingCause_handlesUnmappedRelations() {
        Document document = pF.newDocument();
        document.setNamespace(cPF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName entityId1 = pF.newQualifiedName("uri", "entity1", "ex");
        Entity entity1 = pF.newEntity(entityId1);
        bundle.getStatement().add(entity1);

        QualifiedName entityId2 = pF.newQualifiedName("uri", "entity2", "ex");


        QualifiedName relationId = pF.newQualifiedName("uri", "relation", "ex");
        WasDerivedFrom wasDerivedFrom = pF.newWasDerivedFrom(relationId, entityId1, entityId2);
        bundle.getStatement().add(wasDerivedFrom);

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        assertNotNull(doc.getNode(entityId1));
        assertNull(doc.getNode(entityId2));
        assertNotNull(doc.getEdge(relationId));
        assertNotNull(doc.getEdge(entityId1, entityId2));
        assertNull(doc.getEdge(entityId1, entityId2).getCause());
        assertNotNull(doc.getEdge(entityId1, entityId2).getEffect());
        assertEquals(wasDerivedFrom, doc.getEdge(relationId).getRelation());
        assertFalse(doc.areAllRelationsMapped());
        assertEquals(entity1, doc.getEdge(relationId).getEffect().getElement());
        assertNull(doc.getEdge(relationId).getCause());

        Entity entity2 = pF.newEntity(entityId2);
        bundle.getStatement().add(entity2);
        doc.doAction(entity2);

        assertTrue(doc.areAllRelationsMapped());
        assertEquals(entity2, doc.getEdge(relationId).getCause().getElement());
    }


    @Test
    public void constructor_unfinishedRelationsMappingEffect_handlesUnmappedRelations() {
        Document document = pF.newDocument();
        document.setNamespace(cPF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName entityId1 = pF.newQualifiedName("uri", "entity1", "ex");

        QualifiedName entityId2 = pF.newQualifiedName("uri", "entity2", "ex");
        Entity entity2 = pF.newEntity(entityId2);
        bundle.getStatement().add(entity2);

        QualifiedName relationId = pF.newQualifiedName("uri", "relation", "ex");
        WasDerivedFrom wasDerivedFrom = pF.newWasDerivedFrom(relationId, entityId1, entityId2);
        bundle.getStatement().add(wasDerivedFrom);

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        assertNull(doc.getNode(entityId1));
        assertNotNull(doc.getNode(entityId2));
        assertNotNull(doc.getEdge(relationId));
        assertNotNull(doc.getEdge(entityId1, entityId2));
        assertNotNull(doc.getEdge(entityId1, entityId2).getCause());
        assertNull(doc.getEdge(entityId1, entityId2).getEffect());
        assertEquals(wasDerivedFrom, doc.getEdge(relationId).getRelation());
        assertFalse(doc.areAllRelationsMapped());
        assertEquals(entity2, doc.getEdge(relationId).getCause().getElement());
        assertNull(doc.getEdge(relationId).getEffect());

        Entity entity1 = pF.newEntity(entityId1);
        bundle.getStatement().add(entity1);
        doc.doAction(entity1);

        assertTrue(doc.areAllRelationsMapped());
        assertEquals(entity1, doc.getEdge(relationId).getEffect().getElement());
    }


    @Test
    public void constructor_toDocument_returnsEquivalentDocument() {
        Document document = pF.newDocument();

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName entityId1 = pF.newQualifiedName("uri", "entity", "ex");
        Entity entity1 = pF.newEntity(entityId1);
        bundle.getStatement().add(entity1);

        QualifiedName entityId2 = pF.newQualifiedName("uri", "entity2", "ex");
        Entity entity2 = pF.newEntity(entityId2);
        bundle.getStatement().add(entity2);

        QualifiedName relationId = pF.newQualifiedName("uri", "relation", "ex");
        WasDerivedFrom wasDerivedFrom = pF.newWasDerivedFrom(relationId, entityId1, entityId2);
        bundle.getStatement().add(wasDerivedFrom);
        document.setNamespace(Namespace.gatherNamespaces(document));
        bundle.setNamespace(Namespace.gatherNamespaces(document));

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);
        Document resultDoc = doc.toDocument();

        Map<String, String> resultNs = resultDoc.getNamespace().getNamespaces();
        assertTrue(resultNs.entrySet().containsAll(document.getNamespace().getNamespaces().entrySet()));

        assertEquals(1, resultDoc.getStatementOrBundle().size());
        assertEquals(bundle.getKind(), resultDoc.getStatementOrBundle().getFirst().getKind());
        Bundle resultBundle = (Bundle) resultDoc.getStatementOrBundle().getFirst();
        assertEquals(bundle.getId(), resultBundle.getId());
        assertEquals(bundle.getStatement().size(), resultBundle.getStatement().size());
        assertEquals(new HashSet<>(bundle.getStatement()), new HashSet<>(resultBundle.getStatement()));
        assertNotNull(resultBundle.getNamespace());
        assertEquals(resultDoc.getNamespace(), resultBundle.getNamespace().getParent());
    }


    @Test
    public void constructor_hadMemberLast_returnsMappedHadMemberRelations() {
        Document document = pF.newDocument();
        document.setNamespace(cPF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName collectionId = pF.newQualifiedName("uri", "collection", "ex");
        Entity collection = pF.newEntity(collectionId);
        bundle.getStatement().add(collection);

        QualifiedName entityId1 = pF.newQualifiedName("uri", "entity", "ex");
        Entity entity1 = pF.newEntity(entityId1);
        bundle.getStatement().add(entity1);

        QualifiedName entityId2 = pF.newQualifiedName("uri", "entity2", "ex");
        Entity entity2 = pF.newEntity(entityId2);
        bundle.getStatement().add(entity2);

        HadMember hadMember = pF.newHadMember(collectionId, entityId1, entityId2);
        bundle.getStatement().add(hadMember);

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        assertNotNull(doc.getNode(entityId1));
        assertNotNull(doc.getNode(entityId2));
        assertNotNull(doc.getEdge(collectionId, entityId2));
        assertEquals(hadMember, doc.getEdge(collectionId, entityId1).getRelation());
        assertEquals(hadMember, doc.getEdge(collectionId, entityId2).getRelation());
        assertTrue(doc.areAllRelationsMapped());
    }


    @Test
    public void constructor_hadMemberFirst_returnsMappedHadMemberRelations() {
        Document document = pF.newDocument();
        document.setNamespace(cPF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName collectionId = pF.newQualifiedName("uri", "collection", "ex");
        Entity collection = pF.newEntity(collectionId);

        QualifiedName entityId1 = pF.newQualifiedName("uri", "entity", "ex");
        Entity entity1 = pF.newEntity(entityId1);

        QualifiedName entityId2 = pF.newQualifiedName("uri", "entity2", "ex");
        Entity entity2 = pF.newEntity(entityId2);

        HadMember hadMember = pF.newHadMember(collectionId, entityId1, entityId2);
        bundle.getStatement().add(hadMember);

        bundle.getStatement().addAll(List.of(collection, entity1, entity2));

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        assertNotNull(doc.getNode(entityId1));
        assertNotNull(doc.getNode(entityId2));
        assertNotNull(doc.getEdge(collectionId, entityId2));
        assertEquals(hadMember, doc.getEdge(collectionId, entityId1).getRelation());
        assertEquals(hadMember, doc.getEdge(collectionId, entityId2).getRelation());
        assertDoesNotThrow(() -> doc.getEdge(collectionId, collectionId));
        assertTrue(doc.areAllRelationsMapped());
    }


    @Test
    public void getEdges_unMappedHadMember_returnsCorrectEdge() {
        Document document = pF.newDocument();
        document.setNamespace(cPF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName collectionId = pF.newQualifiedName("uri", "collection", "ex");
        Entity collection = pF.newEntity(collectionId);

        QualifiedName entityId1 = pF.newQualifiedName("uri", "entity", "ex");
        Entity entity1 = pF.newEntity(entityId1);

        QualifiedName entityId2 = pF.newQualifiedName("uri", "entity2", "ex");

        HadMember hadMember = pF.newHadMember(collectionId, entityId1, entityId2);
        bundle.getStatement().add(hadMember);

        bundle.getStatement().addAll(List.of(collection, entity1));

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        assertFalse(doc.areAllRelationsMapped());
        assertEquals(1, doc.getEdges(collectionId, entityId2).size());
        assertNotSame(doc.getEdge(collectionId, entityId1), doc.getEdge(collectionId, entityId2));
    }


    @Test
    public void constructor_HadMemberToDocument_returnsExpectedDocument() {
        Document document = pF.newDocument();

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName collectionId = pF.newQualifiedName("uri", "collection", "ex");
        Entity collection = pF.newEntity(collectionId);
        bundle.getStatement().add(collection);

        QualifiedName entityId1 = pF.newQualifiedName("uri", "entity", "ex");
        Entity entity1 = pF.newEntity(entityId1);
        bundle.getStatement().add(entity1);

        QualifiedName entityId2 = pF.newQualifiedName("uri", "entity2", "ex");
        Entity entity2 = pF.newEntity(entityId2);
        bundle.getStatement().add(entity2);

        QualifiedName relationId = pF.newQualifiedName("uri", "relation", "ex");
        QualifiedHadMember hadMember = pF.newQualifiedHadMember(relationId, collectionId, List.of(entityId1, entityId2), Collections.emptyList());
        bundle.getStatement().add(hadMember);
        document.setNamespace(Namespace.gatherNamespaces(document));
        bundle.setNamespace(Namespace.gatherNamespaces(document));

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);
        Document resultDoc = doc.toDocument();

        Map<String, String> resultNs = resultDoc.getNamespace().getNamespaces();
        assertTrue(resultNs.entrySet().containsAll(document.getNamespace().getNamespaces().entrySet()));

        assertEquals(2, doc.getEdges(relationId).size());
        assertEquals(1, resultDoc.getStatementOrBundle().size());
        assertEquals(bundle.getKind(), resultDoc.getStatementOrBundle().getFirst().getKind());
        Bundle resultBundle = (Bundle) resultDoc.getStatementOrBundle().getFirst();
        assertEquals(bundle.getId(), resultBundle.getId());
        assertEquals(bundle.getStatement().size(), resultBundle.getStatement().size());
        assertEquals(new HashSet<>(bundle.getStatement()), new HashSet<>(resultBundle.getStatement()));
    }

    @Test
    public void getBackbonePartAndDSPart_validData_returnsBBAndDs() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity entity1 = cPF.getProvFactory().newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Entity entity2 = cPF.newCpmEntity(id2, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        QualifiedName id3 = cPF.newCpmQualifiedName("qN3");
        Agent agent = cPF.getProvFactory().newAgent(id3);

        QualifiedName id4 = cPF.newCpmQualifiedName("qN4");
        Entity entity4 = cPF.newCpmEntity(id4, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        Relation relation1 = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id1, id3);

        Relation relation2 = cPF.getProvFactory().newWasDerivedFrom(id2, id4);

        Relation relation3 = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id2, id3);

        Document document = pF.newDocument();
        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        bundle.getStatement().addAll(List.of(entity1, agent, entity2, entity4));
        bundle.getStatement().addAll(List.of(relation1, relation2, relation3));
        document.setNamespace(Namespace.gatherNamespaces(document));
        bundle.setNamespace(Namespace.gatherNamespaces(document));

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        List<INode> bb = doc.getBackbonePart();
        assertEquals(2, bb.size());
        assertTrue(bb.stream().anyMatch(x -> entity2.equals(x.getElement())));
        assertTrue(bb.stream().anyMatch(x -> entity4.equals(x.getElement())));
        INode first = bb.stream().filter(x -> Objects.equals(x.getElement(), entity4)).findAny().get();
        INode last = bb.stream().filter(x -> Objects.equals(x.getElement(), entity2)).findAny().get();
        assertEquals(1, first.getCauseEdges().size());
        assertEquals(0, last.getCauseEdges().size());
        assertEquals(relation2, first.getCauseEdges().getFirst().getRelation());
        assertSame(last, last.getEffectEdges().getLast().getEffect());
        assertSame(first, first.getCauseEdges().getFirst().getCause());

        List<INode> ds = doc.getDomainSpecificPart();
        assertEquals(2, ds.size());
        assertTrue(ds.stream().anyMatch(x -> entity1.equals(x.getElement())));
        assertTrue(ds.stream().anyMatch(x -> agent.equals(x.getElement())));
        first = ds.stream().filter(x -> Objects.equals(x.getElement(), entity1)).findAny().get();
        last = ds.stream().filter(x -> Objects.equals(x.getElement(), agent)).findAny().get();
        assertEquals(0, first.getCauseEdges().size());
        assertEquals(1, last.getCauseEdges().size());
        assertEquals(relation1, first.getEffectEdges().getFirst().getRelation());
        assertSame(first, first.getEffectEdges().getFirst().getEffect());
        assertSame(last, last.getCauseEdges().getFirst().getCause());

        List<IEdge> crossPart = doc.getCrossPartEdges();
        assertEquals(1, crossPart.size());
        assertEquals(relation3, crossPart.getFirst().getRelation());
    }


    @Test
    public void getSuccessors_validData_returnsExpectedNodes() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity entity1 = cPF.newCpmEntity(id1, CpmType.FORWARD_CONNECTOR, new ArrayList<>());

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Entity entity2 = cPF.newCpmEntity(id2, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        QualifiedName id3 = cPF.newCpmQualifiedName("qN3");
        Entity entity3 = cPF.newCpmEntity(id3, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        QualifiedName id4 = cPF.newCpmQualifiedName("qN4");
        Entity entity4 = cPF.newCpmEntity(id4, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        Relation relation1 = cPF.getProvFactory().newWasDerivedFrom(id4, id3);
        Relation relation2 = cPF.getProvFactory().newWasDerivedFrom(id3, id2);
        Relation relation3 = cPF.getProvFactory().newWasDerivedFrom(id2, id1);

        Document document = pF.newDocument();
        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        bundle.getStatement().addAll(List.of(entity1, entity2, entity3, entity4));
        bundle.getStatement().addAll(List.of(relation1, relation2, relation3));
        document.setNamespace(Namespace.gatherNamespaces(document));
        bundle.setNamespace(Namespace.gatherNamespaces(document));

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        List<INode> precursors = doc.getPrecursors(id1);
        assertEquals(3, precursors.size());
        assertTrue(precursors.stream().anyMatch(x -> entity2.equals(x.getElement())));
        assertTrue(precursors.stream().anyMatch(x -> entity3.equals(x.getElement())));
        assertTrue(precursors.stream().anyMatch(x -> entity4.equals(x.getElement())));

        assertEquals(0, doc.getPrecursors(id4).size());

        List<INode> successors = doc.getSuccessors(id4);
        assertEquals(3, successors.size());
        assertTrue(successors.stream().anyMatch(x -> entity1.equals(x.getElement())));
        assertTrue(successors.stream().anyMatch(x -> entity2.equals(x.getElement())));
        assertTrue(successors.stream().anyMatch(x -> entity3.equals(x.getElement())));

        assertEquals(0, doc.getSuccessors(id1).size());
    }


    @Test
    public void getRelatedNodes_invalidAndNullData_returnsNull() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Agent agent = cPF.newCpmAgent(id1, CpmType.SENDER_AGENT, new ArrayList<>());

        Document document = pF.newDocument();
        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        bundle.getStatement().add(agent);
        document.setNamespace(Namespace.gatherNamespaces(document));
        bundle.setNamespace(Namespace.gatherNamespaces(document));

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        List<INode> precursors = doc.getPrecursors(id1);
        assertNull(precursors);
        assertNull(doc.getSuccessors(null));
    }

    @Test
    public void setBundleId_validId_renamesBundle() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Agent agent = cPF.newCpmAgent(id1, CpmType.SENDER_AGENT, new ArrayList<>());

        Document document = pF.newDocument();
        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        bundle.getStatement().add(agent);
        document.setNamespace(Namespace.gatherNamespaces(document));
        bundle.setNamespace(Namespace.gatherNamespaces(document));

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);
        QualifiedName newId = pF.newQualifiedName("uri", "newName", "ex");
        doc.setBundleId(newId);
        Document output = doc.toDocument();
        assertEquals(newId, ((Bundle) output.getStatementOrBundle().getFirst()).getId());
    }


    @Test
    public void setBundleId_nullId_returnsNull() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Agent agent = cPF.newCpmAgent(id1, CpmType.SENDER_AGENT, new ArrayList<>());

        Document document = pF.newDocument();
        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        bundle.getStatement().add(agent);
        document.setNamespace(Namespace.gatherNamespaces(document));
        bundle.setNamespace(Namespace.gatherNamespaces(document));

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);
        doc.setBundleId(null);
        Document output = doc.toDocument();
        assertNull(((Bundle) output.getStatementOrBundle().getFirst()).getId());
    }


    @Test
    public void removeNode_nodeWithRelations_returnsTrue() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity entity = cPF.getProvFactory().newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Agent agent = cPF.getProvFactory().newAgent(id2);

        Relation relation1 = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(entity, agent, relation1), List.of(), bundleId, pF, cPF, cF);

        assertTrue(doc.removeNode(id1));
        assertTrue(doc.getNodes(id1).isEmpty());
        assertFalse(doc.areAllRelationsMapped());
        assertNull(doc.getEdge(id1, id2).getEffect());
        assertEquals(1, doc.getDomainSpecificPart().size());

        assertTrue(doc.removeNode(id2));
        assertTrue(doc.getNodes(id2).isEmpty());
        assertFalse(doc.areAllRelationsMapped());
        assertNull(doc.getEdge(id1, id2).getCause());
        assertEquals(0, doc.getDomainSpecificPart().size());

        doc.doAction(entity);
        doc.doAction(agent);
        assertTrue(doc.areAllRelationsMapped());
        assertNotNull(doc.getEdge(id1, id2).getEffect());
        assertEquals(entity, doc.getEdge(id1, id2).getEffect().getElement());
        assertNotNull(doc.getEdge(id1, id2).getCause());
        assertEquals(agent, doc.getEdge(id1, id2).getCause().getElement());
    }

    @Test
    public void removeNode_duplicateId_returnsFalse() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity entity1 = cPF.getProvFactory().newEntity(id1);

        Agent agent = cPF.getProvFactory().newAgent(id1);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(entity1, agent), List.of(), bundleId, pF, cPF, cF);

        assertFalse(doc.removeNode(id1));
    }

    @Test
    public void removeNode_nonExistentId_returnsFalse() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity entity1 = cPF.getProvFactory().newEntity(id1);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(entity1), List.of(), bundleId, pF, cPF, cF);

        assertFalse(doc.removeNode(cPF.newCpmQualifiedName("nonExistentId")));
    }


    @Test
    public void removeEdge_mappedRelation_returnsTrue() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity entity = cPF.getProvFactory().newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Agent agent = cPF.getProvFactory().newAgent(id2);

        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasAttributedTo relation1 = cPF.getProvFactory().newWasAttributedTo(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(entity, agent, relation1), List.of(), bundleId, pF, cPF, cF);

        assertTrue(doc.removeEdge(rel));
        assertNull(doc.getEdge(rel));
        assertNull(doc.getEdge(id1, id2));
        assertTrue(doc.areAllRelationsMapped());

        doc.doAction(relation1);
        assertTrue(doc.areAllRelationsMapped());
        assertNotNull(doc.getEdge(id1, id2));
        assertNotNull(doc.getEdge(rel));
    }

    @Test
    public void removeEdge_unmappedRelationCause_returnsTrue() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity entity = cPF.getProvFactory().newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");

        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasAttributedTo relation1 = cPF.getProvFactory().newWasAttributedTo(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(entity, relation1), List.of(), bundleId, pF, cPF, cF);

        assertFalse(doc.areAllRelationsMapped());
        assertTrue(doc.removeEdge(rel));
        assertNull(doc.getEdge(rel));
        assertNull(doc.getEdge(id1, id2));
        assertTrue(doc.areAllRelationsMapped());

        Agent agent = cPF.getProvFactory().newAgent(id2);
        doc.doAction(agent);
        assertTrue(doc.areAllRelationsMapped());
        assertTrue(doc.getNode(id2).getCauseEdges().isEmpty());
    }


    @Test
    public void removeEdge_unmappedRelationEffect_returnsTrue() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Agent agent = cPF.getProvFactory().newAgent(id2);

        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasAttributedTo relation1 = cPF.getProvFactory().newWasAttributedTo(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(agent, relation1), List.of(), bundleId, pF, cPF, cF);

        assertFalse(doc.areAllRelationsMapped());
        assertTrue(doc.removeEdge(rel));
        assertNull(doc.getEdge(rel));
        assertNull(doc.getEdge(id1, id2));
        assertTrue(doc.areAllRelationsMapped());

        Entity entity = cPF.getProvFactory().newEntity(id1);
        doc.doAction(entity);
        assertTrue(doc.areAllRelationsMapped());
        assertTrue(doc.getNode(id2).getEffectEdges().isEmpty());
    }


    @Test
    public void removeEdge_nonExistentId_returnsFalse() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");

        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasAttributedTo relation1 = cPF.getProvFactory().newWasAttributedTo(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(relation1), List.of(), bundleId, pF, cPF, cF);

        assertFalse(doc.removeEdge(cPF.newCpmQualifiedName("nonExistentId")));
        assertFalse(doc.removeEdge(cPF.newCpmQualifiedName("nonExistentId1"), cPF.newCpmQualifiedName("nonExistentId2")));
    }

    @Test
    public void addInfluence_withoutCauseAndEffect_returnsUnmappedInfluence() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");

        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasInfluencedBy inf = pF.newWasInfluencedBy(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(inf), List.of(), bundleId, pF, cPF, cF);

        assertNotNull(doc.getEdge(id1, id2));
        assertFalse(doc.areAllRelationsMapped());
    }

    @Test
    public void addInfluence_withOneEffectSecond_returnsUnmappedInfluence() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity entity = pF.newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");

        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasInfluencedBy inf = pF.newWasInfluencedBy(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(inf, entity), List.of(), bundleId, pF, cPF, cF);

        assertNotNull(doc.getEdge(id1, id2));
        assertEquals(doc.getNode(id1), doc.getEdge(id1, id2).getEffect());
        assertFalse(doc.areAllRelationsMapped());
    }


    @Test
    public void addInfluence_withOneCauseSecond_returnsUnmappedInfluence() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Entity entity = pF.newEntity(id2);

        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasInfluencedBy inf = pF.newWasInfluencedBy(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(inf, entity), List.of(), bundleId, pF, cPF, cF);

        assertNotNull(doc.getEdge(id1, id2));
        assertEquals(doc.getNode(id2), doc.getEdge(id1, id2).getCause());
        assertFalse(doc.areAllRelationsMapped());
    }


    @Test
    public void addInfluence_effectFirstInfluenceSecondCauseThird_returnsMappedInfluence() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity cause = pF.newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Entity effect = pF.newEntity(id2);

        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasInfluencedBy inf = pF.newWasInfluencedBy(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(effect, inf, cause), List.of(), bundleId, pF, cPF, cF);

        assertNotNull(doc.getEdge(id1, id2));
        assertEquals(doc.getNode(id1), doc.getEdge(id1, id2).getEffect());
        assertEquals(doc.getNode(id2), doc.getEdge(id1, id2).getCause());
        assertTrue(doc.getNode(id1).getEffectEdges().contains(doc.getEdge(id1, id2)));
        assertTrue(doc.getNode(id2).getCauseEdges().contains(doc.getEdge(id1, id2)));
        assertTrue(doc.areAllRelationsMapped());
    }


    @Test
    public void addInfluence_influenceFirstEffectSecondCauseThird_returnsMappedInfluence() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity cause = pF.newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Entity effect = pF.newEntity(id2);

        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasInfluencedBy inf = pF.newWasInfluencedBy(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(inf, effect, cause), List.of(), bundleId, pF, cPF, cF);

        assertNotNull(doc.getEdge(id1, id2));
        assertEquals(doc.getNode(id1), doc.getEdge(id1, id2).getEffect());
        assertEquals(doc.getNode(id2), doc.getEdge(id1, id2).getCause());
        assertTrue(doc.getNode(id1).getEffectEdges().contains(doc.getEdge(id1, id2)));
        assertTrue(doc.getNode(id2).getCauseEdges().contains(doc.getEdge(id1, id2)));
        assertTrue(doc.areAllRelationsMapped());
    }

    @Test
    public void addInfluence_effectFirstCauseSecondInfluenceThird_returnsMappedInfluence() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity cause = pF.newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Entity effect = pF.newEntity(id2);

        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasInfluencedBy inf = pF.newWasInfluencedBy(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(effect, cause, inf), List.of(), bundleId, pF, cPF, cF);

        assertNotNull(doc.getEdge(id1, id2));
        assertEquals(doc.getNode(id1), doc.getEdge(id1, id2).getEffect());
        assertEquals(doc.getNode(id2), doc.getEdge(id1, id2).getCause());
        assertTrue(doc.getNode(id1).getEffectEdges().contains(doc.getEdge(id1, id2)));
        assertTrue(doc.getNode(id2).getCauseEdges().contains(doc.getEdge(id1, id2)));
        assertTrue(doc.areAllRelationsMapped());
    }

    @Test
    public void addInfluence_oneEffectTwoCauses_returnsMappedInfluence() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity cause = pF.newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Entity effect = pF.newEntity(id2);

        Agent agent = pF.newAgent(id2);

        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasInfluencedBy inf = pF.newWasInfluencedBy(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(effect, cause, inf, agent), List.of(), bundleId, pF, cPF, cF);

        assertNotNull(doc.getEdges(rel));
        assertEquals(2, doc.getEdges(id1, id2).size());
        assertEquals(2, doc.getNodes(id2).size());
        assertEquals(2, doc.getNode(id1).getEffectEdges().size());
        assertEquals(1, doc.getNode(id2, StatementOrBundle.Kind.PROV_ENTITY).getCauseEdges().size());
        assertEquals(1, doc.getNode(id2, StatementOrBundle.Kind.PROV_AGENT).getCauseEdges().size());
        assertTrue(doc.areAllRelationsMapped());
    }


    @Test
    public void addInfluence_oneCauseTwoEffects_returnsMappedInfluence() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity cause = pF.newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Entity effect = pF.newEntity(id2);

        Agent agent = pF.newAgent(id1);

        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasInfluencedBy inf = pF.newWasInfluencedBy(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(effect, cause, inf, agent), List.of(), bundleId, pF, cPF, cF);

        assertNotNull(doc.getEdges(rel));
        assertEquals(2, doc.getEdges(id1, id2).size());
        assertEquals(2, doc.getNodes(id1).size());
        assertEquals(2, doc.getNode(id2).getCauseEdges().size());
        assertEquals(1, doc.getNode(id1, StatementOrBundle.Kind.PROV_ENTITY).getEffectEdges().size());
        assertEquals(1, doc.getNode(id1, StatementOrBundle.Kind.PROV_AGENT).getEffectEdges().size());
        assertTrue(doc.areAllRelationsMapped());
    }

    @Test
    public void addInfluence_twoEffectZeroCauseInfLast_returnsMappedInfluence() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity cause = pF.newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");

        Agent agent1 = pF.newAgent(id1);

        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasInfluencedBy inf = pF.newWasInfluencedBy(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(cause, agent1, inf), List.of(), bundleId, pF, cPF, cF);

        assertNotNull(doc.getEdges(rel));
        assertEquals(2, doc.getEdges(id1, id2).size());
        assertEquals(2, doc.getNodes(id1).size());
        assertEquals(0, doc.getNodes(id2).size());
        assertEquals(1, doc.getNode(id1, StatementOrBundle.Kind.PROV_ENTITY).getEffectEdges().size());
        assertEquals(1, doc.getNode(id1, StatementOrBundle.Kind.PROV_AGENT).getEffectEdges().size());
        ;
        assertFalse(doc.areAllRelationsMapped());
    }


    @Test
    public void addInfluence_twoCauseZeroEffectInfLast_returnsMappedInfluence() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Entity effect = pF.newEntity(id2);

        Agent agent2 = pF.newAgent(id2);

        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasInfluencedBy inf = pF.newWasInfluencedBy(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(effect, agent2, inf), List.of(), bundleId, pF, cPF, cF);

        assertNotNull(doc.getEdges(rel));
        assertEquals(2, doc.getEdges(id1, id2).size());
        assertEquals(0, doc.getNodes(id1).size());
        assertEquals(2, doc.getNodes(id2).size());
        assertEquals(1, doc.getNode(id2, StatementOrBundle.Kind.PROV_ENTITY).getCauseEdges().size());
        assertEquals(1, doc.getNode(id2, StatementOrBundle.Kind.PROV_AGENT).getCauseEdges().size());
        assertFalse(doc.areAllRelationsMapped());
    }


    @Test
    public void addInfluence_twoCauseTwoEffects_returnsMappedInfluence() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity cause = pF.newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Entity effect = pF.newEntity(id2);

        Agent agent1 = pF.newAgent(id1);
        Agent agent2 = pF.newAgent(id2);

        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasInfluencedBy inf = pF.newWasInfluencedBy(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(effect, agent1, inf, cause, agent2), List.of(), bundleId, pF, cPF, cF);

        assertNotNull(doc.getEdges(rel));
        assertEquals(4, doc.getEdges(id1, id2).size());
        assertEquals(2, doc.getNodes(id1).size());
        assertEquals(2, doc.getNodes(id2).size());
        assertEquals(2, doc.getNode(id2, StatementOrBundle.Kind.PROV_ENTITY).getCauseEdges().size());
        assertEquals(2, doc.getNode(id1, StatementOrBundle.Kind.PROV_ENTITY).getEffectEdges().size());
        assertEquals(2, doc.getNode(id2, StatementOrBundle.Kind.PROV_AGENT).getCauseEdges().size());
        assertEquals(2, doc.getNode(id1, StatementOrBundle.Kind.PROV_AGENT).getEffectEdges().size());
        assertTrue(doc.areAllRelationsMapped());
    }


    @Test
    public void addInfluence_threeCauseThreeEffects_returnsMappedInfluence() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity cause = pF.newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Entity effect = pF.newEntity(id2);

        Agent agent1 = pF.newAgent(id1);
        Agent agent2 = pF.newAgent(id2);
        Activity activity1 = pF.newActivity(id1);
        Activity activity2 = pF.newActivity(id2);


        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasInfluencedBy inf = pF.newWasInfluencedBy(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(inf, cause, agent1, effect, agent2, activity1), List.of(), bundleId, pF, cPF, cF);

        assertNotNull(doc.getEdges(rel));
        assertEquals(6, doc.getEdges(id1, id2).size());

        doc.doAction(activity2);

        assertEquals(9, doc.getEdges(id1, id2).size());
        assertEquals(9, doc.getEdges(id1, id2).stream().distinct().toList().size());
        assertEquals(3, doc.getNodes(id1).size());
        assertEquals(3, doc.getNodes(id2).size());
        assertEquals(3, doc.getNode(id1, StatementOrBundle.Kind.PROV_ENTITY).getEffectEdges().size());
        assertEquals(3, doc.getNode(id2, StatementOrBundle.Kind.PROV_ENTITY).getCauseEdges().size());
        assertEquals(3, doc.getNode(id1, StatementOrBundle.Kind.PROV_AGENT).getEffectEdges().size());
        assertEquals(3, doc.getNode(id2, StatementOrBundle.Kind.PROV_AGENT).getCauseEdges().size());
        assertEquals(3, doc.getNode(id1, StatementOrBundle.Kind.PROV_ACTIVITY).getEffectEdges().size());
        assertEquals(3, doc.getNode(id2, StatementOrBundle.Kind.PROV_ACTIVITY).getCauseEdges().size());
        assertTrue(doc.areAllRelationsMapped());
    }


    @Test
    public void removeInfluence_removeThenAddCauseAndEffect_returnsMappedInfluence() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity cause = pF.newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Entity effect = pF.newEntity(id2);

        QualifiedName rel = cPF.newCpmQualifiedName("rel");
        WasInfluencedBy inf = pF.newWasInfluencedBy(rel, id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(inf), List.of(), bundleId, pF, cPF, cF);

        assertTrue(doc.removeEdge(rel));

        doc.doAction(cause);
        assertTrue(doc.getNode(id1).getCauseEdges().isEmpty());
        assertNull(doc.getEdge(id1, id2));

        doc.doAction(effect);
        assertTrue(doc.getNode(id2).getEffectEdges().isEmpty());
        assertNull(doc.getEdge(id1, id2));
    }
}