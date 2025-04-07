package cz.muni.fi.cpm.template.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openprovenance.prov.model.QualifiedName;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

public class MainActivity {
    @JsonProperty(required = true)
    private QualifiedName id;
    private XMLGregorianCalendar startTime;
    private XMLGregorianCalendar endTime;
    private QualifiedName referencedMetaBundleId;
    private List<QualifiedName> hasPart;
    private List<MainActivityUsed> used;
    private List<QualifiedName> generated;

    public MainActivity() {
    }

    public MainActivity(QualifiedName id) {
        this.id = id;
    }

    public QualifiedName getId() {
        return id;
    }

    public void setId(QualifiedName id) {
        this.id = id;
    }

    public XMLGregorianCalendar getStartTime() {
        return startTime;
    }

    public void setStartTime(XMLGregorianCalendar startTime) {
        this.startTime = startTime;
    }

    public XMLGregorianCalendar getEndTime() {
        return endTime;
    }

    public void setEndTime(XMLGregorianCalendar endTime) {
        this.endTime = endTime;
    }

    public QualifiedName getReferencedMetaBundleId() {
        return referencedMetaBundleId;
    }

    public void setReferencedMetaBundleId(QualifiedName referencedMetaBundleId) {
        this.referencedMetaBundleId = referencedMetaBundleId;
    }

    public List<QualifiedName> getHasPart() {
        return hasPart;
    }

    public void setHasPart(List<QualifiedName> hasPart) {
        this.hasPart = hasPart;
    }

    public List<QualifiedName> getGenerated() {
        return generated;
    }

    public void setGenerated(List<QualifiedName> generated) {
        this.generated = generated;
    }

    public List<MainActivityUsed> getUsed() {
        return used;
    }

    public void setUsed(List<MainActivityUsed> used) {
        this.used = used;
    }
}
