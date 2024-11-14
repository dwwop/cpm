package cz.muni.fi.cpm;

import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.CpmTypeConstants;
import org.junit.Before;
import org.junit.Test;
import org.openprovenance.prov.model.*;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

public class CpmFactoryTest {

    private CpmFactory cF;
    private DatatypeFactory datatypeFactory;

    @Before
    public void setUp() throws Exception {
        cF = new CpmFactory();
        datatypeFactory = DatatypeFactory.newInstance();
    }

    @Test
    public void testNewCpmType() {
        String type = CpmTypeConstants.FORWARD_CONNECTOR;
        Type result = cF.newCpmType(type);
        assertNotNull(result);
        assertEquals(type, ((QualifiedName) result.getValue()).getLocalPart());
    }

    @Test
    public void testNewCpmQualifiedName() {
        String local = CpmAttributeConstants.COMMENT;
        QualifiedName result = cF.newCpmQualifiedName(local);
        assertNotNull(result);
        assertEquals(CpmNamespaceConstants.CPM_NS, result.getNamespaceURI());
        assertEquals(CpmNamespaceConstants.CPM_PREFIX, result.getPrefix());
        assertEquals(local, result.getLocalPart());
    }

    @Test
    public void testNewCpmAttribute_withType() {
        String local = CpmAttributeConstants.REFERENCED_BUNDLE_ID;
        Object value = "exampleValue";
        Attribute result = cF.newCpmAttribute(local, value);
        assertNotNull(result);
        assertEquals(local, result.getElementName().getLocalPart());
        assertEquals(value, result.getValue());
    }

    @Test
    public void testNewCpmAttribute_withCustomType() {
        String local = CpmAttributeConstants.REFERENCED_BUNDLE_ID;
        Object value = "exampleValue";
        QualifiedName type = cF.newCpmQualifiedName(CpmTypeConstants.IDENTIFIER);
        Attribute result = cF.newCpmAttribute(local, value, type);
        assertNotNull(result);
        assertEquals(local, result.getElementName().getLocalPart());
        assertEquals(value, result.getValue());
        assertEquals(type, result.getType());
    }

    @Test
    public void testNewCpmEntity() {
        QualifiedName id = cF.newCpmQualifiedName("entityId");
        String type = CpmTypeConstants.FORWARD_CONNECTOR;
        Collection<Attribute> attributes = new ArrayList<>();
        Entity result = cF.newCpmEntity(id, type, attributes);
        assertNotNull(result);
        assertTrue(attributes.contains(cF.newCpmType(type)));
    }

    @Test
    public void testNewCpmActivity() throws Exception {
        QualifiedName id = cF.newCpmQualifiedName("activityId");
        XMLGregorianCalendar startTime = datatypeFactory.newXMLGregorianCalendar("2024-11-13T10:00:00");
        XMLGregorianCalendar endTime = datatypeFactory.newXMLGregorianCalendar("2024-11-13T12:00:00");
        String type = CpmTypeConstants.MAIN_ACTIVITY;
        Collection<Attribute> attributes = new ArrayList<>();
        Activity result = cF.newCpmActivity(id, startTime, endTime, type, attributes);
        assertNotNull(result);
        assertTrue(attributes.contains(cF.newCpmType(type)));
    }

    @Test
    public void testNewCpmAgent() {
        QualifiedName id = cF.newCpmQualifiedName("agentId");
        String type = CpmTypeConstants.SENDER_AGENT;
        Collection<Attribute> attributes = new ArrayList<>();
        Agent result = cF.newCpmAgent(id, type, attributes);
        assertNotNull(result);
        assertTrue(attributes.contains(cF.newCpmType(type)));
    }
}
