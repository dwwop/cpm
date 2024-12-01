package cz.muni.fi.cpm.model;


import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.vannila.CpmFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openprovenance.prov.model.Attribute;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.vanilla.ProvFactory;
import org.openprovenance.prov.vanilla.QualifiedName;

import javax.xml.datatype.DatatypeFactory;
import java.util.Collections;
import java.util.List;


public class CpmUtilitiesTest {
    private DatatypeFactory datatypeFactory;
    private ProvFactory pF;
    private CpmFactory cF;

    @Before
    public void setUp() throws Exception {
        pF = new ProvFactory();
        cF = new CpmFactory(pF);
        datatypeFactory = DatatypeFactory.newInstance();
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

        Assert.assertTrue(CpmUtilities.isCpmElement(element));
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


        Assert.assertFalse(CpmUtilities.isCpmElement(element));
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


        Assert.assertFalse(CpmUtilities.isCpmElement(element));
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


        Assert.assertFalse(CpmUtilities.isCpmElement(element));
    }

    @Test
    public void testIsCpmElement_withoutType() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id, (List<Attribute>) null);

        Assert.assertFalse(CpmUtilities.isCpmElement(element));
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

        Assert.assertTrue(CpmUtilities.hasCpmType(element, CpmType.FORWARD_CONNECTOR));
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

        Assert.assertFalse(CpmUtilities.hasCpmType(element, CpmType.FORWARD_CONNECTOR));
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

        Assert.assertFalse(CpmUtilities.hasCpmType(element, CpmType.FORWARD_CONNECTOR));
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

        Assert.assertFalse(CpmUtilities.hasCpmType(element, CpmType.FORWARD_CONNECTOR));
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

        Assert.assertFalse(CpmUtilities.hasCpmType(element, CpmType.FORWARD_CONNECTOR));
    }


    @Test
    public void testHasCpmType_withoutType() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id, (List<Attribute>) null);

        Assert.assertFalse(CpmUtilities.hasCpmType(element, CpmType.FORWARD_CONNECTOR));
    }

}
