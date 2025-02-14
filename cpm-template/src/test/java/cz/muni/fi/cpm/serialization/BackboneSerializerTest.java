package cz.muni.fi.cpm.serialization;

import cz.muni.fi.cpm.bindings.Backbone;
import cz.muni.fi.cpm.deserialization.BackboneDeserializer;
import cz.muni.fi.cpm.deserialization.IBackboneDeserializer;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.vanilla.ProvFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static cz.muni.fi.cpm.constants.PathConstants.TEST_RESOURCES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class BackboneSerializerTest {
    private static final String SERIALIZE_FOLDER = "serialization" + File.separator;

    @Test
    public void serializeBackbone_pure_deserializesSuccessfully() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ProvFactory pF = new ProvFactory();
        CpmProvFactory cF = new CpmProvFactory(pF);

        try (InputStream inputStream = classLoader.getResourceAsStream(SERIALIZE_FOLDER + "test.json")) {
            IBackboneDeserializer deserializer = new BackboneDeserializer();
            Backbone bb = deserializer.deserializeBackbone(inputStream);

            IBackboneSerializer ser = new BackboneSerializer();
            File output = new File(TEST_RESOURCES + SERIALIZE_FOLDER + "output.json");
            ser.serializeBackbone(bb, output);
            Backbone serBB = deserializer.deserializeBackbone(new FileInputStream(output));

            assertEquals(bb.toDocument(cF), serBB.toDocument(cF));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}