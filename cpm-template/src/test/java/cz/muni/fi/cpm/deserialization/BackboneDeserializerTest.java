package cz.muni.fi.cpm.deserialization;

import cz.muni.fi.cpm.bindings.Backbone;
import cz.muni.fi.cpm.vannila.CpmFactory;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.notation.ProvSerialiser;
import org.openprovenance.prov.vanilla.ProvFactory;

import java.io.FileOutputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class BackboneDeserializerTest {

    @Test
    public void deserialiseBackbone() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream("test.json")) {
            IBackboneDeserializer deserializer = new BackboneDeserializer();
            Backbone bb = deserializer.deserialiseBackbone(inputStream);
            ProvSerialiser serialiser = new ProvSerialiser(new ProvFactory());
            Document doc = bb.toDocument(new CpmFactory());
            serialiser.serialiseDocument(new FileOutputStream("src/test/resources/output.provn"), doc, true);
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}