package cz.muni.fi.cpm.divided.unordered;

import cz.muni.fi.cpm.model.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CpmUnorderedDocumentTest {

    @Nested
    public class CpmUnorderedDocumentAdditionalTest extends CpmDocumentAdditionalTest {
        public CpmUnorderedDocumentAdditionalTest() {
            super(new CpmUnorderedFactory());
        }
    }

    @Nested
    public class CpmUnorderedDocumentConstructorTest extends CpmDocumentConstructorTest {
        public CpmUnorderedDocumentConstructorTest() throws Exception {
            super(new CpmUnorderedFactory());
        }


        @Test
        public void constructor_identicalObjects_keepsBothObjects() {
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


    @Nested
    public class CpmUnorderedDocumentEqualsTest extends CpmDocumentEqualsTest {
        public CpmUnorderedDocumentEqualsTest() {
            super(new CpmUnorderedFactory());
        }
    }


    @Nested
    public class CpmUnorderedDocumentInfluenceTest extends CpmDocumentInfluenceTest {
        public CpmUnorderedDocumentInfluenceTest() {
            super(new CpmUnorderedFactory());
        }

        @Test
        public void addInfluence_twoIdenticalInfluences_keepsBothObjects() {
            QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
            Entity cause = pF.newEntity(id1);

            QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
            Entity effect = pF.newEntity(id2);

            QualifiedName rel = cPF.newCpmQualifiedName("rel");
            WasInfluencedBy inf = pF.newWasInfluencedBy(rel, id1, id2);
            WasInfluencedBy inf2 = pF.newWasInfluencedBy(rel, id1, id2);

            QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

            CpmDocument doc = new CpmDocument(List.of(), List.of(effect, inf, inf2, cause), List.of(), bundleId, pF, cPF, cF);

            assertNotNull(doc.getEdge(id1, id2));
            assertEquals(2, doc.getEdge(id1, id2).getRelations().size());
        }

        @Test
        public void addHadMember_twoIdenticalMemberships_keepsBothObjects() {
            QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
            Entity cause = pF.newEntity(id1);

            QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
            Entity effect = pF.newEntity(id2);

            QualifiedName rel = cPF.newCpmQualifiedName("rel");
            HadMember hM = pF.newHadMember(id1, id2);
            HadMember hM2 = pF.newHadMember(id1, id2);

            QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

            CpmDocument doc = new CpmDocument(List.of(), List.of(effect, hM, hM2, cause), List.of(), bundleId, pF, cPF, cF);

            assertNotNull(doc.getEdge(id1, id2));
            assertEquals(2, doc.getEdge(id1, id2).getRelations().size());
        }
    }

    @Nested
    class CpmUnorderedDocumentModificationTest extends CpmDocumentModificationTest {
        public CpmUnorderedDocumentModificationTest() {
            super(new CpmUnorderedFactory());
        }

        @Test
        public void setElementIdentifier_twoElementsWithSameId_modifiesOnlyOne() {
            QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
            Entity entity = cPF.getProvFactory().newEntity(id1);

            Entity entity2 = cPF.getProvFactory().newEntity(id1);

            QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
            Agent agent = cPF.getProvFactory().newAgent(id2);

            QualifiedName newId1 = cPF.newCpmQualifiedName("newQN1");

            Relation relation1 = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id1, id2);
            Relation relation2 = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), newId1, id2);

            QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

            CpmDocument doc = new CpmDocument(List.of(), List.of(entity, agent, entity2, relation1, relation2), List.of(), bundleId, pF, cPF, cF);

            assertTrue(doc.setNewElementIdentifier(doc.getNode(id1).getAnyElement(), newId1));

            assertNotNull(doc.getNode(id1));
            assertEquals(1, doc.getNodes(id1).size());
            assertNotNull(doc.getNode(newId1));
            assertTrue(doc.areAllRelationsMapped());
            assertNotNull(doc.getEdge(id1, id2).getEffect());
        }
    }

    @Nested
    class CpmUnorderedDocumentRemovalTest extends CpmDocumentRemovalTest {
        public CpmUnorderedDocumentRemovalTest() {
            super(new CpmUnorderedFactory());
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

        @Test
        public void removeRelation_twoRelationsWithSameId_returnsTrue() {
            QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
            Entity cause = pF.newEntity(id1);

            QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
            Entity effect = pF.newEntity(id2);

            QualifiedName rel = cPF.newCpmQualifiedName("rel");
            WasDerivedFrom inf1 = pF.newWasDerivedFrom(rel, id1, id2);
            WasDerivedFrom inf2 = pF.newWasDerivedFrom(rel, id1, id2);

            QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

            CpmDocument doc = new CpmDocument(List.of(cause, effect), List.of(inf1, inf2), List.of(), bundleId, pF, cPF, cF);

            assertEquals(2, doc.getEdge(rel).getRelations().size());
            assertFalse(doc.removeRelation(inf1));
            assertTrue(doc.removeRelation(doc.getEdge(rel).getAnyRelation()));
            assertEquals(1, doc.getEdge(rel).getRelations().size());
            assertTrue(doc.areAllRelationsMapped());
        }
    }

}
