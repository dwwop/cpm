package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.vannila.CpmFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.model.extension.QualifiedHadMember;
import org.openprovenance.prov.vanilla.ProvFactory;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CpmDocumentTest {
    private DatatypeFactory datatypeFactory;
    private ProvFactory pF;
    private CpmFactory cF;

    @BeforeEach
    public void setUp() throws Exception {
        pF = new ProvFactory();
        cF = new CpmFactory(pF);
        datatypeFactory = DatatypeFactory.newInstance();
    }


    @Test
    public void testConstructor_nullDocument() {
        assertThrows(IllegalArgumentException.class, () -> new CpmDocument(null, pF));
    }

    @Test
    public void testConstructor_withoutBundle() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        assertThrows(IllegalArgumentException.class, () -> new CpmDocument(document, pF));
    }


    @Test
    public void testConstructor_namespace() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        CpmDocument doc = new CpmDocument(document, pF);

        assertEquals(document.getNamespace().getNamespaces(), doc.getNamespaces().getNamespaces());
    }

    @Test
    public void testConstructor_mainActivity() {
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

        CpmDocument doc = new CpmDocument(document, pF);

        assertNotNull(doc.getNode(activityId));
        assertFalse(doc.getBackbone().isEmpty());
        assertNotNull(doc.getMainActivity());
        assertEquals(activity, doc.getNode(activityId).getElement());
    }


    @Test
    public void testConstructor_regularActivity() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName activityId = pF.newQualifiedName("uri", "activity", "ex");
        Activity activity = pF.newActivity(activityId);
        bundle.getStatement().add(activity);

        CpmDocument doc = new CpmDocument(document, pF);

        assertNotNull(doc.getNode(activityId));
        assertTrue(doc.getBackbone().isEmpty());
        assertThrows(RuntimeException.class, () -> doc.getMainActivity());
    }


    @Test
    public void testConstructor_forwardConnector() {
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

        CpmDocument doc = new CpmDocument(document, pF);

        assertNotNull(doc.getNode(entityId));
        assertFalse(doc.getBackbone().isEmpty());
        assertFalse(doc.getForwardConnectors().isEmpty());
        assertEquals(entity, doc.getNode(entityId).getElement());
    }


    @Test
    public void testConstructor_regularEntity() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName entityId = pF.newQualifiedName("uri", "entity", "ex");
        Entity entity = pF.newEntity(entityId);
        bundle.getStatement().add(entity);

        CpmDocument doc = new CpmDocument(document, pF);

        assertNotNull(doc.getNode(entityId));
        assertTrue(doc.getBackbone().isEmpty());
        assertTrue(doc.getForwardConnectors().isEmpty());
        assertEquals(entity, doc.getNode(entityId).getElement());
    }


    @Test
    public void testConstructor_senderAgent() {
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

        CpmDocument doc = new CpmDocument(document, pF);

        assertNotNull(doc.getNode(agentId));
        assertFalse(doc.getBackbone().isEmpty());
        assertEquals(agent, doc.getNode(agentId).getElement());
    }


    @Test
    public void testConstructor_regularAgent() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName agentId = pF.newQualifiedName("uri", "agent", "ex");
        Agent agent = pF.newAgent(agentId);
        bundle.getStatement().add(agent);

        CpmDocument doc = new CpmDocument(document, pF);

        assertNotNull(doc.getNode(agentId));
        assertTrue(doc.getBackbone().isEmpty());
        assertEquals(agent, doc.getNode(agentId).getElement());
    }

    @Test
    public void testConstructor_validRelationsMapping() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

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

        CpmDocument doc = new CpmDocument(document, pF);

        assertNotNull(doc.getNode(entityId1));
        assertNotNull(doc.getNode(entityId2));
        assertNotNull(doc.getEdge(relationId));
        assertNotNull(doc.getEdge(entityId1, entityId2));
        assertEquals(wasDerivedFrom, doc.getEdge(relationId).getRelation());
        assertTrue(doc.areAllRelationsMapped());
        assertEquals(entity1, doc.getEdge(relationId).getSource().getElement());
        assertEquals(entity2, doc.getEdge(relationId).getTarget().getElement());
        assertEquals(wasDerivedFrom, doc.getNode(entityId1).getSourceEdges().getFirst().getRelation());
        assertEquals(wasDerivedFrom, doc.getNode(entityId2).getTargetEdges().getFirst().getRelation());
    }


    @Test
    public void testConstructor_unfinishedRelationsMappingTarget() {
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

        CpmDocument doc = new CpmDocument(document, pF);

        assertNotNull(doc.getNode(entityId1));
        assertNull(doc.getNode(entityId2));
        assertNotNull(doc.getEdge(relationId));
        assertNull(doc.getEdge(entityId1, entityId2));
        assertEquals(wasDerivedFrom, doc.getEdge(relationId).getRelation());
        assertFalse(doc.areAllRelationsMapped());
        assertEquals(entity1, doc.getEdge(relationId).getSource().getElement());
        assertNull(doc.getEdge(relationId).getTarget());

        Entity entity2 = pF.newEntity(entityId2);
        bundle.getStatement().add(entity2);
        doc.doAction(entity2);

        assertTrue(doc.areAllRelationsMapped());
        assertEquals(entity2, doc.getEdge(relationId).getTarget().getElement());
    }


    @Test
    public void testConstructor_unfinishedRelationsMappingSource() {
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

        CpmDocument doc = new CpmDocument(document, pF);

        assertNull(doc.getNode(entityId1));
        assertNotNull(doc.getNode(entityId2));
        assertNotNull(doc.getEdge(relationId));
        assertNull(doc.getEdge(entityId2));
        assertNull(doc.getEdge(entityId1, entityId2));
        assertEquals(wasDerivedFrom, doc.getEdge(relationId).getRelation());
        assertFalse(doc.areAllRelationsMapped());
        assertEquals(entity2, doc.getEdge(relationId).getTarget().getElement());
        assertNull(doc.getEdge(relationId).getSource());

        Entity entity1 = pF.newEntity(entityId1);
        bundle.getStatement().add(entity1);
        doc.doAction(entity1);

        assertTrue(doc.areAllRelationsMapped());
        assertEquals(entity1, doc.getEdge(relationId).getSource().getElement());
    }


    @Test
    public void testConstructor_toDocument() {
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

        CpmDocument doc = new CpmDocument(document, pF);
        Document resultDoc = doc.toDocument();

        assertEquals(document.getNamespace().getNamespaces(), resultDoc.getNamespace().getNamespaces());

        assertEquals(1, resultDoc.getStatementOrBundle().size());
        assertEquals(bundle.getKind(), resultDoc.getStatementOrBundle().getFirst().getKind());
        Bundle resultBundle = (Bundle) resultDoc.getStatementOrBundle().getFirst();
        assertEquals(bundle.getId(), resultBundle.getId());
        assertEquals(bundle.getStatement().size(), resultBundle.getStatement().size());
        assertEquals(new HashSet<>(bundle.getStatement()), new HashSet<>(resultBundle.getStatement()));
        assertNotNull(resultBundle.getNamespace());
        assertEquals(resultDoc.getNamespace(), resultBundle.getNamespace().getParent());
        assertEquals(bundle.getNamespace().getNamespaces(), resultBundle.getNamespace().getNamespaces());
    }


    @Test
    public void testConstructor_hadMember() {
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

        CpmDocument doc = new CpmDocument(document, pF);

        assertNotNull(doc.getNode(entityId1));
        assertNotNull(doc.getNode(entityId2));
        assertNotNull(doc.getEdge(collectionId, entityId2));
        assertEquals(hadMember, doc.getEdge(collectionId, entityId1).getRelation());
        assertEquals(hadMember, doc.getEdge(collectionId, entityId2).getRelation());
        assertTrue(doc.areAllRelationsMapped());
    }


    @Test
    public void testConstructor_HadMemberToDocument() {
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

        CpmDocument doc = new CpmDocument(document, pF);
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
}