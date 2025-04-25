package cz.muni.fi.cpm.template.deserialization.embrc.transform.cpm;

import cz.muni.fi.cpm.model.ICpmProvFactory;
import cz.muni.fi.cpm.template.schema.ForwardConnector;
import cz.muni.fi.cpm.template.schema.MainActivity;
import cz.muni.fi.cpm.template.schema.TraversalInformation;
import org.openprovenance.prov.model.*;

import java.util.List;

import static cz.muni.fi.cpm.template.deserialization.embrc.transform.cpm.Dataset2Transformer.PROCESSING;
import static cz.muni.fi.cpm.template.deserialization.embrc.transform.cpm.Dataset3Transformer.IDENTIFIED_SPECIES_CON;
import static cz.muni.fi.cpm.template.deserialization.embrc.transform.cpm.Dataset3Transformer.SPECIES_IDENTIFICATION;
import static cz.muni.fi.cpm.template.deserialization.embrc.transform.cpm.Dataset4Transformer.DNA_SEQUENCING;

public class Dataset1Transformer extends DatasetTransformer {
    static final String SAMPLING = "Sampling";

    static final String STORED_SAMPLE = "StoredSample";
    static final String STORED_SAMPLE_R1 = STORED_SAMPLE + "_r1";
    static final String STORED_SAMPLE_R2_3UM = STORED_SAMPLE + "_r2_3um";

    static final String STORED_SAMPLE_CON = "StoredSampleCon";
    static final String STORED_SAMPLE_CON_R1 = STORED_SAMPLE_CON + "_r1";
    static final String STORED_SAMPLE_CON_R2_3UM = STORED_SAMPLE_CON + "_r2_3um";

    private static final String STORING_ACTIVITY_R1 = "09a6f65fe087c3806631e8926fa3cae8e049cfdfa2b0b63fe6de04bc88144e80";
    private static final String STORING_ACTIVITY_R2_3um = "67c557a442ca2446b987928fb47c4bbbd59c54395e61f01949d76c06e0106925";
    private static final String BASE_SAMPLE = "BigProject_belgium_water_10m";
    static final String SAMPLE_R1 = BASE_SAMPLE + "_1000um_r1";
    static final String SAMPLE_R2_3UM = BASE_SAMPLE + "_1000um_r2_3um";

    public Dataset1Transformer(ProvFactory pF, ICpmProvFactory cPF) {
        super(pF, cPF);
    }

    @Override
    protected Document createTI(IndexedDocument indexedDS) {
        TraversalInformation ti = new TraversalInformation();
        ti.setBundleName(newQNWithBlankNS(SAMPLING + "Bundle"));

        MainActivity mA = new MainActivity(newQNWithBlankNS(SAMPLING));
        ti.setMainActivity(mA);

        mA.setHasPart(indexedDS.getActivities().stream().map(Identifiable::getId).toList());

        ForwardConnector fcR1 = new ForwardConnector(newQNWithBlankNS(STORED_SAMPLE_CON_R1));

        ForwardConnector fcR1Spec = new ForwardConnector(newQNWithBlankNS(STORED_SAMPLE_CON_R1 + "_Spec"));
        fcR1Spec.setReferencedBundleId(newQNWithBlankNS(PROCESSING + "Bundle"));
        fcR1Spec.setSpecializationOf(fcR1.getId());

        SpecializationOf specR1 = pF.newSpecializationOf(newQNWithBlankNS(STORED_SAMPLE_R1), fcR1.getId());
        indexedDS.add(specR1);

        ForwardConnector fcR23UM = new ForwardConnector(newQNWithBlankNS(STORED_SAMPLE_CON_R2_3UM));

        ForwardConnector fcR23UMSpec = new ForwardConnector(newQNWithBlankNS(STORED_SAMPLE_CON_R2_3UM + "_Spec"));
        fcR23UMSpec.setReferencedBundleId(newQNWithBlankNS(DNA_SEQUENCING + "Bundle"));
        fcR23UMSpec.setSpecializationOf(fcR23UM.getId());

        SpecializationOf specR23UM = pF.newSpecializationOf(newQNWithBlankNS(STORED_SAMPLE_R2_3UM), fcR23UM.getId());
        indexedDS.add(specR23UM);

        mA.setGenerated(List.of(fcR1.getId(), fcR23UM.getId()));

        ForwardConnector fCIden = new ForwardConnector(newQNWithBlankNS(IDENTIFIED_SPECIES_CON));
        fCIden.setDerivedFrom(List.of(fcR1.getId()));

        ForwardConnector fCIdenSpec = new ForwardConnector(newQNWithBlankNS(IDENTIFIED_SPECIES_CON + "Spec"));
        fCIdenSpec.setReferencedBundleId(newQNWithBlankNS(SPECIES_IDENTIFICATION + "Bundle"));
        fCIdenSpec.setSpecializationOf(fCIden.getId());

        ti.getForwardConnectors().addAll(List.of(fcR1, fcR1Spec, fcR23UM, fcR23UMSpec, fCIden, fCIdenSpec));

        return mapper.map(ti);
    }

    @Override
    protected void modifyDS(IndexedDocument indexedDS) {
        Entity storedR1 = pF.newEntity(newQNWithBlankNS(STORED_SAMPLE_R1));
        storedR1.getType().addAll(indexedDS.getEntity(newQNWithBlankNS(SAMPLE_R1)).getType());
        indexedDS.add(storedR1);

        WasGeneratedBy storingGenR1 = pF.newWasGeneratedBy(null, storedR1.getId(), newQnWithGenNS(STORING_ACTIVITY_R1));
        indexedDS.add(storingGenR1);

        Entity storedR23UM = pF.newEntity(newQNWithBlankNS(STORED_SAMPLE_R2_3UM));
        storedR23UM.getType().addAll(indexedDS.getEntity(newQNWithBlankNS(SAMPLE_R2_3UM)).getType());
        indexedDS.add(storedR23UM);

        WasGeneratedBy storingGenR23UM = pF.newWasGeneratedBy(null, storedR23UM.getId(), newQnWithGenNS(STORING_ACTIVITY_R2_3um));
        indexedDS.add(storingGenR23UM);
    }

}
