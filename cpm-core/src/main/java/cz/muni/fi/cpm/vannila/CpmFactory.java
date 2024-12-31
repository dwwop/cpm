package cz.muni.fi.cpm.vannila;

import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.constants.DctNamespaceConstants;
import cz.muni.fi.cpm.model.ICpmFactory;
import cz.muni.fi.cpm.model.IEdge;
import cz.muni.fi.cpm.model.INode;
import org.openprovenance.prov.model.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class CpmFactory implements ICpmFactory {
    private final ProvFactory pF;

    public CpmFactory() {
        this.pF = new org.openprovenance.prov.vanilla.ProvFactory();
    }

    public CpmFactory(ProvFactory pF) {
        this.pF = pF;
    }

    @Override
    public ProvFactory getProvFactory() {
        return pF;
    }

    @Override
    public Type newCpmType(CpmType type) {
        return pF.newType(
                newCpmQualifiedName(type.toString()),
                pF.getName().PROV_QUALIFIED_NAME);
    }

    @Override
    public QualifiedName newCpmQualifiedName(String local) {
        return pF.newQualifiedName(CpmNamespaceConstants.CPM_NS, local, CpmNamespaceConstants.CPM_PREFIX);
    }


    @Override
    public Attribute newCpmAttribute(String local, Object value) {
        return newCpmAttribute(
                local,
                value,
                pF.getName().PROV_QUALIFIED_NAME);
    }

    public Attribute newCpmAttribute(String local, Object value, QualifiedName type) {
        return pF.newOther(
                newCpmQualifiedName(local),
                value,
                type);
    }

    @Override
    public Entity newCpmEntity(QualifiedName id, CpmType type, Collection<Attribute> attributes) {
        attributes.add(newCpmType(type));
        return pF.newEntity(id, attributes);
    }

    @Override
    public Activity newCpmActivity(QualifiedName id, XMLGregorianCalendar startTime, XMLGregorianCalendar endTime, CpmType type, Collection<Attribute> attributes) {
        attributes.add(newCpmType(type));
        return pF.newActivity(id, startTime, endTime, attributes);
    }

    @Override
    public Agent newCpmAgent(QualifiedName id, CpmType type, Collection<Attribute> attributes) {
        attributes.add(newCpmType(type));
        return pF.newAgent(id, attributes);
    }

    public Namespace newCpmNamespace() {
        Namespace namespace = pF.newNamespace();
        namespace.addKnownNamespaces();

        namespace.getPrefixes().put(CpmNamespaceConstants.CPM_PREFIX, CpmNamespaceConstants.CPM_NS);
        namespace.getNamespaces().put(CpmNamespaceConstants.CPM_NS, CpmNamespaceConstants.CPM_PREFIX);
        namespace.getPrefixes().put(DctNamespaceConstants.DCT_PREFIX, DctNamespaceConstants.DCT_NS);
        namespace.getNamespaces().put(DctNamespaceConstants.DCT_NS, DctNamespaceConstants.DCT_PREFIX);
        return namespace;
    }

    @Override
    public IEdge newEdge(Relation relation) {
        Relation clonedRelation = pF.newStatement(relation);
        return new Edge(clonedRelation);
    }

    @Override
    public INode newNode(Element element) {
        Element clonedElement = pF.newStatement(element);
        return new Node(clonedElement);
    }

    private INode newFilteredNode(INode node, Predicate<? super IEdge> edgeFilter) {
        Element clonedElement = pF.newStatement(node.getElement());
        List<IEdge> bbEffectEdges = node.getEffectEdges().stream().filter(edgeFilter).toList();
        List<IEdge> bbCauseEdges = node.getCauseEdges().stream().filter(edgeFilter).toList();
        return new Node(clonedElement, bbEffectEdges, bbCauseEdges);
    }

    @Override
    public INode newBBNode(INode node) {
        return newFilteredNode(node, IEdge::isBackbone);
    }

    @Override
    public INode newDSNode(INode node) {
        return newFilteredNode(node, IEdge::isDomainSpecific);
    }
}
