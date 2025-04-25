package cz.muni.fi.cpm.template.mapper;

import cz.muni.fi.cpm.constants.CpmAttribute;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.template.schema.BackwardConnector;
import cz.muni.fi.cpm.template.schema.ConnectorAttributed;
import cz.muni.fi.cpm.template.schema.ForwardConnector;
import cz.muni.fi.cpm.template.schema.HashAlgorithms;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.vanilla.QualifiedName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectorTest {
    private TemplateProvMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new TemplateProvMapper(new CpmProvFactory());
    }

    @Test
    public void toStatements_basicIdSet_returnsOneStatement() {
        BackwardConnector connector = new BackwardConnector();
        QualifiedName id = new QualifiedName("uri", "connectorExample", "ex");
        connector.setId(id);

        List<Statement> statements = mapper.map(connector);

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
    public void toStatements_withExternalId_returnsCorrectExternalId() {
        BackwardConnector connector = new BackwardConnector();
        connector.setId(new QualifiedName("uri", "connectorExample", "ex"));
        String externalId = "externalIdExample";
        connector.setExternalId(externalId);

        List<Statement> statements = mapper.map(connector);
        Entity entity = (Entity) statements.getFirst();

        assertNotNull(entity.getOther());
        assertInstanceOf(LangString.class, entity.getOther().getLast().getValue());
        assertEquals(CpmAttribute.EXTERNAL_ID.toString(), entity.getOther().getLast().getElementName().getLocalPart());
        assertEquals(externalId, ((LangString) entity.getOther().getLast().getValue()).getValue());
    }

    @Test
    public void toStatements_withReferencedBundleId_returnsCorrectBundleId() {
        BackwardConnector connector = new BackwardConnector();
        connector.setId(new QualifiedName("uri", "connectorExample", "ex"));
        QualifiedName bundleId = new QualifiedName("uri", "bundleExample", "ex");
        connector.setReferencedBundleId(bundleId);

        List<Statement> statements = mapper.map(connector);
        Entity entity = (Entity) statements.getFirst();

        assertNotNull(entity.getOther());
        assertEquals(1, entity.getOther().size());
        assertEquals(CpmAttribute.REFERENCED_BUNDLE_ID.toString(), entity.getOther().getFirst().getElementName().getLocalPart());
        assertEquals(bundleId, entity.getOther().getFirst().getValue());
    }

    @Test
    public void toStatements_withHashAndAlg_returnsCorrectValues() {
        BackwardConnector connector = new BackwardConnector();
        connector.setId(new QualifiedName("uri", "connectorExample", "ex"));
        Object hashValue = "hashValue";
        connector.setReferencedBundleHashValue(hashValue);
        connector.setHashAlg(HashAlgorithms.SHA256);

        List<Statement> statements = mapper.map(connector);
        Entity entity = (Entity) statements.getFirst();

        assertNotNull(entity.getOther());
        assertEquals(2, entity.getOther().size());

        Attribute hashValueAttr = entity.getOther().getFirst();
        assertEquals(CpmAttribute.REFERENCED_BUNDLE_HASH_VALUE.toString(), hashValueAttr.getElementName().getLocalPart());
        assertEquals(hashValue, hashValueAttr.getValue());

        assertInstanceOf(LangString.class, entity.getOther().getLast().getValue());
        assertEquals(CpmAttribute.HASH_ALG.toString(), entity.getOther().getLast().getElementName().getLocalPart());
        assertEquals(HashAlgorithms.SHA256.toString(), ((LangString) entity.getOther().getLast().getValue()).getValue());

    }

    @Test
    public void toStatements_withProvenanceUri_returnsCorrectUri() {
        BackwardConnector connector = new BackwardConnector();
        connector.setId(new QualifiedName("uri", "connectorExample", "ex"));
        String provenanceUri = "http://example.com/provenance";
        connector.setProvenanceServiceUri(provenanceUri);

        List<Statement> statements = mapper.map(connector);
        Entity entity = (Entity) statements.get(0);

        assertNotNull(entity.getOther());
        assertEquals(1, entity.getOther().size());

        Attribute uri = entity.getOther().getFirst();
        assertEquals(CpmAttribute.PROVENANCE_SERVICE_URI.toString(), uri.getElementName().getLocalPart());
        assertEquals(provenanceUri, uri.getValue());
    }

    @Test
    public void toStatements_withDerivedFrom_returnsCorrectRelation() {
        BackwardConnector connector = new BackwardConnector();
        QualifiedName qN = new QualifiedName("uri", "connectorExample", "ex");
        connector.setId(qN);
        QualifiedName derivedFromId = new QualifiedName("uri", "derivedFromExample", "ex");
        connector.setDerivedFrom(List.of(derivedFromId));

        List<Statement> statements = mapper.map(connector);
        assertEquals(2, statements.size());

        Statement derivedFromStatement = statements.get(1);
        assertInstanceOf(WasDerivedFrom.class, derivedFromStatement);
        assertEquals(derivedFromId, ((WasDerivedFrom) derivedFromStatement).getUsedEntity());
        assertEquals(qN, ((WasDerivedFrom) derivedFromStatement).getGeneratedEntity());
    }

    @Test
    public void toStatements_withAttributedTo_returnsCorrectAttribution() {
        BackwardConnector connector = new BackwardConnector();
        QualifiedName qN = new QualifiedName("uri", "connectorExample", "ex");
        connector.setId(qN);
        QualifiedName attributedToId = new QualifiedName("uri", "attributedToExample", "ex");
        ConnectorAttributed cA = new ConnectorAttributed();
        cA.setAgentId(attributedToId);
        connector.setAttributedTo(cA);

        List<Statement> statements = mapper.map(connector);
        assertEquals(2, statements.size());
        Statement attributedToStatement = statements.getLast();
        assertInstanceOf(WasAttributedTo.class, attributedToStatement);
        assertEquals(qN, ((WasAttributedTo) attributedToStatement).getEntity());
        assertEquals(attributedToId, ((WasAttributedTo) attributedToStatement).getAgent());
    }

    @Test
    public void toStatements_forwardConnectorSpecialisation_returnsCorrectSpecialisation() {
        ForwardConnector connector = new ForwardConnector();
        QualifiedName qN = new QualifiedName("uri", "connectorExample", "ex");
        connector.setId(qN);
        QualifiedName specialisationId = new QualifiedName("uri", "specialisationExample", "ex");
        connector.setSpecializationOf(specialisationId);

        List<Statement> statements = mapper.map(connector);
        assertEquals(2, statements.size());
        Statement specStatement = statements.getLast();
        assertInstanceOf(SpecializationOf.class, specStatement);
        assertEquals(qN, ((SpecializationOf) specStatement).getSpecificEntity());
        assertEquals(specialisationId, ((SpecializationOf) specStatement).getGeneralEntity());
    }
}
