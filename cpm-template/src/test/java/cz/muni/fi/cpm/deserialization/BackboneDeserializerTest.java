package cz.muni.fi.cpm.deserialization;

import cz.muni.fi.cpm.bindings.Backbone;
import cz.muni.fi.cpm.model.CpmDocument;
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
        ProvFactory pF = new ProvFactory();
        CpmFactory cF = new CpmFactory(pF);

        try (InputStream inputStream = classLoader.getResourceAsStream("test.json")) {
            IBackboneDeserializer deserializer = new BackboneDeserializer();
            Backbone bb = deserializer.deserialiseBackbone(inputStream);
            ProvSerialiser serialiser = new ProvSerialiser(pF);
            Document doc = bb.toDocument(cF);
            serialiser.serialiseDocument(new FileOutputStream("src/test/resources/output.provn"), doc, true);
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    public void deserialiseBackboneWithCpmDocTransform() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ProvFactory pF = new ProvFactory();
        CpmFactory cF = new CpmFactory();

        try (InputStream inputStream = classLoader.getResourceAsStream("test.json")) {
            IBackboneDeserializer deserializer = new BackboneDeserializer();
            Backbone bb = deserializer.deserialiseBackbone(inputStream);
            ProvSerialiser serialiser = new ProvSerialiser(pF);
            Document doc = bb.toDocument(cF);
            Document transDoc = new CpmDocument(doc, pF).toDocument();
            serialiser.serialiseDocument(new FileOutputStream("src/test/resources/outputTrans.provn"), transDoc, true);
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}