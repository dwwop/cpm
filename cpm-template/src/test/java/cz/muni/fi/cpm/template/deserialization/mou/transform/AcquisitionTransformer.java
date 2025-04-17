package cz.muni.fi.cpm.template.deserialization.mou.transform;

import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.model.CpmUtilities;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import cz.muni.fi.cpm.template.deserialization.mou.schema.DiagnosisMaterial;
import cz.muni.fi.cpm.template.deserialization.mou.schema.Patient;
import cz.muni.fi.cpm.template.deserialization.mou.schema.Tissue;
import cz.muni.fi.cpm.template.deserialization.pbm.PbmFactory;
import cz.muni.fi.cpm.template.deserialization.pbm.PbmType;
import cz.muni.fi.cpm.template.schema.*;
import org.openprovenance.prov.model.*;

import java.util.List;
import java.util.Map;

import static cz.muni.fi.cpm.constants.DctAttributeConstants.HAS_PART;
import static cz.muni.fi.cpm.constants.DctNamespaceConstants.DCT_NS;
import static cz.muni.fi.cpm.constants.DctNamespaceConstants.DCT_PREFIX;
import static cz.muni.fi.cpm.template.deserialization.mou.constants.NameConstants.*;
import static cz.muni.fi.cpm.template.deserialization.pbm.PbmNamespaceConstants.PBM_NS;
import static cz.muni.fi.cpm.template.deserialization.pbm.PbmNamespaceConstants.PBM_PREFIX;

public class AcquisitionTransformer extends PatientTransformer {

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
    protected Document createTI(String suffix) {
        TraversalInformation ti = new TraversalInformation();

        ti.setPrefixes(Map.of(BBMRI_PREFIX, BBMRI_NS, PBM_PREFIX, PBM_NS));
        ti.setBundleName(ti.getNamespace().qualifiedName(BBMRI_PREFIX, ACQUISITION + "-bundle" + suffix, pF));

        MainActivity mA = new MainActivity(ti.getNamespace().qualifiedName(BBMRI_PREFIX, ACQUISITION + suffix, pF));
        ti.setMainActivity(mA);

        QualifiedName fcID = ti.getNamespace().qualifiedName(BBMRI_PREFIX, ACQUISITION_CON + suffix, pF);
        mA.setGenerated(List.of(fcID));

        ForwardConnector fC = new ForwardConnector(fcID);
        ti.getForwardConnectors().add(fC);

        ForwardConnector fC2 = new ForwardConnector(ti.getNamespace().qualifiedName(BBMRI_PREFIX, STORE_CON + suffix, pF));
        fC2.setDerivedFrom(List.of(fC.getId()));
        ti.getForwardConnectors().add(fC2);

        QualifiedName fcSpecId = ti.getNamespace().qualifiedName(BBMRI_PREFIX, ACQUISITION_CON + "-spec" + suffix, pF);
        ForwardConnector fCSpec = new ForwardConnector(fcSpecId);
        fCSpec.setSpecializationOf(fcID);
        ti.getForwardConnectors().add(fCSpec);

        QualifiedName agentId = ti.getNamespace().qualifiedName(BBMRI_PREFIX, patient.getBiobank(), pF);
        ReceiverAgent rA = new ReceiverAgent(agentId);
        ti.setReceiverAgents(List.of(rA));

        fCSpec.setAttributedTo(new ConnectorAttributed(agentId));

        return mapper.map(ti);
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
