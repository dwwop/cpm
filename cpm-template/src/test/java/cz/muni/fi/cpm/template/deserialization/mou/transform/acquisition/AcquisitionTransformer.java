package cz.muni.fi.cpm.template.deserialization.mou.transform.acquisition;

import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.model.CpmUtilities;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import cz.muni.fi.cpm.template.deserialization.mou.schema.DiagnosisMaterial;
import cz.muni.fi.cpm.template.deserialization.mou.schema.Patient;
import cz.muni.fi.cpm.template.deserialization.mou.schema.Tissue;
import cz.muni.fi.cpm.template.deserialization.mou.transform.PatientTransformer;
import cz.muni.fi.cpm.template.deserialization.pbm.PbmFactory;
import cz.muni.fi.cpm.template.deserialization.pbm.PbmType;
import org.openprovenance.prov.model.*;

import java.util.List;

import static cz.muni.fi.cpm.constants.DctAttributeConstants.HAS_PART;
import static cz.muni.fi.cpm.constants.DctNamespaceConstants.DCT_NS;
import static cz.muni.fi.cpm.constants.DctNamespaceConstants.DCT_PREFIX;
import static cz.muni.fi.cpm.template.deserialization.mou.constants.NameConstants.ACQUISITION_CON;

public abstract class AcquisitionTransformer extends PatientTransformer {

    public AcquisitionTransformer(Patient patient, ProvFactory pF, ICpmProvFactory cPF, PbmFactory pbmF) {
        super(patient, pF, cPF, pbmF);
    }

    private void addAcquisitionDSToDoc(Document doc, String suffix, String sampleId) {
        Type pbmType = pbmF.newType(PbmType.SAMPLE);

        Other sampleIdOther = newOther("sampleId", sampleId);
        QualifiedName sampleQN = pF.newQualifiedName(BBMRI_NS, "sample" + suffix, BBMRI_PREFIX);
        Entity sample = pF.newEntity(sampleQN, List.of(pbmType, sampleIdOther));

        SpecializationOf spec = pF.newSpecializationOf(sampleQN, doc.getNamespace().qualifiedName(BBMRI_PREFIX, ACQUISITION_CON + suffix, pF));

        QualifiedName acqActivity = pF.newQualifiedName(BBMRI_NS, "acquisition-ac" + suffix, BBMRI_PREFIX);
        Activity acq = pF.newActivity(acqActivity);
        acq.getType().add(pbmF.newType(PbmType.ACQUISITION_ACTIVITY));

        WasGeneratedBy acqGeneratedBy = pF.newWasGeneratedBy(null, sampleQN, acqActivity);

        Bundle bundle = (Bundle) doc.getStatementOrBundle().getFirst();
        bundle.getStatement().add(sample);
        bundle.getStatement().add(spec);
        bundle.getStatement().add(acq);
        bundle.getStatement().add(acqGeneratedBy);

        for (Statement s : bundle.getStatement()) {
            if (CpmUtilities.hasCpmType(s, CpmType.MAIN_ACTIVITY)) {
                Activity mainActivity = (Activity) s;
                mainActivity.getOther().add(pF.newOther(
                        pF.newQualifiedName(DCT_NS, HAS_PART, DCT_PREFIX),
                        acqActivity,
                        pF.getName().PROV_QUALIFIED_NAME));
            }
        }
    }

    @Override
    protected void addTissueDSToDoc(Document doc, String suffix, Tissue tissue) {
        addAcquisitionDSToDoc(doc, suffix, tissue.getSampleId());
    }

    @Override
    protected void addDMDSToDoc(Document doc, String suffix, DiagnosisMaterial dM) {
        addAcquisitionDSToDoc(doc, suffix, dM.getSampleId());
    }
}
