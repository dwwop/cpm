package cz.muni.fi.cpm.template;


import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Statement;

import java.util.List;

public class ForwardConnector extends Connector {
    private QualifiedName specializationOf;

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

    @Override
    public List<Statement> toStatements(ICpmProvFactory cF) {
        List<Statement> statements = super.toStatements(cF);
        if (specializationOf != null) {
            statements.add(cF.getProvFactory().newSpecializationOf(getId(), specializationOf));
        }
        return statements;
    }

    public ForwardConnector() {
    }

    public ForwardConnector(QualifiedName id) {
        super(id);
    }
}
