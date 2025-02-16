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
import java.util.function.Function;

import static cz.muni.fi.cpm.deserialization.constants.PbmNamespaceConstants.PBM_NS;
import static cz.muni.fi.cpm.deserialization.constants.PbmNamespaceConstants.PBM_PREFIX;
import static cz.muni.fi.cpm.deserialization.mou.constants.NameConstants.*;

public class StoreTransformer extends PatientTransformer {
    public StoreTransformer(ProvFactory pF, ICpmProvFactory cPF, PbmFactory pbmF) {
        super(pF, cPF, pbmF);
    }

    @Override
    protected Document createBB(Patient patient, String suffix) {
        Backbone bb = new Backbone();
        bb.setPrefixes(Map.of(BBMRI_PREFIX, BBMRI_NS, PBM_PREFIX, PBM_NS));
        bb.setBundleName(bb.getNamespace().qualifiedName(BBMRI_PREFIX, STORE + "-bundle" + suffix, pF));

        MainActivity mA = new MainActivity(bb.getNamespace().qualifiedName(BBMRI_PREFIX, STORE + suffix, pF));
        bb.setMainActivity(mA);

        QualifiedName aCID = bb.getNamespace().qualifiedName(BBMRI_PREFIX, ACQUISITION_CON + suffix, pF);

        BackwardConnector bC = new BackwardConnector(aCID);
        bb.getBackwardConnectors().add(bC);

        MainActivityUsed used = new MainActivityUsed(aCID);
        mA.setUsed(List.of(used));

        QualifiedName fcID = bb.getNamespace().qualifiedName(BBMRI_PREFIX, STORE_CON + suffix, pF);
        mA.setGenerated(List.of(fcID));

        ForwardConnector fC = new ForwardConnector(fcID);
        bb.getForwardConnectors().add(fC);

        QualifiedName agentId = bb.getNamespace().qualifiedName(BBMRI_PREFIX, patient.getBiobank(), pF);
        bb.setReceiverAgents(List.of(new ReceiverAgent(agentId)));

        bC.setAttributedTo(new ConnectorAttributed(agentId));

        fC.setAttributedTo(new ConnectorAttributed(agentId));

        return bb.toDocument(cPF);
    }

    private void addStoreDSToDocument(Document doc, String suffix, String sampleId, Function<Entity, Void> populateStore) {
        Type sampleType = pbmF.newType(PbmType.SAMPLE);

        Other sampleIdOther = newOther("sampleId", sampleId);
        QualifiedName sampleQN = pF.newQualifiedName(BBMRI_NS, "sample" + suffix, BBMRI_PREFIX);
        Entity sample = pF.newEntity(sampleQN, List.of(sampleType, sampleIdOther));

        SpecializationOf bcSpec = pF.newSpecializationOf(sampleQN, doc.getNamespace().qualifiedName(BBMRI_PREFIX, ACQUISITION_CON + suffix, pF));

        QualifiedName transActivity = pF.newQualifiedName(BBMRI_NS, "transport" + suffix, BBMRI_PREFIX);
        Activity trans = pF.newActivity(transActivity);
        trans.getType().add(pbmF.newType(PbmType.TRANSPORT_ACTIVITY));

        Used transUsed = pF.newUsed(transActivity, sampleQN);

        Other sampleIdTransOther = newOther("sampleId", sampleId);
        QualifiedName sampleTransQN = pF.newQualifiedName(BBMRI_NS, "sample-trans" + suffix, BBMRI_PREFIX);
        Entity sampleTrans = pF.newEntity(sampleTransQN, List.of(sampleType, sampleIdTransOther));

        WasGeneratedBy transGeneratedBy = pF.newWasGeneratedBy(null, sampleTransQN, transActivity);

        QualifiedName storeActivity = pF.newQualifiedName(BBMRI_NS, "store-ac" + suffix, BBMRI_PREFIX);
        Activity storeAc = pF.newActivity(storeActivity);
        storeAc.getType().add(pbmF.newType(PbmType.STORAGE_ACTIVITY));

        Used storeUsed = pF.newUsed(storeActivity, sampleTransQN);

        Other sampleIdStoreOther = newOther("sampleId", sampleId);
        QualifiedName sampleStoreQN = pF.newQualifiedName(BBMRI_NS, "sample-store" + suffix, BBMRI_PREFIX);
        Entity sampleStore = pF.newEntity(sampleStoreQN, List.of(sampleType, sampleIdStoreOther));

        populateStore.apply(sampleStore);

        WasGeneratedBy storeGeneratedBy = pF.newWasGeneratedBy(null, sampleStoreQN, storeActivity);

        SpecializationOf fcSpec = pF.newSpecializationOf(sampleStoreQN, doc.getNamespace().qualifiedName(BBMRI_PREFIX, STORE_CON + suffix, pF));

        Bundle bundle = (Bundle) doc.getStatementOrBundle().getFirst();
        bundle.getStatement().addAll(List.of(sample, bcSpec, trans, transUsed, sampleTrans, transGeneratedBy, storeAc, storeUsed, sampleStore, storeGeneratedBy, fcSpec));
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
