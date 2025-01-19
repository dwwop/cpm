package cz.muni.fi.cpm.model;

import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.Relation;
import org.openprovenance.prov.model.Statement;

import java.util.List;
import java.util.function.Function;

public interface ICpmFactory {

    ProvFactory getProvFactory();

    /**
     * Creates a new edge based on the clone of the given relation.
     *
     * @param relation the relation to create the edge from
     * @return a new IEdge
     */
    IEdge newEdge(Relation relation);

    /**
     * Creates a new edge based on the clone of the underlying relation of the given Edge.
     * Removes cause and effect nodes
     *
     * @param edge the relation to create the edge from
     * @return a new IEdge
     */
    IEdge newEdge(IEdge edge);

    /**
     * Creates a new node based on the clone of the given element.
     *
     * @param element the element to create the node from
     * @return a new INode
     */
    INode newNode(Element element);

    /**
     * Creates a new node based on the clone of the underlying element of the given Node.
     * Empties cause and effect edges
     *
     * @param node the element to create the node from
     * @return a new INode
     */
    INode newNode(INode node);

    Function<List<Component>, List<Statement>> getComponentsTransformer();
}
