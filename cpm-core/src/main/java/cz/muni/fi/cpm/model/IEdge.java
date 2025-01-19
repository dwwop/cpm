package cz.muni.fi.cpm.model;

import org.openprovenance.prov.model.Relation;

import java.util.List;

public interface IEdge extends Component {

    Relation getRelation();

    List<Relation> getRelations();

    /**
     * Method to handle duplicate relation of the same kind
     */
    void handleDuplicate(Relation duplicateRelation);

    INode getEffect();

    void setEffect(INode effect);

    INode getCause();

    void setCause(INode cause);
}
