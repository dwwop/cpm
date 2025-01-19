package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.model.Statement;

import java.util.List;

public interface ToStatements {
    List<Statement> toStatements(ICpmProvFactory cF);
}
