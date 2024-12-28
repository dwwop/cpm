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
    public void isCpmElement_withValidCpmElement_returnsTrue() {
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
    public void isCpmElement_withInvalidUri_returnsFalse() {

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
    public void isCpmElement_withInvalidPrefix_returnsFalse() {

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
    public void isCpmElement_withInvalidUriAndPrefix_returnsFalse() {
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
    public void isCpmElement_withoutType_returnsFalse() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id, (List<Attribute>) null);

        assertFalse(CpmUtilities.isCpmElement(element));
    }


    @Test
    public void hasCpmType_withValidCpmElementAndType_returnsTrue() {
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
    public void hasCpmType_withInvalidUri_returnsFalse() {
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
    public void hasCpmType_withInvalidPrefix_returnsFalse() {
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
    public void hasCpmType_withInvalidType_returnsFalse() {
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
    public void hasCpmType_withInvalidUriAndPrefix_returnsFalse() {
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
    public void hasCpmType_withoutType_returnsFalse() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id, (List<Attribute>) null);

        assertFalse(CpmUtilities.hasCpmType(element, CpmType.FORWARD_CONNECTOR));
    }

}
