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
}
