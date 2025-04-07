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
import java.util.function.Function;

import static cz.muni.fi.cpm.constants.DctAttributeConstants.HAS_PART;
import static cz.muni.fi.cpm.constants.DctNamespaceConstants.DCT_NS;
import static cz.muni.fi.cpm.constants.DctNamespaceConstants.DCT_PREFIX;
import static cz.muni.fi.cpm.template.deserialization.mou.constants.NameConstants.*;
import static cz.muni.fi.cpm.template.deserialization.pbm.PbmNamespaceConstants.PBM_NS;
import static cz.muni.fi.cpm.template.deserialization.pbm.PbmNamespaceConstants.PBM_PREFIX;

public class StoreTransformer extends PatientTransformer {
    public StoreTransformer(Patient patient, ProvFactory pF, ICpmProvFactory cPF, PbmFactory pbmF) {
        super(patient, pF, cPF, pbmF);
    }

    @Override
    protected Document createTI(String suffix) {
        TraversalInformation ti = new TraversalInformation();
        ti.setPrefixes(Map.of(BBMRI_PREFIX, BBMRI_NS, PBM_PREFIX, PBM_NS));
        ti.setBundleName(ti.getNamespace().qualifiedName(BBMRI_PREFIX, STORE + "-bundle" + suffix, pF));

        MainActivity mA = new MainActivity(ti.getNamespace().qualifiedName(BBMRI_PREFIX, STORE + suffix, pF));
        ti.setMainActivity(mA);

        QualifiedName aCID = ti.getNamespace().qualifiedName(BBMRI_PREFIX, ACQUISITION_CON + suffix, pF);

        BackwardConnector bC = new BackwardConnector(aCID);
        ti.getBackwardConnectors().add(bC);

        MainActivityUsed used = new MainActivityUsed(aCID);
        mA.setUsed(List.of(used));

        QualifiedName fcID = ti.getNamespace().qualifiedName(BBMRI_PREFIX, STORE_CON + suffix, pF);
        mA.setGenerated(List.of(fcID));

        ForwardConnector fC = new ForwardConnector(fcID);
        fC.setDerivedFrom(List.of(bC.getId()));
        ti.getForwardConnectors().add(fC);

        QualifiedName agentId = ti.getNamespace().qualifiedName(BBMRI_PREFIX, "UNI", pF);
        ti.setSenderAgents(List.of(new SenderAgent(agentId)));

        bC.setAttributedTo(new ConnectorAttributed(agentId));

        return mapper.map(ti);
    }

