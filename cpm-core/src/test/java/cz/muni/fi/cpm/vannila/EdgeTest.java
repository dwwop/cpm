package cz.muni.fi.cpm.vannila;

import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.model.ICpmFactory;
import cz.muni.fi.cpm.model.IEdge;
import cz.muni.fi.cpm.model.INode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Relation;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EdgeTest {
    private ICpmFactory cF;

    @BeforeEach
    void setUp() {
        cF = new CpmFactory();
    }


    @Test
    public void isType_nullData_returnsFalse() {
        QualifiedName id1 = cF.newCpmQualifiedName("qN1");

        QualifiedName id2 = cF.newCpmQualifiedName("qN2");

        Relation relation = cF.getProvFactory().newWasDerivedFrom(id1, id2);

        IEdge edge = cF.newEdge(relation);

        assertFalse(edge.isBackbone());
        assertFalse(edge.isDomainSpecific());
        assertFalse(edge.isCrossPart());
    }

    @Test
    public void isBackbone_validData_returnsTrue() {
        QualifiedName id1 = cF.newCpmQualifiedName("qN1");
        Entity entity = cF.newCpmEntity(id1, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        QualifiedName id2 = cF.newCpmQualifiedName("qN2");
        Entity entity2 = cF.newCpmEntity(id1, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        Relation relation = cF.getProvFactory().newWasDerivedFrom(id1, id2);

        INode node = cF.newNode(entity);
        INode node2 = cF.newNode(entity2);

        IEdge edge = cF.newEdge(relation);
        edge.setCause(node);
        edge.setEffect(node2);

        assertTrue(edge.isBackbone());
        assertFalse(edge.isDomainSpecific());
        assertFalse(edge.isCrossPart());
    }


    @Test
    public void isDomainSpecific_validData_returnsTrue() {
        QualifiedName id1 = cF.newCpmQualifiedName("qN1");
        Entity entity = cF.getProvFactory().newEntity(id1);

        QualifiedName id2 = cF.newCpmQualifiedName("qN2");
        Entity entity2 = cF.getProvFactory().newEntity(id1);

        Relation relation = cF.getProvFactory().newWasDerivedFrom(id1, id2);

        INode node = cF.newNode(entity);
        INode node2 = cF.newNode(entity2);

        IEdge edge = cF.newEdge(relation);
        edge.setCause(node);
        edge.setEffect(node2);

        assertFalse(edge.isBackbone());
        assertTrue(edge.isDomainSpecific());
        assertFalse(edge.isCrossPart());
    }


    @Test
    public void isCrossPart_validData_returnsTrue() {
        QualifiedName id1 = cF.newCpmQualifiedName("qN1");
        Entity entity = cF.getProvFactory().newEntity(id1);

        QualifiedName id2 = cF.newCpmQualifiedName("qN2");
        Entity entity2 = cF.newCpmEntity(id1, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        Relation relation = cF.getProvFactory().newWasDerivedFrom(id1, id2);

        INode node = cF.newNode(entity);
        INode node2 = cF.newNode(entity2);

        IEdge edge = cF.newEdge(relation);
        edge.setCause(node);
        edge.setEffect(node2);

        assertFalse(edge.isBackbone());
        assertFalse(edge.isDomainSpecific());
        assertTrue(edge.isCrossPart());
    }
}