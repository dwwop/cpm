package cz.muni.fi.cpm.deserialization.embrc.transform.cpm;

import cz.muni.fi.cpm.bindings.*;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.model.*;

import java.util.List;
import java.util.Map;

import static cz.muni.fi.cpm.deserialization.embrc.transform.cpm.Dataset1Transformer.STORED_SAMPLE_CON;

public class Dataset2Transformer extends DatasetTransformer {
    static final String PROCESSING = "processing";
    static final String PROCESSED_SAMPLE_CON = "processed-sample-con-r1";

    static final String PROCESSING_ACTIVITY = "MaterialProcessing";
    private static final String SAMPLE_R1 = "BigProject_belgium_water_10m_1000um_r1";
    private static final String IMAGES = "5017bc3edda5006a4c29737b48585fefdfd4ef740bed03ab10730ec86e4153d7";

    public Dataset2Transformer(ProvFactory pF, ICpmProvFactory cPF) {
        super(pF, cPF);
    }

    @Override
    protected void modifyDS(IndexedDocument indexedDS) {
        Used sampleUsed = pF.newUsed(newQNWithUnknownNS(PROCESSING_ACTIVITY), newQNWithUnknownNS(SAMPLE_R1));
        indexedDS.add(sampleUsed);

        WasGeneratedBy imagesGen = pF.newWasGeneratedBy(null, newQnWithGenNS(IMAGES), newQNWithUnknownNS(PROCESSING_ACTIVITY));
        indexedDS.add(imagesGen);
    }

    @Override
    protected Document createBB(IndexedDocument indexedDS) {
        Backbone bb = new Backbone();
        bb.setPrefixes(Map.of(UNKNOWN_PREFIX, UNKNOWN_NS, GEN_PREFIX, GEN_NS));
        bb.setBundleName(bb.getNamespace().qualifiedName(UNKNOWN_PREFIX, PROCESSING + "-bundle", pF));

        MainActivity mA = new MainActivity(newQNWithUnknownNS(PROCESSING));
        bb.setMainActivity(mA);

        mA.setHasPart(indexedDS.getActivities().stream().map(Identifiable::getId).toList());

        BackwardConnector bC = new BackwardConnector(newQNWithUnknownNS(STORED_SAMPLE_CON + "r1"));
        bb.setBackwardConnectors(List.of(bC));

        SpecializationOf specBc = pF.newSpecializationOf(newQNWithUnknownNS(SAMPLE_R1), bC.getId());
        indexedDS.add(specBc);

        mA.setUsed(List.of(new MainActivityUsed(bC.getId())));

        ForwardConnector fC = new ForwardConnector(newQNWithUnknownNS(PROCESSED_SAMPLE_CON));
        bb.setForwardConnectors(List.of(fC));

        SpecializationOf specFc = pF.newSpecializationOf(newQnWithGenNS(IMAGES), fC.getId());
        indexedDS.add(specFc);

        mA.setGenerated(List.of(fC.getId()));

        return bb.toDocument(cPF);
    }
}
