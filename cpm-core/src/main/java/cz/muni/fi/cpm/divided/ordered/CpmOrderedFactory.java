package cz.muni.fi.cpm.divided.ordered;

import cz.muni.fi.cpm.model.Component;
import cz.muni.fi.cpm.model.ICpmFactory;
import cz.muni.fi.cpm.model.IEdge;
import cz.muni.fi.cpm.model.INode;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.Relation;
import org.openprovenance.prov.model.Statement;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CpmOrderedFactory implements ICpmFactory {
    private final ProvFactory pF;
    private long order = 0;

    public CpmOrderedFactory() {
        this.pF = new org.openprovenance.prov.vanilla.ProvFactory();
    }

    public CpmOrderedFactory(ProvFactory pF) {
        this.pF = pF;
    }

    @Override
    public ProvFactory getProvFactory() {
        return pF;
    }

    @Override
    public IEdge newEdge(Relation relation) {
        Relation clonedRelation = pF.newStatement(relation);
        return new OrderedEdge(clonedRelation, this);
    }

    @Override
    public IEdge newEdge(IEdge edge) {
        Relation clonedRelation = pF.newStatement(edge.getRelation());
        return new OrderedEdge(clonedRelation, this);
    }

    @Override
    public INode newNode(Element element) {
        Element clonedElement = pF.newStatement(element);
        return new OrderedNode(clonedElement, this);
    }

    public INode newNode(INode node) {
        Element clonedElement = pF.newStatement(node.getElement());
        return new OrderedNode(clonedElement, this);
    }

    public Long getOrder() {
        order++;
        return order;
    }

    @Override
    public Function<List<Component>, List<Statement>> getComponentsTransformer() {
        return statements -> statements.stream()
                .map(c -> ((WithOrderedStatements) c).getStatementsWithOrder())
                .flatMap(map -> map.entrySet().stream())
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .distinct()
                .toList();
    }
}
