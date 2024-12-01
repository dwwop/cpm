package cz.muni.fi.cpm.model;


import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.CpmType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.Attribute;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.vanilla.ProvFactory;
import org.openprovenance.prov.vanilla.QualifiedName;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CpmUtilitiesTest {
    private ProvFactory pF;

    @BeforeEach
    public void setUp() {
        pF = new ProvFactory();
    }

    @Test
    public void testIsCpmElement_withValidCpmElement() {
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                "validQualifiedName",
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));

        assertTrue(CpmUtilities.isCpmElement(element));
    }

    @Test
    public void testIsCpmElement_withInvalidUri() {

        QualifiedName validQualifiedName = new QualifiedName(
                "invalidUri",
                "validQualifiedName",
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));

        assertFalse(CpmUtilities.isCpmElement(element));
    }


    @Test
    public void testIsCpmElement_withInvalidPrefix() {

        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                "validQualifiedName",
                "invalidPrefix");

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));

        assertFalse(CpmUtilities.isCpmElement(element));
    }


    @Test
    public void testIsCpmElement_invalidBoth() {
        QualifiedName validQualifiedName = new QualifiedName(
                "invalidUri",
                "validQualifiedName",
                "invalidPrefix");

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));

        assertFalse(CpmUtilities.isCpmElement(element));
    }

    @Test
    public void testIsCpmElement_withoutType() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id, (List<Attribute>) null);

        assertFalse(CpmUtilities.isCpmElement(element));
    }


    @Test
    public void testHasCpmType_withValidCpmElement() {
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                CpmType.FORWARD_CONNECTOR.toString(),
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));

        assertTrue(CpmUtilities.hasCpmType(element, CpmType.FORWARD_CONNECTOR));
    }

    @Test
    public void testHasCpmType_withInvalidUri() {
        QualifiedName validQualifiedName = new QualifiedName(
                "invalidUri",
                CpmType.FORWARD_CONNECTOR.toString(),
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));

        assertFalse(CpmUtilities.hasCpmType(element, CpmType.FORWARD_CONNECTOR));
    }


    @Test
    public void testHasCpmType_withInvalidPrefix() {
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                CpmType.FORWARD_CONNECTOR.toString(),
                "invalidPrefix");

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));

        assertFalse(CpmUtilities.hasCpmType(element, CpmType.FORWARD_CONNECTOR));
    }


    @Test
    public void testHasCpmType_invalidType() {
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                "invalidType",
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));

        assertFalse(CpmUtilities.hasCpmType(element, CpmType.FORWARD_CONNECTOR));
    }

    @Test
    public void testHasCpmType_invalidAll() {
        QualifiedName validQualifiedName = new QualifiedName(
                "invalidUri",
                "validQualifiedName",
                "invalidPrefix");

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));

        assertFalse(CpmUtilities.hasCpmType(element, CpmType.FORWARD_CONNECTOR));
    }


    @Test
    public void testHasCpmType_withoutType() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id, (List<Attribute>) null);

        assertFalse(CpmUtilities.hasCpmType(element, CpmType.FORWARD_CONNECTOR));
    }

}
