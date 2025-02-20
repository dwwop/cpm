package cz.muni.fi.cpm.model;

public interface IBackboneStrategy {

    /**
     * Determines whether the given node belongs to the backbone part of the document.
     *
     * @param node the node to check
     * @return {@code true} if the node is part of the backbone, {@code false} otherwise
     */
    boolean isBackbone(INode node);
}
