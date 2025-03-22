package cz.muni.fi.cpm.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.constants.DctAttributeConstants;
import cz.muni.fi.cpm.constants.DctNamespaceConstants;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.model.Attribute;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Statement;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

public class MainActivity implements ToStatements {
    @JsonProperty(required = true)
    private QualifiedName id;
    private XMLGregorianCalendar startTime;
    private XMLGregorianCalendar endTime;
    private QualifiedName referencedMetaBundleId;
    private List<QualifiedName> hasPart;
    private List<MainActivityUsed> used;
    private List<QualifiedName> generated;

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

    public List<Statement> toStatements(ICpmProvFactory cF) {
        List<Statement> statements = new ArrayList<>();
        List<Attribute> attributes = new ArrayList<>();


        if (referencedMetaBundleId != null) {
            attributes.add(cF.newCpmAttribute(CpmAttributeConstants.REFERENCED_META_BUNDLE_ID, referencedMetaBundleId));
        }

        if (hasPart != null) {
            for (QualifiedName qn : hasPart) {
                attributes.add(cF.getProvFactory().newOther(
                        cF.getProvFactory().newQualifiedName(DctNamespaceConstants.DCT_NS, DctAttributeConstants.HAS_PART, DctNamespaceConstants.DCT_PREFIX),
                        qn,
                        cF.getProvFactory().getName().PROV_QUALIFIED_NAME));
            }
        }

        statements.add(cF.newCpmActivity(id, startTime, endTime, CpmType.MAIN_ACTIVITY, attributes));

        if (used != null) {
            for (MainActivityUsed mainActivityUsed : used) {
                statements.add(cF.getProvFactory().newUsed(mainActivityUsed.getId(), id, mainActivityUsed.getBcId()));
            }
        }

        if (generated != null) {
            for (QualifiedName forwardConnector : generated) {
                statements.add(cF.getProvFactory().newWasGeneratedBy(null, forwardConnector, id));
            }
        }

        return statements;
    }

    public MainActivity() {
    }

    public MainActivity(QualifiedName id) {
        this.id = id;
    }
}
