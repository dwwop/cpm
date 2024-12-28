package cz.muni.fi.cpm.deserialization;

import cz.muni.fi.cpm.bindings.Backbone;
import org.openprovenance.prov.model.Document;

import java.io.IOException;
import java.io.InputStream;

public interface IBackboneDeserializer {
    Backbone deserializeBackbone(InputStream in) throws IOException;

    Document deserializeDocument(InputStream in) throws IOException;
}
