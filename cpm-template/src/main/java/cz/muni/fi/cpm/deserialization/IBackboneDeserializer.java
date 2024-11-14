package cz.muni.fi.cpm.deserialization;

import cz.muni.fi.cpm.bindings.Backbone;
import org.openprovenance.prov.model.Document;

import java.io.IOException;
import java.io.InputStream;

public interface IBackboneDeserializer {
    Backbone deserialiseBackbone(InputStream in) throws IOException;

    Document deserialiseDocument(InputStream in) throws IOException;
}
