package cz.muni.fi.cpm.deserialization.embrc.transform.cpm;

import cz.muni.fi.cpm.bindings.*;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.model.*;

import java.util.List;

import static cz.muni.fi.cpm.deserialization.embrc.transform.cpm.Dataset1Transformer.SAMPLE_R2_3UM;
import static cz.muni.fi.cpm.deserialization.embrc.transform.cpm.Dataset1Transformer.STORED_SAMPLE_CON_R2_3UM;

public class Dataset4Transformer extends DatasetTransformer {
    static final String DNA_SEQUENCING = "dna-sequencing";
    static final String FILTERED_SEQUENCES_CON = "filtered-sequences-con";

    private static final String TRANSPORTING_ACTIVITY = "TransportingDNAFiltersToSequencingIsUs";
    private static final String ACQUIRING_ACTIVITY = "MaterialAcquiring_Sequencing";
    private static final String M_PROCESSING_ACTIVITY = "MaterialProcessingActivity";
    private static final String M_PROCESSING_ACTIVITY_2 = "MaterialProcessingActivity2";
    private static final String M_PROCESSING_ACTIVITY_3 = "MaterialProcessingActivity3";
    private static final String M_PROCESSING_ACTIVITY_4 = "MaterialProcessingActivity4";
    private static final String D_PROCESSING_ACTIVITY = "DataProcessingActivity";

    private static final String DNA_A = "SequencingIsUs_BigProject001_a";
    private static final String DNA_B = "SequencingIsUs_BigProject001_b";
    private static final String DNA_C = "SequencingIsUs_BigProject001_c";
    private static final String FILTERED_SEQUENCES = "filtered-sequences";

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

        SpecializationOf specFc = pF.newSpecializationOf(newQNWithUnknownNS(FILTERED_SEQUENCES), fC.getId());
        indexedDS.add(specFc);

        mA.setGenerated(List.of(fC.getId()));

        return bb.toDocument(cPF);
    }

    @Override
    protected void modifyDS(IndexedDocument indexedDS) {
        Used usedTrans = pF.newUsed(newQNWithUnknownNS(TRANSPORTING_ACTIVITY), newQNWithUnknownNS(SAMPLE_R2_3UM));
        indexedDS.add(usedTrans);

        Entity r23um_transported = pF.newEntity(newQNWithUnknownNS(SAMPLE_R2_3UM + "_transported"));
        indexedDS.add(r23um_transported);

        WasGeneratedBy genTrans = pF.newWasGeneratedBy(null, r23um_transported.getId(), newQNWithUnknownNS(TRANSPORTING_ACTIVITY));
        indexedDS.add(genTrans);

        Used usedAcq = pF.newUsed(newQNWithUnknownNS(ACQUIRING_ACTIVITY), r23um_transported.getId());
        indexedDS.add(usedAcq);

        Entity r23um_acquired = pF.newEntity(newQNWithUnknownNS(SAMPLE_R2_3UM + "_acquired"));
        indexedDS.add(r23um_acquired);

        WasGeneratedBy genAcq = pF.newWasGeneratedBy(null, r23um_acquired.getId(), newQNWithUnknownNS(ACQUIRING_ACTIVITY));
        indexedDS.add(genAcq);

        Used usedProc1 = pF.newUsed(newQNWithUnknownNS(M_PROCESSING_ACTIVITY), r23um_acquired.getId());
        indexedDS.add(usedProc1);

        WasGeneratedBy genProc1 = pF.newWasGeneratedBy(null, newQNWithUnknownNS(DNA_A), newQNWithUnknownNS(M_PROCESSING_ACTIVITY));
        indexedDS.add(genProc1);

        Used usedProc2 = pF.newUsed(newQNWithUnknownNS(M_PROCESSING_ACTIVITY_2), newQNWithUnknownNS(DNA_A));
        indexedDS.add(usedProc2);

        WasGeneratedBy genProc2 = pF.newWasGeneratedBy(null, newQNWithUnknownNS(DNA_B), newQNWithUnknownNS(M_PROCESSING_ACTIVITY_2));
        indexedDS.add(genProc2);

        Used usedProc3 = pF.newUsed(newQNWithUnknownNS(M_PROCESSING_ACTIVITY_3), newQNWithUnknownNS(DNA_B));
        indexedDS.add(usedProc3);

        WasGeneratedBy genProc3 = pF.newWasGeneratedBy(null, newQNWithUnknownNS(DNA_C), newQNWithUnknownNS(M_PROCESSING_ACTIVITY_3));
        indexedDS.add(genProc3);

        Used usedProc4 = pF.newUsed(newQNWithUnknownNS(M_PROCESSING_ACTIVITY_4), newQNWithUnknownNS(DNA_C));
        indexedDS.add(usedProc4);

        Entity rawSequences = pF.newEntity(newQNWithUnknownNS("raw-sequences"));
        indexedDS.add(rawSequences);

        WasGeneratedBy genProc4 = pF.newWasGeneratedBy(null, rawSequences.getId(), newQNWithUnknownNS(M_PROCESSING_ACTIVITY_4));
        indexedDS.add(genProc4);

        Used usedDProc = pF.newUsed(newQNWithUnknownNS(D_PROCESSING_ACTIVITY), rawSequences.getId());
        indexedDS.add(usedDProc);

        Entity filteredSequences = pF.newEntity(newQNWithUnknownNS(FILTERED_SEQUENCES));
        indexedDS.add(filteredSequences);

        indexedDS.getActivity(newQNWithUnknownNS(D_PROCESSING_ACTIVITY))
                .getOther().stream().filter(o -> "result".equals(o.getElementName().getLocalPart()))
                .findFirst().ifPresent(o -> {
                    Other newO = pF.newOther(pF.newQualifiedName(o.getElementName().getNamespaceURI(), "description", o.getElementName().getPrefix()),
                            o.getValue(),
                            o.getType());
                    filteredSequences.getOther().add(newO);
                });

        WasGeneratedBy genDProc = pF.newWasGeneratedBy(null, filteredSequences.getId(), newQNWithUnknownNS(D_PROCESSING_ACTIVITY));
        indexedDS.add(genDProc);
    }
}
