package cz.muni.fi.cpm.divided.unordered;

import cz.muni.fi.cpm.model.IEdge;
import org.openprovenance.prov.model.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class UnorderedNode implements cz.muni.fi.cpm.model.INode {
    final List<Element> elements;
    final List<IEdge> effectEdges;
    final List<IEdge> causeEdges;

    public UnorderedNode(Element element, List<IEdge> effectEdges, List<IEdge> causeEdges) {
        this.elements = new ArrayList<>(List.of(element));
        this.effectEdges = effectEdges;
        this.causeEdges = causeEdges;
    }

    public UnorderedNode(Element element) {
        this.elements = new ArrayList<>(List.of(element));
        this.effectEdges = new ArrayList<>();
        this.causeEdges = new ArrayList<>();
    }

    @Override
    public Element getElement() {
        return elements.getFirst();
    }

    @Override
    public List<Element> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public void handleDuplicate(Element duplicateElement) {
        elements.add(duplicateElement);
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
        UnorderedNode node = (UnorderedNode) o;
        return Objects.equals(elements, node.elements) && Objects.equals(effectEdges, node.effectEdges) && Objects.equals(causeEdges, node.causeEdges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements, effectEdges, causeEdges);
    }
}
