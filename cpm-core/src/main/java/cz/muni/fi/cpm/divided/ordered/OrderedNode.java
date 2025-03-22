package cz.muni.fi.cpm.divided.ordered;

import cz.muni.fi.cpm.model.IEdge;
import cz.muni.fi.cpm.model.INode;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Statement;
import org.openprovenance.prov.model.StatementOrBundle;

import java.util.*;
import java.util.stream.Collectors;

public class OrderedNode implements INode, WithOrderedStatements {
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

    public OrderedNode(List<Element> elements, CpmOrderedFactory cF) {
        this.elements = new IdentityHashMap<>(elements.stream()
                .collect(Collectors.toMap(e -> e, _ -> cF.getOrder())));
        this.effectEdges = new ArrayList<>();
        this.causeEdges = new ArrayList<>();
        this.cF = cF;
    }

    @Override
    public Element getAnyElement() {
        return elements.keySet().iterator().next();
    }

    @Override
    public QualifiedName getId() {
        return elements.keySet().iterator().next().getId();
    }

    @Override
    public StatementOrBundle.Kind getKind() {
        return elements.keySet().iterator().next().getKind();
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
