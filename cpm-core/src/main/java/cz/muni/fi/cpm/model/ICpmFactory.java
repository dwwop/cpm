package cz.muni.fi.cpm.model;

import org.openprovenance.prov.model.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Collection;

public interface ICpmFactory {

    ProvFactory getProvFactory();

    Type newCpmType(String type);

    QualifiedName newCpmQualifiedName(String local);

    Attribute newCpmAttribute(String local, Object value);

    Attribute newCpmAttribute(String local, Object value, QualifiedName type);

    Entity newCpmEntity(QualifiedName id, String type, Collection<Attribute> attributes);

    Activity newCpmActivity(QualifiedName id, XMLGregorianCalendar startTime, XMLGregorianCalendar endTime, String type, Collection<Attribute> attributes);

    Agent newCpmAgent(QualifiedName id, String type, Collection<Attribute> attributes);

    Namespace newCpmNamespace();
}
