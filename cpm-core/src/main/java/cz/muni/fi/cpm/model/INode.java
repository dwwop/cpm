package cz.muni.fi.cpm.model;

import org.openprovenance.prov.model.Element;

import java.util.ArrayList;
import java.util.List;

public interface INode {
    Element getElement();

    /**
     * Retrieves all edges where the current node acts as the source.
     *
     * @return a list of edges where this node is the source node
     */
    List<IEdge> getSourceEdges();

    /**
     * Retrieves all edges where the current node acts as the target.
     *
     * @return a list of edges where this node is the target node
     */
    List<IEdge> getTargetEdges();

    /**
     * Retrieves all edges connected to the current node,
     * including edges where the node is a source or a target.
     *
     * @return a list of all edges connected to this node
     */
    default List<IEdge> getAllEdges() {
        List<IEdge> allEdges = new ArrayList<>();
        allEdges.addAll(getSourceEdges());
        allEdges.addAll(getTargetEdges());
        return allEdges;
    }

}
