package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.vannila.CpmFactory;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.LangString;
import org.openprovenance.prov.model.Statement;
import org.openprovenance.prov.model.Type;
import org.openprovenance.prov.vanilla.QualifiedName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IdentifierEntityTest {

    @Test
    public void toStatements_basicIdSet_returnsOneStatement() {
        IdentifierEntity ie = new IdentifierEntity();
        QualifiedName qN = new QualifiedName("uri", "example", "ex");
        ie.setId(qN);

        List<Statement> statements = ie.toStatements(new CpmFactory());

        assertNotNull(statements);
        assertEquals(1, statements.size());

        Statement statement = statements.getFirst();
        assertNotNull(statement);
        assertInstanceOf(Entity.class, statement);

        Entity entity = (Entity) statement;
        assertEquals(qN, entity.getId());

        assertNotNull(entity.getType());
        assertEquals(1, entity.getType().size());
        Type type = entity.getType().getFirst();
        assertEquals(CpmType.IDENTIFIER.toString(), ((QualifiedName) type.getValue()).getLocalPart());
    }

    @Test
    public void toStatements_withExternalId_returnsCorrectExternalId() {
        IdentifierEntity ie = new IdentifierEntity();
        ie.setId(new QualifiedName("uri", "example", "ex"));
        QualifiedName qN = new QualifiedName("uri", "externalId", "ex");
        ie.setExternalId(qN);

        List<Statement> statements = ie.toStatements(new CpmFactory());

        Entity entity = (Entity) statements.getFirst();

        assertNotNull(entity.getOther());
        assertEquals(1, entity.getOther().size());
        assertEquals(CpmAttributeConstants.EXTERNAL_ID, entity.getOther().getFirst().getElementName().getLocalPart());
        assertEquals(qN, entity.getOther().getFirst().getValue());
    }


    @Test
    public void toStatements_withExternalIdType_returnsCorrectExternalIdType() {
        IdentifierEntity ie = new IdentifierEntity();
        ie.setId(new QualifiedName("uri", "example", "ex"));
        String externalIdType = "externalIdType";
        ie.setExternalIdType(externalIdType);

        List<Statement> statements = ie.toStatements(new CpmFactory());

        Entity entity = (Entity) statements.getFirst();

        assertNotNull(entity.getOther());
        assertEquals(1, entity.getOther().size());
        assertInstanceOf(LangString.class, entity.getOther().getFirst().getValue());
        assertEquals(CpmAttributeConstants.EXTERNAL_ID_TYPE, entity.getOther().getFirst().getElementName().getLocalPart());
        assertEquals(externalIdType, ((LangString) entity.getOther().getFirst().getValue()).getValue());
    }


    @Test
    public void toStatements_withComment_returnsCorrectComment() {
        IdentifierEntity ie = new IdentifierEntity();
        ie.setId(new QualifiedName("uri", "example", "ex"));
        String comment = "Comment";
        ie.setComment(comment);

        List<Statement> statements = ie.toStatements(new CpmFactory());

        Entity entity = (Entity) statements.getFirst();

        assertNotNull(entity.getOther());
        assertEquals(1, entity.getOther().size());
        assertInstanceOf(LangString.class, entity.getOther().getFirst().getValue());
        assertEquals(CpmAttributeConstants.COMMENT, entity.getOther().getFirst().getElementName().getLocalPart());
        assertEquals(comment, ((LangString) entity.getOther().getFirst().getValue()).getValue());
    }
}