package cz.muni.fi.cpm.model;

import org.openprovenance.prov.model.Relation;

public interface IEdge {
    Relation getRelation();

    INode getSource();

    void setSource(INode source);

    INode getTarget();

    void setTarget(INode target);
}
