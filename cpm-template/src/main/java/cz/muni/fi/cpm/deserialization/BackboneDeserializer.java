package cz.muni.fi.cpm.deserialization;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import cz.muni.fi.cpm.CpmFactory;
import cz.muni.fi.cpm.ICpmFactory;
import cz.muni.fi.cpm.bindings.Backbone;
import org.openprovenance.prov.core.json.serialization.deserial.CustomQualifiedNameDeserializer;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.exception.UncheckedException;

import java.io.IOException;
import java.io.InputStream;

import static org.openprovenance.prov.core.json.serialization.deserial.CustomThreadConfig.JSON_CONTEXT_KEY_NAMESPACE;
import static org.openprovenance.prov.core.json.serialization.deserial.CustomThreadConfig.getAttributes;

public class BackboneDeserializer implements IBackboneDeserializer {
    private final ObjectMapper mapper;
    private final ICpmFactory cpmFactory;

    public BackboneDeserializer(ObjectMapper mapper, ICpmFactory cpmFactory) {
        this.mapper = mapper;
        this.cpmFactory = cpmFactory;
        customize(mapper);
    }

    public BackboneDeserializer() {
        this(new ObjectMapper(), new CpmFactory());
    }

    @Override
    public Backbone deserialiseBackbone(InputStream in) throws IOException {
        getAttributes().get().remove(JSON_CONTEXT_KEY_NAMESPACE);

        Backbone bb = null;
        try {
            return mapper.readValue(in, Backbone.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedException(e);
        }
    }

    @Override
    public Document deserialiseDocument(InputStream in) throws IOException {
        return deserialiseBackbone(in).toDocument(cpmFactory);
    }

    private void customize(ObjectMapper mapper) {
        SimpleModule module =
                new SimpleModule("CustomKindSerializer", new Version(1, 0, 0, null, null, null));

        module.addDeserializer(QualifiedName.class, new CustomQualifiedNameDeserializer());
        mapper.registerModule(module);
    }
}
