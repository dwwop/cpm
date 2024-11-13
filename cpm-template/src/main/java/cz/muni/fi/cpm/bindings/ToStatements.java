package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.CpmFactory;
import org.openprovenance.prov.model.StatementOrBundle;

import java.util.List;

public interface ToStatements {
    public List<StatementOrBundle> toStatements(CpmFactory cF);
}
