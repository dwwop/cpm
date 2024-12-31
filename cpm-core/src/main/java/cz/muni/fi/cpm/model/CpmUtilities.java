package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.CpmType;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.QualifiedName;

import java.util.Objects;

public class CpmUtilities {

    public static boolean isBackbone(Element element) {
        return element != null && element.getType().size() == 1 &&
                (element.getType().getFirst().getValue() instanceof QualifiedName qN &&
                        CpmNamespaceConstants.CPM_NS.equals(qN.getNamespaceURI()) &&
                        CpmNamespaceConstants.CPM_PREFIX.equals(qN.getPrefix())) &&
                element.getOther().stream().allMatch(x ->
                        x.getElementName() instanceof QualifiedName qN2 &&
                                CpmNamespaceConstants.CPM_NS.equals(qN2.getNamespaceURI()) &&
                                CpmNamespaceConstants.CPM_PREFIX.equals(qN2.getPrefix())) &&
                element.getLocation().isEmpty() && element.getLabel().isEmpty();
    }

    public static boolean hasCpmType(Element element, CpmType type) {
        return element != null && type != null && element.getType().stream().anyMatch(x ->
                x.getValue() instanceof QualifiedName qN &&
                        CpmNamespaceConstants.CPM_NS.equals(qN.getNamespaceURI()) &&
                        CpmNamespaceConstants.CPM_PREFIX.equals(qN.getPrefix()) &&
                        Objects.equals(type.toString(), qN.getLocalPart()));
    }

    public static boolean isConnector(Element element) {
        return element != null && element.getType().stream().anyMatch(x ->
                x.getValue() instanceof QualifiedName qN &&
                        CpmNamespaceConstants.CPM_NS.equals(qN.getNamespaceURI()) &&
                        CpmNamespaceConstants.CPM_PREFIX.equals(qN.getPrefix()) &&
                        (CpmType.FORWARD_CONNECTOR.toString().equals(qN.getLocalPart()) ||
                                CpmType.BACKWARD_CONNECTOR.toString().equals(qN.getLocalPart())));
    }
}
