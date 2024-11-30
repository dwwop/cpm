package cz.muni.fi.cpm.vannila;

import org.openprovenance.prov.model.Element;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Node {
    Element element;
    Set<Edge> edges;

    public Node(Element element, Set<Edge> edges) {
        this.element = element;
        this.edges = edges;
    }

    public Node(Element element) {
        this.element = element;
        this.edges = new HashSet<>();
    }

    public Element getElement() {
        return element;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public void setEdges(Set<Edge> edges) {
        this.edges = edges;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(element, node.element) && Objects.equals(edges, node.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, edges);
    }
}
