package cz.muni.fi.cpm.deserialization;

import cz.muni.fi.cpm.merged.CpmMergedFactory;
import cz.muni.fi.cpm.model.CpmDocument;
import cz.muni.fi.cpm.template.TraversalInformation;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.ProvDeserialiser;
import org.openprovenance.prov.notation.ProvSerialiser;
import org.openprovenance.prov.vanilla.ProvFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;

import static cz.muni.fi.cpm.constants.PathConstants.TEST_RESOURCES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TraversalInformationDeserializerTest {
    private static final String DESERIALIZE_FOLDER = "deserialization" + File.separator;

    @Test
    public void deserializeTI_pure_serialisesSuccessfully() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ProvFactory pF = new ProvFactory();
        CpmProvFactory cF = new CpmProvFactory(pF);

        try (InputStream inputStream = classLoader.getResourceAsStream(DESERIALIZE_FOLDER + "test.json")) {
            ITraversalInformationDeserializer deserializer = new TraversalInformationDeserializer();
            TraversalInformation ti = deserializer.deserializeTI(inputStream);
            ProvSerialiser serialiser = new ProvSerialiser(pF);
            Document doc = ti.toDocument(cF);

            File outputFile = new File(TEST_RESOURCES + DESERIALIZE_FOLDER + "output.provn");
            serialiser.serialiseDocument(new FileOutputStream(outputFile), doc, true);

            File expectedOutputFile = new File(TEST_RESOURCES + DESERIALIZE_FOLDER + "expectedOutput.provn");
            String outputContent = new String(Files.readAllBytes(outputFile.toPath()));
            String expectedOutputContent = new String(Files.readAllBytes(expectedOutputFile.toPath()));

            assertEquals(expectedOutputContent, outputContent);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void deserializeDocument_withCpmDocTransform_serialisesSuccessfully() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ProvFactory pF = new ProvFactory();
        CpmMergedFactory cF = new CpmMergedFactory();
        CpmProvFactory cPF = new CpmProvFactory();

        try (InputStream inputStream = classLoader.getResourceAsStream(DESERIALIZE_FOLDER + "test.json")) {
            ITraversalInformationDeserializer deserializer = new TraversalInformationDeserializer();
            Document doc = deserializer.deserializeDocument(inputStream);
            ProvSerialiser serialiser = new ProvSerialiser(pF);
            Document transDoc = new CpmDocument(doc, pF, cPF, cF).toDocument();

            File outputFile = new File(TEST_RESOURCES + DESERIALIZE_FOLDER + "outputTrans.provn");
            serialiser.serialiseDocument(new FileOutputStream(outputFile), transDoc, true);

            File expectedOutputFile = new File(TEST_RESOURCES + DESERIALIZE_FOLDER + "expectedOutputTrans.provn");
            String outputContent = new String(Files.readAllBytes(outputFile.toPath()));
            String expectedOutputContent = new String(Files.readAllBytes(expectedOutputFile.toPath()));

            assertEquals(expectedOutputContent, outputContent);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void deserializeDocument_withCpmDocTransform_serialisesAndOrdersCorrectly() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ProvFactory pF = new ProvFactory();
        CpmMergedFactory cF = new CpmMergedFactory();
        CpmProvFactory cPF = new CpmProvFactory();

        try (InputStream inputStream = classLoader.getResourceAsStream(DESERIALIZE_FOLDER + "expectedOutputOrdered.provn")) {
            ProvDeserialiser provDeserialiser = new org.openprovenance.prov.notation.ProvDeserialiser(pF);
            Document doc = provDeserialiser.deserialiseDocument(inputStream);

            Document transDoc = new CpmDocument(doc, pF, cPF, cF).toDocument();

            File outputFile = new File(TEST_RESOURCES + DESERIALIZE_FOLDER + "outputOrdered.provn");

            ProvSerialiser serialiser = new ProvSerialiser(pF);
            serialiser.serialiseDocument(new FileOutputStream(outputFile), transDoc, true);

            File expectedOutputFile = new File(TEST_RESOURCES + DESERIALIZE_FOLDER + "expectedOutputOrdered.provn");
            String outputContent = new String(Files.readAllBytes(outputFile.toPath()));
            String expectedOutputContent = new String(Files.readAllBytes(expectedOutputFile.toPath()));

            assertEquals(expectedOutputContent, outputContent);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}