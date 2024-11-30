package cz.muni.fi.cpm.vannila;

import org.openprovenance.prov.model.Relation;

import java.util.Objects;

public class Edge {
    private Relation relation;
    private Node source;
    private Node target;

    public Edge(Relation relation, Node source, Node target) {
        this.relation = relation;
        this.source = source;
        this.target = target;
    }

    public Edge(Relation relation) {
        this.relation = relation;
    }

    public Relation getRelation() {
        return relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target) {
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
