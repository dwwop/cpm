package cz.muni.fi.cpm.strategy;

import cz.muni.fi.cpm.constants.CpmNamespaceConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.constants.DctAttributeConstants;
import cz.muni.fi.cpm.constants.DctNamespaceConstants;
import cz.muni.fi.cpm.model.IEdge;
import cz.muni.fi.cpm.model.INode;
import cz.muni.fi.cpm.model.ITIStrategy;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.StatementOrBundle;

import java.util.List;
import java.util.Objects;

import static cz.muni.fi.cpm.model.CpmUtilities.hasAnyCpmType;
import static cz.muni.fi.cpm.model.CpmUtilities.hasCpmType;


/**
 * Strategy to determine whether a node belongs to traversal information part of a document based on the attributes
 * present in the underlying element
 */
public class AttributeTIStrategy implements ITIStrategy {
    private static boolean hasNonTIAttributes(INode node) {
        if (node == null) return true;

        return !node.getElements().stream().allMatch(element ->
                element != null &&
                        element.getLocation().isEmpty() && element.getLabel().isEmpty() &&
                        (element.getType().isEmpty() ||
                                (element.getType().size() == 1 &&
                                        element.getType().getFirst().getValue() instanceof QualifiedName qN &&
                                        belongsToCpmNs(qN))) &&
                        element.getOther().stream().allMatch(x ->
                                belongsToCpmNs(x.getElementName()) ||
                                        (x.getElementName() instanceof QualifiedName qN2 &&
                                                DctNamespaceConstants.DCT_PREFIX.equals(qN2.getPrefix()) &&
                                                DctNamespaceConstants.DCT_NS.equals(qN2.getNamespaceURI()) &&
                                                DctAttributeConstants.HAS_PART.equals(qN2.getLocalPart()))));
    }

    private static boolean belongsToCpmNs(QualifiedName qN) {
        return qN != null &&
                Objects.equals(qN.getNamespaceURI(), CpmNamespaceConstants.CPM_NS) &&
                Objects.equals(qN.getPrefix(), CpmNamespaceConstants.CPM_PREFIX);
    }

    @Override
    public boolean belongsToTraversalInformation(INode node) {
        if (node == null) return false;
        if (hasNonTIAttributes(node)) return false;

        boolean hasNodeAnyCpmType = hasAnyCpmType(node);

        List<INode> generalNodes = node.getEffectEdges().stream()
                .filter(x -> StatementOrBundle.Kind.PROV_SPECIALIZATION.equals(x.getRelation().getKind()))
                .map(IEdge::getCause).toList();

        if (generalNodes.isEmpty() && hasNodeAnyCpmType) return true;
        if (generalNodes.size() != 1) return false;

        INode generalNode = generalNodes.getFirst();
        if (hasNonTIAttributes(generalNode)) return false;
        return hasCpmType(generalNode, CpmType.FORWARD_CONNECTOR) &&
                (!hasNodeAnyCpmType || hasCpmType(node, CpmType.FORWARD_CONNECTOR));
    }
}