package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.exception.NoSpecificKind;
import cz.muni.fi.cpm.vannila.CpmFactory;
import org.junit.jupiter.api.BeforeEach;
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

public class CpmDocumentTest {
    private DatatypeFactory datatypeFactory;
    private ProvFactory pF;
    private CpmFactory cF;
    private ProvUtilities u;

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
//                new Object[]{pF.newWasInfluencedBy(pF.newQualifiedName("uri", "wasInfluencedBy", "ex"), entityId1, agentId1)},
                new Object[]{pF.newSpecializationOf(entityId1, entityId2)},
                new Object[]{pF.newQualifiedSpecializationOf(pF.newQualifiedName("uri", "qualifiedSpecializationOf", "ex"), entityId1, entityId2, Collections.emptyList())},
                new Object[]{pF.newQualifiedAlternateOf(pF.newQualifiedName("uri", "qualifiedAlternateOf", "ex"), entityId1, entityId2, Collections.emptyList())}
        );
    }

    @BeforeEach
    public void setUp() throws Exception {
        pF = new ProvFactory();
        cF = new CpmFactory(pF);
        u = new ProvUtilities();
        datatypeFactory = DatatypeFactory.newInstance();
    }

    @Test
    public void constructor_nullDocument_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new CpmDocument(null, pF, cF));
    }

    @Test
    public void constructor_withoutBundle_throwsIllegalArgumentException() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        assertThrows(IllegalArgumentException.class, () -> new CpmDocument(document, pF, cF));
    }


    @Test
    public void constructor_namespace_returnsExpectedNamespaces() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        CpmDocument doc = new CpmDocument(document, pF, cF);

        assertEquals(document.getNamespace().getNamespaces(), doc.getNamespaces().getNamespaces());
    }

    @Test
    public void constructor_mainActivity_returnsExpectedMainActivity() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName activityId = pF.newQualifiedName("uri", "activity", "ex");
        XMLGregorianCalendar startTime = datatypeFactory.newXMLGregorianCalendar("2024-11-13T10:00:00");
        XMLGregorianCalendar endTime = datatypeFactory.newXMLGregorianCalendar("2024-11-13T12:00:00");
        CpmType type = CpmType.MAIN_ACTIVITY;
        Collection<Attribute> attributes = new ArrayList<>();
        Activity activity = cF.newCpmActivity(activityId, startTime, endTime, type, attributes);
        bundle.getStatement().add(activity);

        CpmDocument doc = new CpmDocument(document, pF, cF);

        assertNotNull(doc.getNode(activityId));
        assertFalse(doc.getBackbonePart().isEmpty());
        assertNotNull(doc.getMainActivity());
        assertEquals(activity, doc.getNode(activityId).getElement());
    }


    @Test
    public void constructor_regularActivity_returnsNull() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName activityId = pF.newQualifiedName("uri", "activity", "ex");
        Activity activity = pF.newActivity(activityId);
        bundle.getStatement().add(activity);

        CpmDocument doc = new CpmDocument(document, pF, cF);

        assertNotNull(doc.getNode(activityId));
        assertTrue(doc.getBackbonePart().isEmpty());
        assertNull(doc.getMainActivity());
    }


    @Test
    public void constructor_forwardConnector_returnsExpectedForwardConnectors() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName entityId = pF.newQualifiedName("uri", "entity", "ex");
        CpmType type = CpmType.FORWARD_CONNECTOR;
        Collection<Attribute> attributes = new ArrayList<>();
        Entity entity = cF.newCpmEntity(entityId, type, attributes);
        bundle.getStatement().add(entity);

        CpmDocument doc = new CpmDocument(document, pF, cF);

        assertNotNull(doc.getNode(entityId));
        assertFalse(doc.getBackbonePart().isEmpty());
        assertFalse(doc.getForwardConnectors().isEmpty());
        assertEquals(entity, doc.getNode(entityId).getElement());
    }


    @Test
    public void constructor_regularEntity_returnsExpectedRegularEntity() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName entityId = pF.newQualifiedName("uri", "entity", "ex");
        Entity entity = pF.newEntity(entityId);
        bundle.getStatement().add(entity);

        CpmDocument doc = new CpmDocument(document, pF, cF);

        assertNotNull(doc.getNode(entityId));
        assertTrue(doc.getBackbonePart().isEmpty());
        assertTrue(doc.getForwardConnectors().isEmpty());
        assertEquals(entity, doc.getNode(entityId).getElement());
    }


    @Test
    public void constructor_identicalIdSameKind_returnsExpectedTypes() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName entityId = pF.newQualifiedName("uri", "entity", "ex");
        Entity entity1 = pF.newEntity(entityId);
        bundle.getStatement().add(entity1);

        Entity entity2 = pF.newEntity(entityId);
        QualifiedName typeId = pF.newQualifiedName("uri", "type", "ex");
        Type type = pF.newType("", typeId);
        entity2.getType().add(type);
        bundle.getStatement().add(entity2);

        CpmDocument doc = new CpmDocument(document, pF, cF);

        assertNotNull(doc.getNode(entityId));
        assertTrue(doc.getBackbonePart().isEmpty());
        assertTrue(doc.getForwardConnectors().isEmpty());
        assertFalse(doc.getNode(entityId).getElement().getType().isEmpty());
        assertEquals(type, doc.getNode(entityId).getElement().getType().getFirst());
    }


    @Test
    public void constructor_identicalIdDifferentKind_returnsExpectedTypes() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName identicalId = pF.newQualifiedName("uri", "id", "ex");

        Entity entity = pF.newEntity(identicalId);
        bundle.getStatement().add(entity);

        Activity activity = pF.newActivity(identicalId);
        bundle.getStatement().add(activity);

        CpmDocument doc = new CpmDocument(document, pF, cF);

        assertThrows(IllegalStateException.class, () -> doc.getNode(identicalId));
        assertEquals(2, doc.getNodes(identicalId).size());
        assertEquals(entity, doc.getNode(identicalId, StatementOrBundle.Kind.PROV_ENTITY).getElement());
        assertEquals(activity, doc.getNode(identicalId, StatementOrBundle.Kind.PROV_ACTIVITY).getElement());
    }


    @Test
    public void constructor_senderAgent_returnsExpectedSenderAgent() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName agentId = pF.newQualifiedName("uri", "agent", "ex");
        CpmType type = CpmType.SENDER_AGENT;
        Collection<Attribute> attributes = new ArrayList<>();
        Agent agent = cF.newCpmAgent(agentId, type, attributes);
        bundle.getStatement().add(agent);

        CpmDocument doc = new CpmDocument(document, pF, cF);

        assertNotNull(doc.getNode(agentId));
        assertFalse(doc.getBackbonePart().isEmpty());
        assertEquals(agent, doc.getNode(agentId).getElement());
    }


    @Test
    public void constructor_regularAgent_returnsExpectedRegularAgent() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName agentId = pF.newQualifiedName("uri", "agent", "ex");
        Agent agent = pF.newAgent(agentId);
        bundle.getStatement().add(agent);

        CpmDocument doc = new CpmDocument(document, pF, cF);

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
        document.setNamespace(cF.newCpmNamespace());

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

        CpmDocument doc = new CpmDocument(document, pF, cF);

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
        document.setNamespace(cF.newCpmNamespace());

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

        CpmDocument doc = new CpmDocument(document, pF, cF);

        assertNotNull(doc.getNode(entityId1));
        assertNull(doc.getNode(entityId2));
        assertNotNull(doc.getEdge(relationId));
        assertNull(doc.getEdge(entityId1, entityId2));
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
        document.setNamespace(cF.newCpmNamespace());

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

        CpmDocument doc = new CpmDocument(document, pF, cF);

        assertNull(doc.getNode(entityId1));
        assertNotNull(doc.getNode(entityId2));
        assertNotNull(doc.getEdge(relationId));
        assertNull(doc.getEdge(entityId2));
        assertNull(doc.getEdge(entityId1, entityId2));
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

        CpmDocument doc = new CpmDocument(document, pF, cF);
        Document resultDoc = doc.toDocument();

        assertEquals(document.getNamespace().getNamespaces(), resultDoc.getNamespace().getNamespaces());

        assertEquals(1, resultDoc.getStatementOrBundle().size());
        assertEquals(bundle.getKind(), resultDoc.getStatementOrBundle().getFirst().getKind());
        Bundle resultBundle = (Bundle) resultDoc.getStatementOrBundle().getFirst();
        assertEquals(bundle.getId(), resultBundle.getId());
        assertEquals(bundle.getStatement().size(), resultBundle.getStatement().size());
        assertEquals(new HashSet<>(bundle.getStatement()), new HashSet<>(resultBundle.getStatement()));
        assertEquals(document.getNamespace().getNamespaces(), resultDoc.getNamespace().getNamespaces());
        assertNotNull(resultBundle.getNamespace());
        assertEquals(resultDoc.getNamespace(), resultBundle.getNamespace().getParent());
    }


    @Test
    public void constructor_hadMember_returnsMappedHadMemberRelations() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

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

        CpmDocument doc = new CpmDocument(document, pF, cF);

        assertNotNull(doc.getNode(entityId1));
        assertNotNull(doc.getNode(entityId2));
        assertNotNull(doc.getEdge(collectionId, entityId2));
        assertEquals(hadMember, doc.getEdge(collectionId, entityId1).getRelation());
        assertEquals(hadMember, doc.getEdge(collectionId, entityId2).getRelation());
        assertTrue(doc.areAllRelationsMapped());
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

        CpmDocument doc = new CpmDocument(document, pF, cF);
        Document resultDoc = doc.toDocument();

        assertEquals(document.getNamespace().getNamespaces(), resultDoc.getNamespace().getNamespaces());

        assertNotNull(doc.getEdge(relationId));
        assertEquals(1, resultDoc.getStatementOrBundle().size());
        assertEquals(bundle.getKind(), resultDoc.getStatementOrBundle().getFirst().getKind());
        Bundle resultBundle = (Bundle) resultDoc.getStatementOrBundle().getFirst();
        assertEquals(bundle.getId(), resultBundle.getId());
        assertEquals(bundle.getStatement().size(), resultBundle.getStatement().size());
        assertEquals(new HashSet<>(bundle.getStatement()), new HashSet<>(resultBundle.getStatement()));
    }

    @Test
    public void getBackbonePartAndDSPart_validData_returnsBBAndDs() {
        QualifiedName id1 = cF.newCpmQualifiedName("qN1");
        Entity entity1 = cF.getProvFactory().newEntity(id1);

        QualifiedName id2 = cF.newCpmQualifiedName("qN2");
        Entity entity2 = cF.newCpmEntity(id2, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        QualifiedName id3 = cF.newCpmQualifiedName("qN3");
        Agent agent = cF.getProvFactory().newAgent(id3);

        QualifiedName id4 = cF.newCpmQualifiedName("qN4");
        Entity entity4 = cF.newCpmEntity(id4, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        Relation relation1 = cF.getProvFactory().newWasAttributedTo(cF.newCpmQualifiedName("attr"), id1, id3);

        Relation relation2 = cF.getProvFactory().newWasDerivedFrom(id2, id4);

        Relation relation3 = cF.getProvFactory().newWasAttributedTo(cF.newCpmQualifiedName("attr"), id2, id3);

        Document document = pF.newDocument();
        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        bundle.getStatement().addAll(List.of(entity1, agent, entity2, entity4));
        bundle.getStatement().addAll(List.of(relation1, relation2, relation3));
        document.setNamespace(Namespace.gatherNamespaces(document));
        bundle.setNamespace(Namespace.gatherNamespaces(document));

        CpmDocument doc = new CpmDocument(document, pF, cF);

        List<INode> bb = doc.getBackbonePart();
        assertEquals(2, bb.size());
        assertTrue(bb.stream().anyMatch(x -> entity2.equals(x.getElement())));
        assertTrue(bb.stream().anyMatch(x -> entity4.equals(x.getElement())));
        assertEquals(0, bb.getFirst().getCauseEdges().size());
        assertEquals(1, bb.getFirst().getEffectEdges().size());
        assertEquals(relation2, bb.getFirst().getEffectEdges().getFirst().getRelation());

        List<INode> ds = doc.getDomainSpecificPart();
        assertEquals(2, ds.size());
        assertTrue(ds.stream().anyMatch(x -> entity1.equals(x.getElement())));
        assertTrue(ds.stream().anyMatch(x -> agent.equals(x.getElement())));
        assertEquals(0, ds.getFirst().getCauseEdges().size());
        assertEquals(1, ds.getFirst().getEffectEdges().size());
        assertEquals(relation1, ds.getFirst().getEffectEdges().getFirst().getRelation());

        List<IEdge> crossPart = doc.getCrossPartEdges();
        assertEquals(1, crossPart.size());
        assertEquals(relation3, crossPart.getFirst().getRelation());
    }


    @Test
    public void getSuccessors_validData_returnsExpectedNodes() {
        QualifiedName id1 = cF.newCpmQualifiedName("qN1");
        Entity entity1 = cF.newCpmEntity(id1, CpmType.FORWARD_CONNECTOR, new ArrayList<>());

        QualifiedName id2 = cF.newCpmQualifiedName("qN2");
        Entity entity2 = cF.newCpmEntity(id2, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        QualifiedName id3 = cF.newCpmQualifiedName("qN3");
        Entity entity3 = cF.newCpmEntity(id3, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        QualifiedName id4 = cF.newCpmQualifiedName("qN4");
        Entity entity4 = cF.newCpmEntity(id4, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        Relation relation1 = cF.getProvFactory().newWasDerivedFrom(id4, id3);
        Relation relation2 = cF.getProvFactory().newWasDerivedFrom(id3, id2);
        Relation relation3 = cF.getProvFactory().newWasDerivedFrom(id2, id1);

        Document document = pF.newDocument();
        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        bundle.getStatement().addAll(List.of(entity1, entity2, entity3, entity4));
        bundle.getStatement().addAll(List.of(relation1, relation2, relation3));
        document.setNamespace(Namespace.gatherNamespaces(document));
        bundle.setNamespace(Namespace.gatherNamespaces(document));

        CpmDocument doc = new CpmDocument(document, pF, cF);

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
        QualifiedName id1 = cF.newCpmQualifiedName("qN1");
        Agent agent = cF.newCpmAgent(id1, CpmType.SENDER_AGENT, new ArrayList<>());

        Document document = pF.newDocument();
        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        bundle.getStatement().add(agent);
        document.setNamespace(Namespace.gatherNamespaces(document));
        bundle.setNamespace(Namespace.gatherNamespaces(document));

        CpmDocument doc = new CpmDocument(document, pF, cF);

        List<INode> precursors = doc.getPrecursors(id1);
        assertNull(precursors);
        assertNull(doc.getSuccessors(null));
    }
}