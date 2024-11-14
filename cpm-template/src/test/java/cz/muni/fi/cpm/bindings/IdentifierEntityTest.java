package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.CpmFactory;
import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.constants.CpmTypeConstants;
import org.junit.Assert;
import org.junit.Test;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.LangString;
import org.openprovenance.prov.model.Statement;
import org.openprovenance.prov.model.Type;
import org.openprovenance.prov.vanilla.QualifiedName;

import java.util.List;

public class IdentifierEntityTest {

    @Test
    public void testToStatements_basic() {
        IdentifierEntity ie = new IdentifierEntity();
        QualifiedName qN = new QualifiedName("uri", "example", "ex");
        ie.setId(qN);

        List<Statement> statements = ie.toStatements(new CpmFactory());

        Assert.assertNotNull(statements);
        Assert.assertEquals(1, statements.size());

        Statement statement = statements.getFirst();
        Assert.assertNotNull(statement);
        Assert.assertTrue(statement instanceof Entity);

        Entity entity = (Entity) statement;
        Assert.assertEquals(qN, entity.getId());

        Assert.assertNotNull(entity.getType());
        Assert.assertEquals(1, entity.getType().size());
        Type type = entity.getType().getFirst();
        Assert.assertEquals(CpmTypeConstants.IDENTIFIER, ((QualifiedName) type.getValue()).getLocalPart());
    }

    @Test
    public void testToStatements_externalId() {
        IdentifierEntity ie = new IdentifierEntity();
        ie.setId(new QualifiedName("uri", "example", "ex"));
        QualifiedName qN = new QualifiedName("uri", "externalId", "ex");
        ie.setExternalId(qN);

        List<Statement> statements = ie.toStatements(new CpmFactory());

        Entity entity = (Entity) statements.getFirst();

        Assert.assertNotNull(entity.getOther());
        Assert.assertEquals(1, entity.getOther().size());
        Assert.assertEquals(CpmAttributeConstants.EXTERNAL_ID, entity.getOther().getFirst().getElementName().getLocalPart());
        Assert.assertEquals(qN, entity.getOther().getFirst().getValue());
    }


    @Test
    public void testToStatements_externalIdType() {
        IdentifierEntity ie = new IdentifierEntity();
        ie.setId(new QualifiedName("uri", "example", "ex"));
        String externalIdType = "externalIdType";
        ie.setExternalIdType(externalIdType);

        List<Statement> statements = ie.toStatements(new CpmFactory());

        Entity entity = (Entity) statements.getFirst();

        Assert.assertNotNull(entity.getOther());
        Assert.assertEquals(1, entity.getOther().size());
        Assert.assertTrue(entity.getOther().getFirst().getValue() instanceof LangString);
        Assert.assertEquals(CpmAttributeConstants.EXTERNAL_ID_TYPE, entity.getOther().getFirst().getElementName().getLocalPart());
        Assert.assertEquals(externalIdType, ((LangString) entity.getOther().getFirst().getValue()).getValue());
    }


    @Test
    public void testToStatements_comment() {
        IdentifierEntity ie = new IdentifierEntity();
        ie.setId(new QualifiedName("uri", "example", "ex"));
        String comment = "Comment";
        ie.setComment(comment);

        List<Statement> statements = ie.toStatements(new CpmFactory());

        Entity entity = (Entity) statements.getFirst();

        Assert.assertNotNull(entity.getOther());
        Assert.assertEquals(1, entity.getOther().size());
        Assert.assertTrue(entity.getOther().getFirst().getValue() instanceof LangString);
        Assert.assertEquals(CpmAttributeConstants.COMMENT, entity.getOther().getFirst().getElementName().getLocalPart());
        Assert.assertEquals(comment, ((LangString) entity.getOther().getFirst().getValue()).getValue());
    }
}