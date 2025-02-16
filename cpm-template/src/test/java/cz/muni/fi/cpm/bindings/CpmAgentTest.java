package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.vanilla.QualifiedName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CpmAgentTest {
    @Test
    public void toStatements_basicIdSet_returnsOneStatement() {
        TestAgent agent = new TestAgent();
        QualifiedName id = new QualifiedName("uri", "agentExample", "ex");
        agent.setId(id);

        List<Statement> statements = agent.toStatements(new CpmProvFactory());

        assertNotNull(statements);
        assertEquals(1, statements.size());

        Statement statement = statements.getFirst();
        assertInstanceOf(Agent.class, statement);

        Agent resultAgent = (Agent) statement;
        assertEquals(id, resultAgent.getId());

        assertNotNull(resultAgent.getType());
        assertEquals(1, resultAgent.getType().size());
        Type type = resultAgent.getType().getFirst();
        assertEquals(CpmType.SENDER_AGENT.toString(), ((QualifiedName) type.getValue()).getLocalPart());
    }

    @Test
    public void toStatements_withContactIdPid_returnsCorrectContactId() {
        TestAgent agent = new TestAgent();
        agent.setId(new QualifiedName("uri", "agentExample", "ex"));
        String contactIdPid = "contact123";
        agent.setContactIdPid(contactIdPid);

        List<Statement> statements = agent.toStatements(new CpmProvFactory());
        Agent resultAgent = (Agent) statements.getFirst();

        List<Other> otherAttributes = resultAgent.getOther();
        assertNotNull(otherAttributes);
        assertEquals(1, otherAttributes.size());

        Attribute contactIdAttr = otherAttributes.getFirst();
        assertInstanceOf(LangString.class, contactIdAttr.getValue());
        assertEquals(CpmAttributeConstants.CONTACT_ID_PID, contactIdAttr.getElementName().getLocalPart());
        assertEquals(contactIdPid, ((LangString) contactIdAttr.getValue()).getValue());
    }

    private class TestAgent extends CpmAgent {
        @Override
        public CpmType getType() {
            return CpmType.SENDER_AGENT;
        }

        public TestAgent() {
        }

        public TestAgent(org.openprovenance.prov.model.QualifiedName id) {
            super(id);
        }
    }
}
