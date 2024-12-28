package cz.muni.fi.cpm.deserialization;

import cz.muni.fi.cpm.bindings.Backbone;
import cz.muni.fi.cpm.model.CpmDocument;
import cz.muni.fi.cpm.vannila.CpmFactory;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.notation.ProvSerialiser;
import org.openprovenance.prov.vanilla.ProvFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class BackboneDeserializerTest {
    @Test
    public void deserializeBackbone_Pure_serialisesSuccessfully() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ProvFactory pF = new ProvFactory();
        CpmFactory cF = new CpmFactory(pF);

        try (InputStream inputStream = classLoader.getResourceAsStream("test.json")) {
            IBackboneDeserializer deserializer = new BackboneDeserializer();
            Backbone bb = deserializer.deserializeBackbone(inputStream);
            ProvSerialiser serialiser = new ProvSerialiser(pF);
            Document doc = bb.toDocument(cF);

            File outputFile = new File("src/test/resources/output.provn");
            serialiser.serialiseDocument(new FileOutputStream(outputFile), doc, true);

            File expectedOutputFile = new File("src/test/resources/expectedOutput.provn");
            String outputContent = new String(Files.readAllBytes(outputFile.toPath()));
            String expectedOutputContent = new String(Files.readAllBytes(expectedOutputFile.toPath()));

            assertEquals(expectedOutputContent, outputContent);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void deserializeBackbone_withCpmDocTransform_serialisesSuccessfully() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ProvFactory pF = new ProvFactory();
        CpmFactory cF = new CpmFactory();

        try (InputStream inputStream = classLoader.getResourceAsStream("test.json")) {
            IBackboneDeserializer deserializer = new BackboneDeserializer();
            Backbone bb = deserializer.deserializeBackbone(inputStream);
            ProvSerialiser serialiser = new ProvSerialiser(pF);
            Document doc = bb.toDocument(cF);
            Document transDoc = new CpmDocument(doc, pF, new CpmFactory(pF)).toDocument();

            File outputFile = new File("src/test/resources/outputTrans.provn");
            serialiser.serialiseDocument(new FileOutputStream(outputFile), transDoc, true);

            File expectedOutputFile = new File("src/test/resources/expectedOutputTrans.provn");
            String outputContent = new String(Files.readAllBytes(outputFile.toPath()));
            String expectedOutputContent = new String(Files.readAllBytes(expectedOutputFile.toPath()));

            assertEquals(expectedOutputContent, outputContent);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}