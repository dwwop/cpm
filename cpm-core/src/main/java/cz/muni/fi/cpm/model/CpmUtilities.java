package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.constants.CpmAttribute;
import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.CpmType;
import org.openprovenance.prov.model.HasOther;
import org.openprovenance.prov.model.HasType;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Statement;

import java.util.Objects;


/**
 * Utilities for manipulating CPM Descriptions.
 */
public class CpmUtilities {

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

        return node.getElements().stream().anyMatch(element -> hasCpmType(element, type));
    }

    /**
     * Checks if the given {@link Statement} has a specific CPM type.
     * This method verifies if the statement contains a type that matches
     * the CPM namespace, prefix, and the specified {@link CpmType}.
     *
     * @param statement the {@link Statement} to check
     * @param type      the {@link CpmType} to compare against
     * @return {@code true} if the node has any element with the specified CPM type, {@code false} otherwise
     */
    public static boolean hasCpmType(Statement statement, CpmType type) {
        if (statement == null || type == null) return false;

        return statement instanceof HasType element &&
                element.getType().stream().anyMatch(x ->
                        x.getValue() instanceof QualifiedName qN &&
                                CpmNamespaceConstants.CPM_NS.equals(qN.getNamespaceURI()) &&
                                CpmNamespaceConstants.CPM_PREFIX.equals(qN.getPrefix()) &&
                                Objects.equals(type.toString(), qN.getLocalPart()));
    }

    /**
     * Checks if the given {@link Statement} contains a specific CPM attribute.
     * This method verifies if the statement includes an "other" attribute matching
     * the CPM namespace, prefix, and the specified {@link CpmAttribute}.
     *
     * @param statement the {@link Statement} to check
     * @param attr      the {@link CpmAttribute} to compare against
     * @return {@code true} if the statement contains the specified CPM attribute, {@code false} otherwise
     */
    public static boolean containsCpmAttribute(Statement statement, CpmAttribute attr) {
        if (statement == null || attr == null) return false;

        return statement instanceof HasOther element &&
                element.getOther().stream().anyMatch(x ->
                        x.getElementName() instanceof QualifiedName qN &&
                                CpmNamespaceConstants.CPM_NS.equals(qN.getNamespaceURI()) &&
                                CpmNamespaceConstants.CPM_PREFIX.equals(qN.getPrefix()) &&
                                Objects.equals(attr.toString(), qN.getLocalPart()));
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
