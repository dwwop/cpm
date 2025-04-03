package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class CpmDocumentModificationTest {
    protected final ICpmFactory cF;
    protected final org.openprovenance.prov.model.ProvFactory pF;
    protected final ICpmProvFactory cPF;

    public CpmDocumentModificationTest(ICpmFactory cF) {
        this.cF = cF;
        pF = cF.getProvFactory();
        cPF = new CpmProvFactory(pF);
    }

    @Test
    public void setBundleId_validId_renamesBundle() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Agent agent = cPF.newCpmAgent(id1, CpmType.SENDER_AGENT, new ArrayList<>());

        Document document = pF.newDocument();
        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        bundle.getStatement().add(agent);
        document.setNamespace(Namespace.gatherNamespaces(document));
        bundle.setNamespace(Namespace.gatherNamespaces(document));

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);
        QualifiedName newId = pF.newQualifiedName("uri", "newName", "ex");
        doc.setBundleId(newId);
        Document output = doc.toDocument();
        assertEquals(newId, ((Bundle) output.getStatementOrBundle().getFirst()).getId());
    }


    @Test
    public void setBundleId_nullId_returnsNull() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Agent agent = cPF.newCpmAgent(id1, CpmType.SENDER_AGENT, new ArrayList<>());

        Document document = pF.newDocument();
        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        bundle.getStatement().add(agent);
        document.setNamespace(Namespace.gatherNamespaces(document));
        bundle.setNamespace(Namespace.gatherNamespaces(document));

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);
        doc.setBundleId(null);
        Document output = doc.toDocument();
        assertNull(((Bundle) output.getStatementOrBundle().getFirst()).getId());
    }


    @Test
    public void setNodeIdentifier_nodeWithRelations_returnsTrue() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity entity = cPF.getProvFactory().newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Agent agent = cPF.getProvFactory().newAgent(id2);

        QualifiedName newId1 = cPF.newCpmQualifiedName("newQN1");

        Relation relation1 = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id1, id2);
        Relation relation2 = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), newId1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(entity, agent, relation1, relation2), List.of(), bundleId, pF, cPF, cF);

        assertTrue(doc.setNewNodeIdentifier(id1, StatementOrBundle.Kind.PROV_ENTITY, newId1));

        assertTrue(doc.getNodes(id1).isEmpty());
        assertFalse(doc.areAllRelationsMapped());
        assertNull(doc.getEdge(id1, id2).getEffect());

        assertNotNull(doc.getNode(newId1));
        assertNotNull(doc.getEdge(newId1, id2).getEffect());
        assertEquals(2, doc.getDomainSpecificPart().size());

        doc.doAction(entity);
        assertTrue(doc.areAllRelationsMapped());
        assertNotNull(doc.getEdge(id1, id2).getEffect());
        assertEquals(entity, doc.getEdge(id1, id2).getEffect().getAnyElement());
        assertNotNull(doc.getEdge(id1, id2).getCause());
        assertEquals(agent, doc.getEdge(id1, id2).getCause().getAnyElement());
        assertEquals(3, doc.getDomainSpecificPart().size());
    }

}
