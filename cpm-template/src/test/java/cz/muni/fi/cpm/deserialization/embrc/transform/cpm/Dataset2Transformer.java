package cz.muni.fi.cpm.deserialization.embrc.transform.cpm;

import cz.muni.fi.cpm.bindings.*;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.model.*;

import java.util.List;

import static cz.muni.fi.cpm.deserialization.embrc.transform.cpm.Dataset1Transformer.SAMPLE_R1;
import static cz.muni.fi.cpm.deserialization.embrc.transform.cpm.Dataset1Transformer.STORED_SAMPLE_CON_R1;
import static cz.muni.fi.cpm.deserialization.embrc.transform.cpm.Dataset3Transformer.IDENTIFIED_SPECIES_CON;

public class Dataset2Transformer extends DatasetTransformer {
    static final String PROCESSING = "processing";
    static final String PROCESSED_SAMPLE_CON = "processed-sample-con-r1";

    private static final String IMAGES = "5017bc3edda5006a4c29737b48585fefdfd4ef740bed03ab10730ec86e4153d7";

    public Dataset2Transformer(ProvFactory pF, ICpmProvFactory cPF) {
        super(pF, cPF);
    }

    @Override
    protected Document createBB(IndexedDocument indexedDS) {
        Backbone bb = new Backbone();
        bb.setBundleName(newQNWithUnknownNS(PROCESSING + "-bundle"));

        MainActivity mA = new MainActivity(newQNWithUnknownNS(PROCESSING));
        bb.setMainActivity(mA);

        mA.setHasPart(indexedDS.getActivities().stream().map(Identifiable::getId).toList());

        BackwardConnector bC = new BackwardConnector(newQNWithUnknownNS(STORED_SAMPLE_CON_R1));
        bb.setBackwardConnectors(List.of(bC));

        SpecializationOf specBc = pF.newSpecializationOf(newQNWithUnknownNS(SAMPLE_R1), bC.getId());
        indexedDS.add(specBc);

        mA.setUsed(List.of(new MainActivityUsed(bC.getId())));

        ForwardConnector fC = new ForwardConnector(newQNWithUnknownNS(PROCESSED_SAMPLE_CON));
        fC.setDerivedFrom(List.of(bC.getId()));

        SpecializationOf specFc = pF.newSpecializationOf(newQnWithGenNS(IMAGES), fC.getId());
        indexedDS.add(specFc);

        mA.setGenerated(List.of(fC.getId()));

        ForwardConnector fCIden = new ForwardConnector(newQNWithUnknownNS(IDENTIFIED_SPECIES_CON));
        fCIden.setDerivedFrom(List.of(fC.getId()));

        bb.setForwardConnectors(List.of(fC, fCIden));

        return bb.toDocument(cPF);
    }

    @Override
    protected void modifyDS(IndexedDocument indexedDS) {
    }
}
