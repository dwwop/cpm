package cz.muni.fi.cpm.template.serialization;

import cz.muni.fi.cpm.template.schema.TraversalInformation;

import java.io.File;
import java.io.IOException;

public interface ITraversalInformationSerializer {
    void serializeTI(TraversalInformation ti, File file) throws IOException;

}