package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.vannila.CpmFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.*;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


public class CpmFactoryTest {

    private CpmFactory cF;
    private DatatypeFactory datatypeFactory;

    @BeforeEach
    public void setUp() throws Exception {
        cF = new CpmFactory();
        datatypeFactory = DatatypeFactory.newInstance();
    }

    @Test
    public void newCpmType_withValidType_returnsExpectedType() {
        CpmType type = CpmType.FORWARD_CONNECTOR;
        Type result = cF.newCpmType(type);
        assertNotNull(result);
        assertEquals(type.toString(), ((QualifiedName) result.getValue()).getLocalPart());
    }

    @Test
    public void newCpmQualifiedName_withValidLocalName_returnsExpectedQualifiedName() {
        String local = CpmAttributeConstants.COMMENT;
        QualifiedName result = cF.newCpmQualifiedName(local);
        assertNotNull(result);
        assertEquals(CpmNamespaceConstants.CPM_NS, result.getNamespaceURI());
        assertEquals(CpmNamespaceConstants.CPM_PREFIX, result.getPrefix());
        assertEquals(local, result.getLocalPart());
    }

    @Test
    public void newCpmAttribute_withType_returnsExpectedAttribute() {
        String local = CpmAttributeConstants.REFERENCED_BUNDLE_ID;
        Object value = "exampleValue";
        Attribute result = cF.newCpmAttribute(local, value);
        assertNotNull(result);
        assertEquals(local, result.getElementName().getLocalPart());
        assertEquals(value, result.getValue());
    }

    @Test
    public void newCpmAttribute_withCustomType_returnsExpectedCustomAttribute() {
        String local = CpmAttributeConstants.REFERENCED_BUNDLE_ID;
        Object value = "exampleValue";
        QualifiedName type = cF.newCpmQualifiedName(CpmType.IDENTIFIER.toString());
        Attribute result = cF.newCpmAttribute(local, value, type);
        assertNotNull(result);
        assertEquals(local, result.getElementName().getLocalPart());
        assertEquals(value, result.getValue());
        assertEquals(type, result.getType());
    }

    @Test
    public void newCpmEntity_withValidParameters_returnsExpectedEntity() {
        QualifiedName id = cF.newCpmQualifiedName("entityId");
        CpmType type = CpmType.FORWARD_CONNECTOR;
        Collection<Attribute> attributes = new ArrayList<>();
        Entity result = cF.newCpmEntity(id, type, attributes);
        assertNotNull(result);
        assertTrue(attributes.contains(cF.newCpmType(type)));
    }

    @Test
    public void newCpmActivity_withValidParameters_returnsExpectedActivity() {
        QualifiedName id = cF.newCpmQualifiedName("activityId");
        XMLGregorianCalendar startTime = datatypeFactory.newXMLGregorianCalendar("2024-11-13T10:00:00");
        XMLGregorianCalendar endTime = datatypeFactory.newXMLGregorianCalendar("2024-11-13T12:00:00");
        CpmType type = CpmType.MAIN_ACTIVITY;
        Collection<Attribute> attributes = new ArrayList<>();
        Activity result = cF.newCpmActivity(id, startTime, endTime, type, attributes);
        assertNotNull(result);
        assertTrue(attributes.contains(cF.newCpmType(type)));
    }

    @Test
    public void newCpmAgent_withValidParameters_returnsExpectedAgent() {
        QualifiedName id = cF.newCpmQualifiedName("agentId");
        CpmType type = CpmType.SENDER_AGENT;
        Collection<Attribute> attributes = new ArrayList<>();
        Agent result = cF.newCpmAgent(id, type, attributes);
        assertNotNull(result);
        assertTrue(attributes.contains(cF.newCpmType(type)));
    }

    @Test
    public void newBBNode_edgeWithBBNode_returnsExpectedBBNode() {
        QualifiedName id1 = cF.newCpmQualifiedName("qN1");
        Entity entity = cF.newCpmEntity(id1, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        QualifiedName id2 = cF.newCpmQualifiedName("qN2");
        Entity entity2 = cF.newCpmEntity(id1, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        QualifiedName id3 = cF.newCpmQualifiedName("qN3");
        Entity entity3 = cF.getProvFactory().newEntity(id3);

        Relation relation = cF.getProvFactory().newWasDerivedFrom(id1, id2);

        INode node = cF.newNode(entity);
        INode node2 = cF.newNode(entity2);

        IEdge edge = cF.newEdge(relation);
        edge.setCause(node);
        edge.setEffect(node2);

        node.getCauseEdges().add(edge);

        Relation relation2 = cF.getProvFactory().newWasDerivedFrom(id1, id3);
        INode node3 = cF.newNode(entity3);

        IEdge edge2 = cF.newEdge(relation2);
        edge2.setCause(node);
        edge2.setEffect(node3);

        node.getCauseEdges().add(edge2);

        INode output = cF.newNode(node);

        assertNotNull(output);
        assertEquals(0, output.getCauseEdges().size());
        assertEquals(0, output.getEffectEdges().size());
    }
}
