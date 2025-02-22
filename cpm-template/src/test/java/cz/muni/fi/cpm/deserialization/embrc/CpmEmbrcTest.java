package cz.muni.fi.cpm.deserialization.embrc;

import com.apicatalog.jsonld.JsonLdError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.muni.fi.cpm.deserialization.embrc.transform.EmbrcTransformer;
import cz.muni.fi.cpm.deserialization.embrc.transform.ProvContextManager;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.interop.Formats;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import static cz.muni.fi.cpm.constants.PathConstants.TEST_RESOURCES;

public class CpmEmbrcTest {
    static final String EMBRC_FOLDER = "embrc" + File.separator;
    private static final String TRANSFORMED_FOLDER = "transformed" + File.separator;

    private static Stream<Object[]> dataSetProvider() {
        return Stream.of(
                new Object[]{"Dataset1_ProvenanceMetadata.jsonld", "Dataset1_transformed"},
                new Object[]{"Dataset2_ProvenanceMetadata.jsonld", "Dataset2_transformed"},
                new Object[]{"Dataset3_ProvenanceMetadata.jsonld", "Dataset3_transformed"},
                new Object[]{"Dataset4_ProvenanceMetadata.jsonld", "Dataset4_transformed"}
        );
    }

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
}
