package cz.muni.fi.cpm.template.serialization;

import cz.muni.fi.cpm.template.deserialization.ITraversalInformationDeserializer;
import cz.muni.fi.cpm.template.deserialization.TraversalInformationDeserializer;
import cz.muni.fi.cpm.template.mapper.v1_0.TemplateProvMapper;
import cz.muni.fi.cpm.template.schema.ITraversalInformation;
import cz.muni.fi.cpm.template.schema.v1_0.TraversalInformation;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.vanilla.ProvFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static cz.muni.fi.cpm.template.constants.PathConstants.TEST_RESOURCES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class TraversalInformationSerializerTest {
    private static final String SERIALIZE_FOLDER = "serialization" + File.separator;

    @Test
    public void serializeTI_pure_deserializesSuccessfully() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ProvFactory pF = new ProvFactory();
        CpmProvFactory cF = new CpmProvFactory(pF);
        TemplateProvMapper mapper = new TemplateProvMapper(cF);

        try (InputStream inputStream = classLoader.getResourceAsStream(SERIALIZE_FOLDER + "test.json")) {
            ITraversalInformationDeserializer deserializer = new TraversalInformationDeserializer();
            ITraversalInformation ti = deserializer.deserializeTI(inputStream);

            ITraversalInformationSerializer ser = new TraversalInformationSerializer();
            File output = new File(TEST_RESOURCES + SERIALIZE_FOLDER + "output.json");
            ser.serializeTI(ti, output);
            ITraversalInformation serTI = deserializer.deserializeTI(new FileInputStream(output));

            assertEquals(mapper.map((TraversalInformation) ti), mapper.map((TraversalInformation) serTI));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}