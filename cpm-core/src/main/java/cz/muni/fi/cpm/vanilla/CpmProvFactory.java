package cz.muni.fi.cpm.vanilla;

import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.constants.DctNamespaceConstants;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.model.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Collection;

public class CpmProvFactory implements ICpmProvFactory {

    private final ProvFactory pF;

    public CpmProvFactory() {
        this.pF = new org.openprovenance.prov.vanilla.ProvFactory();
    }

    public CpmProvFactory(ProvFactory pF) {
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

    @Override
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
}
