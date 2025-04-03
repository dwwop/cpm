package cz.muni.fi.cpm.divided.ordered;

import cz.muni.fi.cpm.model.IEdge;
import cz.muni.fi.cpm.model.INode;
import org.openprovenance.prov.model.Influence;
import org.openprovenance.prov.model.Relation;
import org.openprovenance.prov.model.Statement;
import org.openprovenance.prov.model.StatementOrBundle;

import java.util.*;
import java.util.stream.Collectors;

import static cz.muni.fi.cpm.constants.CpmExceptionConstants.UNSUPPORTED_DUPLICATE_RELATION;

public class OrderedEdge implements IEdge, WithOrderedStatements {
    private final Map<Relation, Long> relations;
    private final CpmOrderedFactory cF;
    private INode effect;
    private INode cause;

    public OrderedEdge(Relation relation, CpmOrderedFactory cF) {
        this.relations = new IdentityHashMap<>(Map.of(relation, cF.getOrder()));
        this.cF = cF;
    }

    public OrderedEdge(List<Relation> relations, CpmOrderedFactory cF) {
        this.relations = new IdentityHashMap<>(relations.stream()
                .collect(Collectors.toMap(r -> r, _ -> cF.getOrder())));
        this.cF = cF;
    }

    @Override
    public Relation getAnyRelation() {
        return relations.keySet().iterator().next();
    }

    @Override
    public List<Relation> getRelations() {
        return relations.keySet().stream().toList();
    }

    @Override
    public StatementOrBundle.Kind getKind() {
        return relations.keySet().iterator().next().getKind();
    }

    @Override
    public void handleDuplicate(Relation duplicateRelation) {
        if (duplicateRelation instanceof Influence) {
            relations.put(duplicateRelation, cF.getOrder());
        } else {
            throw new UnsupportedOperationException(UNSUPPORTED_DUPLICATE_RELATION + ": " + duplicateRelation.getKind());
        }
    }

    @Override
    public boolean remove(Relation relation) {
        return relations.remove(relation) != null;
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
        OrderedEdge edge = (OrderedEdge) o;
        return Objects.equals(new HashSet<>(relations.keySet()), new HashSet<>(edge.relations.keySet())) &&
                Objects.equals(effect != null ? effect.getElements() : null,
                        edge.effect != null ? edge.effect.getElements() : null) &&
                Objects.equals(cause != null ? cause.getElements() : null,
                        edge.cause != null ? edge.cause.getElements() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(new HashSet<>(relations.keySet()),
                effect != null ? effect.getElements() : null,
                cause != null ? cause.getElements() : null
        );
    }


    @Override
    public Map<Statement, Long> getStatementsWithOrder() {
        return Collections.unmodifiableMap(relations);
    }
}
