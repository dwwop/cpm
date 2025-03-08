package cz.muni.fi.cpm.deserialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import cz.muni.fi.cpm.template.Backbone;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.openprovenance.prov.core.json.serialization.deserial.CustomQualifiedNameDeserializer;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.QualifiedName;

import java.io.IOException;
import java.io.InputStream;

import static org.openprovenance.prov.core.json.serialization.deserial.CustomThreadConfig.JSON_CONTEXT_KEY_NAMESPACE;
import static org.openprovenance.prov.core.json.serialization.deserial.CustomThreadConfig.getAttributes;

public class BackboneDeserializer implements IBackboneDeserializer {
    private final ObjectMapper mapper;
    private final ICpmProvFactory cpmFactory;

    public BackboneDeserializer(ObjectMapper mapper, ICpmProvFactory cpmFactory) {
        this.mapper = mapper;
        this.cpmFactory = cpmFactory;
        customize(mapper);
    }

    public BackboneDeserializer() {
        this(new ObjectMapper(), new CpmProvFactory());
    }

    @Override
    public Backbone deserializeBackbone(InputStream in) throws IOException {
        getAttributes().get().remove(JSON_CONTEXT_KEY_NAMESPACE);

        return mapper.readValue(in, Backbone.class);
    }

    @Override
    public Document deserializeDocument(InputStream in) throws IOException {
        return deserializeBackbone(in).toDocument(cpmFactory);
    }

    private void customize(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule("CustomKindDeserializer");

        module.addDeserializer(QualifiedName.class, new CustomQualifiedNameDeserializer());
        mapper.registerModule(module);
    }
}
