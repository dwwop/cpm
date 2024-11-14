package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.CpmFactory;
import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import org.junit.Assert;
import org.junit.Test;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.vanilla.QualifiedName;

import java.util.List;

public class ConnectorTest {
    public static final String TEST_CONNECTOR = "TEST";

    @Test
    public void testToStatements_basic() {
        TestConnector connector = new TestConnector();
        QualifiedName id = new QualifiedName("uri", "connectorExample", "ex");
        connector.setId(id);

        List<Statement> statements = connector.toStatements(new CpmFactory());

        Assert.assertNotNull(statements);
        Assert.assertEquals(1, statements.size());

        Statement statement = statements.getFirst();
        Assert.assertTrue(statement instanceof Entity);
        Entity entity = (Entity) statement;

        Assert.assertNotNull(entity.getType());
        Assert.assertEquals(1, entity.getType().size());
        Type type = entity.getType().getFirst();
        Assert.assertEquals(TEST_CONNECTOR, ((QualifiedName) type.getValue()).getLocalPart());
    }

    @Test
    public void testToStatements_withExternalId() {
        TestConnector connector = new TestConnector();
        connector.setId(new QualifiedName("uri", "connectorExample", "ex"));
        QualifiedName externalId = new QualifiedName("uri", "externalIdExample", "ex");
        connector.setExternalId(externalId);

        List<Statement> statements = connector.toStatements(new CpmFactory());
        Entity entity = (Entity) statements.getFirst();

        Assert.assertNotNull(entity.getOther());
        Assert.assertEquals(1, entity.getOther().size());
        Assert.assertEquals(CpmAttributeConstants.EXTERNAL_ID, entity.getOther().getFirst().getElementName().getLocalPart());
        Assert.assertEquals(externalId, entity.getOther().getFirst().getValue());
    }

    @Test
    public void testToStatements_withReferencedBundleId() {
        TestConnector connector = new TestConnector();
        connector.setId(new QualifiedName("uri", "connectorExample", "ex"));
        QualifiedName bundleId = new QualifiedName("uri", "bundleExample", "ex");
        connector.setReferencedBundleId(bundleId);

        List<Statement> statements = connector.toStatements(new CpmFactory());
        Entity entity = (Entity) statements.getFirst();

        Assert.assertNotNull(entity.getOther());
        Assert.assertEquals(1, entity.getOther().size());
        Assert.assertEquals(CpmAttributeConstants.REFERENCED_BUNDLE_ID, entity.getOther().getFirst().getElementName().getLocalPart());
        Assert.assertEquals(bundleId, entity.getOther().getFirst().getValue());
    }

    @Test
    public void testToStatements_withHashValueAndAlg() {
        TestConnector connector = new TestConnector();
        connector.setId(new QualifiedName("uri", "connectorExample", "ex"));
        Object hashValue = "hashValue";
        connector.setReferencedBundleHashValue(hashValue);
        connector.setHashAlg(HashAlgorithms.SHA256);

        List<Statement> statements = connector.toStatements(new CpmFactory());
        Entity entity = (Entity) statements.getFirst();

        Assert.assertNotNull(entity.getOther());
        Assert.assertEquals(2, entity.getOther().size());

        Attribute hashValueAttr = entity.getOther().getFirst();
        Assert.assertEquals(CpmAttributeConstants.REFERENCED_BUNDLE_HASH_VALUE, hashValueAttr.getElementName().getLocalPart());
        Assert.assertEquals(hashValue, hashValueAttr.getValue());

        Assert.assertTrue(entity.getOther().getLast().getValue() instanceof LangString);
        Assert.assertEquals(CpmAttributeConstants.HASH_ALG, entity.getOther().getLast().getElementName().getLocalPart());
        Assert.assertEquals(HashAlgorithms.SHA256.toString(), ((LangString) entity.getOther().getLast().getValue()).getValue());

    }

    @Test
    public void testToStatements_withProvenanceServiceUri() {
        TestConnector connector = new TestConnector();
        connector.setId(new QualifiedName("uri", "connectorExample", "ex"));
        String provenanceUri = "http://example.com/provenance";
        connector.setProvenanceServiceUri(provenanceUri);

        List<Statement> statements = connector.toStatements(new CpmFactory());
        Entity entity = (Entity) statements.get(0);

        Assert.assertNotNull(entity.getOther());
        Assert.assertEquals(1, entity.getOther().size());

        Attribute uri = entity.getOther().getFirst();
        Assert.assertEquals(CpmAttributeConstants.PROVENANCE_SERVICE_URI, uri.getElementName().getLocalPart());
        Assert.assertEquals(provenanceUri, uri.getValue());
    }

    @Test
    public void testToStatements_withDerivedFrom() {
        TestConnector connector = new TestConnector();
        QualifiedName qN = new QualifiedName("uri", "connectorExample", "ex");
        connector.setId(qN);
        QualifiedName derivedFromId = new QualifiedName("uri", "derivedFromExample", "ex");
        connector.setDerivedFrom(List.of(derivedFromId));

        List<Statement> statements = connector.toStatements(new CpmFactory());
        Assert.assertEquals(2, statements.size());

        Statement derivedFromStatement = statements.get(1);
        Assert.assertTrue(derivedFromStatement instanceof WasDerivedFrom);
        Assert.assertEquals(derivedFromId, ((WasDerivedFrom) derivedFromStatement).getUsedEntity());
        Assert.assertEquals(qN, ((WasDerivedFrom) derivedFromStatement).getGeneratedEntity());
    }

    @Test
    public void testToStatements_withAttributedTo() {
        TestConnector connector = new TestConnector();
        QualifiedName qN = new QualifiedName("uri", "connectorExample", "ex");
        connector.setId(qN);
        QualifiedName attributedToId = new QualifiedName("uri", "attributedToExample", "ex");
        connector.setAttributedTo(attributedToId);

        List<Statement> statements = connector.toStatements(new CpmFactory());
        Assert.assertEquals(2, statements.size());
        Statement attributedToStatement = statements.getLast();
        Assert.assertTrue(attributedToStatement instanceof WasAttributedTo);
        Assert.assertEquals(qN, ((WasAttributedTo) attributedToStatement).getEntity());
        Assert.assertEquals(attributedToId, ((WasAttributedTo) attributedToStatement).getAgent());
    }

    private class TestConnector extends Connector {
        @Override
        public String getType() {
            return TEST_CONNECTOR;
        }
    }
}
