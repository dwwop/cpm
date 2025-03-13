package cz.muni.fi.cpm.serialization;

import cz.muni.fi.cpm.template.TraversalInformation;

import java.io.File;
import java.io.IOException;

public interface ITraversalInformationSerializer {
    void serializeTI(TraversalInformation ti, File file) throws IOException;

}