package cz.muni.fi.cpm.template.schema;


import com.fasterxml.jackson.annotation.JsonInclude;
import cz.muni.fi.cpm.constants.CpmType;
import org.openprovenance.prov.model.QualifiedName;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ForwardConnector extends Connector {
    private QualifiedName specializationOf;

    public ForwardConnector() {
    }

    public ForwardConnector(QualifiedName id) {
        super(id);
    }

    @Override
    public CpmType getType() {
        return CpmType.FORWARD_CONNECTOR;
    }

    public QualifiedName getSpecializationOf() {
        return specializationOf;
    }

    public void setSpecializationOf(QualifiedName specializationOf) {
        this.specializationOf = specializationOf;
    }
}
