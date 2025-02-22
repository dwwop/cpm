package cz.muni.fi.cpm.deserialization.embrc;

import com.apicatalog.jsonld.JsonLdError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.muni.fi.cpm.deserialization.embrc.transform.cpm.Dataset1Transformer;
import cz.muni.fi.cpm.deserialization.embrc.transform.cpm.Dataset2Transformer;
import cz.muni.fi.cpm.deserialization.embrc.transform.cpm.Dataset3Transformer;
import cz.muni.fi.cpm.deserialization.embrc.transform.cpm.DatasetTransformer;
import cz.muni.fi.cpm.deserialization.embrc.transform.jsonld.EmbrcTransformer;
import cz.muni.fi.cpm.deserialization.embrc.transform.jsonld.ProvContextManager;
import cz.muni.fi.cpm.deserialization.pbm.PbmFactory;
import cz.muni.fi.cpm.merged.CpmMergedFactory;
import cz.muni.fi.cpm.model.CpmDocument;
import cz.muni.fi.cpm.model.ICpmFactory;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.interop.Formats;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import static cz.muni.fi.cpm.constants.PathConstants.TEST_RESOURCES;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CpmEmbrcTest {
    private static final String EMBRC_FOLDER = "embrc" + File.separator;
    private static final String TRANSFORMED_FOLDER = "transformed" + File.separator;

    private static final String DATASET1_FOLDER = "dataset1" + File.separator;
    private static final String DATASET2_FOLDER = "dataset2" + File.separator;
    private static final String DATASET3_FOLDER = "dataset3" + File.separator;

    private final ProvFactory pF;
    private final ICpmProvFactory cPF;
    private final ICpmFactory cF;
    private final PbmFactory pbmF;

    public CpmEmbrcTest() {
        pF = new org.openprovenance.prov.vanilla.ProvFactory();
        cPF = new CpmProvFactory(pF);
        cF = new CpmMergedFactory(pF);
        pbmF = new PbmFactory(pF);
    }

    private static Stream<Object[]> dataSetProvider() {
        return Stream.of(
                new Object[]{"Dataset1_ProvenanceMetadata.jsonld", "Dataset1_transformed"},
                new Object[]{"Dataset2_ProvenanceMetadata.jsonld", "Dataset2_transformed"},
                new Object[]{"Dataset3_ProvenanceMetadata.jsonld", "Dataset3_transformed"},
                new Object[]{"Dataset4_ProvenanceMetadata.jsonld", "Dataset4_transformed"}
        );
    }

    @Order(0)
    @ParameterizedTest
    @MethodSource("dataSetProvider")
    public void transformEmbrcToProvJsonLD(String inputFileName, String outputFileName) throws IOException, JsonLdError {
        String inputFile = TEST_RESOURCES + EMBRC_FOLDER + inputFileName;
        String outputFile = TEST_RESOURCES + EMBRC_FOLDER + TRANSFORMED_FOLDER + outputFileName;
        String context = TEST_RESOURCES + EMBRC_FOLDER + "context.jsonld";

        ObjectMapper mapper = new ObjectMapper();

        ProvContextManager pCM = new ProvContextManager(mapper);
        ObjectNode root = pCM.enforce(inputFile, context);

        EmbrcTransformer transformer = new EmbrcTransformer(mapper, root);
        ObjectNode jsonLd = transformer.toProvJsonLD();

        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFile + ".jsonld"), jsonLd);

        try (InputStream inputStream = new FileInputStream(outputFile + ".jsonld")) {
            InteropFramework interop = new InteropFramework();
            Document doc = interop.readDocument(inputStream, Formats.ProvFormat.JSONLD);
            interop.writeDocument(outputFile + ".svg", doc);
            System.out.println();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void toDocument_withEmbrcDataset1_serialisesSuccessfully() {
        try (InputStream inputStream = new FileInputStream(TEST_RESOURCES + EMBRC_FOLDER + TRANSFORMED_FOLDER + "Dataset1_transformed.jsonld")) {
            InteropFramework interop = new InteropFramework();
            Document dsDoc = interop.readDocument(inputStream, Formats.ProvFormat.JSONLD);
            Dataset1Transformer dT = new Dataset1Transformer(pF, cPF);

            Document doc = dT.toDocument(dsDoc);
            CpmDocument cpmDoc = new CpmDocument(doc, pF, cPF, cF);

            assertEquals(3, cpmDoc.getBackbonePart().size());
            assertEquals(30, cpmDoc.getDomainSpecificPart().size());
            assertEquals(2, cpmDoc.getCrossPartEdges().size());

            String fileName = TEST_RESOURCES + EMBRC_FOLDER + DATASET1_FOLDER + "Dataset1_cpm";
            interop.writeDocument(fileName + ".provn", doc);
            interop.writeDocument(fileName + ".svg", doc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void toDocument_withEmbrcDataset2_serialisesSuccessfully() {
        try (InputStream inputStream = new FileInputStream(TEST_RESOURCES + EMBRC_FOLDER + TRANSFORMED_FOLDER + "Dataset2_transformed.jsonld")) {
            InteropFramework interop = new InteropFramework();
            Document dsDoc = interop.readDocument(inputStream, Formats.ProvFormat.JSONLD);
            DatasetTransformer dT = new Dataset2Transformer(pF, cPF);

            Document doc = dT.toDocument(dsDoc);
            CpmDocument cpmDoc = new CpmDocument(doc, pF, cPF, cF);

            assertEquals(3, cpmDoc.getBackbonePart().size());
            assertEquals(9, cpmDoc.getDomainSpecificPart().size());
            assertEquals(2, cpmDoc.getCrossPartEdges().size());

            String fileName = TEST_RESOURCES + EMBRC_FOLDER + DATASET2_FOLDER + "Dataset2_cpm";
            interop.writeDocument(fileName + ".provn", doc);
            interop.writeDocument(fileName + ".svg", doc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void toDocument_withEmbrcDataset3_serialisesSuccessfully() {
        try (InputStream inputStream = new FileInputStream(TEST_RESOURCES + EMBRC_FOLDER + TRANSFORMED_FOLDER + "Dataset3_transformed.jsonld")) {
            InteropFramework interop = new InteropFramework();
            Document dsDoc = interop.readDocument(inputStream, Formats.ProvFormat.JSONLD);
            DatasetTransformer dT = new Dataset3Transformer(pF, cPF);

            Document doc = dT.toDocument(dsDoc);
            CpmDocument cpmDoc = new CpmDocument(doc, pF, cPF, cF);

            assertEquals(3, cpmDoc.getBackbonePart().size());
            assertEquals(6, cpmDoc.getDomainSpecificPart().size());
            assertEquals(2, cpmDoc.getCrossPartEdges().size());

            String fileName = TEST_RESOURCES + EMBRC_FOLDER + DATASET3_FOLDER + "Dataset3_cpm";
            interop.writeDocument(fileName + ".provn", doc);
            interop.writeDocument(fileName + ".svg", doc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
