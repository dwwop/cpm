package cz.muni.fi.cpm.template.mapper;

import cz.muni.fi.cpm.template.schema.TraversalInformation;
import org.openprovenance.prov.model.Document;

public interface ITemplateProvMapper {
    Document map(TraversalInformation ti);
}
