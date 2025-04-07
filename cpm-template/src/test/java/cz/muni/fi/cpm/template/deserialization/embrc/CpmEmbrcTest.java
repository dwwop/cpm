package cz.muni.fi.cpm.template.deserialization.embrc;

import com.apicatalog.jsonld.JsonLdError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.muni.fi.cpm.merged.CpmMergedFactory;
import cz.muni.fi.cpm.model.CpmDocument;
import cz.muni.fi.cpm.model.ICpmFactory;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import cz.muni.fi.cpm.template.deserialization.embrc.transform.cpm.*;
import cz.muni.fi.cpm.template.deserialization.embrc.transform.jsonld.EmbrcTransformer;
import cz.muni.fi.cpm.template.deserialization.embrc.transform.jsonld.ProvContextManager;
import cz.muni.fi.cpm.template.util.GraphvizChecker;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
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
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

import static cz.muni.fi.cpm.template.constants.PathConstants.TEST_RESOURCES;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CpmEmbrcTest {
    private static final String EMBRC_FOLDER = "embrc" + File.separator;

    private final ProvFactory pF;
    private final ICpmProvFactory cPF;
    private final ICpmFactory cF;

    public CpmEmbrcTest() {
        pF = new org.openprovenance.prov.vanilla.ProvFactory();
        cPF = new CpmProvFactory(pF);
        cF = new CpmMergedFactory(pF);
    }

    private static Stream<Object[]> dataSetProvider() {
        return Stream.of(
                new Object[]{1},
                new Object[]{2},
                new Object[]{3},
                new Object[]{4}
        );
    }

    private static Stream<Object[]> documentProvider() {
        return Stream.of(
                new Object[]{Dataset1Transformer.class, 1, 6, 35, 2},
                new Object[]{Dataset2Transformer.class, 2, 4, 11, 2},
                new Object[]{Dataset3Transformer.class, 3, 4, 7, 2},
                new Object[]{Dataset4Transformer.class, 4, 3, 40, 2}
        );
    }

    @Order(0)
    @ParameterizedTest
    @MethodSource("dataSetProvider")
    public void transformEmbrcToProvJsonLD(int datasetNum) throws IOException, JsonLdError {
        String datasetFolder = "dataset" + datasetNum + File.separator;
        String inputFile = TEST_RESOURCES + EMBRC_FOLDER + datasetFolder + "Dataset" + datasetNum + "_ProvenanceMetadata.jsonld";
        String outputFile = TEST_RESOURCES + EMBRC_FOLDER + datasetFolder + "Dataset" + datasetNum + "_transformed";
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
            if (GraphvizChecker.isGraphvizInstalled()) {
                interop.writeDocument(outputFile + ".svg", doc);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Order(1)
    @ParameterizedTest
    @MethodSource("documentProvider")
    public void toDocument_withEmbrcDataset_serialisesSuccessfully(Class<DatasetTransformer> dTClass,
                                                                   int datasetNum, int tiCount, int dsCount, int cPCount) {
        String datasetFolder = "dataset" + datasetNum + File.separator;
        try (InputStream inputStream = new FileInputStream(TEST_RESOURCES + EMBRC_FOLDER + datasetFolder + "Dataset" + datasetNum + "_transformed.jsonld")) {
            InteropFramework interop = new InteropFramework();
            Document dsDoc = interop.readDocument(inputStream, Formats.ProvFormat.JSONLD);
            DatasetTransformer dT = dTClass.getDeclaredConstructor(ProvFactory.class, ICpmProvFactory.class).newInstance(pF, cPF);

            Document doc = dT.toDocument(dsDoc);

            String fileName = TEST_RESOURCES + EMBRC_FOLDER + datasetFolder + "Dataset" + datasetNum + "_cpm";
            interop.writeDocument(fileName + ".provn", doc);
            if (GraphvizChecker.isGraphvizInstalled()) {
                interop.writeDocument(fileName + ".svg", doc);
            }

            CpmDocument cpmDoc = new CpmDocument(doc, pF, cPF, cF);
            assertEquals(tiCount, cpmDoc.getTraversalInformationPart().size());
            assertEquals(dsCount, cpmDoc.getDomainSpecificPart().size());
            assertEquals(cPCount, cpmDoc.getCrossPartEdges().size());
        } catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
