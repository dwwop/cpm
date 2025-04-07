package cz.muni.fi.cpm.template.mapper;

import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.template.schema.ReceiverAgent;
import cz.muni.fi.cpm.template.schema.SenderAgent;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.vanilla.QualifiedName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CpmAgentTest {
    private TemplateProvMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new TemplateProvMapper(new CpmProvFactory());
    }

    @Test
    public void toStatements_basicIdSet_returnsOneStatement() {
        ReceiverAgent agent = new ReceiverAgent();
        QualifiedName id = new QualifiedName("uri", "agentExample", "ex");
        agent.setId(id);

        List<Statement> statements = mapper.map(agent);

        assertNotNull(statements);
        assertEquals(1, statements.size());

        Statement statement = statements.getFirst();
        assertInstanceOf(Agent.class, statement);

        Agent resultAgent = (Agent) statement;
        assertEquals(id, resultAgent.getId());

        assertNotNull(resultAgent.getType());
        assertEquals(1, resultAgent.getType().size());
        Type type = resultAgent.getType().getFirst();
        assertEquals(CpmType.RECEIVER_AGENT.toString(), ((QualifiedName) type.getValue()).getLocalPart());
    }

    @Test
    public void toStatements_withContactIdPid_returnsCorrectContactId() {
        SenderAgent agent = new SenderAgent();
        agent.setId(new QualifiedName("uri", "agentExample", "ex"));
        String contactIdPid = "contact123";
        agent.setContactIdPid(contactIdPid);

        List<Statement> statements = mapper.map(agent);
        Agent resultAgent = (Agent) statements.getFirst();

        List<Other> otherAttributes = resultAgent.getOther();
        assertNotNull(otherAttributes);
        assertEquals(1, otherAttributes.size());
        Type type = resultAgent.getType().getFirst();
        assertEquals(CpmType.SENDER_AGENT.toString(), ((QualifiedName) type.getValue()).getLocalPart());

        Attribute contactIdAttr = otherAttributes.getFirst();
        assertInstanceOf(LangString.class, contactIdAttr.getValue());
        assertEquals(CpmAttributeConstants.CONTACT_ID_PID, contactIdAttr.getElementName().getLocalPart());
        assertEquals(contactIdPid, ((LangString) contactIdAttr.getValue()).getValue());
    }
}