    private void addStoreDSToDocument(Document doc, String suffix, String sampleId, Function<Entity, Void> populateStore) {
        Type sampleType = pbmF.newType(PbmType.SAMPLE);

        QualifiedName patientQN = pF.newQualifiedName(BBMRI_NS, "patient-" + patient.getId(), BBMRI_PREFIX);
        Entity patientE = pF.newEntity(patientQN);
        patientE.getOther().add(newOther("sex", patient.getSex()));
        patientE.getOther().add(newOther("birthYear", patient.getYear()));
        patientE.getOther().add(newOther("month", patient.getMonth()));
        patientE.getOther().add(newOther("gaveConsent", patient.isConsent()));
        patientE.getType().add(pbmF.newType(PbmType.SOURCE));

        QualifiedName transActivity = pF.newQualifiedName(BBMRI_NS, "transport" + suffix, BBMRI_PREFIX);
        Activity trans = pF.newActivity(transActivity);
        trans.getType().add(pbmF.newType(PbmType.TRANSPORT_ACTIVITY));

        Other sampleIdTransOther = newOther("sampleId", sampleId);
        QualifiedName sampleTransQN = pF.newQualifiedName(BBMRI_NS, "sample-trans" + suffix, BBMRI_PREFIX);
        Entity sampleTrans = pF.newEntity(sampleTransQN, List.of(sampleType, sampleIdTransOther));

        WasDerivedFrom sampleTransDer = pF.newWasDerivedFrom(sampleTransQN, patientQN);

        SpecializationOf bcSpec = pF.newSpecializationOf(sampleTransQN, doc.getNamespace().qualifiedName(BBMRI_PREFIX, ACQUISITION_CON + suffix, pF));

        WasGeneratedBy transGeneratedBy = pF.newWasGeneratedBy(null, sampleTransQN, transActivity);

        QualifiedName storeActivity = pF.newQualifiedName(BBMRI_NS, "store-ac" + suffix, BBMRI_PREFIX);
        Activity storeAc = pF.newActivity(storeActivity);
        storeAc.getType().add(pbmF.newType(PbmType.STORAGE_ACTIVITY));

        Used storeUsed = pF.newUsed(storeActivity, sampleTransQN);

        Other sampleIdStoreOther = newOther("sampleId", sampleId);
        QualifiedName sampleStoreQN = pF.newQualifiedName(BBMRI_NS, "sample-store" + suffix, BBMRI_PREFIX);
        Entity sampleStore = pF.newEntity(sampleStoreQN, List.of(sampleType, sampleIdStoreOther));

        WasDerivedFrom sampleStoreDer = pF.newWasDerivedFrom(sampleStoreQN, patientQN);

        populateStore.apply(sampleStore);

        WasGeneratedBy storeGeneratedBy = pF.newWasGeneratedBy(null, sampleStoreQN, storeActivity);

        SpecializationOf fcSpec = pF.newSpecializationOf(sampleStoreQN, doc.getNamespace().qualifiedName(BBMRI_PREFIX, STORE_CON + suffix, pF));

        Bundle bundle = (Bundle) doc.getStatementOrBundle().getFirst();
        bundle.getStatement().addAll(List.of(bcSpec, trans, sampleTrans, transGeneratedBy, storeAc, storeUsed, sampleStore, storeGeneratedBy, fcSpec, patientE, sampleTransDer, sampleStoreDer));

        for (Statement s : bundle.getStatement()) {
            if (CpmUtilities.hasCpmType(s, CpmType.MAIN_ACTIVITY)) {
                Activity mainActivity = (Activity) s;
                mainActivity.getOther().add(pF.newOther(
                        pF.newQualifiedName(DCT_NS, HAS_PART, DCT_PREFIX),
                        transActivity,
                        pF.getName().PROV_QUALIFIED_NAME));
                mainActivity.getOther().add(pF.newOther(
                        pF.newQualifiedName(DCT_NS, HAS_PART, DCT_PREFIX),
                        storeActivity,
                        pF.getName().PROV_QUALIFIED_NAME));
            }
        }
    }

    @Override
    protected void addTissueDSToDoc(Document doc, String suffix, Tissue tissue) {
        addStoreDSToDocument(doc, suffix, tissue.getSampleId(), entity ->
        {
            entity.getType().add(pF.newType(
                    pF.newQualifiedName(BBMRI_NS, "tissue", BBMRI_PREFIX),
                    pF.getName().PROV_QUALIFIED_NAME));
            entity.getOther().add(newOther("storageType", "LTS"));
            entity.getOther().add(newOther("year", tissue.getYear()));
            entity.getOther().add(newOther("samplesNo", tissue.getSamplesNo()));
            entity.getOther().add(newOther("availableSamplesNo", tissue.getAvailableSamplesNo()));
            entity.getOther().add(newOther("materialType", tissue.getMaterialType()));
            entity.getOther().add(newOther("pTNM", tissue.getpTNM()));
            entity.getOther().add(newOther("morphology", tissue.getMorphology()));
            entity.getOther().add(newOther("diagnosis", tissue.getDiagnosis()));
            entity.getOther().add(newOther("cutTime", tissue.getCutTime()));
            entity.getOther().add(newOther("freezeTime", tissue.getFreezeTime()));
            entity.getOther().add(newOther("retrieved", tissue.getRetrieved()));
            return null;
        });
    }

    @Override
    protected void addDMDSToDoc(Document doc, String suffix, DiagnosisMaterial dM) {
        addStoreDSToDocument(doc, suffix, dM.getSampleId(), entity ->
        {
            entity.getType().add(pF.newType(
                    pF.newQualifiedName(BBMRI_NS, "diagnosticMaterial", BBMRI_PREFIX),
                    pF.getName().PROV_QUALIFIED_NAME));
            entity.getOther().add(newOther("storageType", "STS"));
            entity.getOther().add(newOther("materialType", dM.getMaterialType()));
            entity.getOther().add(newOther("diagnosis", dM.getDiagnosis()));
            entity.getOther().add(newOther("takingDate", dM.getTakingDate()));
            entity.getOther().add(newOther("retrieved", dM.getRetrieved()));
            return null;
        });
    }

}
