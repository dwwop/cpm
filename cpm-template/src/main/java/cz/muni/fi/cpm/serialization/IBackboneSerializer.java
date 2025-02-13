package cz.muni.fi.cpm.serialization;

import cz.muni.fi.cpm.bindings.Backbone;

import java.io.File;
import java.io.IOException;

public interface IBackboneSerializer {
    void serializeBackbone(Backbone bb, File file) throws IOException;

}