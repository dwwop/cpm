package cz.muni.fi.cpm.merged;

import cz.muni.fi.cpm.model.INode;
import cz.muni.fi.cpm.model.ProvUtilities2;
import org.openprovenance.prov.model.Influence;
import org.openprovenance.prov.model.Relation;

import java.util.List;
import java.util.Objects;

import static cz.muni.fi.cpm.constants.CpmExceptionConstants.UNSUPPORTED_DUPLICATE_RELATION;

public class MergedEdge implements cz.muni.fi.cpm.model.IEdge {
    private final Relation relation;
    private INode effect;
    private INode cause;

    public MergedEdge(Relation relation, INode effect, INode cause) {
        this.relation = relation;
        this.effect = effect;
        this.cause = cause;
    }

    public MergedEdge(Relation relation) {
        this.relation = relation;
    }

    @Override
    public Relation getRelation() {
        return relation;
    }

    @Override
    public List<Relation> getRelations() {
        return List.of(relation);
    }

    @Override
    public void handleDuplicate(Relation duplicateRelation) {
        if (this.relation instanceof Influence i1 && duplicateRelation instanceof Influence i2) {
            ProvUtilities2.mergeAttributes(i1, i2);
        } else {
            throw new UnsupportedOperationException(UNSUPPORTED_DUPLICATE_RELATION + ": " + duplicateRelation.getKind());
        }
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MergedEdge edge = (MergedEdge) o;
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
