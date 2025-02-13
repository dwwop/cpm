package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.CpmType;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.StatementOrBundle;

import java.util.List;
import java.util.Objects;


/**
 * Utilities for manipulating CPM Descriptions.
 */
public class CpmUtilities {
    public static boolean isBackbone(INode node) {
        if (node == null) return false;
        if (!containsOnlyCPMAttributes(node)) return false;

        boolean hasAnyCpmType = hasAnyCpmType(node);

        List<INode> generalNodes = node.getEffectEdges().stream()
                .filter(x -> StatementOrBundle.Kind.PROV_SPECIALIZATION.equals(x.getRelation().getKind()))
                .map(IEdge::getCause).toList();

        if (generalNodes.isEmpty() && hasAnyCpmType) return true;
        if (generalNodes.size() != 1) return false;

        INode generalNode = generalNodes.getFirst();
        if (!containsOnlyCPMAttributes(generalNode)) return false;
        return hasCpmType(generalNode, CpmType.FORWARD_CONNECTOR);
    }

    public static boolean containsOnlyCPMAttributes(INode node) {
        if (node == null) return false;

        return node.getElements().stream().allMatch(element ->
                element != null &&
                        element.getLocation().isEmpty() && element.getLabel().isEmpty() &&
                        (element.getType().isEmpty() ||
                                (element.getType().size() == 1 &&
                                        element.getType().getFirst().getValue() instanceof QualifiedName qN &&
                                        CpmNamespaceConstants.CPM_NS.equals(qN.getNamespaceURI()) &&
                                        CpmNamespaceConstants.CPM_PREFIX.equals(qN.getPrefix()))) &&
                        element.getOther().stream().allMatch(x ->
                                x.getElementName() instanceof QualifiedName qN2 &&
                                        CpmNamespaceConstants.CPM_NS.equals(qN2.getNamespaceURI()) &&
                                        CpmNamespaceConstants.CPM_PREFIX.equals(qN2.getPrefix())));
    }

    /**
     * Checks if the given {@link INode} has any supported CPM type.
     * This method verifies if any of the elements in the node contain a type that matches
     * the CPM namespace, prefix, and is listed in {@link CpmType#STRING_VALUES}.
     *
     * @param node the {@link INode} to check
     * @return {@code true} if the node has any element with a supported CPM type, {@code false} otherwise
     */
    public static boolean hasAnyCpmType(INode node) {
        if (node == null) return false;

        return node.getElements().stream().anyMatch(element ->
                element != null && element.getType().stream().anyMatch(x ->
                        x.getValue() instanceof QualifiedName qN &&
                                CpmNamespaceConstants.CPM_NS.equals(qN.getNamespaceURI()) &&
                                CpmNamespaceConstants.CPM_PREFIX.equals(qN.getPrefix()) &&
                                CpmType.STRING_VALUES.contains(qN.getLocalPart())));
    }

    /**
     * Checks if the given {@link INode} has a specific CPM type.
     * This method verifies if any of the elements in the node contain a type that matches
     * the CPM namespace, prefix, and the specified {@link CpmType}.
     *
     * @param node the {@link INode} to check
     * @param type the {@link CpmType} to compare against
     * @return {@code true} if the node has any element with the specified CPM type, {@code false} otherwise
     */
    public static boolean hasCpmType(INode node, CpmType type) {
        if (node == null) return false;

        return node.getElements().stream().anyMatch(element ->
                element != null && type != null && element.getType().stream().anyMatch(x ->
                        x.getValue() instanceof QualifiedName qN &&
                                CpmNamespaceConstants.CPM_NS.equals(qN.getNamespaceURI()) &&
                                CpmNamespaceConstants.CPM_PREFIX.equals(qN.getPrefix()) &&
                                Objects.equals(type.toString(), qN.getLocalPart())));
    }

    /**
     * Checks if the given {@link INode} is considered a connector node.
     * A connector node is defined as having any element with a type matching either
     * {@link CpmType#FORWARD_CONNECTOR} or {@link CpmType#BACKWARD_CONNECTOR}.
     *
     * @param node the {@link INode} to check
     * @return {@code true} if the node contains a connector element, {@code false} otherwise
     */
    public static boolean isConnector(INode node) {
        if (node == null) return false;
        return node.getElements().stream().anyMatch(element ->
                element != null && element.getType().stream().anyMatch(x ->
                        x.getValue() instanceof QualifiedName qN &&
                                CpmNamespaceConstants.CPM_NS.equals(qN.getNamespaceURI()) &&
                                CpmNamespaceConstants.CPM_PREFIX.equals(qN.getPrefix()) &&
                                (CpmType.FORWARD_CONNECTOR.toString().equals(qN.getLocalPart()) ||
                                        CpmType.BACKWARD_CONNECTOR.toString().equals(qN.getLocalPart()))));
    }
}
