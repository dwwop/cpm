package cz.muni.fi.cpm;

import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.CpmTypeConstants;
import org.openprovenance.prov.model.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Collections;

public class CpmFactory {
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

    public Type newCpmType(String type) {
        return pF.newType(
                newCpmQualifiedName(type),
                pF.getName().PROV_QUALIFIED_NAME);
    }

    public QualifiedName newCpmQualifiedName(String local) {
        return pF.newQualifiedName(CpmNamespaceConstants.CPM_NS, local, CpmNamespaceConstants.CPM_PREFIX);
    }


    public Attribute newCpmAttribute(String local, Object value) {
        return pF.newOther(
                newCpmQualifiedName(local),
                value,
                pF.getName().PROV_QUALIFIED_NAME);
    }

    public Entity newCpmEntity(QualifiedName id, String type){
        return pF.newEntity(id, Collections.singletonList(newCpmType(type)));
    }


    public Activity newCpmActivity(QualifiedName id, XMLGregorianCalendar startTime, XMLGregorianCalendar endTime, String type){
        return pF.newActivity(id, startTime, endTime, Collections.singletonList(newCpmType(type)));
    }

}
