package cz.muni.fi.cpm.template.deserialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import cz.muni.fi.cpm.template.constants.ExceptionConstants;
import cz.muni.fi.cpm.template.constants.VersionConstants;
import cz.muni.fi.cpm.template.mapper.ITemplateProvMapper;
import cz.muni.fi.cpm.template.mapper.v1_0.TemplateProvMapper;
import cz.muni.fi.cpm.template.schema.ITraversalInformation;
import cz.muni.fi.cpm.template.schema.v1_0.TraversalInformation;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.openprovenance.prov.core.json.serialization.deserial.CustomQualifiedNameDeserializer;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.QualifiedName;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.openprovenance.prov.core.json.serialization.deserial.CustomThreadConfig.JSON_CONTEXT_KEY_NAMESPACE;
import static org.openprovenance.prov.core.json.serialization.deserial.CustomThreadConfig.getAttributes;

public class TraversalInformationDeserializer implements ITraversalInformationDeserializer {
    private final ObjectMapper mapper;
    private final Map<String, ITemplateProvMapper<? extends ITraversalInformation>> mappers;

    public TraversalInformationDeserializer(ObjectMapper mapper, ICpmProvFactory cpmFactory) {
        this.mapper = mapper;
        customize(mapper);
        mappers = Map.of(VersionConstants.VERSION_1_0, new TemplateProvMapper(cpmFactory));
    }

    public TraversalInformationDeserializer() {
        this(new ObjectMapper(), new CpmProvFactory());
    }

    @Override
    public ITraversalInformation deserializeTI(InputStream in) throws IOException {
        getAttributes().get().remove(JSON_CONTEXT_KEY_NAMESPACE);

        JsonNode rootNode = mapper.readTree(in);
        if (!rootNode.has(VersionConstants.VERSION)) {
            throw new IllegalArgumentException(ExceptionConstants.VERSION_NOT_SPECIFIED);
        }

        String version = rootNode.get(VersionConstants.VERSION).asText();

        if (VersionConstants.VERSION_1_0.equals(version)) {
            return mapper.treeToValue(rootNode, TraversalInformation.class);
        }

        throw new UnsupportedOperationException(ExceptionConstants.UNSUPPORTED_VERSION + version);
    }

    @SuppressWarnings("unchecked")
    private <T extends ITraversalInformation> Document dispatch(T t) {
        return ((ITemplateProvMapper<T>) mappers.get(t.getVersion())).map(t);
    }

    @Override
    public Document deserializeDocument(InputStream in) throws IOException {
        ITraversalInformation ti = deserializeTI(in);
        return dispatch(ti);
    }

    private void customize(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule("CustomKindDeserializer");

        module.addDeserializer(QualifiedName.class, new CustomQualifiedNameDeserializer());
        mapper.registerModule(module);
    }
}
