package cz.muni.fi.cpm.deserialization.embrc.transform;

import cz.muni.fi.cpm.bindings.Backbone;
import cz.muni.fi.cpm.bindings.ForwardConnector;
import cz.muni.fi.cpm.bindings.MainActivity;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.model.*;

import java.util.List;
import java.util.Map;

public class Dataset1Transformer {
    private static final String UNKNOWN_NS = "_";
    private static final String UNKNOWN_PREFIX = "_";
    private static final String GEN_NS = "gen";
    private static final String GEN_PREFIX = "gen";
    private static final String SAMPLING = "sampling";
    private static final String STORED_SAMPLE = "stored-sample-";
    private static final String STORED_SAMPLE_CON = "stored-sample-con-";

    private final ProvFactory pF;
    private final ICpmProvFactory cPF;
    private final ProvUtilities u;

    public Dataset1Transformer(ProvFactory pF, ICpmProvFactory cPF) {
        this.pF = pF;
        this.cPF = cPF;
        this.u = new ProvUtilities();
    }

    public Document toDocument(Document dsDoc) {
        IndexedDocument indexedDS = new IndexedDocument(pF, dsDoc);
        modifyDS(indexedDS);
        Document bb = createBB(indexedDS);
        Document updatedDs = indexedDS.toDocument();

        ((Bundle) bb.getStatementOrBundle().getFirst()).getStatement().addAll(u.getStatement(updatedDs));
        bb.getNamespace().extendWith(updatedDs.getNamespace());
        return bb;
    }

    private Document createBB(IndexedDocument indexedDS) {
        Backbone bb = new Backbone();
        bb.setPrefixes(Map.of(UNKNOWN_PREFIX, UNKNOWN_NS, GEN_PREFIX, GEN_NS));
        bb.setBundleName(bb.getNamespace().qualifiedName(UNKNOWN_PREFIX, SAMPLING + "-bundle", pF));

        MainActivity mA = new MainActivity(bb.getNamespace().qualifiedName(UNKNOWN_PREFIX, SAMPLING, pF));
        bb.setMainActivity(mA);

        mA.setHasPart(indexedDS.getActivities().stream().map(Identifiable::getId).toList());

        ForwardConnector fcR1 = new ForwardConnector(bb.getNamespace().qualifiedName(UNKNOWN_PREFIX, STORED_SAMPLE_CON + "r1", pF));
        bb.getForwardConnectors().add(fcR1);

        SpecializationOf specR1 = pF.newSpecializationOf(newQNWithUnknownNS(STORED_SAMPLE + "r1"), fcR1.getId());
        indexedDS.add(specR1);

        ForwardConnector fcR23UM = new ForwardConnector(bb.getNamespace().qualifiedName(UNKNOWN_PREFIX, STORED_SAMPLE_CON + "r2_3um", pF));
        bb.getForwardConnectors().add(fcR23UM);

        SpecializationOf specR23UM = pF.newSpecializationOf(newQNWithUnknownNS(STORED_SAMPLE + "r2_3um"), fcR23UM.getId());
        indexedDS.add(specR23UM);

        mA.setGenerated(List.of(fcR1.getId(), fcR23UM.getId()));

        return bb.toDocument(cPF);
    }

    private void modifyDS(IndexedDocument indexedDS) {
        Used samplingUse = pF.newUsed(newQnWithGenNS("031102cf5f8c83749fa2f8fe70e3ce29a71635bd5c25c09878f36b156f9dd869"), newQNWithUnknownNS("BigProject_belgium_water_10m"));
        indexedDS.add(samplingUse);

        WasGeneratedBy processingGen = pF.newWasGeneratedBy(null, newQNWithUnknownNS("BigProject_belgium_water_10m_1000um_r1"), newQnWithGenNS("031102cf5f8c83749fa2f8fe70e3ce29a71635bd5c25c09878f36b156f9dd869"));
        indexedDS.add(processingGen);

        Used storeR1 = pF.newUsed(newQnWithGenNS("51e9c9f29f9d1eb2343077ebc7a5217edc0d7769369daa907fa84f98ac529a65"), newQNWithUnknownNS("BigProject_belgium_water_10m_1000um_r1"));
        indexedDS.add(storeR1);

        Used storeR23UM = pF.newUsed(newQnWithGenNS("abd7abf4dec69163e3717d9e1e3332ecc07ba15fba3b4c27580b6e6cfc0075f4"), newQNWithUnknownNS("BigProject_belgium_water_10m_1000um_r2_3um"));
        indexedDS.add(storeR23UM);

        Entity storedR1 = pF.newEntity(newQNWithUnknownNS(STORED_SAMPLE + "r1"));
        storedR1.getType().addAll(indexedDS.getEntity(newQNWithUnknownNS("BigProject_belgium_water_10m_1000um_r1")).getType());
        indexedDS.add(storedR1);

        WasGeneratedBy storingGenR1 = pF.newWasGeneratedBy(null, storedR1.getId(), newQnWithGenNS("51e9c9f29f9d1eb2343077ebc7a5217edc0d7769369daa907fa84f98ac529a65"));
        indexedDS.add(storingGenR1);

        Entity storedR23UM = pF.newEntity(newQNWithUnknownNS(STORED_SAMPLE + "r2_3um"));
        storedR23UM.getType().addAll(indexedDS.getEntity(newQNWithUnknownNS("BigProject_belgium_water_10m_1000um_r2_3um")).getType());
        indexedDS.add(storedR23UM);

        WasGeneratedBy storingGenR23UM = pF.newWasGeneratedBy(null, storedR23UM.getId(), newQnWithGenNS("abd7abf4dec69163e3717d9e1e3332ecc07ba15fba3b4c27580b6e6cfc0075f4"));
        indexedDS.add(storingGenR23UM);
    }

    private QualifiedName newQNWithUnknownNS(String localPart) {
        return pF.newQualifiedName(UNKNOWN_NS, localPart, UNKNOWN_PREFIX);
    }

    private QualifiedName newQnWithGenNS(String localPart) {
        return pF.newQualifiedName(GEN_NS, localPart, GEN_PREFIX);
    }
}
