package cz.muni.fi.cpm.divided.unordered;

import cz.muni.fi.cpm.model.CpmDocument;
import cz.muni.fi.cpm.model.CpmDocumentTest;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CpmUnorderedDocumentTest extends CpmDocumentTest {
    public CpmUnorderedDocumentTest() throws Exception {
        super(new CpmUnorderedProvFactory());
    }

    @Test
    public void toDocument_identicalObjects_keepsBothObjects() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity entity1 = cPF.getProvFactory().newEntity(id1);
        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Entity entity2 = pF.newEntity(id2);

        Entity duplicate = pF.newEntity(id2);
        Relation relation = cPF.getProvFactory().newWasDerivedFrom(id2, id1);
        Relation duplicateRel = pF.newStatement(relation);

        Document document = pF.newDocument();
        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        List<Statement> st = Arrays.asList(entity2, duplicate, entity1, relation, duplicateRel);

        bundle.getStatement().addAll(st);

        Document docFromCpm = new CpmDocument(document, pF, cPF, cF).toDocument();
        Bundle resultBundle = ((Bundle) docFromCpm.getStatementOrBundle().getFirst());
        assertEquals(new HashSet<>(bundle.getStatement()), new HashSet<>(resultBundle.getStatement()));
    }

    @Test
    public void removeElement_nodeWithMultipleElements_returnsTrue() {
        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity entity = cPF.getProvFactory().newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Agent agent = cPF.getProvFactory().newAgent(id2);

        Entity entity2 = cPF.getProvFactory().newEntity(id1);

        Relation relation1 = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id1, id2);

        QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

        CpmDocument doc = new CpmDocument(List.of(), List.of(entity, agent, entity2, relation1), List.of(), bundleId, pF, cPF, cF);
        List<Element> id1Elements = doc.getNode(id1).getElements();

        assertEquals(2, id1Elements.size());
        assertFalse(doc.removeElement(entity));
        assertTrue(doc.removeElement(id1Elements.getFirst()));
        assertEquals(1, doc.getNodes(id1).size());
        assertEquals(1, doc.getNode(id1).getElements().size());
        assertTrue(doc.areAllRelationsMapped());
        assertNotNull(doc.getEdge(id1, id2).getEffect());
        assertEquals(2, doc.getDomainSpecificPart().size());
    }
}
