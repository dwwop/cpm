package cz.muni.fi.cpm.model;

import org.openprovenance.prov.model.Relation;

public interface IEdge {
    Relation getRelation();

    INode getEffect();

    void setEffect(INode effect);

    INode getCause();

    void setCause(INode cause);

    /**
     * Checks if the current edge is part of the backbone.
     * This method verifies if both the effect and cause are backbone elements.
     *
     * @return {@code true} if both the effect and cause are backbone elements, {@code false} otherwise
     */
    boolean isBackbone();

    /**
     * Checks if the current edge belongs to the domain specific part of the Document
     * This method verifies if both the effect and cause are not backbone elements.
     *
     * @return {@code true} if both the effect and cause are domain-specific (not backbone elements), {@code false} otherwise
     */
    boolean isDomainSpecific();

    /**
     * Checks if the current edge represents a relationship between backbone and domain specific part of the document
     * This method verifies if one of the effect or cause is a backbone element and the other is not, indicating a cross-part relationship.
     *
     * @return {@code true} if the effect and cause represent a cross-part relationship, {@code false} otherwise
     */
    boolean isCrossPart();
}
