package cz.muni.fi.cpm.template.mapper;

import cz.muni.fi.cpm.constants.CpmAttribute;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.constants.DctAttributeConstants;
import cz.muni.fi.cpm.constants.DctNamespaceConstants;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import cz.muni.fi.cpm.template.schema.*;
import org.openprovenance.prov.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.muni.fi.cpm.template.constants.CpmTemplateExceptionConstants.NULL_BUNDLE_NAME;

public class TemplateProvMapper implements ITemplateProvMapper {
    private final ICpmProvFactory cPF;
    private boolean mergeAgents = false;

    public TemplateProvMapper(ICpmProvFactory cPF) {
        this.cPF = cPF;
    }

    public TemplateProvMapper(ICpmProvFactory cPF, boolean mergeAgents) {
        this.cPF = cPF;
        this.mergeAgents = mergeAgents;
    }

    public boolean isMergeAgents() {
        return mergeAgents;
    }

    public void setMergeAgents(boolean mergeAgents) {
        this.mergeAgents = mergeAgents;
    }

    private List<Statement> map(Connector c) {
        if (c == null) {
            return null;
        }
        List<Statement> statements = new ArrayList<>();
        List<Attribute> attributes = new ArrayList<>();


        if (c.getExternalId() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttribute.EXTERNAL_ID, c.getExternalId(), cPF.getProvFactory().getName().XSD_STRING));
        }

        if (c.getReferencedBundleId() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttribute.REFERENCED_BUNDLE_ID, c.getReferencedBundleId()));
        }

        if (c.getReferencedMetaBundleId() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttribute.REFERENCED_META_BUNDLE_ID, c.getReferencedMetaBundleId()));
        }

        if (c.getReferencedBundleHashValue() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttribute.REFERENCED_BUNDLE_HASH_VALUE, c.getReferencedBundleHashValue(), cPF.getProvFactory().getName().XSD_BYTE));
        }

        if (c.getHashAlg() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttribute.HASH_ALG, c.getHashAlg().toString(), cPF.getProvFactory().getName().XSD_STRING));
        }

        if (c.getProvenanceServiceUri() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttribute.PROVENANCE_SERVICE_URI, c.getProvenanceServiceUri(), cPF.getProvFactory().getName().XSD_ANY_URI));
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
        if (fC == null) {
            return null;
        }

        List<Statement> statements = map((Connector) fC);
        if (fC.getSpecializationOf() != null) {
            statements.add(cPF.getProvFactory().newSpecializationOf(fC.getId(), fC.getSpecializationOf()));
        }
        return statements;
    }

    private List<Statement> map(CpmAgent agent) {
        if (agent == null) {
            return null;
        }

        List<Attribute> attributes = new ArrayList<>();

        if (agent.getContactIdPid() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttribute.CONTACT_ID_PID, agent.getContactIdPid(), cPF.getProvFactory().getName().XSD_STRING));
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
        if (iE == null) {
            return null;
        }

        List<Attribute> attributes = new ArrayList<>();

        if (iE.getExternalId() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttribute.EXTERNAL_ID, iE.getExternalId(), cPF.getProvFactory().getName().XSD_STRING));
        }

        if (iE.getExternalIdType() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttribute.EXTERNAL_ID_TYPE, iE.getExternalIdType(), cPF.getProvFactory().getName().XSD_STRING));
        }

        if (iE.getComment() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttribute.COMMENT, iE.getComment(), cPF.getProvFactory().getName().XSD_STRING));
        }

        return Collections.singletonList(cPF.newCpmEntity(iE.getId(), CpmType.IDENTIFIER, attributes));
    }


    public List<Statement> map(MainActivity mA) {
        if (mA == null) {
            return null;
        }

        List<Statement> statements = new ArrayList<>();
        List<Attribute> attributes = new ArrayList<>();

        if (mA.getReferencedMetaBundleId() != null) {
            attributes.add(cPF.newCpmAttribute(CpmAttribute.REFERENCED_META_BUNDLE_ID, mA.getReferencedMetaBundleId()));
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
        if (ti == null) {
            return null;
        }

        if (ti.getBundleName() == null) {
            throw new IllegalArgumentException(NULL_BUNDLE_NAME);
        }

        List<Statement> statements = new ArrayList<>();

        if (ti.getMainActivity() != null) {
            statements.addAll(map(ti.getMainActivity()));
        }

        if (ti.getBackwardConnectors() != null) {
            ti.getBackwardConnectors().stream().map(this::map).flatMap(List::stream).forEach(statements::add);
        }

        if (ti.getForwardConnectors() != null) {
            ti.getForwardConnectors().stream().map(this::map).flatMap(List::stream).forEach(statements::add);
        }

        if (mergeAgents) {
            // if sender and receiver agents share an id, merge them
            Set<QualifiedName> senderAgentIds = ti.getSenderAgents().stream()
                    .map(CpmAgent::getId)
                    .collect(Collectors.toSet());

            Set<QualifiedName> receiverAgentIds = ti.getReceiverAgents().stream()
                    .map(CpmAgent::getId)
                    .collect(Collectors.toSet());

            ti.getReceiverAgents().stream()
                    .flatMap(rA -> {
                        List<Statement> rASts = map(rA);
                        if (senderAgentIds.contains(rA.getId())) {
                            ((Agent) rASts.getFirst()).getType().add(cPF.newCpmType(CpmType.SENDER_AGENT));
                        }
                        return rASts.stream();
                    }).forEach(statements::add);

            ti.getSenderAgents().stream()
                    .filter(sA -> !receiverAgentIds.contains(sA.getId()))
                    .flatMap(sA -> map(sA).stream())
                    .forEach(statements::add);
        } else {
            if (ti.getReceiverAgents() != null) {
                ti.getReceiverAgents().stream().map(this::map).flatMap(List::stream).forEach(statements::add);
            }
            if (ti.getSenderAgents() != null) {
                ti.getSenderAgents().stream().map(this::map).flatMap(List::stream).forEach(statements::add);
            }
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
