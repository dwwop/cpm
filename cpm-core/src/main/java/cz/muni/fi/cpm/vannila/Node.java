package cz.muni.fi.cpm.vannila;

import cz.muni.fi.cpm.model.IEdge;
import org.openprovenance.prov.model.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node implements Component, cz.muni.fi.cpm.model.INode {
    Element element;
    List<IEdge> effectEdges;
    List<IEdge> causeEdges;

    public Node(Element element, List<IEdge> effectEdges, List<IEdge> causeEdges) {
        this.element = element;
        this.effectEdges = effectEdges;
        this.causeEdges = causeEdges;
    }

    public Node(Element element) {
        this.element = element;
        this.effectEdges = new ArrayList<>();
        this.causeEdges = new ArrayList<>();
    }

    @Override
    public Element getElement() {
        return element;
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
        Node node = (Node) o;
        return Objects.equals(element, node.element) && Objects.equals(effectEdges, node.effectEdges) && Objects.equals(causeEdges, node.causeEdges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, effectEdges, causeEdges);
    }
}
