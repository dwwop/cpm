package cz.muni.fi.cpm.template.schema;


import com.fasterxml.jackson.annotation.JsonInclude;
import cz.muni.fi.cpm.constants.CpmType;
import org.openprovenance.prov.model.QualifiedName;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BackwardConnector extends Connector {

    @Override
    public CpmType getType() {
        return CpmType.BACKWARD_CONNECTOR;
    }

    public BackwardConnector() {
    }

    public BackwardConnector(QualifiedName id) {
        super(id);
    }
}
