package cz.muni.fi.cpm.deserialization.mou;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import cz.muni.fi.cpm.deserialization.mou.schema.Patient;
import cz.muni.fi.cpm.deserialization.mou.transform.AcquisitionTransformer;
import cz.muni.fi.cpm.deserialization.mou.transform.PatientTransformer;
import cz.muni.fi.cpm.deserialization.mou.transform.StoreTransformer;
import cz.muni.fi.cpm.deserialization.pbm.PbmFactory;
import cz.muni.fi.cpm.merged.CpmMergedFactory;
import cz.muni.fi.cpm.model.CpmDocument;
import cz.muni.fi.cpm.model.ICpmFactory;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.ProvFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static cz.muni.fi.cpm.constants.PathConstants.TEST_RESOURCES;
import static cz.muni.fi.cpm.deserialization.mou.constants.NameConstants.ACQUISITION;
import static cz.muni.fi.cpm.deserialization.mou.constants.NameConstants.STORE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CpmMouTest {
    private static final String MOU_FOLDER = "mou" + File.separator;

    private final ProvFactory pF;
    private final ICpmProvFactory cPF;
    private final ICpmFactory cF;
    private final PbmFactory pbmF;

    public CpmMouTest() {
        pF = new org.openprovenance.prov.vanilla.ProvFactory();
        cPF = new CpmProvFactory(pF);
        cF = new CpmMergedFactory(pF);
        pbmF = new PbmFactory(pF);
    }

    @Test
    public void toDocument_withMouTestDataAcquisition_serialisesSuccessfully() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Patient patient;
        try (InputStream inputStream = classLoader.getResourceAsStream(MOU_FOLDER + "test-data.xml")) {
            XmlMapper xmlMapper = new XmlMapper();
            patient = xmlMapper.readValue(inputStream, Patient.class);
        } catch (IOException e) {
            fail();
            throw new RuntimeException(e);
        }

        InteropFramework interop = new InteropFramework();
        PatientTransformer aT = new AcquisitionTransformer(patient, pF, cPF, pbmF);
        for (Document doc : aT.toDocuments()) {
            CpmDocument cpmDoc = new CpmDocument(doc, pF, cPF, cF);
            assertEquals(4, cpmDoc.getTraversalInformationPart().size());
            assertEquals(1, cpmDoc.getDomainSpecificPart().size());
            assertEquals(1, cpmDoc.getCrossPartEdges().size());

            Bundle bundle = (Bundle) doc.getStatementOrBundle().getFirst();
            String fileName = TEST_RESOURCES + MOU_FOLDER + ACQUISITION + File.separator + bundle.getId().getLocalPart().replace(":", "-");
            interop.writeDocument(fileName + ".provn", doc);
            interop.writeDocument(fileName + ".svg", doc);
        }
    }


    @Test
    public void toDocument_withMouTestDataStore_serialisesSuccessfully() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Patient patient;
        try (InputStream inputStream = classLoader.getResourceAsStream(MOU_FOLDER + "test-data.xml")) {
            XmlMapper xmlMapper = new XmlMapper();
            patient = xmlMapper.readValue(inputStream, Patient.class);
        } catch (IOException e) {
            fail();
            throw new RuntimeException(e);
        }

        InteropFramework interop = new InteropFramework();
        PatientTransformer sT = new StoreTransformer(patient, pF, cPF, pbmF);
        for (Document doc : sT.toDocuments()) {
            CpmDocument cpmDoc = new CpmDocument(doc, pF, cPF, cF);
            assertEquals(4, cpmDoc.getTraversalInformationPart().size());
            assertEquals(5, cpmDoc.getDomainSpecificPart().size());
            assertEquals(2, cpmDoc.getCrossPartEdges().size());

            Bundle bundle = (Bundle) doc.getStatementOrBundle().getFirst();
            String fileName = TEST_RESOURCES + MOU_FOLDER + STORE + File.separator + bundle.getId().getLocalPart().replace(":", "-");
            interop.writeDocument(fileName + ".provn", doc);
            interop.writeDocument(fileName + ".svg", doc);
        }
    }


}
