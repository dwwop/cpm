package cz.muni.fi.cpm.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import cz.muni.fi.cpm.bindings.Backbone;
import org.openprovenance.prov.core.json.serialization.serial.CustomDateSerializer;
import org.openprovenance.prov.core.json.serialization.serial.CustomQualifiedNameSerializer;
import org.openprovenance.prov.vanilla.QualifiedName;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.IOException;

import static org.openprovenance.prov.core.json.serialization.deserial.CustomThreadConfig.JSON_CONTEXT_KEY_NAMESPACE;
import static org.openprovenance.prov.core.json.serialization.deserial.CustomThreadConfig.getAttributes;

public class BackboneSerializer implements IBackboneSerializer {
    private final ObjectMapper mapper;

    public BackboneSerializer() {
        this(new ObjectMapper());
    }

    public BackboneSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
        customize(mapper);
    }

    @Override
    public void serializeBackbone(Backbone bb, File file) throws IOException {
        getAttributes().get().remove(JSON_CONTEXT_KEY_NAMESPACE);
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, bb);
    }

    private void customize(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule("CustomKindSerializer");
        module.addSerializer(QualifiedName.class, new CustomQualifiedNameSerializer());
        module.addSerializer(XMLGregorianCalendar.class, new CustomDateSerializer());
        mapper.registerModule(module);
    }
}
