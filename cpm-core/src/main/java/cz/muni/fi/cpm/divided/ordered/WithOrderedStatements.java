package cz.muni.fi.cpm.divided.ordered;

import org.openprovenance.prov.model.Statement;

import java.util.Map;

interface WithOrderedStatements {
    Map<Statement, Long> getStatementsWithOrder();
}
