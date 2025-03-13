package cz.muni.fi.cpm.deserialization;

import cz.muni.fi.cpm.template.TraversalInformation;
import org.openprovenance.prov.model.Document;

import java.io.IOException;
import java.io.InputStream;

public interface ITraversalInformationDeserializer {
    TraversalInformation deserializeTI(InputStream in) throws IOException;

    Document deserializeDocument(InputStream in) throws IOException;
}
