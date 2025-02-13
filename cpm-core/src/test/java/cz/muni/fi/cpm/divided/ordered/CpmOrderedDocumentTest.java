package cz.muni.fi.cpm.divided.ordered;

import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.model.CpmDocument;
import cz.muni.fi.cpm.model.CpmDocumentTest;
import org.junit.jupiter.api.RepeatedTest;
import org.openprovenance.prov.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CpmOrderedDocumentTest extends CpmDocumentTest {
    public CpmOrderedDocumentTest() throws Exception {
        super(new CpmOrderedFactory());
    }

    @RepeatedTest(10)
    public void toDocument_randomizedOrderOf_keepsOrder() {

        QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
        Entity entity1 = cPF.getProvFactory().newEntity(id1);

        QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
        Entity entity2 = cPF.newCpmEntity(id2, CpmType.BACKWARD_CONNECTOR, new ArrayList<>());

        QualifiedName id3 = cPF.newCpmQualifiedName("qN3");
        Agent agent = cPF.getProvFactory().newAgent(id3);

        Entity duplicate = pF.newEntity(id2);

        Relation relation1 = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id1, id3);

        Relation relation2 = cPF.getProvFactory().newWasDerivedFrom(id2, id1);

        Activity activity1 = pF.newActivity(id1);

        Document document = pF.newDocument();
        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        List<Statement> st = Arrays.asList(entity1, entity2, agent, relation1, relation2, activity1, duplicate);
        Collections.shuffle(st);

        bundle.getStatement().addAll(st);

        Document docFromCpm = new CpmDocument(document, pF, cPF, cF).toDocument();
        assertEquals(bundle.getStatement(), ((Bundle) docFromCpm.getStatementOrBundle().getFirst()).getStatement());
    }

    @RepeatedTest(3)
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
        Collections.shuffle(st);

        bundle.getStatement().addAll(st);

        Document docFromCpm = new CpmDocument(document, pF, cPF, cF).toDocument();
        assertEquals(bundle.getStatement(), ((Bundle) docFromCpm.getStatementOrBundle().getFirst()).getStatement());
    }
}
