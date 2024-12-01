package cz.muni.fi.cpm.vannila;

import cz.muni.fi.cpm.model.IEdge;
import org.openprovenance.prov.model.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node implements Component, cz.muni.fi.cpm.model.INode {
    Element element;
    List<IEdge> IEdges;

    public Node(Element element, List<IEdge> IEdges) {
        if (element == null) {
            // TODO throw exception
        }
        this.element = element;
        this.IEdges = IEdges;
    }

    public Node(Element element) {
        if (element == null) {
            // TODO throw exception
        }
        this.element = element;
        this.IEdges = new ArrayList<>();
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public List<IEdge> getEdges() {
        return IEdges;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(element, node.element) && Objects.equals(IEdges, node.IEdges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, IEdges);
    }
}
