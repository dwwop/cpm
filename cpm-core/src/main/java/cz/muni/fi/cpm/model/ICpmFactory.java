package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.constants.CpmType;
import org.openprovenance.prov.model.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Collection;

public interface ICpmFactory {

    ProvFactory getProvFactory();

    Type newCpmType(CpmType type);

    QualifiedName newCpmQualifiedName(String local);

    Attribute newCpmAttribute(String local, Object value);

    Attribute newCpmAttribute(String local, Object value, QualifiedName type);

    Entity newCpmEntity(QualifiedName id, CpmType type, Collection<Attribute> attributes);

    Activity newCpmActivity(QualifiedName id, XMLGregorianCalendar startTime, XMLGregorianCalendar endTime, CpmType type, Collection<Attribute> attributes);

    Agent newCpmAgent(QualifiedName id, CpmType type, Collection<Attribute> attributes);

    Namespace newCpmNamespace();

    IEdge newEdge(Relation relation);

    INode newNode(Element element);
}
