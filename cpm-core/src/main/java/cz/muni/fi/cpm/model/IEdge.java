package cz.muni.fi.cpm.model;

import org.openprovenance.prov.model.Relation;

public interface IEdge {
    Relation getRelation();

    INode getEffect();

    void setEffect(INode effect);

    INode getCause();

    void setCause(INode cause);
}
