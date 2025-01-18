package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.CpmType;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.StatementOrBundle;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class CpmUtilities {
    public static boolean isBackbone(INode node) {
        if (node == null) return false;
        if (!containsOnlyCPMAttributes(node.getElement())) return false;

        boolean hasAnyCpmType = hasAnyCpmType(node.getElement());
        List<INode> generalizedNodes = new LinkedList<>(node.getEffectEdges().stream()
                .filter(x -> StatementOrBundle.Kind.PROV_SPECIALIZATION.equals(x.getRelation().getKind()))
                .map(IEdge::getCause).toList());

        while (!generalizedNodes.isEmpty()) {
            INode curNode = generalizedNodes.removeFirst();
            if (!containsOnlyCPMAttributes(curNode.getElement())) return false;
            hasAnyCpmType = hasAnyCpmType || hasAnyCpmType(curNode.getElement());
            generalizedNodes.addAll(curNode.getEffectEdges().stream()
                    .filter(x -> StatementOrBundle.Kind.PROV_SPECIALIZATION.equals(x.getRelation().getKind()))
                    .map(IEdge::getCause).toList());
        }

        return hasAnyCpmType;
    }

    public static boolean containsOnlyCPMAttributes(Element element) {
        return element != null &&
                element.getLocation().isEmpty() && element.getLabel().isEmpty() &&
                (element.getType().isEmpty() ||
                        (element.getType().size() == 1 &&
                                element.getType().getFirst().getValue() instanceof QualifiedName qN &&
                                CpmNamespaceConstants.CPM_NS.equals(qN.getNamespaceURI()) &&
                                CpmNamespaceConstants.CPM_PREFIX.equals(qN.getPrefix()))) &&
                element.getOther().stream().allMatch(x ->
                        x.getElementName() instanceof QualifiedName qN2 &&
                                CpmNamespaceConstants.CPM_NS.equals(qN2.getNamespaceURI()) &&
                                CpmNamespaceConstants.CPM_PREFIX.equals(qN2.getPrefix()));
    }


    /**
     * Checks if the given {@link Element} has any supported CPM type.
     * This method verifies if the element's type matches the given {@link CpmType}.
     *
     * @param element the {@link Element} to check
     * @return {@code true} if the element has any CPM type, {@code false} otherwise
     */
    public static boolean hasAnyCpmType(Element element) {
        return element != null && element.getType().stream().anyMatch(x ->
                x.getValue() instanceof QualifiedName qN &&
                        CpmNamespaceConstants.CPM_NS.equals(qN.getNamespaceURI()) &&
                        CpmNamespaceConstants.CPM_PREFIX.equals(qN.getPrefix()) &&
                        CpmType.STRING_VALUES.contains(qN.getLocalPart()));
    }

    /**
     * Checks if the given {@link Element} has a specific CPM type.
     * This method verifies if the element's type matches the given {@link CpmType}.
     *
     * @param element the {@link Element} to check
     * @param type    the {@link CpmType} to compare against
     * @return {@code true} if the element has the specified CPM type, {@code false} otherwise
     */
    public static boolean hasCpmType(Element element, CpmType type) {
        return element != null && type != null && element.getType().stream().anyMatch(x ->
                x.getValue() instanceof QualifiedName qN &&
                        CpmNamespaceConstants.CPM_NS.equals(qN.getNamespaceURI()) &&
                        CpmNamespaceConstants.CPM_PREFIX.equals(qN.getPrefix()) &&
                        Objects.equals(type.toString(), qN.getLocalPart()));
    }

    /**
     * Checks if the given {@link Element} is considered a connector element.
     * A connector element is defined as having either a forward or backward connector type.
     *
     * @param element the {@link Element} to check
     * @return {@code true} if the element is a connector, {@code false} otherwise
     */
    public static boolean isConnector(Element element) {
        return element != null && element.getType().stream().anyMatch(x ->
                x.getValue() instanceof QualifiedName qN &&
                        CpmNamespaceConstants.CPM_NS.equals(qN.getNamespaceURI()) &&
                        CpmNamespaceConstants.CPM_PREFIX.equals(qN.getPrefix()) &&
                        (CpmType.FORWARD_CONNECTOR.toString().equals(qN.getLocalPart()) ||
                                CpmType.BACKWARD_CONNECTOR.toString().equals(qN.getLocalPart())));
    }
}
