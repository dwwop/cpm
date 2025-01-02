package cz.muni.fi.cpm.vannila;

import cz.muni.fi.cpm.model.CpmUtilities;
import cz.muni.fi.cpm.model.INode;
import org.openprovenance.prov.model.Relation;

import java.util.Objects;

public class Edge implements Component, cz.muni.fi.cpm.model.IEdge {
    private final Relation relation;
    private INode effect;
    private INode cause;

    public Edge(Relation relation, INode effect, INode cause) {
        if (relation == null) {
            // TODO throw exception
        }
        this.relation = relation;
        this.effect = effect;
        this.cause = cause;
    }

    public Edge(Relation relation) {
        this.relation = relation;
    }

    @Override
    public Relation getRelation() {
        return relation;
    }

    @Override
    public INode getEffect() {
        return effect;
    }

    @Override
    public void setEffect(INode effect) {
        this.effect = effect;
    }

    @Override
    public INode getCause() {
        return cause;
    }

    @Override
    public void setCause(INode cause) {
        this.cause = cause;
    }

    @Override
    public boolean isBackbone() {
        return effect != null && CpmUtilities.isBackbone(effect.getElement()) && cause != null && CpmUtilities.isBackbone(cause.getElement());
    }

    @Override
    public boolean isDomainSpecific() {
        return effect != null && !CpmUtilities.isBackbone(effect.getElement()) && cause != null && !CpmUtilities.isBackbone(cause.getElement());
    }

    @Override
    public boolean isCrossPart() {
        if (effect == null || cause == null) {
            return false;
        }
        boolean isCauseBackbone = CpmUtilities.isBackbone(cause.getElement());
        boolean isEffectBackbone = CpmUtilities.isBackbone(effect.getElement());
        return ((isCauseBackbone && !isEffectBackbone) || (!isCauseBackbone && isEffectBackbone));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(relation, edge.relation) &&
                Objects.equals(effect != null ? effect.getElement() : null,
                        edge.effect != null ? edge.effect.getElement() : null) &&
                Objects.equals(cause != null ? cause.getElement() : null,
                        edge.cause != null ? edge.cause.getElement() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relation,
                effect != null ? effect.getElement() : null,
                cause != null ? cause.getElement() : null
        );
    }

}
