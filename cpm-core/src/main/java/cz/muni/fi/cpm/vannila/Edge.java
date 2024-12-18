package cz.muni.fi.cpm.vannila;

import cz.muni.fi.cpm.model.INode;
import org.openprovenance.prov.model.Relation;

import java.util.Objects;

public class Edge implements Component, cz.muni.fi.cpm.model.IEdge {
    private final Relation relation;
    private INode source;
    private INode target;

    public Edge(Relation relation, INode source, INode target) {
        if (relation == null) {
            // TODO throw exception
        }
        this.relation = relation;
        this.source = source;
        this.target = target;
    }

    public Edge(Relation relation) {
        this.relation = relation;
    }

    @Override
    public Relation getRelation() {
        return relation;
    }

    @Override
    public INode getSource() {
        return source;
    }

    @Override
    public void setSource(INode source) {
        this.source = source;
    }

    @Override
    public INode getTarget() {
        return target;
    }

    @Override
    public void setTarget(INode target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(relation, edge.relation) && Objects.equals(source, edge.source) && Objects.equals(target, edge.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relation, source, target);
    }

}
