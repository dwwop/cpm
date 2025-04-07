package cz.muni.fi.cpm.template.mapper;

import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.constants.DctAttributeConstants;
import cz.muni.fi.cpm.constants.DctNamespaceConstants;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import cz.muni.fi.cpm.template.schema.*;
import org.openprovenance.prov.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TemplateProvMapper implements ITemplateProvMapper {
    private final ICpmProvFactory cPF;

    public TemplateProvMapper(ICpmProvFactory cPF) {
        this.cPF = cPF;
    }


    private List<Statement> map(Connector c) {
        List<Statement> statements = new ArrayList<>();
        List<Attribute> attributes = new ArrayList<>();


        if (c.getExternalId() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttributeConstants.EXTERNAL_ID, c.getExternalId(), cPF.getProvFactory().getName().XSD_STRING));
        }

        if (c.getReferencedBundleId() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttributeConstants.REFERENCED_BUNDLE_ID, c.getReferencedBundleId()));
        }

        if (c.getReferencedMetaBundleId() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttributeConstants.REFERENCED_META_BUNDLE_ID, c.getReferencedMetaBundleId()));
        }

        if (c.getReferencedBundleHashValue() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttributeConstants.REFERENCED_BUNDLE_HASH_VALUE, c.getReferencedBundleHashValue(), cPF.getProvFactory().getName().XSD_BYTE));
        }

        if (c.getHashAlg() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttributeConstants.HASH_ALG, c.getHashAlg().toString(), cPF.getProvFactory().getName().XSD_STRING));
        }

        if (c.getProvenanceServiceUri() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttributeConstants.PROVENANCE_SERVICE_URI, c.getProvenanceServiceUri(), cPF.getProvFactory().getName().XSD_ANY_URI));
        }

        statements.add(cPF.newCpmEntity(c.getId(), c.getType(), attributes));

        if (c.getDerivedFrom() != null) {
            for (QualifiedName derivedFromElement : c.getDerivedFrom()) {
                statements.add(cPF.getProvFactory().newWasDerivedFrom(c.getId(), derivedFromElement));
            }
        }

        if (c.getAttributedTo() != null) {
            statements.add(cPF.getProvFactory().newWasAttributedTo(c.getAttributedTo().getId(), c.getId(), c.getAttributedTo().getAgentId()));
        }

        return statements;
    }


    public List<Statement> map(BackwardConnector bC) {
        return map((Connector) bC);
    }


    public List<Statement> map(ForwardConnector fC) {
        List<Statement> statements = map((Connector) fC);
        if (fC.getSpecializationOf() != null) {
            statements.add(cPF.getProvFactory().newSpecializationOf(fC.getId(), fC.getSpecializationOf()));
        }
        return statements;
    }

    private List<Statement> map(CpmAgent agent) {
        List<Attribute> attributes = new ArrayList<>();

        if (agent.getContactIdPid() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttributeConstants.CONTACT_ID_PID, agent.getContactIdPid(), cPF.getProvFactory().getName().XSD_STRING));
        }

        return Collections.singletonList(cPF.newCpmAgent(agent.getId(), agent.getType(), attributes));
    }


    public List<Statement> map(ReceiverAgent rA) {
        return map((CpmAgent) rA);
    }


    public List<Statement> map(SenderAgent sA) {
        return map((CpmAgent) sA);
    }


    public List<Statement> map(IdentifierEntity iE) {
        List<Attribute> attributes = new ArrayList<>();

        if (iE.getExternalId() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttributeConstants.EXTERNAL_ID, iE.getExternalId(), cPF.getProvFactory().getName().XSD_STRING));
        }

        if (iE.getExternalIdType() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttributeConstants.EXTERNAL_ID_TYPE, iE.getExternalIdType(), cPF.getProvFactory().getName().XSD_STRING));
        }

        if (iE.getComment() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttributeConstants.COMMENT, iE.getComment(), cPF.getProvFactory().getName().XSD_STRING));
        }

        return Collections.singletonList(cPF.newCpmEntity(iE.getId(), CpmType.IDENTIFIER, attributes));
    }


    public List<Statement> map(MainActivity mA) {
        List<Statement> statements = new ArrayList<>();
        List<Attribute> attributes = new ArrayList<>();


        if (mA.getReferencedMetaBundleId() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttributeConstants.REFERENCED_META_BUNDLE_ID, mA.getReferencedMetaBundleId()));
        }

        if (mA.getHasPart() != null) {
            for (QualifiedName qn : mA.getHasPart()) {
                attributes.add(cPF.getProvFactory().newOther(
                        cPF.getProvFactory().newQualifiedName(DctNamespaceConstants.DCT_NS, DctAttributeConstants.HAS_PART, DctNamespaceConstants.DCT_PREFIX),
                        qn,
                        cPF.getProvFactory().getName().PROV_QUALIFIED_NAME));
            }
        }

        statements.add(cPF.newCpmActivity(mA.getId(), mA.getStartTime(), mA.getEndTime(), CpmType.MAIN_ACTIVITY, attributes));

        if (mA.getUsed() != null) {
            for (MainActivityUsed mainActivityUsed : mA.getUsed()) {
                statements.add(cPF.getProvFactory().newUsed(mainActivityUsed.getId(), mA.getId(), mainActivityUsed.getBcId()));
            }
        }

        if (mA.getGenerated() != null) {
            for (QualifiedName forwardConnector : mA.getGenerated()) {
                statements.add(cPF.getProvFactory().newWasGeneratedBy(null, forwardConnector, mA.getId()));
            }
        }

        return statements;
    }

    @Override
    public Document map(TraversalInformation ti) {
        List<Statement> statements = new ArrayList<>(map(ti.getMainActivity()));

        if (ti.getBackwardConnectors() != null) {
            ti.getBackwardConnectors().stream().map(this::map).flatMap(List::stream).forEach(statements::add);
        }

        if (ti.getForwardConnectors() != null) {
            ti.getForwardConnectors().stream().map(this::map).flatMap(List::stream).forEach(statements::add);
        }
        if (ti.getReceiverAgents() != null) {
            ti.getReceiverAgents().stream().map(this::map).flatMap(List::stream).forEach(statements::add);
        }
        if (ti.getSenderAgents() != null) {
            ti.getSenderAgents().stream().map(this::map).flatMap(List::stream).forEach(statements::add);
        }
        if (ti.getIdentifierEntities() != null) {
            ti.getIdentifierEntities().stream().map(this::map).flatMap(List::stream).forEach(statements::add);
        }

        ti.getNamespace().extendWith(cPF.newCpmNamespace());

        Namespace bundleNamespace = new Namespace();
        bundleNamespace.setParent(ti.getNamespace());
        Bundle tiBundle = cPF.getProvFactory().newNamedBundle(ti.getBundleName(), bundleNamespace, statements);

        return cPF.getProvFactory().newDocument(ti.getNamespace(), Collections.singletonList(tiBundle));
    }
}
