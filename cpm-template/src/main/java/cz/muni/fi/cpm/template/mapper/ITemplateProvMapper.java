package cz.muni.fi.cpm.template.mapper;

import cz.muni.fi.cpm.template.schema.ITraversalInformation;
import org.openprovenance.prov.model.Document;

public interface ITemplateProvMapper<T extends ITraversalInformation> {
    Document map(T ti);
}
