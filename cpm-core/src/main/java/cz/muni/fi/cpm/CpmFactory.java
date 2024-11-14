package cz.muni.fi.cpm;

import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import org.openprovenance.prov.model.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Collection;
import java.util.Collections;

public class CpmFactory implements ICpmFactory {
    private final ProvFactory pF;

    public CpmFactory() {
        this.pF = new org.openprovenance.prov.vanilla.ProvFactory();
    }

    public CpmFactory(ProvFactory pF) {
        this.pF = pF;
    }

    public ProvFactory getProvFactory() {
        return pF;
    }

    @Override
    public Type newCpmType(String type) {
        return pF.newType(
                newCpmQualifiedName(type),
                pF.getName().PROV_QUALIFIED_NAME);
    }

    @Override
    public QualifiedName newCpmQualifiedName(String local) {
        return pF.newQualifiedName(CpmNamespaceConstants.CPM_NS, local, CpmNamespaceConstants.CPM_PREFIX);
    }


    @Override
    public Attribute newCpmAttribute(String local, Object value) {
        return pF.newOther(
                newCpmQualifiedName(local),
                value,
                pF.getName().PROV_QUALIFIED_NAME);
    }

    @Override
    public Entity newCpmEntity(QualifiedName id, String type, Collection<Attribute> attributes) {
        attributes.add(newCpmType(type));
        return pF.newEntity(id, Collections.singletonList(newCpmType(type)));
    }

    @Override
    public Activity newCpmActivity(QualifiedName id, XMLGregorianCalendar startTime, XMLGregorianCalendar endTime, String type, Collection<Attribute> attributes) {
        attributes.add(newCpmType(type));
        return pF.newActivity(id, startTime, endTime, attributes);
    }

    @Override
    public Agent newCpmAgent(QualifiedName id, String type, Collection<Attribute> attributes) {
        attributes.add(newCpmType(type));
        return pF.newAgent(id, Collections.singletonList(newCpmType(type)));
    }

}
