package cz.muni.fi.cpm.divided.unordered;

import cz.muni.fi.cpm.divided.AbstractDividedFactory;
import cz.muni.fi.cpm.model.Component;
import cz.muni.fi.cpm.model.IEdge;
import cz.muni.fi.cpm.model.INode;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.Statement;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CpmUnorderedFactory extends AbstractDividedFactory {

    public CpmUnorderedFactory() {
        this(new org.openprovenance.prov.vanilla.ProvFactory());
    }

    public CpmUnorderedFactory(ProvFactory pF) {
        super(pF);
    }

    @Override
    protected IEdge processEdge(IEdge edge) {
        return edge;
    }

    @Override
    protected INode processNode(INode node) {
        return node;
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
