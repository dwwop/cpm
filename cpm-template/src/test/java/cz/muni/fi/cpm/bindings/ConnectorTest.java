package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.vannila.CpmFactory;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.vanilla.QualifiedName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectorTest {
    @Test
    public void testToStatements_basic() {
        TestConnector connector = new TestConnector();
        QualifiedName id = new QualifiedName("uri", "connectorExample", "ex");
        connector.setId(id);

        List<Statement> statements = connector.toStatements(new CpmFactory());

        assertNotNull(statements);
        assertEquals(1, statements.size());

        Statement statement = statements.getFirst();
        assertInstanceOf(Entity.class, statement);
        Entity entity = (Entity) statement;

        assertNotNull(entity.getType());
        assertEquals(1, entity.getType().size());
        Type type = entity.getType().getFirst();
        assertEquals(CpmType.BACKWARD_CONNECTOR.toString(), ((QualifiedName) type.getValue()).getLocalPart());
    }

    @Test
    public void testToStatements_withExternalId() {
        TestConnector connector = new TestConnector();
        connector.setId(new QualifiedName("uri", "connectorExample", "ex"));
        QualifiedName externalId = new QualifiedName("uri", "externalIdExample", "ex");
        connector.setExternalId(externalId);

        List<Statement> statements = connector.toStatements(new CpmFactory());
        Entity entity = (Entity) statements.getFirst();

        assertNotNull(entity.getOther());
        assertEquals(1, entity.getOther().size());
        assertEquals(CpmAttributeConstants.EXTERNAL_ID, entity.getOther().getFirst().getElementName().getLocalPart());
        assertEquals(externalId, entity.getOther().getFirst().getValue());
    }

    @Test
    public void testToStatements_withReferencedBundleId() {
        TestConnector connector = new TestConnector();
        connector.setId(new QualifiedName("uri", "connectorExample", "ex"));
        QualifiedName bundleId = new QualifiedName("uri", "bundleExample", "ex");
        connector.setReferencedBundleId(bundleId);

        List<Statement> statements = connector.toStatements(new CpmFactory());
        Entity entity = (Entity) statements.getFirst();

        assertNotNull(entity.getOther());
        assertEquals(1, entity.getOther().size());
        assertEquals(CpmAttributeConstants.REFERENCED_BUNDLE_ID, entity.getOther().getFirst().getElementName().getLocalPart());
        assertEquals(bundleId, entity.getOther().getFirst().getValue());
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

        assertNotNull(entity.getOther());
        assertEquals(2, entity.getOther().size());

        Attribute hashValueAttr = entity.getOther().getFirst();
        assertEquals(CpmAttributeConstants.REFERENCED_BUNDLE_HASH_VALUE, hashValueAttr.getElementName().getLocalPart());
        assertEquals(hashValue, hashValueAttr.getValue());

        assertInstanceOf(LangString.class, entity.getOther().getLast().getValue());
        assertEquals(CpmAttributeConstants.HASH_ALG, entity.getOther().getLast().getElementName().getLocalPart());
        assertEquals(HashAlgorithms.SHA256.toString(), ((LangString) entity.getOther().getLast().getValue()).getValue());

    }

    @Test
    public void testToStatements_withProvenanceServiceUri() {
        TestConnector connector = new TestConnector();
        connector.setId(new QualifiedName("uri", "connectorExample", "ex"));
        String provenanceUri = "http://example.com/provenance";
        connector.setProvenanceServiceUri(provenanceUri);

        List<Statement> statements = connector.toStatements(new CpmFactory());
        Entity entity = (Entity) statements.get(0);

        assertNotNull(entity.getOther());
        assertEquals(1, entity.getOther().size());

        Attribute uri = entity.getOther().getFirst();
        assertEquals(CpmAttributeConstants.PROVENANCE_SERVICE_URI, uri.getElementName().getLocalPart());
        assertEquals(provenanceUri, uri.getValue());
    }

    @Test
    public void testToStatements_withDerivedFrom() {
        TestConnector connector = new TestConnector();
        QualifiedName qN = new QualifiedName("uri", "connectorExample", "ex");
        connector.setId(qN);
        QualifiedName derivedFromId = new QualifiedName("uri", "derivedFromExample", "ex");
        connector.setDerivedFrom(List.of(derivedFromId));

        List<Statement> statements = connector.toStatements(new CpmFactory());
        assertEquals(2, statements.size());

        Statement derivedFromStatement = statements.get(1);
        assertInstanceOf(WasDerivedFrom.class, derivedFromStatement);
        assertEquals(derivedFromId, ((WasDerivedFrom) derivedFromStatement).getUsedEntity());
        assertEquals(qN, ((WasDerivedFrom) derivedFromStatement).getGeneratedEntity());
    }

    @Test
    public void testToStatements_withAttributedTo() {
        TestConnector connector = new TestConnector();
        QualifiedName qN = new QualifiedName("uri", "connectorExample", "ex");
        connector.setId(qN);
        QualifiedName attributedToId = new QualifiedName("uri", "attributedToExample", "ex");
        connector.setAttributedTo(attributedToId);

        List<Statement> statements = connector.toStatements(new CpmFactory());
        assertEquals(2, statements.size());
        Statement attributedToStatement = statements.getLast();
        assertInstanceOf(WasAttributedTo.class, attributedToStatement);
        assertEquals(qN, ((WasAttributedTo) attributedToStatement).getEntity());
        assertEquals(attributedToId, ((WasAttributedTo) attributedToStatement).getAgent());
    }

    private class TestConnector extends Connector {
        @Override
        public CpmType getType() {
            return CpmType.BACKWARD_CONNECTOR;
        }
    }
}
