package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.constants.CpmType;
import org.openprovenance.prov.model.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Collection;

public interface ICpmFactory {

    ProvFactory getProvFactory();


    /**
     * Creates a new CPM type with the given CpmType.
     *
     * @param type the CpmType to create
     * @return a new Type
     */
    Type newCpmType(CpmType type);

    /**
     * Creates a new CPM qualified name with the given local name, CPM uri and CPM prefix
     *
     * @param local the local part of the qualified name
     * @return a new QualifiedName
     */
    QualifiedName newCpmQualifiedName(String local);


    /**
     * Creates a new CPM attribute with the given local name and value based off {@link Other}.
     *
     * @param local the local part of the attribute
     * @param value the value of the attribute
     * @return a new Attribute
     */
    Attribute newCpmAttribute(String local, Object value);

    /**
     * Creates a new CPM attribute with the given local name, value, and type based off {@link Other}.
     *
     * @param local the local part of the attribute
     * @param value the value of the attribute
     * @param type  the type of the attribute
     * @return a new Attribute
     */
    Attribute newCpmAttribute(String local, Object value, QualifiedName type);

    /**
     * Creates a new CPM entity with the given ID, type, and attributes.
     *
     * @param id         the ID of the entity
     * @param type       the type of the entity
     * @param attributes the attributes of the entity
     * @return a new Entity
     */
    Entity newCpmEntity(QualifiedName id, CpmType type, Collection<Attribute> attributes);

    /**
     * Creates a new CPM activity with the given ID, start time, end time, type, and attributes.
     *
     * @param id         the ID of the activity
     * @param startTime  the start time of the activity
     * @param endTime    the end time of the activity
     * @param type       the type of the activity
     * @param attributes the attributes of the activity
     * @return a new Activity
     */
    Activity newCpmActivity(QualifiedName id, XMLGregorianCalendar startTime, XMLGregorianCalendar endTime, CpmType type, Collection<Attribute> attributes);

    /**
     * Creates a new CPM agent with the given ID, type, and attributes.
     *
     * @param id         the ID of the agent
     * @param type       the type of the agent
     * @param attributes the attributes of the agent
     * @return a new Agent
     */
    Agent newCpmAgent(QualifiedName id, CpmType type, Collection<Attribute> attributes);

    /**
     * Creates a new PROV namespace and adds known CPM namespaces.
     *
     * @return a new Namespace
     */
    Namespace newCpmNamespace();

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
}
