package cz.muni.fi.cpm.divided.unordered;

import cz.muni.fi.cpm.model.Component;
import cz.muni.fi.cpm.model.ICpmFactory;
import cz.muni.fi.cpm.model.IEdge;
import cz.muni.fi.cpm.model.INode;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.Relation;
import org.openprovenance.prov.model.Statement;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CpmUnorderedProvFactory implements ICpmFactory {
    private final ProvFactory pF;

    public CpmUnorderedProvFactory() {
        this.pF = new org.openprovenance.prov.vanilla.ProvFactory();
    }

    public CpmUnorderedProvFactory(ProvFactory pF) {
        this.pF = pF;
    }

    @Override
    public ProvFactory getProvFactory() {
        return pF;
    }

    @Override
    public IEdge newEdge(Relation relation) {
        Relation clonedRelation = pF.newStatement(relation);
        return new UnorderedEdge(clonedRelation);
    }

    @Override
    public IEdge newEdgeWithoutCloning(Relation relation) {
        return new UnorderedEdge(relation);
    }

    @Override
    public IEdge newEdge(IEdge edge) {
        return new UnorderedEdge(edge.getRelations().stream().map(pF::newStatement).toList());
    }

    @Override
    public IEdge newEdgeWithoutCloning(IEdge edge) {
        return new UnorderedEdge(edge.getRelations());
    }

    @Override
    public INode newNode(Element element) {
        Element clonedElement = pF.newStatement(element);
        return new UnorderedNode(clonedElement);
    }

    public INode newNode(INode node) {
        return new UnorderedNode(node.getElements().stream().map(pF::newStatement).toList());
    }

    @Override
    public Function<List<Component>, List<Statement>> getComponentsTransformer() {
        return list -> list.stream()
                .flatMap(x -> {
                    if (x instanceof INode n) {
                        return n.getElements().stream();
                    }
                    return ((IEdge) x).getRelations().stream();
                }).collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> Collections.newSetFromMap(new IdentityHashMap<>())),
                        List::copyOf
                ));
    }

}
