package cz.muni.fi.cpm.deserialization.embrc.transform.cpm;

import cz.muni.fi.cpm.bindings.Backbone;
import cz.muni.fi.cpm.bindings.ForwardConnector;
import cz.muni.fi.cpm.bindings.MainActivity;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.model.*;

import java.util.List;

public class Dataset1Transformer extends DatasetTransformer {
    static final String SAMPLING = "sampling";
    static final String STORED_SAMPLE = "stored-sample-";
    static final String STORED_SAMPLE_CON = "stored-sample-con-";
    static final String STORED_SAMPLE_CON_R1 = STORED_SAMPLE_CON + "r1";
    static final String STORED_SAMPLE_CON_R2_3UM = STORED_SAMPLE_CON + "r2-3um";

    private static final String SAMPLE_ACTIVITY = "031102cf5f8c83749fa2f8fe70e3ce29a71635bd5c25c09878f36b156f9dd869";
    private static final String STORING_ACTIVITY_R1 = "51e9c9f29f9d1eb2343077ebc7a5217edc0d7769369daa907fa84f98ac529a65";
    private static final String STORING_ACTIVITY_R2_3um = "abd7abf4dec69163e3717d9e1e3332ecc07ba15fba3b4c27580b6e6cfc0075f4";
    private static final String BASE_SAMPLE = "BigProject_belgium_water_10m";
    static final String SAMPLE_R1 = BASE_SAMPLE + "_1000um_r1";
    static final String SAMPLE_R2_3UM = BASE_SAMPLE + "_1000um_r2_3um";

    public Dataset1Transformer(ProvFactory pF, ICpmProvFactory cPF) {
        super(pF, cPF);
    }

    @Override
    protected Document createBB(IndexedDocument indexedDS) {
        Backbone bb = new Backbone();
        bb.setBundleName(newQNWithUnknownNS(SAMPLING + "-bundle"));

        MainActivity mA = new MainActivity(newQNWithUnknownNS(SAMPLING));
        bb.setMainActivity(mA);

        mA.setHasPart(indexedDS.getActivities().stream().map(Identifiable::getId).toList());

        ForwardConnector fcR1 = new ForwardConnector(newQNWithUnknownNS(STORED_SAMPLE_CON_R1));
        bb.getForwardConnectors().add(fcR1);

        SpecializationOf specR1 = pF.newSpecializationOf(newQNWithUnknownNS(STORED_SAMPLE + "r1"), fcR1.getId());
        indexedDS.add(specR1);

        ForwardConnector fcR23UM = new ForwardConnector(newQNWithUnknownNS(STORED_SAMPLE_CON_R2_3UM));
        bb.getForwardConnectors().add(fcR23UM);

        SpecializationOf specR23UM = pF.newSpecializationOf(newQNWithUnknownNS(STORED_SAMPLE + "r2_3um"), fcR23UM.getId());
        indexedDS.add(specR23UM);

        mA.setGenerated(List.of(fcR1.getId(), fcR23UM.getId()));

        return bb.toDocument(cPF);
    }

    @Override
    protected void modifyDS(IndexedDocument indexedDS) {
        Used samplingUse = pF.newUsed(newQnWithGenNS(SAMPLE_ACTIVITY), newQNWithUnknownNS(BASE_SAMPLE));
        indexedDS.add(samplingUse);

        WasGeneratedBy processingGen = pF.newWasGeneratedBy(null, newQNWithUnknownNS(SAMPLE_R1), newQnWithGenNS(SAMPLE_ACTIVITY));
        indexedDS.add(processingGen);

        Used storeR1 = pF.newUsed(newQnWithGenNS(STORING_ACTIVITY_R1), newQNWithUnknownNS(SAMPLE_R1));
        indexedDS.add(storeR1);

        Used storeR23UM = pF.newUsed(newQnWithGenNS(STORING_ACTIVITY_R2_3um), newQNWithUnknownNS(SAMPLE_R2_3UM));
        indexedDS.add(storeR23UM);

        Entity storedR1 = pF.newEntity(newQNWithUnknownNS(STORED_SAMPLE + "r1"));
        storedR1.getType().addAll(indexedDS.getEntity(newQNWithUnknownNS(SAMPLE_R1)).getType());
        indexedDS.add(storedR1);

        WasGeneratedBy storingGenR1 = pF.newWasGeneratedBy(null, storedR1.getId(), newQnWithGenNS(STORING_ACTIVITY_R1));
        indexedDS.add(storingGenR1);

        Entity storedR23UM = pF.newEntity(newQNWithUnknownNS(STORED_SAMPLE + "r2_3um"));
        storedR23UM.getType().addAll(indexedDS.getEntity(newQNWithUnknownNS(SAMPLE_R2_3UM)).getType());
        indexedDS.add(storedR23UM);

        WasGeneratedBy storingGenR23UM = pF.newWasGeneratedBy(null, storedR23UM.getId(), newQnWithGenNS(STORING_ACTIVITY_R2_3um));
        indexedDS.add(storingGenR23UM);
    }

}
