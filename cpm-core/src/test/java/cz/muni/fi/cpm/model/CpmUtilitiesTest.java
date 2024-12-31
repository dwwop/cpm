package cz.muni.fi.cpm.model;


import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.vannila.CpmFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openprovenance.prov.model.Attribute;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.vanilla.ProvFactory;
import org.openprovenance.prov.vanilla.QualifiedName;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CpmUtilitiesTest {
    private ProvFactory pF;
    private CpmFactory cF;

    private static Stream<Object[]> provideConnectorTypes() {
        return Stream.of(
                new Object[]{CpmType.FORWARD_CONNECTOR.toString()},
                new Object[]{CpmType.BACKWARD_CONNECTOR.toString()}
        );
    }

    @BeforeEach
    public void setUp() {
        pF = new ProvFactory();
        cF = new CpmFactory();
    }

    @Test
    public void isBackbone_nullElement_returnsFalse() {
        assertFalse(CpmUtilities.isBackbone(null));
    }

    @Test
    public void isBackbone_withValidBackbone_returnsTrue() {
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                "validQualifiedName",
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));

        assertTrue(CpmUtilities.isBackbone(element));
    }

    @Test
    public void isBackbone_withInvalidUri_returnsFalse() {

        QualifiedName invalidNS = new QualifiedName(
                "invalidUri",
                "validQualifiedName",
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(invalidNS, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));

        assertFalse(CpmUtilities.isBackbone(element));
    }

    @Test
    public void isBackbone_withInvalidPrefix_returnsFalse() {

        QualifiedName invalidPrefix = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                "validQualifiedName",
                "invalidPrefix");

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(invalidPrefix, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));

        assertFalse(CpmUtilities.isBackbone(element));
    }

    @Test
    public void isBackbone_withInvalidUriAndPrefix_returnsFalse() {
        QualifiedName invalidQualifiedName = new QualifiedName(
                "invalidUri",
                "invalidQualifiedName",
                "invalidPrefix");

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(invalidQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));

        assertFalse(CpmUtilities.isBackbone(element));
    }

    @Test
    public void isBackbone_withoutType_returnsFalse() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id, (List<Attribute>) null);

        assertFalse(CpmUtilities.isBackbone(element));
    }

    @Test
    public void isBackbone_withInvalidOtherAttr_returnsFalse() {
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                "validQualifiedName",
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);


        QualifiedName invalidQualifiedName = new QualifiedName(
                "invalidUri",
                "invalidQualifiedName",
                "invalidPrefix");
        Attribute other = pF.newOther(invalidQualifiedName, "", pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, List.of(attribute, other));

        assertFalse(CpmUtilities.isBackbone(element));
    }

    @Test
    public void isBackbone_withValidOtherAttr_returnsTrue() {
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                "validQualifiedName",
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);
        Attribute other = cF.newCpmAttribute(CpmAttributeConstants.PROVENANCE_SERVICE_URI, "", pF.getName().XSD_ANY_URI);

        Element element = pF.newEntity(id, List.of(attribute, other));

        assertTrue(CpmUtilities.isBackbone(element));
    }

    @Test
    public void hasCpmType_nullElementAndNullType_returnsFalse() {
        assertFalse(CpmUtilities.hasCpmType(null, null));
    }

    @Test
    public void hasCpmType_withValidCpmElementAndNullType_returnsFalse() {
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                CpmType.FORWARD_CONNECTOR.toString(),
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));

        assertFalse(CpmUtilities.hasCpmType(element, null));
    }

    @Test
    public void hasCpmType_withNullElementAndValidType_returnsFalse() {
        assertFalse(CpmUtilities.hasCpmType(null, CpmType.BACKWARD_CONNECTOR));
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

    @ParameterizedTest
    @MethodSource("provideConnectorTypes")
    public void isConnector_withValidCpmElementAndType_returnsTrue(String cpmType) {
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                cpmType,
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));

        assertTrue(CpmUtilities.isConnector(element));
    }

    @Test
    public void isConnector_invalidType_returnsFalse() {
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                CpmType.MAIN_ACTIVITY.toString(),
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));

        assertFalse(CpmUtilities.isConnector(element));
    }
}
