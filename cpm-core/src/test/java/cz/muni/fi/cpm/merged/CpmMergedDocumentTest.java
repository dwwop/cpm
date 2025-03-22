package cz.muni.fi.cpm.merged;

import cz.muni.fi.cpm.model.CpmDocument;
import cz.muni.fi.cpm.model.CpmDocumentTest;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CpmMergedDocumentTest extends CpmDocumentTest {
    public CpmMergedDocumentTest() throws Exception {
        super(new CpmMergedFactory());
    }

    @Test
    public void constructor_identicalIdSameKind_returnsExpectedTypes() {
        Document document = pF.newDocument();
        document.setNamespace(cPF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName entityId = pF.newQualifiedName("uri", "entity", "ex");
        Entity entity1 = pF.newEntity(entityId);
        bundle.getStatement().add(entity1);

        Entity entity2 = pF.newEntity(entityId);
        QualifiedName typeId = pF.newQualifiedName("uri", "type", "ex");
        Type type = pF.newType("", typeId);
        entity2.getType().add(type);
        bundle.getStatement().add(entity2);

        CpmDocument doc = new CpmDocument(document, pF, cPF, cF);

        assertNotNull(doc.getNode(entityId));
        assertTrue(doc.getTraversalInformationPart().isEmpty());
        assertTrue(doc.getForwardConnectors().isEmpty());
        assertFalse(doc.getNode(entityId).getAnyElement().getType().isEmpty());
        assertEquals(type, doc.getNode(entityId).getAnyElement().getType().getFirst());
    }
}
