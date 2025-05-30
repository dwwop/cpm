package cz.muni.fi.cpm.merged;

import cz.muni.fi.cpm.model.Component;
import cz.muni.fi.cpm.model.ICpmFactory;
import cz.muni.fi.cpm.model.IEdge;
import cz.muni.fi.cpm.model.INode;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.Relation;
import org.openprovenance.prov.model.Statement;

import java.util.List;
import java.util.function.Function;

public class CpmMergedFactory implements ICpmFactory {

    private final ProvFactory pF;

    public CpmMergedFactory() {
        this.pF = new org.openprovenance.prov.vanilla.ProvFactory();
    }

    public CpmMergedFactory(ProvFactory pF) {
        this.pF = pF;
    }

    @Override
    public ProvFactory getProvFactory() {
        return pF;
    }

    @Override
    public IEdge newEdge(Relation relation) {
        Relation clonedRelation = pF.newStatement(relation);
        return new MergedEdge(clonedRelation);
    }

    @Override
    public IEdge newEdgeWithoutCloning(Relation relation) {
        return new MergedEdge(relation);
    }

    @Override
    public IEdge newEdge(IEdge edge) {
        Relation clonedRelation = pF.newStatement(edge.getRelations().getFirst());
        return new MergedEdge(clonedRelation);
    }

    @Override
    public IEdge newEdgeWithoutCloning(IEdge edge) {
        return new MergedEdge(edge.getRelations().getFirst());
    }

    @Override
    public INode newNode(Element element) {
        Element clonedElement = pF.newStatement(element);
        return new MergedNode(clonedElement);
    }

    public INode newNode(INode node) {
        Element clonedElement = pF.newStatement(node.getElements().getFirst());
        return new MergedNode(clonedElement);
    }

    @Override
    public INode newNodeWithoutCloning(Element element) {
        return new MergedNode(element);
    }

    @Override
    public Function<List<Component>, List<Statement>> getComponentsTransformer() {
        return list -> list.stream()
                .map(x -> {
                    if (x instanceof INode n) {
                        return n.getElements().getFirst();
                    }
                    return ((IEdge) x).getRelations().getFirst();
                }).distinct().toList();
    }


}
