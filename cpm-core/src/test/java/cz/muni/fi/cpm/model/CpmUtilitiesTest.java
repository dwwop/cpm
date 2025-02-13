package cz.muni.fi.cpm.model;


import cz.muni.fi.cpm.constants.CpmAttributeConstants;
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
import org.openprovenance.prov.model.Relation;
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
    public void containsOnlyCPMAttributes_nullElement_returnsFalse() {
        assertFalse(CpmUtilities.isBackbone(null));
    }

    @Test
    public void containsOnlyCPMAttributes_returnsTrue() {
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                "validQualifiedName",
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));
        INode node = cF.newNode(element);

        assertTrue(CpmUtilities.containsOnlyCPMAttributes(node));
    }

    @Test
    public void containsOnlyCPMAttributes_withInvalidUri_returnsFalse() {

        QualifiedName invalidNS = new QualifiedName(
                "invalidUri",
                "validQualifiedName",
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(invalidNS, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));
        INode node = cF.newNode(element);

        assertFalse(CpmUtilities.containsOnlyCPMAttributes(node));
    }

    @Test
    public void containsOnlyCPMAttributes_withInvalidPrefix_returnsFalse() {

        QualifiedName invalidPrefix = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                "validQualifiedName",
                "invalidPrefix");

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(invalidPrefix, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));
        INode node = cF.newNode(element);

        assertFalse(CpmUtilities.containsOnlyCPMAttributes(node));
    }

    @Test
    public void containsOnlyCPMAttributes_withInvalidUriAndPrefix_returnsFalse() {
        QualifiedName invalidQualifiedName = new QualifiedName(
                "invalidUri",
                "invalidQualifiedName",
                "invalidPrefix");

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(invalidQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));
        INode node = cF.newNode(element);

        assertFalse(CpmUtilities.containsOnlyCPMAttributes(node));
    }

    @Test
    public void containsOnlyCPMAttributes_withoutType_returnsTrue() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id, (List<Attribute>) null);
        INode node = cF.newNode(element);

        assertTrue(CpmUtilities.containsOnlyCPMAttributes(node));
    }

    @Test
    public void containsOnlyCPMAttributes_withInvalidOtherAttr_returnsFalse() {
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
        INode node = cF.newNode(element);

        assertFalse(CpmUtilities.containsOnlyCPMAttributes(node));
    }

    @Test
    public void containsOnlyCPMAttributes_withValidOtherAttr_returnsTrue() {
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                "validQualifiedName",
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);
        Attribute other = cPF.newCpmAttribute(CpmAttributeConstants.PROVENANCE_SERVICE_URI, "", pF.getName().XSD_ANY_URI);

        Element element = pF.newEntity(id, List.of(attribute, other));
        INode node = cF.newNode(element);

        assertTrue(CpmUtilities.containsOnlyCPMAttributes(node));
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
    public void isBackbone_withoutCpmType_returnsFalse() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id);
        INode node = cF.newNode(element);

        assertFalse(CpmUtilities.isBackbone(node));
    }


    @Test
    public void isBackbone_withCpmType_returnsTrue() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Attribute cpmType = cPF.newCpmType(CpmType.BACKWARD_CONNECTOR);
        Element element = pF.newEntity(id, Collections.singletonList(cpmType));
        INode node = cF.newNode(element);

        assertTrue(CpmUtilities.isBackbone(node));
    }


    @Test
    public void isBackbone_withCpmTypeAndInvalidSecType_returnsFalse() {
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                "validQualifiedName",
                CpmNamespaceConstants.CPM_PREFIX);

        Attribute cpmType = cPF.newCpmType(CpmType.BACKWARD_CONNECTOR);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, List.of(attribute, cpmType));
        INode node = cF.newNode(element);

        assertFalse(CpmUtilities.isBackbone(node));
    }


    @Test
    public void isBackbone_withInvalidSecType_returnsFalse() {
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                "validQualifiedName",
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, List.of(attribute));
        INode node = cF.newNode(element);

        assertFalse(CpmUtilities.isBackbone(node));
    }


    @Test
    public void isBackbone_withGeneralNodeContainingFW_returnsTrue() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id);
        INode node = cF.newNode(element);

        org.openprovenance.prov.model.QualifiedName genId = pF.newQualifiedName("uri", "genEntity", "ex");

        Attribute cpmType = cPF.newCpmType(CpmType.FORWARD_CONNECTOR);
        Element genElement = pF.newEntity(genId, Collections.singletonList(cpmType));
        INode genNode = cF.newNode(genElement);

        Relation rel = pF.newSpecializationOf(id, genId);
        IEdge edge = cF.newEdge(rel);
        edge.setCause(genNode);
        genNode.getCauseEdges().add(edge);
        edge.setEffect(node);
        node.getEffectEdges().add(edge);

        assertTrue(CpmUtilities.isBackbone(node));
    }

    @Test
    public void isBackbone_withGeneralNodeContainingOtherCpmType_returnsFalse() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id);
        INode node = cF.newNode(element);

        org.openprovenance.prov.model.QualifiedName genId = pF.newQualifiedName("uri", "genEntity", "ex");

        Attribute cpmType = cPF.newCpmType(CpmType.BACKWARD_CONNECTOR);
        Element genElement = pF.newEntity(genId, Collections.singletonList(cpmType));
        INode genNode = cF.newNode(genElement);

        Relation rel = pF.newSpecializationOf(id, genId);
        IEdge edge = cF.newEdge(rel);
        edge.setCause(genNode);
        genNode.getCauseEdges().add(edge);
        edge.setEffect(node);
        node.getEffectEdges().add(edge);

        assertFalse(CpmUtilities.isBackbone(node));
    }

    @Test
    public void isBackbone_withGeneralNodeContainingCpmTypeAndInvalidAttr_returnsFalse() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id);
        INode node = cF.newNode(element);

        org.openprovenance.prov.model.QualifiedName genId = pF.newQualifiedName("uri", "genEntity", "ex");

        Attribute cpmType = cPF.newCpmType(CpmType.BACKWARD_CONNECTOR);
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                "validQualifiedName",
                CpmNamespaceConstants.CPM_PREFIX);

        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element genElement = pF.newEntity(genId, List.of(cpmType, attribute));
        INode genNode = cF.newNode(genElement);

        Relation rel = pF.newSpecializationOf(id, genId);
        IEdge edge = cF.newEdge(rel);
        edge.setCause(genNode);
        genNode.getCauseEdges().add(edge);
        edge.setEffect(node);
        node.getEffectEdges().add(edge);

        assertFalse(CpmUtilities.isBackbone(node));
    }


    @Test
    public void isBackbone_with2GeneralNodes_returnsFalse() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id);
        INode node = cF.newNode(element);

        org.openprovenance.prov.model.QualifiedName genId = pF.newQualifiedName("uri", "genEntity", "ex");

        Element genElement = pF.newEntity(genId);
        INode genNode = cF.newNode(genElement);

        org.openprovenance.prov.model.QualifiedName genId2 = pF.newQualifiedName("uri", "genEntity2", "ex");

        Attribute cpmType2 = cPF.newCpmType(CpmType.BACKWARD_CONNECTOR);
        Element genElement2 = pF.newEntity(genId, Collections.singletonList(cpmType2));
        INode genNode2 = cF.newNode(genElement2);

        Relation rel = pF.newSpecializationOf(id, genId);
        IEdge edge = cF.newEdge(rel);
        edge.setCause(genNode);
        genNode.getCauseEdges().add(edge);
        edge.setEffect(node);
        node.getEffectEdges().add(edge);

        Relation rel2 = pF.newSpecializationOf(genId, genId2);
        IEdge edge2 = cF.newEdge(rel2);
        edge2.setCause(genNode2);
        genNode2.getCauseEdges().add(edge2);
        edge2.setEffect(genNode);
        genNode.getEffectEdges().add(edge2);

        assertFalse(CpmUtilities.isBackbone(node));
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
