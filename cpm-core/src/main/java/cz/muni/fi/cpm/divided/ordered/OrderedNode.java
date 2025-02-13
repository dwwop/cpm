package cz.muni.fi.cpm.divided.ordered;

import cz.muni.fi.cpm.model.IEdge;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.Statement;

import java.util.*;

public class OrderedNode implements cz.muni.fi.cpm.model.INode, WithOrderedStatements {
    final Map<Element, Long> elements;
    final List<IEdge> effectEdges;
    final List<IEdge> causeEdges;
    private final CpmOrderedFactory cF;


    public OrderedNode(Element element, CpmOrderedFactory cF) {
        this.elements = new IdentityHashMap<>(Map.of(element, cF.getOrder()));
        this.effectEdges = new ArrayList<>();
        this.causeEdges = new ArrayList<>();
        this.cF = cF;
    }

    @Override
    public Element getElement() {
        return elements.keySet().iterator().next();
    }

    @Override
    public List<Element> getElements() {
        return elements.keySet().stream().toList();
    }

    @Override
    public void handleDuplicate(Element duplicateElement) {
        elements.put(duplicateElement, cF.getOrder());
    }

    @Override
    public List<IEdge> getEffectEdges() {
        return effectEdges;
    }

    @Override
    public List<IEdge> getCauseEdges() {
        return causeEdges;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderedNode node = (OrderedNode) o;
        return Objects.equals(new HashSet<>(elements.keySet()), new HashSet<>(node.elements.keySet()))
                && Objects.equals(effectEdges, node.effectEdges) && Objects.equals(causeEdges, node.causeEdges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements.keySet(), effectEdges, causeEdges);
    }


    @Override
    public Map<Statement, Long> getStatementsWithOrder() {
        return Collections.unmodifiableMap(elements);
    }
}
