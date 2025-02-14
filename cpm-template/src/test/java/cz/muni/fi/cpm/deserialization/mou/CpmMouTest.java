package cz.muni.fi.cpm.deserialization.mou;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import cz.muni.fi.cpm.bindings.*;
import cz.muni.fi.cpm.deserialization.constants.PbmType;
import cz.muni.fi.cpm.deserialization.mou.schema.DiagnosisMaterial;
import cz.muni.fi.cpm.deserialization.mou.schema.Patient;
import cz.muni.fi.cpm.deserialization.mou.schema.Tissue;
import cz.muni.fi.cpm.merged.CpmMergedFactory;
import cz.muni.fi.cpm.model.CpmDocument;
import cz.muni.fi.cpm.model.ICpmFactory;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cz.muni.fi.cpm.constants.PathConstants.TEST_RESOURCES;
import static cz.muni.fi.cpm.deserialization.constants.PbmNamespaceConstants.PBM_NS;
import static cz.muni.fi.cpm.deserialization.constants.PbmNamespaceConstants.PBM_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CpmMouTest {
    private static final String MOU_FOLDER = "mou" + File.separator;

    private static final String BBMRI_NS = "http://www.bbmri.cz/schemas/biobank/data";
    private static final String BBMRI_PREFIX = "bbmri";

    private static final String SUFFIX_TEMPLATE = "-patient-%s-%s";
    private static final String ACQUISITION = "acquisition";
    private static final String ACQUISITION_FC = "sampleAcqConnector";


    @Test
    public void toDocument_withMouTestData_serialisesSuccessfully() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ProvFactory pF = new org.openprovenance.prov.vanilla.ProvFactory();
        ICpmProvFactory cPF = new CpmProvFactory(pF);
        ICpmFactory cF = new CpmMergedFactory(pF);

        Patient patient = null;
        try (InputStream inputStream = classLoader.getResourceAsStream(MOU_FOLDER + "test-data.xml")) {
            XmlMapper xmlMapper = new XmlMapper();
            patient = xmlMapper.readValue(inputStream, Patient.class);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

        InteropFramework interop = new InteropFramework();
        for (Document doc : createAcquisitionDocs(patient, pF, cPF)) {
            CpmDocument cpmDoc = new CpmDocument(doc, pF, cPF, cF);
            assertEquals(3, cpmDoc.getBackbonePart().size());
            assertEquals(1, cpmDoc.getDomainSpecificPart().size());
            assertEquals(1, cpmDoc.getCrossPartEdges().size());

            Bundle bundle = (Bundle) doc.getStatementOrBundle().getFirst();
            String fileName = TEST_RESOURCES + MOU_FOLDER + ACQUISITION + File.separator + bundle.getId().getLocalPart().replace(":", "-");
            interop.writeDocument(fileName + ".provn", doc);
            interop.writeDocument(fileName + ".jpg", doc);
        }
    }

    private List<Document> createAcquisitionDocs(Patient patient, ProvFactory pF, ICpmProvFactory cPF) {
        List<Document> docs = new ArrayList<>();
        for (Tissue tissue : patient.getLts().getTissues()) {
            String suffix = String.format(SUFFIX_TEMPLATE, patient.getId(), tissue.getSampleId());
            Document doc = createAcquisitionBB(patient, suffix, pF, cPF);

            addAcquisitionDSToDoc(doc, suffix, tissue.getSampleId(), pF);
            docs.add(doc);
        }

        for (DiagnosisMaterial dM : patient.getSts().getDiagnosisMaterials()) {
            String suffix = String.format(SUFFIX_TEMPLATE, patient.getId(), dM.getSampleId());
            Document doc = createAcquisitionBB(patient, suffix, pF, cPF);

            addAcquisitionDSToDoc(doc, suffix, dM.getSampleId(), pF);
            docs.add(doc);
        }
        return docs;
    }

    private Document createAcquisitionBB(Patient patient, String suffix, ProvFactory pF, ICpmProvFactory cPF) {
        Backbone bb = new Backbone();

        bb.setPrefixes(Map.of(BBMRI_PREFIX, BBMRI_NS, PBM_PREFIX, PBM_NS));
        bb.setBundleName(bb.getNamespace().qualifiedName(BBMRI_PREFIX, ACQUISITION + "-bundle" + suffix, pF));

        MainActivity mA = new MainActivity();
        bb.setMainActivity(mA);

        String mAIDString = ACQUISITION + suffix;
        mA.setId(bb.getNamespace().qualifiedName(BBMRI_PREFIX, mAIDString, pF));

        String fCIDString = ACQUISITION_FC + suffix;
        QualifiedName fcID = bb.getNamespace().qualifiedName(BBMRI_PREFIX, fCIDString, pF);
        mA.setGenerated(List.of(fcID));

        ReceiverAgent rA = new ReceiverAgent();
        bb.setReceiverAgents(List.of(rA));
        QualifiedName agentId = bb.getNamespace().qualifiedName(BBMRI_PREFIX, patient.getBiobank(), pF);
        rA.setId(agentId);

        ForwardConnector fC = new ForwardConnector();
        bb.getForwardConnectors().add(fC);
        fC.setId(fcID);

        ConnectorAttributed cA = new ConnectorAttributed();
        cA.setAgentId(agentId);
        fC.setAttributedTo(cA);

        return bb.toDocument(cPF);
    }

    private void addAcquisitionDSToDoc(Document doc, String suffix, String sampleId, ProvFactory pF) {
        Type pbmType = pF.newType(
                pF.newQualifiedName(PBM_NS, PbmType.SAMPLE.toString(), PBM_PREFIX),
                pF.getName().PROV_QUALIFIED_NAME);

        Other sampleIdOther = pF.newOther(
                pF.newQualifiedName(BBMRI_NS, "sampleId", BBMRI_PREFIX),
                sampleId,
                pF.getName().XSD_STRING);
        QualifiedName sampleQN = pF.newQualifiedName(BBMRI_NS, "sample" + suffix, BBMRI_PREFIX);
        Entity sample = pF.newEntity(sampleQN, List.of(pbmType, sampleIdOther));

        SpecializationOf spec = pF.newSpecializationOf(sampleQN, doc.getNamespace().qualifiedName(BBMRI_PREFIX, ACQUISITION_FC + suffix, pF));

        Bundle bundle = (Bundle) doc.getStatementOrBundle().getFirst();
        bundle.getStatement().add(sample);
        bundle.getStatement().add(spec);
    }
}
