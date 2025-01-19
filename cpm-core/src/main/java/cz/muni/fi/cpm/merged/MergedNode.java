package cz.muni.fi.cpm.merged;

import cz.muni.fi.cpm.model.IEdge;
import cz.muni.fi.cpm.model.ProvUtilities2;
import org.openprovenance.prov.model.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MergedNode implements cz.muni.fi.cpm.model.INode {
    Element element;
    List<IEdge> effectEdges;
    List<IEdge> causeEdges;

    public MergedNode(Element element, List<IEdge> effectEdges, List<IEdge> causeEdges) {
        this.element = element;
        this.effectEdges = effectEdges;
        this.causeEdges = causeEdges;
    }

    public MergedNode(Element element) {
        this.element = element;
        this.effectEdges = new ArrayList<>();
        this.causeEdges = new ArrayList<>();
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public List<Element> getElements() {
        return List.of(element);
    }

    @Override
    public void handleDuplicate(Element duplicateElement) {
        ProvUtilities2.mergeAttributes(this.element, duplicateElement);
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
        MergedNode node = (MergedNode) o;
        return Objects.equals(element, node.element) && Objects.equals(effectEdges, node.effectEdges) && Objects.equals(causeEdges, node.causeEdges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, effectEdges, causeEdges);
    }
}
