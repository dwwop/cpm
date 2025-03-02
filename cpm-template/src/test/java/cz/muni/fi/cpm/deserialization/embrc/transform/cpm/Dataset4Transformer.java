package cz.muni.fi.cpm.deserialization.embrc.transform.cpm;

import cz.muni.fi.cpm.bindings.*;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.model.*;

import java.util.List;
import java.util.Objects;

import static cz.muni.fi.cpm.deserialization.embrc.transform.cpm.Dataset1Transformer.SAMPLE_R2_3UM;
import static cz.muni.fi.cpm.deserialization.embrc.transform.cpm.Dataset1Transformer.STORED_SAMPLE_CON_R2_3UM;

public class Dataset4Transformer extends DatasetTransformer {
    static final String DNA_SEQUENCING = "dna-sequencing";
    static final String FILTERED_SEQUENCES_CON = "filtered-sequences-con";

    private static final String TRANSPORTING_ACTIVITY = "TransportingDNAFiltersToSequencingIsUs";
    private static final String ACQUIRING_ACTIVITY = "MaterialAcquiringActivity1";
    private static final String M_PROCESSING_ACTIVITY = "MaterialProcessingActivity1";
    private static final String M_PROCESSING_ACTIVITY_4 = "MaterialProcessingActivity4";

    private static final String FILTERED_SEQUENCES = "6dff3f85a8f1fc9ddb2b58fd748861080efab7de4e1360c39d24feb8050a297c";
    private static final String DIGITAL_SEQUENCES_OBJ = "9f1c2750a08dc77b9fdb815d05a476f27af1131e3af5003b73d5d7d6c3503b47";
    private static final String DIGITAL_SEQUENCES_RES = "d02fd085b9e9d705a7c90797480b2357d2cbdddcdd9549a03318d1f9b2da7199";

    public Dataset4Transformer(ProvFactory pF, ICpmProvFactory cPF) {
        super(pF, cPF);
    }

    @Override
    protected Document createBB(IndexedDocument indexedDS) {
        Backbone bb = new Backbone();
        bb.setBundleName(newQNWithUnknownNS(DNA_SEQUENCING + "-bundle"));

        MainActivity mA = new MainActivity(newQNWithUnknownNS(DNA_SEQUENCING));
        bb.setMainActivity(mA);

        mA.setHasPart(indexedDS.getActivities().stream().map(Identifiable::getId).toList());

        BackwardConnector bC = new BackwardConnector(newQNWithUnknownNS(STORED_SAMPLE_CON_R2_3UM));
        bb.setBackwardConnectors(List.of(bC));

        mA.setUsed(List.of(new MainActivityUsed(bC.getId())));

        SpecializationOf specBc = pF.newSpecializationOf(newQNWithUnknownNS(SAMPLE_R2_3UM), bC.getId());
        indexedDS.add(specBc);

        ForwardConnector fC = new ForwardConnector(newQNWithUnknownNS(FILTERED_SEQUENCES_CON));
        fC.setDerivedFrom(List.of(bC.getId()));
        bb.setForwardConnectors(List.of(fC));

        SpecializationOf specFc = pF.newSpecializationOf(newQnWithGenNS(FILTERED_SEQUENCES), fC.getId());
        indexedDS.add(specFc);

        mA.setGenerated(List.of(fC.getId()));

        return bb.toDocument(cPF);
    }

    @Override
    protected void modifyDS(IndexedDocument indexedDS) {

        Entity r23um_transported = pF.newEntity(newQNWithUnknownNS(SAMPLE_R2_3UM + "_transported"));
        indexedDS.add(r23um_transported);

        WasGeneratedBy genTrans = pF.newWasGeneratedBy(null, r23um_transported.getId(), newQNWithUnknownNS(TRANSPORTING_ACTIVITY));
        indexedDS.add(genTrans);

        Used usedAcq = pF.newUsed(newQNWithUnknownNS(ACQUIRING_ACTIVITY), r23um_transported.getId());
        indexedDS.add(usedAcq);

        indexedDS.getUsed().removeIf(u ->
                Objects.equals(u.getEntity(), newQNWithUnknownNS(SAMPLE_R2_3UM))
                        && Objects.equals(u.getActivity(), newQNWithUnknownNS(ACQUIRING_ACTIVITY)));

        Entity r23um_acquired = pF.newEntity(newQNWithUnknownNS(SAMPLE_R2_3UM + "_acquired"));
        indexedDS.add(r23um_acquired);

        WasGeneratedBy genAcq = pF.newWasGeneratedBy(null, r23um_acquired.getId(), newQNWithUnknownNS(ACQUIRING_ACTIVITY));
        indexedDS.add(genAcq);

        Used usedProc1 = pF.newUsed(newQNWithUnknownNS(M_PROCESSING_ACTIVITY), r23um_acquired.getId());
        indexedDS.add(usedProc1);

        Entity rawSequencesRes = indexedDS.getEntity(newQnWithGenNS(DIGITAL_SEQUENCES_RES));
        Entity rawSequencesObj = indexedDS.getEntity(newQnWithGenNS(DIGITAL_SEQUENCES_OBJ));
        rawSequencesObj.getOther().addAll(rawSequencesRes.getOther());

        WasGeneratedBy genProc4 = pF.newWasGeneratedBy(null, rawSequencesObj.getId(), newQNWithUnknownNS(M_PROCESSING_ACTIVITY_4));
        indexedDS.add(genProc4);

        indexedDS.getWasGeneratedBy()
                .removeIf(w -> Objects.equals(w.getEntity(), rawSequencesRes.getId())
                        && Objects.equals(w.getActivity(), newQNWithUnknownNS(M_PROCESSING_ACTIVITY_4)));
        indexedDS.getEntities().remove(rawSequencesRes);
    }
}
