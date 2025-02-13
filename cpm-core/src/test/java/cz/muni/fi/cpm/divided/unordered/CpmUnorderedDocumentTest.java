package cz.muni.fi.cpm.divided.unordered;

import cz.muni.fi.cpm.model.CpmDocument;
import cz.muni.fi.cpm.model.CpmDocumentTest;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
