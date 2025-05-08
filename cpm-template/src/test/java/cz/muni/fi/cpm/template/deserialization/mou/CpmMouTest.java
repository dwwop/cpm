package cz.muni.fi.cpm.template.deserialization.mou;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import cz.muni.fi.cpm.merged.CpmMergedFactory;
import cz.muni.fi.cpm.model.CpmDocument;
import cz.muni.fi.cpm.model.ICpmFactory;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import cz.muni.fi.cpm.template.deserialization.mou.schema.Patient;
import cz.muni.fi.cpm.template.deserialization.mou.transform.acquisition.CpmAcquisitionTransformer;
import cz.muni.fi.cpm.template.deserialization.mou.transform.acquisition.ProvAcquisitionTransformer;
import cz.muni.fi.cpm.template.deserialization.mou.transform.storage.CpmStorageTransformer;
import cz.muni.fi.cpm.template.deserialization.mou.transform.storage.ProvStorageTransformer;
import cz.muni.fi.cpm.template.deserialization.pbm.PbmFactory;
import cz.muni.fi.cpm.template.util.GraphvizChecker;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.ProvFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

import static cz.muni.fi.cpm.template.constants.PathConstants.TEST_RESOURCES;
import static cz.muni.fi.cpm.template.deserialization.mou.constants.NameConstants.ACQUISITION;
import static cz.muni.fi.cpm.template.deserialization.mou.constants.NameConstants.STORAGE;
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
        List<Document> pATDocs = new ProvAcquisitionTransformer(patient, pF, cPF, pbmF).toDocuments();
        List<Document> cATDocs = new CpmAcquisitionTransformer(patient, pF, cPF, pbmF).toDocuments();
        for (int i = 0; i < pATDocs.size(); i++) {
            Document pATDoc = pATDocs.get(i);
            assertAcquisition(pATDoc);

            Document cATDoc = cATDocs.get(i);
            assertAcquisition(cATDoc);

            Bundle pATBundle = (Bundle) pATDoc.getStatementOrBundle().getFirst();
            Bundle cATBundle = (Bundle) cATDoc.getStatementOrBundle().getFirst();
            assertEquals(new HashSet<>(pATBundle.getStatement()), new HashSet<>(cATBundle.getStatement()));

            Bundle bundle = (Bundle) cATDoc.getStatementOrBundle().getFirst();
            String fileName = TEST_RESOURCES + MOU_FOLDER + ACQUISITION + File.separator + bundle.getId().getLocalPart().replace(":", "-");
            interop.writeDocument(fileName + ".provn", cATDoc);
            if (GraphvizChecker.isGraphvizInstalled()) {
                interop.writeDocument(fileName + ".svg", cATDoc);
            }
        }
    }

    private void assertAcquisition(Document doc) {
        CpmDocument cpmDoc = new CpmDocument(doc, pF, cPF, cF);
        assertEquals(4, cpmDoc.getTraversalInformationPart().size());
        assertEquals(2, cpmDoc.getDomainSpecificPart().size());
        assertEquals(1, cpmDoc.getCrossPartEdges().size());
    }

    @Test
    public void toDocument_withMouTestDataStorage_serialisesSuccessfully() {
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
        List<Document> pATDocs = new ProvStorageTransformer(patient, pF, cPF, pbmF).toDocuments();
        List<Document> cATDocs = new CpmStorageTransformer(patient, pF, cPF, pbmF).toDocuments();
        for (int i = 0; i < pATDocs.size(); i++) {
            Document pATDoc = pATDocs.get(i);
            assertStorage(pATDoc);

            Document cATDoc = cATDocs.get(i);
            assertStorage(cATDoc);

            Bundle pATBundle = (Bundle) pATDoc.getStatementOrBundle().getFirst();
            Bundle cATBundle = (Bundle) cATDoc.getStatementOrBundle().getFirst();
            assertEquals(new HashSet<>(pATBundle.getStatement()), new HashSet<>(cATBundle.getStatement()));

            Bundle bundle = (Bundle) cATDoc.getStatementOrBundle().getFirst();
            String fileName = TEST_RESOURCES + MOU_FOLDER + STORAGE + File.separator + bundle.getId().getLocalPart().replace(":", "-");
            interop.writeDocument(fileName + ".provn", cATDoc);
            if (GraphvizChecker.isGraphvizInstalled()) {
                interop.writeDocument(fileName + ".svg", cATDoc);
            }
        }
    }

    private void assertStorage(Document doc) {
        CpmDocument cpmDoc = new CpmDocument(doc, pF, cPF, cF);
        assertEquals(4, cpmDoc.getTraversalInformationPart().size());
        assertEquals(5, cpmDoc.getDomainSpecificPart().size());
        assertEquals(2, cpmDoc.getCrossPartEdges().size());
    }


}
