package cz.muni.fi.cpm.template.serialization;

import cz.muni.fi.cpm.template.schema.ITraversalInformation;

import java.io.File;
import java.io.IOException;

public interface ITraversalInformationSerializer {
    void serializeTI(ITraversalInformation ti, File file) throws IOException;

}