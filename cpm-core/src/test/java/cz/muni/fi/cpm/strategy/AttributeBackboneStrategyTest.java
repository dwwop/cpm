package cz.muni.fi.cpm.strategy;

import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.merged.CpmMergedFactory;
import cz.muni.fi.cpm.model.IBackboneStrategy;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import cz.muni.fi.cpm.model.IEdge;
import cz.muni.fi.cpm.model.INode;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.Attribute;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.Relation;
import org.openprovenance.prov.vanilla.ProvFactory;
import org.openprovenance.prov.vanilla.QualifiedName;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AttributeBackboneStrategyTest {
    IBackboneStrategy strategy;

    private ProvFactory pF;
    private CpmMergedFactory cF;
    private ICpmProvFactory cPF;

    @BeforeEach
    public void setUp() {
        pF = new ProvFactory();
        cF = new CpmMergedFactory();
        cPF = new CpmProvFactory();
        strategy = new AttributeBackboneStrategy();
    }

    private boolean hasNonBackboneAttributes(INode node) {
        try {
            Method method = AttributeBackboneStrategy.class.getDeclaredMethod("hasNonBackboneAttributes", INode.class);
            method.setAccessible(true);
            return (boolean) method.invoke(null, node);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            fail();
            throw new RuntimeException(e);
        }
    }


    @Test
    public void hasNonBackboneAttributes_returnsFalse() {
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                "validQualifiedName",
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));
        INode node = cF.newNode(element);

        assertFalse(hasNonBackboneAttributes(node));
    }

    @Test
    public void hasNonBackboneAttributes_withInvalidUri_returnsTrue() {

        QualifiedName invalidNS = new QualifiedName(
                "invalidUri",
                "validQualifiedName",
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(invalidNS, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));
        INode node = cF.newNode(element);

        assertTrue(hasNonBackboneAttributes(node));
    }

    @Test
    public void hasNonBackboneAttributes_withInvalidPrefix_returnsTrue() {

        QualifiedName invalidPrefix = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                "validQualifiedName",
                "invalidPrefix");

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(invalidPrefix, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));
        INode node = cF.newNode(element);

        assertTrue(hasNonBackboneAttributes(node));
    }

    @Test
    public void hasNonBackboneAttributes_withInvalidUriAndPrefix_returnsTrue() {
        QualifiedName invalidQualifiedName = new QualifiedName(
                "invalidUri",
                "invalidQualifiedName",
                "invalidPrefix");

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(invalidQualifiedName, pF.getName().PROV_QUALIFIED_NAME);

        Element element = pF.newEntity(id, Collections.singletonList(attribute));
        INode node = cF.newNode(element);

        assertTrue(hasNonBackboneAttributes(node));
    }

    @Test
    public void hasNonBackboneAttributes_withoutType_returnsFalse() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id, (List<Attribute>) null);
        INode node = cF.newNode(element);

        assertFalse(hasNonBackboneAttributes(node));
    }

    @Test
    public void hasNonBackboneAttributes_withInvalidOtherAttr_returnsTrue() {
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

        assertTrue(hasNonBackboneAttributes(node));
    }

    @Test
    public void hasNonBackboneAttributes_withValidOtherAttr_returnsFalse() {
        QualifiedName validQualifiedName = new QualifiedName(
                CpmNamespaceConstants.CPM_NS,
                "validQualifiedName",
                CpmNamespaceConstants.CPM_PREFIX);

        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");
        Attribute attribute = pF.newType(validQualifiedName, pF.getName().PROV_QUALIFIED_NAME);
        Attribute other = cPF.newCpmAttribute(CpmAttributeConstants.PROVENANCE_SERVICE_URI, "", pF.getName().XSD_ANY_URI);

        Element element = pF.newEntity(id, List.of(attribute, other));
        INode node = cF.newNode(element);

        assertFalse(hasNonBackboneAttributes(node));
    }


    @Test
    public void hasNonBackboneAttributes_nullElement_returnsFalse() {
        assertFalse(strategy.isBackbone(null));
    }

    @Test
    public void isBackbone_withoutCpmType_returnsFalse() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Element element = pF.newEntity(id);
        INode node = cF.newNode(element);

        assertFalse(strategy.isBackbone(node));
    }


    @Test
    public void isBackbone_withCpmType_returnsTrue() {
        org.openprovenance.prov.model.QualifiedName id = pF.newQualifiedName("uri", "entity", "ex");

        Attribute cpmType = cPF.newCpmType(CpmType.BACKWARD_CONNECTOR);
        Element element = pF.newEntity(id, Collections.singletonList(cpmType));
        INode node = cF.newNode(element);

        assertTrue(strategy.isBackbone(node));
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

        assertFalse(strategy.isBackbone(node));
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

        assertFalse(strategy.isBackbone(node));
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

        assertTrue(strategy.isBackbone(node));
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

        assertFalse(strategy.isBackbone(node));
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

        assertFalse(strategy.isBackbone(node));
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

        assertFalse(strategy.isBackbone(node));
    }
}