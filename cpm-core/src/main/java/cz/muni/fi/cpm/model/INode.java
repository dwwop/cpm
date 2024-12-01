package cz.muni.fi.cpm.model;

import org.openprovenance.prov.model.Element;

import java.util.List;

public interface INode {
    Element getElement();

    List<IEdge> getEdges();
}
