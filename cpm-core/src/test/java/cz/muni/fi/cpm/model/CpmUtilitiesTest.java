package cz.muni.fi.cpm.model;


import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.merged.CpmMergedFactory;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
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
    private CpmMergedFactory cF;
    private ICpmProvFactory cPF;

    private static Stream<Object[]> provideConnectorTypes() {
        return Stream.of(
                new Object[]{CpmType.FORWARD_CONNECTOR.toString()},
                new Object[]{CpmType.BACKWARD_CONNECTOR.toString()}
        );
    }

    @BeforeEach
    public void setUp() {
        pF = new ProvFactory();
        cF = new CpmMergedFactory();
        cPF = new CpmProvFactory();
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
        INode node = cF.newNode(element);

        assertFalse(CpmUtilities.hasCpmType(node, null));
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
        INode node = cF.newNode(element);

        assertTrue(CpmUtilities.hasCpmType(node, CpmType.FORWARD_CONNECTOR));
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
        INode node = cF.newNode(element);

        assertFalse(CpmUtilities.hasCpmType(node, CpmType.FORWARD_CONNECTOR));
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
        INode node = cF.newNode(element);

        assertFalse(CpmUtilities.hasCpmType(node, CpmType.FORWARD_CONNECTOR));
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
        INode node = cF.newNode(element);

        assertFalse(CpmUtilities.hasCpmType(node, CpmType.FORWARD_CONNECTOR));
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
        INode node = cF.newNode(element);

        assertFalse(CpmUtilities.hasCpmType(node, CpmType.FORWARD_CONNECTOR));
    }

    @Test
    public void hasCpmType_withoutType_returnsFalse() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id, (List<Attribute>) null);
        INode node = cF.newNode(element);

        assertFalse(CpmUtilities.hasCpmType(node, CpmType.FORWARD_CONNECTOR));
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
        INode node = cF.newNode(element);

        assertTrue(CpmUtilities.isConnector(node));
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
        INode node = cF.newNode(element);

        assertFalse(CpmUtilities.isConnector(node));
    }


    @Test
    public void hasAnyCpmType_hasType_returnsTrue() {
        for (CpmType type : CpmType.values()) {
            org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

            Attribute cpmType = cPF.newCpmType(type);
            Element element = pF.newEntity(id, Collections.singletonList(cpmType));
            INode node = cF.newNode(element);

            assertTrue(CpmUtilities.hasAnyCpmType(node));
        }
    }


    @Test
    public void hasAnyCpmType_noType_returnsFalse() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id);
        INode node = cF.newNode(element);

        assertFalse(CpmUtilities.hasAnyCpmType(node));
    }


    @Test
    public void hasAnyCpmType_invalidType_returnsFalse() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Attribute invalidType = pF.newType(
                cPF.newCpmQualifiedName("invalid"),
                pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(invalidType));
        INode node = cF.newNode(element);

        assertFalse(CpmUtilities.hasAnyCpmType(node));
    }
}
