package cz.muni.fi.cpm.template.deserialization;

import cz.muni.fi.cpm.template.schema.ITraversalInformation;
import org.openprovenance.prov.model.Document;

import java.io.IOException;
import java.io.InputStream;

public interface ITraversalInformationDeserializer {
    ITraversalInformation deserializeTI(InputStream in) throws IOException;

    Document deserializeDocument(InputStream in) throws IOException;
}
