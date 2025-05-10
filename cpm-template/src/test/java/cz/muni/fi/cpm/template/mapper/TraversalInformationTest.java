package cz.muni.fi.cpm.template.mapper;

import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.divided.ordered.CpmOrderedFactory;
import cz.muni.fi.cpm.merged.CpmMergedFactory;
import cz.muni.fi.cpm.model.CpmDocument;
import cz.muni.fi.cpm.model.CpmUtilities;
import cz.muni.fi.cpm.model.INode;
import cz.muni.fi.cpm.template.schema.*;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.*;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TraversalInformationTest {

    private DatatypeFactory datatypeFactory;
    private ProvFactory pF;

    @BeforeEach
    public void setUp() throws Exception {
        datatypeFactory = DatatypeFactory.newInstance();
        pF = new org.openprovenance.prov.vanilla.ProvFactory();
    }

    @Test
    public void toDocument_null_returnsNull() {
        assertNull(new TemplateProvMapper(new CpmProvFactory(pF)).map((TraversalInformation) null));
    }

    @Test
    public void toDocument_emptyTI_returnsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new TemplateProvMapper(new CpmProvFactory(pF)).map(new TraversalInformation())
        );
    }

    @Test
    public void toDocument_basicTI_returnsDocument() {
        TraversalInformation ti = new TraversalInformation();

        ti.setPrefixes(Map.of("ex", "www.example.com/"));
        ti.setBundleName(ti.getNamespace().qualifiedName("ex", "bundle1", pF));

        QualifiedName mAID = ti.getNamespace().qualifiedName("ex", "activity1", pF);
        MainActivity mA = new MainActivity(mAID);
        XMLGregorianCalendar startTime = datatypeFactory.newXMLGregorianCalendar("2011-11-16T16:05:00");
        mA.setStartTime(startTime);
        XMLGregorianCalendar endTime = datatypeFactory.newXMLGregorianCalendar("2011-11-16T18:05:00");
        mA.setEndTime(endTime);
        ti.setMainActivity(mA);

        QualifiedName bcID = ti.getNamespace().qualifiedName("ex", "backConnector1", pF);
        BackwardConnector bC = new BackwardConnector(bcID);
        ti.getBackwardConnectors().add(bC);

        MainActivityUsed used = new MainActivityUsed(bcID);
        mA.setUsed(List.of(used));

        QualifiedName fcID = ti.getNamespace().qualifiedName("ex", "forwardConnector1", pF);
        mA.setGenerated(List.of(fcID));

        ForwardConnector fC = new ForwardConnector(fcID);
        fC.setDerivedFrom(List.of(bC.getId()));
        ti.getForwardConnectors().add(fC);

        ITemplateProvMapper mapper = new TemplateProvMapper(new CpmProvFactory(pF));
        Document doc = mapper.map(ti);

        assertNotNull(doc);
        CpmDocument cpmDoc = new CpmDocument(doc, pF, new CpmProvFactory(pF), new CpmMergedFactory(pF));
        assertEquals(ti.getBundleName(), cpmDoc.getBundleId());

        INode mANode = cpmDoc.getMainActivity();
        assertNotNull(mANode);
        assertEquals(mAID, mANode.getId());
        assertEquals(StatementOrBundle.Kind.PROV_ACTIVITY, mANode.getKind());
        assertEquals(startTime, ((Activity) mANode.getAnyElement()).getStartTime());
        assertEquals(endTime, ((Activity) mANode.getAnyElement()).getEndTime());

        assertEquals(1, cpmDoc.getBackwardConnectors().size());
        assertEquals(bcID, cpmDoc.getBackwardConnectors().getFirst().getId());

        assertEquals(1, cpmDoc.getForwardConnectors().size());
        assertEquals(fcID, cpmDoc.getForwardConnectors().getFirst().getId());
        assertNotNull(cpmDoc.getEdge(fcID, bcID, StatementOrBundle.Kind.PROV_DERIVATION));
    }

    @Test
    public void toDocument_mergeAgents_returnsDocument() {
        TraversalInformation ti = new TraversalInformation();

        ti.setPrefixes(Map.of("ex", "www.example.com/"));
        ti.setBundleName(ti.getNamespace().qualifiedName("ex", "bundle1", pF));

        QualifiedName agentID = ti.getNamespace().qualifiedName("ex", "agent", pF);

        SenderAgent stationSenderAg = new SenderAgent(agentID);
        ti.setSenderAgents(List.of(stationSenderAg));

        ReceiverAgent stationAg = new ReceiverAgent(agentID);
        ti.setReceiverAgents(List.of(stationAg));

        ITemplateProvMapper mapper = new TemplateProvMapper(new CpmProvFactory(pF), true);
        Document doc = mapper.map(ti);

        assertNotNull(doc);
        CpmDocument cpmDoc = new CpmDocument(doc, pF, new CpmProvFactory(pF), new CpmOrderedFactory(pF));

        INode agentNode = cpmDoc.getNode(agentID, StatementOrBundle.Kind.PROV_AGENT);
        assertNotNull(agentNode);
        assertEquals(1, agentNode.getElements().size());
        assertTrue(CpmUtilities.hasCpmType(agentNode, CpmType.SENDER_AGENT));
        assertTrue(CpmUtilities.hasCpmType(agentNode, CpmType.RECEIVER_AGENT));
    }

    @Test
    public void toDocument_separateAgents_returnsDocument() {
        TraversalInformation ti = new TraversalInformation();

        ti.setPrefixes(Map.of("ex", "www.example.com/"));
        ti.setBundleName(ti.getNamespace().qualifiedName("ex", "bundle1", pF));

        QualifiedName agentID = ti.getNamespace().qualifiedName("ex", "agent", pF);

        SenderAgent stationSenderAg = new SenderAgent(agentID);
        ti.setSenderAgents(List.of(stationSenderAg));

        ReceiverAgent stationAg = new ReceiverAgent(agentID);
        ti.setReceiverAgents(List.of(stationAg));

        ITemplateProvMapper mapper = new TemplateProvMapper(new CpmProvFactory(pF), false);
        Document doc = mapper.map(ti);

        assertNotNull(doc);
        CpmDocument cpmDoc = new CpmDocument(doc, pF, new CpmProvFactory(pF), new CpmOrderedFactory(pF));

        INode agentNode = cpmDoc.getNode(agentID, StatementOrBundle.Kind.PROV_AGENT);
        assertNotNull(agentNode);
        assertEquals(2, agentNode.getElements().size());
        assertTrue(CpmUtilities.hasCpmType(agentNode, CpmType.SENDER_AGENT));
        assertTrue(CpmUtilities.hasCpmType(agentNode, CpmType.RECEIVER_AGENT));
    }
}
