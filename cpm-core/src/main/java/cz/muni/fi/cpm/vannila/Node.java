package cz.muni.fi.cpm.vannila;

import cz.muni.fi.cpm.model.IEdge;
import org.openprovenance.prov.model.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node implements Component, cz.muni.fi.cpm.model.INode {
    Element element;
    List<IEdge> sourceEdges;
    List<IEdge> targetEdges;

    public Node(Element element, List<IEdge> sourceEdges, List<IEdge> targetEdges) {
        if (element == null) {
            // TODO throw exception
        }
        this.element = element;
        this.sourceEdges = sourceEdges;
        this.targetEdges = targetEdges;
    }

    public Node(Element element) {
        if (element == null) {
            // TODO throw exception
        }
        this.element = element;
        this.sourceEdges = new ArrayList<>();
        this.targetEdges = new ArrayList<>();
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public List<IEdge> getSourceEdges() {
        return sourceEdges;
    }

    @Override
    public List<IEdge> getTargetEdges() {
        return targetEdges;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(element, node.element) && Objects.equals(sourceEdges, node.sourceEdges) && Objects.equals(targetEdges, node.targetEdges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, sourceEdges, targetEdges);
    }
}
