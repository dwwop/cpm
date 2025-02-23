package cz.muni.fi.cpm.deserialization.embrc.transform.cpm;

import cz.muni.fi.cpm.bindings.*;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.model.*;

import java.util.List;

import static cz.muni.fi.cpm.deserialization.embrc.transform.cpm.Dataset1Transformer.STORED_SAMPLE_CON_R1;
import static cz.muni.fi.cpm.deserialization.embrc.transform.cpm.Dataset2Transformer.PROCESSED_SAMPLE_CON;

public class Dataset3Transformer extends DatasetTransformer {
    static final String SPECIES_IDENTIFICATION = "species-identification";
    static final String IDENTIFIED_SPECIES_CON = "identified-species-con";

    private static final String IDENTIFICATION_ACTIVITY = "FlowCamSpeciesIdentification";
    private static final String IMAGES = "db2aef1f9e9cdf745897953efba7931099655ee21f323526decd0ad11b4cc0e0";
    private static final String RESULTS = "ac6f25ecc17ae20cbb5789ee654db6d401c771b4ad4c9e971a0d960c3b596ac4";

    public Dataset3Transformer(ProvFactory pF, ICpmProvFactory cPF) {
        super(pF, cPF);
    }

    @Override
    protected Document createBB(IndexedDocument indexedDS) {
        Backbone bb = new Backbone();
        bb.setBundleName(newQNWithUnknownNS(SPECIES_IDENTIFICATION + "-bundle"));

        MainActivity mA = new MainActivity(newQNWithUnknownNS(SPECIES_IDENTIFICATION));
        bb.setMainActivity(mA);

        mA.setHasPart(indexedDS.getActivities().stream().map(Identifiable::getId).toList());

        BackwardConnector bC = new BackwardConnector(newQNWithUnknownNS(PROCESSED_SAMPLE_CON));

        SpecializationOf specBc = pF.newSpecializationOf(newQnWithGenNS(IMAGES), bC.getId());
        indexedDS.add(specBc);

        mA.setUsed(List.of(new MainActivityUsed(bC.getId())));

        BackwardConnector bcStored = new BackwardConnector(newQNWithUnknownNS(STORED_SAMPLE_CON_R1));
        bC.setDerivedFrom(List.of(bcStored.getId()));

        bb.setBackwardConnectors(List.of(bC, bcStored));

        ForwardConnector fC = new ForwardConnector(newQNWithUnknownNS(IDENTIFIED_SPECIES_CON));
        fC.setDerivedFrom(List.of(bC.getId()));
        bb.setForwardConnectors(List.of(fC));

        SpecializationOf specFc = pF.newSpecializationOf(newQnWithGenNS(RESULTS), fC.getId());
        indexedDS.add(specFc);

        mA.setGenerated(List.of(fC.getId()));
        return bb.toDocument(cPF);
    }

    @Override
    protected void modifyDS(IndexedDocument indexedDS) {
        Used sampleUsed = pF.newUsed(newQNWithUnknownNS(IDENTIFICATION_ACTIVITY), newQnWithGenNS(IMAGES));
        indexedDS.add(sampleUsed);

        WasGeneratedBy imagesGen = pF.newWasGeneratedBy(null, newQnWithGenNS(RESULTS), newQNWithUnknownNS(IDENTIFICATION_ACTIVITY));
        indexedDS.add(imagesGen);
    }
}
