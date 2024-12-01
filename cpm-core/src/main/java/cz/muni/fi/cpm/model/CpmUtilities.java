package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.QualifiedName;

import java.util.Objects;

public class CpmUtilities {

    public static boolean isCpmElement(Element element) {
        return element.getType().stream().anyMatch(x ->
                x.getValue() instanceof QualifiedName qN &&
                        CpmNamespaceConstants.CPM_NS.equals(qN.getNamespaceURI()) &&
                        CpmNamespaceConstants.CPM_PREFIX.equals(qN.getPrefix()));
    }

    public static boolean hasCpmType(Element element, String type) {
        return element.getType().stream().anyMatch(x ->
                x.getValue() instanceof QualifiedName qN &&
                        CpmNamespaceConstants.CPM_NS.equals(qN.getNamespaceURI()) &&
                        CpmNamespaceConstants.CPM_PREFIX.equals(qN.getPrefix()) &&
                        Objects.equals(type, qN.getLocalPart()));
    }
}
