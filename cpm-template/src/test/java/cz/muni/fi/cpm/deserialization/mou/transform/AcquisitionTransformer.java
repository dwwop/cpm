package cz.muni.fi.cpm.deserialization.mou.transform;

import cz.muni.fi.cpm.bindings.*;
import cz.muni.fi.cpm.deserialization.constants.PbmFactory;
import cz.muni.fi.cpm.deserialization.constants.PbmType;
import cz.muni.fi.cpm.deserialization.mou.schema.DiagnosisMaterial;
import cz.muni.fi.cpm.deserialization.mou.schema.Patient;
import cz.muni.fi.cpm.deserialization.mou.schema.Tissue;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.model.*;

import java.util.List;
import java.util.Map;

import static cz.muni.fi.cpm.deserialization.constants.PbmNamespaceConstants.PBM_NS;
import static cz.muni.fi.cpm.deserialization.constants.PbmNamespaceConstants.PBM_PREFIX;
import static cz.muni.fi.cpm.deserialization.mou.constants.NameConstants.ACQUISITION;
import static cz.muni.fi.cpm.deserialization.mou.constants.NameConstants.ACQUISITION_CON;

public class AcquisitionTransformer extends PatientTransformer {

    public AcquisitionTransformer(ProvFactory pF, ICpmProvFactory cPF, PbmFactory pbmF) {
        super(pF, cPF, pbmF);
    }

    private void addAcquisitionDSToDoc(Document doc, String suffix, String sampleId) {
        Type pbmType = pbmF.newType(PbmType.SAMPLE);

        Other sampleIdOther = newOther("sampleId", sampleId);
        QualifiedName sampleQN = pF.newQualifiedName(BBMRI_NS, "sample" + suffix, BBMRI_PREFIX);
        Entity sample = pF.newEntity(sampleQN, List.of(pbmType, sampleIdOther));

        SpecializationOf spec = pF.newSpecializationOf(sampleQN, doc.getNamespace().qualifiedName(BBMRI_PREFIX, ACQUISITION_CON + suffix, pF));

        Bundle bundle = (Bundle) doc.getStatementOrBundle().getFirst();
        bundle.getStatement().add(sample);
        bundle.getStatement().add(spec);
    }

    @Override
    protected Document createBB(Patient patient, String suffix) {
        Backbone bb = new Backbone();

        bb.setPrefixes(Map.of(BBMRI_PREFIX, BBMRI_NS, PBM_PREFIX, PBM_NS));
        bb.setBundleName(bb.getNamespace().qualifiedName(BBMRI_PREFIX, ACQUISITION + "-bundle" + suffix, pF));

        MainActivity mA = new MainActivity(bb.getNamespace().qualifiedName(BBMRI_PREFIX, ACQUISITION + suffix, pF));
        bb.setMainActivity(mA);

        QualifiedName fcID = bb.getNamespace().qualifiedName(BBMRI_PREFIX, ACQUISITION_CON + suffix, pF);
        mA.setGenerated(List.of(fcID));

        ForwardConnector fC = new ForwardConnector(fcID);
        bb.getForwardConnectors().add(fC);

        QualifiedName agentId = bb.getNamespace().qualifiedName(BBMRI_PREFIX, patient.getBiobank(), pF);
        ReceiverAgent rA = new ReceiverAgent(agentId);
        bb.setReceiverAgents(List.of(rA));

        fC.setAttributedTo(new ConnectorAttributed(agentId));

        return bb.toDocument(cPF);
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
